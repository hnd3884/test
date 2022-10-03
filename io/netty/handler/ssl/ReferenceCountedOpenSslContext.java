package io.netty.handler.ssl;

import io.netty.util.concurrent.FutureListener;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.internal.tcnative.ResultCallback;
import io.netty.util.internal.StringUtil;
import java.security.cert.CertPathValidatorException;
import java.security.cert.CertificateRevokedException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.CertificateExpiredException;
import io.netty.internal.tcnative.CertificateVerifier;
import io.netty.util.ResourceLeakDetectorFactory;
import io.netty.util.internal.SystemPropertyUtil;
import io.netty.util.internal.logging.InternalLoggerFactory;
import javax.net.ssl.SSLSessionContext;
import java.security.SignatureException;
import javax.net.ssl.KeyManagerFactory;
import io.netty.buffer.ByteBuf;
import java.security.PrivateKey;
import io.netty.util.internal.SuppressJava6Requirement;
import javax.net.ssl.X509ExtendedTrustManager;
import javax.net.ssl.X509KeyManager;
import javax.net.ssl.KeyManager;
import io.netty.util.internal.PlatformDependent;
import javax.net.ssl.X509TrustManager;
import javax.net.ssl.TrustManager;
import io.netty.handler.ssl.util.LazyX509Certificate;
import java.security.cert.X509Certificate;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.Executor;
import javax.net.ssl.SSLEngine;
import io.netty.buffer.ByteBufAllocator;
import io.netty.internal.tcnative.AsyncSSLPrivateKeyMethod;
import io.netty.internal.tcnative.SSLPrivateKeyMethod;
import io.netty.internal.tcnative.SSL;
import javax.net.ssl.SSLException;
import io.netty.internal.tcnative.SSLContext;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import io.netty.util.internal.ObjectUtil;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.security.cert.Certificate;
import io.netty.util.AbstractReferenceCounted;
import io.netty.util.ResourceLeakTracker;
import java.util.List;
import io.netty.util.ResourceLeakDetector;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.ReferenceCounted;

public abstract class ReferenceCountedOpenSslContext extends SslContext implements ReferenceCounted
{
    private static final InternalLogger logger;
    private static final int DEFAULT_BIO_NON_APPLICATION_BUFFER_SIZE;
    static final boolean USE_TASKS;
    private static final Integer DH_KEY_LENGTH;
    private static final ResourceLeakDetector<ReferenceCountedOpenSslContext> leakDetector;
    protected static final int VERIFY_DEPTH = 10;
    static final boolean CLIENT_ENABLE_SESSION_TICKET;
    static final boolean CLIENT_ENABLE_SESSION_TICKET_TLSV13;
    static final boolean SERVER_ENABLE_SESSION_TICKET;
    static final boolean SERVER_ENABLE_SESSION_TICKET_TLSV13;
    static final boolean SERVER_ENABLE_SESSION_CACHE;
    static final boolean CLIENT_ENABLE_SESSION_CACHE;
    protected long ctx;
    private final List<String> unmodifiableCiphers;
    private final OpenSslApplicationProtocolNegotiator apn;
    private final int mode;
    private final ResourceLeakTracker<ReferenceCountedOpenSslContext> leak;
    private final AbstractReferenceCounted refCnt;
    final Certificate[] keyCertChain;
    final ClientAuth clientAuth;
    final String[] protocols;
    final boolean enableOcsp;
    final OpenSslEngineMap engineMap;
    final ReadWriteLock ctxLock;
    private volatile int bioNonApplicationBufferSize;
    static final OpenSslApplicationProtocolNegotiator NONE_PROTOCOL_NEGOTIATOR;
    final boolean tlsFalseStart;
    
