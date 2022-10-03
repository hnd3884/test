package io.netty.handler.ssl;

import javax.net.ssl.SSLSessionContext;
import java.security.SecureRandom;
import javax.net.ssl.TrustManager;
import javax.net.ssl.SSLContext;
import java.security.PrivateKey;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManagerFactory;
import java.security.cert.X509Certificate;
import java.security.KeyStore;
import javax.net.ssl.SSLException;
import java.security.Provider;
import java.io.File;

@Deprecated
public final class JdkSslServerContext extends JdkSslContext
{
    @Deprecated
    public JdkSslServerContext(final File certChainFile, final File keyFile) throws SSLException {
        this(null, certChainFile, keyFile, null, null, IdentityCipherSuiteFilter.INSTANCE, JdkDefaultApplicationProtocolNegotiator.INSTANCE, 0L, 0L, null);
    }
    
    @Deprecated
    public JdkSslServerContext(final File certChainFile, final File keyFile, final String keyPassword) throws SSLException {
        this(certChainFile, keyFile, keyPassword, null, IdentityCipherSuiteFilter.INSTANCE, JdkDefaultApplicationProtocolNegotiator.INSTANCE, 0L, 0L);
    }
    
    @Deprecated
    public JdkSslServerContext(final File certChainFile, final File keyFile, final String keyPassword, final Iterable<String> ciphers, final Iterable<String> nextProtocols, final long sessionCacheSize, final long sessionTimeout) throws SSLException {
        this(null, certChainFile, keyFile, keyPassword, ciphers, IdentityCipherSuiteFilter.INSTANCE, JdkSslContext.toNegotiator(SslContext.toApplicationProtocolConfig(nextProtocols), true), sessionCacheSize, sessionTimeout, KeyStore.getDefaultType());
    }
    
    @Deprecated
    public JdkSslServerContext(final File certChainFile, final File keyFile, final String keyPassword, final Iterable<String> ciphers, final CipherSuiteFilter cipherFilter, final ApplicationProtocolConfig apn, final long sessionCacheSize, final long sessionTimeout) throws SSLException {
        this(null, certChainFile, keyFile, keyPassword, ciphers, cipherFilter, JdkSslContext.toNegotiator(apn, true), sessionCacheSize, sessionTimeout, KeyStore.getDefaultType());
    }
    
    @Deprecated
    public JdkSslServerContext(final File certChainFile, final File keyFile, final String keyPassword, final Iterable<String> ciphers, final CipherSuiteFilter cipherFilter, final JdkApplicationProtocolNegotiator apn, final long sessionCacheSize, final long sessionTimeout) throws SSLException {
        this(null, certChainFile, keyFile, keyPassword, ciphers, cipherFilter, apn, sessionCacheSize, sessionTimeout, KeyStore.getDefaultType());
    }
    
    JdkSslServerContext(final Provider provider, final File certChainFile, final File keyFile, final String keyPassword, final Iterable<String> ciphers, final CipherSuiteFilter cipherFilter, final JdkApplicationProtocolNegotiator apn, final long sessionCacheSize, final long sessionTimeout, final String keyStore) throws SSLException {
        super(newSSLContext(provider, null, null, SslContext.toX509CertificatesInternal(certChainFile), SslContext.toPrivateKeyInternal(keyFile, keyPassword), keyPassword, null, sessionCacheSize, sessionTimeout, keyStore), false, ciphers, cipherFilter, apn, ClientAuth.NONE, null, false);
    }
    
    @Deprecated
    public JdkSslServerContext(final File trustCertCollectionFile, final TrustManagerFactory trustManagerFactory, final File keyCertChainFile, final File keyFile, final String keyPassword, final KeyManagerFactory keyManagerFactory, final Iterable<String> ciphers, final CipherSuiteFilter cipherFilter, final ApplicationProtocolConfig apn, final long sessionCacheSize, final long sessionTimeout) throws SSLException {
        super(newSSLContext(null, SslContext.toX509CertificatesInternal(trustCertCollectionFile), trustManagerFactory, SslContext.toX509CertificatesInternal(keyCertChainFile), SslContext.toPrivateKeyInternal(keyFile, keyPassword), keyPassword, keyManagerFactory, sessionCacheSize, sessionTimeout, null), false, ciphers, cipherFilter, apn, ClientAuth.NONE, null, false);
    }
    
    @Deprecated
    public JdkSslServerContext(final File trustCertCollectionFile, final TrustManagerFactory trustManagerFactory, final File keyCertChainFile, final File keyFile, final String keyPassword, final KeyManagerFactory keyManagerFactory, final Iterable<String> ciphers, final CipherSuiteFilter cipherFilter, final JdkApplicationProtocolNegotiator apn, final long sessionCacheSize, final long sessionTimeout) throws SSLException {
        super(newSSLContext(null, SslContext.toX509CertificatesInternal(trustCertCollectionFile), trustManagerFactory, SslContext.toX509CertificatesInternal(keyCertChainFile), SslContext.toPrivateKeyInternal(keyFile, keyPassword), keyPassword, keyManagerFactory, sessionCacheSize, sessionTimeout, KeyStore.getDefaultType()), false, ciphers, cipherFilter, apn, ClientAuth.NONE, null, false);
    }
    
    JdkSslServerContext(final Provider provider, final X509Certificate[] trustCertCollection, final TrustManagerFactory trustManagerFactory, final X509Certificate[] keyCertChain, final PrivateKey key, final String keyPassword, final KeyManagerFactory keyManagerFactory, final Iterable<String> ciphers, final CipherSuiteFilter cipherFilter, final ApplicationProtocolConfig apn, final long sessionCacheSize, final long sessionTimeout, final ClientAuth clientAuth, final String[] protocols, final boolean startTls, final String keyStore) throws SSLException {
        super(newSSLContext(provider, trustCertCollection, trustManagerFactory, keyCertChain, key, keyPassword, keyManagerFactory, sessionCacheSize, sessionTimeout, keyStore), false, ciphers, cipherFilter, JdkSslContext.toNegotiator(apn, true), clientAuth, protocols, startTls);
    }
    
    private static SSLContext newSSLContext(final Provider sslContextProvider, final X509Certificate[] trustCertCollection, TrustManagerFactory trustManagerFactory, final X509Certificate[] keyCertChain, final PrivateKey key, final String keyPassword, KeyManagerFactory keyManagerFactory, final long sessionCacheSize, final long sessionTimeout, final String keyStore) throws SSLException {
        if (key == null && keyManagerFactory == null) {
            throw new NullPointerException("key, keyManagerFactory");
        }
        try {
            if (trustCertCollection != null) {
                trustManagerFactory = SslContext.buildTrustManagerFactory(trustCertCollection, trustManagerFactory, keyStore);
            }
            if (key != null) {
                keyManagerFactory = SslContext.buildKeyManagerFactory(keyCertChain, null, key, keyPassword, keyManagerFactory, null);
            }
            final SSLContext ctx = (sslContextProvider == null) ? SSLContext.getInstance("TLS") : SSLContext.getInstance("TLS", sslContextProvider);
            ctx.init(keyManagerFactory.getKeyManagers(), (TrustManager[])((trustManagerFactory == null) ? null : trustManagerFactory.getTrustManagers()), null);
            final SSLSessionContext sessCtx = ctx.getServerSessionContext();
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
            throw new SSLException("failed to initialize the server-side SSL context", e);
        }
    }
}
