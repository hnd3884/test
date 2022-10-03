package io.netty.channel.epoll;

import io.netty.channel.socket.ServerSocketChannelConfig;
import io.netty.channel.ChannelConfig;
import java.io.IOException;
import java.util.Map;
import io.netty.channel.unix.NativeInetAddress;
import io.netty.channel.Channel;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import io.netty.channel.EventLoop;
import java.util.Collections;
import java.net.InetAddress;
import java.util.Collection;
import io.netty.channel.socket.ServerSocketChannel;

public final class EpollServerSocketChannel extends AbstractEpollServerChannel implements ServerSocketChannel
{
    private final EpollServerSocketChannelConfig config;
    private volatile Collection<InetAddress> tcpMd5SigAddresses;
    
    public EpollServerSocketChannel() {
        super(LinuxSocket.newSocketStream(), false);
        this.tcpMd5SigAddresses = (Collection<InetAddress>)Collections.emptyList();
        this.config = new EpollServerSocketChannelConfig(this);
    }
    
    public EpollServerSocketChannel(final int fd) {
        this(new LinuxSocket(fd));
    }
    
    EpollServerSocketChannel(final LinuxSocket fd) {
        super(fd);
        this.tcpMd5SigAddresses = (Collection<InetAddress>)Collections.emptyList();
        this.config = new EpollServerSocketChannelConfig(this);
    }
    
    EpollServerSocketChannel(final LinuxSocket fd, final boolean active) {
        super(fd, active);
        this.tcpMd5SigAddresses = (Collection<InetAddress>)Collections.emptyList();
        this.config = new EpollServerSocketChannelConfig(this);
    }
    
    @Override
    protected boolean isCompatible(final EventLoop loop) {
        return loop instanceof EpollEventLoop;
    }
    
    @Override
    protected void doBind(final SocketAddress localAddress) throws Exception {
        super.doBind(localAddress);
        final int tcpFastopen;
        if (Native.IS_SUPPORTING_TCP_FASTOPEN_SERVER && (tcpFastopen = this.config.getTcpFastopen()) > 0) {
            this.socket.setTcpFastOpen(tcpFastopen);
        }
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
    public EpollServerSocketChannelConfig config() {
        return this.config;
    }
    
    protected Channel newChildChannel(final int fd, final byte[] address, final int offset, final int len) throws Exception {
        return new EpollSocketChannel(this, new LinuxSocket(fd), NativeInetAddress.address(address, offset, len));
    }
    
    Collection<InetAddress> tcpMd5SigAddresses() {
        return this.tcpMd5SigAddresses;
    }
    
    void setTcpMd5Sig(final Map<InetAddress, byte[]> keys) throws IOException {
        this.tcpMd5SigAddresses = TcpMd5Util.newTcpMd5Sigs(this, this.tcpMd5SigAddresses, keys);
    }
}
