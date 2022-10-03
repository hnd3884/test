package com.microsoft.sqlserver.jdbc;

import java.util.Iterator;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.nio.channels.SocketChannel;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.io.FileNotFoundException;
import java.io.FileInputStream;
import java.security.Provider;
import java.text.MessageFormat;
import java.util.Arrays;
import java.security.Security;
import java.security.SecureRandom;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.X509TrustManager;
import javax.net.ssl.TrustManagerFactory;
import java.security.KeyStore;
import javax.net.ssl.TrustManager;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.io.OutputStream;
import java.io.InputStream;
import javax.net.ssl.SSLSocket;
import java.net.Socket;
import java.util.logging.Logger;
import java.io.Serializable;

final class TDSChannel implements Serializable
{
    private static final long serialVersionUID = -866497813437384090L;
    private static final Logger logger;
    private final String traceID;
    private final SQLServerConnection con;
    private final TDSWriter tdsWriter;
    private Socket tcpSocket;
    private SSLSocket sslSocket;
    private Socket channelSocket;
    ProxySocket proxySocket;
    private InputStream tcpInputStream;
    private OutputStream tcpOutputStream;
    private InputStream inputStream;
    private OutputStream outputStream;
    private static Logger packetLogger;
    private final boolean isLoggingPackets;
    int numMsgsSent;
    int numMsgsRcvd;
    private int spid;
    private static final String SEPARATOR;
    private static final String JAVA_HOME;
    private static final String JAVA_SECURITY;
    private static final String JSSECACERTS;
    private static final String CACERTS;
    
    final Logger getLogger() {
        return TDSChannel.logger;
    }
    
    @Override
    public final String toString() {
        return this.traceID;
    }
    
    final TDSWriter getWriter() {
        return this.tdsWriter;
    }
    
    final TDSReader getReader(final TDSCommand command) {
        return new TDSReader(this, this.con, command);
    }
    
    final boolean isLoggingPackets() {
        return this.isLoggingPackets;
    }
    
    void setSPID(final int spid) {
        this.spid = spid;
    }
    
    int getSPID() {
        return this.spid;
    }
    
    void resetPooledConnection() {
        this.tdsWriter.resetPooledConnection();
    }
    
    TDSChannel(final SQLServerConnection con) {
        this.proxySocket = null;
        this.isLoggingPackets = TDSChannel.packetLogger.isLoggable(Level.FINEST);
        this.numMsgsSent = 0;
        this.numMsgsRcvd = 0;
        this.spid = 0;
        this.con = con;
        this.traceID = "TDSChannel (" + con.toString() + ")";
        this.tcpSocket = null;
        this.sslSocket = null;
        this.channelSocket = null;
        this.tcpInputStream = null;
        this.tcpOutputStream = null;
        this.inputStream = null;
        this.outputStream = null;
        this.tdsWriter = new TDSWriter(this, con);
    }
    
    final void open(final String host, final int port, final int timeoutMillis, final boolean useParallel, final boolean useTnir, final boolean isTnirFirstAttempt, final int timeoutMillisForFullTimeout) throws SQLServerException {
        if (TDSChannel.logger.isLoggable(Level.FINER)) {
            TDSChannel.logger.finer(this.toString() + ": Opening TCP socket...");
        }
        final SocketFinder socketFinder = new SocketFinder(this.traceID, this.con);
        final Socket socket = socketFinder.findSocket(host, port, timeoutMillis, useParallel, useTnir, isTnirFirstAttempt, timeoutMillisForFullTimeout);
        this.tcpSocket = socket;
        this.channelSocket = socket;
        try {
            this.tcpSocket.setTcpNoDelay(true);
            this.tcpSocket.setKeepAlive(true);
            final int socketTimeout = this.con.getSocketTimeoutMilliseconds();
            this.tcpSocket.setSoTimeout(socketTimeout);
            final InputStream inputStream = this.tcpSocket.getInputStream();
            this.tcpInputStream = inputStream;
            this.inputStream = inputStream;
            final OutputStream outputStream = this.tcpSocket.getOutputStream();
            this.tcpOutputStream = outputStream;
            this.outputStream = outputStream;
        }
        catch (final IOException ex) {
            SQLServerException.ConvertConnectExceptionToSQLServerException(host, port, this.con, ex);
        }
    }
    
    void disableSSL() {
        if (TDSChannel.logger.isLoggable(Level.FINER)) {
            TDSChannel.logger.finer(this.toString() + " Disabling SSL...");
        }
        final InputStream is = new ByteArrayInputStream(new byte[0]);
        try {
            is.close();
        }
        catch (final IOException e) {
            TDSChannel.logger.fine("Ignored error closing InputStream: " + e.getMessage());
        }
        final OutputStream os = new ByteArrayOutputStream();
        try {
            os.close();
        }
        catch (final IOException e2) {
            TDSChannel.logger.fine("Ignored error closing OutputStream: " + e2.getMessage());
        }
        if (TDSChannel.logger.isLoggable(Level.FINEST)) {
            TDSChannel.logger.finest(this.toString() + " Rewiring proxy streams for SSL socket close");
        }
        this.proxySocket.setStreams(is, os);
        try {
            if (TDSChannel.logger.isLoggable(Level.FINER)) {
                TDSChannel.logger.finer(this.toString() + " Closing SSL socket");
            }
            this.sslSocket.close();
        }
        catch (final IOException e2) {
            TDSChannel.logger.fine("Ignored error closing SSLSocket: " + e2.getMessage());
        }
        this.proxySocket = null;
        this.inputStream = this.tcpInputStream;
        this.outputStream = this.tcpOutputStream;
        this.channelSocket = this.tcpSocket;
        this.sslSocket = null;
        if (TDSChannel.logger.isLoggable(Level.FINER)) {
            TDSChannel.logger.finer(this.toString() + " SSL disabled");
        }
    }
    
