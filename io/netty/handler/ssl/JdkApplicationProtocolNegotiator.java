package io.netty.handler.ssl;

import java.util.Set;
import java.util.List;
import io.netty.buffer.ByteBufAllocator;
import javax.net.ssl.SSLEngine;

@Deprecated
public interface JdkApplicationProtocolNegotiator extends ApplicationProtocolNegotiator
{
    SslEngineWrapperFactory wrapperFactory();
    
    ProtocolSelectorFactory protocolSelectorFactory();
    
    ProtocolSelectionListenerFactory protocolListenerFactory();
    
    public abstract static class AllocatorAwareSslEngineWrapperFactory implements SslEngineWrapperFactory
    {
        @Override
        public final SSLEngine wrapSslEngine(final SSLEngine engine, final JdkApplicationProtocolNegotiator applicationNegotiator, final boolean isServer) {
            return this.wrapSslEngine(engine, ByteBufAllocator.DEFAULT, applicationNegotiator, isServer);
        }
        
        abstract SSLEngine wrapSslEngine(final SSLEngine p0, final ByteBufAllocator p1, final JdkApplicationProtocolNegotiator p2, final boolean p3);
    }
    
    public interface SslEngineWrapperFactory
    {
        SSLEngine wrapSslEngine(final SSLEngine p0, final JdkApplicationProtocolNegotiator p1, final boolean p2);
    }
    
    public interface ProtocolSelectionListener
    {
        void unsupported();
        
        void selected(final String p0) throws Exception;
    }
    
    public interface ProtocolSelectionListenerFactory
    {
        ProtocolSelectionListener newListener(final SSLEngine p0, final List<String> p1);
    }
    
    public interface ProtocolSelector
    {
        void unsupported();
        
        String select(final List<String> p0) throws Exception;
    }
    
    public interface ProtocolSelectorFactory
    {
        ProtocolSelector newSelector(final SSLEngine p0, final Set<String> p1);
    }
}
