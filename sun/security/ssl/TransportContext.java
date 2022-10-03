package sun.security.ssl;

import java.util.Iterator;
import java.security.PrivilegedAction;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import javax.net.ssl.HandshakeCompletedListener;
import javax.net.ssl.SSLSession;
import javax.net.ssl.HandshakeCompletedEvent;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLEngineResult;
import javax.net.ssl.SSLException;
import java.io.IOException;
import java.util.HashMap;
import java.security.AccessController;
import java.util.List;
import java.security.AccessControlContext;
import java.util.Map;

class TransportContext implements ConnectionContext
{
    final SSLTransport transport;
    final Map<Byte, SSLConsumer> consumers;
    final AccessControlContext acc;
    final SSLContextImpl sslContext;
    final SSLConfiguration sslConfig;
    final InputRecord inputRecord;
    final OutputRecord outputRecord;
    boolean isUnsureMode;
    boolean isNegotiated;
    boolean isBroken;
    boolean isInputCloseNotified;
    boolean peerUserCanceled;
    Exception closeReason;
    Exception delegatedThrown;
    SSLSessionImpl conSession;
    ProtocolVersion protocolVersion;
    String applicationProtocol;
    HandshakeContext handshakeContext;
    boolean secureRenegotiation;
    byte[] clientVerifyData;
    byte[] serverVerifyData;
    List<SupportedGroupsExtension.NamedGroup> serverRequestedNamedGroups;
    CipherSuite cipherSuite;
    private static final byte[] emptyByteArray;
    
    TransportContext(final SSLContextImpl sslContextImpl, final SSLTransport sslTransport, final InputRecord inputRecord, final OutputRecord outputRecord) {
        this(sslContextImpl, sslTransport, new SSLConfiguration(sslContextImpl, false), inputRecord, outputRecord, true);
    }
    
    TransportContext(final SSLContextImpl sslContextImpl, final SSLTransport sslTransport, final InputRecord inputRecord, final OutputRecord outputRecord, final boolean b) {
        this(sslContextImpl, sslTransport, new SSLConfiguration(sslContextImpl, b), inputRecord, outputRecord, false);
    }
    
    TransportContext(final SSLContextImpl sslContextImpl, final SSLTransport sslTransport, final SSLConfiguration sslConfiguration, final InputRecord inputRecord, final OutputRecord outputRecord) {
        this(sslContextImpl, sslTransport, (SSLConfiguration)sslConfiguration.clone(), inputRecord, outputRecord, false);
    }
    
    private TransportContext(final SSLContextImpl sslContext, final SSLTransport transport, final SSLConfiguration sslConfig, final InputRecord inputRecord, final OutputRecord outputRecord, final boolean isUnsureMode) {
        this.isNegotiated = false;
        this.isBroken = false;
        this.isInputCloseNotified = false;
        this.peerUserCanceled = false;
        this.closeReason = null;
        this.delegatedThrown = null;
        this.applicationProtocol = null;
        this.handshakeContext = null;
        this.secureRenegotiation = false;
        this.transport = transport;
        this.sslContext = sslContext;
        this.inputRecord = inputRecord;
        this.outputRecord = outputRecord;
        this.sslConfig = sslConfig;
        if (this.sslConfig.maximumPacketSize == 0) {
            this.sslConfig.maximumPacketSize = outputRecord.getMaxPacketSize();
        }
        this.isUnsureMode = isUnsureMode;
        this.conSession = new SSLSessionImpl();
        this.protocolVersion = this.sslConfig.maximumProtocolVersion;
        this.clientVerifyData = TransportContext.emptyByteArray;
        this.serverVerifyData = TransportContext.emptyByteArray;
        this.acc = AccessController.getContext();
        this.consumers = new HashMap<Byte, SSLConsumer>();
    }
    
