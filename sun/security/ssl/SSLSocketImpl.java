package sun.security.ssl;

import javax.net.ssl.SSLHandshakeException;
import sun.misc.SharedSecrets;
import javax.net.ssl.SSLProtocolException;
import java.io.EOFException;
import javax.net.ssl.SSLException;
import java.nio.ByteBuffer;
import java.util.List;
import javax.net.ssl.SSLSocket;
import java.util.function.BiFunction;
import javax.net.ssl.SSLParameters;
import java.io.OutputStream;
import java.io.InterruptedIOException;
import javax.net.ssl.HandshakeCompletedListener;
import javax.net.ssl.SSLSession;
import java.net.SocketException;
import java.io.InputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.io.IOException;
import java.net.SocketAddress;
import java.net.InetAddress;
import java.net.InetSocketAddress;

public final class SSLSocketImpl extends BaseSSLSocketImpl implements SSLTransport
{
    final SSLContextImpl sslContext;
    final TransportContext conContext;
    private final AppInputStream appInput;
    private final AppOutputStream appOutput;
    private String peerHost;
    private boolean autoClose;
    private boolean isConnected;
    private volatile boolean tlsIsClosed;
    private static final boolean trustNameService;
    
    SSLSocketImpl(final SSLContextImpl sslContext) {
        this.appInput = new AppInputStream();
        this.appOutput = new AppOutputStream();
        this.isConnected = false;
        this.tlsIsClosed = false;
        this.sslContext = sslContext;
        final HandshakeHash handshakeHash = new HandshakeHash();
        this.conContext = new TransportContext(sslContext, this, new SSLSocketInputRecord(handshakeHash), new SSLSocketOutputRecord(handshakeHash), true);
    }
    
    SSLSocketImpl(final SSLContextImpl sslContext, final SSLConfiguration sslConfiguration) {
        this.appInput = new AppInputStream();
        this.appOutput = new AppOutputStream();
        this.isConnected = false;
        this.tlsIsClosed = false;
        this.sslContext = sslContext;
        final HandshakeHash handshakeHash = new HandshakeHash();
        this.conContext = new TransportContext(sslContext, this, sslConfiguration, new SSLSocketInputRecord(handshakeHash), new SSLSocketOutputRecord(handshakeHash));
    }
    
    SSLSocketImpl(final SSLContextImpl sslContext, final String peerHost, final int n) throws IOException, UnknownHostException {
        this.appInput = new AppInputStream();
        this.appOutput = new AppOutputStream();
        this.isConnected = false;
        this.tlsIsClosed = false;
        this.sslContext = sslContext;
        final HandshakeHash handshakeHash = new HandshakeHash();
        this.conContext = new TransportContext(sslContext, this, new SSLSocketInputRecord(handshakeHash), new SSLSocketOutputRecord(handshakeHash), true);
        this.peerHost = peerHost;
        this.connect((peerHost != null) ? new InetSocketAddress(peerHost, n) : new InetSocketAddress(InetAddress.getByName(null), n), 0);
    }
    
    SSLSocketImpl(final SSLContextImpl sslContext, final InetAddress inetAddress, final int n) throws IOException {
        this.appInput = new AppInputStream();
        this.appOutput = new AppOutputStream();
        this.isConnected = false;
        this.tlsIsClosed = false;
        this.sslContext = sslContext;
        final HandshakeHash handshakeHash = new HandshakeHash();
        this.conContext = new TransportContext(sslContext, this, new SSLSocketInputRecord(handshakeHash), new SSLSocketOutputRecord(handshakeHash), true);
        this.connect(new InetSocketAddress(inetAddress, n), 0);
    }
    
    SSLSocketImpl(final SSLContextImpl sslContext, final String peerHost, final int n, final InetAddress inetAddress, final int n2) throws IOException, UnknownHostException {
        this.appInput = new AppInputStream();
        this.appOutput = new AppOutputStream();
        this.isConnected = false;
        this.tlsIsClosed = false;
        this.sslContext = sslContext;
        final HandshakeHash handshakeHash = new HandshakeHash();
        this.conContext = new TransportContext(sslContext, this, new SSLSocketInputRecord(handshakeHash), new SSLSocketOutputRecord(handshakeHash), true);
        this.peerHost = peerHost;
        this.bind(new InetSocketAddress(inetAddress, n2));
        this.connect((peerHost != null) ? new InetSocketAddress(peerHost, n) : new InetSocketAddress(InetAddress.getByName(null), n), 0);
    }
    
