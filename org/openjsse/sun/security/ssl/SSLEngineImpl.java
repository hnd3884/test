package org.openjsse.sun.security.ssl;

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
import org.openjsse.javax.net.ssl.SSLEngineResult;
import java.nio.ByteBuffer;
import javax.net.ssl.SSLException;
import java.io.IOException;
import org.openjsse.javax.net.ssl.SSLEngine;

final class SSLEngineImpl extends SSLEngine implements SSLTransport
{
    private final SSLContextImpl sslContext;
    final TransportContext conContext;
    
    SSLEngineImpl(final SSLContextImpl sslContext) {
        this(sslContext, null, -1);
    }
    
    SSLEngineImpl(final SSLContextImpl sslContext, final String host, final int port) {
        super(host, port);
        this.sslContext = sslContext;
        final HandshakeHash handshakeHash = new HandshakeHash();
        if (sslContext.isDTLS()) {
            this.conContext = new TransportContext(sslContext, this, new DTLSInputRecord(handshakeHash), new DTLSOutputRecord(handshakeHash));
        }
        else {
            this.conContext = new TransportContext(sslContext, this, new SSLEngineInputRecord(handshakeHash), new SSLEngineOutputRecord(handshakeHash));
        }
        if (host != null) {
            this.conContext.sslConfig.serverNames = Utilities.addToSNIServerNameList(this.conContext.sslConfig.serverNames, host);
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
        catch (final IOException ioe) {
            throw this.conContext.fatal(Alert.HANDSHAKE_FAILURE, "Couldn't kickstart handshaking", ioe);
        }
        catch (final Exception ex) {
            throw this.conContext.fatal(Alert.INTERNAL_ERROR, "Fail to begin handshake", ex);
        }
    }
    
    @Override
    public synchronized SSLEngineResult wrap(final ByteBuffer[] appData, final int offset, final int length, final ByteBuffer netData) throws SSLException {
        return this.wrap(appData, offset, length, new ByteBuffer[] { netData }, 0, 1);
    }
    
    public synchronized SSLEngineResult wrap(final ByteBuffer[] srcs, final int srcsOffset, final int srcsLength, final ByteBuffer[] dsts, final int dstsOffset, final int dstsLength) throws SSLException {
        if (this.conContext.isUnsureMode) {
            throw new IllegalStateException("Client/Server mode has not yet been set.");
        }
        this.checkTaskThrown();
        checkParams(srcs, srcsOffset, srcsLength, dsts, dstsOffset, dstsLength);
        try {
            return this.writeRecord(srcs, srcsOffset, srcsLength, dsts, dstsOffset, dstsLength);
        }
        catch (final SSLProtocolException spe) {
            throw this.conContext.fatal(Alert.UNEXPECTED_MESSAGE, spe);
        }
        catch (final IOException ioe) {
            throw this.conContext.fatal(Alert.INTERNAL_ERROR, "problem wrapping app data", ioe);
        }
        catch (final Exception ex) {
            throw this.conContext.fatal(Alert.INTERNAL_ERROR, "Fail to wrap application data", ex);
        }
    }
    
    private SSLEngineResult writeRecord(final ByteBuffer[] srcs, final int srcsOffset, final int srcsLength, final ByteBuffer[] dsts, final int dstsOffset, final int dstsLength) throws IOException {
        if (this.isOutboundDone()) {
            return new SSLEngineResult(javax.net.ssl.SSLEngineResult.Status.CLOSED, this.getHandshakeStatus(), 0, 0, -1L, this.conContext.needUnwrapAgain());
        }
        final HandshakeContext hc = this.conContext.handshakeContext;
        javax.net.ssl.SSLEngineResult.HandshakeStatus hsStatus = null;
        if (!this.conContext.isNegotiated && !this.conContext.isBroken && !this.conContext.isInboundClosed() && !this.conContext.isOutboundClosed()) {
            this.conContext.kickstart();
            hsStatus = this.getHandshakeStatus();
            if (hsStatus == javax.net.ssl.SSLEngineResult.HandshakeStatus.NEED_UNWRAP && !this.needUnwrapAgain() && (!this.sslContext.isDTLS() || hc == null || !hc.sslConfig.enableRetransmissions || this.conContext.outputRecord.firstMessage)) {
                return new SSLEngineResult(javax.net.ssl.SSLEngineResult.Status.OK, hsStatus, 0, 0, -1L, this.needUnwrapAgain());
            }
        }
        if (hsStatus == null) {
            hsStatus = this.getHandshakeStatus();
        }
        if (hsStatus == javax.net.ssl.SSLEngineResult.HandshakeStatus.NEED_TASK) {
            return new SSLEngineResult(javax.net.ssl.SSLEngineResult.Status.OK, hsStatus, 0, 0);
        }
        int dstsRemains = 0;
        for (int i = dstsOffset; i < dstsOffset + dstsLength; ++i) {
            dstsRemains += dsts[i].remaining();
        }
        if (dstsRemains < this.conContext.conSession.getPacketBufferSize()) {
            return new SSLEngineResult(javax.net.ssl.SSLEngineResult.Status.BUFFER_OVERFLOW, this.getHandshakeStatus(), 0, 0, -1L, this.needUnwrapAgain());
        }
        int srcsRemains = 0;
        for (int j = srcsOffset; j < srcsOffset + srcsLength; ++j) {
            srcsRemains += srcs[j].remaining();
        }
        Ciphertext ciphertext = null;
        try {
            if (!this.conContext.outputRecord.isEmpty() || (hc != null && hc.sslConfig.enableRetransmissions && hc.sslContext.isDTLS() && hsStatus == javax.net.ssl.SSLEngineResult.HandshakeStatus.NEED_UNWRAP && !this.needUnwrapAgain())) {
                ciphertext = this.encode(null, 0, 0, dsts, dstsOffset, dstsLength);
            }
            if (ciphertext == null && srcsRemains != 0) {
                ciphertext = this.encode(srcs, srcsOffset, srcsLength, dsts, dstsOffset, dstsLength);
            }
        }
        catch (final IOException ioe) {
            if (ioe instanceof SSLException) {
                throw ioe;
            }
            throw new SSLException("Write problems", ioe);
        }
        javax.net.ssl.SSLEngineResult.Status status = this.isOutboundDone() ? javax.net.ssl.SSLEngineResult.Status.CLOSED : javax.net.ssl.SSLEngineResult.Status.OK;
        if (ciphertext != null && ciphertext.handshakeStatus != null) {
            hsStatus = ciphertext.handshakeStatus;
        }
        else {
            hsStatus = this.getHandshakeStatus();
            if (ciphertext == null && !this.conContext.isNegotiated && this.conContext.isInboundClosed() && hsStatus == javax.net.ssl.SSLEngineResult.HandshakeStatus.NEED_WRAP) {
                status = javax.net.ssl.SSLEngineResult.Status.CLOSED;
            }
        }
        int deltaSrcs = srcsRemains;
        for (int k = srcsOffset; k < srcsOffset + srcsLength; ++k) {
            deltaSrcs -= srcs[k].remaining();
        }
        int deltaDsts = dstsRemains;
        for (int l = dstsOffset; l < dstsOffset + dstsLength; ++l) {
            deltaDsts -= dsts[l].remaining();
        }
        return new SSLEngineResult(status, hsStatus, deltaSrcs, deltaDsts, (ciphertext != null) ? ciphertext.recordSN : -1L, this.needUnwrapAgain());
    }
    
    private Ciphertext encode(final ByteBuffer[] srcs, final int srcsOffset, final int srcsLength, final ByteBuffer[] dsts, final int dstsOffset, final int dstsLength) throws IOException {
        Ciphertext ciphertext = null;
        try {
            ciphertext = this.conContext.outputRecord.encode(srcs, srcsOffset, srcsLength, dsts, dstsOffset, dstsLength);
        }
        catch (final SSLHandshakeException she) {
            throw this.conContext.fatal(Alert.HANDSHAKE_FAILURE, she);
        }
        catch (final IOException e) {
            throw this.conContext.fatal(Alert.UNEXPECTED_MESSAGE, e);
        }
        if (ciphertext == null) {
            return null;
        }
        final boolean needRetransmission = this.conContext.sslContext.isDTLS() && this.conContext.handshakeContext != null && this.conContext.handshakeContext.sslConfig.enableRetransmissions;
        javax.net.ssl.SSLEngineResult.HandshakeStatus hsStatus = this.tryToFinishHandshake(ciphertext.contentType);
        if (needRetransmission && hsStatus == javax.net.ssl.SSLEngineResult.HandshakeStatus.FINISHED && this.conContext.sslContext.isDTLS() && ciphertext.handshakeType == SSLHandshake.FINISHED.id) {
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,verbose")) {
                SSLLogger.finest("retransmit the last flight messages", new Object[0]);
            }
            this.conContext.outputRecord.launchRetransmission();
            hsStatus = javax.net.ssl.SSLEngineResult.HandshakeStatus.NEED_WRAP;
        }
        if (hsStatus == null) {
            hsStatus = this.conContext.getHandshakeStatus();
        }
        if (this.conContext.outputRecord.seqNumIsHuge() || this.conContext.outputRecord.writeCipher.atKeyLimit()) {
            hsStatus = this.tryKeyUpdate(hsStatus);
        }
        ciphertext.handshakeStatus = hsStatus;
        return ciphertext;
    }
    
