package io.netty.handler.ssl;

import io.netty.util.ReferenceCounted;
import javax.net.ssl.SSLEngine;
import io.netty.buffer.ByteBufAllocator;
import javax.net.ssl.SSLException;
import java.util.Map;
import java.security.cert.Certificate;

public abstract class OpenSslContext extends ReferenceCountedOpenSslContext
{
    OpenSslContext(final Iterable<String> ciphers, final CipherSuiteFilter cipherFilter, final ApplicationProtocolConfig apnCfg, final int mode, final Certificate[] keyCertChain, final ClientAuth clientAuth, final String[] protocols, final boolean startTls, final boolean enableOcsp, final Map.Entry<SslContextOption<?>, Object>... options) throws SSLException {
        super(ciphers, cipherFilter, ReferenceCountedOpenSslContext.toNegotiator(apnCfg), mode, keyCertChain, clientAuth, protocols, startTls, enableOcsp, false, options);
    }
    
    OpenSslContext(final Iterable<String> ciphers, final CipherSuiteFilter cipherFilter, final OpenSslApplicationProtocolNegotiator apn, final int mode, final Certificate[] keyCertChain, final ClientAuth clientAuth, final String[] protocols, final boolean startTls, final boolean enableOcsp, final Map.Entry<SslContextOption<?>, Object>... options) throws SSLException {
        super(ciphers, cipherFilter, apn, mode, keyCertChain, clientAuth, protocols, startTls, enableOcsp, false, options);
    }
    
    @Override
    final SSLEngine newEngine0(final ByteBufAllocator alloc, final String peerHost, final int peerPort, final boolean jdkCompatibilityMode) {
        return new OpenSslEngine(this, alloc, peerHost, peerPort, jdkCompatibilityMode);
    }
    
    @Override
    protected final void finalize() throws Throwable {
        super.finalize();
        OpenSsl.releaseIfNeeded(this);
    }
}