    ReferenceCountedOpenSslContext(final Iterable<String> ciphers, final CipherSuiteFilter cipherFilter, final OpenSslApplicationProtocolNegotiator apn, final int mode, final Certificate[] keyCertChain, final ClientAuth clientAuth, final String[] protocols, final boolean startTls, final boolean enableOcsp, final boolean leakDetection, final Map.Entry<SslContextOption<?>, Object>... ctxOptions) throws SSLException {
        super(startTls);
        this.refCnt = new AbstractReferenceCounted() {
            @Override
            public ReferenceCounted touch(final Object hint) {
                if (ReferenceCountedOpenSslContext.this.leak != null) {
                    ReferenceCountedOpenSslContext.this.leak.record(hint);
                }
                return ReferenceCountedOpenSslContext.this;
            }
            
            @Override
            protected void deallocate() {
                ReferenceCountedOpenSslContext.this.destroy();
                if (ReferenceCountedOpenSslContext.this.leak != null) {
                    final boolean closed = ReferenceCountedOpenSslContext.this.leak.close(ReferenceCountedOpenSslContext.this);
                    assert closed;
                }
            }
        };
        this.engineMap = new DefaultOpenSslEngineMap();
        this.ctxLock = new ReentrantReadWriteLock();
        this.bioNonApplicationBufferSize = ReferenceCountedOpenSslContext.DEFAULT_BIO_NON_APPLICATION_BUFFER_SIZE;
        OpenSsl.ensureAvailability();
        if (enableOcsp && !OpenSsl.isOcspSupported()) {
            throw new IllegalStateException("OCSP is not supported.");
        }
        if (mode != 1 && mode != 0) {
            throw new IllegalArgumentException("mode most be either SSL.SSL_MODE_SERVER or SSL.SSL_MODE_CLIENT");
        }
        boolean tlsFalseStart = false;
        boolean useTasks = ReferenceCountedOpenSslContext.USE_TASKS;
        OpenSslPrivateKeyMethod privateKeyMethod = null;
        OpenSslAsyncPrivateKeyMethod asyncPrivateKeyMethod = null;
        if (ctxOptions != null) {
            for (final Map.Entry<SslContextOption<?>, Object> ctxOpt : ctxOptions) {
                final SslContextOption<?> option = ctxOpt.getKey();
                if (option == OpenSslContextOption.TLS_FALSE_START) {
                    tlsFalseStart = ctxOpt.getValue();
                }
                else if (option == OpenSslContextOption.USE_TASKS) {
                    useTasks = ctxOpt.getValue();
                }
                else if (option == OpenSslContextOption.PRIVATE_KEY_METHOD) {
                    privateKeyMethod = ctxOpt.getValue();
                }
                else if (option == OpenSslContextOption.ASYNC_PRIVATE_KEY_METHOD) {
                    asyncPrivateKeyMethod = ctxOpt.getValue();
                }
                else {
                    ReferenceCountedOpenSslContext.logger.debug("Skipping unsupported " + SslContextOption.class.getSimpleName() + ": " + ctxOpt.getKey());
                }
            }
        }
        if (privateKeyMethod != null && asyncPrivateKeyMethod != null) {
            throw new IllegalArgumentException("You can either only use " + OpenSslAsyncPrivateKeyMethod.class.getSimpleName() + " or " + OpenSslPrivateKeyMethod.class.getSimpleName());
        }
        this.tlsFalseStart = tlsFalseStart;
        this.leak = (leakDetection ? ReferenceCountedOpenSslContext.leakDetector.track(this) : null);
        this.mode = mode;
        this.clientAuth = (this.isServer() ? ObjectUtil.checkNotNull(clientAuth, "clientAuth") : ClientAuth.NONE);
        this.protocols = protocols;
        this.enableOcsp = enableOcsp;
        this.keyCertChain = (Certificate[])((keyCertChain == null) ? null : ((Certificate[])keyCertChain.clone()));
        final String[] suites = ObjectUtil.checkNotNull(cipherFilter, "cipherFilter").filterCipherSuites(ciphers, OpenSsl.DEFAULT_CIPHERS, OpenSsl.availableJavaCipherSuites());
        final LinkedHashSet<String> suitesSet = new LinkedHashSet<String>(suites.length);
        Collections.addAll(suitesSet, suites);
        this.unmodifiableCiphers = new ArrayList<String>(suitesSet);
        this.apn = ObjectUtil.checkNotNull(apn, "apn");
        boolean success = false;
        try {
            final boolean tlsv13Supported = OpenSsl.isTlsv13Supported();
            try {
                int protocolOpts = 30;
                if (tlsv13Supported) {
                    protocolOpts |= 0x20;
                }
                this.ctx = SSLContext.make(protocolOpts, mode);
            }
            catch (final Exception e) {
                throw new SSLException("failed to create an SSL_CTX", e);
            }
            final StringBuilder cipherBuilder = new StringBuilder();
            final StringBuilder cipherTLSv13Builder = new StringBuilder();
            try {
                if (this.unmodifiableCiphers.isEmpty()) {
                    SSLContext.setCipherSuite(this.ctx, "", false);
                    if (tlsv13Supported) {
                        SSLContext.setCipherSuite(this.ctx, "", true);
                    }
                }
                else {
                    CipherSuiteConverter.convertToCipherStrings(this.unmodifiableCiphers, cipherBuilder, cipherTLSv13Builder, OpenSsl.isBoringSSL());
                    SSLContext.setCipherSuite(this.ctx, cipherBuilder.toString(), false);
                    if (tlsv13Supported) {
                        SSLContext.setCipherSuite(this.ctx, OpenSsl.checkTls13Ciphers(ReferenceCountedOpenSslContext.logger, cipherTLSv13Builder.toString()), true);
                    }
                }
            }
            catch (final SSLException e2) {
                throw e2;
            }
            catch (final Exception e3) {
                throw new SSLException("failed to set cipher suite: " + this.unmodifiableCiphers, e3);
            }
            int options = SSLContext.getOptions(this.ctx) | SSL.SSL_OP_NO_SSLv2 | SSL.SSL_OP_NO_SSLv3 | SSL.SSL_OP_NO_TLSv1 | SSL.SSL_OP_NO_TLSv1_1 | SSL.SSL_OP_CIPHER_SERVER_PREFERENCE | SSL.SSL_OP_NO_COMPRESSION | SSL.SSL_OP_NO_TICKET;
            if (cipherBuilder.length() == 0) {
                options |= (SSL.SSL_OP_NO_SSLv2 | SSL.SSL_OP_NO_SSLv3 | SSL.SSL_OP_NO_TLSv1 | SSL.SSL_OP_NO_TLSv1_1 | SSL.SSL_OP_NO_TLSv1_2);
            }
            SSLContext.setOptions(this.ctx, options);
            SSLContext.setMode(this.ctx, SSLContext.getMode(this.ctx) | SSL.SSL_MODE_ACCEPT_MOVING_WRITE_BUFFER);
            if (ReferenceCountedOpenSslContext.DH_KEY_LENGTH != null) {
                SSLContext.setTmpDHLength(this.ctx, (int)ReferenceCountedOpenSslContext.DH_KEY_LENGTH);
            }
            final List<String> nextProtoList = apn.protocols();
            if (!nextProtoList.isEmpty()) {
                final String[] appProtocols = nextProtoList.toArray(new String[0]);
                final int selectorBehavior = opensslSelectorFailureBehavior(apn.selectorFailureBehavior());
                switch (apn.protocol()) {
                    case NPN: {
                        SSLContext.setNpnProtos(this.ctx, appProtocols, selectorBehavior);
                        break;
                    }
                    case ALPN: {
                        SSLContext.setAlpnProtos(this.ctx, appProtocols, selectorBehavior);
                        break;
                    }
                    case NPN_AND_ALPN: {
                        SSLContext.setNpnProtos(this.ctx, appProtocols, selectorBehavior);
                        SSLContext.setAlpnProtos(this.ctx, appProtocols, selectorBehavior);
                        break;
                    }
                    default: {
                        throw new Error();
                    }
                }
            }
            if (enableOcsp) {
                SSLContext.enableOcsp(this.ctx, this.isClient());
            }
            SSLContext.setUseTasks(this.ctx, useTasks);
            if (privateKeyMethod != null) {
                SSLContext.setPrivateKeyMethod(this.ctx, (SSLPrivateKeyMethod)new PrivateKeyMethod(this.engineMap, privateKeyMethod));
            }
            if (asyncPrivateKeyMethod != null) {
                SSLContext.setPrivateKeyMethod(this.ctx, (AsyncSSLPrivateKeyMethod)new AsyncPrivateKeyMethod(this.engineMap, asyncPrivateKeyMethod));
            }
            SSLContext.setCurvesList(this.ctx, OpenSsl.NAMED_GROUPS);
            success = true;
        }
        finally {
            if (!success) {
                this.release();
            }
        }
    }
    
