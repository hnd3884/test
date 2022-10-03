package org.apache.commons.net.ftp;

import org.apache.commons.net.util.Base64;
import javax.net.ServerSocketFactory;
import javax.net.SocketFactory;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.SSLHandshakeException;
import java.io.Writer;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import org.apache.commons.net.util.SSLSocketUtils;
import javax.net.ssl.SSLSocket;
import org.apache.commons.net.util.SSLContextUtils;
import javax.net.ssl.SSLException;
import java.io.IOException;
import org.apache.commons.net.util.TrustManagerUtils;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.KeyManager;
import javax.net.ssl.TrustManager;
import java.net.Socket;
import javax.net.ssl.SSLContext;

public class FTPSClient extends FTPClient
{
    public static final int DEFAULT_FTPS_DATA_PORT = 989;
    public static final int DEFAULT_FTPS_PORT = 990;
    private static final String[] PROT_COMMAND_VALUE;
    private static final String DEFAULT_PROT = "C";
    private static final String DEFAULT_PROTOCOL = "TLS";
    private static final String CMD_AUTH = "AUTH";
    private static final String CMD_ADAT = "ADAT";
    private static final String CMD_PROT = "PROT";
    private static final String CMD_PBSZ = "PBSZ";
    private static final String CMD_MIC = "MIC";
    private static final String CMD_CONF = "CONF";
    private static final String CMD_ENC = "ENC";
    private static final String CMD_CCC = "CCC";
    private final boolean isImplicit;
    private final String protocol;
    private String auth;
    private SSLContext context;
    private Socket plainSocket;
    private boolean isCreation;
    private boolean isClientMode;
    private boolean isNeedClientAuth;
    private boolean isWantClientAuth;
    private String[] suites;
    private String[] protocols;
    private TrustManager trustManager;
    private KeyManager keyManager;
    private HostnameVerifier hostnameVerifier;
    private boolean tlsEndpointChecking;
    @Deprecated
    public static String KEYSTORE_ALGORITHM;
    @Deprecated
    public static String TRUSTSTORE_ALGORITHM;
    @Deprecated
    public static String PROVIDER;
    @Deprecated
    public static String STORE_TYPE;
    
    public FTPSClient() {
        this("TLS", false);
    }
    
    public FTPSClient(final boolean isImplicit) {
        this("TLS", isImplicit);
    }
    
    public FTPSClient(final String protocol) {
        this(protocol, false);
    }
    
    public FTPSClient(final String protocol, final boolean isImplicit) {
        this.auth = "TLS";
        this.isCreation = true;
        this.isClientMode = true;
        this.isNeedClientAuth = false;
        this.isWantClientAuth = false;
        this.suites = null;
        this.protocols = null;
        this.trustManager = TrustManagerUtils.getValidateServerCertificateTrustManager();
        this.keyManager = null;
        this.hostnameVerifier = null;
        this.protocol = protocol;
        this.isImplicit = isImplicit;
        if (isImplicit) {
            this.setDefaultPort(990);
        }
    }
    
    public FTPSClient(final boolean isImplicit, final SSLContext context) {
        this("TLS", isImplicit);
        this.context = context;
    }
    
    public FTPSClient(final SSLContext context) {
        this(false, context);
    }
    
    public void setAuthValue(final String auth) {
        this.auth = auth;
    }
    
    public String getAuthValue() {
        return this.auth;
    }
    
    @Override
    protected void _connectAction_() throws IOException {
        if (this.isImplicit) {
            this.sslNegotiation();
        }
        super._connectAction_();
        if (!this.isImplicit) {
            this.execAUTH();
            this.sslNegotiation();
        }
    }
    
    protected void execAUTH() throws SSLException, IOException {
        final int replyCode = this.sendCommand("AUTH", this.auth);
        if (334 != replyCode) {
            if (234 != replyCode) {
                throw new SSLException(this.getReplyString());
            }
        }
    }
    
    private void initSslContext() throws IOException {
        if (this.context == null) {
            this.context = SSLContextUtils.createSSLContext(this.protocol, this.getKeyManager(), this.getTrustManager());
        }
    }
    
