package io.netty.channel.kqueue;

import io.netty.channel.socket.ServerSocketChannelConfig;
import io.netty.channel.ChannelConfig;
import io.netty.channel.unix.NativeInetAddress;
import io.netty.channel.Channel;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import io.netty.channel.EventLoop;
import io.netty.channel.socket.ServerSocketChannel;

public final class KQueueServerSocketChannel extends AbstractKQueueServerChannel implements ServerSocketChannel
{
    private final KQueueServerSocketChannelConfig config;
    
    public KQueueServerSocketChannel() {
        super(BsdSocket.newSocketStream(), false);
        this.config = new KQueueServerSocketChannelConfig(this);
    }
    
    public KQueueServerSocketChannel(final int fd) {
        this(new BsdSocket(fd));
    }
    
    KQueueServerSocketChannel(final BsdSocket fd) {
        super(fd);
        this.config = new KQueueServerSocketChannelConfig(this);
    }
    
    KQueueServerSocketChannel(final BsdSocket fd, final boolean active) {
        super(fd, active);
        this.config = new KQueueServerSocketChannelConfig(this);
    }
    
    @Override
    protected boolean isCompatible(final EventLoop loop) {
        return loop instanceof KQueueEventLoop;
    }
    
    @Override
    protected void doBind(final SocketAddress localAddress) throws Exception {
        super.doBind(localAddress);
        this.socket.listen(this.config.getBacklog());
        this.active = true;
    }
    
    @Override
    public InetSocketAddress remoteAddress() {
        return (InetSocketAddress)super.remoteAddress();
    }
    
    @Override
    public InetSocketAddress localAddress() {
        return (InetSocketAddress)super.localAddress();
    }
    
    @Override
    public KQueueServerSocketChannelConfig config() {
        return this.config;
    }
    
    protected Channel newChildChannel(final int fd, final byte[] address, final int offset, final int len) throws Exception {
        return new KQueueSocketChannel(this, new BsdSocket(fd), NativeInetAddress.address(address, offset, len));
    }
}
