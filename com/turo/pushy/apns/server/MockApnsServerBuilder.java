package com.turo.pushy.apns.server;

import io.netty.handler.ssl.SslContext;
import javax.net.ssl.SSLException;
import io.netty.channel.EventLoopGroup;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.io.InputStream;
import java.io.File;

public class MockApnsServerBuilder extends BaseHttp2ServerBuilder<MockApnsServer>
{
    private PushNotificationHandlerFactory handlerFactory;
    private MockApnsServerListener listener;
    
    @Override
    public MockApnsServerBuilder setServerCredentials(final File certificatePemFile, final File privateKeyPkcs8File, final String privateKeyPassword) {
        super.setServerCredentials(certificatePemFile, privateKeyPkcs8File, privateKeyPassword);
        return this;
    }
    
    @Override
    public MockApnsServerBuilder setServerCredentials(final InputStream certificatePemInputStream, final InputStream privateKeyPkcs8InputStream, final String privateKeyPassword) {
        super.setServerCredentials(certificatePemInputStream, privateKeyPkcs8InputStream, privateKeyPassword);
        return this;
    }
    
    @Override
    public MockApnsServerBuilder setServerCredentials(final X509Certificate[] certificates, final PrivateKey privateKey, final String privateKeyPassword) {
        super.setServerCredentials(certificates, privateKey, privateKeyPassword);
        return this;
    }
    
    @Override
    public MockApnsServerBuilder setTrustedClientCertificateChain(final File certificatePemFile) {
        super.setTrustedClientCertificateChain(certificatePemFile);
        return this;
    }
    
    @Override
    public MockApnsServerBuilder setTrustedClientCertificateChain(final InputStream certificateInputStream) {
        super.setTrustedClientCertificateChain(certificateInputStream);
        return this;
    }
    
    @Override
    public MockApnsServerBuilder setTrustedServerCertificateChain(final X509Certificate... certificates) {
        super.setTrustedServerCertificateChain(certificates);
        return this;
    }
    
    @Override
    public MockApnsServerBuilder setEventLoopGroup(final EventLoopGroup eventLoopGroup) {
        super.setEventLoopGroup(eventLoopGroup);
        return this;
    }
    
    @Override
    public MockApnsServerBuilder setMaxConcurrentStreams(final int maxConcurrentStreams) {
        super.setMaxConcurrentStreams(maxConcurrentStreams);
        return this;
    }
    
    @Override
    public MockApnsServerBuilder setUseAlpn(final boolean useAlpn) {
        super.setUseAlpn(useAlpn);
        return this;
    }
    
    public MockApnsServerBuilder setHandlerFactory(final PushNotificationHandlerFactory handlerFactory) {
        this.handlerFactory = handlerFactory;
        return this;
    }
    
    public MockApnsServerBuilder setListener(final MockApnsServerListener listener) {
        this.listener = listener;
        return this;
    }
    
    @Override
    public MockApnsServer build() throws SSLException {
        return super.build();
    }
    
    @Override
    protected MockApnsServer constructServer(final SslContext sslContext) {
        if (this.handlerFactory == null) {
            throw new IllegalStateException("Must provide a push notification handler factory before building a mock server.");
        }
        return new MockApnsServer(sslContext, this.eventLoopGroup, this.handlerFactory, this.listener, this.maxConcurrentStreams);
    }
}
