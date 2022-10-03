package io.netty.handler.ssl;

import javax.net.ssl.SSLSessionContext;
import java.security.cert.Certificate;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.security.KeyStore;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.SSLException;
import java.io.File;

public final class OpenSslServerContext extends OpenSslContext
{
    private final OpenSslServerSessionContext sessionContext;
    
    @Deprecated
    public OpenSslServerContext(final File certChainFile, final File keyFile) throws SSLException {
        this(certChainFile, keyFile, null);
    }
    
    @Deprecated
    public OpenSslServerContext(final File certChainFile, final File keyFile, final String keyPassword) throws SSLException {
        this(certChainFile, keyFile, keyPassword, null, IdentityCipherSuiteFilter.INSTANCE, ApplicationProtocolConfig.DISABLED, 0L, 0L);
    }
    
    @Deprecated
    public OpenSslServerContext(final File certChainFile, final File keyFile, final String keyPassword, final Iterable<String> ciphers, final ApplicationProtocolConfig apn, final long sessionCacheSize, final long sessionTimeout) throws SSLException {
        this(certChainFile, keyFile, keyPassword, ciphers, IdentityCipherSuiteFilter.INSTANCE, apn, sessionCacheSize, sessionTimeout);
    }
    
    @Deprecated
    public OpenSslServerContext(final File certChainFile, final File keyFile, final String keyPassword, final Iterable<String> ciphers, final Iterable<String> nextProtocols, final long sessionCacheSize, final long sessionTimeout) throws SSLException {
        this(certChainFile, keyFile, keyPassword, ciphers, SslContext.toApplicationProtocolConfig(nextProtocols), sessionCacheSize, sessionTimeout);
    }
    
    @Deprecated
    public OpenSslServerContext(final File certChainFile, final File keyFile, final String keyPassword, final TrustManagerFactory trustManagerFactory, final Iterable<String> ciphers, final ApplicationProtocolConfig config, final long sessionCacheSize, final long sessionTimeout) throws SSLException {
        this(certChainFile, keyFile, keyPassword, trustManagerFactory, ciphers, ReferenceCountedOpenSslContext.toNegotiator(config), sessionCacheSize, sessionTimeout);
    }
    
    @Deprecated
    public OpenSslServerContext(final File certChainFile, final File keyFile, final String keyPassword, final TrustManagerFactory trustManagerFactory, final Iterable<String> ciphers, final OpenSslApplicationProtocolNegotiator apn, final long sessionCacheSize, final long sessionTimeout) throws SSLException {
        this(null, trustManagerFactory, certChainFile, keyFile, keyPassword, null, ciphers, null, apn, sessionCacheSize, sessionTimeout);
    }
    
    @Deprecated
    public OpenSslServerContext(final File certChainFile, final File keyFile, final String keyPassword, final Iterable<String> ciphers, final CipherSuiteFilter cipherFilter, final ApplicationProtocolConfig apn, final long sessionCacheSize, final long sessionTimeout) throws SSLException {
        this(null, null, certChainFile, keyFile, keyPassword, null, ciphers, cipherFilter, apn, sessionCacheSize, sessionTimeout);
    }
    
    @Deprecated
    public OpenSslServerContext(final File trustCertCollectionFile, final TrustManagerFactory trustManagerFactory, final File keyCertChainFile, final File keyFile, final String keyPassword, final KeyManagerFactory keyManagerFactory, final Iterable<String> ciphers, final CipherSuiteFilter cipherFilter, final ApplicationProtocolConfig config, final long sessionCacheSize, final long sessionTimeout) throws SSLException {
        this(trustCertCollectionFile, trustManagerFactory, keyCertChainFile, keyFile, keyPassword, keyManagerFactory, ciphers, cipherFilter, ReferenceCountedOpenSslContext.toNegotiator(config), sessionCacheSize, sessionTimeout);
    }
    
