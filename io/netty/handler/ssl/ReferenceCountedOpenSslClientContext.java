package io.netty.handler.ssl;

import java.util.HashSet;
import javax.security.auth.x500.X500Principal;
import javax.net.ssl.SSLEngine;
import io.netty.internal.tcnative.SSL;
import java.util.Collections;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Arrays;
import javax.net.ssl.SSLSessionContext;
import io.netty.util.internal.SuppressJava6Requirement;
import io.netty.internal.tcnative.CertificateVerifier;
import javax.net.ssl.X509ExtendedTrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.KeyStore;
import io.netty.internal.tcnative.CertificateCallback;
import io.netty.internal.tcnative.SSLContext;
import javax.net.ssl.SSLException;
import java.security.cert.Certificate;
import java.util.Map;
import javax.net.ssl.KeyManagerFactory;
import java.security.PrivateKey;
import javax.net.ssl.TrustManagerFactory;
import java.security.cert.X509Certificate;
import java.util.Set;

public final class ReferenceCountedOpenSslClientContext extends ReferenceCountedOpenSslContext
{
    private static final Set<String> SUPPORTED_KEY_TYPES;
    private final OpenSslSessionContext sessionContext;
    
    ReferenceCountedOpenSslClientContext(final X509Certificate[] trustCertCollection, final TrustManagerFactory trustManagerFactory, final X509Certificate[] keyCertChain, final PrivateKey key, final String keyPassword, final KeyManagerFactory keyManagerFactory, final Iterable<String> ciphers, final CipherSuiteFilter cipherFilter, final ApplicationProtocolConfig apn, final String[] protocols, final long sessionCacheSize, final long sessionTimeout, final boolean enableOcsp, final String keyStore, final Map.Entry<SslContextOption<?>, Object>... options) throws SSLException {
        super(ciphers, cipherFilter, ReferenceCountedOpenSslContext.toNegotiator(apn), 0, keyCertChain, ClientAuth.NONE, protocols, false, enableOcsp, true, options);
        boolean success = false;
        try {
            this.sessionContext = newSessionContext(this, this.ctx, this.engineMap, trustCertCollection, trustManagerFactory, keyCertChain, key, keyPassword, keyManagerFactory, keyStore, sessionCacheSize, sessionTimeout);
            success = true;
        }
        finally {
            if (!success) {
                this.release();
            }
        }
    }
    
    @Override
    public OpenSslSessionContext sessionContext() {
        return this.sessionContext;
    }
    
