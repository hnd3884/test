package io.netty.handler.ssl;

import javax.net.ssl.SSLSessionContext;
import java.security.Principal;
import javax.net.ssl.SSLPeerUnverifiedException;
import io.netty.handler.ssl.util.LazyJavaxX509Certificate;
import io.netty.handler.ssl.util.LazyX509Certificate;
import javax.net.ssl.SSLSessionBindingListener;
import java.util.HashMap;
import javax.net.ssl.SSLSessionBindingEvent;
import java.util.Map;
import javax.security.cert.X509Certificate;
import io.netty.util.ResourceLeakDetectorFactory;
import io.netty.util.internal.logging.InternalLoggerFactory;
import io.netty.internal.tcnative.Buffer;
import java.util.Iterator;
import io.netty.util.internal.SuppressJava6Requirement;
import javax.net.ssl.SSLParameters;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Arrays;
import io.netty.internal.tcnative.AsyncTask;
import io.netty.util.internal.ThrowableUtil;
import javax.net.ssl.SSLHandshakeException;
import java.nio.ReadOnlyBufferException;
import javax.net.ssl.SSLException;
import io.netty.buffer.ByteBuf;
import javax.net.ssl.SSLSession;
import javax.crypto.spec.SecretKeySpec;
import java.security.cert.Certificate;
import java.util.concurrent.locks.Lock;
import java.util.Set;
import java.util.LinkedHashSet;
import io.netty.util.internal.EmptyArrays;
import io.netty.util.CharsetUtil;
import io.netty.internal.tcnative.SSL;
import java.util.Collections;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.ObjectUtil;
import java.nio.ByteBuffer;
import io.netty.buffer.ByteBufAllocator;
import java.util.Collection;
import java.util.List;
import io.netty.util.AbstractReferenceCounted;
import io.netty.util.ResourceLeakTracker;
import javax.net.ssl.SSLEngineResult;
import io.netty.util.ResourceLeakDetector;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.ReferenceCounted;
import javax.net.ssl.SSLEngine;

public class ReferenceCountedOpenSslEngine extends SSLEngine implements ReferenceCounted, ApplicationProtocolAccessor
{
    private static final InternalLogger logger;
    private static final ResourceLeakDetector<ReferenceCountedOpenSslEngine> leakDetector;
    private static final int OPENSSL_OP_NO_PROTOCOL_INDEX_SSLV2 = 0;
    private static final int OPENSSL_OP_NO_PROTOCOL_INDEX_SSLV3 = 1;
    private static final int OPENSSL_OP_NO_PROTOCOL_INDEX_TLSv1 = 2;
    private static final int OPENSSL_OP_NO_PROTOCOL_INDEX_TLSv1_1 = 3;
    private static final int OPENSSL_OP_NO_PROTOCOL_INDEX_TLSv1_2 = 4;
    private static final int OPENSSL_OP_NO_PROTOCOL_INDEX_TLSv1_3 = 5;
    private static final int[] OPENSSL_OP_NO_PROTOCOLS;
    static final int MAX_PLAINTEXT_LENGTH;
    static final int MAX_RECORD_SIZE;
    private static final SSLEngineResult NEED_UNWRAP_OK;
    private static final SSLEngineResult NEED_UNWRAP_CLOSED;
    private static final SSLEngineResult NEED_WRAP_OK;
    private static final SSLEngineResult NEED_WRAP_CLOSED;
    private static final SSLEngineResult CLOSED_NOT_HANDSHAKING;
    private long ssl;
    private long networkBIO;
    private HandshakeState handshakeState;
    private boolean receivedShutdown;
    private volatile boolean destroyed;
    private volatile String applicationProtocol;
    private volatile boolean needTask;
    private String[] explicitlyEnabledProtocols;
    private boolean sessionSet;
    private final ResourceLeakTracker<ReferenceCountedOpenSslEngine> leak;
    private final AbstractReferenceCounted refCnt;
    private volatile ClientAuth clientAuth;
    private volatile long lastAccessed;
    private String endPointIdentificationAlgorithm;
    private Object algorithmConstraints;
    private List<String> sniHostNames;
    private volatile Collection<?> matchers;
    private boolean isInboundDone;
    private boolean outboundClosed;
    final boolean jdkCompatibilityMode;
    private final boolean clientMode;
    final ByteBufAllocator alloc;
    private final OpenSslEngineMap engineMap;
    private final OpenSslApplicationProtocolNegotiator apn;
    private final ReferenceCountedOpenSslContext parentContext;
    private final OpenSslSession session;
    private final ByteBuffer[] singleSrcBuffer;
    private final ByteBuffer[] singleDstBuffer;
    private final boolean enableOcsp;
    private int maxWrapOverhead;
    private int maxWrapBufferSize;
    private Throwable pendingException;
    
    ReferenceCountedOpenSslEngine(final ReferenceCountedOpenSslContext context, final ByteBufAllocator alloc, final String peerHost, final int peerPort, final boolean jdkCompatibilityMode, final boolean leakDetection) {
        super(peerHost, peerPort);
        this.handshakeState = HandshakeState.NOT_STARTED;
        this.refCnt = new AbstractReferenceCounted() {
            @Override
            public ReferenceCounted touch(final Object hint) {
                if (ReferenceCountedOpenSslEngine.this.leak != null) {
                    ReferenceCountedOpenSslEngine.this.leak.record(hint);
                }
                return ReferenceCountedOpenSslEngine.this;
            }
            
            @Override
            protected void deallocate() {
                ReferenceCountedOpenSslEngine.this.shutdown();
                if (ReferenceCountedOpenSslEngine.this.leak != null) {
                    final boolean closed = ReferenceCountedOpenSslEngine.this.leak.close(ReferenceCountedOpenSslEngine.this);
                    assert closed;
                }
                ReferenceCountedOpenSslEngine.this.parentContext.release();
            }
        };
        this.clientAuth = ClientAuth.NONE;
        this.lastAccessed = -1L;
        this.singleSrcBuffer = new ByteBuffer[1];
        this.singleDstBuffer = new ByteBuffer[1];
        OpenSsl.ensureAvailability();
        this.alloc = ObjectUtil.checkNotNull(alloc, "alloc");
        this.apn = (OpenSslApplicationProtocolNegotiator)context.applicationProtocolNegotiator();
        this.clientMode = context.isClient();
        if (PlatformDependent.javaVersion() >= 7) {
            this.session = new ExtendedOpenSslSession(new DefaultOpenSslSession(context.sessionContext())) {
                private String[] peerSupportedSignatureAlgorithms;
                private List requestedServerNames;
                
                @Override
                public List getRequestedServerNames() {
                    if (ReferenceCountedOpenSslEngine.this.clientMode) {
                        return Java8SslUtils.getSniHostNames(ReferenceCountedOpenSslEngine.this.sniHostNames);
                    }
                    synchronized (ReferenceCountedOpenSslEngine.this) {
                        if (this.requestedServerNames == null) {
                            if (ReferenceCountedOpenSslEngine.this.isDestroyed()) {
                                this.requestedServerNames = Collections.emptyList();
                            }
                            else {
                                final String name = SSL.getSniHostname(ReferenceCountedOpenSslEngine.this.ssl);
                                if (name == null) {
                                    this.requestedServerNames = Collections.emptyList();
                                }
                                else {
                                    this.requestedServerNames = Java8SslUtils.getSniHostName(SSL.getSniHostname(ReferenceCountedOpenSslEngine.this.ssl).getBytes(CharsetUtil.UTF_8));
                                }
                            }
                        }
                        return this.requestedServerNames;
                    }
                }
                
                @Override
                public String[] getPeerSupportedSignatureAlgorithms() {
                    synchronized (ReferenceCountedOpenSslEngine.this) {
                        if (this.peerSupportedSignatureAlgorithms == null) {
                            if (ReferenceCountedOpenSslEngine.this.isDestroyed()) {
                                this.peerSupportedSignatureAlgorithms = EmptyArrays.EMPTY_STRINGS;
                            }
                            else {
                                final String[] algs = SSL.getSigAlgs(ReferenceCountedOpenSslEngine.this.ssl);
                                if (algs == null) {
                                    this.peerSupportedSignatureAlgorithms = EmptyArrays.EMPTY_STRINGS;
                                }
                                else {
                                    final Set<String> algorithmList = new LinkedHashSet<String>(algs.length);
                                    for (final String alg : algs) {
                                        final String converted = SignatureAlgorithmConverter.toJavaName(alg);
                                        if (converted != null) {
                                            algorithmList.add(converted);
                                        }
                                    }
                                    this.peerSupportedSignatureAlgorithms = algorithmList.toArray(new String[0]);
                                }
                            }
                        }
                        return this.peerSupportedSignatureAlgorithms.clone();
                    }
                }
                
                @Override
                public List<byte[]> getStatusResponses() {
                    byte[] ocspResponse = null;
                    if (ReferenceCountedOpenSslEngine.this.enableOcsp && ReferenceCountedOpenSslEngine.this.clientMode) {
                        synchronized (ReferenceCountedOpenSslEngine.this) {
                            if (!ReferenceCountedOpenSslEngine.this.isDestroyed()) {
                                ocspResponse = SSL.getOcspResponse(ReferenceCountedOpenSslEngine.this.ssl);
                            }
                        }
                    }
                    return (ocspResponse == null) ? Collections.emptyList() : Collections.singletonList(ocspResponse);
                }
            };
        }
        else {
            this.session = new DefaultOpenSslSession(context.sessionContext());
        }
        this.engineMap = context.engineMap;
        this.enableOcsp = context.enableOcsp;
        if (!context.sessionContext().useKeyManager()) {
            this.session.setLocalCertificate(context.keyCertChain);
        }
        this.jdkCompatibilityMode = jdkCompatibilityMode;
        final Lock readerLock = context.ctxLock.readLock();
        readerLock.lock();
        long finalSsl;
        try {
            finalSsl = SSL.newSSL(context.ctx, !context.isClient());
        }
        finally {
            readerLock.unlock();
        }
        synchronized (this) {
            this.ssl = finalSsl;
            try {
                this.networkBIO = SSL.bioNewByteBuffer(this.ssl, context.getBioNonApplicationBufferSize());
                this.setClientAuth(this.clientMode ? ClientAuth.NONE : context.clientAuth);
                if (context.protocols != null) {
                    this.setEnabledProtocols0(context.protocols, true);
                }
                else {
                    this.explicitlyEnabledProtocols = this.getEnabledProtocols();
                }
                if (this.clientMode && SslUtils.isValidHostNameForSNI(peerHost)) {
                    SSL.setTlsExtHostName(this.ssl, peerHost);
                    this.sniHostNames = Collections.singletonList(peerHost);
                }
                if (this.enableOcsp) {
                    SSL.enableOcsp(this.ssl);
                }
                if (!jdkCompatibilityMode) {
                    SSL.setMode(this.ssl, SSL.getMode(this.ssl) | SSL.SSL_MODE_ENABLE_PARTIAL_WRITE);
                }
                if (isProtocolEnabled(SSL.getOptions(this.ssl), SSL.SSL_OP_NO_TLSv1_3, "TLSv1.3")) {
                    final boolean enableTickets = this.clientMode ? ReferenceCountedOpenSslContext.CLIENT_ENABLE_SESSION_TICKET_TLSV13 : ReferenceCountedOpenSslContext.SERVER_ENABLE_SESSION_TICKET_TLSV13;
                    if (enableTickets) {
                        SSL.clearOptions(this.ssl, SSL.SSL_OP_NO_TICKET);
                    }
                }
                if (OpenSsl.isBoringSSL() && this.clientMode) {
                    SSL.setRenegotiateMode(this.ssl, SSL.SSL_RENEGOTIATE_ONCE);
                }
                this.calculateMaxWrapOverhead();
            }
            catch (final Throwable cause) {
                this.shutdown();
                PlatformDependent.throwException(cause);
            }
        }
        (this.parentContext = context).retain();
        this.leak = (leakDetection ? ReferenceCountedOpenSslEngine.leakDetector.track(this) : null);
    }
    
