package io.netty.handler.ssl;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.BiConsumer;
import javax.net.ssl.SSLEngine;
import io.netty.util.internal.SuppressJava6Requirement;

@SuppressJava6Requirement(reason = "Usage guarded by java version check")
final class BouncyCastleAlpnSslEngine extends JdkAlpnSslEngine
{
    BouncyCastleAlpnSslEngine(final SSLEngine engine, final JdkApplicationProtocolNegotiator applicationNegotiator, final boolean isServer) {
        super(engine, applicationNegotiator, isServer, new BiConsumer<SSLEngine, AlpnSelector>() {
            @Override
            public void accept(final SSLEngine e, final AlpnSelector s) {
                BouncyCastleAlpnSslUtils.setHandshakeApplicationProtocolSelector(e, s);
            }
        }, new BiConsumer<SSLEngine, List<String>>() {
            @Override
            public void accept(final SSLEngine e, final List<String> p) {
                BouncyCastleAlpnSslUtils.setApplicationProtocols(e, p);
            }
        });
    }
    
    @Override
    public String getApplicationProtocol() {
        return BouncyCastleAlpnSslUtils.getApplicationProtocol(this.getWrappedEngine());
    }
    
    @Override
    public String getHandshakeApplicationProtocol() {
        return BouncyCastleAlpnSslUtils.getHandshakeApplicationProtocol(this.getWrappedEngine());
    }
    
    @Override
    public void setHandshakeApplicationProtocolSelector(final BiFunction<SSLEngine, List<String>, String> selector) {
        BouncyCastleAlpnSslUtils.setHandshakeApplicationProtocolSelector(this.getWrappedEngine(), selector);
    }
    
    @Override
    public BiFunction<SSLEngine, List<String>, String> getHandshakeApplicationProtocolSelector() {
        return BouncyCastleAlpnSslUtils.getHandshakeApplicationProtocolSelector(this.getWrappedEngine());
    }
}