    void enableSSL(final String host, final int port) throws SQLServerException {
        Provider tmfProvider = null;
        Provider sslContextProvider = null;
        Provider ksProvider = null;
        String tmfDefaultAlgorithm = null;
        SSLHandhsakeState handshakeState = SSLHandhsakeState.SSL_HANDHSAKE_NOT_STARTED;
        boolean isFips = false;
        String trustStoreType = null;
        String sslProtocol = null;
        try {
            if (TDSChannel.logger.isLoggable(Level.FINER)) {
                TDSChannel.logger.finer(this.toString() + " Enabling SSL...");
            }
            final String trustStoreFileName = this.con.activeConnectionProperties.getProperty(SQLServerDriverStringProperty.TRUST_STORE.toString());
            final String trustStorePassword = this.con.activeConnectionProperties.getProperty(SQLServerDriverStringProperty.TRUST_STORE_PASSWORD.toString());
            final String hostNameInCertificate = this.con.activeConnectionProperties.getProperty(SQLServerDriverStringProperty.HOSTNAME_IN_CERTIFICATE.toString());
            trustStoreType = this.con.activeConnectionProperties.getProperty(SQLServerDriverStringProperty.TRUST_STORE_TYPE.toString());
            if (StringUtils.isEmpty(trustStoreType)) {
                trustStoreType = SQLServerDriverStringProperty.TRUST_STORE_TYPE.getDefaultValue();
            }
            isFips = Boolean.valueOf(this.con.activeConnectionProperties.getProperty(SQLServerDriverBooleanProperty.FIPS.toString()));
            sslProtocol = this.con.activeConnectionProperties.getProperty(SQLServerDriverStringProperty.SSL_PROTOCOL.toString());
            if (isFips) {
                this.validateFips(trustStoreType, trustStoreFileName);
            }
            assert 1 == this.con.getRequestedEncryptionLevel();
            assert 3 == this.con.getNegotiatedEncryptionLevel();
            TrustManager[] tm = null;
            if (0 == this.con.getRequestedEncryptionLevel() || (1 == this.con.getRequestedEncryptionLevel() && this.con.trustServerCertificate())) {
                if (TDSChannel.logger.isLoggable(Level.FINER)) {
                    TDSChannel.logger.finer(this.toString() + " SSL handshake will trust any certificate");
                }
                tm = new TrustManager[] { new PermissiveX509TrustManager(this) };
            }
            else if (this.con.getTrustManagerClass() != null) {
                final Class<?> tmClass = Class.forName(this.con.getTrustManagerClass());
                if (!TrustManager.class.isAssignableFrom(tmClass)) {
                    throw new IllegalArgumentException("The class specified by the trustManagerClass property must implement javax.net.ssl.TrustManager");
                }
                final String constructorArg = this.con.getTrustManagerConstructorArg();
                if (constructorArg == null) {
                    tm = new TrustManager[] { (TrustManager)tmClass.getDeclaredConstructor((Class<?>[])new Class[0]).newInstance(new Object[0]) };
                }
                else {
                    tm = new TrustManager[] { (TrustManager)tmClass.getDeclaredConstructor(String.class).newInstance(constructorArg) };
                }
            }
            else {
                if (TDSChannel.logger.isLoggable(Level.FINER)) {
                    TDSChannel.logger.finer(this.toString() + " SSL handshake will validate server certificate");
                }
                KeyStore ks = null;
                if (null == trustStoreFileName && null == trustStorePassword) {
                    if (TDSChannel.logger.isLoggable(Level.FINER)) {
                        TDSChannel.logger.finer(this.toString() + " Using system default trust store and password");
                    }
                }
                else {
                    if (TDSChannel.logger.isLoggable(Level.FINEST)) {
                        TDSChannel.logger.finest(this.toString() + " Finding key store interface");
                    }
                    ks = KeyStore.getInstance(trustStoreType);
                    ksProvider = ks.getProvider();
                    final InputStream is = this.loadTrustStore(trustStoreFileName);
                    if (TDSChannel.logger.isLoggable(Level.FINEST)) {
                        TDSChannel.logger.finest(this.toString() + " Loading key store");
                    }
                    try {
                        ks.load(is, (char[])((null == trustStorePassword) ? null : trustStorePassword.toCharArray()));
                    }
                    finally {
                        this.con.activeConnectionProperties.remove(SQLServerDriverStringProperty.TRUST_STORE_PASSWORD.toString());
                        if (null != is) {
                            try {
                                is.close();
                            }
                            catch (final IOException e) {
                                if (TDSChannel.logger.isLoggable(Level.FINE)) {
                                    TDSChannel.logger.fine(this.toString() + " Ignoring error closing trust material InputStream...");
                                }
                            }
                        }
                    }
                }
                TrustManagerFactory tmf = null;
                if (TDSChannel.logger.isLoggable(Level.FINEST)) {
                    TDSChannel.logger.finest(this.toString() + " Locating X.509 trust manager factory");
                }
                tmfDefaultAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
                tmf = TrustManagerFactory.getInstance(tmfDefaultAlgorithm);
                tmfProvider = tmf.getProvider();
                if (TDSChannel.logger.isLoggable(Level.FINEST)) {
                    TDSChannel.logger.finest(this.toString() + " Getting trust manager");
                }
                tmf.init(ks);
                tm = tmf.getTrustManagers();
                if (!isFips) {
                    if (null != hostNameInCertificate) {
                        tm = new TrustManager[] { new HostNameOverrideX509TrustManager(this, (X509TrustManager)tm[0], hostNameInCertificate) };
                    }
                    else {
                        tm = new TrustManager[] { new HostNameOverrideX509TrustManager(this, (X509TrustManager)tm[0], host) };
                    }
                }
            }
            SSLContext sslContext = null;
            if (TDSChannel.logger.isLoggable(Level.FINEST)) {
                TDSChannel.logger.finest(this.toString() + " Getting TLS or better SSL context");
            }
            sslContext = SSLContext.getInstance(sslProtocol);
            sslContextProvider = sslContext.getProvider();
            if (TDSChannel.logger.isLoggable(Level.FINEST)) {
                TDSChannel.logger.finest(this.toString() + " Initializing SSL context");
            }
            sslContext.init(null, tm, null);
            this.proxySocket = new ProxySocket(this);
            if (TDSChannel.logger.isLoggable(Level.FINEST)) {
                TDSChannel.logger.finest(this.toString() + " Creating SSL socket");
            }
            this.sslSocket = (SSLSocket)sslContext.getSocketFactory().createSocket(this.proxySocket, host, port, false);
            if (TDSChannel.logger.isLoggable(Level.FINER)) {
                TDSChannel.logger.finer(this.toString() + " Starting SSL handshake");
            }
            handshakeState = SSLHandhsakeState.SSL_HANDHSAKE_STARTED;
            this.sslSocket.startHandshake();
            handshakeState = SSLHandhsakeState.SSL_HANDHSAKE_COMPLETE;
            if (TDSChannel.logger.isLoggable(Level.FINEST)) {
                TDSChannel.logger.finest(this.toString() + " Rewiring proxy streams after handshake");
            }
            this.proxySocket.setStreams(this.inputStream, this.outputStream);
            if (TDSChannel.logger.isLoggable(Level.FINEST)) {
                TDSChannel.logger.finest(this.toString() + " Getting SSL InputStream");
            }
            this.inputStream = this.sslSocket.getInputStream();
            if (TDSChannel.logger.isLoggable(Level.FINEST)) {
                TDSChannel.logger.finest(this.toString() + " Getting SSL OutputStream");
            }
            this.outputStream = this.sslSocket.getOutputStream();
            this.channelSocket = this.sslSocket;
            if (TDSChannel.logger.isLoggable(Level.FINER)) {
                TDSChannel.logger.finer(this.toString() + " SSL enabled");
            }
        }
        catch (final Exception e2) {
            if (TDSChannel.logger.isLoggable(Level.FINER)) {
                TDSChannel.logger.log(Level.FINER, e2.getMessage(), e2);
            }
            if (TDSChannel.logger.isLoggable(Level.FINER)) {
                TDSChannel.logger.log(Level.FINER, "java.security path: " + TDSChannel.JAVA_SECURITY + "\nSecurity providers: " + Arrays.asList(Security.getProviders()) + "\n" + ((null != sslContextProvider) ? ("SSLContext provider info: " + sslContextProvider.getInfo() + "\nSSLContext provider services:\n" + sslContextProvider.getServices() + "\n") : "") + ((null != tmfProvider) ? ("TrustManagerFactory provider info: " + tmfProvider.getInfo() + "\n") : "") + ((null != tmfDefaultAlgorithm) ? ("TrustManagerFactory default algorithm: " + tmfDefaultAlgorithm + "\n") : "") + ((null != ksProvider) ? ("KeyStore provider info: " + ksProvider.getInfo() + "\n") : "") + "java.ext.dirs: " + System.getProperty("java.ext.dirs"));
            }
            final String localizedMessage = e2.getLocalizedMessage();
            String errMsg = (localizedMessage != null) ? localizedMessage : e2.getMessage();
            String causeErrMsg = null;
            final Throwable cause = e2.getCause();
            if (cause != null) {
                final String causeLocalizedMessage = cause.getLocalizedMessage();
                causeErrMsg = ((causeLocalizedMessage != null) ? causeLocalizedMessage : cause.getMessage());
            }
            final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_sslFailed"));
            final Object[] msgArgs = { errMsg };
            if (errMsg != null && errMsg.contains(" ClientConnectionId:")) {
                errMsg = errMsg.substring(0, errMsg.indexOf(" ClientConnectionId:"));
            }
            if (causeErrMsg != null && causeErrMsg.contains(" ClientConnectionId:")) {
                causeErrMsg = causeErrMsg.substring(0, causeErrMsg.indexOf(" ClientConnectionId:"));
            }
            if (e2 instanceof IOException && SSLHandhsakeState.SSL_HANDHSAKE_STARTED == handshakeState && (SQLServerException.getErrString("R_truncatedServerResponse").equals(errMsg) || SQLServerException.getErrString("R_truncatedServerResponse").equals(causeErrMsg))) {
                this.con.terminate(7, form.format(msgArgs), e2);
            }
            else {
                this.con.terminate(5, form.format(msgArgs), e2);
            }
        }
    }
    