    final synchronized String[] authMethods() {
        if (this.isDestroyed()) {
            return EmptyArrays.EMPTY_STRINGS;
        }
        return SSL.authenticationMethods(this.ssl);
    }
    
    final boolean setKeyMaterial(final OpenSslKeyMaterial keyMaterial) throws Exception {
        synchronized (this) {
            if (this.isDestroyed()) {
                return false;
            }
            SSL.setKeyMaterial(this.ssl, keyMaterial.certificateChainAddress(), keyMaterial.privateKeyAddress());
        }
        this.session.setLocalCertificate(keyMaterial.certificateChain());
        return true;
    }
    
    final synchronized SecretKeySpec masterKey() {
        if (this.isDestroyed()) {
            return null;
        }
        return new SecretKeySpec(SSL.getMasterKey(this.ssl), "AES");
    }
    
    synchronized boolean isSessionReused() {
        return !this.isDestroyed() && SSL.isSessionReused(this.ssl);
    }
    
    public void setOcspResponse(final byte[] response) {
        if (!this.enableOcsp) {
            throw new IllegalStateException("OCSP stapling is not enabled");
        }
        if (this.clientMode) {
            throw new IllegalStateException("Not a server SSLEngine");
        }
        synchronized (this) {
            if (!this.isDestroyed()) {
                SSL.setOcspResponse(this.ssl, response);
            }
        }
    }
    
    public byte[] getOcspResponse() {
        if (!this.enableOcsp) {
            throw new IllegalStateException("OCSP stapling is not enabled");
        }
        if (!this.clientMode) {
            throw new IllegalStateException("Not a client SSLEngine");
        }
        synchronized (this) {
            if (this.isDestroyed()) {
                return EmptyArrays.EMPTY_BYTES;
            }
            return SSL.getOcspResponse(this.ssl);
        }
    }
    
    @Override
    public final int refCnt() {
        return this.refCnt.refCnt();
    }
    
    @Override
    public final ReferenceCounted retain() {
        this.refCnt.retain();
        return this;
    }
    
    @Override
    public final ReferenceCounted retain(final int increment) {
        this.refCnt.retain(increment);
        return this;
    }
    
    @Override
    public final ReferenceCounted touch() {
        this.refCnt.touch();
        return this;
    }
    
    @Override
    public final ReferenceCounted touch(final Object hint) {
        this.refCnt.touch(hint);
        return this;
    }
    
    @Override
    public final boolean release() {
        return this.refCnt.release();
    }
    
    @Override
    public final boolean release(final int decrement) {
        return this.refCnt.release(decrement);
    }
    
    public String getApplicationProtocol() {
        return this.applicationProtocol;
    }
    
    public String getHandshakeApplicationProtocol() {
        return this.applicationProtocol;
    }
    
    @Override
    public final synchronized SSLSession getHandshakeSession() {
        switch (this.handshakeState) {
            case NOT_STARTED:
            case FINISHED: {
                return null;
            }
            default: {
                return this.session;
            }
        }
    }
    
    public final synchronized long sslPointer() {
        return this.ssl;
    }
    
    public final synchronized void shutdown() {
        if (!this.destroyed) {
            this.destroyed = true;
            this.engineMap.remove(this.ssl);
            SSL.freeSSL(this.ssl);
            final long n = 0L;
            this.networkBIO = n;
            this.ssl = n;
            final boolean b = true;
            this.outboundClosed = b;
            this.isInboundDone = b;
        }
        SSL.clearError();
    }
    
    private int writePlaintextData(final ByteBuffer src, final int len) {
        final int pos = src.position();
        final int limit = src.limit();
        int sslWrote;
        if (src.isDirect()) {
            sslWrote = SSL.writeToSSL(this.ssl, bufferAddress(src) + pos, len);
            if (sslWrote > 0) {
                src.position(pos + sslWrote);
            }
        }
        else {
            final ByteBuf buf = this.alloc.directBuffer(len);
            try {
                src.limit(pos + len);
                buf.setBytes(0, src);
                src.limit(limit);
                sslWrote = SSL.writeToSSL(this.ssl, OpenSsl.memoryAddress(buf), len);
                if (sslWrote > 0) {
                    src.position(pos + sslWrote);
                }
                else {
                    src.position(pos);
                }
            }
            finally {
                buf.release();
            }
        }
        return sslWrote;
    }
    
    private ByteBuf writeEncryptedData(final ByteBuffer src, final int len) throws SSLException {
        final int pos = src.position();
        if (src.isDirect()) {
            SSL.bioSetByteBuffer(this.networkBIO, bufferAddress(src) + pos, len, false);
        }
        else {
            final ByteBuf buf = this.alloc.directBuffer(len);
            try {
                final int limit = src.limit();
                src.limit(pos + len);
                buf.writeBytes(src);
                src.position(pos);
                src.limit(limit);
                SSL.bioSetByteBuffer(this.networkBIO, OpenSsl.memoryAddress(buf), len, false);
                return buf;
            }
            catch (final Throwable cause) {
                buf.release();
                PlatformDependent.throwException(cause);
            }
        }
        return null;
    }
    
    private int readPlaintextData(final ByteBuffer dst) throws SSLException {
        final int pos = dst.position();
        int sslRead;
        if (dst.isDirect()) {
            sslRead = SSL.readFromSSL(this.ssl, bufferAddress(dst) + pos, dst.limit() - pos);
            if (sslRead > 0) {
                dst.position(pos + sslRead);
            }
        }
        else {
            final int limit = dst.limit();
            final int len = Math.min(this.maxEncryptedPacketLength0(), limit - pos);
            final ByteBuf buf = this.alloc.directBuffer(len);
            try {
                sslRead = SSL.readFromSSL(this.ssl, OpenSsl.memoryAddress(buf), len);
                if (sslRead > 0) {
                    dst.limit(pos + sslRead);
                    buf.getBytes(buf.readerIndex(), dst);
                    dst.limit(limit);
                }
            }
            finally {
                buf.release();
            }
        }
        return sslRead;
    }
    
    final synchronized int maxWrapOverhead() {
        return this.maxWrapOverhead;
    }
    
    final synchronized int maxEncryptedPacketLength() {
        return this.maxEncryptedPacketLength0();
    }
    
    final int maxEncryptedPacketLength0() {
        return this.maxWrapOverhead + ReferenceCountedOpenSslEngine.MAX_PLAINTEXT_LENGTH;
    }
    
    final int calculateMaxLengthForWrap(final int plaintextLength, final int numComponents) {
        return (int)Math.min(this.maxWrapBufferSize, plaintextLength + this.maxWrapOverhead * (long)numComponents);
    }
    
    final synchronized int sslPending() {
        return this.sslPending0();
    }
    
    private void calculateMaxWrapOverhead() {
        this.maxWrapOverhead = SSL.getMaxWrapOverhead(this.ssl);
        this.maxWrapBufferSize = (this.jdkCompatibilityMode ? this.maxEncryptedPacketLength0() : (this.maxEncryptedPacketLength0() << 4));
    }
    
    private int sslPending0() {
        return (this.handshakeState != HandshakeState.FINISHED) ? 0 : SSL.sslPending(this.ssl);
    }
    
    private boolean isBytesAvailableEnoughForWrap(final int bytesAvailable, final int plaintextLength, final int numComponents) {
        return bytesAvailable - this.maxWrapOverhead * (long)numComponents >= plaintextLength;
    }
    
