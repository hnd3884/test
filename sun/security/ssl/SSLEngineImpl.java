package sun.security.ssl;

import java.util.Map;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.security.AccessController;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLKeyException;
import java.util.List;
import java.util.function.BiFunction;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLSession;
import java.nio.ReadOnlyBufferException;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLProtocolException;
import javax.net.ssl.SSLEngineResult;
import java.nio.ByteBuffer;
import javax.net.ssl.SSLException;
import java.io.IOException;
import javax.net.ssl.SSLEngine;

final class SSLEngineImpl extends SSLEngine implements SSLTransport
{
    private final SSLContextImpl sslContext;
    final TransportContext conContext;
    
    SSLEngineImpl(final SSLContextImpl sslContextImpl) {
        this(sslContextImpl, null, -1);
    }
    
    SSLEngineImpl(final SSLContextImpl sslContext, final String s, final int n) {
        super(s, n);
        this.sslContext = sslContext;
        final HandshakeHash handshakeHash = new HandshakeHash();
        this.conContext = new TransportContext(sslContext, this, new SSLEngineInputRecord(handshakeHash), new SSLEngineOutputRecord(handshakeHash));
        if (s != null) {
            this.conContext.sslConfig.serverNames = Utilities.addToSNIServerNameList(this.conContext.sslConfig.serverNames, s);
        }
    }
    
    @Override
    public synchronized void beginHandshake() throws SSLException {
        if (this.conContext.isUnsureMode) {
            throw new IllegalStateException("Client/Server mode has not yet been set.");
        }
        try {
            this.conContext.kickstart();
        }
        catch (final IOException ex) {
            throw this.conContext.fatal(Alert.HANDSHAKE_FAILURE, "Couldn't kickstart handshaking", ex);
        }
        catch (final Exception ex2) {
            throw this.conContext.fatal(Alert.INTERNAL_ERROR, "Fail to begin handshake", ex2);
        }
    }
    
    @Override
    public synchronized SSLEngineResult wrap(final ByteBuffer[] array, final int n, final int n2, final ByteBuffer byteBuffer) throws SSLException {
        return this.wrap(array, n, n2, new ByteBuffer[] { byteBuffer }, 0, 1);
    }
    
    public synchronized SSLEngineResult wrap(final ByteBuffer[] array, final int n, final int n2, final ByteBuffer[] array2, final int n3, final int n4) throws SSLException {
        if (this.conContext.isUnsureMode) {
            throw new IllegalStateException("Client/Server mode has not yet been set.");
        }
        this.checkTaskThrown();
        checkParams(array, n, n2, array2, n3, n4);
        try {
            return this.writeRecord(array, n, n2, array2, n3, n4);
        }
        catch (final SSLProtocolException ex) {
            throw this.conContext.fatal(Alert.UNEXPECTED_MESSAGE, ex);
        }
        catch (final IOException ex2) {
            throw this.conContext.fatal(Alert.INTERNAL_ERROR, "problem wrapping app data", ex2);
        }
        catch (final Exception ex3) {
            throw this.conContext.fatal(Alert.INTERNAL_ERROR, "Fail to wrap application data", ex3);
        }
    }
    