    SSLSocketImpl(final SSLContextImpl sslContext, final InetAddress inetAddress, final int n, final InetAddress inetAddress2, final int n2) throws IOException {
        this.appInput = new AppInputStream();
        this.appOutput = new AppOutputStream();
        this.isConnected = false;
        this.tlsIsClosed = false;
        this.sslContext = sslContext;
        final HandshakeHash handshakeHash = new HandshakeHash();
        this.conContext = new TransportContext(sslContext, this, new SSLSocketInputRecord(handshakeHash), new SSLSocketOutputRecord(handshakeHash), true);
        this.bind(new InetSocketAddress(inetAddress2, n2));
        this.connect(new InetSocketAddress(inetAddress, n), 0);
    }
    
    SSLSocketImpl(final SSLContextImpl sslContext, final Socket socket, final InputStream inputStream, final boolean autoClose) throws IOException {
        super(socket, inputStream);
        this.appInput = new AppInputStream();
        this.appOutput = new AppOutputStream();
        this.isConnected = false;
        this.tlsIsClosed = false;
        if (!socket.isConnected()) {
            throw new SocketException("Underlying socket is not connected");
        }
        this.sslContext = sslContext;
        final HandshakeHash handshakeHash = new HandshakeHash();
        this.conContext = new TransportContext(sslContext, this, new SSLSocketInputRecord(handshakeHash), new SSLSocketOutputRecord(handshakeHash), false);
        this.autoClose = autoClose;
        this.doneConnect();
    }
    
    SSLSocketImpl(final SSLContextImpl sslContext, final Socket socket, final String peerHost, final int n, final boolean autoClose) throws IOException {
        super(socket);
        this.appInput = new AppInputStream();
        this.appOutput = new AppOutputStream();
        this.isConnected = false;
        this.tlsIsClosed = false;
        if (!socket.isConnected()) {
            throw new SocketException("Underlying socket is not connected");
        }
        this.sslContext = sslContext;
        final HandshakeHash handshakeHash = new HandshakeHash();
        this.conContext = new TransportContext(sslContext, this, new SSLSocketInputRecord(handshakeHash), new SSLSocketOutputRecord(handshakeHash), true);
        this.peerHost = peerHost;
        this.autoClose = autoClose;
        this.doneConnect();
    }
    
    @Override
    public void connect(final SocketAddress socketAddress, final int n) throws IOException {
        if (this.isLayered()) {
            throw new SocketException("Already connected");
        }
        if (!(socketAddress instanceof InetSocketAddress)) {
            throw new SocketException("Cannot handle non-Inet socket addresses.");
        }
        super.connect(socketAddress, n);
        this.doneConnect();
    }
    
    @Override
    public String[] getSupportedCipherSuites() {
        return CipherSuite.namesOf(this.sslContext.getSupportedCipherSuites());
    }
    
    @Override
    public synchronized String[] getEnabledCipherSuites() {
        return CipherSuite.namesOf(this.conContext.sslConfig.enabledCipherSuites);
    }
    
    @Override
    public synchronized void setEnabledCipherSuites(final String[] array) {
        this.conContext.sslConfig.enabledCipherSuites = CipherSuite.validValuesOf(array);
    }
    
    @Override
    public String[] getSupportedProtocols() {
        return ProtocolVersion.toStringArray(this.sslContext.getSupportedProtocolVersions());
    }
    
    @Override
    public synchronized String[] getEnabledProtocols() {
        return ProtocolVersion.toStringArray(this.conContext.sslConfig.enabledProtocols);
    }
    
    @Override
    public synchronized void setEnabledProtocols(final String[] array) {
        if (array == null) {
            throw new IllegalArgumentException("Protocols cannot be null");
        }
        this.conContext.sslConfig.enabledProtocols = ProtocolVersion.namesOf(array);
    }
    
    @Override
    public SSLSession getSession() {
        try {
            this.ensureNegotiated(false);
        }
        catch (final IOException ex) {
            if (SSLLogger.isOn && SSLLogger.isOn("handshake")) {
                SSLLogger.severe("handshake failed", ex);
            }
            return new SSLSessionImpl();
        }
        return this.conContext.conSession;
    }
    
    @Override
    public synchronized SSLSession getHandshakeSession() {
        return (this.conContext.handshakeContext == null) ? null : this.conContext.handshakeContext.handshakeSession;
    }
    
