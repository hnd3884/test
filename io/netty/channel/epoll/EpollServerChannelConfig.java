package io.netty.channel.epoll;

import io.netty.channel.ChannelConfig;
import io.netty.channel.MessageSizeEstimator;
import io.netty.channel.WriteBufferWaterMark;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.buffer.ByteBufAllocator;
import io.netty.util.internal.ObjectUtil;
import java.io.IOException;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelOption;
import java.util.Map;
import io.netty.util.NetUtil;
import io.netty.channel.socket.ServerSocketChannelConfig;

public class EpollServerChannelConfig extends EpollChannelConfig implements ServerSocketChannelConfig
{
    private volatile int backlog;
    private volatile int pendingFastOpenRequestsThreshold;
    
    EpollServerChannelConfig(final AbstractEpollChannel channel) {
        super(channel);
        this.backlog = NetUtil.SOMAXCONN;
    }
    
    @Override
    public Map<ChannelOption<?>, Object> getOptions() {
        return this.getOptions(super.getOptions(), ChannelOption.SO_RCVBUF, ChannelOption.SO_REUSEADDR, ChannelOption.SO_BACKLOG, ChannelOption.TCP_FASTOPEN);
    }
    
    @Override
    public <T> T getOption(final ChannelOption<T> option) {
        if (option == ChannelOption.SO_RCVBUF) {
            return (T)Integer.valueOf(this.getReceiveBufferSize());
        }
        if (option == ChannelOption.SO_REUSEADDR) {
            return (T)Boolean.valueOf(this.isReuseAddress());
        }
        if (option == ChannelOption.SO_BACKLOG) {
            return (T)Integer.valueOf(this.getBacklog());
        }
        if (option == ChannelOption.TCP_FASTOPEN) {
            return (T)Integer.valueOf(this.getTcpFastopen());
        }
        return super.getOption(option);
    }
    
    @Override
    public <T> boolean setOption(final ChannelOption<T> option, final T value) {
        this.validate(option, value);
        if (option == ChannelOption.SO_RCVBUF) {
            this.setReceiveBufferSize((int)value);
        }
        else if (option == ChannelOption.SO_REUSEADDR) {
            this.setReuseAddress((boolean)value);
        }
        else if (option == ChannelOption.SO_BACKLOG) {
            this.setBacklog((int)value);
        }
        else {
            if (option != ChannelOption.TCP_FASTOPEN) {
                return super.setOption(option, value);
            }
            this.setTcpFastopen((int)value);
        }
        return true;
    }
    
    @Override
    public boolean isReuseAddress() {
        try {
            return ((AbstractEpollChannel)this.channel).socket.isReuseAddress();
        }
        catch (final IOException e) {
            throw new ChannelException(e);
        }
    }
    
    @Override
    public EpollServerChannelConfig setReuseAddress(final boolean reuseAddress) {
        try {
            ((AbstractEpollChannel)this.channel).socket.setReuseAddress(reuseAddress);
            return this;
        }
        catch (final IOException e) {
            throw new ChannelException(e);
        }
    }
    
    @Override
    public int getReceiveBufferSize() {
        try {
            return ((AbstractEpollChannel)this.channel).socket.getReceiveBufferSize();
        }
        catch (final IOException e) {
            throw new ChannelException(e);
        }
    }
    
    @Override
    public EpollServerChannelConfig setReceiveBufferSize(final int receiveBufferSize) {
        try {
            ((AbstractEpollChannel)this.channel).socket.setReceiveBufferSize(receiveBufferSize);
            return this;
        }
        catch (final IOException e) {
            throw new ChannelException(e);
        }
    }
    
    @Override
    public int getBacklog() {
        return this.backlog;
    }
    
    @Override
    public EpollServerChannelConfig setBacklog(final int backlog) {
        ObjectUtil.checkPositiveOrZero(backlog, "backlog");
        this.backlog = backlog;
        return this;
    }
    
    public int getTcpFastopen() {
        return this.pendingFastOpenRequestsThreshold;
    }
    
    public EpollServerChannelConfig setTcpFastopen(final int pendingFastOpenRequestsThreshold) {
        ObjectUtil.checkPositiveOrZero(this.pendingFastOpenRequestsThreshold, "pendingFastOpenRequestsThreshold");
        this.pendingFastOpenRequestsThreshold = pendingFastOpenRequestsThreshold;
        return this;
    }
    
    @Override
    public EpollServerChannelConfig setPerformancePreferences(final int connectionTime, final int latency, final int bandwidth) {
        return this;
    }
    
    @Override
    public EpollServerChannelConfig setConnectTimeoutMillis(final int connectTimeoutMillis) {
        super.setConnectTimeoutMillis(connectTimeoutMillis);
        return this;
    }
    
    @Deprecated
    @Override
    public EpollServerChannelConfig setMaxMessagesPerRead(final int maxMessagesPerRead) {
        super.setMaxMessagesPerRead(maxMessagesPerRead);
        return this;
    }
    
    @Override
    public EpollServerChannelConfig setWriteSpinCount(final int writeSpinCount) {
        super.setWriteSpinCount(writeSpinCount);
        return this;
    }
    
    @Override
    public EpollServerChannelConfig setAllocator(final ByteBufAllocator allocator) {
        super.setAllocator(allocator);
        return this;
    }
    
    @Override
    public EpollServerChannelConfig setRecvByteBufAllocator(final RecvByteBufAllocator allocator) {
        super.setRecvByteBufAllocator(allocator);
        return this;
    }
    
    @Override
    public EpollServerChannelConfig setAutoRead(final boolean autoRead) {
        super.setAutoRead(autoRead);
        return this;
    }
    
    @Deprecated
    @Override
    public EpollServerChannelConfig setWriteBufferHighWaterMark(final int writeBufferHighWaterMark) {
        super.setWriteBufferHighWaterMark(writeBufferHighWaterMark);
        return this;
    }
    
    @Deprecated
    @Override
    public EpollServerChannelConfig setWriteBufferLowWaterMark(final int writeBufferLowWaterMark) {
        super.setWriteBufferLowWaterMark(writeBufferLowWaterMark);
        return this;
    }
    
    @Override
    public EpollServerChannelConfig setWriteBufferWaterMark(final WriteBufferWaterMark writeBufferWaterMark) {
        super.setWriteBufferWaterMark(writeBufferWaterMark);
        return this;
    }
    
    @Override
    public EpollServerChannelConfig setMessageSizeEstimator(final MessageSizeEstimator estimator) {
        super.setMessageSizeEstimator(estimator);
        return this;
    }
    
    @Override
    public EpollServerChannelConfig setEpollMode(final EpollMode mode) {
        super.setEpollMode(mode);
        return this;
    }
}
