package org.openjsse.sun.security.ssl;

import javax.net.ssl.SSLHandshakeException;
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
import java.util.concurrent.locks.ReentrantLock;

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
    private final ReentrantLock socketLock;
    private final ReentrantLock handshakeLock;
    private static final boolean trustNameService;
    
    SSLSocketImpl(final SSLContextImpl sslContext) {
        this.appInput = new AppInputStream();
        this.appOutput = new AppOutputStream();
        this.isConnected = false;
        this.tlsIsClosed = false;
        this.socketLock = new ReentrantLock();
        this.handshakeLock = new ReentrantLock();
        this.sslContext = sslContext;
        final HandshakeHash handshakeHash = new HandshakeHash();
        this.conContext = new TransportContext(sslContext, this, new SSLSocketInputRecord(handshakeHash), new SSLSocketOutputRecord(handshakeHash), true);
    }
    
    SSLSocketImpl(final SSLContextImpl sslContext, final SSLConfiguration sslConfig) {
        this.appInput = new AppInputStream();
        this.appOutput = new AppOutputStream();
        this.isConnected = false;
        this.tlsIsClosed = false;
        this.socketLock = new ReentrantLock();
        this.handshakeLock = new ReentrantLock();
        this.sslContext = sslContext;
        final HandshakeHash handshakeHash = new HandshakeHash();
        this.conContext = new TransportContext(sslContext, this, sslConfig, new SSLSocketInputRecord(handshakeHash), new SSLSocketOutputRecord(handshakeHash));
    }
    
    SSLSocketImpl(final SSLContextImpl sslContext, final String peerHost, final int peerPort) throws IOException, UnknownHostException {
        this.appInput = new AppInputStream();
        this.appOutput = new AppOutputStream();
        this.isConnected = false;
        this.tlsIsClosed = false;
        this.socketLock = new ReentrantLock();
        this.handshakeLock = new ReentrantLock();
        this.sslContext = sslContext;
        final HandshakeHash handshakeHash = new HandshakeHash();
        this.conContext = new TransportContext(sslContext, this, new SSLSocketInputRecord(handshakeHash), new SSLSocketOutputRecord(handshakeHash), true);
        this.peerHost = peerHost;
        final SocketAddress socketAddress = (peerHost != null) ? new InetSocketAddress(peerHost, peerPort) : new InetSocketAddress(InetAddress.getByName(null), peerPort);
        this.connect(socketAddress, 0);
    }
    
    SSLSocketImpl(final SSLContextImpl sslContext, final InetAddress address, final int peerPort) throws IOException {
        this.appInput = new AppInputStream();
        this.appOutput = new AppOutputStream();
        this.isConnected = false;
        this.tlsIsClosed = false;
        this.socketLock = new ReentrantLock();
        this.handshakeLock = new ReentrantLock();
        this.sslContext = sslContext;
        final HandshakeHash handshakeHash = new HandshakeHash();
        this.conContext = new TransportContext(sslContext, this, new SSLSocketInputRecord(handshakeHash), new SSLSocketOutputRecord(handshakeHash), true);
        final SocketAddress socketAddress = new InetSocketAddress(address, peerPort);
        this.connect(socketAddress, 0);
    }
    
    SSLSocketImpl(final SSLContextImpl sslContext, final String peerHost, final int peerPort, final InetAddress localAddr, final int localPort) throws IOException, UnknownHostException {
        this.appInput = new AppInputStream();
        this.appOutput = new AppOutputStream();
        this.isConnected = false;
        this.tlsIsClosed = false;
        this.socketLock = new ReentrantLock();
        this.handshakeLock = new ReentrantLock();
        this.sslContext = sslContext;
        final HandshakeHash handshakeHash = new HandshakeHash();
        this.conContext = new TransportContext(sslContext, this, new SSLSocketInputRecord(handshakeHash), new SSLSocketOutputRecord(handshakeHash), true);
        this.peerHost = peerHost;
        this.bind(new InetSocketAddress(localAddr, localPort));
        final SocketAddress socketAddress = (peerHost != null) ? new InetSocketAddress(peerHost, peerPort) : new InetSocketAddress(InetAddress.getByName(null), peerPort);
        this.connect(socketAddress, 0);
    }
    
    SSLSocketImpl(final SSLContextImpl sslContext, final InetAddress peerAddr, final int peerPort, final InetAddress localAddr, final int localPort) throws IOException {
        this.appInput = new AppInputStream();
        this.appOutput = new AppOutputStream();
        this.isConnected = false;
        this.tlsIsClosed = false;
        this.socketLock = new ReentrantLock();
        this.handshakeLock = new ReentrantLock();
        this.sslContext = sslContext;
        final HandshakeHash handshakeHash = new HandshakeHash();
        this.conContext = new TransportContext(sslContext, this, new SSLSocketInputRecord(handshakeHash), new SSLSocketOutputRecord(handshakeHash), true);
        this.bind(new InetSocketAddress(localAddr, localPort));
        final SocketAddress socketAddress = new InetSocketAddress(peerAddr, peerPort);
        this.connect(socketAddress, 0);
    }
    
    SSLSocketImpl(final SSLContextImpl sslContext, final Socket sock, final InputStream consumed, final boolean autoClose) throws IOException {
        super(sock, consumed);
        this.appInput = new AppInputStream();
        this.appOutput = new AppOutputStream();
        this.isConnected = false;
        this.tlsIsClosed = false;
        this.socketLock = new ReentrantLock();
        this.handshakeLock = new ReentrantLock();
        if (!sock.isConnected()) {
            throw new SocketException("Underlying socket is not connected");
        }
        this.sslContext = sslContext;
        final HandshakeHash handshakeHash = new HandshakeHash();
        this.conContext = new TransportContext(sslContext, this, new SSLSocketInputRecord(handshakeHash), new SSLSocketOutputRecord(handshakeHash), false);
        this.autoClose = autoClose;
        this.doneConnect();
    }
    
    SSLSocketImpl(final SSLContextImpl sslContext, final Socket sock, final String peerHost, final int port, final boolean autoClose) throws IOException {
        super(sock);
        this.appInput = new AppInputStream();
        this.appOutput = new AppOutputStream();
        this.isConnected = false;
        this.tlsIsClosed = false;
        this.socketLock = new ReentrantLock();
        this.handshakeLock = new ReentrantLock();
        if (!sock.isConnected()) {
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
    public void connect(final SocketAddress endpoint, final int timeout) throws IOException {
        if (this.isLayered()) {
            throw new SocketException("Already connected");
        }
        if (!(endpoint instanceof InetSocketAddress)) {
            throw new SocketException("Cannot handle non-Inet socket addresses.");
        }
        super.connect(endpoint, timeout);
        this.doneConnect();
    }
    
    @Override
    public String[] getSupportedCipherSuites() {
        return CipherSuite.namesOf(this.sslContext.getSupportedCipherSuites());
    }
    
    @Override
    public String[] getEnabledCipherSuites() {
        this.socketLock.lock();
        try {
            return CipherSuite.namesOf(this.conContext.sslConfig.enabledCipherSuites);
        }
        finally {
            this.socketLock.unlock();
        }
    }
    
    @Override
    public void setEnabledCipherSuites(final String[] suites) {
        this.socketLock.lock();
        try {
            this.conContext.sslConfig.enabledCipherSuites = CipherSuite.validValuesOf(suites);
        }
        finally {
            this.socketLock.unlock();
        }
    }
    
    @Override
    public String[] getSupportedProtocols() {
        return ProtocolVersion.toStringArray(this.sslContext.getSupportedProtocolVersions());
    }
    
    @Override
    public String[] getEnabledProtocols() {
        this.socketLock.lock();
        try {
            return ProtocolVersion.toStringArray(this.conContext.sslConfig.enabledProtocols);
        }
        finally {
            this.socketLock.unlock();
        }
    }
    
    @Override
    public void setEnabledProtocols(final String[] protocols) {
        if (protocols == null) {
            throw new IllegalArgumentException("Protocols cannot be null");
        }
        this.socketLock.lock();
        try {
            this.conContext.sslConfig.enabledProtocols = ProtocolVersion.namesOf(protocols);
        }
        finally {
            this.socketLock.unlock();
        }
    }
    
    @Override
    public SSLSession getSession() {
        try {
            this.ensureNegotiated(false);
        }
        catch (final IOException ioe) {
            if (SSLLogger.isOn && SSLLogger.isOn("handshake")) {
                SSLLogger.severe("handshake failed", ioe);
            }
            return new SSLSessionImpl();
        }
        return this.conContext.conSession;
    }
    
    @Override
    public SSLSession getHandshakeSession() {
        this.socketLock.lock();
        try {
            return (this.conContext.handshakeContext == null) ? null : this.conContext.handshakeContext.handshakeSession;
        }
        finally {
            this.socketLock.unlock();
        }
    }
    
    @Override
    public void addHandshakeCompletedListener(final HandshakeCompletedListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("listener is null");
        }
        this.socketLock.lock();
        try {
            this.conContext.sslConfig.addHandshakeCompletedListener(listener);
        }
        finally {
            this.socketLock.unlock();
        }
    }
    
    @Override
    public void removeHandshakeCompletedListener(final HandshakeCompletedListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("listener is null");
        }
        this.socketLock.lock();
        try {
            this.conContext.sslConfig.removeHandshakeCompletedListener(listener);
        }
        finally {
            this.socketLock.unlock();
        }
    }
    
    @Override
    public void startHandshake() throws IOException {
        this.startHandshake(true);
    }
    
    private void startHandshake(final boolean resumable) throws IOException {
        if (!this.isConnected) {
            throw new SocketException("Socket is not connected");
        }
        if (this.conContext.isBroken || this.conContext.isInboundClosed() || this.conContext.isOutboundClosed()) {
            throw new SocketException("Socket has been closed or broken");
        }
        this.handshakeLock.lock();
        try {
            if (this.conContext.isBroken || this.conContext.isInboundClosed() || this.conContext.isOutboundClosed()) {
                throw new SocketException("Socket has been closed or broken");
            }
            try {
                this.conContext.kickstart();
                if (!this.conContext.isNegotiated) {
                    this.readHandshakeRecord();
                }
            }
            catch (final InterruptedIOException iioe) {
                if (!resumable) {
                    throw this.conContext.fatal(Alert.HANDSHAKE_FAILURE, "Couldn't kickstart handshaking", iioe);
                }
                this.handleException(iioe);
            }
            catch (final IOException ioe) {
                throw this.conContext.fatal(Alert.HANDSHAKE_FAILURE, "Couldn't kickstart handshaking", ioe);
            }
            catch (final Exception oe) {
                this.handleException(oe);
            }
        }
        finally {
            this.handshakeLock.unlock();
        }
    }
    
    @Override
    public void setUseClientMode(final boolean mode) {
        this.socketLock.lock();
        try {
            this.conContext.setUseClientMode(mode);
        }
        finally {
            this.socketLock.unlock();
        }
    }
    
    @Override
    public boolean getUseClientMode() {
        this.socketLock.lock();
        try {
            return this.conContext.sslConfig.isClientMode;
        }
        finally {
            this.socketLock.unlock();
        }
    }
    
    @Override
    public void setNeedClientAuth(final boolean need) {
        this.socketLock.lock();
        try {
            this.conContext.sslConfig.clientAuthType = (need ? ClientAuthType.CLIENT_AUTH_REQUIRED : ClientAuthType.CLIENT_AUTH_NONE);
        }
        finally {
            this.socketLock.unlock();
        }
    }
    
    @Override
    public boolean getNeedClientAuth() {
        this.socketLock.lock();
        try {
            return this.conContext.sslConfig.clientAuthType == ClientAuthType.CLIENT_AUTH_REQUIRED;
        }
        finally {
            this.socketLock.unlock();
        }
    }
    
    @Override
    public void setWantClientAuth(final boolean want) {
        this.socketLock.lock();
        try {
            this.conContext.sslConfig.clientAuthType = (want ? ClientAuthType.CLIENT_AUTH_REQUESTED : ClientAuthType.CLIENT_AUTH_NONE);
        }
        finally {
            this.socketLock.unlock();
        }
    }
    
    @Override
    public boolean getWantClientAuth() {
        this.socketLock.lock();
        try {
            return this.conContext.sslConfig.clientAuthType == ClientAuthType.CLIENT_AUTH_REQUESTED;
        }
        finally {
            this.socketLock.unlock();
        }
    }
    
    @Override
    public void setEnableSessionCreation(final boolean flag) {
        this.socketLock.lock();
        try {
            this.conContext.sslConfig.enableSessionCreation = flag;
        }
        finally {
            this.socketLock.unlock();
        }
    }
    
    @Override
    public boolean getEnableSessionCreation() {
        this.socketLock.lock();
        try {
            return this.conContext.sslConfig.enableSessionCreation;
        }
        finally {
            this.socketLock.unlock();
        }
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
        catch (final IOException ioe) {
            if (SSLLogger.isOn && SSLLogger.isOn("ssl")) {
                SSLLogger.warning("SSLSocket duplex close failed", ioe);
            }
        }
        finally {
            this.tlsIsClosed = true;
        }
    }
    
    private void duplexCloseOutput() throws IOException {
        boolean useUserCanceled = false;
        boolean hasCloseReceipt = false;
        if (this.conContext.isNegotiated) {
            if (!this.conContext.protocolVersion.useTLS13PlusSpec()) {
                hasCloseReceipt = true;
            }
            else {
                useUserCanceled = true;
            }
        }
        else if (this.conContext.handshakeContext != null) {
            useUserCanceled = true;
            final ProtocolVersion pv = this.conContext.handshakeContext.negotiatedProtocol;
            if (pv == null || !pv.useTLS13PlusSpec()) {
                hasCloseReceipt = true;
            }
        }
        this.closeNotify(useUserCanceled);
        if (!this.isInputShutdown()) {
            this.bruteForceCloseInput(hasCloseReceipt);
        }
    }
    
    void closeNotify(final boolean useUserCanceled) throws IOException {
        try {
            synchronized (this.conContext.outputRecord) {
                if (useUserCanceled) {
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
        boolean hasCloseReceipt = false;
        if (this.conContext.isNegotiated && !this.conContext.protocolVersion.useTLS13PlusSpec()) {
            hasCloseReceipt = true;
        }
        this.bruteForceCloseInput(hasCloseReceipt);
    }
    
    private void bruteForceCloseInput(final boolean hasCloseReceipt) throws IOException {
        if (hasCloseReceipt) {
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
    
    private void shutdownInput(final boolean checkCloseNotify) throws IOException {
        if (this.isInputShutdown()) {
            return;
        }
        if (SSLLogger.isOn && SSLLogger.isOn("ssl")) {
            SSLLogger.fine("close inbound of SSLSocket", new Object[0]);
        }
        if (checkCloseNotify && !this.conContext.isInputCloseNotified && (this.conContext.isNegotiated || this.conContext.handshakeContext != null)) {
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
    public InputStream getInputStream() throws IOException {
        this.socketLock.lock();
        try {
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
        finally {
            this.socketLock.unlock();
        }
    }
    
    private void ensureNegotiated(final boolean resumable) throws IOException {
        if (this.conContext.isNegotiated || this.conContext.isBroken || this.conContext.isInboundClosed() || this.conContext.isOutboundClosed()) {
            return;
        }
        this.handshakeLock.lock();
        try {
            if (this.conContext.isNegotiated || this.conContext.isBroken || this.conContext.isInboundClosed() || this.conContext.isOutboundClosed()) {
                return;
            }
            this.startHandshake(resumable);
        }
        finally {
            this.handshakeLock.unlock();
        }
    }
    
    @Override
    public OutputStream getOutputStream() throws IOException {
        this.socketLock.lock();
        try {
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
        finally {
            this.socketLock.unlock();
        }
    }
    
    @Override
    public SSLParameters getSSLParameters() {
        this.socketLock.lock();
        try {
            return this.conContext.sslConfig.getSSLParameters();
        }
        finally {
            this.socketLock.unlock();
        }
    }
    
    @Override
    public void setSSLParameters(final SSLParameters params) {
        this.socketLock.lock();
        try {
            this.conContext.sslConfig.setSSLParameters(params);
            if (this.conContext.sslConfig.maximumPacketSize != 0) {
                this.conContext.outputRecord.changePacketSize(this.conContext.sslConfig.maximumPacketSize);
            }
        }
        finally {
            this.socketLock.unlock();
        }
    }
    
    public String getApplicationProtocol() {
        this.socketLock.lock();
        try {
            return this.conContext.applicationProtocol;
        }
        finally {
            this.socketLock.unlock();
        }
    }
    
    public String getHandshakeApplicationProtocol() {
        this.socketLock.lock();
        try {
            if (this.conContext.handshakeContext != null) {
                return this.conContext.handshakeContext.applicationProtocol;
            }
        }
        finally {
            this.socketLock.unlock();
        }
        return null;
    }
    
    public void setHandshakeApplicationProtocolSelector(final BiFunction<javax.net.ssl.SSLSocket, List<String>, String> selector) {
        this.socketLock.lock();
        try {
            this.conContext.sslConfig.socketAPSelector = selector;
        }
        finally {
            this.socketLock.unlock();
        }
    }
    
    public BiFunction<javax.net.ssl.SSLSocket, List<String>, String> getHandshakeApplicationProtocolSelector() {
        this.socketLock.lock();
        try {
            return this.conContext.sslConfig.socketAPSelector;
        }
        finally {
            this.socketLock.unlock();
        }
    }
    
    private int readHandshakeRecord() throws IOException {
        while (!this.conContext.isInboundClosed()) {
            try {
                final Plaintext plainText = this.decode(null);
                if (plainText.contentType == ContentType.HANDSHAKE.id && this.conContext.isNegotiated) {
                    return 0;
                }
                continue;
            }
            catch (final SSLException ssle) {
                throw ssle;
            }
            catch (final InterruptedIOException iioe) {
                throw iioe;
            }
            catch (final IOException ioe) {
                throw new SSLException("readHandshakeRecord", ioe);
            }
            break;
        }
        return -1;
    }
    
    private ByteBuffer readApplicationRecord(ByteBuffer buffer) throws IOException {
        while (!this.conContext.isInboundClosed()) {
            buffer.clear();
            final int inLen = this.conContext.inputRecord.bytesInCompletePacket();
            if (inLen < 0) {
                this.handleEOF(null);
                return null;
            }
            if (inLen > 33093) {
                throw new SSLProtocolException("Illegal packet size: " + inLen);
            }
            if (inLen > buffer.remaining()) {
                buffer = ByteBuffer.allocate(inLen);
            }
            try {
                this.socketLock.lock();
                Plaintext plainText;
                try {
                    plainText = this.decode(buffer);
                }
                finally {
                    this.socketLock.unlock();
                }
                if (plainText.contentType == ContentType.APPLICATION_DATA.id && buffer.position() > 0) {
                    return buffer;
                }
                continue;
            }
            catch (final SSLException ssle) {
                throw ssle;
            }
            catch (final InterruptedIOException iioe) {
                throw iioe;
            }
            catch (final IOException ioe) {
                if (!(ioe instanceof SSLException)) {
                    throw new SSLException("readApplicationRecord", ioe);
                }
                throw ioe;
            }
        }
        return null;
    }
    
    private Plaintext decode(final ByteBuffer destination) throws IOException {
        Plaintext plainText;
        try {
            if (destination == null) {
                plainText = SSLTransport.decode(this.conContext, null, 0, 0, null, 0, 0);
            }
            else {
                plainText = SSLTransport.decode(this.conContext, null, 0, 0, new ByteBuffer[] { destination }, 0, 1);
            }
        }
        catch (final EOFException eofe) {
            plainText = this.handleEOF(eofe);
        }
        if (plainText != Plaintext.PLAINTEXT_NULL && (this.conContext.inputRecord.seqNumIsHuge() || this.conContext.inputRecord.readCipher.atKeyLimit())) {
            this.tryKeyUpdate();
        }
        return plainText;
    }
    
    private void tryKeyUpdate() throws IOException {
        if (this.conContext.handshakeContext == null && !this.conContext.isOutboundClosed() && !this.conContext.isInboundClosed() && !this.conContext.isBroken) {
            if (SSLLogger.isOn && SSLLogger.isOn("ssl")) {
                SSLLogger.finest("trigger key update", new Object[0]);
            }
            this.startHandshake();
        }
    }
    
    void doneConnect() throws IOException {
        this.socketLock.lock();
        try {
            if (this.peerHost == null || this.peerHost.isEmpty()) {
                final boolean useNameService = SSLSocketImpl.trustNameService && this.conContext.sslConfig.isClientMode;
                this.useImplicitHost(useNameService);
            }
            else {
                this.conContext.sslConfig.serverNames = Utilities.addToSNIServerNameList(this.conContext.sslConfig.serverNames, this.peerHost);
            }
            final InputStream sockInput = super.getInputStream();
            this.conContext.inputRecord.setReceiverStream(sockInput);
            final OutputStream sockOutput = super.getOutputStream();
            this.conContext.inputRecord.setDeliverStream(sockOutput);
            this.conContext.outputRecord.setDeliverStream(sockOutput);
            this.isConnected = true;
        }
        finally {
            this.socketLock.unlock();
        }
    }
    
    private void useImplicitHost(final boolean useNameService) {
        final InetAddress inetAddress = this.getInetAddress();
        if (inetAddress == null) {
            return;
        }
        final String originalHostname = HostNameAccessor.getOriginalHostName(inetAddress);
        if (originalHostname != null && originalHostname.length() != 0) {
            this.peerHost = originalHostname;
            if (this.conContext.sslConfig.serverNames.isEmpty() && !this.conContext.sslConfig.noSniExtension) {
                this.conContext.sslConfig.serverNames = Utilities.addToSNIServerNameList(this.conContext.sslConfig.serverNames, this.peerHost);
            }
            return;
        }
        if (!useNameService) {
            this.peerHost = inetAddress.getHostAddress();
        }
        else {
            this.peerHost = this.getInetAddress().getHostName();
        }
    }
    
    public void setHost(final String host) {
        this.socketLock.lock();
        try {
            this.peerHost = host;
            this.conContext.sslConfig.serverNames = Utilities.addToSNIServerNameList(this.conContext.sslConfig.serverNames, host);
        }
        finally {
            this.socketLock.unlock();
        }
    }
    
    private void handleException(final Exception cause) throws IOException {
        if (SSLLogger.isOn && SSLLogger.isOn("ssl")) {
            SSLLogger.warning("handling exception", cause);
        }
        if (cause instanceof InterruptedIOException) {
            throw (IOException)cause;
        }
        final boolean isSSLException = cause instanceof SSLException;
        Alert alert;
        if (isSSLException) {
            if (cause instanceof SSLHandshakeException) {
                alert = Alert.HANDSHAKE_FAILURE;
            }
            else {
                alert = Alert.UNEXPECTED_MESSAGE;
            }
        }
        else if (cause instanceof IOException) {
            alert = Alert.UNEXPECTED_MESSAGE;
        }
        else {
            alert = Alert.INTERNAL_ERROR;
        }
        throw this.conContext.fatal(alert, cause);
    }
    
    private Plaintext handleEOF(final EOFException eofe) throws IOException {
        if (SSLSocketImpl.requireCloseNotify || this.conContext.handshakeContext != null) {
            SSLException ssle;
            if (this.conContext.handshakeContext != null) {
                ssle = new SSLHandshakeException("Remote host terminated the handshake");
            }
            else {
                ssle = new SSLProtocolException("Remote host terminated the connection");
            }
            if (eofe != null) {
                ssle.initCause(eofe);
            }
            throw ssle;
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
    
    private void closeSocket(final boolean selfInitiated) throws IOException {
        if (SSLLogger.isOn && SSLLogger.isOn("ssl")) {
            SSLLogger.fine("close the SSL connection " + (selfInitiated ? "(initiative)" : "(passive)"), new Object[0]);
        }
        if (this.autoClose || !this.isLayered()) {
            super.close();
        }
        else if (selfInitiated && !this.conContext.isInboundClosed() && !this.isInputShutdown()) {
            this.waitForClose();
        }
    }
    
    private void waitForClose() throws IOException {
        if (SSLLogger.isOn && SSLLogger.isOn("ssl")) {
            SSLLogger.fine("wait for close_notify or alert", new Object[0]);
        }
        while (!this.conContext.isInboundClosed()) {
            try {
                final Plaintext plainText = this.decode(null);
                if (!SSLLogger.isOn || !SSLLogger.isOn("ssl")) {
                    continue;
                }
                SSLLogger.finest("discard plaintext while waiting for close", plainText);
            }
            catch (final Exception e) {
                this.handleException(e);
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
        private final ReentrantLock readLock;
        private volatile boolean isClosing;
        private volatile boolean hasDepleted;
        
        AppInputStream() {
            this.oneByte = new byte[1];
            this.readLock = new ReentrantLock();
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
            final int n = this.read(this.oneByte, 0, 1);
            if (n <= 0) {
                return -1;
            }
            return this.oneByte[0] & 0xFF;
        }
        
        @Override
        public int read(final byte[] b, final int off, final int len) throws IOException {
            if (b == null) {
                throw new NullPointerException("the target buffer is null");
            }
            if (off < 0 || len < 0 || len > b.length - off) {
                throw new IndexOutOfBoundsException("buffer length: " + b.length + ", offset; " + off + ", bytes to read:" + len);
            }
            if (len == 0) {
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
            if (this.hasDepleted) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl")) {
                    SSLLogger.fine("The input stream has been depleted", new Object[0]);
                }
                return -1;
            }
            this.readLock.lock();
            try {
                if (SSLSocketImpl.this.conContext.isBroken || SSLSocketImpl.this.conContext.isInboundClosed()) {
                    throw new SocketException("Connection or inbound has closed");
                }
                if (this.hasDepleted) {
                    if (SSLLogger.isOn && SSLLogger.isOn("ssl")) {
                        SSLLogger.fine("The input stream is closing", new Object[0]);
                    }
                    return -1;
                }
                final int remains = this.available();
                if (remains > 0) {
                    final int howmany = Math.min(remains, len);
                    this.buffer.get(b, off, howmany);
                    return howmany;
                }
                this.appDataIsAvailable = false;
                try {
                    final ByteBuffer bb = SSLSocketImpl.this.readApplicationRecord(this.buffer);
                    if (bb == null) {
                        return -1;
                    }
                    (this.buffer = bb).flip();
                    final int volume = Math.min(len, bb.remaining());
                    this.buffer.get(b, off, volume);
                    this.appDataIsAvailable = true;
                    return volume;
                }
                catch (final Exception e) {
                    SSLSocketImpl.this.handleException(e);
                    return -1;
                }
            }
            finally {
                try {
                    if (this.isClosing) {
                        this.readLockedDeplete();
                    }
                }
                finally {
                    this.readLock.unlock();
                }
            }
        }
        
        @Override
        public long skip(long n) throws IOException {
            final byte[] skipArray = new byte[256];
            long skipped = 0L;
            this.readLock.lock();
            try {
                while (n > 0L) {
                    final int len = (int)Math.min(n, skipArray.length);
                    final int r = this.read(skipArray, 0, len);
                    if (r <= 0) {
                        break;
                    }
                    n -= r;
                    skipped += r;
                }
            }
            finally {
                this.readLock.unlock();
            }
            return skipped;
        }
        
        @Override
        public void close() throws IOException {
            if (SSLLogger.isOn && SSLLogger.isOn("ssl")) {
                SSLLogger.finest("Closing input stream", new Object[0]);
            }
            try {
                SSLSocketImpl.this.close();
            }
            catch (final IOException ioe) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl")) {
                    SSLLogger.warning("input stream close failed", ioe);
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
        
        private void deplete() {
            if (SSLSocketImpl.this.conContext.isInboundClosed() || this.isClosing) {
                return;
            }
            this.isClosing = true;
            if (this.readLock.tryLock()) {
                try {
                    this.readLockedDeplete();
                }
                finally {
                    this.readLock.unlock();
                }
            }
        }
        
        private void readLockedDeplete() {
            if (this.hasDepleted || SSLSocketImpl.this.conContext.isInboundClosed()) {
                return;
            }
            if (!(SSLSocketImpl.this.conContext.inputRecord instanceof SSLSocketInputRecord)) {
                return;
            }
            final SSLSocketInputRecord socketInputRecord = (SSLSocketInputRecord)SSLSocketImpl.this.conContext.inputRecord;
            try {
                socketInputRecord.deplete(SSLSocketImpl.this.conContext.isNegotiated && SSLSocketImpl.this.getSoTimeout() > 0);
            }
            catch (final Exception ex) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl")) {
                    SSLLogger.warning("input stream close depletion failed", ex);
                }
            }
            finally {
                this.hasDepleted = true;
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
        public void write(final int i) throws IOException {
            this.oneByte[0] = (byte)i;
            this.write(this.oneByte, 0, 1);
        }
        
        @Override
        public void write(final byte[] b, final int off, final int len) throws IOException {
            if (b == null) {
                throw new NullPointerException("the source buffer is null");
            }
            if (off < 0 || len < 0 || len > b.length - off) {
                throw new IndexOutOfBoundsException("buffer length: " + b.length + ", offset; " + off + ", bytes to read:" + len);
            }
            if (len == 0) {
                return;
            }
            if (!SSLSocketImpl.this.conContext.isNegotiated && !SSLSocketImpl.this.conContext.isBroken && !SSLSocketImpl.this.conContext.isInboundClosed() && !SSLSocketImpl.this.conContext.isOutboundClosed()) {
                SSLSocketImpl.this.ensureNegotiated(true);
            }
            if (!SSLSocketImpl.this.conContext.isNegotiated || SSLSocketImpl.this.conContext.isBroken || SSLSocketImpl.this.conContext.isOutboundClosed()) {
                throw new SocketException("Connection or outbound has closed");
            }
            try {
                SSLSocketImpl.this.conContext.outputRecord.deliver(b, off, len);
            }
            catch (final SSLHandshakeException she) {
                throw SSLSocketImpl.this.conContext.fatal(Alert.HANDSHAKE_FAILURE, she);
            }
            catch (final SSLException ssle) {
                throw SSLSocketImpl.this.conContext.fatal(Alert.UNEXPECTED_MESSAGE, ssle);
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
            catch (final IOException ioe) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl")) {
                    SSLLogger.warning("output stream close failed", ioe);
                }
            }
        }
    }
}
