package io.netty.channel.kqueue;

import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelConfig;
import io.netty.channel.AbstractChannel;
import java.net.SocketAddress;
import io.netty.channel.ChannelOutboundBuffer;
import java.net.InetSocketAddress;
import io.netty.channel.EventLoop;
import io.netty.channel.Channel;
import io.netty.channel.ChannelMetadata;
import io.netty.channel.ServerChannel;

public abstract class AbstractKQueueServerChannel extends AbstractKQueueChannel implements ServerChannel
{
    private static final ChannelMetadata METADATA;
    
    AbstractKQueueServerChannel(final BsdSocket fd) {
        this(fd, AbstractKQueueChannel.isSoErrorZero(fd));
    }
    
    AbstractKQueueServerChannel(final BsdSocket fd, final boolean active) {
        super(null, fd, active);
    }
    
    @Override
    public ChannelMetadata metadata() {
        return AbstractKQueueServerChannel.METADATA;
    }
    
    @Override
    protected boolean isCompatible(final EventLoop loop) {
        return loop instanceof KQueueEventLoop;
    }
    
    @Override
    protected InetSocketAddress remoteAddress0() {
        return null;
    }
    
    @Override
    protected AbstractKQueueUnsafe newUnsafe() {
        return new KQueueServerSocketUnsafe();
    }
    
    @Override
    protected void doWrite(final ChannelOutboundBuffer in) throws Exception {
        throw new UnsupportedOperationException();
    }
    
    @Override
    protected Object filterOutboundMessage(final Object msg) throws Exception {
        throw new UnsupportedOperationException();
    }
    
    abstract Channel newChildChannel(final int p0, final byte[] p1, final int p2, final int p3) throws Exception;
    
    @Override
    protected boolean doConnect(final SocketAddress remoteAddress, final SocketAddress localAddress) throws Exception {
        throw new UnsupportedOperationException();
    }
    
    static {
        METADATA = new ChannelMetadata(false, 16);
    }
    
    final class KQueueServerSocketUnsafe extends AbstractKQueueUnsafe
    {
        private final byte[] acceptedAddress;
        
        KQueueServerSocketUnsafe() {
            this.acceptedAddress = new byte[26];
        }
        
        @Override
        void readReady(final KQueueRecvByteAllocatorHandle allocHandle) {
            assert AbstractKQueueServerChannel.this.eventLoop().inEventLoop();
            final ChannelConfig config = AbstractKQueueServerChannel.this.config();
            if (AbstractKQueueServerChannel.this.shouldBreakReadReady(config)) {
                this.clearReadFilter0();
                return;
            }
            final ChannelPipeline pipeline = AbstractKQueueServerChannel.this.pipeline();
            allocHandle.reset(config);
            allocHandle.attemptedBytesRead(1);
            this.readReadyBefore();
            Throwable exception = null;
            try {
                try {
                    do {
                        final int acceptFd = AbstractKQueueServerChannel.this.socket.accept(this.acceptedAddress);
                        if (acceptFd == -1) {
                            allocHandle.lastBytesRead(-1);
                            break;
                        }
                        allocHandle.lastBytesRead(1);
                        allocHandle.incMessagesRead(1);
                        this.readPending = false;
                        pipeline.fireChannelRead((Object)AbstractKQueueServerChannel.this.newChildChannel(acceptFd, this.acceptedAddress, 1, this.acceptedAddress[0]));
                    } while (allocHandle.continueReading());
                }
                catch (final Throwable t) {
                    exception = t;
                }
                allocHandle.readComplete();
                pipeline.fireChannelReadComplete();
                if (exception != null) {
                    pipeline.fireExceptionCaught(exception);
                }
            }
            finally {
                this.readReadyFinally(config);
            }
        }
    }
}
