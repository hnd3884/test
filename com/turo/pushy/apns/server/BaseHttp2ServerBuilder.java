package com.turo.pushy.apns.server;

import org.slf4j.LoggerFactory;
import javax.net.ssl.SSLException;
import io.netty.handler.ssl.SslContext;
import io.netty.util.ReferenceCounted;
import io.netty.handler.ssl.ApplicationProtocolConfig;
import io.netty.handler.ssl.ClientAuth;
import io.netty.handler.ssl.CipherSuiteFilter;
import io.netty.handler.ssl.SupportedCipherSuiteFilter;
import io.netty.handler.codec.http2.Http2SecurityUtil;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslProvider;
import io.netty.handler.ssl.OpenSsl;
import org.slf4j.Logger;
import io.netty.channel.EventLoopGroup;
import java.io.InputStream;
import java.io.File;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

abstract class BaseHttp2ServerBuilder<T extends BaseHttp2Server>
{
    protected X509Certificate[] certificateChain;
    protected PrivateKey privateKey;
    protected File certificateChainPemFile;
    protected File privateKeyPkcs8File;
    protected InputStream certificateChainInputStream;
    protected InputStream privateKeyPkcs8InputStream;
    protected String privateKeyPassword;
    protected File trustedClientCertificatePemFile;
    protected InputStream trustedClientCertificateInputStream;
    protected X509Certificate[] trustedClientCertificates;
    protected EventLoopGroup eventLoopGroup;
    protected int maxConcurrentStreams;
    protected boolean useAlpn;
    public static final int DEFAULT_MAX_CONCURRENT_STREAMS = 1500;
    private static final Logger log;
    
    BaseHttp2ServerBuilder() {
        this.maxConcurrentStreams = 1500;
    }
    
    public BaseHttp2ServerBuilder setServerCredentials(final File certificatePemFile, final File privateKeyPkcs8File, final String privateKeyPassword) {
        this.certificateChain = null;
        this.privateKey = null;
        this.certificateChainPemFile = certificatePemFile;
        this.privateKeyPkcs8File = privateKeyPkcs8File;
        this.certificateChainInputStream = null;
        this.privateKeyPkcs8InputStream = null;
        this.privateKeyPassword = privateKeyPassword;
        return this;
    }
    
    public BaseHttp2ServerBuilder setServerCredentials(final InputStream certificatePemInputStream, final InputStream privateKeyPkcs8InputStream, final String privateKeyPassword) {
        this.certificateChain = null;
        this.privateKey = null;
        this.certificateChainPemFile = null;
        this.privateKeyPkcs8File = null;
        this.certificateChainInputStream = certificatePemInputStream;
        this.privateKeyPkcs8InputStream = privateKeyPkcs8InputStream;
        this.privateKeyPassword = privateKeyPassword;
        return this;
    }
    
    public BaseHttp2ServerBuilder setServerCredentials(final X509Certificate[] certificates, final PrivateKey privateKey, final String privateKeyPassword) {
        this.certificateChain = certificates;
        this.privateKey = privateKey;
        this.certificateChainPemFile = null;
        this.privateKeyPkcs8File = null;
        this.certificateChainInputStream = null;
        this.privateKeyPkcs8InputStream = null;
        this.privateKeyPassword = privateKeyPassword;
        return this;
    }
    
    public BaseHttp2ServerBuilder setTrustedClientCertificateChain(final File certificatePemFile) {
        this.trustedClientCertificatePemFile = certificatePemFile;
        this.trustedClientCertificateInputStream = null;
        this.trustedClientCertificates = null;
        return this;
    }
    
    public BaseHttp2ServerBuilder setTrustedClientCertificateChain(final InputStream certificateInputStream) {
        this.trustedClientCertificatePemFile = null;
        this.trustedClientCertificateInputStream = certificateInputStream;
        this.trustedClientCertificates = null;
        return this;
    }
    
    public BaseHttp2ServerBuilder setTrustedServerCertificateChain(final X509Certificate... certificates) {
        this.trustedClientCertificatePemFile = null;
        this.trustedClientCertificateInputStream = null;
        this.trustedClientCertificates = certificates;
        return this;
    }
    
    public BaseHttp2ServerBuilder setEventLoopGroup(final EventLoopGroup eventLoopGroup) {
        this.eventLoopGroup = eventLoopGroup;
        return this;
    }
    
    public BaseHttp2ServerBuilder setMaxConcurrentStreams(final int maxConcurrentStreams) {
        if (maxConcurrentStreams <= 0) {
            throw new IllegalArgumentException("Maximum number of concurrent streams must be positive.");
        }
        this.maxConcurrentStreams = maxConcurrentStreams;
        return this;
    }
    
    public BaseHttp2ServerBuilder setUseAlpn(final boolean useAlpn) {
        this.useAlpn = useAlpn;
        return this;
    }
    
    public T build() throws SSLException {
        SslProvider sslProvider;
        if (OpenSsl.isAvailable()) {
            BaseHttp2ServerBuilder.log.info("Native SSL provider is available; will use native provider.");
            sslProvider = SslProvider.OPENSSL;
        }
        else {
            BaseHttp2ServerBuilder.log.info("Native SSL provider not available; will use JDK SSL provider.");
            sslProvider = SslProvider.JDK;
        }
        SslContextBuilder sslContextBuilder;
        if (this.certificateChain != null && this.privateKey != null) {
            sslContextBuilder = SslContextBuilder.forServer(this.privateKey, this.privateKeyPassword, this.certificateChain);
        }
        else if (this.certificateChainPemFile != null && this.privateKeyPkcs8File != null) {
            sslContextBuilder = SslContextBuilder.forServer(this.certificateChainPemFile, this.privateKeyPkcs8File, this.privateKeyPassword);
        }
        else {
            if (this.certificateChainInputStream == null || this.privateKeyPkcs8InputStream == null) {
                throw new IllegalStateException("Must specify server credentials before building a mock server.");
            }
            sslContextBuilder = SslContextBuilder.forServer(this.certificateChainInputStream, this.privateKeyPkcs8InputStream, this.privateKeyPassword);
        }
        sslContextBuilder.sslProvider(sslProvider).ciphers((Iterable)Http2SecurityUtil.CIPHERS, (CipherSuiteFilter)SupportedCipherSuiteFilter.INSTANCE).clientAuth(ClientAuth.OPTIONAL);
        if (this.trustedClientCertificatePemFile != null) {
            sslContextBuilder.trustManager(this.trustedClientCertificatePemFile);
        }
        else if (this.trustedClientCertificateInputStream != null) {
            sslContextBuilder.trustManager(this.trustedClientCertificateInputStream);
        }
        else if (this.trustedClientCertificates != null) {
            sslContextBuilder.trustManager(this.trustedClientCertificates);
        }
        if (this.useAlpn) {
            sslContextBuilder.applicationProtocolConfig(new ApplicationProtocolConfig(ApplicationProtocolConfig.Protocol.ALPN, ApplicationProtocolConfig.SelectorFailureBehavior.NO_ADVERTISE, ApplicationProtocolConfig.SelectedListenerFailureBehavior.ACCEPT, new String[] { "h2" }));
        }
        final SslContext sslContext = sslContextBuilder.build();
        final T server = this.constructServer(sslContext);
        if (sslContext instanceof ReferenceCounted) {
            ((ReferenceCounted)sslContext).release();
        }
        return server;
    }
    
    protected abstract T constructServer(final SslContext p0);
    
    static {
        log = LoggerFactory.getLogger((Class)BaseHttp2ServerBuilder.class);
    }
}
