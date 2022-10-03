package io.netty.handler.ssl;

import io.netty.util.CharsetUtil;
import javax.net.ssl.SSLEngine;
import io.netty.util.internal.logging.InternalLoggerFactory;
import javax.net.ssl.SSLSessionContext;
import io.netty.util.internal.SuppressJava6Requirement;
import io.netty.internal.tcnative.CertificateVerifier;
import javax.net.ssl.X509ExtendedTrustManager;
import javax.net.ssl.X509TrustManager;
import io.netty.internal.tcnative.SniHostNameMatcher;
import io.netty.util.internal.PlatformDependent;
import io.netty.buffer.ByteBufAllocator;
import java.security.KeyStore;
import io.netty.internal.tcnative.CertificateCallback;
import io.netty.util.internal.ObjectUtil;
import io.netty.internal.tcnative.SSLContext;
import java.security.cert.Certificate;
import javax.net.ssl.SSLException;
import java.util.Map;
import javax.net.ssl.KeyManagerFactory;
import java.security.PrivateKey;
import javax.net.ssl.TrustManagerFactory;
import java.security.cert.X509Certificate;
import io.netty.util.internal.logging.InternalLogger;

public final class ReferenceCountedOpenSslServerContext extends ReferenceCountedOpenSslContext
{
    private static final InternalLogger logger;
    private static final byte[] ID;
    private final OpenSslServerSessionContext sessionContext;
    
    ReferenceCountedOpenSslServerContext(final X509Certificate[] trustCertCollection, final TrustManagerFactory trustManagerFactory, final X509Certificate[] keyCertChain, final PrivateKey key, final String keyPassword, final KeyManagerFactory keyManagerFactory, final Iterable<String> ciphers, final CipherSuiteFilter cipherFilter, final ApplicationProtocolConfig apn, final long sessionCacheSize, final long sessionTimeout, final ClientAuth clientAuth, final String[] protocols, final boolean startTls, final boolean enableOcsp, final String keyStore, final Map.Entry<SslContextOption<?>, Object>... options) throws SSLException {
        this(trustCertCollection, trustManagerFactory, keyCertChain, key, keyPassword, keyManagerFactory, ciphers, cipherFilter, ReferenceCountedOpenSslContext.toNegotiator(apn), sessionCacheSize, sessionTimeout, clientAuth, protocols, startTls, enableOcsp, keyStore, options);
    }
    
    ReferenceCountedOpenSslServerContext(final X509Certificate[] trustCertCollection, final TrustManagerFactory trustManagerFactory, final X509Certificate[] keyCertChain, final PrivateKey key, final String keyPassword, final KeyManagerFactory keyManagerFactory, final Iterable<String> ciphers, final CipherSuiteFilter cipherFilter, final OpenSslApplicationProtocolNegotiator apn, final long sessionCacheSize, final long sessionTimeout, final ClientAuth clientAuth, final String[] protocols, final boolean startTls, final boolean enableOcsp, final String keyStore, final Map.Entry<SslContextOption<?>, Object>... options) throws SSLException {
        super(ciphers, cipherFilter, apn, 1, keyCertChain, clientAuth, protocols, startTls, enableOcsp, true, options);
        boolean success = false;
        try {
            this.sessionContext = newSessionContext(this, this.ctx, this.engineMap, trustCertCollection, trustManagerFactory, keyCertChain, key, keyPassword, keyManagerFactory, keyStore, sessionCacheSize, sessionTimeout);
            if (ReferenceCountedOpenSslServerContext.SERVER_ENABLE_SESSION_TICKET) {
                this.sessionContext.setTicketKeys(new OpenSslSessionTicketKey[0]);
            }
            success = true;
        }
        finally {
            if (!success) {
                this.release();
            }
        }
    }
    
    @Override
    public OpenSslServerSessionContext sessionContext() {
        return this.sessionContext;
    }
    