    @Override
    public synchronized void addHandshakeCompletedListener(final HandshakeCompletedListener handshakeCompletedListener) {
        if (handshakeCompletedListener == null) {
            throw new IllegalArgumentException("listener is null");
        }
        this.conContext.sslConfig.addHandshakeCompletedListener(handshakeCompletedListener);
    }
    
    @Override
    public synchronized void removeHandshakeCompletedListener(final HandshakeCompletedListener handshakeCompletedListener) {
        if (handshakeCompletedListener == null) {
            throw new IllegalArgumentException("listener is null");
        }
        this.conContext.sslConfig.removeHandshakeCompletedListener(handshakeCompletedListener);
    }
    
    @Override
    public void startHandshake() throws IOException {
        this.startHandshake(true);
    }
    
    private void startHandshake(final boolean b) throws IOException {
        if (!this.isConnected) {
            throw new SocketException("Socket is not connected");
        }
        if (this.conContext.isBroken || this.conContext.isInboundClosed() || this.conContext.isOutboundClosed()) {
            throw new SocketException("Socket has been closed or broken");
        }
        synchronized (this.conContext) {
            if (this.conContext.isBroken || this.conContext.isInboundClosed() || this.conContext.isOutboundClosed()) {
                throw new SocketException("Socket has been closed or broken");
            }
            try {
                this.conContext.kickstart();
                if (!this.conContext.isNegotiated) {
                    this.readHandshakeRecord();
                }
            }
            catch (final InterruptedIOException ex) {
                if (!b) {
                    throw this.conContext.fatal(Alert.HANDSHAKE_FAILURE, "Couldn't kickstart handshaking", ex);
                }
                this.handleException(ex);
            }
            catch (final IOException ex2) {
                throw this.conContext.fatal(Alert.HANDSHAKE_FAILURE, "Couldn't kickstart handshaking", ex2);
            }
            catch (final Exception ex3) {
                this.handleException(ex3);
            }
        }
    }
    
    @Override
    public synchronized void setUseClientMode(final boolean useClientMode) {
        this.conContext.setUseClientMode(useClientMode);
    }
    
    @Override
    public synchronized boolean getUseClientMode() {
        return this.conContext.sslConfig.isClientMode;
    }
    
    @Override
    public synchronized void setNeedClientAuth(final boolean b) {
        this.conContext.sslConfig.clientAuthType = (b ? ClientAuthType.CLIENT_AUTH_REQUIRED : ClientAuthType.CLIENT_AUTH_NONE);
    }
    
    @Override
    public synchronized boolean getNeedClientAuth() {
        return this.conContext.sslConfig.clientAuthType == ClientAuthType.CLIENT_AUTH_REQUIRED;
    }
    
    @Override
    public synchronized void setWantClientAuth(final boolean b) {
        this.conContext.sslConfig.clientAuthType = (b ? ClientAuthType.CLIENT_AUTH_REQUESTED : ClientAuthType.CLIENT_AUTH_NONE);
    }
    
    @Override
    public synchronized boolean getWantClientAuth() {
        return this.conContext.sslConfig.clientAuthType == ClientAuthType.CLIENT_AUTH_REQUESTED;
    }
    
    @Override
    public synchronized void setEnableSessionCreation(final boolean enableSessionCreation) {
        this.conContext.sslConfig.enableSessionCreation = enableSessionCreation;
    }
    
    @Override
    public synchronized boolean getEnableSessionCreation() {
        return this.conContext.sslConfig.enableSessionCreation;
    }
    
    @Override
    public boolean isClosed() {
        return this.tlsIsClosed;
    }
    
    @Override
    public void close() throws IOException {
        if (this.tlsIsClosed) {
            return;
        }
        if (SSLLogger.isOn && SSLLogger.isOn("ssl")) {
            SSLLogger.fine("duplex close of SSLSocket", new Object[0]);
        }
        try {
            if (!this.isOutputShutdown()) {
                this.duplexCloseOutput();
            }
            if (!this.isInputShutdown()) {
                this.duplexCloseInput();
            }
            if (!this.isClosed()) {
                this.closeSocket(false);
            }
        }
        catch (final IOException ex) {
            if (SSLLogger.isOn && SSLLogger.isOn("ssl")) {
                SSLLogger.warning("SSLSocket duplex close failed", ex);
            }
        }
        finally {
            this.tlsIsClosed = true;
        }
    }
    
