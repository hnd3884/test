package io.netty.channel.epoll;

import io.netty.channel.ChannelConfig;
import java.io.IOException;
import io.netty.channel.ChannelException;
import io.netty.channel.MessageSizeEstimator;
import io.netty.channel.WriteBufferWaterMark;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.buffer.ByteBufAllocator;
import java.net.InetAddress;
import io.netty.channel.ChannelOption;
import java.util.Map;
import io.netty.channel.socket.ServerSocketChannelConfig;

public final class EpollServerSocketChannelConfig extends EpollServerChannelConfig implements ServerSocketChannelConfig
{
    EpollServerSocketChannelConfig(final EpollServerSocketChannel channel) {
        super(channel);
        this.setReuseAddress(true);
    }
    
    @Override
    public Map<ChannelOption<?>, Object> getOptions() {
        return this.getOptions(super.getOptions(), EpollChannelOption.SO_REUSEPORT, EpollChannelOption.IP_FREEBIND, EpollChannelOption.IP_TRANSPARENT, EpollChannelOption.TCP_DEFER_ACCEPT);
    }
    
    @Override
    public <T> T getOption(final ChannelOption<T> option) {
        if (option == EpollChannelOption.SO_REUSEPORT) {
            return (T)Boolean.valueOf(this.isReusePort());
        }
        if (option == EpollChannelOption.IP_FREEBIND) {
            return (T)Boolean.valueOf(this.isFreeBind());
        }
        if (option == EpollChannelOption.IP_TRANSPARENT) {
            return (T)Boolean.valueOf(this.isIpTransparent());
        }
        if (option == EpollChannelOption.TCP_DEFER_ACCEPT) {
            return (T)Integer.valueOf(this.getTcpDeferAccept());
        }
        return super.getOption(option);
    }
    
    @Override
    public <T> boolean setOption(final ChannelOption<T> option, final T value) {
        this.validate(option, value);
        if (option == EpollChannelOption.SO_REUSEPORT) {
            this.setReusePort((boolean)value);
        }
        else if (option == EpollChannelOption.IP_FREEBIND) {
            this.setFreeBind((boolean)value);
        }
        else if (option == EpollChannelOption.IP_TRANSPARENT) {
            this.setIpTransparent((boolean)value);
        }
        else if (option == EpollChannelOption.TCP_MD5SIG) {
            final Map<InetAddress, byte[]> m = (Map<InetAddress, byte[]>)value;
            this.setTcpMd5Sig(m);
        }
        else {
            if (option != EpollChannelOption.TCP_DEFER_ACCEPT) {
                return super.setOption(option, value);
            }
            this.setTcpDeferAccept((int)value);
        }
        return true;
    }
    
    @Override
    public EpollServerSocketChannelConfig setReuseAddress(final boolean reuseAddress) {
        super.setReuseAddress(reuseAddress);
        return this;
    }
    
    @Override
    public EpollServerSocketChannelConfig setReceiveBufferSize(final int receiveBufferSize) {
        super.setReceiveBufferSize(receiveBufferSize);
        return this;
    }
    
    @Override
    public EpollServerSocketChannelConfig setPerformancePreferences(final int connectionTime, final int latency, final int bandwidth) {
        return this;
    }
    
    @Override
    public EpollServerSocketChannelConfig setBacklog(final int backlog) {
        super.setBacklog(backlog);
        return this;
    }
    
    @Override
    public EpollServerSocketChannelConfig setConnectTimeoutMillis(final int connectTimeoutMillis) {
        super.setConnectTimeoutMillis(connectTimeoutMillis);
        return this;
    }
    
    @Deprecated
    @Override
    public EpollServerSocketChannelConfig setMaxMessagesPerRead(final int maxMessagesPerRead) {
        super.setMaxMessagesPerRead(maxMessagesPerRead);
        return this;
    }
    
    @Override
    public EpollServerSocketChannelConfig setWriteSpinCount(final int writeSpinCount) {
        super.setWriteSpinCount(writeSpinCount);
        return this;
    }
    