    static OpenSslServerSessionContext newSessionContext(final ReferenceCountedOpenSslContext thiz, final long ctx, final OpenSslEngineMap engineMap, final X509Certificate[] trustCertCollection, TrustManagerFactory trustManagerFactory, final X509Certificate[] keyCertChain, final PrivateKey key, final String keyPassword, KeyManagerFactory keyManagerFactory, final String keyStore, final long sessionCacheSize, final long sessionTimeout) throws SSLException {
        OpenSslKeyMaterialProvider keyMaterialProvider = null;
        try {
            try {
                SSLContext.setVerify(ctx, 0, 10);
                if (!OpenSsl.useKeyManagerFactory()) {
                    if (keyManagerFactory != null) {
                        throw new IllegalArgumentException("KeyManagerFactory not supported");
                    }
                    ObjectUtil.checkNotNull(keyCertChain, "keyCertChain");
                    ReferenceCountedOpenSslContext.setKeyMaterial(ctx, keyCertChain, key, keyPassword);
                }
                else {
                    if (keyManagerFactory == null) {
                        final char[] keyPasswordChars = SslContext.keyStorePassword(keyPassword);
                        final KeyStore ks = SslContext.buildKeyStore(keyCertChain, key, keyPasswordChars, keyStore);
                        if (ks.aliases().hasMoreElements()) {
                            keyManagerFactory = new OpenSslX509KeyManagerFactory();
                        }
                        else {
                            keyManagerFactory = new OpenSslCachingX509KeyManagerFactory(KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm()));
                        }
                        keyManagerFactory.init(ks, keyPasswordChars);
                    }
                    keyMaterialProvider = ReferenceCountedOpenSslContext.providerFor(keyManagerFactory, keyPassword);
                    SSLContext.setCertificateCallback(ctx, (CertificateCallback)new OpenSslServerCertificateCallback(engineMap, new OpenSslKeyMaterialManager(keyMaterialProvider)));
                }
            }
            catch (final Exception e) {
                throw new SSLException("failed to set certificate and key", e);
            }
            try {
                if (trustCertCollection != null) {
                    trustManagerFactory = SslContext.buildTrustManagerFactory(trustCertCollection, trustManagerFactory, keyStore);
                }
                else if (trustManagerFactory == null) {
                    trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                    trustManagerFactory.init((KeyStore)null);
                }
                final X509TrustManager manager = ReferenceCountedOpenSslContext.chooseTrustManager(trustManagerFactory.getTrustManagers());
                setVerifyCallback(ctx, engineMap, manager);
                final X509Certificate[] issuers = manager.getAcceptedIssuers();
                if (issuers != null && issuers.length > 0) {
                    long bio = 0L;
                    try {
                        bio = ReferenceCountedOpenSslContext.toBIO(ByteBufAllocator.DEFAULT, issuers);
                        if (!SSLContext.setCACertificateBio(ctx, bio)) {
                            throw new SSLException("unable to setup accepted issuers for trustmanager " + manager);
                        }
                    }
                    finally {
                        ReferenceCountedOpenSslContext.freeBio(bio);
                    }
                }
                if (PlatformDependent.javaVersion() >= 8) {
                    SSLContext.setSniHostnameMatcher(ctx, (SniHostNameMatcher)new OpenSslSniHostnameMatcher(engineMap));
                }
            }
            catch (final SSLException e2) {
                throw e2;
            }
            catch (final Exception e) {
                throw new SSLException("unable to setup trustmanager", e);
            }
            final OpenSslServerSessionContext sessionContext = new OpenSslServerSessionContext(thiz, keyMaterialProvider);
            sessionContext.setSessionIdContext(ReferenceCountedOpenSslServerContext.ID);
            sessionContext.setSessionCacheEnabled(ReferenceCountedOpenSslServerContext.SERVER_ENABLE_SESSION_CACHE);
            if (sessionCacheSize > 0L) {
                sessionContext.setSessionCacheSize((int)Math.min(sessionCacheSize, 2147483647L));
            }
            if (sessionTimeout > 0L) {
                sessionContext.setSessionTimeout((int)Math.min(sessionTimeout, 2147483647L));
            }
            keyMaterialProvider = null;
            return sessionContext;
        }
        finally {
            if (keyMaterialProvider != null) {
                keyMaterialProvider.destroy();
            }
        }
    }
    
    @SuppressJava6Requirement(reason = "Guarded by java version check")
    private static void setVerifyCallback(final long ctx, final OpenSslEngineMap engineMap, final X509TrustManager manager) {
        if (ReferenceCountedOpenSslContext.useExtendedTrustManager(manager)) {
            SSLContext.setCertVerifyCallback(ctx, (CertificateVerifier)new ExtendedTrustManagerVerifyCallback(engineMap, (X509ExtendedTrustManager)manager));
        }
        else {
            SSLContext.setCertVerifyCallback(ctx, (CertificateVerifier)new TrustManagerVerifyCallback(engineMap, manager));
        }
    }
    
    static {
        logger = InternalLoggerFactory.getInstance(ReferenceCountedOpenSslServerContext.class);
        ID = new byte[] { 110, 101, 116, 116, 121 };
    }
    
    private static final class OpenSslServerCertificateCallback implements CertificateCallback
    {
        private final OpenSslEngineMap engineMap;
        private final OpenSslKeyMaterialManager keyManagerHolder;
        
        OpenSslServerCertificateCallback(final OpenSslEngineMap engineMap, final OpenSslKeyMaterialManager keyManagerHolder) {
            this.engineMap = engineMap;
            this.keyManagerHolder = keyManagerHolder;
        }
        
        public void handle(final long ssl, final byte[] keyTypeBytes, final byte[][] asn1DerEncodedPrincipals) throws Exception {
            final ReferenceCountedOpenSslEngine engine = this.engineMap.get(ssl);
            if (engine == null) {
                return;
            }
            try {
                this.keyManagerHolder.setKeyMaterialServerSide(engine);
            }
            catch (final Throwable cause) {
                engine.initHandshakeException(cause);
                if (cause instanceof Exception) {
                    throw (Exception)cause;
                }
                throw new SSLException(cause);
            }
        }
    }
    
    private static final class TrustManagerVerifyCallback extends AbstractCertificateVerifier
    {
        private final X509TrustManager manager;
        
        TrustManagerVerifyCallback(final OpenSslEngineMap engineMap, final X509TrustManager manager) {
            super(engineMap);
            this.manager = manager;
        }
        
        @Override
        void verify(final ReferenceCountedOpenSslEngine engine, final X509Certificate[] peerCerts, final String auth) throws Exception {
            this.manager.checkClientTrusted(peerCerts, auth);
        }
    }
    
    @SuppressJava6Requirement(reason = "Usage guarded by java version check")
    private static final class ExtendedTrustManagerVerifyCallback extends AbstractCertificateVerifier
    {
        private final X509ExtendedTrustManager manager;
        
        ExtendedTrustManagerVerifyCallback(final OpenSslEngineMap engineMap, final X509ExtendedTrustManager manager) {
            super(engineMap);
            this.manager = manager;
        }
        
        @Override
        void verify(final ReferenceCountedOpenSslEngine engine, final X509Certificate[] peerCerts, final String auth) throws Exception {
            this.manager.checkClientTrusted(peerCerts, auth, engine);
        }
    }
    
    private static final class OpenSslSniHostnameMatcher implements SniHostNameMatcher
    {
        private final OpenSslEngineMap engineMap;
        
        OpenSslSniHostnameMatcher(final OpenSslEngineMap engineMap) {
            this.engineMap = engineMap;
        }
        
        public boolean match(final long ssl, final String hostname) {
            final ReferenceCountedOpenSslEngine engine = this.engineMap.get(ssl);
            if (engine != null) {
                return engine.checkSniHostnameMatch(hostname.getBytes(CharsetUtil.UTF_8));
            }
            ReferenceCountedOpenSslServerContext.logger.warn("No ReferenceCountedOpenSslEngine found for SSL pointer: {}", (Object)ssl);
            return false;
        }
    }
}