    private void duplexCloseOutput() throws IOException {
        boolean b = false;
        boolean b2 = false;
        if (this.conContext.isNegotiated) {
            if (!this.conContext.protocolVersion.useTLS13PlusSpec()) {
                b2 = true;
            }
            else {
                b = true;
            }
        }
        else if (this.conContext.handshakeContext != null) {
            b = true;
            final ProtocolVersion negotiatedProtocol = this.conContext.handshakeContext.negotiatedProtocol;
            if (negotiatedProtocol == null || !negotiatedProtocol.useTLS13PlusSpec()) {
                b2 = true;
            }
        }
        this.closeNotify(b);
        if (!this.isInputShutdown()) {
            this.bruteForceCloseInput(b2);
        }
    }
    
    void closeNotify(final boolean b) throws IOException {
        try {
            synchronized (this.conContext.outputRecord) {
                if (b) {
                    this.conContext.warning(Alert.USER_CANCELED);
                }
                this.conContext.warning(Alert.CLOSE_NOTIFY);
            }
        }
        finally {
            if (!this.conContext.isOutboundClosed()) {
                this.conContext.outputRecord.close();
            }
            if ((this.autoClose || !this.isLayered()) && !super.isOutputShutdown()) {
                super.shutdownOutput();
            }
        }
    }
    
    private void duplexCloseInput() throws IOException {
        boolean b = false;
        if (this.conContext.isNegotiated && !this.conContext.protocolVersion.useTLS13PlusSpec()) {
            b = true;
        }
        this.bruteForceCloseInput(b);
    }
    
    private void bruteForceCloseInput(final boolean b) throws IOException {
        if (b) {
            try {
                this.shutdown();
            }
            finally {
                if (!this.isInputShutdown()) {
                    this.shutdownInput(false);
                }
            }
        }
        else {
            if (!this.conContext.isInboundClosed()) {
                try {
                    this.appInput.deplete();
                }
                finally {
                    this.conContext.inputRecord.close();
                }
            }
            if ((this.autoClose || !this.isLayered()) && !super.isInputShutdown()) {
                super.shutdownInput();
            }
        }
    }
    
    @Override
    public void shutdownInput() throws IOException {
        this.shutdownInput(true);
    }
    
    private void shutdownInput(final boolean b) throws IOException {
        if (this.isInputShutdown()) {
            return;
        }
        if (SSLLogger.isOn && SSLLogger.isOn("ssl")) {
            SSLLogger.fine("close inbound of SSLSocket", new Object[0]);
        }
        if (b && !this.conContext.isInputCloseNotified && (this.conContext.isNegotiated || this.conContext.handshakeContext != null)) {
            throw this.conContext.fatal(Alert.INTERNAL_ERROR, "closing inbound before receiving peer's close_notify");
        }
        this.conContext.closeInbound();
        if ((this.autoClose || !this.isLayered()) && !super.isInputShutdown()) {
            super.shutdownInput();
        }
    }
    
    @Override
    public boolean isInputShutdown() {
        return this.conContext.isInboundClosed() && ((!this.autoClose && this.isLayered()) || super.isInputShutdown());
    }
    
    @Override
    public void shutdownOutput() throws IOException {
        if (this.isOutputShutdown()) {
            return;
        }
        if (SSLLogger.isOn && SSLLogger.isOn("ssl")) {
            SSLLogger.fine("close outbound of SSLSocket", new Object[0]);
        }
        this.conContext.closeOutbound();
        if ((this.autoClose || !this.isLayered()) && !super.isOutputShutdown()) {
            super.shutdownOutput();
        }
    }
    
    @Override
    public boolean isOutputShutdown() {
        return this.conContext.isOutboundClosed() && ((!this.autoClose && this.isLayered()) || super.isOutputShutdown());
    }
    
    @Override
    public synchronized InputStream getInputStream() throws IOException {
        if (this.isClosed()) {
            throw new SocketException("Socket is closed");
        }
        if (!this.isConnected) {
            throw new SocketException("Socket is not connected");
        }
        if (this.conContext.isInboundClosed() || this.isInputShutdown()) {
            throw new SocketException("Socket input is already shutdown");
        }
        return this.appInput;
    }
    
