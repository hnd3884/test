package io.netty.handler.ssl;

import io.netty.buffer.ByteBuf;
import org.conscrypt.AllocatedBuffer;
import java.util.Collections;
import java.util.Set;
import java.util.Collection;
import java.util.LinkedHashSet;
import io.netty.util.internal.ObjectUtil;
import org.conscrypt.HandshakeListener;
import io.netty.util.internal.SystemPropertyUtil;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLEngineResult;
import java.nio.ByteBuffer;
import org.conscrypt.BufferAllocator;
import org.conscrypt.Conscrypt;
import java.util.List;
import io.netty.buffer.ByteBufAllocator;
import javax.net.ssl.SSLEngine;

abstract class ConscryptAlpnSslEngine extends JdkSslEngine
{
    private static final boolean USE_BUFFER_ALLOCATOR;
    
    static ConscryptAlpnSslEngine newClientEngine(final SSLEngine engine, final ByteBufAllocator alloc, final JdkApplicationProtocolNegotiator applicationNegotiator) {
        return new ClientEngine(engine, alloc, applicationNegotiator);
    }
    
    static ConscryptAlpnSslEngine newServerEngine(final SSLEngine engine, final ByteBufAllocator alloc, final JdkApplicationProtocolNegotiator applicationNegotiator) {
        return new ServerEngine(engine, alloc, applicationNegotiator);
    }
    
    private ConscryptAlpnSslEngine(final SSLEngine engine, final ByteBufAllocator alloc, final List<String> protocols) {
        super(engine);
        if (ConscryptAlpnSslEngine.USE_BUFFER_ALLOCATOR) {
            Conscrypt.setBufferAllocator(engine, (BufferAllocator)new BufferAllocatorAdapter(alloc));
        }
        Conscrypt.setApplicationProtocols(engine, (String[])protocols.toArray(new String[0]));
    }
    
    final int calculateOutNetBufSize(final int plaintextBytes, final int numBuffers) {
        final long maxOverhead = Conscrypt.maxSealOverhead(this.getWrappedEngine()) * (long)numBuffers;
        return (int)Math.min(2147483647L, plaintextBytes + maxOverhead);
    }
    
    final SSLEngineResult unwrap(final ByteBuffer[] srcs, final ByteBuffer[] dests) throws SSLException {
        return Conscrypt.unwrap(this.getWrappedEngine(), srcs, dests);
    }
    
    static {
        USE_BUFFER_ALLOCATOR = SystemPropertyUtil.getBoolean("io.netty.handler.ssl.conscrypt.useBufferAllocator", true);
    }
    
    private static final class ClientEngine extends ConscryptAlpnSslEngine
    {
        private final JdkApplicationProtocolNegotiator.ProtocolSelectionListener protocolListener;
        
        ClientEngine(final SSLEngine engine, final ByteBufAllocator alloc, final JdkApplicationProtocolNegotiator applicationNegotiator) {
            super(engine, alloc, applicationNegotiator.protocols(), null);
            Conscrypt.setHandshakeListener(engine, (HandshakeListener)new HandshakeListener() {
                public void onHandshakeFinished() throws SSLException {
                    ClientEngine.this.selectProtocol();
                }
            });
            this.protocolListener = ObjectUtil.checkNotNull(applicationNegotiator.protocolListenerFactory().newListener(this, applicationNegotiator.protocols()), "protocolListener");
        }
        
        private void selectProtocol() throws SSLException {
            final String protocol = Conscrypt.getApplicationProtocol(this.getWrappedEngine());
            try {
                this.protocolListener.selected(protocol);
            }
            catch (final Throwable e) {
                throw SslUtils.toSSLHandshakeException(e);
            }
        }
    }
    
    private static final class ServerEngine extends ConscryptAlpnSslEngine
    {
        private final JdkApplicationProtocolNegotiator.ProtocolSelector protocolSelector;
        
        ServerEngine(final SSLEngine engine, final ByteBufAllocator alloc, final JdkApplicationProtocolNegotiator applicationNegotiator) {
            super(engine, alloc, applicationNegotiator.protocols(), null);
            Conscrypt.setHandshakeListener(engine, (HandshakeListener)new HandshakeListener() {
                public void onHandshakeFinished() throws SSLException {
                    ServerEngine.this.selectProtocol();
                }
            });
            this.protocolSelector = ObjectUtil.checkNotNull(applicationNegotiator.protocolSelectorFactory().newSelector(this, new LinkedHashSet<String>(applicationNegotiator.protocols())), "protocolSelector");
        }
        
        private void selectProtocol() throws SSLException {
            try {
                final String protocol = Conscrypt.getApplicationProtocol(this.getWrappedEngine());
                this.protocolSelector.select((protocol != null) ? Collections.singletonList(protocol) : Collections.emptyList());
            }
            catch (final Throwable e) {
                throw SslUtils.toSSLHandshakeException(e);
            }
        }
    }
    
    private static final class BufferAllocatorAdapter extends BufferAllocator
    {
        private final ByteBufAllocator alloc;
        
        BufferAllocatorAdapter(final ByteBufAllocator alloc) {
            this.alloc = alloc;
        }
        
        public AllocatedBuffer allocateDirectBuffer(final int capacity) {
            return new BufferAdapter(this.alloc.directBuffer(capacity));
        }
    }
    
    private static final class BufferAdapter extends AllocatedBuffer
    {
        private final ByteBuf nettyBuffer;
        private final ByteBuffer buffer;
        
        BufferAdapter(final ByteBuf nettyBuffer) {
            this.nettyBuffer = nettyBuffer;
            this.buffer = nettyBuffer.nioBuffer(0, nettyBuffer.capacity());
        }
        
        public ByteBuffer nioBuffer() {
            return this.buffer;
        }
        
        public AllocatedBuffer retain() {
            this.nettyBuffer.retain();
            return this;
        }
        
        public AllocatedBuffer release() {
            this.nettyBuffer.release();
            return this;
        }
    }
}
