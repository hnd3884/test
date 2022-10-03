package io.netty.channel.kqueue;

import io.netty.channel.ChannelConfig;
import io.netty.channel.MessageSizeEstimator;
import io.netty.channel.WriteBufferWaterMark;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.buffer.ByteBufAllocator;
import java.io.IOException;
import io.netty.channel.ChannelException;
import io.netty.channel.unix.UnixChannelOption;
import io.netty.channel.ChannelOption;
import java.util.Map;
import io.netty.channel.socket.ServerSocketChannelConfig;

public class KQueueServerSocketChannelConfig extends KQueueServerChannelConfig implements ServerSocketChannelConfig
{
    KQueueServerSocketChannelConfig(final KQueueServerSocketChannel channel) {
        super(channel);
        this.setReuseAddress(true);
    }
    
    @Override
    public Map<ChannelOption<?>, Object> getOptions() {
        return this.getOptions(super.getOptions(), UnixChannelOption.SO_REUSEPORT, KQueueChannelOption.SO_ACCEPTFILTER);
    }
    
    @Override
    public <T> T getOption(final ChannelOption<T> option) {
        if (option == UnixChannelOption.SO_REUSEPORT) {
            return (T)Boolean.valueOf(this.isReusePort());
        }
        if (option == KQueueChannelOption.SO_ACCEPTFILTER) {
            return (T)this.getAcceptFilter();
        }
        return super.getOption(option);
    }
    
    @Override
    public <T> boolean setOption(final ChannelOption<T> option, final T value) {
        this.validate(option, value);
        if (option == UnixChannelOption.SO_REUSEPORT) {
            this.setReusePort((boolean)value);
        }
        else {
            if (option != KQueueChannelOption.SO_ACCEPTFILTER) {
                return super.setOption(option, value);
            }
            this.setAcceptFilter((AcceptFilter)value);
        }
        return true;
    }
    
    public KQueueServerSocketChannelConfig setReusePort(final boolean reusePort) {
        try {
            ((KQueueServerSocketChannel)this.channel).socket.setReusePort(reusePort);
            return this;
        }
        catch (final IOException e) {
            throw new ChannelException(e);
        }
    }
    
    public boolean isReusePort() {
        try {
            return ((KQueueServerSocketChannel)this.channel).socket.isReusePort();
        }
        catch (final IOException e) {
            throw new ChannelException(e);
        }
    }
    
    public KQueueServerSocketChannelConfig setAcceptFilter(final AcceptFilter acceptFilter) {
        try {
            ((KQueueServerSocketChannel)this.channel).socket.setAcceptFilter(acceptFilter);
            return this;
        }
        catch (final IOException e) {
            throw new ChannelException(e);
        }
    }
    
    public AcceptFilter getAcceptFilter() {
        try {
            return ((KQueueServerSocketChannel)this.channel).socket.getAcceptFilter();
        }
        catch (final IOException e) {
            throw new ChannelException(e);
        }
    }
    
    @Override
    public KQueueServerSocketChannelConfig setRcvAllocTransportProvidesGuess(final boolean transportProvidesGuess) {
        super.setRcvAllocTransportProvidesGuess(transportProvidesGuess);
        return this;
    }
    
    @Override
    public KQueueServerSocketChannelConfig setReuseAddress(final boolean reuseAddress) {
        super.setReuseAddress(reuseAddress);
        return this;
    }
    
    @Override
    public KQueueServerSocketChannelConfig setReceiveBufferSize(final int receiveBufferSize) {
        super.setReceiveBufferSize(receiveBufferSize);
        return this;
    }
    
    @Override
    public KQueueServerSocketChannelConfig setPerformancePreferences(final int connectionTime, final int latency, final int bandwidth) {
        return this;
    }
    
    @Override
    public KQueueServerSocketChannelConfig setBacklog(final int backlog) {
        super.setBacklog(backlog);
        return this;
    }
    
    @Override
    public KQueueServerSocketChannelConfig setConnectTimeoutMillis(final int connectTimeoutMillis) {
        super.setConnectTimeoutMillis(connectTimeoutMillis);
        return this;
    }
    
    @Deprecated
    @Override
    public KQueueServerSocketChannelConfig setMaxMessagesPerRead(final int maxMessagesPerRead) {
        super.setMaxMessagesPerRead(maxMessagesPerRead);
        return this;
    }
    
    @Override
    public KQueueServerSocketChannelConfig setWriteSpinCount(final int writeSpinCount) {
        super.setWriteSpinCount(writeSpinCount);
        return this;
    }
    
    @Override
    public KQueueServerSocketChannelConfig setAllocator(final ByteBufAllocator allocator) {
        super.setAllocator(allocator);
        return this;
    }
    
    @Override
    public KQueueServerSocketChannelConfig setRecvByteBufAllocator(final RecvByteBufAllocator allocator) {
        super.setRecvByteBufAllocator(allocator);
        return this;
    }
    
    @Override
    public KQueueServerSocketChannelConfig setAutoRead(final boolean autoRead) {
        super.setAutoRead(autoRead);
        return this;
    }
    
    @Deprecated
    @Override
    public KQueueServerSocketChannelConfig setWriteBufferHighWaterMark(final int writeBufferHighWaterMark) {
        super.setWriteBufferHighWaterMark(writeBufferHighWaterMark);
        return this;
    }
    
    @Deprecated
    @Override
    public KQueueServerSocketChannelConfig setWriteBufferLowWaterMark(final int writeBufferLowWaterMark) {
        super.setWriteBufferLowWaterMark(writeBufferLowWaterMark);
        return this;
    }
    
    @Override
    public KQueueServerSocketChannelConfig setWriteBufferWaterMark(final WriteBufferWaterMark writeBufferWaterMark) {
        super.setWriteBufferWaterMark(writeBufferWaterMark);
        return this;
    }
    
    @Override
    public KQueueServerSocketChannelConfig setMessageSizeEstimator(final MessageSizeEstimator estimator) {
        super.setMessageSizeEstimator(estimator);
        return this;
    }
}