    @Override
    public EpollServerSocketChannelConfig setAllocator(final ByteBufAllocator allocator) {
        super.setAllocator(allocator);
        return this;
    }
    
    @Override
    public EpollServerSocketChannelConfig setRecvByteBufAllocator(final RecvByteBufAllocator allocator) {
        super.setRecvByteBufAllocator(allocator);
        return this;
    }
    
    @Override
    public EpollServerSocketChannelConfig setAutoRead(final boolean autoRead) {
        super.setAutoRead(autoRead);
        return this;
    }
    
    @Deprecated
    @Override
    public EpollServerSocketChannelConfig setWriteBufferHighWaterMark(final int writeBufferHighWaterMark) {
        super.setWriteBufferHighWaterMark(writeBufferHighWaterMark);
        return this;
    }
    
    @Deprecated
    @Override
    public EpollServerSocketChannelConfig setWriteBufferLowWaterMark(final int writeBufferLowWaterMark) {
        super.setWriteBufferLowWaterMark(writeBufferLowWaterMark);
        return this;
    }
    
    @Override
    public EpollServerSocketChannelConfig setWriteBufferWaterMark(final WriteBufferWaterMark writeBufferWaterMark) {
        super.setWriteBufferWaterMark(writeBufferWaterMark);
        return this;
    }
    
    @Override
    public EpollServerSocketChannelConfig setMessageSizeEstimator(final MessageSizeEstimator estimator) {
        super.setMessageSizeEstimator(estimator);
        return this;
    }
    
    public EpollServerSocketChannelConfig setTcpMd5Sig(final Map<InetAddress, byte[]> keys) {
        try {
            ((EpollServerSocketChannel)this.channel).setTcpMd5Sig(keys);
            return this;
        }
        catch (final IOException e) {
            throw new ChannelException(e);
        }
    }
    
    public boolean isReusePort() {
        try {
            return ((EpollServerSocketChannel)this.channel).socket.isReusePort();
        }
        catch (final IOException e) {
            throw new ChannelException(e);
        }
    }
    
    public EpollServerSocketChannelConfig setReusePort(final boolean reusePort) {
        try {
            ((EpollServerSocketChannel)this.channel).socket.setReusePort(reusePort);
            return this;
        }
        catch (final IOException e) {
            throw new ChannelException(e);
        }
    }
    
    public boolean isFreeBind() {
        try {
            return ((EpollServerSocketChannel)this.channel).socket.isIpFreeBind();
        }
        catch (final IOException e) {
            throw new ChannelException(e);
        }
    }
    
    public EpollServerSocketChannelConfig setFreeBind(final boolean freeBind) {
        try {
            ((EpollServerSocketChannel)this.channel).socket.setIpFreeBind(freeBind);
            return this;
        }
        catch (final IOException e) {
            throw new ChannelException(e);
        }
    }
    
    public boolean isIpTransparent() {
        try {
            return ((EpollServerSocketChannel)this.channel).socket.isIpTransparent();
        }
        catch (final IOException e) {
            throw new ChannelException(e);
        }
    }
    
    public EpollServerSocketChannelConfig setIpTransparent(final boolean transparent) {
        try {
            ((EpollServerSocketChannel)this.channel).socket.setIpTransparent(transparent);
            return this;
        }
        catch (final IOException e) {
            throw new ChannelException(e);
        }
    }
    
    public EpollServerSocketChannelConfig setTcpDeferAccept(final int deferAccept) {
        try {
            ((EpollServerSocketChannel)this.channel).socket.setTcpDeferAccept(deferAccept);
            return this;
        }
        catch (final IOException e) {
            throw new ChannelException(e);
        }
    }
    
    public int getTcpDeferAccept() {
        try {
            return ((EpollServerSocketChannel)this.channel).socket.getTcpDeferAccept();
        }
        catch (final IOException e) {
            throw new ChannelException(e);
        }
    }
}