    private void validateFips(final String trustStoreType, final String trustStoreFileName) throws SQLServerException {
        boolean isValid = false;
        final String strError = SQLServerException.getErrString("R_invalidFipsConfig");
        final boolean isEncryptOn = 1 == this.con.getRequestedEncryptionLevel();
        final boolean isValidTrustStoreType = !StringUtils.isEmpty(trustStoreType);
        final boolean isValidTrustStore = !StringUtils.isEmpty(trustStoreFileName);
        final boolean isTrustServerCertificate = this.con.trustServerCertificate();
        if (isEncryptOn && !isTrustServerCertificate) {
            isValid = true;
            if (isValidTrustStore && !isValidTrustStoreType) {
                isValid = false;
                if (TDSChannel.logger.isLoggable(Level.FINER)) {
                    TDSChannel.logger.finer(this.toString() + "TrustStoreType is required alongside with TrustStore.");
                }
            }
        }
        if (!isValid) {
            throw new SQLServerException(strError, null, 0, null);
        }
    }
    
    final InputStream loadTrustStore(String trustStoreFileName) {
        FileInputStream is = null;
        if (null != trustStoreFileName) {
            try {
                if (TDSChannel.logger.isLoggable(Level.FINEST)) {
                    TDSChannel.logger.finest(this.toString() + " Opening specified trust store: " + trustStoreFileName);
                }
                is = new FileInputStream(trustStoreFileName);
            }
            catch (final FileNotFoundException e) {
                if (TDSChannel.logger.isLoggable(Level.FINE)) {
                    TDSChannel.logger.fine(this.toString() + " Trust store not found: " + e.getMessage());
                }
            }
        }
        else if (null != (trustStoreFileName = System.getProperty("javax.net.ssl.trustStore"))) {
            try {
                if (TDSChannel.logger.isLoggable(Level.FINEST)) {
                    TDSChannel.logger.finest(this.toString() + " Opening default trust store (from javax.net.ssl.trustStore): " + trustStoreFileName);
                }
                is = new FileInputStream(trustStoreFileName);
            }
            catch (final FileNotFoundException e) {
                if (TDSChannel.logger.isLoggable(Level.FINE)) {
                    TDSChannel.logger.fine(this.toString() + " Trust store not found: " + e.getMessage());
                }
            }
        }
        else {
            try {
                if (TDSChannel.logger.isLoggable(Level.FINEST)) {
                    TDSChannel.logger.finest(this.toString() + " Opening default trust store: " + TDSChannel.JSSECACERTS);
                }
                is = new FileInputStream(TDSChannel.JSSECACERTS);
            }
            catch (final FileNotFoundException e) {
                if (TDSChannel.logger.isLoggable(Level.FINE)) {
                    TDSChannel.logger.fine(this.toString() + " Trust store not found: " + e.getMessage());
                }
            }
            if (null == is) {
                try {
                    if (TDSChannel.logger.isLoggable(Level.FINEST)) {
                        TDSChannel.logger.finest(this.toString() + " Opening default trust store: " + TDSChannel.CACERTS);
                    }
                    is = new FileInputStream(TDSChannel.CACERTS);
                }
                catch (final FileNotFoundException e) {
                    if (TDSChannel.logger.isLoggable(Level.FINE)) {
                        TDSChannel.logger.fine(this.toString() + " Trust store not found: " + e.getMessage());
                    }
                }
            }
        }
        return is;
    }
    