    private javax.net.ssl.SSLEngineResult.HandshakeStatus tryToFinishHandshake(final byte contentType) {
        javax.net.ssl.SSLEngineResult.HandshakeStatus hsStatus = null;
        if (contentType == ContentType.HANDSHAKE.id && this.conContext.outputRecord.isEmpty()) {
            if (this.conContext.handshakeContext == null) {
                hsStatus = javax.net.ssl.SSLEngineResult.HandshakeStatus.FINISHED;
            }
            else if (this.conContext.isPostHandshakeContext()) {
                hsStatus = this.conContext.finishPostHandshake();
            }
            else if (this.conContext.handshakeContext.handshakeFinished) {
                hsStatus = this.conContext.finishHandshake();
            }
        }
        return hsStatus;
    }
    
    private javax.net.ssl.SSLEngineResult.HandshakeStatus tryKeyUpdate(final javax.net.ssl.SSLEngineResult.HandshakeStatus currentHandshakeStatus) throws IOException {
        if (this.conContext.handshakeContext == null && !this.conContext.isOutboundClosed() && !this.conContext.isInboundClosed() && !this.conContext.isBroken) {
            if (SSLLogger.isOn && SSLLogger.isOn("ssl")) {
                SSLLogger.finest("trigger key update", new Object[0]);
            }
            this.beginHandshake();
            return this.conContext.getHandshakeStatus();
        }
        return currentHandshakeStatus;
    }
    