    private SSLEngineResult writeRecord(final ByteBuffer[] array, final int n, final int n2, final ByteBuffer[] array2, final int n3, final int n4) throws IOException {
        if (this.isOutboundDone()) {
            return new SSLEngineResult(SSLEngineResult.Status.CLOSED, this.getHandshakeStatus(), 0, 0);
        }
        final HandshakeContext handshakeContext = this.conContext.handshakeContext;
        SSLEngineResult.HandshakeStatus handshakeStatus = null;
        if (!this.conContext.isNegotiated && !this.conContext.isBroken && !this.conContext.isInboundClosed() && !this.conContext.isOutboundClosed()) {
            this.conContext.kickstart();
            handshakeStatus = this.getHandshakeStatus();
            if (handshakeStatus == SSLEngineResult.HandshakeStatus.NEED_UNWRAP) {
                return new SSLEngineResult(SSLEngineResult.Status.OK, handshakeStatus, 0, 0);
            }
        }
        if (handshakeStatus == null) {
            handshakeStatus = this.getHandshakeStatus();
        }
        if (handshakeStatus == SSLEngineResult.HandshakeStatus.NEED_TASK) {
            return new SSLEngineResult(SSLEngineResult.Status.OK, handshakeStatus, 0, 0);
        }
        int n5 = 0;
        for (int i = n3; i < n3 + n4; ++i) {
            n5 += array2[i].remaining();
        }
        if (n5 < this.conContext.conSession.getPacketBufferSize()) {
            return new SSLEngineResult(SSLEngineResult.Status.BUFFER_OVERFLOW, this.getHandshakeStatus(), 0, 0);
        }
        int n6 = 0;
        for (int j = n; j < n + n2; ++j) {
            n6 += array[j].remaining();
        }
        Ciphertext ciphertext = null;
        try {
            if (!this.conContext.outputRecord.isEmpty()) {
                ciphertext = this.encode(null, 0, 0, array2, n3, n4);
            }
            if (ciphertext == null && n6 != 0) {
                ciphertext = this.encode(array, n, n2, array2, n3, n4);
            }
        }
        catch (final IOException ex) {
            if (ex instanceof SSLException) {
                throw ex;
            }
            throw new SSLException("Write problems", ex);
        }
        final SSLEngineResult.Status status = this.isOutboundDone() ? SSLEngineResult.Status.CLOSED : SSLEngineResult.Status.OK;
        SSLEngineResult.HandshakeStatus handshakeStatus2;
        if (ciphertext != null && ciphertext.handshakeStatus != null) {
            handshakeStatus2 = ciphertext.handshakeStatus;
        }
        else {
            handshakeStatus2 = this.getHandshakeStatus();
        }
        int n7 = n6;
        for (int k = n; k < n + n2; ++k) {
            n7 -= array[k].remaining();
        }
        int n8 = n5;
        for (int l = n3; l < n3 + n4; ++l) {
            n8 -= array2[l].remaining();
        }
        return new SSLEngineResult(status, handshakeStatus2, n7, n8);
    }
    
    private Ciphertext encode(final ByteBuffer[] array, final int n, final int n2, final ByteBuffer[] array2, final int n3, final int n4) throws IOException {
        Ciphertext encode;
        try {
            encode = this.conContext.outputRecord.encode(array, n, n2, array2, n3, n4);
        }
        catch (final SSLHandshakeException ex) {
            throw this.conContext.fatal(Alert.HANDSHAKE_FAILURE, ex);
        }
        catch (final IOException ex2) {
            throw this.conContext.fatal(Alert.UNEXPECTED_MESSAGE, ex2);
        }
        if (encode == null) {
            return Ciphertext.CIPHERTEXT_NULL;
        }
        SSLEngineResult.HandshakeStatus handshakeStatus = this.tryToFinishHandshake(encode.contentType);
        if (handshakeStatus == null) {
            handshakeStatus = this.conContext.getHandshakeStatus();
        }
        if (this.conContext.outputRecord.seqNumIsHuge() || this.conContext.outputRecord.writeCipher.atKeyLimit()) {
            handshakeStatus = this.tryKeyUpdate(handshakeStatus);
        }
        encode.handshakeStatus = handshakeStatus;
        return encode;
    }
    
    private SSLEngineResult.HandshakeStatus tryToFinishHandshake(final byte b) {
        SSLEngineResult.HandshakeStatus handshakeStatus = null;
        if (b == ContentType.HANDSHAKE.id && this.conContext.outputRecord.isEmpty()) {
            if (this.conContext.handshakeContext == null) {
                handshakeStatus = SSLEngineResult.HandshakeStatus.FINISHED;
            }
            else if (this.conContext.isPostHandshakeContext()) {
                handshakeStatus = this.conContext.finishPostHandshake();
            }
            else if (this.conContext.handshakeContext.handshakeFinished) {
                handshakeStatus = this.conContext.finishHandshake();
            }
        }
        return handshakeStatus;
    }
    
    private SSLEngineResult.HandshakeStatus tryKeyUpdate(final SSLEngineResult.HandshakeStatus handshakeStatus) throws IOException {
        if (this.conContext.handshakeContext == null && !this.conContext.isOutboundClosed() && !this.conContext.isInboundClosed() && !this.conContext.isBroken) {
            if (SSLLogger.isOn && SSLLogger.isOn("ssl")) {
                SSLLogger.finest("trigger key update", new Object[0]);
            }
            this.beginHandshake();
            return this.conContext.getHandshakeStatus();
        }
        return handshakeStatus;
    }
    