    private static int opensslSelectorFailureBehavior(final ApplicationProtocolConfig.SelectorFailureBehavior behavior) {
        switch (behavior) {
            case NO_ADVERTISE: {
                return 0;
            }
            case CHOOSE_MY_LAST_PROTOCOL: {
                return 1;
            }
            default: {
                throw new Error();
            }
        }
    }
    
    @Override
    public final List<String> cipherSuites() {
        return this.unmodifiableCiphers;
    }
    
    @Override
    public ApplicationProtocolNegotiator applicationProtocolNegotiator() {
        return this.apn;
    }
    
    @Override
    public final boolean isClient() {
        return this.mode == 0;
    }
    
    @Override
    public final SSLEngine newEngine(final ByteBufAllocator alloc, final String peerHost, final int peerPort) {
        return this.newEngine0(alloc, peerHost, peerPort, true);
    }
    
    @Override
    protected final SslHandler newHandler(final ByteBufAllocator alloc, final boolean startTls) {
        return new SslHandler(this.newEngine0(alloc, null, -1, false), startTls);
    }
    
    @Override
    protected final SslHandler newHandler(final ByteBufAllocator alloc, final String peerHost, final int peerPort, final boolean startTls) {
        return new SslHandler(this.newEngine0(alloc, peerHost, peerPort, false), startTls);
    }
    