    @Override
    public final SSLEngineResult wrap(final ByteBuffer[] srcs, int offset, final int length, final ByteBuffer dst) throws SSLException {
        ObjectUtil.checkNotNullWithIAE(srcs, "srcs");
        ObjectUtil.checkNotNullWithIAE(dst, "dst");
        if (offset >= srcs.length || offset + length > srcs.length) {
            throw new IndexOutOfBoundsException("offset: " + offset + ", length: " + length + " (expected: offset <= offset + length <= srcs.length (" + srcs.length + "))");
        }
        if (dst.isReadOnly()) {
            throw new ReadOnlyBufferException();
        }
        synchronized (this) {
            if (this.isOutboundDone()) {
                return (this.isInboundDone() || this.isDestroyed()) ? ReferenceCountedOpenSslEngine.CLOSED_NOT_HANDSHAKING : ReferenceCountedOpenSslEngine.NEED_UNWRAP_CLOSED;
            }
            int bytesProduced = 0;
            ByteBuf bioReadCopyBuf = null;
            try {
                if (dst.isDirect()) {
                    SSL.bioSetByteBuffer(this.networkBIO, bufferAddress(dst) + dst.position(), dst.remaining(), true);
                }
                else {
                    bioReadCopyBuf = this.alloc.directBuffer(dst.remaining());
                    SSL.bioSetByteBuffer(this.networkBIO, OpenSsl.memoryAddress(bioReadCopyBuf), bioReadCopyBuf.writableBytes(), true);
                }
                int bioLengthBefore = SSL.bioLengthByteBuffer(this.networkBIO);
                if (this.outboundClosed) {
                    if (!this.isBytesAvailableEnoughForWrap(dst.remaining(), 2, 1)) {
                        return new SSLEngineResult(SSLEngineResult.Status.BUFFER_OVERFLOW, this.getHandshakeStatus(), 0, 0);
                    }
                    bytesProduced = SSL.bioFlushByteBuffer(this.networkBIO);
                    if (bytesProduced <= 0) {
                        return this.newResultMayFinishHandshake(SSLEngineResult.HandshakeStatus.NOT_HANDSHAKING, 0, 0);
                    }
                    if (!this.doSSLShutdown()) {
                        return this.newResultMayFinishHandshake(SSLEngineResult.HandshakeStatus.NOT_HANDSHAKING, 0, bytesProduced);
                    }
                    bytesProduced = bioLengthBefore - SSL.bioLengthByteBuffer(this.networkBIO);
                    return this.newResultMayFinishHandshake(SSLEngineResult.HandshakeStatus.NEED_WRAP, 0, bytesProduced);
                }
                else {
                    SSLEngineResult.HandshakeStatus status = SSLEngineResult.HandshakeStatus.NOT_HANDSHAKING;
                    if (this.handshakeState != HandshakeState.FINISHED) {
                        if (this.handshakeState != HandshakeState.STARTED_EXPLICITLY) {
                            this.handshakeState = HandshakeState.STARTED_IMPLICITLY;
                        }
                        bytesProduced = SSL.bioFlushByteBuffer(this.networkBIO);
                        if (this.pendingException != null) {
                            if (bytesProduced > 0) {
                                return this.newResult(SSLEngineResult.HandshakeStatus.NEED_WRAP, 0, bytesProduced);
                            }
                            return this.newResult(this.handshakeException(), 0, 0);
                        }
                        else {
                            status = this.handshake();
                            bytesProduced = bioLengthBefore - SSL.bioLengthByteBuffer(this.networkBIO);
                            if (status == SSLEngineResult.HandshakeStatus.NEED_TASK) {
                                return this.newResult(status, 0, bytesProduced);
                            }
                            if (bytesProduced > 0) {
                                return this.newResult(this.mayFinishHandshake((status != SSLEngineResult.HandshakeStatus.FINISHED) ? ((bytesProduced == bioLengthBefore) ? SSLEngineResult.HandshakeStatus.NEED_WRAP : this.getHandshakeStatus(SSL.bioLengthNonApplication(this.networkBIO))) : SSLEngineResult.HandshakeStatus.FINISHED), 0, bytesProduced);
                            }
                            if (status == SSLEngineResult.HandshakeStatus.NEED_UNWRAP) {
                                return this.isOutboundDone() ? ReferenceCountedOpenSslEngine.NEED_UNWRAP_CLOSED : ReferenceCountedOpenSslEngine.NEED_UNWRAP_OK;
                            }
                            if (this.outboundClosed) {
                                bytesProduced = SSL.bioFlushByteBuffer(this.networkBIO);
                                return this.newResultMayFinishHandshake(status, 0, bytesProduced);
                            }
                        }
                    }
                    final int endOffset = offset + length;
                    if (this.jdkCompatibilityMode) {
                        int srcsLen = 0;
                        for (int i = offset; i < endOffset; ++i) {
                            final ByteBuffer src = srcs[i];
                            if (src == null) {
                                throw new IllegalArgumentException("srcs[" + i + "] is null");
                            }
                            if (srcsLen != ReferenceCountedOpenSslEngine.MAX_PLAINTEXT_LENGTH) {
                                srcsLen += src.remaining();
                                if (srcsLen > ReferenceCountedOpenSslEngine.MAX_PLAINTEXT_LENGTH || srcsLen < 0) {
                                    srcsLen = ReferenceCountedOpenSslEngine.MAX_PLAINTEXT_LENGTH;
                                }
                            }
                        }
                        if (!this.isBytesAvailableEnoughForWrap(dst.remaining(), srcsLen, 1)) {
                            return new SSLEngineResult(SSLEngineResult.Status.BUFFER_OVERFLOW, this.getHandshakeStatus(), 0, 0);
                        }
                    }
                    int bytesConsumed = 0;
                    assert bytesProduced == 0;
                    bytesProduced = SSL.bioFlushByteBuffer(this.networkBIO);
                    if (bytesProduced > 0) {
                        return this.newResultMayFinishHandshake(status, bytesConsumed, bytesProduced);
                    }
                    if (this.pendingException != null) {
                        final Throwable error = this.pendingException;
                        this.pendingException = null;
                        this.shutdown();
                        throw new SSLException(error);
                    }
                    while (offset < endOffset) {
                        final ByteBuffer src2 = srcs[offset];
                        final int remaining = src2.remaining();
                        if (remaining != 0) {
                            int bytesWritten;
                            if (this.jdkCompatibilityMode) {
                                bytesWritten = this.writePlaintextData(src2, Math.min(remaining, ReferenceCountedOpenSslEngine.MAX_PLAINTEXT_LENGTH - bytesConsumed));
                            }
                            else {
                                final int availableCapacityForWrap = dst.remaining() - bytesProduced - this.maxWrapOverhead;
                                if (availableCapacityForWrap <= 0) {
                                    return new SSLEngineResult(SSLEngineResult.Status.BUFFER_OVERFLOW, this.getHandshakeStatus(), bytesConsumed, bytesProduced);
                                }
                                bytesWritten = this.writePlaintextData(src2, Math.min(remaining, availableCapacityForWrap));
                            }
                            final int pendingNow = SSL.bioLengthByteBuffer(this.networkBIO);
                            bytesProduced += bioLengthBefore - pendingNow;
                            bioLengthBefore = pendingNow;
                            if (bytesWritten > 0) {
                                bytesConsumed += bytesWritten;
                                if (this.jdkCompatibilityMode || bytesProduced == dst.remaining()) {
                                    return this.newResultMayFinishHandshake(status, bytesConsumed, bytesProduced);
                                }
                            }
                            else {
                                final int sslError = SSL.getError(this.ssl, bytesWritten);
                                if (sslError == SSL.SSL_ERROR_ZERO_RETURN) {
                                    if (!this.receivedShutdown) {
                                        this.closeAll();
                                        bytesProduced += bioLengthBefore - SSL.bioLengthByteBuffer(this.networkBIO);
                                        final SSLEngineResult.HandshakeStatus hs = this.mayFinishHandshake((status != SSLEngineResult.HandshakeStatus.FINISHED) ? ((bytesProduced == dst.remaining()) ? SSLEngineResult.HandshakeStatus.NEED_WRAP : this.getHandshakeStatus(SSL.bioLengthNonApplication(this.networkBIO))) : SSLEngineResult.HandshakeStatus.FINISHED);
                                        return this.newResult(hs, bytesConsumed, bytesProduced);
                                    }
                                    return this.newResult(SSLEngineResult.HandshakeStatus.NOT_HANDSHAKING, bytesConsumed, bytesProduced);
                                }
                                else {
                                    if (sslError == SSL.SSL_ERROR_WANT_READ) {
                                        return this.newResult(SSLEngineResult.HandshakeStatus.NEED_UNWRAP, bytesConsumed, bytesProduced);
                                    }
                                    if (sslError == SSL.SSL_ERROR_WANT_WRITE) {
                                        if (bytesProduced > 0) {
                                            return this.newResult(SSLEngineResult.HandshakeStatus.NEED_WRAP, bytesConsumed, bytesProduced);
                                        }
                                        return this.newResult(SSLEngineResult.Status.BUFFER_OVERFLOW, status, bytesConsumed, bytesProduced);
                                    }
                                    else {
                                        if (sslError == SSL.SSL_ERROR_WANT_X509_LOOKUP || sslError == SSL.SSL_ERROR_WANT_CERTIFICATE_VERIFY || sslError == SSL.SSL_ERROR_WANT_PRIVATE_KEY_OPERATION) {
                                            return this.newResult(SSLEngineResult.HandshakeStatus.NEED_TASK, bytesConsumed, bytesProduced);
                                        }
                                        throw this.shutdownWithError("SSL_write", sslError);
                                    }
                                }
                            }
                        }
                        ++offset;
                    }
                    return this.newResultMayFinishHandshake(status, bytesConsumed, bytesProduced);
                }
            }
            finally {
                SSL.bioClearByteBuffer(this.networkBIO);
                if (bioReadCopyBuf == null) {
                    dst.position(dst.position() + bytesProduced);
                }
                else {
                    assert bioReadCopyBuf.readableBytes() <= dst.remaining() : "The destination buffer " + dst + " didn't have enough remaining space to hold the encrypted content in " + bioReadCopyBuf;
                    dst.put(bioReadCopyBuf.internalNioBuffer(bioReadCopyBuf.readerIndex(), bytesProduced));
                    bioReadCopyBuf.release();
                }
            }
        }
    }
    
    private SSLEngineResult newResult(final SSLEngineResult.HandshakeStatus hs, final int bytesConsumed, final int bytesProduced) {
        return this.newResult(SSLEngineResult.Status.OK, hs, bytesConsumed, bytesProduced);
    }
    
    private SSLEngineResult newResult(final SSLEngineResult.Status status, SSLEngineResult.HandshakeStatus hs, final int bytesConsumed, final int bytesProduced) {
        if (this.isOutboundDone()) {
            if (this.isInboundDone()) {
                hs = SSLEngineResult.HandshakeStatus.NOT_HANDSHAKING;
                this.shutdown();
            }
            return new SSLEngineResult(SSLEngineResult.Status.CLOSED, hs, bytesConsumed, bytesProduced);
        }
        if (hs == SSLEngineResult.HandshakeStatus.NEED_TASK) {
            this.needTask = true;
        }
        return new SSLEngineResult(status, hs, bytesConsumed, bytesProduced);
    }
    
    private SSLEngineResult newResultMayFinishHandshake(final SSLEngineResult.HandshakeStatus hs, final int bytesConsumed, final int bytesProduced) throws SSLException {
        return this.newResult(this.mayFinishHandshake(hs, bytesConsumed, bytesProduced), bytesConsumed, bytesProduced);
    }
    
    private SSLEngineResult newResultMayFinishHandshake(final SSLEngineResult.Status status, final SSLEngineResult.HandshakeStatus hs, final int bytesConsumed, final int bytesProduced) throws SSLException {
        return this.newResult(status, this.mayFinishHandshake(hs, bytesConsumed, bytesProduced), bytesConsumed, bytesProduced);
    }
    
    private SSLException shutdownWithError(final String operations, final int sslError) {
        return this.shutdownWithError(operations, sslError, SSL.getLastErrorNumber());
    }
    
    private SSLException shutdownWithError(final String operation, final int sslError, final int error) {
        final String errorString = SSL.getErrorString((long)error);
        if (ReferenceCountedOpenSslEngine.logger.isDebugEnabled()) {
            ReferenceCountedOpenSslEngine.logger.debug("{} failed with {}: OpenSSL error: {} {}", operation, sslError, error, errorString);
        }
        this.shutdown();
        if (this.handshakeState == HandshakeState.FINISHED) {
            return new SSLException(errorString);
        }
        final SSLHandshakeException exception = new SSLHandshakeException(errorString);
        if (this.pendingException != null) {
            exception.initCause(this.pendingException);
            this.pendingException = null;
        }
        return exception;
    }
    