    private static void checkParams(final ByteBuffer[] array, final int n, final int n2, final ByteBuffer[] array2, final int n3, final int n4) {
        if (array == null || array2 == null) {
            throw new IllegalArgumentException("source or destination buffer is null");
        }
        if (n3 < 0 || n4 < 0 || n3 > array2.length - n4) {
            throw new IndexOutOfBoundsException("index out of bound of the destination buffers");
        }
        if (n < 0 || n2 < 0 || n > array.length - n2) {
            throw new IndexOutOfBoundsException("index out of bound of the source buffers");
        }
        for (int i = n3; i < n3 + n4; ++i) {
            if (array2[i] == null) {
                throw new IllegalArgumentException("destination buffer[" + i + "] == null");
            }
            if (array2[i].isReadOnly()) {
                throw new ReadOnlyBufferException();
            }
        }
        for (int j = n; j < n + n2; ++j) {
            if (array[j] == null) {
                throw new IllegalArgumentException("source buffer[" + j + "] == null");
            }
        }
    }
    
    @Override
    public synchronized SSLEngineResult unwrap(final ByteBuffer byteBuffer, final ByteBuffer[] array, final int n, final int n2) throws SSLException {
        return this.unwrap(new ByteBuffer[] { byteBuffer }, 0, 1, array, n, n2);
    }
    
    public synchronized SSLEngineResult unwrap(final ByteBuffer[] array, final int n, final int n2, final ByteBuffer[] array2, final int n3, final int n4) throws SSLException {
        if (this.conContext.isUnsureMode) {
            throw new IllegalStateException("Client/Server mode has not yet been set.");
        }
        this.checkTaskThrown();
        checkParams(array, n, n2, array2, n3, n4);
        try {
            return this.readRecord(array, n, n2, array2, n3, n4);
        }
        catch (final SSLProtocolException ex) {
            throw this.conContext.fatal(Alert.UNEXPECTED_MESSAGE, ex.getMessage(), ex);
        }
        catch (final IOException ex2) {
            throw this.conContext.fatal(Alert.INTERNAL_ERROR, "problem unwrapping net record", ex2);
        }
        catch (final Exception ex3) {
            throw this.conContext.fatal(Alert.INTERNAL_ERROR, "Fail to unwrap network record", ex3);
        }
    }
    
    private SSLEngineResult readRecord(final ByteBuffer[] array, final int n, final int n2, final ByteBuffer[] array2, final int n3, final int n4) throws IOException {
        if (this.isInboundDone()) {
            return new SSLEngineResult(SSLEngineResult.Status.CLOSED, this.getHandshakeStatus(), 0, 0);
        }
        SSLEngineResult.HandshakeStatus handshakeStatus = null;
        if (!this.conContext.isNegotiated && !this.conContext.isBroken && !this.conContext.isInboundClosed() && !this.conContext.isOutboundClosed()) {
            this.conContext.kickstart();
            handshakeStatus = this.getHandshakeStatus();
            if (handshakeStatus == SSLEngineResult.HandshakeStatus.NEED_WRAP) {
                return new SSLEngineResult(SSLEngineResult.Status.OK, handshakeStatus, 0, 0);
            }
        }
        if (handshakeStatus == null) {
            handshakeStatus = this.getHandshakeStatus();
        }
        if (handshakeStatus == SSLEngineResult.HandshakeStatus.NEED_TASK) {
            return new SSLEngineResult(SSLEngineResult.Status.OK, handshakeStatus, 0, 0);
        }
        int n5 = 0;
        for (int i = n; i < n + n2; ++i) {
            n5 += array[i].remaining();
        }
        if (n5 == 0) {
            return new SSLEngineResult(SSLEngineResult.Status.BUFFER_UNDERFLOW, handshakeStatus, 0, 0);
        }
        final int bytesInCompletePacket = this.conContext.inputRecord.bytesInCompletePacket(array, n, n2);
        if (bytesInCompletePacket > this.conContext.conSession.getPacketBufferSize()) {
            if (bytesInCompletePacket <= 33093) {
                this.conContext.conSession.expandBufferSizes();
            }
            final int packetBufferSize = this.conContext.conSession.getPacketBufferSize();
            if (bytesInCompletePacket > packetBufferSize) {
                throw new SSLProtocolException("Input record too big: max = " + packetBufferSize + " len = " + bytesInCompletePacket);
            }
        }
        int n6 = 0;
        for (int j = n3; j < n3 + n4; ++j) {
            n6 += array2[j].remaining();
        }
        if (this.conContext.isNegotiated && this.conContext.inputRecord.estimateFragmentSize(bytesInCompletePacket) > n6) {
            return new SSLEngineResult(SSLEngineResult.Status.BUFFER_OVERFLOW, handshakeStatus, 0, 0);
        }
        if (bytesInCompletePacket == -1 || n5 < bytesInCompletePacket) {
            return new SSLEngineResult(SSLEngineResult.Status.BUFFER_UNDERFLOW, handshakeStatus, 0, 0);
        }
        Plaintext decode;
        try {
            decode = this.decode(array, n, n2, array2, n3, n4);
        }
        catch (final IOException ex) {
            if (ex instanceof SSLException) {
                throw ex;
            }
            throw new SSLException("readRecord", ex);
        }
        final SSLEngineResult.Status status = this.isInboundDone() ? SSLEngineResult.Status.CLOSED : SSLEngineResult.Status.OK;
        SSLEngineResult.HandshakeStatus handshakeStatus2;
        if (decode.handshakeStatus != null) {
            handshakeStatus2 = decode.handshakeStatus;
        }
        else {
            handshakeStatus2 = this.getHandshakeStatus();
        }
        int n7 = n5;
        for (int k = n; k < n + n2; ++k) {
            n7 -= array[k].remaining();
        }
        int n8 = n6;
        for (int l = n3; l < n3 + n4; ++l) {
            n8 -= array2[l].remaining();
        }
        return new SSLEngineResult(status, handshakeStatus2, n7, n8);
    }
    