    @Override
    protected SslHandler newHandler(final ByteBufAllocator alloc, final boolean startTls, final Executor executor) {
        return new SslHandler(this.newEngine0(alloc, null, -1, false), startTls, executor);
    }
    
    @Override
    protected SslHandler newHandler(final ByteBufAllocator alloc, final String peerHost, final int peerPort, final boolean startTls, final Executor executor) {
        return new SslHandler(this.newEngine0(alloc, peerHost, peerPort, false), executor);
    }
    
    SSLEngine newEngine0(final ByteBufAllocator alloc, final String peerHost, final int peerPort, final boolean jdkCompatibilityMode) {
        return new ReferenceCountedOpenSslEngine(this, alloc, peerHost, peerPort, jdkCompatibilityMode, true);
    }
    
    @Override
    public final SSLEngine newEngine(final ByteBufAllocator alloc) {
        return this.newEngine(alloc, null, -1);
    }
    
    @Deprecated
    public final long context() {
        return this.sslCtxPointer();
    }
    
    @Deprecated
    public final OpenSslSessionStats stats() {
        return this.sessionContext().stats();
    }
    
    @Deprecated
    public void setRejectRemoteInitiatedRenegotiation(final boolean rejectRemoteInitiatedRenegotiation) {
        if (!rejectRemoteInitiatedRenegotiation) {
            throw new UnsupportedOperationException("Renegotiation is not supported");
        }
    }
    
    @Deprecated
    public boolean getRejectRemoteInitiatedRenegotiation() {
        return true;
    }
    
    public void setBioNonApplicationBufferSize(final int bioNonApplicationBufferSize) {
        this.bioNonApplicationBufferSize = ObjectUtil.checkPositiveOrZero(bioNonApplicationBufferSize, "bioNonApplicationBufferSize");
    }
    
    public int getBioNonApplicationBufferSize() {
        return this.bioNonApplicationBufferSize;
    }
    
    @Deprecated
    public final void setTicketKeys(final byte[] keys) {
        this.sessionContext().setTicketKeys(keys);
    }
    
    @Override
    public abstract OpenSslSessionContext sessionContext();
    
    @Deprecated
    public final long sslCtxPointer() {
        final Lock readerLock = this.ctxLock.readLock();
        readerLock.lock();
        try {
            return SSLContext.getSslCtx(this.ctx);
        }
        finally {
            readerLock.unlock();
        }
    }
    
    @Deprecated
    public final void setPrivateKeyMethod(final OpenSslPrivateKeyMethod method) {
        ObjectUtil.checkNotNull(method, "method");
        final Lock writerLock = this.ctxLock.writeLock();
        writerLock.lock();
        try {
            SSLContext.setPrivateKeyMethod(this.ctx, (SSLPrivateKeyMethod)new PrivateKeyMethod(this.engineMap, method));
        }
        finally {
            writerLock.unlock();
        }
    }
    
    @Deprecated
    public final void setUseTasks(final boolean useTasks) {
        final Lock writerLock = this.ctxLock.writeLock();
        writerLock.lock();
        try {
            SSLContext.setUseTasks(this.ctx, useTasks);
        }
        finally {
            writerLock.unlock();
        }
    }
    
    private void destroy() {
        final Lock writerLock = this.ctxLock.writeLock();
        writerLock.lock();
        try {
            if (this.ctx != 0L) {
                if (this.enableOcsp) {
                    SSLContext.disableOcsp(this.ctx);
                }
                SSLContext.free(this.ctx);
                this.ctx = 0L;
                final OpenSslSessionContext context = this.sessionContext();
                if (context != null) {
                    context.destroy();
                }
            }
        }
        finally {
            writerLock.unlock();
        }
    }
    
