package io.netty.channel.kqueue;

import io.netty.channel.ChannelConfig;
import io.netty.channel.MessageSizeEstimator;
import io.netty.channel.WriteBufferWaterMark;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelOption;
import java.util.Map;
import io.netty.channel.unix.Limits;
import io.netty.channel.Channel;
import io.netty.channel.DefaultChannelConfig;

public class KQueueChannelConfig extends DefaultChannelConfig
{
    private volatile boolean transportProvidesGuess;
    private volatile long maxBytesPerGatheringWrite;
    
    KQueueChannelConfig(final AbstractKQueueChannel channel) {
        super(channel);
        this.maxBytesPerGatheringWrite = Limits.SSIZE_MAX;
    }
    
    @Override
    public Map<ChannelOption<?>, Object> getOptions() {
        return this.getOptions(super.getOptions(), KQueueChannelOption.RCV_ALLOC_TRANSPORT_PROVIDES_GUESS);
    }
    
    @Override
    public <T> T getOption(final ChannelOption<T> option) {
        if (option == KQueueChannelOption.RCV_ALLOC_TRANSPORT_PROVIDES_GUESS) {
            return (T)Boolean.valueOf(this.getRcvAllocTransportProvidesGuess());
        }
        return super.getOption(option);
    }
    
    @Override
    public <T> boolean setOption(final ChannelOption<T> option, final T value) {
        this.validate(option, value);
        if (option == KQueueChannelOption.RCV_ALLOC_TRANSPORT_PROVIDES_GUESS) {
            this.setRcvAllocTransportProvidesGuess((boolean)value);
            return true;
        }
        return super.setOption(option, value);
    }
    
    public KQueueChannelConfig setRcvAllocTransportProvidesGuess(final boolean transportProvidesGuess) {
        this.transportProvidesGuess = transportProvidesGuess;
        return this;
    }
    
    public boolean getRcvAllocTransportProvidesGuess() {
        return this.transportProvidesGuess;
    }
    
    @Override
    public KQueueChannelConfig setConnectTimeoutMillis(final int connectTimeoutMillis) {
        super.setConnectTimeoutMillis(connectTimeoutMillis);
        return this;
    }
    
    @Deprecated
    @Override
    public KQueueChannelConfig setMaxMessagesPerRead(final int maxMessagesPerRead) {
        super.setMaxMessagesPerRead(maxMessagesPerRead);
        return this;
    }
    
    @Override
    public KQueueChannelConfig setWriteSpinCount(final int writeSpinCount) {
        super.setWriteSpinCount(writeSpinCount);
        return this;
    }
    
    @Override
    public KQueueChannelConfig setAllocator(final ByteBufAllocator allocator) {
        super.setAllocator(allocator);
        return this;
    }
    
    @Override
    public KQueueChannelConfig setRecvByteBufAllocator(final RecvByteBufAllocator allocator) {
        if (!(allocator.newHandle() instanceof RecvByteBufAllocator.ExtendedHandle)) {
            throw new IllegalArgumentException("allocator.newHandle() must return an object of type: " + RecvByteBufAllocator.ExtendedHandle.class);
        }
        super.setRecvByteBufAllocator(allocator);
        return this;
    }
    
    @Override
    public KQueueChannelConfig setAutoRead(final boolean autoRead) {
        super.setAutoRead(autoRead);
        return this;
    }
    
    @Deprecated
    @Override
    public KQueueChannelConfig setWriteBufferHighWaterMark(final int writeBufferHighWaterMark) {
        super.setWriteBufferHighWaterMark(writeBufferHighWaterMark);
        return this;
    }
    
    @Deprecated
    @Override
    public KQueueChannelConfig setWriteBufferLowWaterMark(final int writeBufferLowWaterMark) {
        super.setWriteBufferLowWaterMark(writeBufferLowWaterMark);
        return this;
    }
    
    @Override
    public KQueueChannelConfig setWriteBufferWaterMark(final WriteBufferWaterMark writeBufferWaterMark) {
        super.setWriteBufferWaterMark(writeBufferWaterMark);
        return this;
    }
    
    @Override
    public KQueueChannelConfig setMessageSizeEstimator(final MessageSizeEstimator estimator) {
        super.setMessageSizeEstimator(estimator);
        return this;
    }
    
    @Override
    protected final void autoReadCleared() {
        ((AbstractKQueueChannel)this.channel).clearReadFilter();
    }
    
    final void setMaxBytesPerGatheringWrite(final long maxBytesPerGatheringWrite) {
        this.maxBytesPerGatheringWrite = Math.min(Limits.SSIZE_MAX, maxBytesPerGatheringWrite);
    }
    
    final long getMaxBytesPerGatheringWrite() {
        return this.maxBytesPerGatheringWrite;
    }
}
