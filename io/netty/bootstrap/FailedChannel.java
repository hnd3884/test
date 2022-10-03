package io.netty.bootstrap;

import io.netty.channel.ChannelPromise;
import io.netty.channel.ChannelOutboundBuffer;
import java.net.SocketAddress;
import io.netty.channel.EventLoop;
import io.netty.channel.DefaultChannelConfig;
import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelMetadata;
import io.netty.channel.AbstractChannel;

final class FailedChannel extends AbstractChannel
{
    private static final ChannelMetadata METADATA;
    private final ChannelConfig config;
    
    FailedChannel() {
        super(null);
        this.config = new DefaultChannelConfig(this);
    }
    
    @Override
    protected AbstractUnsafe newUnsafe() {
        return new FailedChannelUnsafe();
    }
    
    @Override
    protected boolean isCompatible(final EventLoop loop) {
        return false;
    }
    
    @Override
    protected SocketAddress localAddress0() {
        return null;
    }
    
    @Override
    protected SocketAddress remoteAddress0() {
        return null;
    }
    
    @Override
    protected void doBind(final SocketAddress localAddress) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    protected void doDisconnect() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    protected void doClose() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    protected void doBeginRead() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    protected void doWrite(final ChannelOutboundBuffer in) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public ChannelConfig config() {
        return this.config;
    }
    
    @Override
    public boolean isOpen() {
        return false;
    }
    
    @Override
    public boolean isActive() {
        return false;
    }
    
    @Override
    public ChannelMetadata metadata() {
        return FailedChannel.METADATA;
    }
    
    static {
        METADATA = new ChannelMetadata(false);
    }
    
    private final class FailedChannelUnsafe extends AbstractUnsafe
    {
        @Override
        public void connect(final SocketAddress remoteAddress, final SocketAddress localAddress, final ChannelPromise promise) {
            promise.setFailure((Throwable)new UnsupportedOperationException());
        }
    }
}