    void dispatch(final Plaintext plaintext) throws IOException {
        if (plaintext == null) {
            return;
        }
        final ContentType value = ContentType.valueOf(plaintext.contentType);
        if (value == null) {
            throw this.fatal(Alert.UNEXPECTED_MESSAGE, "Unknown content type: " + plaintext.contentType);
        }
        switch (value) {
            case HANDSHAKE: {
                final byte handshakeType = HandshakeContext.getHandshakeType(this, plaintext);
                if (this.handshakeContext == null) {
                    if (handshakeType == SSLHandshake.KEY_UPDATE.id || handshakeType == SSLHandshake.NEW_SESSION_TICKET.id) {
                        if (!this.isNegotiated) {
                            throw this.fatal(Alert.UNEXPECTED_MESSAGE, "Unexpected unnegotiated post-handshake message: " + SSLHandshake.nameOf(handshakeType));
                        }
                        if (!PostHandshakeContext.isConsumable(this, handshakeType)) {
                            throw this.fatal(Alert.UNEXPECTED_MESSAGE, "Unexpected post-handshake message: " + SSLHandshake.nameOf(handshakeType));
                        }
                        this.handshakeContext = new PostHandshakeContext(this);
                    }
                    else {
                        this.handshakeContext = (this.sslConfig.isClientMode ? new ClientHandshakeContext(this.sslContext, this) : new ServerHandshakeContext(this.sslContext, this));
                    }
                }
                this.handshakeContext.dispatch(handshakeType, plaintext);
                break;
            }
            case ALERT: {
                Alert.alertConsumer.consume(this, plaintext.fragment);
                break;
            }
            default: {
                final SSLConsumer sslConsumer = this.consumers.get(plaintext.contentType);
                if (sslConsumer != null) {
                    sslConsumer.consume(this, plaintext.fragment);
                    break;
                }
                throw this.fatal(Alert.UNEXPECTED_MESSAGE, "Unexpected content: " + plaintext.contentType);
            }
        }
    }
    
    void kickstart() throws IOException {
        if (this.isUnsureMode) {
            throw new IllegalStateException("Client/Server mode not yet set.");
        }
        if (!this.outputRecord.isClosed() && !this.inputRecord.isClosed() && !this.isBroken) {
            if (this.handshakeContext == null) {
                if (this.isNegotiated && this.protocolVersion.useTLS13PlusSpec()) {
                    this.handshakeContext = new PostHandshakeContext(this);
                }
                else {
                    this.handshakeContext = (this.sslConfig.isClientMode ? new ClientHandshakeContext(this.sslContext, this) : new ServerHandshakeContext(this.sslContext, this));
                }
            }
            if (this.isNegotiated || this.sslConfig.isClientMode) {
                this.handshakeContext.kickstart();
            }
            return;
        }
        if (this.closeReason != null) {
            throw new SSLException("Cannot kickstart, the connection is broken or closed", this.closeReason);
        }
        throw new SSLException("Cannot kickstart, the connection is broken or closed");
    }
    
    boolean isPostHandshakeContext() {
        return this.handshakeContext != null && this.handshakeContext instanceof PostHandshakeContext;
    }
    
    void warning(final Alert alert) {
        if (!this.isNegotiated) {
            if (this.handshakeContext == null) {
                return;
            }
        }
        try {
            this.outputRecord.encodeAlert(Alert.Level.WARNING.level, alert.id);
        }
        catch (final IOException ex) {
            if (SSLLogger.isOn && SSLLogger.isOn("ssl")) {
                SSLLogger.warning("Warning: failed to send warning alert " + alert, ex);
            }
        }
    }
    
    void closeNotify(final boolean b) throws IOException {
        if (this.transport instanceof SSLSocketImpl) {
            ((SSLSocketImpl)this.transport).closeNotify(b);
        }
        else {
            synchronized (this.outputRecord) {
                try {
                    if (b) {
                        this.warning(Alert.USER_CANCELED);
                    }
                    this.warning(Alert.CLOSE_NOTIFY);
                }
                finally {
                    this.outputRecord.close();
                }
            }
        }
    }
    
    SSLException fatal(final Alert alert, final String s) throws SSLException {
        return this.fatal(alert, s, null);
    }
    
    SSLException fatal(final Alert alert, final Throwable t) throws SSLException {
        return this.fatal(alert, null, t);
    }
    
    SSLException fatal(final Alert alert, final String s, final Throwable t) throws SSLException {
        return this.fatal(alert, s, false, t);
    }
    
