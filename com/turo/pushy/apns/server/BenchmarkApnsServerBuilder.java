package com.turo.pushy.apns.server;

import io.netty.handler.ssl.SslContext;
import javax.net.ssl.SSLException;
import io.netty.channel.EventLoopGroup;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.io.InputStream;
import java.io.File;

public class BenchmarkApnsServerBuilder extends BaseHttp2ServerBuilder<BenchmarkApnsServer>
{
    @Override
    public BenchmarkApnsServerBuilder setServerCredentials(final File certificatePemFile, final File privateKeyPkcs8File, final String privateKeyPassword) {
        super.setServerCredentials(certificatePemFile, privateKeyPkcs8File, privateKeyPassword);
        return this;
    }
    
    @Override
    public BenchmarkApnsServerBuilder setServerCredentials(final InputStream certificatePemInputStream, final InputStream privateKeyPkcs8InputStream, final String privateKeyPassword) {
        super.setServerCredentials(certificatePemInputStream, privateKeyPkcs8InputStream, privateKeyPassword);
        return this;
    }
    
    @Override
    public BenchmarkApnsServerBuilder setServerCredentials(final X509Certificate[] certificates, final PrivateKey privateKey, final String privateKeyPassword) {
        super.setServerCredentials(certificates, privateKey, privateKeyPassword);
        return this;
    }
    
    @Override
    public BenchmarkApnsServerBuilder setTrustedClientCertificateChain(final File certificatePemFile) {
        super.setTrustedClientCertificateChain(certificatePemFile);
        return this;
    }
    
    @Override
    public BenchmarkApnsServerBuilder setTrustedClientCertificateChain(final InputStream certificateInputStream) {
        super.setTrustedClientCertificateChain(certificateInputStream);
        return this;
    }
    
    @Override
    public BenchmarkApnsServerBuilder setTrustedServerCertificateChain(final X509Certificate... certificates) {
        super.setTrustedServerCertificateChain(certificates);
        return this;
    }
    
    @Override
    public BenchmarkApnsServerBuilder setEventLoopGroup(final EventLoopGroup eventLoopGroup) {
        super.setEventLoopGroup(eventLoopGroup);
        return this;
    }
    
    @Override
    public BenchmarkApnsServerBuilder setMaxConcurrentStreams(final int maxConcurrentStreams) {
        super.setMaxConcurrentStreams(maxConcurrentStreams);
        return this;
    }
    
    @Override
    public BenchmarkApnsServerBuilder setUseAlpn(final boolean useAlpn) {
        super.setUseAlpn(useAlpn);
        return this;
    }
    
    @Override
    public BenchmarkApnsServer build() throws SSLException {
        return super.build();
    }
    
    @Override
    protected BenchmarkApnsServer constructServer(final SslContext sslContext) {
        return new BenchmarkApnsServer(sslContext, this.eventLoopGroup, this.maxConcurrentStreams);
    }
}