    private Plaintext decode(final ByteBuffer[] array, final int n, final int n2, final ByteBuffer[] array2, final int n3, final int n4) throws IOException {
        final Plaintext decode = SSLTransport.decode(this.conContext, array, n, n2, array2, n3, n4);
        if (decode != Plaintext.PLAINTEXT_NULL) {
            final SSLEngineResult.HandshakeStatus tryToFinishHandshake = this.tryToFinishHandshake(decode.contentType);
            if (tryToFinishHandshake == null) {
                decode.handshakeStatus = this.conContext.getHandshakeStatus();
            }
            else {
                decode.handshakeStatus = tryToFinishHandshake;
            }
            if (this.conContext.inputRecord.seqNumIsHuge() || this.conContext.inputRecord.readCipher.atKeyLimit()) {
                decode.handshakeStatus = this.tryKeyUpdate(decode.handshakeStatus);
            }
        }
        return decode;
    }
    
    @Override
    public synchronized Runnable getDelegatedTask() {
        if (this.conContext.handshakeContext != null && !this.conContext.handshakeContext.taskDelegated && !this.conContext.handshakeContext.delegatedActions.isEmpty()) {
            this.conContext.handshakeContext.taskDelegated = true;
            return new DelegatedTask(this);
        }
        return null;
    }
    
    @Override
    public synchronized void closeInbound() throws SSLException {
        if (this.isInboundDone()) {
            return;
        }
        if (SSLLogger.isOn && SSLLogger.isOn("ssl")) {
            SSLLogger.finest("Closing inbound of SSLEngine", new Object[0]);
        }
        if (!this.conContext.isInputCloseNotified && (this.conContext.isNegotiated || this.conContext.handshakeContext != null)) {
            throw this.conContext.fatal(Alert.INTERNAL_ERROR, "closing inbound before receiving peer's close_notify");
        }
        this.conContext.closeInbound();
    }
    
    @Override
    public synchronized boolean isInboundDone() {
        return this.conContext.isInboundClosed();
    }
    
    @Override
    public synchronized void closeOutbound() {
        if (this.conContext.isOutboundClosed()) {
            return;
        }
        if (SSLLogger.isOn && SSLLogger.isOn("ssl")) {
            SSLLogger.finest("Closing outbound of SSLEngine", new Object[0]);
        }
        this.conContext.closeOutbound();
    }
    
