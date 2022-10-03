package io.netty.channel.epoll;

import io.netty.channel.ChannelConfig;
import java.io.IOException;
import io.netty.util.internal.ObjectUtil;
import io.netty.channel.WriteBufferWaterMark;
import io.netty.channel.MessageSizeEstimator;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.unix.UnixChannelOption;
import io.netty.channel.ChannelOption;
import java.util.Map;
import io.netty.channel.unix.DomainSocketReadMode;
import io.netty.channel.socket.DuplexChannelConfig;
import io.netty.channel.unix.DomainSocketChannelConfig;

public final class EpollDomainSocketChannelConfig extends EpollChannelConfig implements DomainSocketChannelConfig, DuplexChannelConfig
{
    private volatile DomainSocketReadMode mode;
    private volatile boolean allowHalfClosure;
    
    EpollDomainSocketChannelConfig(final AbstractEpollChannel channel) {
        super(channel);
        this.mode = DomainSocketReadMode.BYTES;
    }
    
    @Override
    public Map<ChannelOption<?>, Object> getOptions() {
        return this.getOptions(super.getOptions(), UnixChannelOption.DOMAIN_SOCKET_READ_MODE, ChannelOption.ALLOW_HALF_CLOSURE, ChannelOption.SO_SNDBUF, ChannelOption.SO_RCVBUF);
    }
    
    @Override
    public <T> T getOption(final ChannelOption<T> option) {
        if (option == UnixChannelOption.DOMAIN_SOCKET_READ_MODE) {
            return (T)this.getReadMode();
        }
        if (option == ChannelOption.ALLOW_HALF_CLOSURE) {
            return (T)Boolean.valueOf(this.isAllowHalfClosure());
        }
        if (option == ChannelOption.SO_SNDBUF) {
            return (T)Integer.valueOf(this.getSendBufferSize());
        }
        if (option == ChannelOption.SO_RCVBUF) {
            return (T)Integer.valueOf(this.getReceiveBufferSize());
        }
        return super.getOption(option);
    }
    
    @Override
    public <T> boolean setOption(final ChannelOption<T> option, final T value) {
        this.validate(option, value);
        if (option == UnixChannelOption.DOMAIN_SOCKET_READ_MODE) {
            this.setReadMode((DomainSocketReadMode)value);
        }
        else if (option == ChannelOption.ALLOW_HALF_CLOSURE) {
            this.setAllowHalfClosure((boolean)value);
        }
        else if (option == ChannelOption.SO_SNDBUF) {
            this.setSendBufferSize((int)value);
        }
        else {
            if (option != ChannelOption.SO_RCVBUF) {
                return super.setOption(option, value);
            }
            this.setReceiveBufferSize((int)value);
        }
        return true;
    }
    
    @Deprecated
    @Override
    public EpollDomainSocketChannelConfig setMaxMessagesPerRead(final int maxMessagesPerRead) {
        super.setMaxMessagesPerRead(maxMessagesPerRead);
        return this;
    }
    
    @Override
    public EpollDomainSocketChannelConfig setConnectTimeoutMillis(final int connectTimeoutMillis) {
        super.setConnectTimeoutMillis(connectTimeoutMillis);
        return this;
    }
    
    @Override
    public EpollDomainSocketChannelConfig setWriteSpinCount(final int writeSpinCount) {
        super.setWriteSpinCount(writeSpinCount);
        return this;
    }
    
    @Override
    public EpollDomainSocketChannelConfig setRecvByteBufAllocator(final RecvByteBufAllocator allocator) {
        super.setRecvByteBufAllocator(allocator);
        return this;
    }
    
    @Override
    public EpollDomainSocketChannelConfig setAllocator(final ByteBufAllocator allocator) {
        super.setAllocator(allocator);
        return this;
    }
    
    @Override
    public EpollDomainSocketChannelConfig setAutoClose(final boolean autoClose) {
        super.setAutoClose(autoClose);
        return this;
    }
    
    @Override
    public EpollDomainSocketChannelConfig setMessageSizeEstimator(final MessageSizeEstimator estimator) {
        super.setMessageSizeEstimator(estimator);
        return this;
    }
    
    @Deprecated
    @Override
    public EpollDomainSocketChannelConfig setWriteBufferLowWaterMark(final int writeBufferLowWaterMark) {
        super.setWriteBufferLowWaterMark(writeBufferLowWaterMark);
        return this;
    }
    
    @Deprecated
    @Override
    public EpollDomainSocketChannelConfig setWriteBufferHighWaterMark(final int writeBufferHighWaterMark) {
        super.setWriteBufferHighWaterMark(writeBufferHighWaterMark);
        return this;
    }
    
    @Override
    public EpollDomainSocketChannelConfig setWriteBufferWaterMark(final WriteBufferWaterMark writeBufferWaterMark) {
        super.setWriteBufferWaterMark(writeBufferWaterMark);
        return this;
    }
    
    @Override
    public EpollDomainSocketChannelConfig setAutoRead(final boolean autoRead) {
        super.setAutoRead(autoRead);
        return this;
    }
    
    @Override
    public EpollDomainSocketChannelConfig setEpollMode(final EpollMode mode) {
        super.setEpollMode(mode);
        return this;
    }
    
    @Override
    public EpollDomainSocketChannelConfig setReadMode(final DomainSocketReadMode mode) {
        this.mode = ObjectUtil.checkNotNull(mode, "mode");
        return this;
    }
    
    @Override
    public DomainSocketReadMode getReadMode() {
        return this.mode;
    }
    
    @Override
    public boolean isAllowHalfClosure() {
        return this.allowHalfClosure;
    }
    
    @Override
    public EpollDomainSocketChannelConfig setAllowHalfClosure(final boolean allowHalfClosure) {
        this.allowHalfClosure = allowHalfClosure;
        return this;
    }
    
    public int getSendBufferSize() {
        try {
            return ((EpollDomainSocketChannel)this.channel).socket.getSendBufferSize();
        }
        catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    public EpollDomainSocketChannelConfig setSendBufferSize(final int sendBufferSize) {
        try {
            ((EpollDomainSocketChannel)this.channel).socket.setSendBufferSize(sendBufferSize);
            return this;
        }
        catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    public int getReceiveBufferSize() {
        try {
            return ((EpollDomainSocketChannel)this.channel).socket.getReceiveBufferSize();
        }
        catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    public EpollDomainSocketChannelConfig setReceiveBufferSize(final int receiveBufferSize) {
        try {
            ((EpollDomainSocketChannel)this.channel).socket.setReceiveBufferSize(receiveBufferSize);
            return this;
        }
        catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }
}