    private static void checkParams(final ByteBuffer[] srcs, final int srcsOffset, final int srcsLength, final ByteBuffer[] dsts, final int dstsOffset, final int dstsLength) {
        if (srcs == null || dsts == null) {
            throw new IllegalArgumentException("source or destination buffer is null");
        }
        if (dstsOffset < 0 || dstsLength < 0 || dstsOffset > dsts.length - dstsLength) {
            throw new IndexOutOfBoundsException("index out of bound of the destination buffers");
        }
        if (srcsOffset < 0 || srcsLength < 0 || srcsOffset > srcs.length - srcsLength) {
            throw new IndexOutOfBoundsException("index out of bound of the source buffers");
        }
        for (int i = dstsOffset; i < dstsOffset + dstsLength; ++i) {
            if (dsts[i] == null) {
                throw new IllegalArgumentException("destination buffer[" + i + "] == null");
            }
            if (dsts[i].isReadOnly()) {
                throw new ReadOnlyBufferException();
            }
        }
        for (int i = srcsOffset; i < srcsOffset + srcsLength; ++i) {
            if (srcs[i] == null) {
                throw new IllegalArgumentException("source buffer[" + i + "] == null");
            }
        }
    }
    
    @Override
    public synchronized SSLEngineResult unwrap(final ByteBuffer src, final ByteBuffer[] dsts, final int offset, final int length) throws SSLException {
        return this.unwrap(new ByteBuffer[] { src }, 0, 1, dsts, offset, length);
    }
    