    @Override
    public synchronized boolean isOutboundDone() {
        return this.conContext.isOutboundDone();
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
    public synchronized SSLSession getSession() {
        return this.conContext.conSession;
    }
    
    @Override
    public synchronized SSLSession getHandshakeSession() {
        return (this.conContext.handshakeContext == null) ? null : this.conContext.handshakeContext.handshakeSession;
    }
    
    @Override
    public synchronized SSLEngineResult.HandshakeStatus getHandshakeStatus() {
        return this.conContext.getHandshakeStatus();
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
        return (this.conContext.handshakeContext == null) ? null : this.conContext.handshakeContext.applicationProtocol;
    }
    
    public synchronized void setHandshakeApplicationProtocolSelector(final BiFunction<SSLEngine, List<String>, String> engineAPSelector) {
        this.conContext.sslConfig.engineAPSelector = engineAPSelector;
    }
    
    public synchronized BiFunction<SSLEngine, List<String>, String> getHandshakeApplicationProtocolSelector() {
        return this.conContext.sslConfig.engineAPSelector;
    }
    
    @Override
    public boolean useDelegatedTask() {
        return true;
    }
    
    private synchronized void checkTaskThrown() throws SSLException {
        Exception ex = null;
        final HandshakeContext handshakeContext = this.conContext.handshakeContext;
        if (handshakeContext != null && handshakeContext.delegatedThrown != null) {
            ex = handshakeContext.delegatedThrown;
            handshakeContext.delegatedThrown = null;
        }
        if (this.conContext.delegatedThrown != null) {
            if (ex != null) {
                if (this.conContext.delegatedThrown == ex) {
                    this.conContext.delegatedThrown = null;
                }
            }
            else {
                ex = this.conContext.delegatedThrown;
                this.conContext.delegatedThrown = null;
            }
        }
        if (ex == null) {
            return;
        }
        if (ex instanceof SSLException) {
            throw (SSLException)ex;
        }
        if (ex instanceof RuntimeException) {
            throw (RuntimeException)ex;
        }
        throw getTaskThrown(ex);
    }
    
    private static SSLException getTaskThrown(final Exception ex) {
        String message = ex.getMessage();
        if (message == null) {
            message = "Delegated task threw Exception or Error";
        }
        if (ex instanceof RuntimeException) {
            throw new RuntimeException(message, ex);
        }
        if (ex instanceof SSLHandshakeException) {
            return (SSLHandshakeException)new SSLHandshakeException(message).initCause(ex);
        }
        if (ex instanceof SSLKeyException) {
            return (SSLKeyException)new SSLKeyException(message).initCause(ex);
        }
        if (ex instanceof SSLPeerUnverifiedException) {
            return (SSLPeerUnverifiedException)new SSLPeerUnverifiedException(message).initCause(ex);
        }
        if (ex instanceof SSLProtocolException) {
            return (SSLProtocolException)new SSLProtocolException(message).initCause(ex);
        }
        if (ex instanceof SSLException) {
            return (SSLException)ex;
        }
        return new SSLException(message, ex);
    }
    
    private static class DelegatedTask implements Runnable
    {
        private final SSLEngineImpl engine;
        
        DelegatedTask(final SSLEngineImpl engine) {
            this.engine = engine;
        }
        
        @Override
        public void run() {
            synchronized (this.engine) {
                final HandshakeContext handshakeContext = this.engine.conContext.handshakeContext;
                if (handshakeContext == null || handshakeContext.delegatedActions.isEmpty()) {
                    return;
                }
                try {
                    AccessController.doPrivileged((PrivilegedExceptionAction<Object>)new DelegatedAction(handshakeContext), this.engine.conContext.acc);
                }
                catch (final PrivilegedActionException ex) {
                    final Exception exception = ex.getException();
                    if (this.engine.conContext.delegatedThrown == null) {
                        this.engine.conContext.delegatedThrown = exception;
                    }
                    final HandshakeContext handshakeContext2 = this.engine.conContext.handshakeContext;
                    if (handshakeContext2 != null) {
                        handshakeContext2.delegatedThrown = exception;
                    }
                    else if (this.engine.conContext.closeReason != null) {
                        this.engine.conContext.closeReason = getTaskThrown(exception);
                    }
                }
                catch (final RuntimeException closeReason) {
                    if (this.engine.conContext.delegatedThrown == null) {
                        this.engine.conContext.delegatedThrown = closeReason;
                    }
                    final HandshakeContext handshakeContext3 = this.engine.conContext.handshakeContext;
                    if (handshakeContext3 != null) {
                        handshakeContext3.delegatedThrown = closeReason;
                    }
                    else if (this.engine.conContext.closeReason != null) {
                        this.engine.conContext.closeReason = closeReason;
                    }
                }
                final HandshakeContext handshakeContext4 = this.engine.conContext.handshakeContext;
                if (handshakeContext4 != null) {
                    handshakeContext4.taskDelegated = false;
                }
            }
        }
        
        private static class DelegatedAction implements PrivilegedExceptionAction<Void>
        {
            final HandshakeContext context;
            
            DelegatedAction(final HandshakeContext context) {
                this.context = context;
            }
            
            @Override
            public Void run() throws Exception {
                while (!this.context.delegatedActions.isEmpty()) {
                    final Map.Entry entry = this.context.delegatedActions.poll();
                    if (entry != null) {
                        this.context.dispatch((byte)entry.getKey(), (ByteBuffer)entry.getValue());
                    }
                }
                return null;
            }
        }
    }
}