    private void ensureNegotiated(final boolean b) throws IOException {
        if (this.conContext.isNegotiated || this.conContext.isBroken || this.conContext.isInboundClosed() || this.conContext.isOutboundClosed()) {
            return;
        }
        synchronized (this.conContext) {
            if (this.conContext.isNegotiated || this.conContext.isBroken || this.conContext.isInboundClosed() || this.conContext.isOutboundClosed()) {
                return;
            }
            this.startHandshake(b);
        }
    }
    
    @Override
    public synchronized OutputStream getOutputStream() throws IOException {
        if (this.isClosed()) {
            throw new SocketException("Socket is closed");
        }
        if (!this.isConnected) {
            throw new SocketException("Socket is not connected");
        }
        if (this.conContext.isOutboundDone() || this.isOutputShutdown()) {
            throw new SocketException("Socket output is already shutdown");
        }
        return this.appOutput;
    }
    
    @Override
    public synchronized SSLParameters getSSLParameters() {
        return this.conContext.sslConfig.getSSLParameters();
    }
    
    @Override
    public synchronized void setSSLParameters(final SSLParameters sslParameters) {
        this.conContext.sslConfig.setSSLParameters(sslParameters);
        if (this.conContext.sslConfig.maximumPacketSize != 0) {
            this.conContext.outputRecord.changePacketSize(this.conContext.sslConfig.maximumPacketSize);
        }
    }
    
    public synchronized String getApplicationProtocol() {
        return this.conContext.applicationProtocol;
    }
    
    public synchronized String getHandshakeApplicationProtocol() {
        if (this.conContext.handshakeContext != null) {
            return this.conContext.handshakeContext.applicationProtocol;
        }
        return null;
    }
    
    public synchronized void setHandshakeApplicationProtocolSelector(final BiFunction<SSLSocket, List<String>, String> socketAPSelector) {
        this.conContext.sslConfig.socketAPSelector = socketAPSelector;
    }
    
    public synchronized BiFunction<SSLSocket, List<String>, String> getHandshakeApplicationProtocolSelector() {
        return this.conContext.sslConfig.socketAPSelector;
    }
    
    private int readHandshakeRecord() throws IOException {
        while (!this.conContext.isInboundClosed()) {
            try {
                if (this.decode(null).contentType == ContentType.HANDSHAKE.id && this.conContext.isNegotiated) {
                    return 0;
                }
                continue;
            }
            catch (final SSLException ex) {
                throw ex;
            }
            catch (final InterruptedIOException ex2) {
                throw ex2;
            }
            catch (final IOException ex3) {
                throw new SSLException("readHandshakeRecord", ex3);
            }
            break;
        }
        return -1;
    }
    
    private ByteBuffer readApplicationRecord(ByteBuffer allocate) throws IOException {
        while (!this.conContext.isInboundClosed()) {
            allocate.clear();
            final int bytesInCompletePacket = this.conContext.inputRecord.bytesInCompletePacket();
            if (bytesInCompletePacket < 0) {
                this.handleEOF(null);
                return null;
            }
            if (bytesInCompletePacket > 33093) {
                throw new SSLProtocolException("Illegal packet size: " + bytesInCompletePacket);
            }
            if (bytesInCompletePacket > allocate.remaining()) {
                allocate = ByteBuffer.allocate(bytesInCompletePacket);
            }
            try {
                final Plaintext decode;
                synchronized (this) {
                    decode = this.decode(allocate);
                }
                if (decode.contentType == ContentType.APPLICATION_DATA.id && allocate.position() > 0) {
                    return allocate;
                }
                continue;
            }
            catch (final SSLException ex) {
                throw ex;
            }
            catch (final InterruptedIOException ex2) {
                throw ex2;
            }
            catch (final IOException ex3) {
                if (!(ex3 instanceof SSLException)) {
                    throw new SSLException("readApplicationRecord", ex3);
                }
                throw ex3;
            }
        }
        return null;
    }
    
    private Plaintext decode(final ByteBuffer byteBuffer) throws IOException {
        Plaintext plaintext;
        try {
            if (byteBuffer == null) {
                plaintext = SSLTransport.decode(this.conContext, null, 0, 0, null, 0, 0);
            }
            else {
                plaintext = SSLTransport.decode(this.conContext, null, 0, 0, new ByteBuffer[] { byteBuffer }, 0, 1);
            }
        }
        catch (final EOFException ex) {
            plaintext = this.handleEOF(ex);
        }
        if (plaintext != Plaintext.PLAINTEXT_NULL && (this.conContext.inputRecord.seqNumIsHuge() || this.conContext.inputRecord.readCipher.atKeyLimit())) {
            this.tryKeyUpdate();
        }
        return plaintext;
    }
    
