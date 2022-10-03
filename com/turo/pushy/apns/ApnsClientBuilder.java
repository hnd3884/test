package com.turo.pushy.apns;

import org.slf4j.LoggerFactory;
import io.netty.handler.ssl.SslContext;
import io.netty.util.ReferenceCounted;
import io.netty.handler.ssl.CipherSuiteFilter;
import io.netty.handler.ssl.SupportedCipherSuiteFilter;
import io.netty.handler.codec.http2.Http2SecurityUtil;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslProvider;
import io.netty.handler.ssl.OpenSsl;
import java.util.concurrent.TimeUnit;
import java.security.cert.Certificate;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.io.IOException;
import javax.net.ssl.SSLException;
import java.io.FileInputStream;
import org.slf4j.Logger;
import io.netty.handler.codec.http2.Http2FrameLogger;
import com.turo.pushy.apns.proxy.ProxyHandlerFactory;
import io.netty.channel.EventLoopGroup;
import java.io.InputStream;
import java.io.File;
import com.turo.pushy.apns.auth.ApnsSigningKey;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.net.InetSocketAddress;

public class ApnsClientBuilder
{
    private InetSocketAddress apnsServerAddress;
    private X509Certificate clientCertificate;
    private PrivateKey privateKey;
    private String privateKeyPassword;
    private ApnsSigningKey signingKey;
    private File trustedServerCertificatePemFile;
    private InputStream trustedServerCertificateInputStream;
    private X509Certificate[] trustedServerCertificates;
    private EventLoopGroup eventLoopGroup;
    private int concurrentConnections;
    private ApnsClientMetricsListener metricsListener;
    private ProxyHandlerFactory proxyHandlerFactory;
    private int connectionTimeoutMillis;
    private long idlePingIntervalMillis;
    private long gracefulShutdownTimeoutMillis;
    private Http2FrameLogger frameLogger;
    public static final int DEFAULT_PING_IDLE_TIME_MILLIS = 60000;
    public static final String PRODUCTION_APNS_HOST = "api.push.apple.com";
    public static final String DEVELOPMENT_APNS_HOST = "api.development.push.apple.com";
    public static final int DEFAULT_APNS_PORT = 443;
    public static final int ALTERNATE_APNS_PORT = 2197;
    private static final Logger log;
    
    public ApnsClientBuilder() {
        this.concurrentConnections = 1;
        this.idlePingIntervalMillis = 60000L;
    }
    
    public ApnsClientBuilder setApnsServer(final String hostname) {
        return this.setApnsServer(hostname, 443);
    }
    
    public ApnsClientBuilder setApnsServer(final String hostname, final int port) {
        this.apnsServerAddress = InetSocketAddress.createUnresolved(hostname, port);
        return this;
    }
    
    public ApnsClientBuilder setClientCredentials(final File p12File, final String p12Password) throws SSLException, IOException {
        try (final InputStream p12InputStream = new FileInputStream(p12File)) {
            return this.setClientCredentials(p12InputStream, p12Password);
        }
    }
    
    public ApnsClientBuilder setClientCredentials(final InputStream p12InputStream, final String p12Password) throws SSLException, IOException {
        X509Certificate x509Certificate;
        PrivateKey privateKey;
        try {
            final KeyStore.PrivateKeyEntry privateKeyEntry = P12Util.getFirstPrivateKeyEntryFromP12InputStream(p12InputStream, p12Password);
            final Certificate certificate = privateKeyEntry.getCertificate();
            if (!(certificate instanceof X509Certificate)) {
                throw new KeyStoreException("Found a certificate in the provided PKCS#12 file, but it was not an X.509 certificate.");
            }
            x509Certificate = (X509Certificate)certificate;
            privateKey = privateKeyEntry.getPrivateKey();
        }
        catch (final KeyStoreException e) {
            throw new SSLException(e);
        }
        return this.setClientCredentials(x509Certificate, privateKey, p12Password);
    }
    
    public ApnsClientBuilder setClientCredentials(final X509Certificate clientCertificate, final PrivateKey privateKey, final String privateKeyPassword) {
        this.clientCertificate = clientCertificate;
        this.privateKey = privateKey;
        this.privateKeyPassword = privateKeyPassword;
        return this;
    }
    
    public ApnsClientBuilder setSigningKey(final ApnsSigningKey signingKey) {
        this.signingKey = signingKey;
        return this;
    }
    
    public ApnsClientBuilder setTrustedServerCertificateChain(final File certificatePemFile) {
        this.trustedServerCertificatePemFile = certificatePemFile;
        this.trustedServerCertificateInputStream = null;
        this.trustedServerCertificates = null;
        return this;
    }
    
