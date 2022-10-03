package io.netty.handler.ssl;

import io.netty.buffer.ByteBufAllocator;
import javax.net.ssl.SSLEngine;
import java.util.List;

@Deprecated
public final class JdkAlpnApplicationProtocolNegotiator extends JdkBaseApplicationProtocolNegotiator
{
    private static final boolean AVAILABLE;
    private static final JdkApplicationProtocolNegotiator.SslEngineWrapperFactory ALPN_WRAPPER;
    
    public JdkAlpnApplicationProtocolNegotiator(final Iterable<String> protocols) {
        this(false, protocols);
    }
    
    public JdkAlpnApplicationProtocolNegotiator(final String... protocols) {
        this(false, protocols);
    }
    
    public JdkAlpnApplicationProtocolNegotiator(final boolean failIfNoCommonProtocols, final Iterable<String> protocols) {
        this(failIfNoCommonProtocols, failIfNoCommonProtocols, protocols);
    }
    
    public JdkAlpnApplicationProtocolNegotiator(final boolean failIfNoCommonProtocols, final String... protocols) {
        this(failIfNoCommonProtocols, failIfNoCommonProtocols, protocols);
    }
    
    public JdkAlpnApplicationProtocolNegotiator(final boolean clientFailIfNoCommonProtocols, final boolean serverFailIfNoCommonProtocols, final Iterable<String> protocols) {
        this(serverFailIfNoCommonProtocols ? JdkAlpnApplicationProtocolNegotiator.FAIL_SELECTOR_FACTORY : JdkAlpnApplicationProtocolNegotiator.NO_FAIL_SELECTOR_FACTORY, clientFailIfNoCommonProtocols ? JdkAlpnApplicationProtocolNegotiator.FAIL_SELECTION_LISTENER_FACTORY : JdkAlpnApplicationProtocolNegotiator.NO_FAIL_SELECTION_LISTENER_FACTORY, protocols);
    }
    
    public JdkAlpnApplicationProtocolNegotiator(final boolean clientFailIfNoCommonProtocols, final boolean serverFailIfNoCommonProtocols, final String... protocols) {
        this(serverFailIfNoCommonProtocols ? JdkAlpnApplicationProtocolNegotiator.FAIL_SELECTOR_FACTORY : JdkAlpnApplicationProtocolNegotiator.NO_FAIL_SELECTOR_FACTORY, clientFailIfNoCommonProtocols ? JdkAlpnApplicationProtocolNegotiator.FAIL_SELECTION_LISTENER_FACTORY : JdkAlpnApplicationProtocolNegotiator.NO_FAIL_SELECTION_LISTENER_FACTORY, protocols);
    }
    
    public JdkAlpnApplicationProtocolNegotiator(final JdkApplicationProtocolNegotiator.ProtocolSelectorFactory selectorFactory, final JdkApplicationProtocolNegotiator.ProtocolSelectionListenerFactory listenerFactory, final Iterable<String> protocols) {
        super(JdkAlpnApplicationProtocolNegotiator.ALPN_WRAPPER, selectorFactory, listenerFactory, protocols);
    }
    
    public JdkAlpnApplicationProtocolNegotiator(final JdkApplicationProtocolNegotiator.ProtocolSelectorFactory selectorFactory, final JdkApplicationProtocolNegotiator.ProtocolSelectionListenerFactory listenerFactory, final String... protocols) {
        super(JdkAlpnApplicationProtocolNegotiator.ALPN_WRAPPER, selectorFactory, listenerFactory, protocols);
    }
    
    static boolean isAlpnSupported() {
        return JdkAlpnApplicationProtocolNegotiator.AVAILABLE;
    }
    
    static {
        AVAILABLE = (Conscrypt.isAvailable() || JdkAlpnSslUtils.supportsAlpn() || JettyAlpnSslEngine.isAvailable() || BouncyCastle.isAvailable());
        ALPN_WRAPPER = (JdkAlpnApplicationProtocolNegotiator.AVAILABLE ? new AlpnWrapper() : new FailureWrapper());
    }
    
    private static final class FailureWrapper extends JdkApplicationProtocolNegotiator.AllocatorAwareSslEngineWrapperFactory
    {
        public SSLEngine wrapSslEngine(final SSLEngine engine, final ByteBufAllocator alloc, final JdkApplicationProtocolNegotiator applicationNegotiator, final boolean isServer) {
            throw new RuntimeException("ALPN unsupported. Is your classpath configured correctly? For Conscrypt, add the appropriate Conscrypt JAR to classpath and set the security provider. For Jetty-ALPN, see https://www.eclipse.org/jetty/documentation/current/alpn-chapter.html#alpn-starting");
        }
    }
    
    private static final class AlpnWrapper extends JdkApplicationProtocolNegotiator.AllocatorAwareSslEngineWrapperFactory
    {
        public SSLEngine wrapSslEngine(final SSLEngine engine, final ByteBufAllocator alloc, final JdkApplicationProtocolNegotiator applicationNegotiator, final boolean isServer) {
            if (Conscrypt.isEngineSupported(engine)) {
                return isServer ? ConscryptAlpnSslEngine.newServerEngine(engine, alloc, applicationNegotiator) : ConscryptAlpnSslEngine.newClientEngine(engine, alloc, applicationNegotiator);
            }
            if (BouncyCastle.isInUse(engine)) {
                return new BouncyCastleAlpnSslEngine(engine, applicationNegotiator, isServer);
            }
            if (JdkAlpnSslUtils.supportsAlpn()) {
                return new JdkAlpnSslEngine(engine, applicationNegotiator, isServer);
            }
            if (JettyAlpnSslEngine.isAvailable()) {
                return isServer ? JettyAlpnSslEngine.newServerEngine(engine, applicationNegotiator) : JettyAlpnSslEngine.newClientEngine(engine, applicationNegotiator);
            }
            throw new UnsupportedOperationException("ALPN not supported. Unable to wrap SSLEngine of type '" + engine.getClass().getName() + "')");
        }
    }
}
