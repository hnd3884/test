package io.netty.handler.ssl;

import javax.net.ssl.SSLSessionContext;
import java.security.SecureRandom;
import javax.net.ssl.TrustManager;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.KeyManagerFactory;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.security.KeyStore;
import java.security.Provider;
import javax.net.ssl.SSLException;
import javax.net.ssl.TrustManagerFactory;
import java.io.File;

@Deprecated
public final class JdkSslClientContext extends JdkSslContext
{
    @Deprecated
    public JdkSslClientContext() throws SSLException {
        this(null, null);
    }
    
    @Deprecated
    public JdkSslClientContext(final File certChainFile) throws SSLException {
        this(certChainFile, null);
    }
    
    @Deprecated
    public JdkSslClientContext(final TrustManagerFactory trustManagerFactory) throws SSLException {
        this(null, trustManagerFactory);
    }
    
    @Deprecated
    public JdkSslClientContext(final File certChainFile, final TrustManagerFactory trustManagerFactory) throws SSLException {
        this(certChainFile, trustManagerFactory, null, IdentityCipherSuiteFilter.INSTANCE, JdkDefaultApplicationProtocolNegotiator.INSTANCE, 0L, 0L);
    }
    
    @Deprecated
    public JdkSslClientContext(final File certChainFile, final TrustManagerFactory trustManagerFactory, final Iterable<String> ciphers, final Iterable<String> nextProtocols, final long sessionCacheSize, final long sessionTimeout) throws SSLException {
        this(certChainFile, trustManagerFactory, ciphers, IdentityCipherSuiteFilter.INSTANCE, JdkSslContext.toNegotiator(SslContext.toApplicationProtocolConfig(nextProtocols), false), sessionCacheSize, sessionTimeout);
    }
    
    @Deprecated
    public JdkSslClientContext(final File certChainFile, final TrustManagerFactory trustManagerFactory, final Iterable<String> ciphers, final CipherSuiteFilter cipherFilter, final ApplicationProtocolConfig apn, final long sessionCacheSize, final long sessionTimeout) throws SSLException {
        this(certChainFile, trustManagerFactory, ciphers, cipherFilter, JdkSslContext.toNegotiator(apn, false), sessionCacheSize, sessionTimeout);
    }
    
    @Deprecated
    public JdkSslClientContext(final File certChainFile, final TrustManagerFactory trustManagerFactory, final Iterable<String> ciphers, final CipherSuiteFilter cipherFilter, final JdkApplicationProtocolNegotiator apn, final long sessionCacheSize, final long sessionTimeout) throws SSLException {
        this(null, certChainFile, trustManagerFactory, ciphers, cipherFilter, apn, sessionCacheSize, sessionTimeout);
    }
    
    JdkSslClientContext(final Provider provider, final File trustCertCollectionFile, final TrustManagerFactory trustManagerFactory, final Iterable<String> ciphers, final CipherSuiteFilter cipherFilter, final JdkApplicationProtocolNegotiator apn, final long sessionCacheSize, final long sessionTimeout) throws SSLException {
        super(newSSLContext(provider, SslContext.toX509CertificatesInternal(trustCertCollectionFile), trustManagerFactory, null, null, null, null, sessionCacheSize, sessionTimeout, KeyStore.getDefaultType()), true, ciphers, cipherFilter, apn, ClientAuth.NONE, null, false);
    }
    
    @Deprecated
    public JdkSslClientContext(final File trustCertCollectionFile, final TrustManagerFactory trustManagerFactory, final File keyCertChainFile, final File keyFile, final String keyPassword, final KeyManagerFactory keyManagerFactory, final Iterable<String> ciphers, final CipherSuiteFilter cipherFilter, final ApplicationProtocolConfig apn, final long sessionCacheSize, final long sessionTimeout) throws SSLException {
        this(trustCertCollectionFile, trustManagerFactory, keyCertChainFile, keyFile, keyPassword, keyManagerFactory, ciphers, cipherFilter, JdkSslContext.toNegotiator(apn, false), sessionCacheSize, sessionTimeout);
    }
    
    @Deprecated
    public JdkSslClientContext(final File trustCertCollectionFile, final TrustManagerFactory trustManagerFactory, final File keyCertChainFile, final File keyFile, final String keyPassword, final KeyManagerFactory keyManagerFactory, final Iterable<String> ciphers, final CipherSuiteFilter cipherFilter, final JdkApplicationProtocolNegotiator apn, final long sessionCacheSize, final long sessionTimeout) throws SSLException {
        super(newSSLContext(null, SslContext.toX509CertificatesInternal(trustCertCollectionFile), trustManagerFactory, SslContext.toX509CertificatesInternal(keyCertChainFile), SslContext.toPrivateKeyInternal(keyFile, keyPassword), keyPassword, keyManagerFactory, sessionCacheSize, sessionTimeout, KeyStore.getDefaultType()), true, ciphers, cipherFilter, apn, ClientAuth.NONE, null, false);
    }
    
    JdkSslClientContext(final Provider sslContextProvider, final X509Certificate[] trustCertCollection, final TrustManagerFactory trustManagerFactory, final X509Certificate[] keyCertChain, final PrivateKey key, final String keyPassword, final KeyManagerFactory keyManagerFactory, final Iterable<String> ciphers, final CipherSuiteFilter cipherFilter, final ApplicationProtocolConfig apn, final String[] protocols, final long sessionCacheSize, final long sessionTimeout, final String keyStoreType) throws SSLException {
        super(newSSLContext(sslContextProvider, trustCertCollection, trustManagerFactory, keyCertChain, key, keyPassword, keyManagerFactory, sessionCacheSize, sessionTimeout, keyStoreType), true, ciphers, cipherFilter, JdkSslContext.toNegotiator(apn, false), ClientAuth.NONE, protocols, false);
    }
    
    private static SSLContext newSSLContext(final Provider sslContextProvider, final X509Certificate[] trustCertCollection, TrustManagerFactory trustManagerFactory, final X509Certificate[] keyCertChain, final PrivateKey key, final String keyPassword, KeyManagerFactory keyManagerFactory, final long sessionCacheSize, final long sessionTimeout, final String keyStore) throws SSLException {
        try {
            if (trustCertCollection != null) {
                trustManagerFactory = SslContext.buildTrustManagerFactory(trustCertCollection, trustManagerFactory, keyStore);
            }
            if (keyCertChain != null) {
                keyManagerFactory = SslContext.buildKeyManagerFactory(keyCertChain, null, key, keyPassword, keyManagerFactory, keyStore);
            }
            final SSLContext ctx = (sslContextProvider == null) ? SSLContext.getInstance("TLS") : SSLContext.getInstance("TLS", sslContextProvider);
            ctx.init((KeyManager[])((keyManagerFactory == null) ? null : keyManagerFactory.getKeyManagers()), (TrustManager[])((trustManagerFactory == null) ? null : trustManagerFactory.getTrustManagers()), null);
            final SSLSessionContext sessCtx = ctx.getClientSessionContext();
            if (sessionCacheSize > 0L) {
                sessCtx.setSessionCacheSize((int)Math.min(sessionCacheSize, 2147483647L));
            }
            if (sessionTimeout > 0L) {
                sessCtx.setSessionTimeout((int)Math.min(sessionTimeout, 2147483647L));
            }
            return ctx;
        }
        catch (final Exception e) {
            if (e instanceof SSLException) {
                throw (SSLException)e;
            }
            throw new SSLException("failed to initialize the client-side SSL context", e);
        }
    }
}