    static OpenSslSessionContext newSessionContext(final ReferenceCountedOpenSslContext thiz, final long ctx, final OpenSslEngineMap engineMap, final X509Certificate[] trustCertCollection, TrustManagerFactory trustManagerFactory, final X509Certificate[] keyCertChain, final PrivateKey key, final String keyPassword, KeyManagerFactory keyManagerFactory, final String keyStore, final long sessionCacheSize, final long sessionTimeout) throws SSLException {
        if ((key == null && keyCertChain != null) || (key != null && keyCertChain == null)) {
            throw new IllegalArgumentException("Either both keyCertChain and key needs to be null or none of them");
        }
        OpenSslKeyMaterialProvider keyMaterialProvider = null;
        try {
            try {
                if (!OpenSsl.useKeyManagerFactory()) {
                    if (keyManagerFactory != null) {
                        throw new IllegalArgumentException("KeyManagerFactory not supported");
                    }
                    if (keyCertChain != null) {
                        ReferenceCountedOpenSslContext.setKeyMaterial(ctx, keyCertChain, key, keyPassword);
                    }
                }
                else {
                    if (keyManagerFactory == null && keyCertChain != null) {
                        final char[] keyPasswordChars = SslContext.keyStorePassword(keyPassword);
                        final KeyStore ks = SslContext.buildKeyStore(keyCertChain, key, keyPasswordChars, keyStore);
                        if (ks.aliases().hasMoreElements()) {
                            keyManagerFactory = new OpenSslX509KeyManagerFactory();
                        }
                        else {
                            keyManagerFactory = new OpenSslCachingX509KeyManagerFactory(KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm()));
                        }
                        keyManagerFactory.init(ks, keyPasswordChars);
                        keyMaterialProvider = ReferenceCountedOpenSslContext.providerFor(keyManagerFactory, keyPassword);
                    }
                    else if (keyManagerFactory != null) {
                        keyMaterialProvider = ReferenceCountedOpenSslContext.providerFor(keyManagerFactory, keyPassword);
                    }
                    if (keyMaterialProvider != null) {
                        final OpenSslKeyMaterialManager materialManager = new OpenSslKeyMaterialManager(keyMaterialProvider);
                        SSLContext.setCertificateCallback(ctx, (CertificateCallback)new OpenSslClientCertificateCallback(engineMap, materialManager));
                    }
                }
            }
            catch (final Exception e) {
                throw new SSLException("failed to set certificate and key", e);
            }
            SSLContext.setVerify(ctx, 1, 10);
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
            }
            catch (final Exception e) {
                if (keyMaterialProvider != null) {
                    keyMaterialProvider.destroy();
                }
                throw new SSLException("unable to setup trustmanager", e);
            }
            final OpenSslClientSessionContext context = new OpenSslClientSessionContext(thiz, keyMaterialProvider);
            context.setSessionCacheEnabled(ReferenceCountedOpenSslClientContext.CLIENT_ENABLE_SESSION_CACHE);
            if (sessionCacheSize > 0L) {
                context.setSessionCacheSize((int)Math.min(sessionCacheSize, 2147483647L));
            }
            if (sessionTimeout > 0L) {
                context.setSessionTimeout((int)Math.min(sessionTimeout, 2147483647L));
            }
            if (ReferenceCountedOpenSslClientContext.CLIENT_ENABLE_SESSION_TICKET) {
                context.setTicketKeys(new OpenSslSessionTicketKey[0]);
            }
            keyMaterialProvider = null;
            return context;
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
        SUPPORTED_KEY_TYPES = Collections.unmodifiableSet((Set<? extends String>)new LinkedHashSet<String>(Arrays.asList("RSA", "DH_RSA", "EC", "EC_RSA", "EC_EC")));
    }
    
    static final class OpenSslClientSessionContext extends OpenSslSessionContext
    {
        OpenSslClientSessionContext(final ReferenceCountedOpenSslContext context, final OpenSslKeyMaterialProvider provider) {
            super(context, provider, SSL.SSL_SESS_CACHE_CLIENT, new OpenSslClientSessionCache(context.engineMap));
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
            this.manager.checkServerTrusted(peerCerts, auth);
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
            this.manager.checkServerTrusted(peerCerts, auth, engine);
        }
    }
    
    private static final class OpenSslClientCertificateCallback implements CertificateCallback
    {
        private final OpenSslEngineMap engineMap;
        private final OpenSslKeyMaterialManager keyManagerHolder;
        
        OpenSslClientCertificateCallback(final OpenSslEngineMap engineMap, final OpenSslKeyMaterialManager keyManagerHolder) {
            this.engineMap = engineMap;
            this.keyManagerHolder = keyManagerHolder;
        }
        
        public void handle(final long ssl, final byte[] keyTypeBytes, final byte[][] asn1DerEncodedPrincipals) throws Exception {
            final ReferenceCountedOpenSslEngine engine = this.engineMap.get(ssl);
            if (engine == null) {
                return;
            }
            try {
                final Set<String> keyTypesSet = supportedClientKeyTypes(keyTypeBytes);
                final String[] keyTypes = keyTypesSet.toArray(new String[0]);
                X500Principal[] issuers;
                if (asn1DerEncodedPrincipals == null) {
                    issuers = null;
                }
                else {
                    issuers = new X500Principal[asn1DerEncodedPrincipals.length];
                    for (int i = 0; i < asn1DerEncodedPrincipals.length; ++i) {
                        issuers[i] = new X500Principal(asn1DerEncodedPrincipals[i]);
                    }
                }
                this.keyManagerHolder.setKeyMaterialClientSide(engine, keyTypes, issuers);
            }
            catch (final Throwable cause) {
                engine.initHandshakeException(cause);
                if (cause instanceof Exception) {
                    throw (Exception)cause;
                }
                throw new SSLException(cause);
            }
        }
        
        private static Set<String> supportedClientKeyTypes(final byte[] clientCertificateTypes) {
            if (clientCertificateTypes == null) {
                return ReferenceCountedOpenSslClientContext.SUPPORTED_KEY_TYPES;
            }
            final Set<String> result = new HashSet<String>(clientCertificateTypes.length);
            for (final byte keyTypeCode : clientCertificateTypes) {
                final String keyType = clientKeyType(keyTypeCode);
                if (keyType != null) {
                    result.add(keyType);
                }
            }
            return result;
        }
        
        private static String clientKeyType(final byte clientCertificateType) {
            switch (clientCertificateType) {
                case 1: {
                    return "RSA";
                }
                case 3: {
                    return "DH_RSA";
                }
                case 64: {
                    return "EC";
                }
                case 65: {
                    return "EC_RSA";
                }
                case 66: {
                    return "EC_EC";
                }
                default: {
                    return null;
                }
            }
        }
    }
}
