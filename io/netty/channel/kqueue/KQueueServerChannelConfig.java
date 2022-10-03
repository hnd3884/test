package io.netty.channel.kqueue;

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

public class KQueueServerChannelConfig extends KQueueChannelConfig implements ServerSocketChannelConfig
{
    private volatile int backlog;
    
    KQueueServerChannelConfig(final AbstractKQueueChannel channel) {
        super(channel);
        this.backlog = NetUtil.SOMAXCONN;
    }
    
    @Override
    public Map<ChannelOption<?>, Object> getOptions() {
        return this.getOptions(super.getOptions(), ChannelOption.SO_RCVBUF, ChannelOption.SO_REUSEADDR, ChannelOption.SO_BACKLOG);
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
        else {
            if (option != ChannelOption.SO_BACKLOG) {
                return super.setOption(option, value);
            }
            this.setBacklog((int)value);
        }
        return true;
    }
    
    @Override
    public boolean isReuseAddress() {
        try {
            return ((AbstractKQueueChannel)this.channel).socket.isReuseAddress();
        }
        catch (final IOException e) {
            throw new ChannelException(e);
        }
    }
    
    @Override
    public KQueueServerChannelConfig setReuseAddress(final boolean reuseAddress) {
        try {
            ((AbstractKQueueChannel)this.channel).socket.setReuseAddress(reuseAddress);
            return this;
        }
        catch (final IOException e) {
            throw new ChannelException(e);
        }
    }
    
    @Override
    public int getReceiveBufferSize() {
        try {
            return ((AbstractKQueueChannel)this.channel).socket.getReceiveBufferSize();
        }
        catch (final IOException e) {
            throw new ChannelException(e);
        }
    }
    
    @Override
    public KQueueServerChannelConfig setReceiveBufferSize(final int receiveBufferSize) {
        try {
            ((AbstractKQueueChannel)this.channel).socket.setReceiveBufferSize(receiveBufferSize);
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
    public KQueueServerChannelConfig setBacklog(final int backlog) {
        ObjectUtil.checkPositiveOrZero(backlog, "backlog");
        this.backlog = backlog;
        return this;
    }
    
    @Override
    public KQueueServerChannelConfig setRcvAllocTransportProvidesGuess(final boolean transportProvidesGuess) {
        super.setRcvAllocTransportProvidesGuess(transportProvidesGuess);
        return this;
    }
    
    @Override
    public KQueueServerChannelConfig setPerformancePreferences(final int connectionTime, final int latency, final int bandwidth) {
        return this;
    }
    
    @Override
    public KQueueServerChannelConfig setConnectTimeoutMillis(final int connectTimeoutMillis) {
        super.setConnectTimeoutMillis(connectTimeoutMillis);
        return this;
    }
    
    @Deprecated
    @Override
    public KQueueServerChannelConfig setMaxMessagesPerRead(final int maxMessagesPerRead) {
        super.setMaxMessagesPerRead(maxMessagesPerRead);
        return this;
    }
    
    @Override
    public KQueueServerChannelConfig setWriteSpinCount(final int writeSpinCount) {
        super.setWriteSpinCount(writeSpinCount);
        return this;
    }
    
    @Override
    public KQueueServerChannelConfig setAllocator(final ByteBufAllocator allocator) {
        super.setAllocator(allocator);
        return this;
    }
    
    @Override
    public KQueueServerChannelConfig setRecvByteBufAllocator(final RecvByteBufAllocator allocator) {
        super.setRecvByteBufAllocator(allocator);
        return this;
    }
    
    @Override
    public KQueueServerChannelConfig setAutoRead(final boolean autoRead) {
        super.setAutoRead(autoRead);
        return this;
    }
    
    @Deprecated
    @Override
    public KQueueServerChannelConfig setWriteBufferHighWaterMark(final int writeBufferHighWaterMark) {
        super.setWriteBufferHighWaterMark(writeBufferHighWaterMark);
        return this;
    }
    
    @Deprecated
    @Override
    public KQueueServerChannelConfig setWriteBufferLowWaterMark(final int writeBufferLowWaterMark) {
        super.setWriteBufferLowWaterMark(writeBufferLowWaterMark);
        return this;
    }
    
    @Override
    public KQueueServerChannelConfig setWriteBufferWaterMark(final WriteBufferWaterMark writeBufferWaterMark) {
        super.setWriteBufferWaterMark(writeBufferWaterMark);
        return this;
    }
    
    @Override
    public KQueueServerChannelConfig setMessageSizeEstimator(final MessageSizeEstimator estimator) {
        super.setMessageSizeEstimator(estimator);
        return this;
    }
}