    public synchronized SSLEngineResult unwrap(final ByteBuffer[] srcs, final int srcsOffset, final int srcsLength, final ByteBuffer[] dsts, final int dstsOffset, final int dstsLength) throws SSLException {
        if (this.conContext.isUnsureMode) {
            throw new IllegalStateException("Client/Server mode has not yet been set.");
        }
        this.checkTaskThrown();
        checkParams(srcs, srcsOffset, srcsLength, dsts, dstsOffset, dstsLength);
        try {
            return this.readRecord(srcs, srcsOffset, srcsLength, dsts, dstsOffset, dstsLength);
        }
        catch (final SSLProtocolException spe) {
            throw this.conContext.fatal(Alert.UNEXPECTED_MESSAGE, spe.getMessage(), spe);
        }
        catch (final IOException ioe) {
            throw this.conContext.fatal(Alert.INTERNAL_ERROR, "problem unwrapping net record", ioe);
        }
        catch (final Exception ex) {
            throw this.conContext.fatal(Alert.INTERNAL_ERROR, "Fail to unwrap network record", ex);
        }
    }
    
    private SSLEngineResult readRecord(final ByteBuffer[] srcs, final int srcsOffset, final int srcsLength, final ByteBuffer[] dsts, final int dstsOffset, final int dstsLength) throws IOException {
        if (this.isInboundDone()) {
            return new SSLEngineResult(javax.net.ssl.SSLEngineResult.Status.CLOSED, this.getHandshakeStatus(), 0, 0, -1L, this.needUnwrapAgain());
        }
        javax.net.ssl.SSLEngineResult.HandshakeStatus hsStatus = null;
        if (!this.conContext.isNegotiated && !this.conContext.isBroken && !this.conContext.isInboundClosed() && !this.conContext.isOutboundClosed()) {
            this.conContext.kickstart();
            hsStatus = this.getHandshakeStatus();
            if (hsStatus == javax.net.ssl.SSLEngineResult.HandshakeStatus.NEED_WRAP) {
                return new SSLEngineResult(javax.net.ssl.SSLEngineResult.Status.OK, hsStatus, 0, 0);
            }
        }
        if (hsStatus == null) {
            hsStatus = this.getHandshakeStatus();
        }
        if (hsStatus == javax.net.ssl.SSLEngineResult.HandshakeStatus.NEED_TASK) {
            return new SSLEngineResult(javax.net.ssl.SSLEngineResult.Status.OK, hsStatus, 0, 0);
        }
        final boolean needUnwrapAgain = this.needUnwrapAgain();
        if (hsStatus == javax.net.ssl.SSLEngineResult.HandshakeStatus.NEED_UNWRAP && needUnwrapAgain) {
            Plaintext plainText = null;
            try {
                plainText = this.decode(null, 0, 0, dsts, dstsOffset, dstsLength);
            }
            catch (final IOException ioe) {
                if (ioe instanceof SSLException) {
                    throw ioe;
                }
                throw new SSLException("readRecord", ioe);
            }
            final javax.net.ssl.SSLEngineResult.Status status = this.isInboundDone() ? javax.net.ssl.SSLEngineResult.Status.CLOSED : javax.net.ssl.SSLEngineResult.Status.OK;
            if (plainText.handshakeStatus != null) {
                hsStatus = plainText.handshakeStatus;
            }
            else {
                hsStatus = this.getHandshakeStatus();
            }
            return new SSLEngineResult(status, hsStatus, 0, 0, plainText.recordSN, needUnwrapAgain);
        }
        int srcsRemains = 0;
        for (int i = srcsOffset; i < srcsOffset + srcsLength; ++i) {
            srcsRemains += srcs[i].remaining();
        }
        if (srcsRemains == 0) {
            return new SSLEngineResult(javax.net.ssl.SSLEngineResult.Status.BUFFER_UNDERFLOW, hsStatus, 0, 0, -1L, needUnwrapAgain);
        }
        int packetLen = 0;
        try {
            packetLen = this.conContext.inputRecord.bytesInCompletePacket(srcs, srcsOffset, srcsLength);
        }
        catch (final SSLException ssle) {
            if (this.sslContext.isDTLS()) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,verbose")) {
                    SSLLogger.finest("Discard invalid DTLS records", ssle);
                }
                final int deltaNet = 0;
                final javax.net.ssl.SSLEngineResult.Status status2 = this.isInboundDone() ? javax.net.ssl.SSLEngineResult.Status.CLOSED : javax.net.ssl.SSLEngineResult.Status.OK;
                if (hsStatus == null) {
                    hsStatus = this.getHandshakeStatus();
                }
                return new SSLEngineResult(status2, hsStatus, deltaNet, 0, -1L, needUnwrapAgain);
            }
            throw ssle;
        }
        if (packetLen > this.conContext.conSession.getPacketBufferSize()) {
            int largestRecordSize = this.sslContext.isDTLS() ? 16717 : 33093;
            if (packetLen <= largestRecordSize && !this.sslContext.isDTLS()) {
                this.conContext.conSession.expandBufferSizes();
            }
            largestRecordSize = this.conContext.conSession.getPacketBufferSize();
            if (packetLen > largestRecordSize) {
                throw new SSLProtocolException("Input record too big: max = " + largestRecordSize + " len = " + packetLen);
            }
        }
        int dstsRemains = 0;
        for (int j = dstsOffset; j < dstsOffset + dstsLength; ++j) {
            dstsRemains += dsts[j].remaining();
        }
        if (this.conContext.isNegotiated) {
            final int FragLen = this.conContext.inputRecord.estimateFragmentSize(packetLen);
            if (FragLen > dstsRemains) {
                return new SSLEngineResult(javax.net.ssl.SSLEngineResult.Status.BUFFER_OVERFLOW, hsStatus, 0, 0, -1L, needUnwrapAgain);
            }
        }
        if (packetLen == -1 || srcsRemains < packetLen) {
            return new SSLEngineResult(javax.net.ssl.SSLEngineResult.Status.BUFFER_UNDERFLOW, hsStatus, 0, 0, -1L, needUnwrapAgain);
        }
        Plaintext plainText2 = null;
        try {
            plainText2 = this.decode(srcs, srcsOffset, srcsLength, dsts, dstsOffset, dstsLength);
        }
        catch (final IOException ioe2) {
            if (ioe2 instanceof SSLException) {
                throw ioe2;
            }
            throw new SSLException("readRecord", ioe2);
        }
        final javax.net.ssl.SSLEngineResult.Status status2 = this.isInboundDone() ? javax.net.ssl.SSLEngineResult.Status.CLOSED : javax.net.ssl.SSLEngineResult.Status.OK;
        if (plainText2.handshakeStatus != null) {
            hsStatus = plainText2.handshakeStatus;
        }
        else {
            hsStatus = this.getHandshakeStatus();
        }
        int deltaNet2 = srcsRemains;
        for (int k = srcsOffset; k < srcsOffset + srcsLength; ++k) {
            deltaNet2 -= srcs[k].remaining();
        }
        int deltaApp = dstsRemains;
        for (int l = dstsOffset; l < dstsOffset + dstsLength; ++l) {
            deltaApp -= dsts[l].remaining();
        }
        return new SSLEngineResult(status2, hsStatus, deltaNet2, deltaApp, plainText2.recordSN, needUnwrapAgain);
    }
    
    private Plaintext decode(final ByteBuffer[] srcs, final int srcsOffset, final int srcsLength, final ByteBuffer[] dsts, final int dstsOffset, final int dstsLength) throws IOException {
        final Plaintext pt = SSLTransport.decode(this.conContext, srcs, srcsOffset, srcsLength, dsts, dstsOffset, dstsLength);
        if (pt != Plaintext.PLAINTEXT_NULL) {
            final javax.net.ssl.SSLEngineResult.HandshakeStatus hsStatus = this.tryToFinishHandshake(pt.contentType);
            if (hsStatus == null) {
                pt.handshakeStatus = this.conContext.getHandshakeStatus();
            }
            else {
                pt.handshakeStatus = hsStatus;
            }
            if (this.conContext.inputRecord.seqNumIsHuge() || this.conContext.inputRecord.readCipher.atKeyLimit()) {
                pt.handshakeStatus = this.tryKeyUpdate(pt.handshakeStatus);
            }
        }
        return pt;
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
    public synchronized void setEnabledCipherSuites(final String[] suites) {
        this.conContext.sslConfig.enabledCipherSuites = CipherSuite.validValuesOf(suites);
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
    public synchronized void setEnabledProtocols(final String[] protocols) {
        if (protocols == null) {
            throw new IllegalArgumentException("Protocols cannot be null");
        }
        this.conContext.sslConfig.enabledProtocols = ProtocolVersion.namesOf(protocols);
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
    public synchronized javax.net.ssl.SSLEngineResult.HandshakeStatus getHandshakeStatus() {
        return this.conContext.getHandshakeStatus();
    }
    
    @Override
    public synchronized boolean needUnwrapAgain() {
        return this.conContext.needUnwrapAgain();
    }
    
    @Override
    public synchronized void setUseClientMode(final boolean mode) {
        this.conContext.setUseClientMode(mode);
    }
    
    @Override
    public synchronized boolean getUseClientMode() {
        return this.conContext.sslConfig.isClientMode;
    }
    
    @Override
    public synchronized void setNeedClientAuth(final boolean need) {
        this.conContext.sslConfig.clientAuthType = (need ? ClientAuthType.CLIENT_AUTH_REQUIRED : ClientAuthType.CLIENT_AUTH_NONE);
    }
    
    @Override
    public synchronized boolean getNeedClientAuth() {
        return this.conContext.sslConfig.clientAuthType == ClientAuthType.CLIENT_AUTH_REQUIRED;
    }
    
    @Override
    public synchronized void setWantClientAuth(final boolean want) {
        this.conContext.sslConfig.clientAuthType = (want ? ClientAuthType.CLIENT_AUTH_REQUESTED : ClientAuthType.CLIENT_AUTH_NONE);
    }
    
    @Override
    public synchronized boolean getWantClientAuth() {
        return this.conContext.sslConfig.clientAuthType == ClientAuthType.CLIENT_AUTH_REQUESTED;
    }
    
    @Override
    public synchronized void setEnableSessionCreation(final boolean flag) {
        this.conContext.sslConfig.enableSessionCreation = flag;
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
    public synchronized void setSSLParameters(final SSLParameters params) {
        this.conContext.sslConfig.setSSLParameters(params);
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
    
    public synchronized void setHandshakeApplicationProtocolSelector(final BiFunction<javax.net.ssl.SSLEngine, List<String>, String> selector) {
        this.conContext.sslConfig.engineAPSelector = selector;
    }
    
    public synchronized BiFunction<javax.net.ssl.SSLEngine, List<String>, String> getHandshakeApplicationProtocolSelector() {
        return this.conContext.sslConfig.engineAPSelector;
    }
    
    @Override
    public boolean useDelegatedTask() {
        return true;
    }
    
    private synchronized void checkTaskThrown() throws SSLException {
        Exception exc = null;
        final HandshakeContext hc = this.conContext.handshakeContext;
        if (hc != null && hc.delegatedThrown != null) {
            exc = hc.delegatedThrown;
            hc.delegatedThrown = null;
        }
        if (this.conContext.delegatedThrown != null) {
            if (exc != null) {
                if (this.conContext.delegatedThrown == exc) {
                    this.conContext.delegatedThrown = null;
                }
            }
            else {
                exc = this.conContext.delegatedThrown;
                this.conContext.delegatedThrown = null;
            }
        }
        if (exc == null) {
            return;
        }
        if (exc instanceof SSLException) {
            throw (SSLException)exc;
        }
        if (exc instanceof RuntimeException) {
            throw (RuntimeException)exc;
        }
        throw getTaskThrown(exc);
    }
    
    private static SSLException getTaskThrown(final Exception taskThrown) {
        String msg = taskThrown.getMessage();
        if (msg == null) {
            msg = "Delegated task threw Exception or Error";
        }
        if (taskThrown instanceof RuntimeException) {
            throw new RuntimeException(msg, taskThrown);
        }
        if (taskThrown instanceof SSLHandshakeException) {
            return (SSLHandshakeException)new SSLHandshakeException(msg).initCause(taskThrown);
        }
        if (taskThrown instanceof SSLKeyException) {
            return (SSLKeyException)new SSLKeyException(msg).initCause(taskThrown);
        }
        if (taskThrown instanceof SSLPeerUnverifiedException) {
            return (SSLPeerUnverifiedException)new SSLPeerUnverifiedException(msg).initCause(taskThrown);
        }
        if (taskThrown instanceof SSLProtocolException) {
            return (SSLProtocolException)new SSLProtocolException(msg).initCause(taskThrown);
        }
        if (taskThrown instanceof SSLException) {
            return (SSLException)taskThrown;
        }
        return new SSLException(msg, taskThrown);
    }
    
    private static class DelegatedTask implements Runnable
    {
        private final SSLEngineImpl engine;
        
        DelegatedTask(final SSLEngineImpl engineInstance) {
            this.engine = engineInstance;
        }
        
        @Override
        public void run() {
            synchronized (this.engine) {
                HandshakeContext hc = this.engine.conContext.handshakeContext;
                if (hc == null || hc.delegatedActions.isEmpty()) {
                    return;
                }
                try {
                    AccessController.doPrivileged((PrivilegedExceptionAction<Object>)new DelegatedAction(hc), this.engine.conContext.acc);
                }
                catch (final PrivilegedActionException pae) {
                    final Exception reportedException = pae.getException();
                    if (this.engine.conContext.delegatedThrown == null) {
                        this.engine.conContext.delegatedThrown = reportedException;
                    }
                    hc = this.engine.conContext.handshakeContext;
                    if (hc != null) {
                        hc.delegatedThrown = reportedException;
                    }
                    else if (this.engine.conContext.closeReason != null) {
                        this.engine.conContext.closeReason = getTaskThrown(reportedException);
                    }
                }
                catch (final RuntimeException rte) {
                    if (this.engine.conContext.delegatedThrown == null) {
                        this.engine.conContext.delegatedThrown = rte;
                    }
                    hc = this.engine.conContext.handshakeContext;
                    if (hc != null) {
                        hc.delegatedThrown = rte;
                    }
                    else if (this.engine.conContext.closeReason != null) {
                        this.engine.conContext.closeReason = rte;
                    }
                }
                hc = this.engine.conContext.handshakeContext;
                if (hc != null) {
                    hc.taskDelegated = false;
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
                    final Map.Entry<Byte, ByteBuffer> me = this.context.delegatedActions.poll();
                    if (me != null) {
                        this.context.dispatch(me.getKey(), me.getValue());
                    }
                }
                return null;
            }
        }
    }
}
