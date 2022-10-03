package io.netty.handler.ssl;

import javax.net.ssl.SSLSessionContext;
import java.security.cert.Certificate;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.security.KeyStore;
import javax.net.ssl.SSLException;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManagerFactory;
import java.io.File;

public final class OpenSslClientContext extends OpenSslContext
{
    private final OpenSslSessionContext sessionContext;
    
    @Deprecated
    public OpenSslClientContext() throws SSLException {
        this(null, null, null, null, null, null, null, IdentityCipherSuiteFilter.INSTANCE, null, 0L, 0L);
    }
    
    @Deprecated
    public OpenSslClientContext(final File certChainFile) throws SSLException {
        this(certChainFile, null);
    }
    
    @Deprecated
    public OpenSslClientContext(final TrustManagerFactory trustManagerFactory) throws SSLException {
        this(null, trustManagerFactory);
    }
    
    @Deprecated
    public OpenSslClientContext(final File certChainFile, final TrustManagerFactory trustManagerFactory) throws SSLException {
        this(certChainFile, trustManagerFactory, null, null, null, null, null, IdentityCipherSuiteFilter.INSTANCE, null, 0L, 0L);
    }
    
    @Deprecated
    public OpenSslClientContext(final File certChainFile, final TrustManagerFactory trustManagerFactory, final Iterable<String> ciphers, final ApplicationProtocolConfig apn, final long sessionCacheSize, final long sessionTimeout) throws SSLException {
        this(certChainFile, trustManagerFactory, null, null, null, null, ciphers, IdentityCipherSuiteFilter.INSTANCE, apn, sessionCacheSize, sessionTimeout);
    }
    
    @Deprecated
    public OpenSslClientContext(final File certChainFile, final TrustManagerFactory trustManagerFactory, final Iterable<String> ciphers, final CipherSuiteFilter cipherFilter, final ApplicationProtocolConfig apn, final long sessionCacheSize, final long sessionTimeout) throws SSLException {
        this(certChainFile, trustManagerFactory, null, null, null, null, ciphers, cipherFilter, apn, sessionCacheSize, sessionTimeout);
    }
    
    @Deprecated
    public OpenSslClientContext(final File trustCertCollectionFile, final TrustManagerFactory trustManagerFactory, final File keyCertChainFile, final File keyFile, final String keyPassword, final KeyManagerFactory keyManagerFactory, final Iterable<String> ciphers, final CipherSuiteFilter cipherFilter, final ApplicationProtocolConfig apn, final long sessionCacheSize, final long sessionTimeout) throws SSLException {
        this(SslContext.toX509CertificatesInternal(trustCertCollectionFile), trustManagerFactory, SslContext.toX509CertificatesInternal(keyCertChainFile), SslContext.toPrivateKeyInternal(keyFile, keyPassword), keyPassword, keyManagerFactory, ciphers, cipherFilter, apn, null, sessionCacheSize, sessionTimeout, false, KeyStore.getDefaultType(), (Map.Entry<SslContextOption<?>, Object>[])new Map.Entry[0]);
    }
    
    OpenSslClientContext(final X509Certificate[] trustCertCollection, final TrustManagerFactory trustManagerFactory, final X509Certificate[] keyCertChain, final PrivateKey key, final String keyPassword, final KeyManagerFactory keyManagerFactory, final Iterable<String> ciphers, final CipherSuiteFilter cipherFilter, final ApplicationProtocolConfig apn, final String[] protocols, final long sessionCacheSize, final long sessionTimeout, final boolean enableOcsp, final String keyStore, final Map.Entry<SslContextOption<?>, Object>... options) throws SSLException {
        super(ciphers, cipherFilter, apn, 0, keyCertChain, ClientAuth.NONE, protocols, false, enableOcsp, options);
        boolean success = false;
        try {
            OpenSslKeyMaterialProvider.validateKeyMaterialSupported(keyCertChain, key, keyPassword);
            this.sessionContext = ReferenceCountedOpenSslClientContext.newSessionContext(this, this.ctx, this.engineMap, trustCertCollection, trustManagerFactory, keyCertChain, key, keyPassword, keyManagerFactory, keyStore, sessionCacheSize, sessionTimeout);
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
}