    public ApnsClientBuilder setTrustedServerCertificateChain(final InputStream certificateInputStream) {
        this.trustedServerCertificatePemFile = null;
        this.trustedServerCertificateInputStream = certificateInputStream;
        this.trustedServerCertificates = null;
        return this;
    }
    
    public ApnsClientBuilder setTrustedServerCertificateChain(final X509Certificate... certificates) {
        this.trustedServerCertificatePemFile = null;
        this.trustedServerCertificateInputStream = null;
        this.trustedServerCertificates = certificates;
        return this;
    }
    
    public ApnsClientBuilder setEventLoopGroup(final EventLoopGroup eventLoopGroup) {
        this.eventLoopGroup = eventLoopGroup;
        return this;
    }
    
    public ApnsClientBuilder setConcurrentConnections(final int concurrentConnections) {
        this.concurrentConnections = concurrentConnections;
        return this;
    }
    
    public ApnsClientBuilder setMetricsListener(final ApnsClientMetricsListener metricsListener) {
        this.metricsListener = metricsListener;
        return this;
    }
    
    public ApnsClientBuilder setProxyHandlerFactory(final ProxyHandlerFactory proxyHandlerFactory) {
        this.proxyHandlerFactory = proxyHandlerFactory;
        return this;
    }
    
    public ApnsClientBuilder setConnectionTimeout(final long connectionTimeout, final TimeUnit timeoutUnit) {
        this.connectionTimeoutMillis = (int)timeoutUnit.toMillis(connectionTimeout);
        return this;
    }
    
    public ApnsClientBuilder setIdlePingInterval(final long pingInterval, final TimeUnit pingIntervalUnit) {
        this.idlePingIntervalMillis = pingIntervalUnit.toMillis(pingInterval);
        return this;
    }
    
    public ApnsClientBuilder setGracefulShutdownTimeout(final long gracefulShutdownTimeout, final TimeUnit timeoutUnit) {
        this.gracefulShutdownTimeoutMillis = timeoutUnit.toMillis(gracefulShutdownTimeout);
        return this;
    }
    
    public ApnsClientBuilder setFrameLogger(final Http2FrameLogger frameLogger) {
        this.frameLogger = frameLogger;
        return this;
    }
    
    public ApnsClient build() throws SSLException {
        if (this.apnsServerAddress == null) {
            throw new IllegalStateException("No APNs server address specified.");
        }
        if (this.clientCertificate == null && this.privateKey == null && this.signingKey == null) {
            throw new IllegalStateException("No client credentials specified; either TLS credentials (a certificate/private key) or an APNs signing key must be provided before building a client.");
        }
        if ((this.clientCertificate != null || this.privateKey != null) && this.signingKey != null) {
            throw new IllegalStateException("Clients may not have both a signing key and TLS credentials.");
        }
        SslProvider sslProvider;
        if (OpenSsl.isAvailable()) {
            ApnsClientBuilder.log.info("Native SSL provider is available; will use native provider.");
            sslProvider = SslProvider.OPENSSL_REFCNT;
        }
        else {
            ApnsClientBuilder.log.info("Native SSL provider not available; will use JDK SSL provider.");
            sslProvider = SslProvider.JDK;
        }
        final SslContextBuilder sslContextBuilder = SslContextBuilder.forClient().sslProvider(sslProvider).ciphers((Iterable)Http2SecurityUtil.CIPHERS, (CipherSuiteFilter)SupportedCipherSuiteFilter.INSTANCE);
        if (this.clientCertificate != null && this.privateKey != null) {
            sslContextBuilder.keyManager(this.privateKey, this.privateKeyPassword, new X509Certificate[] { this.clientCertificate });
        }
        if (this.trustedServerCertificatePemFile != null) {
            sslContextBuilder.trustManager(this.trustedServerCertificatePemFile);
        }
        else if (this.trustedServerCertificateInputStream != null) {
            sslContextBuilder.trustManager(this.trustedServerCertificateInputStream);
        }
        else if (this.trustedServerCertificates != null) {
            sslContextBuilder.trustManager(this.trustedServerCertificates);
        }
        final SslContext sslContext = sslContextBuilder.build();
        final ApnsClient client = new ApnsClient(this.apnsServerAddress, sslContext, this.signingKey, this.proxyHandlerFactory, this.connectionTimeoutMillis, this.idlePingIntervalMillis, this.gracefulShutdownTimeoutMillis, this.concurrentConnections, this.metricsListener, this.frameLogger, this.eventLoopGroup);
        if (sslContext instanceof ReferenceCounted) {
            ((ReferenceCounted)sslContext).release();
        }
        return client;
    }
    
    static {
        log = LoggerFactory.getLogger((Class)ApnsClientBuilder.class);
    }
}