    @Deprecated
    public OpenSslServerContext(final File certChainFile, final File keyFile, final String keyPassword, final TrustManagerFactory trustManagerFactory, final Iterable<String> ciphers, final CipherSuiteFilter cipherFilter, final ApplicationProtocolConfig config, final long sessionCacheSize, final long sessionTimeout) throws SSLException {
        this(null, trustManagerFactory, certChainFile, keyFile, keyPassword, null, ciphers, cipherFilter, ReferenceCountedOpenSslContext.toNegotiator(config), sessionCacheSize, sessionTimeout);
    }
    
    @Deprecated
    public OpenSslServerContext(final File certChainFile, final File keyFile, final String keyPassword, final TrustManagerFactory trustManagerFactory, final Iterable<String> ciphers, final CipherSuiteFilter cipherFilter, final OpenSslApplicationProtocolNegotiator apn, final long sessionCacheSize, final long sessionTimeout) throws SSLException {
        this(null, trustManagerFactory, certChainFile, keyFile, keyPassword, null, ciphers, cipherFilter, apn, sessionCacheSize, sessionTimeout);
    }
    
    @Deprecated
    public OpenSslServerContext(final File trustCertCollectionFile, final TrustManagerFactory trustManagerFactory, final File keyCertChainFile, final File keyFile, final String keyPassword, final KeyManagerFactory keyManagerFactory, final Iterable<String> ciphers, final CipherSuiteFilter cipherFilter, final OpenSslApplicationProtocolNegotiator apn, final long sessionCacheSize, final long sessionTimeout) throws SSLException {
        this(SslContext.toX509CertificatesInternal(trustCertCollectionFile), trustManagerFactory, SslContext.toX509CertificatesInternal(keyCertChainFile), SslContext.toPrivateKeyInternal(keyFile, keyPassword), keyPassword, keyManagerFactory, ciphers, cipherFilter, apn, sessionCacheSize, sessionTimeout, ClientAuth.NONE, null, false, false, KeyStore.getDefaultType(), (Map.Entry<SslContextOption<?>, Object>[])new Map.Entry[0]);
    }
    
    OpenSslServerContext(final X509Certificate[] trustCertCollection, final TrustManagerFactory trustManagerFactory, final X509Certificate[] keyCertChain, final PrivateKey key, final String keyPassword, final KeyManagerFactory keyManagerFactory, final Iterable<String> ciphers, final CipherSuiteFilter cipherFilter, final ApplicationProtocolConfig apn, final long sessionCacheSize, final long sessionTimeout, final ClientAuth clientAuth, final String[] protocols, final boolean startTls, final boolean enableOcsp, final String keyStore, final Map.Entry<SslContextOption<?>, Object>... options) throws SSLException {
        this(trustCertCollection, trustManagerFactory, keyCertChain, key, keyPassword, keyManagerFactory, ciphers, cipherFilter, ReferenceCountedOpenSslContext.toNegotiator(apn), sessionCacheSize, sessionTimeout, clientAuth, protocols, startTls, enableOcsp, keyStore, options);
    }
    
    private OpenSslServerContext(final X509Certificate[] trustCertCollection, final TrustManagerFactory trustManagerFactory, final X509Certificate[] keyCertChain, final PrivateKey key, final String keyPassword, final KeyManagerFactory keyManagerFactory, final Iterable<String> ciphers, final CipherSuiteFilter cipherFilter, final OpenSslApplicationProtocolNegotiator apn, final long sessionCacheSize, final long sessionTimeout, final ClientAuth clientAuth, final String[] protocols, final boolean startTls, final boolean enableOcsp, final String keyStore, final Map.Entry<SslContextOption<?>, Object>... options) throws SSLException {
        super(ciphers, cipherFilter, apn, 1, keyCertChain, clientAuth, protocols, startTls, enableOcsp, options);
        boolean success = false;
        try {
            OpenSslKeyMaterialProvider.validateKeyMaterialSupported(keyCertChain, key, keyPassword);
            this.sessionContext = ReferenceCountedOpenSslServerContext.newSessionContext(this, this.ctx, this.engineMap, trustCertCollection, trustManagerFactory, keyCertChain, key, keyPassword, keyManagerFactory, keyStore, sessionCacheSize, sessionTimeout);
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
}