    private void tryKeyUpdate() throws IOException {
        if (this.conContext.handshakeContext == null && !this.conContext.isOutboundClosed() && !this.conContext.isInboundClosed() && !this.conContext.isBroken) {
            if (SSLLogger.isOn && SSLLogger.isOn("ssl")) {
                SSLLogger.finest("trigger key update", new Object[0]);
            }
            this.startHandshake();
        }
    }
    
    synchronized void doneConnect() throws IOException {
        if (this.peerHost == null || this.peerHost.isEmpty()) {
            this.useImplicitHost(SSLSocketImpl.trustNameService && this.conContext.sslConfig.isClientMode);
        }
        else {
            this.conContext.sslConfig.serverNames = Utilities.addToSNIServerNameList(this.conContext.sslConfig.serverNames, this.peerHost);
        }
        this.conContext.inputRecord.setReceiverStream(super.getInputStream());
        final OutputStream outputStream = super.getOutputStream();
        this.conContext.inputRecord.setDeliverStream(outputStream);
        this.conContext.outputRecord.setDeliverStream(outputStream);
        this.isConnected = true;
    }
    
    private void useImplicitHost(final boolean b) {
        final InetAddress inetAddress = this.getInetAddress();
        if (inetAddress == null) {
            return;
        }
        final String originalHostName = SharedSecrets.getJavaNetAccess().getOriginalHostName(inetAddress);
        if (originalHostName != null && !originalHostName.isEmpty()) {
            this.peerHost = originalHostName;
            if (this.conContext.sslConfig.serverNames.isEmpty() && !this.conContext.sslConfig.noSniExtension) {
                this.conContext.sslConfig.serverNames = Utilities.addToSNIServerNameList(this.conContext.sslConfig.serverNames, this.peerHost);
            }
            return;
        }
        if (!b) {
            this.peerHost = inetAddress.getHostAddress();
        }
        else {
            this.peerHost = this.getInetAddress().getHostName();
        }
    }
    
    public synchronized void setHost(final String peerHost) {
        this.peerHost = peerHost;
        this.conContext.sslConfig.serverNames = Utilities.addToSNIServerNameList(this.conContext.sslConfig.serverNames, peerHost);
    }
    
    private void handleException(final Exception ex) throws IOException {
        if (SSLLogger.isOn && SSLLogger.isOn("ssl")) {
            SSLLogger.warning("handling exception", ex);
        }
        if (ex instanceof InterruptedIOException) {
            throw (IOException)ex;
        }
        Alert alert;
        if (ex instanceof SSLException) {
            if (ex instanceof SSLHandshakeException) {
                alert = Alert.HANDSHAKE_FAILURE;
            }
            else {
                alert = Alert.UNEXPECTED_MESSAGE;
            }
        }
        else if (ex instanceof IOException) {
            alert = Alert.UNEXPECTED_MESSAGE;
        }
        else {
            alert = Alert.INTERNAL_ERROR;
        }
        throw this.conContext.fatal(alert, ex);
    }
    
    private Plaintext handleEOF(final EOFException ex) throws IOException {
        if (SSLSocketImpl.requireCloseNotify || this.conContext.handshakeContext != null) {
            SSLException ex2;
            if (this.conContext.handshakeContext != null) {
                ex2 = new SSLHandshakeException("Remote host terminated the handshake");
            }
            else {
                ex2 = new SSLProtocolException("Remote host terminated the connection");
            }
            if (ex != null) {
                ex2.initCause(ex);
            }
            throw ex2;
        }
        this.conContext.isInputCloseNotified = true;
        this.shutdownInput();
        return Plaintext.PLAINTEXT_NULL;
    }
    
    @Override
    public String getPeerHost() {
        return this.peerHost;
    }
    
    @Override
    public int getPeerPort() {
        return this.getPort();
    }
    
    @Override
    public boolean useDelegatedTask() {
        return false;
    }
    
    @Override
    public void shutdown() throws IOException {
        if (!this.isClosed()) {
            if (SSLLogger.isOn && SSLLogger.isOn("ssl")) {
                SSLLogger.fine("close the underlying socket", new Object[0]);
            }
            try {
                if (this.conContext.isInputCloseNotified) {
                    this.closeSocket(false);
                }
                else {
                    this.closeSocket(true);
                }
            }
            finally {
                this.tlsIsClosed = true;
            }
        }
    }
    