    SSLException fatal(final Alert alert, String message, final boolean b, Throwable sslException) throws SSLException {
        if (this.closeReason != null) {
            if (sslException == null) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl")) {
                    SSLLogger.warning("Closed transport, general or untracked problem", new Object[0]);
                }
                throw alert.createSSLException("Closed transport, general or untracked problem");
            }
            if (sslException instanceof SSLException) {
                throw (SSLException)sslException;
            }
            if (SSLLogger.isOn && SSLLogger.isOn("ssl")) {
                SSLLogger.warning("Closed transport, unexpected rethrowing", sslException);
            }
            throw alert.createSSLException("Unexpected rethrowing", sslException);
        }
        else {
            if (message == null) {
                if (sslException == null) {
                    message = "General/Untracked problem";
                }
                else {
                    message = sslException.getMessage();
                }
            }
            if (sslException == null) {
                sslException = alert.createSSLException(message);
            }
            if (SSLLogger.isOn && SSLLogger.isOn("ssl")) {
                SSLLogger.severe("Fatal (" + alert + "): " + message, sslException);
            }
            if (sslException instanceof SSLException) {
                this.closeReason = (SSLException)sslException;
            }
            else {
                this.closeReason = alert.createSSLException(message, sslException);
            }
            try {
                this.inputRecord.close();
            }
            catch (final IOException ex) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl")) {
                    SSLLogger.warning("Fatal: input record closure failed", ex);
                }
                this.closeReason.addSuppressed(ex);
            }
            if (this.conSession != null) {
                this.conSession.invalidate();
            }
            if (this.handshakeContext != null && this.handshakeContext.handshakeSession != null) {
                this.handshakeContext.handshakeSession.invalidate();
            }
            Label_0409: {
                if (!b && !this.isOutboundClosed() && !this.isBroken) {
                    if (!this.isNegotiated) {
                        if (this.handshakeContext == null) {
                            break Label_0409;
                        }
                    }
                    try {
                        this.outputRecord.encodeAlert(Alert.Level.FATAL.level, alert.id);
                    }
                    catch (final IOException ex2) {
                        if (SSLLogger.isOn && SSLLogger.isOn("ssl")) {
                            SSLLogger.warning("Fatal: failed to send fatal alert " + alert, ex2);
                        }
                        this.closeReason.addSuppressed(ex2);
                    }
                }
                try {
                    this.outputRecord.close();
                }
                catch (final IOException ex3) {
                    if (SSLLogger.isOn && SSLLogger.isOn("ssl")) {
                        SSLLogger.warning("Fatal: output record closure failed", ex3);
                    }
                    this.closeReason.addSuppressed(ex3);
                }
            }
            if (this.handshakeContext != null) {
                this.handshakeContext = null;
            }
            try {
                this.transport.shutdown();
            }
            catch (final IOException ex4) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl")) {
                    SSLLogger.warning("Fatal: transport closure failed", ex4);
                }
                this.closeReason.addSuppressed(ex4);
            }
            finally {
                this.isBroken = true;
            }
            if (this.closeReason instanceof SSLException) {
                throw (SSLException)this.closeReason;
            }
            throw (RuntimeException)this.closeReason;
        }
    }
    
    void setUseClientMode(final boolean b) {
        if (this.handshakeContext != null || this.isNegotiated) {
            throw new IllegalArgumentException("Cannot change mode after SSL traffic has started");
        }
        if (this.sslConfig.isClientMode != b) {
            if (this.sslContext.isDefaultProtocolVesions(this.sslConfig.enabledProtocols)) {
                this.sslConfig.enabledProtocols = this.sslContext.getDefaultProtocolVersions(!b);
            }
            if (this.sslContext.isDefaultCipherSuiteList(this.sslConfig.enabledCipherSuites)) {
                this.sslConfig.enabledCipherSuites = this.sslContext.getDefaultCipherSuites(!b);
            }
            this.sslConfig.toggleClientMode();
        }
        this.isUnsureMode = false;
    }
    
    boolean isOutboundDone() {
        return this.outputRecord.isClosed() && this.outputRecord.isEmpty();
    }
    
    boolean isOutboundClosed() {
        return this.outputRecord.isClosed();
    }
    
    boolean isInboundClosed() {
        return this.inputRecord.isClosed();
    }
    
    void closeInbound() throws SSLException {
        if (this.isInboundClosed()) {
            return;
        }
        try {
            if (!this.isInputCloseNotified) {
                this.initiateInboundClose();
            }
            else {
                this.passiveInboundClose();
            }
        }
        catch (final IOException ex) {
            if (SSLLogger.isOn && SSLLogger.isOn("ssl")) {
                SSLLogger.warning("inbound closure failed", ex);
            }
        }
    }
    
    private void passiveInboundClose() throws IOException {
        if (!this.isInboundClosed()) {
            this.inputRecord.close();
        }
        if (!this.isOutboundClosed()) {
            int acknowledgeCloseNotify = SSLConfiguration.acknowledgeCloseNotify ? 1 : 0;
            if (acknowledgeCloseNotify == 0) {
                if (this.isNegotiated) {
                    if (!this.protocolVersion.useTLS13PlusSpec()) {
                        acknowledgeCloseNotify = 1;
                    }
                }
                else if (this.handshakeContext != null) {
                    final ProtocolVersion negotiatedProtocol = this.handshakeContext.negotiatedProtocol;
                    if (negotiatedProtocol == null || !negotiatedProtocol.useTLS13PlusSpec()) {
                        acknowledgeCloseNotify = 1;
                    }
                }
            }
            if (acknowledgeCloseNotify != 0) {
                this.closeNotify(false);
            }
        }
    }
    
    private void initiateInboundClose() throws IOException {
        if (!this.isInboundClosed()) {
            this.inputRecord.close();
        }
    }
    
    void closeOutbound() {
        if (this.isOutboundClosed()) {
            return;
        }
        try {
            this.initiateOutboundClose();
        }
        catch (final IOException ex) {
            if (SSLLogger.isOn && SSLLogger.isOn("ssl")) {
                SSLLogger.warning("outbound closure failed", ex);
            }
        }
    }
    
    private void initiateOutboundClose() throws IOException {
        boolean b = false;
        if (!this.isNegotiated && this.handshakeContext != null && !this.peerUserCanceled) {
            b = true;
        }
        this.closeNotify(b);
    }
    
    SSLEngineResult.HandshakeStatus getHandshakeStatus() {
        if (!this.outputRecord.isEmpty()) {
            return SSLEngineResult.HandshakeStatus.NEED_WRAP;
        }
        if (this.isOutboundClosed() && this.isInboundClosed()) {
            return SSLEngineResult.HandshakeStatus.NOT_HANDSHAKING;
        }
        if (this.handshakeContext != null) {
            if (!this.handshakeContext.delegatedActions.isEmpty()) {
                return SSLEngineResult.HandshakeStatus.NEED_TASK;
            }
            if (!this.isInboundClosed()) {
                return SSLEngineResult.HandshakeStatus.NEED_UNWRAP;
            }
            if (!this.isOutboundClosed()) {
                return SSLEngineResult.HandshakeStatus.NEED_WRAP;
            }
        }
        else {
            if (this.isOutboundClosed() && !this.isInboundClosed()) {
                return SSLEngineResult.HandshakeStatus.NEED_UNWRAP;
            }
            if (!this.isOutboundClosed() && this.isInboundClosed()) {
                return SSLEngineResult.HandshakeStatus.NEED_WRAP;
            }
        }
        return SSLEngineResult.HandshakeStatus.NOT_HANDSHAKING;
    }
    
    SSLEngineResult.HandshakeStatus finishHandshake() {
        if (this.protocolVersion.useTLS13PlusSpec()) {
            this.outputRecord.tc = this;
            this.inputRecord.tc = this;
            this.cipherSuite = this.handshakeContext.negotiatedCipherSuite;
            this.inputRecord.readCipher.baseSecret = this.handshakeContext.baseReadSecret;
            this.outputRecord.writeCipher.baseSecret = this.handshakeContext.baseWriteSecret;
        }
        this.handshakeContext = null;
        this.outputRecord.handshakeHash.finish();
        this.isNegotiated = true;
        if (this.transport instanceof SSLSocket && this.sslConfig.handshakeListeners != null && !this.sslConfig.handshakeListeners.isEmpty()) {
            new Thread(null, new NotifyHandshake(this.sslConfig.handshakeListeners, new HandshakeCompletedEvent((SSLSocket)this.transport, this.conSession)), "HandshakeCompletedNotify-Thread", 0L).start();
        }
        return SSLEngineResult.HandshakeStatus.FINISHED;
    }
    
    SSLEngineResult.HandshakeStatus finishPostHandshake() {
        this.handshakeContext = null;
        return SSLEngineResult.HandshakeStatus.FINISHED;
    }
    
    static {
        emptyByteArray = new byte[0];
    }
    
    private static class NotifyHandshake implements Runnable
    {
        private final Set<Map.Entry<HandshakeCompletedListener, AccessControlContext>> targets;
        private final HandshakeCompletedEvent event;
        
        NotifyHandshake(final Map<HandshakeCompletedListener, AccessControlContext> map, final HandshakeCompletedEvent event) {
            this.targets = new HashSet<Map.Entry<HandshakeCompletedListener, AccessControlContext>>(map.entrySet());
            this.event = event;
        }
        
        @Override
        public void run() {
            for (final Map.Entry entry : this.targets) {
                AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Void>() {
                    final /* synthetic */ HandshakeCompletedListener val$listener = (HandshakeCompletedListener)entry.getKey();
                    
                    @Override
                    public Void run() {
                        this.val$listener.handshakeCompleted(NotifyHandshake.this.event);
                        return null;
                    }
                }, (AccessControlContext)entry.getValue());
            }
        }
    }
}
