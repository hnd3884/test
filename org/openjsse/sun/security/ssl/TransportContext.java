package org.openjsse.sun.security.ssl;

import java.util.Iterator;
import java.security.PrivilegedAction;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import javax.net.ssl.HandshakeCompletedListener;
import javax.net.ssl.SSLSession;
import javax.net.ssl.HandshakeCompletedEvent;
import org.openjsse.javax.net.ssl.SSLSocket;
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
    
    TransportContext(final SSLContextImpl sslContext, final SSLTransport transport, final InputRecord inputRecord, final OutputRecord outputRecord) {
        this(sslContext, transport, new SSLConfiguration(sslContext, false), inputRecord, outputRecord, true);
    }
    
    TransportContext(final SSLContextImpl sslContext, final SSLTransport transport, final InputRecord inputRecord, final OutputRecord outputRecord, final boolean isClientMode) {
        this(sslContext, transport, new SSLConfiguration(sslContext, isClientMode), inputRecord, outputRecord, false);
    }
    
    TransportContext(final SSLContextImpl sslContext, final SSLTransport transport, final SSLConfiguration sslConfig, final InputRecord inputRecord, final OutputRecord outputRecord) {
        this(sslContext, transport, (SSLConfiguration)sslConfig.clone(), inputRecord, outputRecord, false);
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
        final ContentType ct = ContentType.valueOf(plaintext.contentType);
        if (ct == null) {
            throw this.fatal(Alert.UNEXPECTED_MESSAGE, "Unknown content type: " + plaintext.contentType);
        }
        switch (ct) {
            case HANDSHAKE: {
                final byte type = HandshakeContext.getHandshakeType(this, plaintext);
                if (this.handshakeContext == null) {
                    if (type == SSLHandshake.KEY_UPDATE.id || type == SSLHandshake.NEW_SESSION_TICKET.id) {
                        if (!this.isNegotiated) {
                            throw this.fatal(Alert.UNEXPECTED_MESSAGE, "Unexpected unnegotiated post-handshake message: " + SSLHandshake.nameOf(type));
                        }
                        if (!PostHandshakeContext.isConsumable(this, type)) {
                            throw this.fatal(Alert.UNEXPECTED_MESSAGE, "Unexpected post-handshake message: " + SSLHandshake.nameOf(type));
                        }
                        this.handshakeContext = new PostHandshakeContext(this);
                    }
                    else {
                        this.handshakeContext = (this.sslConfig.isClientMode ? new ClientHandshakeContext(this.sslContext, this) : new ServerHandshakeContext(this.sslContext, this));
                        this.outputRecord.initHandshaker();
                    }
                }
                this.handshakeContext.dispatch(type, plaintext);
                break;
            }
            case ALERT: {
                Alert.alertConsumer.consume(this, plaintext.fragment);
                break;
            }
            default: {
                final SSLConsumer consumer = this.consumers.get(plaintext.contentType);
                if (consumer != null) {
                    consumer.consume(this, plaintext.fragment);
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
                    this.outputRecord.initHandshaker();
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
        catch (final IOException ioe) {
            if (SSLLogger.isOn && SSLLogger.isOn("ssl")) {
                SSLLogger.warning("Warning: failed to send warning alert " + alert, ioe);
            }
        }
    }
    
    void closeNotify(final boolean isUserCanceled) throws IOException {
        if (this.transport instanceof SSLSocketImpl) {
            ((SSLSocketImpl)this.transport).closeNotify(isUserCanceled);
        }
        else {
            synchronized (this.outputRecord) {
                try {
                    if (isUserCanceled) {
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
    
    SSLException fatal(final Alert alert, final String diagnostic) throws SSLException {
        return this.fatal(alert, diagnostic, null);
    }
    
    SSLException fatal(final Alert alert, final Throwable cause) throws SSLException {
        return this.fatal(alert, null, cause);
    }
    
    SSLException fatal(final Alert alert, final String diagnostic, final Throwable cause) throws SSLException {
        return this.fatal(alert, diagnostic, false, cause);
    }
    
    SSLException fatal(final Alert alert, String diagnostic, final boolean recvFatalAlert, Throwable cause) throws SSLException {
        if (this.closeReason != null) {
            if (cause == null) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl")) {
                    SSLLogger.warning("Closed transport, general or untracked problem", new Object[0]);
                }
                throw alert.createSSLException("Closed transport, general or untracked problem");
            }
            if (cause instanceof SSLException) {
                throw (SSLException)cause;
            }
            if (SSLLogger.isOn && SSLLogger.isOn("ssl")) {
                SSLLogger.warning("Closed transport, unexpected rethrowing", cause);
            }
            throw alert.createSSLException("Unexpected rethrowing", cause);
        }
        else {
            if (diagnostic == null) {
                if (cause == null) {
                    diagnostic = "General/Untracked problem";
                }
                else {
                    diagnostic = cause.getMessage();
                }
            }
            if (cause == null) {
                cause = alert.createSSLException(diagnostic);
            }
            if (SSLLogger.isOn && SSLLogger.isOn("ssl")) {
                SSLLogger.severe("Fatal (" + alert + "): " + diagnostic, cause);
            }
            if (cause instanceof SSLException) {
                this.closeReason = (SSLException)cause;
            }
            else {
                this.closeReason = alert.createSSLException(diagnostic, cause);
            }
            try {
                this.inputRecord.close();
            }
            catch (final IOException ioe) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl")) {
                    SSLLogger.warning("Fatal: input record closure failed", ioe);
                }
                this.closeReason.addSuppressed(ioe);
            }
            if (this.conSession != null) {
                this.conSession.invalidate();
            }
            if (this.handshakeContext != null && this.handshakeContext.handshakeSession != null) {
                this.handshakeContext.handshakeSession.invalidate();
            }
            Label_0409: {
                if (!recvFatalAlert && !this.isOutboundClosed() && !this.isBroken) {
                    if (!this.isNegotiated) {
                        if (this.handshakeContext == null) {
                            break Label_0409;
                        }
                    }
                    try {
                        this.outputRecord.encodeAlert(Alert.Level.FATAL.level, alert.id);
                    }
                    catch (final IOException ioe) {
                        if (SSLLogger.isOn && SSLLogger.isOn("ssl")) {
                            SSLLogger.warning("Fatal: failed to send fatal alert " + alert, ioe);
                        }
                        this.closeReason.addSuppressed(ioe);
                    }
                }
                try {
                    this.outputRecord.close();
                }
                catch (final IOException ioe) {
                    if (SSLLogger.isOn && SSLLogger.isOn("ssl")) {
                        SSLLogger.warning("Fatal: output record closure failed", ioe);
                    }
                    this.closeReason.addSuppressed(ioe);
                }
            }
            if (this.handshakeContext != null) {
                this.handshakeContext = null;
            }
            try {
                this.transport.shutdown();
            }
            catch (final IOException ioe) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl")) {
                    SSLLogger.warning("Fatal: transport closure failed", ioe);
                }
                this.closeReason.addSuppressed(ioe);
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
    
    void setUseClientMode(final boolean useClientMode) {
        if (this.handshakeContext != null || this.isNegotiated) {
            throw new IllegalArgumentException("Cannot change mode after SSL traffic has started");
        }
        if (this.sslConfig.isClientMode != useClientMode) {
            if (this.sslContext.isDefaultProtocolVesions(this.sslConfig.enabledProtocols)) {
                this.sslConfig.enabledProtocols = this.sslContext.getDefaultProtocolVersions(!useClientMode);
            }
            if (this.sslContext.isDefaultCipherSuiteList(this.sslConfig.enabledCipherSuites)) {
                this.sslConfig.enabledCipherSuites = this.sslContext.getDefaultCipherSuites(!useClientMode);
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
        catch (final IOException ioe) {
            if (SSLLogger.isOn && SSLLogger.isOn("ssl")) {
                SSLLogger.warning("inbound closure failed", ioe);
            }
        }
    }
    
    private void passiveInboundClose() throws IOException {
        if (!this.isInboundClosed()) {
            this.inputRecord.close();
        }
        if (!this.isOutboundClosed()) {
            boolean needCloseNotify = SSLConfiguration.acknowledgeCloseNotify;
            if (!needCloseNotify) {
                if (this.isNegotiated) {
                    if (!this.protocolVersion.useTLS13PlusSpec()) {
                        needCloseNotify = true;
                    }
                }
                else if (this.handshakeContext != null) {
                    final ProtocolVersion pv = this.handshakeContext.negotiatedProtocol;
                    if (pv == null || !pv.useTLS13PlusSpec()) {
                        needCloseNotify = true;
                    }
                }
            }
            if (needCloseNotify) {
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
        catch (final IOException ioe) {
            if (SSLLogger.isOn && SSLLogger.isOn("ssl")) {
                SSLLogger.warning("outbound closure failed", ioe);
            }
        }
    }
    
    private void initiateOutboundClose() throws IOException {
        boolean useUserCanceled = false;
        if (!this.isNegotiated && this.handshakeContext != null && !this.peerUserCanceled) {
            useUserCanceled = true;
        }
        this.closeNotify(useUserCanceled);
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
        return SSLEngineResult.HandshakeStatus.NOT_HANDSHAKING;
    }
    
    boolean needUnwrapAgain() {
        if (!this.outputRecord.isEmpty()) {
            return false;
        }
        if (this.isOutboundClosed() && this.isInboundClosed()) {
            return false;
        }
        if (this.handshakeContext != null) {
            if (!this.handshakeContext.delegatedActions.isEmpty()) {
                return false;
            }
            if (!this.isInboundClosed() && this.sslContext.isDTLS() && !this.inputRecord.isEmpty()) {
                return this.getHandshakeStatus() == SSLEngineResult.HandshakeStatus.NEED_UNWRAP;
            }
        }
        return false;
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
        this.inputRecord.finishHandshake();
        this.outputRecord.finishHandshake();
        this.isNegotiated = true;
        if (this.transport instanceof SSLSocket && this.sslConfig.handshakeListeners != null && !this.sslConfig.handshakeListeners.isEmpty()) {
            final HandshakeCompletedEvent hce = new HandshakeCompletedEvent((javax.net.ssl.SSLSocket)this.transport, this.conSession);
            final Thread thread = new Thread(null, new NotifyHandshake(this.sslConfig.handshakeListeners, hce), "HandshakeCompletedNotify-Thread", 0L);
            thread.start();
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
        
        NotifyHandshake(final Map<HandshakeCompletedListener, AccessControlContext> listeners, final HandshakeCompletedEvent event) {
            this.targets = new HashSet<Map.Entry<HandshakeCompletedListener, AccessControlContext>>(listeners.entrySet());
            this.event = event;
        }
        
        @Override
        public void run() {
            for (final Map.Entry<HandshakeCompletedListener, AccessControlContext> entry : this.targets) {
                final HandshakeCompletedListener listener = entry.getKey();
                final AccessControlContext acc = entry.getValue();
                AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Void>() {
                    @Override
                    public Void run() {
                        listener.handshakeCompleted(NotifyHandshake.this.event);
                        return null;
                    }
                }, acc);
            }
        }
    }
}