    final int read(final byte[] data, final int offset, final int length) throws SQLServerException {
        try {
            return this.inputStream.read(data, offset, length);
        }
        catch (final IOException e) {
            if (TDSChannel.logger.isLoggable(Level.FINE)) {
                TDSChannel.logger.fine(this.toString() + " read failed:" + e.getMessage());
            }
            if (e instanceof SocketTimeoutException) {
                this.con.terminate(8, e.getMessage(), e);
            }
            else {
                this.con.terminate(3, e.getMessage(), e);
            }
            return 0;
        }
    }
    
    final void write(final byte[] data, final int offset, final int length) throws SQLServerException {
        try {
            this.outputStream.write(data, offset, length);
        }
        catch (final IOException e) {
            if (TDSChannel.logger.isLoggable(Level.FINER)) {
                TDSChannel.logger.finer(this.toString() + " write failed:" + e.getMessage());
            }
            this.con.terminate(3, e.getMessage(), e);
        }
    }
    
    final void flush() throws SQLServerException {
        try {
            this.outputStream.flush();
        }
        catch (final IOException e) {
            if (TDSChannel.logger.isLoggable(Level.FINER)) {
                TDSChannel.logger.finer(this.toString() + " flush failed:" + e.getMessage());
            }
            this.con.terminate(3, e.getMessage(), e);
        }
    }
    
    final void close() {
        if (null != this.sslSocket) {
            this.disableSSL();
        }
        if (null != this.inputStream) {
            if (TDSChannel.logger.isLoggable(Level.FINEST)) {
                TDSChannel.logger.finest(this.toString() + ": Closing inputStream...");
            }
            try {
                this.inputStream.close();
            }
            catch (final IOException e) {
                if (TDSChannel.logger.isLoggable(Level.FINE)) {
                    TDSChannel.logger.log(Level.FINE, this.toString() + ": Ignored error closing inputStream", e);
                }
            }
        }
        if (null != this.outputStream) {
            if (TDSChannel.logger.isLoggable(Level.FINEST)) {
                TDSChannel.logger.finest(this.toString() + ": Closing outputStream...");
            }
            try {
                this.outputStream.close();
            }
            catch (final IOException e) {
                if (TDSChannel.logger.isLoggable(Level.FINE)) {
                    TDSChannel.logger.log(Level.FINE, this.toString() + ": Ignored error closing outputStream", e);
                }
            }
        }
        if (null != this.tcpSocket) {
            if (TDSChannel.logger.isLoggable(Level.FINER)) {
                TDSChannel.logger.finer(this.toString() + ": Closing TCP socket...");
            }
            try {
                this.tcpSocket.close();
            }
            catch (final IOException e) {
                if (TDSChannel.logger.isLoggable(Level.FINE)) {
                    TDSChannel.logger.log(Level.FINE, this.toString() + ": Ignored error closing socket", e);
                }
            }
        }
    }
    