    protected static X509Certificate[] certificates(final byte[][] chain) {
        final X509Certificate[] peerCerts = new X509Certificate[chain.length];
        for (int i = 0; i < peerCerts.length; ++i) {
            peerCerts[i] = new LazyX509Certificate(chain[i]);
        }
        return peerCerts;
    }
    
    protected static X509TrustManager chooseTrustManager(final TrustManager[] managers) {
        final int length = managers.length;
        int i = 0;
        while (i < length) {
            final TrustManager m = managers[i];
            if (m instanceof X509TrustManager) {
                if (PlatformDependent.javaVersion() >= 7) {
                    return OpenSslX509TrustManagerWrapper.wrapIfNeeded((X509TrustManager)m);
                }
                return (X509TrustManager)m;
            }
            else {
                ++i;
            }
        }
        throw new IllegalStateException("no X509TrustManager found");
    }
    
    protected static X509KeyManager chooseX509KeyManager(final KeyManager[] kms) {
        for (final KeyManager km : kms) {
            if (km instanceof X509KeyManager) {
                return (X509KeyManager)km;
            }
        }
        throw new IllegalStateException("no X509KeyManager found");
    }
    
    static OpenSslApplicationProtocolNegotiator toNegotiator(final ApplicationProtocolConfig config) {
        if (config == null) {
            return ReferenceCountedOpenSslContext.NONE_PROTOCOL_NEGOTIATOR;
        }
        switch (config.protocol()) {
            case NONE: {
                return ReferenceCountedOpenSslContext.NONE_PROTOCOL_NEGOTIATOR;
            }
            case NPN:
            case ALPN:
            case NPN_AND_ALPN: {
                switch (config.selectedListenerFailureBehavior()) {
                    case CHOOSE_MY_LAST_PROTOCOL:
                    case ACCEPT: {
                        switch (config.selectorFailureBehavior()) {
                            case NO_ADVERTISE:
                            case CHOOSE_MY_LAST_PROTOCOL: {
                                return new OpenSslDefaultApplicationProtocolNegotiator(config);
                            }
                            default: {
                                throw new UnsupportedOperationException("OpenSSL provider does not support " + config.selectorFailureBehavior() + " behavior");
                            }
                        }
                        break;
                    }
                    default: {
                        throw new UnsupportedOperationException("OpenSSL provider does not support " + config.selectedListenerFailureBehavior() + " behavior");
                    }
                }
                break;
            }
            default: {
                throw new Error();
            }
        }
    }
    