    private void closeSocket(final boolean b) throws IOException {
        if (SSLLogger.isOn && SSLLogger.isOn("ssl")) {
            SSLLogger.fine("close the SSL connection " + (b ? "(initiative)" : "(passive)"), new Object[0]);
        }
        if (this.autoClose || !this.isLayered()) {
            super.close();
        }
        else if (b && !this.conContext.isInboundClosed() && !this.isInputShutdown()) {
            this.waitForClose();
        }
    }
    
    private void waitForClose() throws IOException {
        if (SSLLogger.isOn && SSLLogger.isOn("ssl")) {
            SSLLogger.fine("wait for close_notify or alert", new Object[0]);
        }
        while (!this.conContext.isInboundClosed()) {
            try {
                final Plaintext decode = this.decode(null);
                if (!SSLLogger.isOn || !SSLLogger.isOn("ssl")) {
                    continue;
                }
                SSLLogger.finest("discard plaintext while waiting for close", decode);
            }
            catch (final Exception ex) {
                this.handleException(ex);
            }
        }
    }
    
    static {
        trustNameService = Utilities.getBooleanProperty("jdk.tls.trustNameService", false);
    }
    
    private class AppInputStream extends InputStream
    {
        private final byte[] oneByte;
        private ByteBuffer buffer;
        private volatile boolean appDataIsAvailable;
        
        AppInputStream() {
            this.oneByte = new byte[1];
            this.appDataIsAvailable = false;
            this.buffer = ByteBuffer.allocate(4096);
        }
        
        @Override
        public int available() throws IOException {
            if (!this.appDataIsAvailable || this.checkEOF()) {
                return 0;
            }
            return this.buffer.remaining();
        }
        
        @Override
        public int read() throws IOException {
            if (this.read(this.oneByte, 0, 1) <= 0) {
                return -1;
            }
            return this.oneByte[0] & 0xFF;
        }
        
        @Override
        public int read(final byte[] array, final int n, final int n2) throws IOException {
            if (array == null) {
                throw new NullPointerException("the target buffer is null");
            }
            if (n < 0 || n2 < 0 || n2 > array.length - n) {
                throw new IndexOutOfBoundsException("buffer length: " + array.length + ", offset; " + n + ", bytes to read:" + n2);
            }
            if (n2 == 0) {
                return 0;
            }
            if (this.checkEOF()) {
                return -1;
            }
            if (!SSLSocketImpl.this.conContext.isNegotiated && !SSLSocketImpl.this.conContext.isBroken && !SSLSocketImpl.this.conContext.isInboundClosed() && !SSLSocketImpl.this.conContext.isOutboundClosed()) {
                SSLSocketImpl.this.ensureNegotiated(true);
            }
            if (!SSLSocketImpl.this.conContext.isNegotiated || SSLSocketImpl.this.conContext.isBroken || SSLSocketImpl.this.conContext.isInboundClosed()) {
                throw new SocketException("Connection or inbound has closed");
            }
            synchronized (this) {
                final int available = this.available();
                if (available > 0) {
                    final int min = Math.min(available, n2);
                    this.buffer.get(array, n, min);
                    return min;
                }
                this.appDataIsAvailable = false;
                try {
                    final ByteBuffer access$300 = SSLSocketImpl.this.readApplicationRecord(this.buffer);
                    if (access$300 == null) {
                        return -1;
                    }
                    (this.buffer = access$300).flip();
                    final int min2 = Math.min(n2, access$300.remaining());
                    this.buffer.get(array, n, min2);
                    this.appDataIsAvailable = true;
                    return min2;
                }
                catch (final Exception ex) {
                    SSLSocketImpl.this.handleException(ex);
                    return -1;
                }
            }
        }
        
        @Override
        public synchronized long skip(long n) throws IOException {
            final byte[] array = new byte[256];
            long n2;
            int read;
            for (n2 = 0L; n > 0L; n -= read, n2 += read) {
                read = this.read(array, 0, (int)Math.min(n, array.length));
                if (read <= 0) {
                    break;
                }
            }
            return n2;
        }
        