    private SSLEngineResult handleUnwrapException(final int bytesConsumed, final int bytesProduced, final SSLException e) throws SSLException {
        final int lastError = SSL.getLastErrorNumber();
        if (lastError != 0) {
            return this.sslReadErrorResult(SSL.SSL_ERROR_SSL, lastError, bytesConsumed, bytesProduced);
        }
        throw e;
    }
    
    public final SSLEngineResult unwrap(final ByteBuffer[] srcs, int srcsOffset, final int srcsLength, final ByteBuffer[] dsts, int dstsOffset, final int dstsLength) throws SSLException {
        ObjectUtil.checkNotNullWithIAE(srcs, "srcs");
        if (srcsOffset >= srcs.length || srcsOffset + srcsLength > srcs.length) {
            throw new IndexOutOfBoundsException("offset: " + srcsOffset + ", length: " + srcsLength + " (expected: offset <= offset + length <= srcs.length (" + srcs.length + "))");
        }
        ObjectUtil.checkNotNullWithIAE(dsts, "dsts");
        if (dstsOffset >= dsts.length || dstsOffset + dstsLength > dsts.length) {
            throw new IndexOutOfBoundsException("offset: " + dstsOffset + ", length: " + dstsLength + " (expected: offset <= offset + length <= dsts.length (" + dsts.length + "))");
        }
        long capacity = 0L;
        final int dstsEndOffset = dstsOffset + dstsLength;
        for (int i = dstsOffset; i < dstsEndOffset; ++i) {
            final ByteBuffer dst = ObjectUtil.checkNotNullArrayParam(dsts[i], i, "dsts");
            if (dst.isReadOnly()) {
                throw new ReadOnlyBufferException();
            }
            capacity += dst.remaining();
        }
        final int srcsEndOffset = srcsOffset + srcsLength;
        long len = 0L;
        for (int j = srcsOffset; j < srcsEndOffset; ++j) {
            final ByteBuffer src = ObjectUtil.checkNotNullArrayParam(srcs[j], j, "srcs");
            len += src.remaining();
        }
        synchronized (this) {
            if (this.isInboundDone()) {
                return (this.isOutboundDone() || this.isDestroyed()) ? ReferenceCountedOpenSslEngine.CLOSED_NOT_HANDSHAKING : ReferenceCountedOpenSslEngine.NEED_WRAP_CLOSED;
            }
            SSLEngineResult.HandshakeStatus status = SSLEngineResult.HandshakeStatus.NOT_HANDSHAKING;
            if (this.handshakeState != HandshakeState.FINISHED) {
                if (this.handshakeState != HandshakeState.STARTED_EXPLICITLY) {
                    this.handshakeState = HandshakeState.STARTED_IMPLICITLY;
                }
                status = this.handshake();
                if (status == SSLEngineResult.HandshakeStatus.NEED_TASK) {
                    return this.newResult(status, 0, 0);
                }
                if (status == SSLEngineResult.HandshakeStatus.NEED_WRAP) {
                    return ReferenceCountedOpenSslEngine.NEED_WRAP_OK;
                }
                if (this.isInboundDone) {
                    return ReferenceCountedOpenSslEngine.NEED_WRAP_CLOSED;
                }
            }
            int sslPending = this.sslPending0();
            int packetLength;
            if (this.jdkCompatibilityMode) {
                if (len < 5L) {
                    return this.newResultMayFinishHandshake(SSLEngineResult.Status.BUFFER_UNDERFLOW, status, 0, 0);
                }
                packetLength = SslUtils.getEncryptedPacketLength(srcs, srcsOffset);
                if (packetLength == -2) {
                    throw new NotSslRecordException("not an SSL/TLS record");
                }
                final int packetLengthDataOnly = packetLength - 5;
                if (packetLengthDataOnly > capacity) {
                    if (packetLengthDataOnly > ReferenceCountedOpenSslEngine.MAX_RECORD_SIZE) {
                        throw new SSLException("Illegal packet length: " + packetLengthDataOnly + " > " + this.session.getApplicationBufferSize());
                    }
                    this.session.tryExpandApplicationBufferSize(packetLengthDataOnly);
                    return this.newResultMayFinishHandshake(SSLEngineResult.Status.BUFFER_OVERFLOW, status, 0, 0);
                }
                else if (len < packetLength) {
                    return this.newResultMayFinishHandshake(SSLEngineResult.Status.BUFFER_UNDERFLOW, status, 0, 0);
                }
            }
            else {
                if (len == 0L && sslPending <= 0) {
                    return this.newResultMayFinishHandshake(SSLEngineResult.Status.BUFFER_UNDERFLOW, status, 0, 0);
                }
                if (capacity == 0L) {
                    return this.newResultMayFinishHandshake(SSLEngineResult.Status.BUFFER_OVERFLOW, status, 0, 0);
                }
                packetLength = (int)Math.min(2147483647L, len);
            }
            assert srcsOffset < srcsEndOffset;
            assert capacity > 0L;
            int bytesProduced = 0;
            int bytesConsumed = 0;
            try {
            Label_1361:
                while (true) {
                    final ByteBuffer src2 = srcs[srcsOffset];
                    final int remaining = src2.remaining();
                    Label_0795: {
                        ByteBuf bioWriteCopyBuf;
                        int pendingEncryptedBytes;
                        if (remaining == 0) {
                            if (sslPending > 0) {
                                bioWriteCopyBuf = null;
                                pendingEncryptedBytes = SSL.bioLengthByteBuffer(this.networkBIO);
                                break Label_0795;
                            }
                            if (++srcsOffset >= srcsEndOffset) {
                                break;
                            }
                            continue;
                        }
                        else {
                            pendingEncryptedBytes = Math.min(packetLength, remaining);
                            try {
                                bioWriteCopyBuf = this.writeEncryptedData(src2, pendingEncryptedBytes);
                                break Label_0795;
                            }
                            catch (final SSLException e) {
                                return this.handleUnwrapException(bytesConsumed, bytesProduced, e);
                            }
                        }
                        try {
                            while (true) {
                                final ByteBuffer dst2 = dsts[dstsOffset];
                                if (!dst2.hasRemaining()) {
                                    if (++dstsOffset >= dstsEndOffset) {
                                        break Label_1361;
                                    }
                                    continue;
                                }
                                else {
                                    int bytesRead;
                                    try {
                                        bytesRead = this.readPlaintextData(dst2);
                                    }
                                    catch (final SSLException e2) {
                                        return this.handleUnwrapException(bytesConsumed, bytesProduced, e2);
                                    }
                                    final int localBytesConsumed = pendingEncryptedBytes - SSL.bioLengthByteBuffer(this.networkBIO);
                                    bytesConsumed += localBytesConsumed;
                                    packetLength -= localBytesConsumed;
                                    pendingEncryptedBytes -= localBytesConsumed;
                                    src2.position(src2.position() + localBytesConsumed);
                                    if (bytesRead > 0) {
                                        bytesProduced += bytesRead;
                                        if (!dst2.hasRemaining()) {
                                            sslPending = this.sslPending0();
                                            if (++dstsOffset >= dstsEndOffset) {
                                                return (sslPending > 0) ? this.newResult(SSLEngineResult.Status.BUFFER_OVERFLOW, status, bytesConsumed, bytesProduced) : this.newResultMayFinishHandshake(this.isInboundDone() ? SSLEngineResult.Status.CLOSED : SSLEngineResult.Status.OK, status, bytesConsumed, bytesProduced);
                                            }
                                            continue;
                                        }
                                        else {
                                            if (packetLength == 0 || this.jdkCompatibilityMode) {
                                                break Label_1361;
                                            }
                                            continue;
                                        }
                                    }
                                    else {
                                        final int sslError = SSL.getError(this.ssl, bytesRead);
                                        if (sslError != SSL.SSL_ERROR_WANT_READ && sslError != SSL.SSL_ERROR_WANT_WRITE) {
                                            if (sslError == SSL.SSL_ERROR_ZERO_RETURN) {
                                                if (!this.receivedShutdown) {
                                                    this.closeAll();
                                                }
                                                return this.newResultMayFinishHandshake(this.isInboundDone() ? SSLEngineResult.Status.CLOSED : SSLEngineResult.Status.OK, status, bytesConsumed, bytesProduced);
                                            }
                                            if (sslError == SSL.SSL_ERROR_WANT_X509_LOOKUP || sslError == SSL.SSL_ERROR_WANT_CERTIFICATE_VERIFY || sslError == SSL.SSL_ERROR_WANT_PRIVATE_KEY_OPERATION) {
                                                return this.newResult(this.isInboundDone() ? SSLEngineResult.Status.CLOSED : SSLEngineResult.Status.OK, SSLEngineResult.HandshakeStatus.NEED_TASK, bytesConsumed, bytesProduced);
                                            }
                                            return this.sslReadErrorResult(sslError, SSL.getLastErrorNumber(), bytesConsumed, bytesProduced);
                                        }
                                        else {
                                            if (++srcsOffset >= srcsEndOffset) {
                                                break Label_1361;
                                            }
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                        finally {
                            if (bioWriteCopyBuf != null) {
                                bioWriteCopyBuf.release();
                            }
                        }
                    }
                }
            }
            finally {
                SSL.bioClearByteBuffer(this.networkBIO);
                this.rejectRemoteInitiatedRenegotiation();
            }
            if (!this.receivedShutdown && (SSL.getShutdown(this.ssl) & SSL.SSL_RECEIVED_SHUTDOWN) == SSL.SSL_RECEIVED_SHUTDOWN) {
                this.closeAll();
            }
            return this.newResultMayFinishHandshake(this.isInboundDone() ? SSLEngineResult.Status.CLOSED : SSLEngineResult.Status.OK, status, bytesConsumed, bytesProduced);
        }
    }
    
    private boolean needWrapAgain(final int stackError) {
        if (SSL.bioLengthNonApplication(this.networkBIO) > 0) {
            final String message = SSL.getErrorString((long)stackError);
            final SSLException exception = (this.handshakeState == HandshakeState.FINISHED) ? new SSLException(message) : new SSLHandshakeException(message);
            if (this.pendingException == null) {
                this.pendingException = exception;
            }
            else {
                ThrowableUtil.addSuppressed(this.pendingException, exception);
            }
            SSL.clearError();
            return true;
        }
        return false;
    }
    
    private SSLEngineResult sslReadErrorResult(final int error, final int stackError, final int bytesConsumed, final int bytesProduced) throws SSLException {
        if (this.needWrapAgain(stackError)) {
            return new SSLEngineResult(SSLEngineResult.Status.OK, SSLEngineResult.HandshakeStatus.NEED_WRAP, bytesConsumed, bytesProduced);
        }
        throw this.shutdownWithError("SSL_read", error, stackError);
    }
    
    private void closeAll() throws SSLException {
        this.receivedShutdown = true;
        this.closeOutbound();
        this.closeInbound();
    }
    
    private void rejectRemoteInitiatedRenegotiation() throws SSLHandshakeException {
        if (!this.isDestroyed() && ((!this.clientMode && SSL.getHandshakeCount(this.ssl) > 1) || (this.clientMode && SSL.getHandshakeCount(this.ssl) > 2)) && !"TLSv1.3".equals(this.session.getProtocol()) && this.handshakeState == HandshakeState.FINISHED) {
            this.shutdown();
            throw new SSLHandshakeException("remote-initiated renegotiation not allowed");
        }
    }
    
    public final SSLEngineResult unwrap(final ByteBuffer[] srcs, final ByteBuffer[] dsts) throws SSLException {
        return this.unwrap(srcs, 0, srcs.length, dsts, 0, dsts.length);
    }
    
    private ByteBuffer[] singleSrcBuffer(final ByteBuffer src) {
        this.singleSrcBuffer[0] = src;
        return this.singleSrcBuffer;
    }
    
    private void resetSingleSrcBuffer() {
        this.singleSrcBuffer[0] = null;
    }
    
    private ByteBuffer[] singleDstBuffer(final ByteBuffer src) {
        this.singleDstBuffer[0] = src;
        return this.singleDstBuffer;
    }
    
    private void resetSingleDstBuffer() {
        this.singleDstBuffer[0] = null;
    }
    
    @Override
    public final synchronized SSLEngineResult unwrap(final ByteBuffer src, final ByteBuffer[] dsts, final int offset, final int length) throws SSLException {
        try {
            return this.unwrap(this.singleSrcBuffer(src), 0, 1, dsts, offset, length);
        }
        finally {
            this.resetSingleSrcBuffer();
        }
    }
    
    @Override
    public final synchronized SSLEngineResult wrap(final ByteBuffer src, final ByteBuffer dst) throws SSLException {
        try {
            return this.wrap(this.singleSrcBuffer(src), dst);
        }
        finally {
            this.resetSingleSrcBuffer();
        }
    }
    
    @Override
    public final synchronized SSLEngineResult unwrap(final ByteBuffer src, final ByteBuffer dst) throws SSLException {
        try {
            return this.unwrap(this.singleSrcBuffer(src), this.singleDstBuffer(dst));
        }
        finally {
            this.resetSingleSrcBuffer();
            this.resetSingleDstBuffer();
        }
    }
    
    @Override
    public final synchronized SSLEngineResult unwrap(final ByteBuffer src, final ByteBuffer[] dsts) throws SSLException {
        try {
            return this.unwrap(this.singleSrcBuffer(src), dsts);
        }
        finally {
            this.resetSingleSrcBuffer();
        }
    }
    
    @Override
    public final synchronized Runnable getDelegatedTask() {
        if (this.isDestroyed()) {
            return null;
        }
        final Runnable task = SSL.getTask(this.ssl);
        if (task == null) {
            return null;
        }
        if (task instanceof AsyncTask) {
            return new AsyncTaskDecorator((AsyncTask)task);
        }
        return new TaskDecorator<Object>(task);
    }
    
    @Override
    public final synchronized void closeInbound() throws SSLException {
        if (this.isInboundDone) {
            return;
        }
        this.isInboundDone = true;
        if (this.isOutboundDone()) {
            this.shutdown();
        }
        if (this.handshakeState != HandshakeState.NOT_STARTED && !this.receivedShutdown) {
            throw new SSLException("Inbound closed before receiving peer's close_notify: possible truncation attack?");
        }
    }
    
    @Override
    public final synchronized boolean isInboundDone() {
        return this.isInboundDone;
    }
    
    @Override
    public final synchronized void closeOutbound() {
        if (this.outboundClosed) {
            return;
        }
        this.outboundClosed = true;
        if (this.handshakeState != HandshakeState.NOT_STARTED && !this.isDestroyed()) {
            final int mode = SSL.getShutdown(this.ssl);
            if ((mode & SSL.SSL_SENT_SHUTDOWN) != SSL.SSL_SENT_SHUTDOWN) {
                this.doSSLShutdown();
            }
        }
        else {
            this.shutdown();
        }
    }
    
    private boolean doSSLShutdown() {
        if (SSL.isInInit(this.ssl) != 0) {
            return false;
        }
        final int err = SSL.shutdownSSL(this.ssl);
        if (err < 0) {
            final int sslErr = SSL.getError(this.ssl, err);
            if (sslErr == SSL.SSL_ERROR_SYSCALL || sslErr == SSL.SSL_ERROR_SSL) {
                if (ReferenceCountedOpenSslEngine.logger.isDebugEnabled()) {
                    final int error = SSL.getLastErrorNumber();
                    ReferenceCountedOpenSslEngine.logger.debug("SSL_shutdown failed: OpenSSL error: {} {}", (Object)error, SSL.getErrorString((long)error));
                }
                this.shutdown();
                return false;
            }
            SSL.clearError();
        }
        return true;
    }
    
    @Override
    public final synchronized boolean isOutboundDone() {
        return this.outboundClosed && (this.networkBIO == 0L || SSL.bioLengthNonApplication(this.networkBIO) == 0);
    }
    
    @Override
    public final String[] getSupportedCipherSuites() {
        return OpenSsl.AVAILABLE_CIPHER_SUITES.toArray(new String[0]);
    }
    
    @Override
    public final String[] getEnabledCipherSuites() {
        final String[] enabled;
        String[] extraCiphers;
        boolean tls13Enabled;
        synchronized (this) {
            if (this.isDestroyed()) {
                return EmptyArrays.EMPTY_STRINGS;
            }
            enabled = SSL.getCiphers(this.ssl);
            final int opts = SSL.getOptions(this.ssl);
            if (isProtocolEnabled(opts, SSL.SSL_OP_NO_TLSv1_3, "TLSv1.3")) {
                extraCiphers = OpenSsl.EXTRA_SUPPORTED_TLS_1_3_CIPHERS;
                tls13Enabled = true;
            }
            else {
                extraCiphers = EmptyArrays.EMPTY_STRINGS;
                tls13Enabled = false;
            }
        }
        if (enabled == null) {
            return EmptyArrays.EMPTY_STRINGS;
        }
        final Set<String> enabledSet = new LinkedHashSet<String>(enabled.length + extraCiphers.length);
        synchronized (this) {
            for (int i = 0; i < enabled.length; ++i) {
                final String mapped = this.toJavaCipherSuite(enabled[i]);
                final String cipher = (mapped == null) ? enabled[i] : mapped;
                if ((tls13Enabled && OpenSsl.isTlsv13Supported()) || !SslUtils.isTLSv13Cipher(cipher)) {
                    enabledSet.add(cipher);
                }
            }
            Collections.addAll(enabledSet, extraCiphers);
        }
        return enabledSet.toArray(new String[0]);
    }
    
    @Override
    public final void setEnabledCipherSuites(final String[] cipherSuites) {
        ObjectUtil.checkNotNull(cipherSuites, "cipherSuites");
        final StringBuilder buf = new StringBuilder();
        final StringBuilder bufTLSv13 = new StringBuilder();
        CipherSuiteConverter.convertToCipherStrings(Arrays.asList(cipherSuites), buf, bufTLSv13, OpenSsl.isBoringSSL());
        final String cipherSuiteSpec = buf.toString();
        final String cipherSuiteSpecTLSv13 = bufTLSv13.toString();
        if (!OpenSsl.isTlsv13Supported() && !cipherSuiteSpecTLSv13.isEmpty()) {
            throw new IllegalArgumentException("TLSv1.3 is not supported by this java version.");
        }
        synchronized (this) {
            if (!this.isDestroyed()) {
                try {
                    SSL.setCipherSuites(this.ssl, cipherSuiteSpec, false);
                    if (OpenSsl.isTlsv13Supported()) {
                        SSL.setCipherSuites(this.ssl, OpenSsl.checkTls13Ciphers(ReferenceCountedOpenSslEngine.logger, cipherSuiteSpecTLSv13), true);
                    }
                    final Set<String> protocols = new HashSet<String>(this.explicitlyEnabledProtocols.length);
                    Collections.addAll(protocols, this.explicitlyEnabledProtocols);
                    if (cipherSuiteSpec.isEmpty()) {
                        protocols.remove("TLSv1");
                        protocols.remove("TLSv1.1");
                        protocols.remove("TLSv1.2");
                        protocols.remove("SSLv3");
                        protocols.remove("SSLv2");
                        protocols.remove("SSLv2Hello");
                    }
                    if (cipherSuiteSpecTLSv13.isEmpty()) {
                        protocols.remove("TLSv1.3");
                    }
                    this.setEnabledProtocols0(protocols.toArray(EmptyArrays.EMPTY_STRINGS), false);
                    return;
                }
                catch (final Exception e) {
                    throw new IllegalStateException("failed to enable cipher suites: " + cipherSuiteSpec, e);
                }
                throw new IllegalStateException("failed to enable cipher suites: " + cipherSuiteSpec);
            }
            throw new IllegalStateException("failed to enable cipher suites: " + cipherSuiteSpec);
        }
    }
    
    @Override
    public final String[] getSupportedProtocols() {
        return OpenSsl.SUPPORTED_PROTOCOLS_SET.toArray(new String[0]);
    }
    
    @Override
    public final String[] getEnabledProtocols() {
        final List<String> enabled = new ArrayList<String>(6);
        enabled.add("SSLv2Hello");
        final int opts;
        synchronized (this) {
            if (this.isDestroyed()) {
                return enabled.toArray(new String[0]);
            }
            opts = SSL.getOptions(this.ssl);
        }
        if (isProtocolEnabled(opts, SSL.SSL_OP_NO_TLSv1, "TLSv1")) {
            enabled.add("TLSv1");
        }
        if (isProtocolEnabled(opts, SSL.SSL_OP_NO_TLSv1_1, "TLSv1.1")) {
            enabled.add("TLSv1.1");
        }
        if (isProtocolEnabled(opts, SSL.SSL_OP_NO_TLSv1_2, "TLSv1.2")) {
            enabled.add("TLSv1.2");
        }
        if (isProtocolEnabled(opts, SSL.SSL_OP_NO_TLSv1_3, "TLSv1.3")) {
            enabled.add("TLSv1.3");
        }
        if (isProtocolEnabled(opts, SSL.SSL_OP_NO_SSLv2, "SSLv2")) {
            enabled.add("SSLv2");
        }
        if (isProtocolEnabled(opts, SSL.SSL_OP_NO_SSLv3, "SSLv3")) {
            enabled.add("SSLv3");
        }
        return enabled.toArray(new String[0]);
    }
    
    private static boolean isProtocolEnabled(final int opts, final int disableMask, final String protocolString) {
        return (opts & disableMask) == 0x0 && OpenSsl.SUPPORTED_PROTOCOLS_SET.contains(protocolString);
    }
    
    @Override
    public final void setEnabledProtocols(final String[] protocols) {
        this.setEnabledProtocols0(protocols, true);
    }
    
    private void setEnabledProtocols0(final String[] protocols, final boolean cache) {
        ObjectUtil.checkNotNullWithIAE(protocols, "protocols");
        int minProtocolIndex = ReferenceCountedOpenSslEngine.OPENSSL_OP_NO_PROTOCOLS.length;
        int maxProtocolIndex = 0;
        for (final String p : protocols) {
            if (!OpenSsl.SUPPORTED_PROTOCOLS_SET.contains(p)) {
                throw new IllegalArgumentException("Protocol " + p + " is not supported.");
            }
            if (p.equals("SSLv2")) {
                if (minProtocolIndex > 0) {
                    minProtocolIndex = 0;
                }
                if (maxProtocolIndex < 0) {
                    maxProtocolIndex = 0;
                }
            }
            else if (p.equals("SSLv3")) {
                if (minProtocolIndex > 1) {
                    minProtocolIndex = 1;
                }
                if (maxProtocolIndex < 1) {
                    maxProtocolIndex = 1;
                }
            }
            else if (p.equals("TLSv1")) {
                if (minProtocolIndex > 2) {
                    minProtocolIndex = 2;
                }
                if (maxProtocolIndex < 2) {
                    maxProtocolIndex = 2;
                }
            }
            else if (p.equals("TLSv1.1")) {
                if (minProtocolIndex > 3) {
                    minProtocolIndex = 3;
                }
                if (maxProtocolIndex < 3) {
                    maxProtocolIndex = 3;
                }
            }
            else if (p.equals("TLSv1.2")) {
                if (minProtocolIndex > 4) {
                    minProtocolIndex = 4;
                }
                if (maxProtocolIndex < 4) {
                    maxProtocolIndex = 4;
                }
            }
            else if (p.equals("TLSv1.3")) {
                if (minProtocolIndex > 5) {
                    minProtocolIndex = 5;
                }
                if (maxProtocolIndex < 5) {
                    maxProtocolIndex = 5;
                }
            }
        }
        synchronized (this) {
            if (cache) {
                this.explicitlyEnabledProtocols = protocols;
            }
            if (this.isDestroyed()) {
                throw new IllegalStateException("failed to enable protocols: " + Arrays.asList(protocols));
            }
            SSL.clearOptions(this.ssl, SSL.SSL_OP_NO_SSLv2 | SSL.SSL_OP_NO_SSLv3 | SSL.SSL_OP_NO_TLSv1 | SSL.SSL_OP_NO_TLSv1_1 | SSL.SSL_OP_NO_TLSv1_2 | SSL.SSL_OP_NO_TLSv1_3);
            int opts = 0;
            for (int i = 0; i < minProtocolIndex; ++i) {
                opts |= ReferenceCountedOpenSslEngine.OPENSSL_OP_NO_PROTOCOLS[i];
            }
            assert maxProtocolIndex != Integer.MAX_VALUE;
            for (int i = maxProtocolIndex + 1; i < ReferenceCountedOpenSslEngine.OPENSSL_OP_NO_PROTOCOLS.length; ++i) {
                opts |= ReferenceCountedOpenSslEngine.OPENSSL_OP_NO_PROTOCOLS[i];
            }
            SSL.setOptions(this.ssl, opts);
        }
    }
    
    @Override
    public final SSLSession getSession() {
        return this.session;
    }
    
    @Override
    public final synchronized void beginHandshake() throws SSLException {
        switch (this.handshakeState) {
            case STARTED_IMPLICITLY: {
                this.checkEngineClosed();
                this.handshakeState = HandshakeState.STARTED_EXPLICITLY;
                this.calculateMaxWrapOverhead();
                break;
            }
            case STARTED_EXPLICITLY: {
                break;
            }
            case FINISHED: {
                throw new SSLException("renegotiation unsupported");
            }
            case NOT_STARTED: {
                this.handshakeState = HandshakeState.STARTED_EXPLICITLY;
                if (this.handshake() == SSLEngineResult.HandshakeStatus.NEED_TASK) {
                    this.needTask = true;
                }
                this.calculateMaxWrapOverhead();
                break;
            }
            default: {
                throw new Error();
            }
        }
    }
    
    private void checkEngineClosed() throws SSLException {
        if (this.isDestroyed()) {
            throw new SSLException("engine closed");
        }
    }
    
    private static SSLEngineResult.HandshakeStatus pendingStatus(final int pendingStatus) {
        return (pendingStatus > 0) ? SSLEngineResult.HandshakeStatus.NEED_WRAP : SSLEngineResult.HandshakeStatus.NEED_UNWRAP;
    }
    
    private static boolean isEmpty(final Object[] arr) {
        return arr == null || arr.length == 0;
    }
    
    private static boolean isEmpty(final byte[] cert) {
        return cert == null || cert.length == 0;
    }
    
    private SSLEngineResult.HandshakeStatus handshakeException() throws SSLException {
        if (SSL.bioLengthNonApplication(this.networkBIO) > 0) {
            return SSLEngineResult.HandshakeStatus.NEED_WRAP;
        }
        final Throwable exception = this.pendingException;
        assert exception != null;
        this.pendingException = null;
        this.shutdown();
        if (exception instanceof SSLHandshakeException) {
            throw (SSLHandshakeException)exception;
        }
        final SSLHandshakeException e = new SSLHandshakeException("General OpenSslEngine problem");
        e.initCause(exception);
        throw e;
    }
    
    final void initHandshakeException(final Throwable cause) {
        if (this.pendingException == null) {
            this.pendingException = cause;
        }
        else {
            ThrowableUtil.addSuppressed(this.pendingException, cause);
        }
    }
    
    private SSLEngineResult.HandshakeStatus handshake() throws SSLException {
        if (this.needTask) {
            return SSLEngineResult.HandshakeStatus.NEED_TASK;
        }
        if (this.handshakeState == HandshakeState.FINISHED) {
            return SSLEngineResult.HandshakeStatus.FINISHED;
        }
        this.checkEngineClosed();
        if (this.pendingException != null) {
            if (SSL.doHandshake(this.ssl) <= 0) {
                SSL.clearError();
            }
            return this.handshakeException();
        }
        this.engineMap.add(this);
        if (!this.sessionSet) {
            this.parentContext.sessionContext().setSessionFromCache(this.getPeerHost(), this.getPeerPort(), this.ssl);
            this.sessionSet = true;
        }
        if (this.lastAccessed == -1L) {
            this.lastAccessed = System.currentTimeMillis();
        }
        final int code = SSL.doHandshake(this.ssl);
        if (code <= 0) {
            final int sslError = SSL.getError(this.ssl, code);
            if (sslError == SSL.SSL_ERROR_WANT_READ || sslError == SSL.SSL_ERROR_WANT_WRITE) {
                return pendingStatus(SSL.bioLengthNonApplication(this.networkBIO));
            }
            if (sslError == SSL.SSL_ERROR_WANT_X509_LOOKUP || sslError == SSL.SSL_ERROR_WANT_CERTIFICATE_VERIFY || sslError == SSL.SSL_ERROR_WANT_PRIVATE_KEY_OPERATION) {
                return SSLEngineResult.HandshakeStatus.NEED_TASK;
            }
            if (this.needWrapAgain(SSL.getLastErrorNumber())) {
                return SSLEngineResult.HandshakeStatus.NEED_WRAP;
            }
            if (this.pendingException != null) {
                return this.handshakeException();
            }
            throw this.shutdownWithError("SSL_do_handshake", sslError);
        }
        else {
            if (SSL.bioLengthNonApplication(this.networkBIO) > 0) {
                return SSLEngineResult.HandshakeStatus.NEED_WRAP;
            }
            this.session.handshakeFinished(SSL.getSessionId(this.ssl), SSL.getCipherForSSL(this.ssl), SSL.getVersion(this.ssl), SSL.getPeerCertificate(this.ssl), SSL.getPeerCertChain(this.ssl), SSL.getTime(this.ssl) * 1000L, this.parentContext.sessionTimeout() * 1000L);
            this.selectApplicationProtocol();
            return SSLEngineResult.HandshakeStatus.FINISHED;
        }
    }
    
    private SSLEngineResult.HandshakeStatus mayFinishHandshake(final SSLEngineResult.HandshakeStatus hs, final int bytesConsumed, final int bytesProduced) throws SSLException {
        return ((hs == SSLEngineResult.HandshakeStatus.NEED_UNWRAP && bytesProduced > 0) || (hs == SSLEngineResult.HandshakeStatus.NEED_WRAP && bytesConsumed > 0)) ? this.handshake() : this.mayFinishHandshake((hs != SSLEngineResult.HandshakeStatus.FINISHED) ? this.getHandshakeStatus() : SSLEngineResult.HandshakeStatus.FINISHED);
    }
    
    private SSLEngineResult.HandshakeStatus mayFinishHandshake(final SSLEngineResult.HandshakeStatus status) throws SSLException {
        if (status == SSLEngineResult.HandshakeStatus.NOT_HANDSHAKING) {
            if (this.handshakeState != HandshakeState.FINISHED) {
                return this.handshake();
            }
            if (!this.isDestroyed() && SSL.bioLengthNonApplication(this.networkBIO) > 0) {
                return SSLEngineResult.HandshakeStatus.NEED_WRAP;
            }
        }
        return status;
    }
    
    @Override
    public final synchronized SSLEngineResult.HandshakeStatus getHandshakeStatus() {
        if (!this.needPendingStatus()) {
            return SSLEngineResult.HandshakeStatus.NOT_HANDSHAKING;
        }
        if (this.needTask) {
            return SSLEngineResult.HandshakeStatus.NEED_TASK;
        }
        return pendingStatus(SSL.bioLengthNonApplication(this.networkBIO));
    }
    
    private SSLEngineResult.HandshakeStatus getHandshakeStatus(final int pending) {
        if (!this.needPendingStatus()) {
            return SSLEngineResult.HandshakeStatus.NOT_HANDSHAKING;
        }
        if (this.needTask) {
            return SSLEngineResult.HandshakeStatus.NEED_TASK;
        }
        return pendingStatus(pending);
    }
    
    private boolean needPendingStatus() {
        return this.handshakeState != HandshakeState.NOT_STARTED && !this.isDestroyed() && (this.handshakeState != HandshakeState.FINISHED || this.isInboundDone() || this.isOutboundDone());
    }
    
    private String toJavaCipherSuite(final String openSslCipherSuite) {
        if (openSslCipherSuite == null) {
            return null;
        }
        final String version = SSL.getVersion(this.ssl);
        final String prefix = toJavaCipherSuitePrefix(version);
        return CipherSuiteConverter.toJava(openSslCipherSuite, prefix);
    }
    
    private static String toJavaCipherSuitePrefix(final String protocolVersion) {
        char c;
        if (protocolVersion == null || protocolVersion.isEmpty()) {
            c = '\0';
        }
        else {
            c = protocolVersion.charAt(0);
        }
        switch (c) {
            case 'T': {
                return "TLS";
            }
            case 'S': {
                return "SSL";
            }
            default: {
                return "UNKNOWN";
            }
        }
    }
    
    @Override
    public final void setUseClientMode(final boolean clientMode) {
        if (clientMode != this.clientMode) {
            throw new UnsupportedOperationException();
        }
    }
    
    @Override
    public final boolean getUseClientMode() {
        return this.clientMode;
    }
    
    @Override
    public final void setNeedClientAuth(final boolean b) {
        this.setClientAuth(b ? ClientAuth.REQUIRE : ClientAuth.NONE);
    }
    
    @Override
    public final boolean getNeedClientAuth() {
        return this.clientAuth == ClientAuth.REQUIRE;
    }
    
    @Override
    public final void setWantClientAuth(final boolean b) {
        this.setClientAuth(b ? ClientAuth.OPTIONAL : ClientAuth.NONE);
    }
    
    @Override
    public final boolean getWantClientAuth() {
        return this.clientAuth == ClientAuth.OPTIONAL;
    }
    
    public final synchronized void setVerify(final int verifyMode, final int depth) {
        if (!this.isDestroyed()) {
            SSL.setVerify(this.ssl, verifyMode, depth);
        }
    }
    
    private void setClientAuth(final ClientAuth mode) {
        if (this.clientMode) {
            return;
        }
        synchronized (this) {
            if (this.clientAuth == mode) {
                return;
            }
            if (!this.isDestroyed()) {
                switch (mode) {
                    case NONE: {
                        SSL.setVerify(this.ssl, 0, 10);
                        break;
                    }
                    case REQUIRE: {
                        SSL.setVerify(this.ssl, 2, 10);
                        break;
                    }
                    case OPTIONAL: {
                        SSL.setVerify(this.ssl, 1, 10);
                        break;
                    }
                    default: {
                        throw new Error(mode.toString());
                    }
                }
            }
            this.clientAuth = mode;
        }
    }
    
    @Override
    public final void setEnableSessionCreation(final boolean b) {
        if (b) {
            throw new UnsupportedOperationException();
        }
    }
    
    @Override
    public final boolean getEnableSessionCreation() {
        return false;
    }
    
    @SuppressJava6Requirement(reason = "Usage guarded by java version check")
    @Override
    public final synchronized SSLParameters getSSLParameters() {
        final SSLParameters sslParameters = super.getSSLParameters();
        final int version = PlatformDependent.javaVersion();
        if (version >= 7) {
            sslParameters.setEndpointIdentificationAlgorithm(this.endPointIdentificationAlgorithm);
            Java7SslParametersUtils.setAlgorithmConstraints(sslParameters, this.algorithmConstraints);
            if (version >= 8) {
                if (this.sniHostNames != null) {
                    Java8SslUtils.setSniHostNames(sslParameters, this.sniHostNames);
                }
                if (!this.isDestroyed()) {
                    Java8SslUtils.setUseCipherSuitesOrder(sslParameters, (SSL.getOptions(this.ssl) & SSL.SSL_OP_CIPHER_SERVER_PREFERENCE) != 0x0);
                }
                Java8SslUtils.setSNIMatchers(sslParameters, this.matchers);
            }
        }
        return sslParameters;
    }
    
    @SuppressJava6Requirement(reason = "Usage guarded by java version check")
    @Override
    public final synchronized void setSSLParameters(final SSLParameters sslParameters) {
        final int version = PlatformDependent.javaVersion();
        if (version >= 7) {
            if (sslParameters.getAlgorithmConstraints() != null) {
                throw new IllegalArgumentException("AlgorithmConstraints are not supported.");
            }
            final boolean isDestroyed = this.isDestroyed();
            if (version >= 8) {
                if (!isDestroyed) {
                    if (this.clientMode) {
                        final List<String> sniHostNames = Java8SslUtils.getSniHostNames(sslParameters);
                        for (final String name : sniHostNames) {
                            SSL.setTlsExtHostName(this.ssl, name);
                        }
                        this.sniHostNames = sniHostNames;
                    }
                    if (Java8SslUtils.getUseCipherSuitesOrder(sslParameters)) {
                        SSL.setOptions(this.ssl, SSL.SSL_OP_CIPHER_SERVER_PREFERENCE);
                    }
                    else {
                        SSL.clearOptions(this.ssl, SSL.SSL_OP_CIPHER_SERVER_PREFERENCE);
                    }
                }
                this.matchers = sslParameters.getSNIMatchers();
            }
            final String endPointIdentificationAlgorithm = sslParameters.getEndpointIdentificationAlgorithm();
            if (!isDestroyed && this.clientMode && isEndPointVerificationEnabled(endPointIdentificationAlgorithm)) {
                SSL.setVerify(this.ssl, 2, -1);
            }
            this.endPointIdentificationAlgorithm = endPointIdentificationAlgorithm;
            this.algorithmConstraints = sslParameters.getAlgorithmConstraints();
        }
        super.setSSLParameters(sslParameters);
    }
    
    private static boolean isEndPointVerificationEnabled(final String endPointIdentificationAlgorithm) {
        return endPointIdentificationAlgorithm != null && !endPointIdentificationAlgorithm.isEmpty();
    }
    
    private boolean isDestroyed() {
        return this.destroyed;
    }
    
    final boolean checkSniHostnameMatch(final byte[] hostname) {
        return Java8SslUtils.checkSniHostnameMatch(this.matchers, hostname);
    }
    
    @Override
    public String getNegotiatedApplicationProtocol() {
        return this.applicationProtocol;
    }
    
    private static long bufferAddress(final ByteBuffer b) {
        assert b.isDirect();
        if (PlatformDependent.hasUnsafe()) {
            return PlatformDependent.directBufferAddress(b);
        }
        return Buffer.address(b);
    }
    
    private void selectApplicationProtocol() throws SSLException {
        final ApplicationProtocolConfig.SelectedListenerFailureBehavior behavior = this.apn.selectedListenerFailureBehavior();
        final List<String> protocols = this.apn.protocols();
        switch (this.apn.protocol()) {
            case NONE: {
                break;
            }
            case ALPN: {
                final String applicationProtocol = SSL.getAlpnSelected(this.ssl);
                if (applicationProtocol != null) {
                    this.applicationProtocol = this.selectApplicationProtocol(protocols, behavior, applicationProtocol);
                    break;
                }
                break;
            }
            case NPN: {
                final String applicationProtocol = SSL.getNextProtoNegotiated(this.ssl);
                if (applicationProtocol != null) {
                    this.applicationProtocol = this.selectApplicationProtocol(protocols, behavior, applicationProtocol);
                    break;
                }
                break;
            }
            case NPN_AND_ALPN: {
                String applicationProtocol = SSL.getAlpnSelected(this.ssl);
                if (applicationProtocol == null) {
                    applicationProtocol = SSL.getNextProtoNegotiated(this.ssl);
                }
                if (applicationProtocol != null) {
                    this.applicationProtocol = this.selectApplicationProtocol(protocols, behavior, applicationProtocol);
                    break;
                }
                break;
            }
            default: {
                throw new Error();
            }
        }
    }
    
    private String selectApplicationProtocol(final List<String> protocols, final ApplicationProtocolConfig.SelectedListenerFailureBehavior behavior, final String applicationProtocol) throws SSLException {
        if (behavior == ApplicationProtocolConfig.SelectedListenerFailureBehavior.ACCEPT) {
            return applicationProtocol;
        }
        final int size = protocols.size();
        assert size > 0;
        if (protocols.contains(applicationProtocol)) {
            return applicationProtocol;
        }
        if (behavior == ApplicationProtocolConfig.SelectedListenerFailureBehavior.CHOOSE_MY_LAST_PROTOCOL) {
            return protocols.get(size - 1);
        }
        throw new SSLException("unknown protocol " + applicationProtocol);
    }
    
    final void setSessionId(final OpenSslSessionId id) {
        this.session.setSessionId(id);
    }
    
    static {
        logger = InternalLoggerFactory.getInstance(ReferenceCountedOpenSslEngine.class);
        leakDetector = ResourceLeakDetectorFactory.instance().newResourceLeakDetector(ReferenceCountedOpenSslEngine.class);
        OPENSSL_OP_NO_PROTOCOLS = new int[] { SSL.SSL_OP_NO_SSLv2, SSL.SSL_OP_NO_SSLv3, SSL.SSL_OP_NO_TLSv1, SSL.SSL_OP_NO_TLSv1_1, SSL.SSL_OP_NO_TLSv1_2, SSL.SSL_OP_NO_TLSv1_3 };
        MAX_PLAINTEXT_LENGTH = SSL.SSL_MAX_PLAINTEXT_LENGTH;
        MAX_RECORD_SIZE = SSL.SSL_MAX_RECORD_LENGTH;
        NEED_UNWRAP_OK = new SSLEngineResult(SSLEngineResult.Status.OK, SSLEngineResult.HandshakeStatus.NEED_UNWRAP, 0, 0);
        NEED_UNWRAP_CLOSED = new SSLEngineResult(SSLEngineResult.Status.CLOSED, SSLEngineResult.HandshakeStatus.NEED_UNWRAP, 0, 0);
        NEED_WRAP_OK = new SSLEngineResult(SSLEngineResult.Status.OK, SSLEngineResult.HandshakeStatus.NEED_WRAP, 0, 0);
        NEED_WRAP_CLOSED = new SSLEngineResult(SSLEngineResult.Status.CLOSED, SSLEngineResult.HandshakeStatus.NEED_WRAP, 0, 0);
        CLOSED_NOT_HANDSHAKING = new SSLEngineResult(SSLEngineResult.Status.CLOSED, SSLEngineResult.HandshakeStatus.NOT_HANDSHAKING, 0, 0);
    }
    
    private enum HandshakeState
    {
        NOT_STARTED, 
        STARTED_IMPLICITLY, 
        STARTED_EXPLICITLY, 
        FINISHED;
    }
    
    private class TaskDecorator<R extends Runnable> implements Runnable
    {
        protected final R task;
        
        TaskDecorator(final R task) {
            this.task = task;
        }
        
        @Override
        public void run() {
            if (ReferenceCountedOpenSslEngine.this.isDestroyed()) {
                return;
            }
            try {
                this.task.run();
            }
            finally {
                ReferenceCountedOpenSslEngine.this.needTask = false;
            }
        }
    }
    
    private final class AsyncTaskDecorator extends TaskDecorator<AsyncTask> implements AsyncRunnable
    {
        AsyncTaskDecorator(final AsyncTask task) {
            super((Runnable)task);
        }
        
        @Override
        public void run(final Runnable runnable) {
            if (ReferenceCountedOpenSslEngine.this.isDestroyed()) {
                runnable.run();
                return;
            }
            ((AsyncTask)this.task).runAsync((Runnable)new Runnable() {
                @Override
                public void run() {
                    ReferenceCountedOpenSslEngine.this.needTask = false;
                    runnable.run();
                }
            });
        }
    }
    
    private final class DefaultOpenSslSession implements OpenSslSession
    {
        private final OpenSslSessionContext sessionContext;
        private X509Certificate[] x509PeerCerts;
        private Certificate[] peerCerts;
        private boolean valid;
        private String protocol;
        private String cipher;
        private OpenSslSessionId id;
        private volatile long creationTime;
        private volatile int applicationBufferSize;
        private volatile Certificate[] localCertificateChain;
        private Map<String, Object> values;
        
        DefaultOpenSslSession(final OpenSslSessionContext sessionContext) {
            this.valid = true;
            this.id = OpenSslSessionId.NULL_ID;
            this.applicationBufferSize = ReferenceCountedOpenSslEngine.MAX_PLAINTEXT_LENGTH;
            this.sessionContext = sessionContext;
        }
        
        private SSLSessionBindingEvent newSSLSessionBindingEvent(final String name) {
            return new SSLSessionBindingEvent(ReferenceCountedOpenSslEngine.this.session, name);
        }
        
        @Override
        public void setSessionId(final OpenSslSessionId sessionId) {
            synchronized (ReferenceCountedOpenSslEngine.this) {
                if (this.id == OpenSslSessionId.NULL_ID) {
                    this.id = sessionId;
                    this.creationTime = System.currentTimeMillis();
                }
            }
        }
        
        @Override
        public OpenSslSessionId sessionId() {
            synchronized (ReferenceCountedOpenSslEngine.this) {
                if (this.id == OpenSslSessionId.NULL_ID && !ReferenceCountedOpenSslEngine.this.isDestroyed()) {
                    final byte[] sessionId = SSL.getSessionId(ReferenceCountedOpenSslEngine.this.ssl);
                    if (sessionId != null) {
                        this.id = new OpenSslSessionId(sessionId);
                    }
                }
                return this.id;
            }
        }
        
        @Override
        public void setLocalCertificate(final Certificate[] localCertificate) {
            this.localCertificateChain = localCertificate;
        }
        
        @Override
        public byte[] getId() {
            return this.sessionId().cloneBytes();
        }
        
        @Override
        public OpenSslSessionContext getSessionContext() {
            return this.sessionContext;
        }
        
        @Override
        public long getCreationTime() {
            synchronized (ReferenceCountedOpenSslEngine.this) {
                return this.creationTime;
            }
        }
        
        @Override
        public long getLastAccessedTime() {
            final long lastAccessed = ReferenceCountedOpenSslEngine.this.lastAccessed;
            return (lastAccessed == -1L) ? this.getCreationTime() : lastAccessed;
        }
        
        @Override
        public void invalidate() {
            synchronized (ReferenceCountedOpenSslEngine.this) {
                this.valid = false;
                this.sessionContext.removeFromCache(this.id);
            }
        }
        
        @Override
        public boolean isValid() {
            synchronized (ReferenceCountedOpenSslEngine.this) {
                return this.valid || this.sessionContext.isInCache(this.id);
            }
        }
        
        @Override
        public void putValue(final String name, final Object value) {
            ObjectUtil.checkNotNull(name, "name");
            ObjectUtil.checkNotNull(value, "value");
            final Object old;
            synchronized (this) {
                Map<String, Object> values = this.values;
                if (values == null) {
                    final HashMap<String, Object> values2 = new HashMap<String, Object>(2);
                    this.values = values2;
                    values = values2;
                }
                old = values.put(name, value);
            }
            if (value instanceof SSLSessionBindingListener) {
                ((SSLSessionBindingListener)value).valueBound(this.newSSLSessionBindingEvent(name));
            }
            this.notifyUnbound(old, name);
        }
        
        @Override
        public Object getValue(final String name) {
            ObjectUtil.checkNotNull(name, "name");
            synchronized (this) {
                if (this.values == null) {
                    return null;
                }
                return this.values.get(name);
            }
        }
        
        @Override
        public void removeValue(final String name) {
            ObjectUtil.checkNotNull(name, "name");
            final Object old;
            synchronized (this) {
                final Map<String, Object> values = this.values;
                if (values == null) {
                    return;
                }
                old = values.remove(name);
            }
            this.notifyUnbound(old, name);
        }
        
        @Override
        public String[] getValueNames() {
            synchronized (this) {
                final Map<String, Object> values = this.values;
                if (values == null || values.isEmpty()) {
                    return EmptyArrays.EMPTY_STRINGS;
                }
                return values.keySet().toArray(new String[0]);
            }
        }
        
        private void notifyUnbound(final Object value, final String name) {
            if (value instanceof SSLSessionBindingListener) {
                ((SSLSessionBindingListener)value).valueUnbound(this.newSSLSessionBindingEvent(name));
            }
        }
        
        @Override
        public void handshakeFinished(final byte[] id, final String cipher, final String protocol, final byte[] peerCertificate, final byte[][] peerCertificateChain, final long creationTime, final long timeout) throws SSLException {
            synchronized (ReferenceCountedOpenSslEngine.this) {
                if (ReferenceCountedOpenSslEngine.this.isDestroyed()) {
                    throw new SSLException("Already closed");
                }
                this.creationTime = creationTime;
                if (this.id == OpenSslSessionId.NULL_ID) {
                    this.id = ((id == null) ? OpenSslSessionId.NULL_ID : new OpenSslSessionId(id));
                }
                this.cipher = ReferenceCountedOpenSslEngine.this.toJavaCipherSuite(cipher);
                this.protocol = protocol;
                if (ReferenceCountedOpenSslEngine.this.clientMode) {
                    if (isEmpty(peerCertificateChain)) {
                        this.peerCerts = EmptyArrays.EMPTY_CERTIFICATES;
                        this.x509PeerCerts = EmptyArrays.EMPTY_JAVAX_X509_CERTIFICATES;
                    }
                    else {
                        this.peerCerts = new Certificate[peerCertificateChain.length];
                        this.x509PeerCerts = new X509Certificate[peerCertificateChain.length];
                        this.initCerts(peerCertificateChain, 0);
                    }
                }
                else if (isEmpty(peerCertificate)) {
                    this.peerCerts = EmptyArrays.EMPTY_CERTIFICATES;
                    this.x509PeerCerts = EmptyArrays.EMPTY_JAVAX_X509_CERTIFICATES;
                }
                else if (isEmpty(peerCertificateChain)) {
                    this.peerCerts = new Certificate[] { new LazyX509Certificate(peerCertificate) };
                    this.x509PeerCerts = new X509Certificate[] { new LazyJavaxX509Certificate(peerCertificate) };
                }
                else {
                    this.peerCerts = new Certificate[peerCertificateChain.length + 1];
                    this.x509PeerCerts = new X509Certificate[peerCertificateChain.length + 1];
                    this.peerCerts[0] = new LazyX509Certificate(peerCertificate);
                    this.x509PeerCerts[0] = new LazyJavaxX509Certificate(peerCertificate);
                    this.initCerts(peerCertificateChain, 1);
                }
                ReferenceCountedOpenSslEngine.this.calculateMaxWrapOverhead();
                ReferenceCountedOpenSslEngine.this.handshakeState = HandshakeState.FINISHED;
            }
        }
        
        private void initCerts(final byte[][] chain, final int startPos) {
            for (int i = 0; i < chain.length; ++i) {
                final int certPos = startPos + i;
                this.peerCerts[certPos] = new LazyX509Certificate(chain[i]);
                this.x509PeerCerts[certPos] = new LazyJavaxX509Certificate(chain[i]);
            }
        }
        
        @Override
        public Certificate[] getPeerCertificates() throws SSLPeerUnverifiedException {
            synchronized (ReferenceCountedOpenSslEngine.this) {
                if (isEmpty(this.peerCerts)) {
                    throw new SSLPeerUnverifiedException("peer not verified");
                }
                return this.peerCerts.clone();
            }
        }
        
        @Override
        public Certificate[] getLocalCertificates() {
            final Certificate[] localCerts = this.localCertificateChain;
            if (localCerts == null) {
                return null;
            }
            return localCerts.clone();
        }
        
        @Override
        public X509Certificate[] getPeerCertificateChain() throws SSLPeerUnverifiedException {
            synchronized (ReferenceCountedOpenSslEngine.this) {
                if (isEmpty(this.x509PeerCerts)) {
                    throw new SSLPeerUnverifiedException("peer not verified");
                }
                return this.x509PeerCerts.clone();
            }
        }
        
        @Override
        public Principal getPeerPrincipal() throws SSLPeerUnverifiedException {
            final Certificate[] peer = this.getPeerCertificates();
            return ((java.security.cert.X509Certificate)peer[0]).getSubjectX500Principal();
        }
        
        @Override
        public Principal getLocalPrincipal() {
            final Certificate[] local = this.localCertificateChain;
            if (local == null || local.length == 0) {
                return null;
            }
            return ((java.security.cert.X509Certificate)local[0]).getIssuerX500Principal();
        }
        
        @Override
        public String getCipherSuite() {
            synchronized (ReferenceCountedOpenSslEngine.this) {
                if (this.cipher == null) {
                    return "SSL_NULL_WITH_NULL_NULL";
                }
                return this.cipher;
            }
        }
        
        @Override
        public String getProtocol() {
            String protocol = this.protocol;
            if (protocol == null) {
                synchronized (ReferenceCountedOpenSslEngine.this) {
                    if (!ReferenceCountedOpenSslEngine.this.isDestroyed()) {
                        protocol = SSL.getVersion(ReferenceCountedOpenSslEngine.this.ssl);
                    }
                    else {
                        protocol = "";
                    }
                }
            }
            return protocol;
        }
        
        @Override
        public String getPeerHost() {
            return ReferenceCountedOpenSslEngine.this.getPeerHost();
        }
        
        @Override
        public int getPeerPort() {
            return ReferenceCountedOpenSslEngine.this.getPeerPort();
        }
        
        @Override
        public int getPacketBufferSize() {
            return ReferenceCountedOpenSslEngine.this.maxEncryptedPacketLength();
        }
        
        @Override
        public int getApplicationBufferSize() {
            return this.applicationBufferSize;
        }
        
        @Override
        public void tryExpandApplicationBufferSize(final int packetLengthDataOnly) {
            if (packetLengthDataOnly > ReferenceCountedOpenSslEngine.MAX_PLAINTEXT_LENGTH && this.applicationBufferSize != ReferenceCountedOpenSslEngine.MAX_RECORD_SIZE) {
                this.applicationBufferSize = ReferenceCountedOpenSslEngine.MAX_RECORD_SIZE;
            }
        }
        
        @Override
        public String toString() {
            return "DefaultOpenSslSession{sessionContext=" + this.sessionContext + ", id=" + this.id + '}';
        }
    }
}