    @SuppressJava6Requirement(reason = "Guarded by java version check")
    static boolean useExtendedTrustManager(final X509TrustManager trustManager) {
        return PlatformDependent.javaVersion() >= 7 && trustManager instanceof X509ExtendedTrustManager;
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
    
    static void setKeyMaterial(final long ctx, final X509Certificate[] keyCertChain, final PrivateKey key, final String keyPassword) throws SSLException {
        long keyBio = 0L;
        long keyCertChainBio = 0L;
        long keyCertChainBio2 = 0L;
        PemEncoded encoded = null;
        try {
            encoded = PemX509Certificate.toPEM(ByteBufAllocator.DEFAULT, true, keyCertChain);
            keyCertChainBio = toBIO(ByteBufAllocator.DEFAULT, encoded.retain());
            keyCertChainBio2 = toBIO(ByteBufAllocator.DEFAULT, encoded.retain());
            if (key != null) {
                keyBio = toBIO(ByteBufAllocator.DEFAULT, key);
            }
            SSLContext.setCertificateBio(ctx, keyCertChainBio, keyBio, (keyPassword == null) ? "" : keyPassword);
            SSLContext.setCertificateChainBio(ctx, keyCertChainBio2, true);
        }
        catch (final SSLException e) {
            throw e;
        }
        catch (final Exception e2) {
            throw new SSLException("failed to set certificate and key", e2);
        }
        finally {
            freeBio(keyBio);
            freeBio(keyCertChainBio);
            freeBio(keyCertChainBio2);
            if (encoded != null) {
                encoded.release();
            }
        }
    }
    
    static void freeBio(final long bio) {
        if (bio != 0L) {
            SSL.freeBIO(bio);
        }
    }
    
    static long toBIO(final ByteBufAllocator allocator, final PrivateKey key) throws Exception {
        if (key == null) {
            return 0L;
        }
        final PemEncoded pem = PemPrivateKey.toPEM(allocator, true, key);
        try {
            return toBIO(allocator, pem.retain());
        }
        finally {
            pem.release();
        }
    }
    
    static long toBIO(final ByteBufAllocator allocator, final X509Certificate... certChain) throws Exception {
        if (certChain == null) {
            return 0L;
        }
        ObjectUtil.checkNonEmpty(certChain, "certChain");
        final PemEncoded pem = PemX509Certificate.toPEM(allocator, true, certChain);
        try {
            return toBIO(allocator, pem.retain());
        }
        finally {
            pem.release();
        }
    }
    
    static long toBIO(final ByteBufAllocator allocator, final PemEncoded pem) throws Exception {
        try {
            final ByteBuf content = pem.content();
            if (content.isDirect()) {
                return newBIO(content.retainedSlice());
            }
            final ByteBuf buffer = allocator.directBuffer(content.readableBytes());
            try {
                buffer.writeBytes(content, content.readerIndex(), content.readableBytes());
                return newBIO(buffer.retainedSlice());
            }
            finally {
                try {
                    if (pem.isSensitive()) {
                        SslUtils.zeroout(buffer);
                    }
                }
                finally {
                    buffer.release();
                }
            }
        }
        finally {
            pem.release();
        }
    }
    
    private static long newBIO(final ByteBuf buffer) throws Exception {
        try {
            final long bio = SSL.newMemBIO();
            final int readable = buffer.readableBytes();
            if (SSL.bioWrite(bio, OpenSsl.memoryAddress(buffer) + buffer.readerIndex(), readable) != readable) {
                SSL.freeBIO(bio);
                throw new IllegalStateException("Could not write data to memory BIO");
            }
            return bio;
        }
        finally {
            buffer.release();
        }
    }
    
    static OpenSslKeyMaterialProvider providerFor(final KeyManagerFactory factory, final String password) {
        if (factory instanceof OpenSslX509KeyManagerFactory) {
            return ((OpenSslX509KeyManagerFactory)factory).newProvider();
        }
        if (factory instanceof OpenSslCachingX509KeyManagerFactory) {
            return ((OpenSslCachingX509KeyManagerFactory)factory).newProvider(password);
        }
        return new OpenSslKeyMaterialProvider(chooseX509KeyManager(factory.getKeyManagers()), password);
    }
    
    private static byte[] verifyResult(final byte[] result) throws SignatureException {
        if (result == null) {
            throw new SignatureException();
        }
        return result;
    }
    
    static {
        logger = InternalLoggerFactory.getInstance(ReferenceCountedOpenSslContext.class);
        DEFAULT_BIO_NON_APPLICATION_BUFFER_SIZE = Math.max(1, SystemPropertyUtil.getInt("io.netty.handler.ssl.openssl.bioNonApplicationBufferSize", 2048));
        USE_TASKS = SystemPropertyUtil.getBoolean("io.netty.handler.ssl.openssl.useTasks", true);
        leakDetector = ResourceLeakDetectorFactory.instance().newResourceLeakDetector(ReferenceCountedOpenSslContext.class);
        CLIENT_ENABLE_SESSION_TICKET = SystemPropertyUtil.getBoolean("jdk.tls.client.enableSessionTicketExtension", false);
        CLIENT_ENABLE_SESSION_TICKET_TLSV13 = SystemPropertyUtil.getBoolean("jdk.tls.client.enableSessionTicketExtension", true);
        SERVER_ENABLE_SESSION_TICKET = SystemPropertyUtil.getBoolean("jdk.tls.server.enableSessionTicketExtension", false);
        SERVER_ENABLE_SESSION_TICKET_TLSV13 = SystemPropertyUtil.getBoolean("jdk.tls.server.enableSessionTicketExtension", true);
        SERVER_ENABLE_SESSION_CACHE = SystemPropertyUtil.getBoolean("io.netty.handler.ssl.openssl.sessionCacheServer", true);
        CLIENT_ENABLE_SESSION_CACHE = SystemPropertyUtil.getBoolean("io.netty.handler.ssl.openssl.sessionCacheClient", false);
        NONE_PROTOCOL_NEGOTIATOR = new OpenSslApplicationProtocolNegotiator() {
            @Override
            public ApplicationProtocolConfig.Protocol protocol() {
                return ApplicationProtocolConfig.Protocol.NONE;
            }
            
            @Override
            public List<String> protocols() {
                return Collections.emptyList();
            }
            
            @Override
            public ApplicationProtocolConfig.SelectorFailureBehavior selectorFailureBehavior() {
                return ApplicationProtocolConfig.SelectorFailureBehavior.CHOOSE_MY_LAST_PROTOCOL;
            }
            
            @Override
            public ApplicationProtocolConfig.SelectedListenerFailureBehavior selectedListenerFailureBehavior() {
                return ApplicationProtocolConfig.SelectedListenerFailureBehavior.ACCEPT;
            }
        };
        Integer dhLen = null;
        try {
            final String dhKeySize = SystemPropertyUtil.get("jdk.tls.ephemeralDHKeySize");
            if (dhKeySize != null) {
                try {
                    dhLen = Integer.valueOf(dhKeySize);
                }
                catch (final NumberFormatException e) {
                    ReferenceCountedOpenSslContext.logger.debug("ReferenceCountedOpenSslContext supports -Djdk.tls.ephemeralDHKeySize={int}, but got: " + dhKeySize);
                }
            }
        }
        catch (final Throwable t) {}
        DH_KEY_LENGTH = dhLen;
    }
    
    abstract static class AbstractCertificateVerifier extends CertificateVerifier
    {
        private final OpenSslEngineMap engineMap;
        
        AbstractCertificateVerifier(final OpenSslEngineMap engineMap) {
            this.engineMap = engineMap;
        }
        
        public final int verify(final long ssl, final byte[][] chain, final String auth) {
            final ReferenceCountedOpenSslEngine engine = this.engineMap.get(ssl);
            if (engine == null) {
                return CertificateVerifier.X509_V_ERR_UNSPECIFIED;
            }
            final X509Certificate[] peerCerts = ReferenceCountedOpenSslContext.certificates(chain);
            try {
                this.verify(engine, peerCerts, auth);
                return CertificateVerifier.X509_V_OK;
            }
            catch (final Throwable cause) {
                ReferenceCountedOpenSslContext.logger.debug("verification of certificate failed", cause);
                engine.initHandshakeException(cause);
                if (cause instanceof OpenSslCertificateException) {
                    return ((OpenSslCertificateException)cause).errorCode();
                }
                if (cause instanceof CertificateExpiredException) {
                    return CertificateVerifier.X509_V_ERR_CERT_HAS_EXPIRED;
                }
                if (cause instanceof CertificateNotYetValidException) {
                    return CertificateVerifier.X509_V_ERR_CERT_NOT_YET_VALID;
                }
                if (PlatformDependent.javaVersion() >= 7) {
                    return translateToError(cause);
                }
                return CertificateVerifier.X509_V_ERR_UNSPECIFIED;
            }
        }
        
        @SuppressJava6Requirement(reason = "Usage guarded by java version check")
        private static int translateToError(final Throwable cause) {
            if (cause instanceof CertificateRevokedException) {
                return CertificateVerifier.X509_V_ERR_CERT_REVOKED;
            }
            for (Throwable wrapped = cause.getCause(); wrapped != null; wrapped = wrapped.getCause()) {
                if (wrapped instanceof CertPathValidatorException) {
                    final CertPathValidatorException ex = (CertPathValidatorException)wrapped;
                    final CertPathValidatorException.Reason reason = ex.getReason();
                    if (reason == CertPathValidatorException.BasicReason.EXPIRED) {
                        return CertificateVerifier.X509_V_ERR_CERT_HAS_EXPIRED;
                    }
                    if (reason == CertPathValidatorException.BasicReason.NOT_YET_VALID) {
                        return CertificateVerifier.X509_V_ERR_CERT_NOT_YET_VALID;
                    }
                    if (reason == CertPathValidatorException.BasicReason.REVOKED) {
                        return CertificateVerifier.X509_V_ERR_CERT_REVOKED;
                    }
                }
            }
            return CertificateVerifier.X509_V_ERR_UNSPECIFIED;
        }
        
        abstract void verify(final ReferenceCountedOpenSslEngine p0, final X509Certificate[] p1, final String p2) throws Exception;
    }
    
    private static final class DefaultOpenSslEngineMap implements OpenSslEngineMap
    {
        private final Map<Long, ReferenceCountedOpenSslEngine> engines;
        
        private DefaultOpenSslEngineMap() {
            this.engines = (Map<Long, ReferenceCountedOpenSslEngine>)PlatformDependent.newConcurrentHashMap();
        }
        
        @Override
        public ReferenceCountedOpenSslEngine remove(final long ssl) {
            return this.engines.remove(ssl);
        }
        
        @Override
        public void add(final ReferenceCountedOpenSslEngine engine) {
            this.engines.put(engine.sslPointer(), engine);
        }
        
        @Override
        public ReferenceCountedOpenSslEngine get(final long ssl) {
            return this.engines.get(ssl);
        }
    }
    
    private static final class PrivateKeyMethod implements SSLPrivateKeyMethod
    {
        private final OpenSslEngineMap engineMap;
        private final OpenSslPrivateKeyMethod keyMethod;
        
        PrivateKeyMethod(final OpenSslEngineMap engineMap, final OpenSslPrivateKeyMethod keyMethod) {
            this.engineMap = engineMap;
            this.keyMethod = keyMethod;
        }
        
        private ReferenceCountedOpenSslEngine retrieveEngine(final long ssl) throws SSLException {
            final ReferenceCountedOpenSslEngine engine = this.engineMap.get(ssl);
            if (engine == null) {
                throw new SSLException("Could not find a " + StringUtil.simpleClassName(ReferenceCountedOpenSslEngine.class) + " for sslPointer " + ssl);
            }
            return engine;
        }
        
        public byte[] sign(final long ssl, final int signatureAlgorithm, final byte[] digest) throws Exception {
            final ReferenceCountedOpenSslEngine engine = this.retrieveEngine(ssl);
            try {
                return verifyResult(this.keyMethod.sign(engine, signatureAlgorithm, digest));
            }
            catch (final Exception e) {
                engine.initHandshakeException(e);
                throw e;
            }
        }
        
        public byte[] decrypt(final long ssl, final byte[] input) throws Exception {
            final ReferenceCountedOpenSslEngine engine = this.retrieveEngine(ssl);
            try {
                return verifyResult(this.keyMethod.decrypt(engine, input));
            }
            catch (final Exception e) {
                engine.initHandshakeException(e);
                throw e;
            }
        }
    }
    
    private static final class AsyncPrivateKeyMethod implements AsyncSSLPrivateKeyMethod
    {
        private final OpenSslEngineMap engineMap;
        private final OpenSslAsyncPrivateKeyMethod keyMethod;
        
        AsyncPrivateKeyMethod(final OpenSslEngineMap engineMap, final OpenSslAsyncPrivateKeyMethod keyMethod) {
            this.engineMap = engineMap;
            this.keyMethod = keyMethod;
        }
        
        private ReferenceCountedOpenSslEngine retrieveEngine(final long ssl) throws SSLException {
            final ReferenceCountedOpenSslEngine engine = this.engineMap.get(ssl);
            if (engine == null) {
                throw new SSLException("Could not find a " + StringUtil.simpleClassName(ReferenceCountedOpenSslEngine.class) + " for sslPointer " + ssl);
            }
            return engine;
        }
        
        public void sign(final long ssl, final int signatureAlgorithm, final byte[] bytes, final ResultCallback<byte[]> resultCallback) {
            try {
                final ReferenceCountedOpenSslEngine engine = this.retrieveEngine(ssl);
                this.keyMethod.sign(engine, signatureAlgorithm, bytes).addListener(new ResultCallbackListener(engine, ssl, resultCallback));
            }
            catch (final SSLException e) {
                resultCallback.onError(ssl, (Throwable)e);
            }
        }
        
        public void decrypt(final long ssl, final byte[] bytes, final ResultCallback<byte[]> resultCallback) {
            try {
                final ReferenceCountedOpenSslEngine engine = this.retrieveEngine(ssl);
                this.keyMethod.decrypt(engine, bytes).addListener(new ResultCallbackListener(engine, ssl, resultCallback));
            }
            catch (final SSLException e) {
                resultCallback.onError(ssl, (Throwable)e);
            }
        }
        
        private static final class ResultCallbackListener implements FutureListener<byte[]>
        {
            private final ReferenceCountedOpenSslEngine engine;
            private final long ssl;
            private final ResultCallback<byte[]> resultCallback;
            
            ResultCallbackListener(final ReferenceCountedOpenSslEngine engine, final long ssl, final ResultCallback<byte[]> resultCallback) {
                this.engine = engine;
                this.ssl = ssl;
                this.resultCallback = resultCallback;
            }
            
            @Override
            public void operationComplete(final Future<byte[]> future) {
                Throwable cause = future.cause();
                if (cause == null) {
                    try {
                        final byte[] result = verifyResult(future.getNow());
                        this.resultCallback.onSuccess(this.ssl, (Object)result);
                        return;
                    }
                    catch (final SignatureException e) {
                        cause = e;
                        this.engine.initHandshakeException(e);
                    }
                }
                this.resultCallback.onError(this.ssl, cause);
            }
        }
    }
}