    protected void sslNegotiation() throws IOException {
        this.plainSocket = this._socket_;
        this.initSslContext();
        final SSLSocketFactory ssf = this.context.getSocketFactory();
        final String host = (this._hostname_ != null) ? this._hostname_ : this.getRemoteAddress().getHostAddress();
        final int port = this._socket_.getPort();
        final SSLSocket socket = (SSLSocket)ssf.createSocket(this._socket_, host, port, false);
        socket.setEnableSessionCreation(this.isCreation);
        socket.setUseClientMode(this.isClientMode);
        if (this.isClientMode) {
            if (this.tlsEndpointChecking) {
                SSLSocketUtils.enableEndpointNameVerification(socket);
            }
        }
        else {
            socket.setNeedClientAuth(this.isNeedClientAuth);
            socket.setWantClientAuth(this.isWantClientAuth);
        }
        if (this.protocols != null) {
            socket.setEnabledProtocols(this.protocols);
        }
        if (this.suites != null) {
            socket.setEnabledCipherSuites(this.suites);
        }
        socket.startHandshake();
        this._socket_ = socket;
        this._controlInput_ = new BufferedReader(new InputStreamReader(socket.getInputStream(), this.getControlEncoding()));
        this._controlOutput_ = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), this.getControlEncoding()));
        if (this.isClientMode && this.hostnameVerifier != null && !this.hostnameVerifier.verify(host, socket.getSession())) {
            throw new SSLHandshakeException("Hostname doesn't match certificate");
        }
    }
    
    private KeyManager getKeyManager() {
        return this.keyManager;
    }
    
    public void setKeyManager(final KeyManager keyManager) {
        this.keyManager = keyManager;
    }
    
    public void setEnabledSessionCreation(final boolean isCreation) {
        this.isCreation = isCreation;
    }
    
    public boolean getEnableSessionCreation() {
        return this._socket_ instanceof SSLSocket && ((SSLSocket)this._socket_).getEnableSessionCreation();
    }
    
    public void setNeedClientAuth(final boolean isNeedClientAuth) {
        this.isNeedClientAuth = isNeedClientAuth;
    }
    
    public boolean getNeedClientAuth() {
        return this._socket_ instanceof SSLSocket && ((SSLSocket)this._socket_).getNeedClientAuth();
    }
    
    public void setWantClientAuth(final boolean isWantClientAuth) {
        this.isWantClientAuth = isWantClientAuth;
    }
    
    public boolean getWantClientAuth() {
        return this._socket_ instanceof SSLSocket && ((SSLSocket)this._socket_).getWantClientAuth();
    }
    
    public void setUseClientMode(final boolean isClientMode) {
        this.isClientMode = isClientMode;
    }
    
    public boolean getUseClientMode() {
        return this._socket_ instanceof SSLSocket && ((SSLSocket)this._socket_).getUseClientMode();
    }
    
    public void setEnabledCipherSuites(final String[] cipherSuites) {
        System.arraycopy(cipherSuites, 0, this.suites = new String[cipherSuites.length], 0, cipherSuites.length);
    }
    
    public String[] getEnabledCipherSuites() {
        if (this._socket_ instanceof SSLSocket) {
            return ((SSLSocket)this._socket_).getEnabledCipherSuites();
        }
        return null;
    }
    
    public void setEnabledProtocols(final String[] protocolVersions) {
        System.arraycopy(protocolVersions, 0, this.protocols = new String[protocolVersions.length], 0, protocolVersions.length);
    }
    
    public String[] getEnabledProtocols() {
        if (this._socket_ instanceof SSLSocket) {
            return ((SSLSocket)this._socket_).getEnabledProtocols();
        }
        return null;
    }
    
    public void execPBSZ(final long pbsz) throws SSLException, IOException {
        if (pbsz < 0L || 4294967295L < pbsz) {
            throw new IllegalArgumentException();
        }
        final int status = this.sendCommand("PBSZ", String.valueOf(pbsz));
        if (200 != status) {
            throw new SSLException(this.getReplyString());
        }
    }
    
    public long parsePBSZ(final long pbsz) throws SSLException, IOException {
        this.execPBSZ(pbsz);
        long minvalue = pbsz;
        final String remainder = this.extractPrefixedData("PBSZ=", this.getReplyString());
        if (remainder != null) {
            final long replysz = Long.parseLong(remainder);
            if (replysz < minvalue) {
                minvalue = replysz;
            }
        }
        return minvalue;
    }
    
    public void execPROT(String prot) throws SSLException, IOException {
        if (prot == null) {
            prot = "C";
        }
        if (!this.checkPROTValue(prot)) {
            throw new IllegalArgumentException();
        }
        if (200 != this.sendCommand("PROT", prot)) {
            throw new SSLException(this.getReplyString());
        }
        if ("C".equals(prot)) {
            this.setSocketFactory(null);
            this.setServerSocketFactory(null);
        }
        else {
            this.setSocketFactory(new FTPSSocketFactory(this.context));
            this.setServerSocketFactory(new FTPSServerSocketFactory(this.context));
            this.initSslContext();
        }
    }
    
    private boolean checkPROTValue(final String prot) {
        for (final String element : FTPSClient.PROT_COMMAND_VALUE) {
            if (element.equals(prot)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public int sendCommand(final String command, final String args) throws IOException {
        final int repCode = super.sendCommand(command, args);
        if ("CCC".equals(command)) {
            if (200 != repCode) {
                throw new SSLException(this.getReplyString());
            }
            this._socket_.close();
            this._socket_ = this.plainSocket;
            this._controlInput_ = new BufferedReader(new InputStreamReader(this._socket_.getInputStream(), this.getControlEncoding()));
            this._controlOutput_ = new BufferedWriter(new OutputStreamWriter(this._socket_.getOutputStream(), this.getControlEncoding()));
        }
        return repCode;
    }
    
    @Deprecated
    @Override
    protected Socket _openDataConnection_(final int command, final String arg) throws IOException {
        return this._openDataConnection_(FTPCommand.getCommand(command), arg);
    }
    
    @Override
    protected Socket _openDataConnection_(final String command, final String arg) throws IOException {
        final Socket socket = super._openDataConnection_(command, arg);
        this._prepareDataSocket_(socket);
        if (socket instanceof SSLSocket) {
            final SSLSocket sslSocket = (SSLSocket)socket;
            sslSocket.setUseClientMode(this.isClientMode);
            sslSocket.setEnableSessionCreation(this.isCreation);
            if (!this.isClientMode) {
                sslSocket.setNeedClientAuth(this.isNeedClientAuth);
                sslSocket.setWantClientAuth(this.isWantClientAuth);
            }
            if (this.suites != null) {
                sslSocket.setEnabledCipherSuites(this.suites);
            }
            if (this.protocols != null) {
                sslSocket.setEnabledProtocols(this.protocols);
            }
            sslSocket.startHandshake();
        }
        return socket;
    }
    
    protected void _prepareDataSocket_(final Socket socket) throws IOException {
    }
    
    public TrustManager getTrustManager() {
        return this.trustManager;
    }
    
    public void setTrustManager(final TrustManager trustManager) {
        this.trustManager = trustManager;
    }
    
    public HostnameVerifier getHostnameVerifier() {
        return this.hostnameVerifier;
    }
    
    public void setHostnameVerifier(final HostnameVerifier newHostnameVerifier) {
        this.hostnameVerifier = newHostnameVerifier;
    }
    
    public boolean isEndpointCheckingEnabled() {
        return this.tlsEndpointChecking;
    }
    
    public void setEndpointCheckingEnabled(final boolean enable) {
        this.tlsEndpointChecking = enable;
    }
    
    @Override
    public void disconnect() throws IOException {
        super.disconnect();
        if (this.plainSocket != null) {
            this.plainSocket.close();
        }
        this.setSocketFactory(null);
        this.setServerSocketFactory(null);
    }
    
    public int execAUTH(final String mechanism) throws IOException {
        return this.sendCommand("AUTH", mechanism);
    }
    
    public int execADAT(final byte[] data) throws IOException {
        if (data != null) {
            return this.sendCommand("ADAT", Base64.encodeBase64StringUnChunked(data));
        }
        return this.sendCommand("ADAT");
    }
    
    public int execCCC() throws IOException {
        final int repCode = this.sendCommand("CCC");
        return repCode;
    }
    
    public int execMIC(final byte[] data) throws IOException {
        if (data != null) {
            return this.sendCommand("MIC", Base64.encodeBase64StringUnChunked(data));
        }
        return this.sendCommand("MIC", "");
    }
    
    public int execCONF(final byte[] data) throws IOException {
        if (data != null) {
            return this.sendCommand("CONF", Base64.encodeBase64StringUnChunked(data));
        }
        return this.sendCommand("CONF", "");
    }
    
    public int execENC(final byte[] data) throws IOException {
        if (data != null) {
            return this.sendCommand("ENC", Base64.encodeBase64StringUnChunked(data));
        }
        return this.sendCommand("ENC", "");
    }
    
    public byte[] parseADATReply(final String reply) {
        if (reply == null) {
            return null;
        }
        return Base64.decodeBase64(this.extractPrefixedData("ADAT=", reply));
    }
    
    private String extractPrefixedData(final String prefix, final String reply) {
        final int idx = reply.indexOf(prefix);
        if (idx == -1) {
            return null;
        }
        return reply.substring(idx + prefix.length()).trim();
    }
    
    static {
        PROT_COMMAND_VALUE = new String[] { "C", "E", "S", "P" };
    }
}
