package io.netty.channel.epoll;

import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelPromise;
import io.netty.channel.AbstractChannel;
import java.net.SocketAddress;
import io.netty.channel.ChannelOutboundBuffer;
import java.net.InetSocketAddress;
import io.netty.channel.EventLoop;
import io.netty.channel.Channel;
import io.netty.channel.unix.Socket;
import io.netty.channel.ChannelMetadata;
import io.netty.channel.ServerChannel;

public abstract class AbstractEpollServerChannel extends AbstractEpollChannel implements ServerChannel
{
    private static final ChannelMetadata METADATA;
    
    protected AbstractEpollServerChannel(final int fd) {
        this(new LinuxSocket(fd), false);
    }
    
    AbstractEpollServerChannel(final LinuxSocket fd) {
        this(fd, AbstractEpollChannel.isSoErrorZero(fd));
    }
    
    AbstractEpollServerChannel(final LinuxSocket fd, final boolean active) {
        super(null, fd, active);
    }
    
    @Override
    public ChannelMetadata metadata() {
        return AbstractEpollServerChannel.METADATA;
    }
    
    @Override
    protected boolean isCompatible(final EventLoop loop) {
        return loop instanceof EpollEventLoop;
    }
    
    @Override
    protected InetSocketAddress remoteAddress0() {
        return null;
    }
    
    @Override
    protected AbstractEpollUnsafe newUnsafe() {
        return new EpollServerSocketUnsafe();
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
    
    final class EpollServerSocketUnsafe extends AbstractEpollUnsafe
    {
        private final byte[] acceptedAddress;
        
        EpollServerSocketUnsafe() {
            this.acceptedAddress = new byte[26];
        }
        
        @Override
        public void connect(final SocketAddress socketAddress, final SocketAddress socketAddress2, final ChannelPromise channelPromise) {
            channelPromise.setFailure((Throwable)new UnsupportedOperationException());
        }
        
        @Override
        void epollInReady() {
            assert AbstractEpollServerChannel.this.eventLoop().inEventLoop();
            final ChannelConfig config = AbstractEpollServerChannel.this.config();
            if (AbstractEpollServerChannel.this.shouldBreakEpollInReady(config)) {
                this.clearEpollIn0();
                return;
            }
            final EpollRecvByteAllocatorHandle allocHandle = this.recvBufAllocHandle();
            allocHandle.edgeTriggered(AbstractEpollServerChannel.this.isFlagSet(Native.EPOLLET));
            final ChannelPipeline pipeline = AbstractEpollServerChannel.this.pipeline();
            allocHandle.reset(config);
            allocHandle.attemptedBytesRead(1);
            this.epollInBefore();
            Throwable exception = null;
            try {
                try {
                    do {
                        allocHandle.lastBytesRead(AbstractEpollServerChannel.this.socket.accept(this.acceptedAddress));
                        if (allocHandle.lastBytesRead() == -1) {
                            break;
                        }
                        allocHandle.incMessagesRead(1);
                        this.readPending = false;
                        pipeline.fireChannelRead((Object)AbstractEpollServerChannel.this.newChildChannel(allocHandle.lastBytesRead(), this.acceptedAddress, 1, this.acceptedAddress[0]));
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
                this.epollInFinally(config);
            }
        }
    }
}