        @Override
        public void close() throws IOException {
            if (SSLLogger.isOn && SSLLogger.isOn("ssl")) {
                SSLLogger.finest("Closing input stream", new Object[0]);
            }
            try {
                SSLSocketImpl.this.close();
            }
            catch (final IOException ex) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl")) {
                    SSLLogger.warning("input stream close failed", ex);
                }
            }
        }
        
        private boolean checkEOF() throws IOException {
            if (SSLSocketImpl.this.conContext.isBroken) {
                if (SSLSocketImpl.this.conContext.closeReason == null) {
                    return true;
                }
                throw new SSLException("Connection has closed: " + SSLSocketImpl.this.conContext.closeReason, SSLSocketImpl.this.conContext.closeReason);
            }
            else {
                if (SSLSocketImpl.this.conContext.isInboundClosed()) {
                    return true;
                }
                if (!SSLSocketImpl.this.conContext.isInputCloseNotified) {
                    return false;
                }
                if (SSLSocketImpl.this.conContext.closeReason == null) {
                    return true;
                }
                throw new SSLException("Connection has closed: " + SSLSocketImpl.this.conContext.closeReason, SSLSocketImpl.this.conContext.closeReason);
            }
        }
        
        private synchronized void deplete() {
            if (!SSLSocketImpl.this.conContext.isInboundClosed()) {
                if (!(SSLSocketImpl.this.conContext.inputRecord instanceof SSLSocketInputRecord)) {
                    return;
                }
                final SSLSocketInputRecord sslSocketInputRecord = (SSLSocketInputRecord)SSLSocketImpl.this.conContext.inputRecord;
                try {
                    sslSocketInputRecord.deplete(SSLSocketImpl.this.conContext.isNegotiated && SSLSocketImpl.this.getSoTimeout() > 0);
                }
                catch (final IOException ex) {
                    if (SSLLogger.isOn && SSLLogger.isOn("ssl")) {
                        SSLLogger.warning("input stream close depletion failed", ex);
                    }
                }
            }
        }
    }
    
    private class AppOutputStream extends OutputStream
    {
        private final byte[] oneByte;
        
        private AppOutputStream() {
            this.oneByte = new byte[1];
        }
        
        @Override
        public void write(final int n) throws IOException {
            this.oneByte[0] = (byte)n;
            this.write(this.oneByte, 0, 1);
        }
        
        @Override
        public void write(final byte[] array, final int n, final int n2) throws IOException {
            if (array == null) {
                throw new NullPointerException("the source buffer is null");
            }
            if (n < 0 || n2 < 0 || n2 > array.length - n) {
                throw new IndexOutOfBoundsException("buffer length: " + array.length + ", offset; " + n + ", bytes to read:" + n2);
            }
            if (n2 == 0) {
                return;
            }
            if (!SSLSocketImpl.this.conContext.isNegotiated && !SSLSocketImpl.this.conContext.isBroken && !SSLSocketImpl.this.conContext.isInboundClosed() && !SSLSocketImpl.this.conContext.isOutboundClosed()) {
                SSLSocketImpl.this.ensureNegotiated(true);
            }
            if (!SSLSocketImpl.this.conContext.isNegotiated || SSLSocketImpl.this.conContext.isBroken || SSLSocketImpl.this.conContext.isOutboundClosed()) {
                throw new SocketException("Connection or outbound has closed");
            }
            try {
                SSLSocketImpl.this.conContext.outputRecord.deliver(array, n, n2);
            }
            catch (final SSLHandshakeException ex) {
                throw SSLSocketImpl.this.conContext.fatal(Alert.HANDSHAKE_FAILURE, ex);
            }
            catch (final SSLException ex2) {
                throw SSLSocketImpl.this.conContext.fatal(Alert.UNEXPECTED_MESSAGE, ex2);
            }
            if (SSLSocketImpl.this.conContext.outputRecord.seqNumIsHuge() || SSLSocketImpl.this.conContext.outputRecord.writeCipher.atKeyLimit()) {
                SSLSocketImpl.this.tryKeyUpdate();
            }
        }
        
        @Override
        public void close() throws IOException {
            if (SSLLogger.isOn && SSLLogger.isOn("ssl")) {
                SSLLogger.finest("Closing output stream", new Object[0]);
            }
            try {
                SSLSocketImpl.this.close();
            }
            catch (final IOException ex) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl")) {
                    SSLLogger.warning("output stream close failed", ex);
                }
            }
        }
    }
}