    void logPacket(final byte[] data, final int nStartOffset, final int nLength, final String messageDetail) {
        assert 0 <= nLength && nLength <= data.length;
        assert 0 <= nStartOffset && nStartOffset <= data.length;
        final char[] hexChars = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
        final char[] printableChars = { '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', ' ', '!', '\"', '#', '$', '%', '&', '\'', '(', ')', '*', '+', ',', '-', '.', '/', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', ':', ';', '<', '=', '>', '?', '@', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '[', '\\', ']', '^', '_', '`', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '{', '|', '}', '~', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.' };
        final char[] lineTemplate = { ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.' };
        final char[] logLine = new char[lineTemplate.length];
        System.arraycopy(lineTemplate, 0, logLine, 0, lineTemplate.length);
        final StringBuilder logMsg = new StringBuilder(messageDetail.length() + 4 * nLength + 4 * (1 + nLength / 16) + 80);
        logMsg.append(this.tcpSocket.getLocalAddress().toString()).append(":").append(this.tcpSocket.getLocalPort()).append(" SPID:").append(this.spid).append(" ").append(messageDetail).append("\r\n");
        int nBytesLogged = 0;
        while (true) {
            int nBytesThisLine;
            for (nBytesThisLine = 0; nBytesThisLine < 16 && nBytesLogged < nLength; ++nBytesThisLine, ++nBytesLogged) {
                final int nUnsignedByteVal = (data[nStartOffset + nBytesLogged] + 256) % 256;
                logLine[3 * nBytesThisLine] = hexChars[nUnsignedByteVal / 16];
                logLine[3 * nBytesThisLine + 1] = hexChars[nUnsignedByteVal % 16];
                logLine[50 + nBytesThisLine] = printableChars[nUnsignedByteVal];
            }
            for (int nBytesJustified = nBytesThisLine; nBytesJustified < 16; ++nBytesJustified) {
                logLine[3 * nBytesJustified + 1] = (logLine[3 * nBytesJustified] = ' ');
            }
            logMsg.append(logLine, 0, 50 + nBytesThisLine);
            if (nBytesLogged == nLength) {
                break;
            }
            logMsg.append("\r\n");
        }
        if (TDSChannel.packetLogger.isLoggable(Level.FINEST)) {
            TDSChannel.packetLogger.finest(logMsg.toString());
        }
    }
    
    final int getNetworkTimeout() throws IOException {
        return this.tcpSocket.getSoTimeout();
    }
    
    final void setNetworkTimeout(final int timeout) throws IOException {
        this.tcpSocket.setSoTimeout(timeout);
    }
    
    static {
        logger = Logger.getLogger("com.microsoft.sqlserver.jdbc.internals.TDS.Channel");
        TDSChannel.packetLogger = Logger.getLogger("com.microsoft.sqlserver.jdbc.internals.TDS.DATA");
        SEPARATOR = System.getProperty("file.separator");
        JAVA_HOME = System.getProperty("java.home");
        JAVA_SECURITY = TDSChannel.JAVA_HOME + TDSChannel.SEPARATOR + "lib" + TDSChannel.SEPARATOR + "security";
        JSSECACERTS = TDSChannel.JAVA_SECURITY + TDSChannel.SEPARATOR + "jssecacerts";
        CACERTS = TDSChannel.JAVA_SECURITY + TDSChannel.SEPARATOR + "cacerts";
    }
    
    private class SSLHandshakeInputStream extends InputStream
    {
        private final TDSReader tdsReader;
        private final SSLHandshakeOutputStream sslHandshakeOutputStream;
        private final Logger logger;
        private final String logContext;
        private final byte[] oneByte;
        
        SSLHandshakeInputStream(final TDSChannel tdsChannel, final SSLHandshakeOutputStream sslHandshakeOutputStream) {
            this.oneByte = new byte[1];
            this.tdsReader = tdsChannel.getReader(null);
            this.sslHandshakeOutputStream = sslHandshakeOutputStream;
            this.logger = tdsChannel.getLogger();
            this.logContext = tdsChannel.toString() + " (SSLHandshakeInputStream):";
        }
        
        private void ensureSSLPayload() throws IOException {
            if (0 == this.tdsReader.available()) {
                if (this.logger.isLoggable(Level.FINEST)) {
                    this.logger.finest(this.logContext + " No handshake response bytes available. Flushing SSL handshake output stream.");
                }
                try {
                    this.sslHandshakeOutputStream.endMessage();
                }
                catch (final SQLServerException e) {
                    this.logger.finer(this.logContext + " Ending TDS message threw exception:" + e.getMessage());
                    throw new IOException(e.getMessage());
                }
                if (this.logger.isLoggable(Level.FINEST)) {
                    this.logger.finest(this.logContext + " Reading first packet of SSL handshake response");
                }
                try {
                    this.tdsReader.readPacket();
                }
                catch (final SQLServerException e) {
                    this.logger.finer(this.logContext + " Reading response packet threw exception:" + e.getMessage());
                    throw new IOException(e.getMessage());
                }
            }
        }
        
        @Override
        public long skip(long n) throws IOException {
            if (this.logger.isLoggable(Level.FINEST)) {
                this.logger.finest(this.logContext + " Skipping " + n + " bytes...");
            }
            if (n <= 0L) {
                return 0L;
            }
            if (n > 2147483647L) {
                n = 2147483647L;
            }
            this.ensureSSLPayload();
            try {
                this.tdsReader.skip((int)n);
            }
            catch (final SQLServerException e) {
                this.logger.finer(this.logContext + " Skipping bytes threw exception:" + e.getMessage());
                throw new IOException(e.getMessage());
            }
            return n;
        }
        
        @Override
        public int read() throws IOException {
            int bytesRead;
            while (0 == (bytesRead = this.readInternal(this.oneByte, 0, this.oneByte.length))) {}
            assert -1 == bytesRead;
            return (1 == bytesRead) ? this.oneByte[0] : -1;
        }
        
        @Override
        public int read(final byte[] b) throws IOException {
            return this.readInternal(b, 0, b.length);
        }
        
        @Override
        public int read(final byte[] b, final int offset, final int maxBytes) throws IOException {
            return this.readInternal(b, offset, maxBytes);
        }
        
        private int readInternal(final byte[] b, final int offset, final int maxBytes) throws IOException {
            if (this.logger.isLoggable(Level.FINEST)) {
                this.logger.finest(this.logContext + " Reading " + maxBytes + " bytes...");
            }
            this.ensureSSLPayload();
            try {
                this.tdsReader.readBytes(b, offset, maxBytes);
            }
            catch (final SQLServerException e) {
                this.logger.finer(this.logContext + " Reading bytes threw exception:" + e.getMessage());
                throw new IOException(e.getMessage());
            }
            return maxBytes;
        }
    }
    
    private class SSLHandshakeOutputStream extends OutputStream
    {
        private final TDSWriter tdsWriter;
        private boolean messageStarted;
        private final Logger logger;
        private final String logContext;
        private final byte[] singleByte;
        
        SSLHandshakeOutputStream(final TDSChannel tdsChannel) {
            this.singleByte = new byte[1];
            this.tdsWriter = tdsChannel.getWriter();
            this.messageStarted = false;
            this.logger = tdsChannel.getLogger();
            this.logContext = tdsChannel.toString() + " (SSLHandshakeOutputStream):";
        }
        
        @Override
        public void flush() throws IOException {
            if (this.logger.isLoggable(Level.FINEST)) {
                this.logger.finest(this.logContext + " Ignored a request to flush the stream");
            }
        }
        
        void endMessage() throws SQLServerException {
            assert this.messageStarted;
            if (this.logger.isLoggable(Level.FINEST)) {
                this.logger.finest(this.logContext + " Finishing TDS message");
            }
            this.tdsWriter.endMessage();
            this.messageStarted = false;
        }
        
        @Override
        public void write(final int b) throws IOException {
            this.singleByte[0] = (byte)(b & 0xFF);
            this.writeInternal(this.singleByte, 0, this.singleByte.length);
        }
        
        @Override
        public void write(final byte[] b) throws IOException {
            this.writeInternal(b, 0, b.length);
        }
        
        @Override
        public void write(final byte[] b, final int off, final int len) throws IOException {
            this.writeInternal(b, off, len);
        }
        
        private void writeInternal(final byte[] b, final int off, final int len) throws IOException {
            try {
                if (!this.messageStarted) {
                    if (this.logger.isLoggable(Level.FINEST)) {
                        this.logger.finest(this.logContext + " Starting new TDS packet...");
                    }
                    this.tdsWriter.startMessage(null, (byte)18);
                    this.messageStarted = true;
                }
                if (this.logger.isLoggable(Level.FINEST)) {
                    this.logger.finest(this.logContext + " Writing " + len + " bytes...");
                }
                this.tdsWriter.writeBytes(b, off, len);
            }
            catch (final SQLServerException e) {
                this.logger.finer(this.logContext + " Writing bytes threw exception:" + e.getMessage());
                throw new IOException(e.getMessage());
            }
        }
    }
    
    private final class ProxyInputStream extends InputStream
    {
        private InputStream filteredStream;
        private final byte[] oneByte;
        
        ProxyInputStream(final InputStream is) {
            this.oneByte = new byte[1];
            this.filteredStream = is;
        }
        
        final void setFilteredStream(final InputStream is) {
            this.filteredStream = is;
        }
        
        @Override
        public long skip(final long n) throws IOException {
            if (TDSChannel.logger.isLoggable(Level.FINEST)) {
                TDSChannel.logger.finest(this.toString() + " Skipping " + n + " bytes");
            }
            final long bytesSkipped = this.filteredStream.skip(n);
            if (TDSChannel.logger.isLoggable(Level.FINEST)) {
                TDSChannel.logger.finest(this.toString() + " Skipped " + n + " bytes");
            }
            return bytesSkipped;
        }
        
        @Override
        public int available() throws IOException {
            final int bytesAvailable = this.filteredStream.available();
            if (TDSChannel.logger.isLoggable(Level.FINEST)) {
                TDSChannel.logger.finest(this.toString() + " " + bytesAvailable + " bytes available");
            }
            return bytesAvailable;
        }
        
        @Override
        public int read() throws IOException {
            int bytesRead;
            while (0 == (bytesRead = this.readInternal(this.oneByte, 0, this.oneByte.length))) {}
            assert -1 == bytesRead;
            return (1 == bytesRead) ? this.oneByte[0] : -1;
        }
        
        @Override
        public int read(final byte[] b) throws IOException {
            return this.readInternal(b, 0, b.length);
        }
        
        @Override
        public int read(final byte[] b, final int offset, final int maxBytes) throws IOException {
            return this.readInternal(b, offset, maxBytes);
        }
        
        private int readInternal(final byte[] b, final int offset, final int maxBytes) throws IOException {
            if (TDSChannel.logger.isLoggable(Level.FINEST)) {
                TDSChannel.logger.finest(this.toString() + " Reading " + maxBytes + " bytes");
            }
            int bytesRead;
            try {
                bytesRead = this.filteredStream.read(b, offset, maxBytes);
            }
            catch (final IOException e) {
                if (TDSChannel.logger.isLoggable(Level.FINER)) {
                    TDSChannel.logger.finer(this.toString() + " " + e.getMessage());
                }
                TDSChannel.logger.finer(this.toString() + " Reading bytes threw exception:" + e.getMessage());
                throw e;
            }
            if (TDSChannel.logger.isLoggable(Level.FINEST)) {
                TDSChannel.logger.finest(this.toString() + " Read " + bytesRead + " bytes");
            }
            return bytesRead;
        }
        
        @Override
        public boolean markSupported() {
            final boolean markSupported = this.filteredStream.markSupported();
            if (TDSChannel.logger.isLoggable(Level.FINEST)) {
                TDSChannel.logger.finest(this.toString() + " Returning markSupported: " + markSupported);
            }
            return markSupported;
        }
        
        @Override
        public void mark(final int readLimit) {
            if (TDSChannel.logger.isLoggable(Level.FINEST)) {
                TDSChannel.logger.finest(this.toString() + " Marking next " + readLimit + " bytes");
            }
            this.filteredStream.mark(readLimit);
        }
        
        @Override
        public void reset() throws IOException {
            if (TDSChannel.logger.isLoggable(Level.FINEST)) {
                TDSChannel.logger.finest(this.toString() + " Resetting to previous mark");
            }
            this.filteredStream.reset();
        }
        
        @Override
        public void close() throws IOException {
            if (TDSChannel.logger.isLoggable(Level.FINEST)) {
                TDSChannel.logger.finest(this.toString() + " Closing");
            }
            this.filteredStream.close();
        }
    }
    
    final class ProxyOutputStream extends OutputStream
    {
        private OutputStream filteredStream;
        private final byte[] singleByte;
        
        ProxyOutputStream(final OutputStream os) {
            this.singleByte = new byte[1];
            this.filteredStream = os;
        }
        
        final void setFilteredStream(final OutputStream os) {
            this.filteredStream = os;
        }
        
        @Override
        public void close() throws IOException {
            if (TDSChannel.logger.isLoggable(Level.FINEST)) {
                TDSChannel.logger.finest(this.toString() + " Closing");
            }
            this.filteredStream.close();
        }
        
        @Override
        public void flush() throws IOException {
            if (TDSChannel.logger.isLoggable(Level.FINEST)) {
                TDSChannel.logger.finest(this.toString() + " Flushing");
            }
            this.filteredStream.flush();
        }
        
        @Override
        public void write(final int b) throws IOException {
            this.singleByte[0] = (byte)(b & 0xFF);
            this.writeInternal(this.singleByte, 0, this.singleByte.length);
        }
        
        @Override
        public void write(final byte[] b) throws IOException {
            this.writeInternal(b, 0, b.length);
        }
        
        @Override
        public void write(final byte[] b, final int off, final int len) throws IOException {
            this.writeInternal(b, off, len);
        }
        
        private void writeInternal(final byte[] b, final int off, final int len) throws IOException {
            if (TDSChannel.logger.isLoggable(Level.FINEST)) {
                TDSChannel.logger.finest(this.toString() + " Writing " + len + " bytes");
            }
            this.filteredStream.write(b, off, len);
        }
    }
    
    private class ProxySocket extends Socket
    {
        private final TDSChannel tdsChannel;
        private final Logger logger;
        private final String logContext;
        private final ProxyInputStream proxyInputStream;
        private final ProxyOutputStream proxyOutputStream;
        
        ProxySocket(final TDSChannel tdsChannel) {
            this.tdsChannel = tdsChannel;
            this.logger = tdsChannel.getLogger();
            this.logContext = tdsChannel.toString() + " (ProxySocket):";
            final SSLHandshakeOutputStream sslHandshakeOutputStream = new SSLHandshakeOutputStream(tdsChannel);
            final SSLHandshakeInputStream sslHandshakeInputStream = new SSLHandshakeInputStream(tdsChannel, sslHandshakeOutputStream);
            this.proxyOutputStream = new ProxyOutputStream(sslHandshakeOutputStream);
            this.proxyInputStream = new ProxyInputStream(sslHandshakeInputStream);
        }
        
        void setStreams(final InputStream is, final OutputStream os) {
            this.proxyInputStream.setFilteredStream(is);
            this.proxyOutputStream.setFilteredStream(os);
        }
        
        @Override
        public InputStream getInputStream() throws IOException {
            if (this.logger.isLoggable(Level.FINEST)) {
                this.logger.finest(this.logContext + " Getting input stream");
            }
            return this.proxyInputStream;
        }
        
        @Override
        public OutputStream getOutputStream() throws IOException {
            if (this.logger.isLoggable(Level.FINEST)) {
                this.logger.finest(this.logContext + " Getting output stream");
            }
            return this.proxyOutputStream;
        }
        
        @Override
        public InetAddress getInetAddress() {
            return this.tdsChannel.tcpSocket.getInetAddress();
        }
        
        @Override
        public boolean getKeepAlive() throws SocketException {
            return this.tdsChannel.tcpSocket.getKeepAlive();
        }
        
        @Override
        public InetAddress getLocalAddress() {
            return this.tdsChannel.tcpSocket.getLocalAddress();
        }
        
        @Override
        public int getLocalPort() {
            return this.tdsChannel.tcpSocket.getLocalPort();
        }
        
        @Override
        public SocketAddress getLocalSocketAddress() {
            return this.tdsChannel.tcpSocket.getLocalSocketAddress();
        }
        
        @Override
        public boolean getOOBInline() throws SocketException {
            return this.tdsChannel.tcpSocket.getOOBInline();
        }
        
        @Override
        public int getPort() {
            return this.tdsChannel.tcpSocket.getPort();
        }
        
        @Override
        public int getReceiveBufferSize() throws SocketException {
            return this.tdsChannel.tcpSocket.getReceiveBufferSize();
        }
        
        @Override
        public SocketAddress getRemoteSocketAddress() {
            return this.tdsChannel.tcpSocket.getRemoteSocketAddress();
        }
        
        @Override
        public boolean getReuseAddress() throws SocketException {
            return this.tdsChannel.tcpSocket.getReuseAddress();
        }
        
        @Override
        public int getSendBufferSize() throws SocketException {
            return this.tdsChannel.tcpSocket.getSendBufferSize();
        }
        
        @Override
        public int getSoLinger() throws SocketException {
            return this.tdsChannel.tcpSocket.getSoLinger();
        }
        
        @Override
        public int getSoTimeout() throws SocketException {
            return this.tdsChannel.tcpSocket.getSoTimeout();
        }
        
        @Override
        public boolean getTcpNoDelay() throws SocketException {
            return this.tdsChannel.tcpSocket.getTcpNoDelay();
        }
        
        @Override
        public int getTrafficClass() throws SocketException {
            return this.tdsChannel.tcpSocket.getTrafficClass();
        }
        
        @Override
        public boolean isBound() {
            return true;
        }
        
        @Override
        public boolean isClosed() {
            return false;
        }
        
        @Override
        public boolean isConnected() {
            return true;
        }
        
        @Override
        public boolean isInputShutdown() {
            return false;
        }
        
        @Override
        public boolean isOutputShutdown() {
            return false;
        }
        
        @Override
        public String toString() {
            return this.tdsChannel.tcpSocket.toString();
        }
        
        @Override
        public SocketChannel getChannel() {
            return null;
        }
        
        @Override
        public void bind(final SocketAddress bindPoint) throws IOException {
            this.logger.finer(this.logContext + " Disallowed call to bind.  Throwing IOException.");
            throw new IOException();
        }
        
        @Override
        public void connect(final SocketAddress endpoint) throws IOException {
            this.logger.finer(this.logContext + " Disallowed call to connect (without timeout).  Throwing IOException.");
            throw new IOException();
        }
        
        @Override
        public void connect(final SocketAddress endpoint, final int timeout) throws IOException {
            this.logger.finer(this.logContext + " Disallowed call to connect (with timeout).  Throwing IOException.");
            throw new IOException();
        }
        
        @Override
        public void close() throws IOException {
            if (this.logger.isLoggable(Level.FINER)) {
                this.logger.finer(this.logContext + " Ignoring close");
            }
        }
        
        @Override
        public void setReceiveBufferSize(final int size) throws SocketException {
            if (this.logger.isLoggable(Level.FINER)) {
                this.logger.finer(this.toString() + " Ignoring setReceiveBufferSize size:" + size);
            }
        }
        
        @Override
        public void setSendBufferSize(final int size) throws SocketException {
            if (this.logger.isLoggable(Level.FINER)) {
                this.logger.finer(this.toString() + " Ignoring setSendBufferSize size:" + size);
            }
        }
        
        @Override
        public void setReuseAddress(final boolean on) throws SocketException {
            if (this.logger.isLoggable(Level.FINER)) {
                this.logger.finer(this.toString() + " Ignoring setReuseAddress");
            }
        }
        
        @Override
        public void setSoLinger(final boolean on, final int linger) throws SocketException {
            if (this.logger.isLoggable(Level.FINER)) {
                this.logger.finer(this.toString() + " Ignoring setSoLinger");
            }
        }
        
        @Override
        public void setSoTimeout(final int timeout) throws SocketException {
            if (this.logger.isLoggable(Level.FINER)) {
                this.logger.finer(this.toString() + " Ignoring setSoTimeout");
            }
        }
        
        @Override
        public void setTcpNoDelay(final boolean on) throws SocketException {
            if (this.logger.isLoggable(Level.FINER)) {
                this.logger.finer(this.toString() + " Ignoring setTcpNoDelay");
            }
        }
        
        @Override
        public void setTrafficClass(final int tc) throws SocketException {
            if (this.logger.isLoggable(Level.FINER)) {
                this.logger.finer(this.toString() + " Ignoring setTrafficClass");
            }
        }
        
        @Override
        public void shutdownInput() throws IOException {
            if (this.logger.isLoggable(Level.FINER)) {
                this.logger.finer(this.toString() + " Ignoring shutdownInput");
            }
        }
        
        @Override
        public void shutdownOutput() throws IOException {
            if (this.logger.isLoggable(Level.FINER)) {
                this.logger.finer(this.toString() + " Ignoring shutdownOutput");
            }
        }
        
        @Override
        public void sendUrgentData(final int data) throws IOException {
            if (this.logger.isLoggable(Level.FINER)) {
                this.logger.finer(this.toString() + " Ignoring sendUrgentData");
            }
        }
        
        @Override
        public void setKeepAlive(final boolean on) throws SocketException {
            if (this.logger.isLoggable(Level.FINER)) {
                this.logger.finer(this.toString() + " Ignoring setKeepAlive");
            }
        }
        
        @Override
        public void setOOBInline(final boolean on) throws SocketException {
            if (this.logger.isLoggable(Level.FINER)) {
                this.logger.finer(this.toString() + " Ignoring setOOBInline");
            }
        }
    }
    
    private final class PermissiveX509TrustManager implements X509TrustManager
    {
        private final TDSChannel tdsChannel;
        private final Logger logger;
        private final String logContext;
        
        PermissiveX509TrustManager(final TDSChannel tdsChannel) {
            this.tdsChannel = tdsChannel;
            this.logger = tdsChannel.getLogger();
            this.logContext = tdsChannel.toString() + " (PermissiveX509TrustManager):";
        }
        
        @Override
        public void checkClientTrusted(final X509Certificate[] chain, final String authType) throws CertificateException {
            if (this.logger.isLoggable(Level.FINER)) {
                this.logger.finer(this.logContext + " Trusting client certificate (!)");
            }
        }
        
        @Override
        public void checkServerTrusted(final X509Certificate[] chain, final String authType) throws CertificateException {
            if (this.logger.isLoggable(Level.FINER)) {
                this.logger.finer(this.logContext + " Trusting server certificate");
            }
        }
        
        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }
    
    private final class HostNameOverrideX509TrustManager implements X509TrustManager
    {
        private final Logger logger;
        private final String logContext;
        private final X509TrustManager defaultTrustManager;
        private String hostName;
        
        HostNameOverrideX509TrustManager(final TDSChannel tdsChannel, final X509TrustManager tm, final String hostName) {
            this.logger = tdsChannel.getLogger();
            this.logContext = tdsChannel.toString() + " (HostNameOverrideX509TrustManager):";
            this.defaultTrustManager = tm;
            this.hostName = hostName.toLowerCase(Locale.ENGLISH);
        }
        
        private String parseCommonName(String distinguishedName) {
            int index = distinguishedName.indexOf("cn=");
            if (index == -1) {
                return null;
            }
            for (distinguishedName = distinguishedName.substring(index + 3), index = 0; index < distinguishedName.length() && distinguishedName.charAt(index) != ','; ++index) {}
            String commonName = distinguishedName.substring(0, index);
            if (commonName.length() > 1 && '\"' == commonName.charAt(0)) {
                if ('\"' == commonName.charAt(commonName.length() - 1)) {
                    commonName = commonName.substring(1, commonName.length() - 1);
                }
                else {
                    commonName = null;
                }
            }
            return commonName;
        }
        
        private boolean validateServerName(final String nameInCert) {
            if (null == nameInCert) {
                if (this.logger.isLoggable(Level.FINER)) {
                    this.logger.finer(this.logContext + " Failed to parse the name from the certificate or name is empty.");
                }
                return false;
            }
            if (!nameInCert.startsWith("xn--") && nameInCert.contains("*")) {
                int hostIndex = 0;
                int certIndex = 0;
                int match = 0;
                int startIndex = -1;
                int periodCount = 0;
                while (hostIndex < this.hostName.length()) {
                    if ('.' == this.hostName.charAt(hostIndex)) {
                        ++periodCount;
                    }
                    if (certIndex < nameInCert.length() && this.hostName.charAt(hostIndex) == nameInCert.charAt(certIndex)) {
                        ++hostIndex;
                        ++certIndex;
                    }
                    else if (certIndex < nameInCert.length() && '*' == nameInCert.charAt(certIndex)) {
                        startIndex = certIndex;
                        match = hostIndex;
                        ++certIndex;
                    }
                    else {
                        if (startIndex == -1 || 0 != periodCount) {
                            this.logFailMessage(nameInCert);
                            return false;
                        }
                        certIndex = startIndex + 1;
                        hostIndex = ++match;
                    }
                }
                if (nameInCert.length() == certIndex && periodCount > 1) {
                    this.logSuccessMessage(nameInCert);
                    return true;
                }
                this.logFailMessage(nameInCert);
                return false;
            }
            else {
                if (!nameInCert.equals(this.hostName)) {
                    this.logFailMessage(nameInCert);
                    return false;
                }
                this.logSuccessMessage(nameInCert);
                return true;
            }
        }
        
        private void logFailMessage(final String nameInCert) {
            if (this.logger.isLoggable(Level.FINER)) {
                this.logger.finer(this.logContext + " The name in certificate " + nameInCert + " does not match with the server name " + this.hostName + ".");
            }
        }
        
        private void logSuccessMessage(final String nameInCert) {
            if (this.logger.isLoggable(Level.FINER)) {
                this.logger.finer(this.logContext + " The name in certificate:" + nameInCert + " validated against server name " + this.hostName + ".");
            }
        }
        
        @Override
        public void checkClientTrusted(final X509Certificate[] chain, final String authType) throws CertificateException {
            if (this.logger.isLoggable(Level.FINEST)) {
                this.logger.finest(this.logContext + " Forwarding ClientTrusted.");
            }
            this.defaultTrustManager.checkClientTrusted(chain, authType);
        }
        
        @Override
        public void checkServerTrusted(final X509Certificate[] chain, final String authType) throws CertificateException {
            if (this.logger.isLoggable(Level.FINEST)) {
                this.logger.finest(this.logContext + " Forwarding Trusting server certificate");
            }
            this.defaultTrustManager.checkServerTrusted(chain, authType);
            if (this.logger.isLoggable(Level.FINEST)) {
                this.logger.finest(this.logContext + " default serverTrusted succeeded proceeding with server name validation");
            }
            this.validateServerNameInCertificate(chain[0]);
        }
        
        private void validateServerNameInCertificate(final X509Certificate cert) throws CertificateException {
            final String nameInCertDN = cert.getSubjectX500Principal().getName("canonical");
            if (this.logger.isLoggable(Level.FINER)) {
                this.logger.finer(this.logContext + " Validating the server name:" + this.hostName);
                this.logger.finer(this.logContext + " The DN name in certificate:" + nameInCertDN);
            }
            final String subjectCN = this.parseCommonName(nameInCertDN);
            boolean isServerNameValidated = this.validateServerName(subjectCN);
            if (!isServerNameValidated) {
                final Collection<List<?>> sanCollection = cert.getSubjectAlternativeNames();
                if (sanCollection != null) {
                    for (final List<?> sanEntry : sanCollection) {
                        if (sanEntry != null && sanEntry.size() >= 2) {
                            final Object key = sanEntry.get(0);
                            final Object value = sanEntry.get(1);
                            if (this.logger.isLoggable(Level.FINER)) {
                                this.logger.finer(this.logContext + "Key: " + key + "; KeyClass:" + ((key != null) ? key.getClass() : null) + ";value: " + value + "; valueClass:" + ((value != null) ? value.getClass() : null));
                            }
                            if (key == null || !(key instanceof Integer) || (int)key != 2) {
                                continue;
                            }
                            if (value != null && value instanceof String) {
                                String dnsNameInSANCert = (String)value;
                                dnsNameInSANCert = dnsNameInSANCert.toLowerCase(Locale.ENGLISH);
                                isServerNameValidated = this.validateServerName(dnsNameInSANCert);
                                if (isServerNameValidated) {
                                    if (this.logger.isLoggable(Level.FINER)) {
                                        this.logger.finer(this.logContext + " found a valid name in certificate: " + dnsNameInSANCert);
                                        break;
                                    }
                                    break;
                                }
                            }
                            if (!this.logger.isLoggable(Level.FINER)) {
                                continue;
                            }
                            this.logger.finer(this.logContext + " the following name in certificate does not match the serverName: " + value);
                        }
                        else {
                            if (!this.logger.isLoggable(Level.FINER)) {
                                continue;
                            }
                            this.logger.finer(this.logContext + " found an invalid san entry: " + sanEntry);
                        }
                    }
                }
            }
            if (!isServerNameValidated) {
                final String msg = SQLServerException.getErrString("R_certNameFailed");
                throw new CertificateException(msg);
            }
        }
        
        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return this.defaultTrustManager.getAcceptedIssuers();
        }
    }
    
    enum SSLHandhsakeState
    {
        SSL_HANDHSAKE_NOT_STARTED, 
        SSL_HANDHSAKE_STARTED, 
        SSL_HANDHSAKE_COMPLETE;
    }
}
