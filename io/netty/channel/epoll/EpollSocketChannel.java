package io.netty.channel.epoll;

import io.netty.util.concurrent.GlobalEventExecutor;
import java.util.concurrent.Executor;
import io.netty.channel.socket.SocketChannelConfig;
import io.netty.channel.AbstractChannel;
import io.netty.channel.ChannelConfig;
import java.util.Map;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.socket.ServerSocketChannel;
import java.io.IOException;
import io.netty.channel.ChannelException;
import java.net.SocketAddress;
import java.net.InetSocketAddress;
import io.netty.channel.Channel;
import java.util.Collections;
import java.net.InetAddress;
import java.util.Collection;
import io.netty.channel.socket.SocketChannel;

public final class EpollSocketChannel extends AbstractEpollStreamChannel implements SocketChannel
{
    private final EpollSocketChannelConfig config;
    private volatile Collection<InetAddress> tcpMd5SigAddresses;
    
    public EpollSocketChannel() {
        super(LinuxSocket.newSocketStream(), false);
        this.tcpMd5SigAddresses = (Collection<InetAddress>)Collections.emptyList();
        this.config = new EpollSocketChannelConfig(this);
    }
    
    public EpollSocketChannel(final int fd) {
        super(fd);
        this.tcpMd5SigAddresses = (Collection<InetAddress>)Collections.emptyList();
        this.config = new EpollSocketChannelConfig(this);
    }
    
    EpollSocketChannel(final LinuxSocket fd, final boolean active) {
        super(fd, active);
        this.tcpMd5SigAddresses = (Collection<InetAddress>)Collections.emptyList();
        this.config = new EpollSocketChannelConfig(this);
    }
    
    EpollSocketChannel(final Channel parent, final LinuxSocket fd, final InetSocketAddress remoteAddress) {
        super(parent, fd, remoteAddress);
        this.tcpMd5SigAddresses = (Collection<InetAddress>)Collections.emptyList();
        this.config = new EpollSocketChannelConfig(this);
        if (parent instanceof EpollServerSocketChannel) {
            this.tcpMd5SigAddresses = ((EpollServerSocketChannel)parent).tcpMd5SigAddresses();
        }
    }
    
    public EpollTcpInfo tcpInfo() {
        return this.tcpInfo(new EpollTcpInfo());
    }
    
    public EpollTcpInfo tcpInfo(final EpollTcpInfo info) {
        try {
            this.socket.getTcpInfo(info);
            return info;
        }
        catch (final IOException e) {
            throw new ChannelException(e);
        }
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
    public EpollSocketChannelConfig config() {
        return this.config;
    }
    
    @Override
    public ServerSocketChannel parent() {
        return (ServerSocketChannel)super.parent();
    }
    
    @Override
    protected AbstractEpollUnsafe newUnsafe() {
        return new EpollSocketChannelUnsafe();
    }
    
    @Override
    boolean doConnect0(final SocketAddress remote) throws Exception {
        if (Native.IS_SUPPORTING_TCP_FASTOPEN_CLIENT && this.config.isTcpFastOpenConnect()) {
            final ChannelOutboundBuffer outbound = this.unsafe().outboundBuffer();
            outbound.addFlush();
            final Object curr;
            if ((curr = outbound.current()) instanceof ByteBuf) {
                final ByteBuf initialData = (ByteBuf)curr;
                final long localFlushedAmount = this.doWriteOrSendBytes(initialData, (InetSocketAddress)remote, true);
                if (localFlushedAmount > 0L) {
                    outbound.removeBytes(localFlushedAmount);
                    return true;
                }
            }
        }
        return super.doConnect0(remote);
    }
    
    void setTcpMd5Sig(final Map<InetAddress, byte[]> keys) throws IOException {
        this.tcpMd5SigAddresses = TcpMd5Util.newTcpMd5Sigs(this, this.tcpMd5SigAddresses, keys);
    }
    
    private final class EpollSocketChannelUnsafe extends EpollStreamUnsafe
    {
        @Override
        protected Executor prepareToClose() {
            try {
                if (EpollSocketChannel.this.isOpen() && EpollSocketChannel.this.config().getSoLinger() > 0) {
                    ((EpollEventLoop)EpollSocketChannel.this.eventLoop()).remove(EpollSocketChannel.this);
                    return GlobalEventExecutor.INSTANCE;
                }
            }
            catch (final Throwable t) {}
            return null;
        }
    }
}
