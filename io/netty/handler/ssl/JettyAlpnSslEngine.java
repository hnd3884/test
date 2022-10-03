package io.netty.handler.ssl;

import java.util.Set;
import java.util.Collection;
import java.util.LinkedHashSet;
import javax.net.ssl.SSLException;
import java.util.List;
import org.eclipse.jetty.alpn.ALPN;
import io.netty.util.internal.ObjectUtil;
import javax.net.ssl.SSLEngine;
import io.netty.util.internal.PlatformDependent;

abstract class JettyAlpnSslEngine extends JdkSslEngine
{
    private static final boolean available;
    
    static boolean isAvailable() {
        return JettyAlpnSslEngine.available;
    }
    
    private static boolean initAvailable() {
        if (PlatformDependent.javaVersion() <= 8) {
            try {
                Class.forName("sun.security.ssl.ALPNExtension", true, null);
                return true;
            }
            catch (final Throwable t) {}
        }
        return false;
    }
    
    static JettyAlpnSslEngine newClientEngine(final SSLEngine engine, final JdkApplicationProtocolNegotiator applicationNegotiator) {
        return new ClientEngine(engine, applicationNegotiator);
    }
    
    static JettyAlpnSslEngine newServerEngine(final SSLEngine engine, final JdkApplicationProtocolNegotiator applicationNegotiator) {
        return new ServerEngine(engine, applicationNegotiator);
    }
    
    private JettyAlpnSslEngine(final SSLEngine engine) {
        super(engine);
    }
    
    static {
        available = initAvailable();
    }
    
    private static final class ClientEngine extends JettyAlpnSslEngine
    {
        ClientEngine(final SSLEngine engine, final JdkApplicationProtocolNegotiator applicationNegotiator) {
            super(engine, null);
            ObjectUtil.checkNotNull(applicationNegotiator, "applicationNegotiator");
            final JdkApplicationProtocolNegotiator.ProtocolSelectionListener protocolListener = ObjectUtil.checkNotNull(applicationNegotiator.protocolListenerFactory().newListener(this, applicationNegotiator.protocols()), "protocolListener");
            ALPN.put(engine, (ALPN.Provider)new ALPN.ClientProvider() {
                public List<String> protocols() {
                    return applicationNegotiator.protocols();
                }
                
                public void selected(final String protocol) throws SSLException {
                    try {
                        protocolListener.selected(protocol);
                    }
                    catch (final Throwable t) {
                        throw SslUtils.toSSLHandshakeException(t);
                    }
                }
                
                public void unsupported() {
                    protocolListener.unsupported();
                }
            });
        }
        
        @Override
        public void closeInbound() throws SSLException {
            try {
                ALPN.remove(this.getWrappedEngine());
            }
            finally {
                super.closeInbound();
            }
        }
        
        @Override
        public void closeOutbound() {
            try {
                ALPN.remove(this.getWrappedEngine());
            }
            finally {
                super.closeOutbound();
            }
        }
    }
    
    private static final class ServerEngine extends JettyAlpnSslEngine
    {
        ServerEngine(final SSLEngine engine, final JdkApplicationProtocolNegotiator applicationNegotiator) {
            super(engine, null);
            ObjectUtil.checkNotNull(applicationNegotiator, "applicationNegotiator");
            final JdkApplicationProtocolNegotiator.ProtocolSelector protocolSelector = ObjectUtil.checkNotNull(applicationNegotiator.protocolSelectorFactory().newSelector(this, new LinkedHashSet<String>(applicationNegotiator.protocols())), "protocolSelector");
            ALPN.put(engine, (ALPN.Provider)new ALPN.ServerProvider() {
                public String select(final List<String> protocols) throws SSLException {
                    try {
                        return protocolSelector.select(protocols);
                    }
                    catch (final Throwable t) {
                        throw SslUtils.toSSLHandshakeException(t);
                    }
                }
                
                public void unsupported() {
                    protocolSelector.unsupported();
                }
            });
        }
        
        @Override
        public void closeInbound() throws SSLException {
            try {
                ALPN.remove(this.getWrappedEngine());
            }
            finally {
                super.closeInbound();
            }
        }
        
        @Override
        public void closeOutbound() {
            try {
                ALPN.remove(this.getWrappedEngine());
            }
            finally {
                super.closeOutbound();
            }
        }
    }
}
