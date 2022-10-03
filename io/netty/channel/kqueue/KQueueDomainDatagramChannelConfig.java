package io.netty.channel.kqueue;

import io.netty.channel.FixedRecvByteBufAllocator;
import io.netty.channel.ChannelConfig;
import io.netty.channel.WriteBufferWaterMark;
import java.io.IOException;
import io.netty.channel.ChannelException;
import io.netty.channel.MessageSizeEstimator;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelOption;
import java.util.Map;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.unix.DomainDatagramChannelConfig;

public final class KQueueDomainDatagramChannelConfig extends KQueueChannelConfig implements DomainDatagramChannelConfig
{
    private static final RecvByteBufAllocator DEFAULT_RCVBUF_ALLOCATOR;
    private boolean activeOnOpen;
    
    KQueueDomainDatagramChannelConfig(final KQueueDomainDatagramChannel channel) {
        super(channel);
        this.setRecvByteBufAllocator(KQueueDomainDatagramChannelConfig.DEFAULT_RCVBUF_ALLOCATOR);
    }
    
    @Override
    public Map<ChannelOption<?>, Object> getOptions() {
        return this.getOptions(super.getOptions(), ChannelOption.DATAGRAM_CHANNEL_ACTIVE_ON_REGISTRATION, ChannelOption.SO_SNDBUF);
    }
    
    @Override
    public <T> T getOption(final ChannelOption<T> option) {
        if (option == ChannelOption.DATAGRAM_CHANNEL_ACTIVE_ON_REGISTRATION) {
            return (T)Boolean.valueOf(this.activeOnOpen);
        }
        if (option == ChannelOption.SO_SNDBUF) {
            return (T)Integer.valueOf(this.getSendBufferSize());
        }
        return super.getOption(option);
    }
    
    @Override
    public <T> boolean setOption(final ChannelOption<T> option, final T value) {
        this.validate(option, value);
        if (option == ChannelOption.DATAGRAM_CHANNEL_ACTIVE_ON_REGISTRATION) {
            this.setActiveOnOpen((boolean)value);
        }
        else {
            if (option != ChannelOption.SO_SNDBUF) {
                return super.setOption(option, value);
            }
            this.setSendBufferSize((int)value);
        }
        return true;
    }
    
    private void setActiveOnOpen(final boolean activeOnOpen) {
        if (this.channel.isRegistered()) {
            throw new IllegalStateException("Can only changed before channel was registered");
        }
        this.activeOnOpen = activeOnOpen;
    }
    
    boolean getActiveOnOpen() {
        return this.activeOnOpen;
    }
    
    @Override
    public KQueueDomainDatagramChannelConfig setAllocator(final ByteBufAllocator allocator) {
        super.setAllocator(allocator);
        return this;
    }
    
    @Override
    public KQueueDomainDatagramChannelConfig setAutoClose(final boolean autoClose) {
        super.setAutoClose(autoClose);
        return this;
    }
    
    @Override
    public KQueueDomainDatagramChannelConfig setAutoRead(final boolean autoRead) {
        super.setAutoRead(autoRead);
        return this;
    }
    
    @Override
    public KQueueDomainDatagramChannelConfig setConnectTimeoutMillis(final int connectTimeoutMillis) {
        super.setConnectTimeoutMillis(connectTimeoutMillis);
        return this;
    }
    
    @Deprecated
    @Override
    public KQueueDomainDatagramChannelConfig setMaxMessagesPerRead(final int maxMessagesPerRead) {
        super.setMaxMessagesPerRead(maxMessagesPerRead);
        return this;
    }
    
    @Override
    public KQueueDomainDatagramChannelConfig setMaxMessagesPerWrite(final int maxMessagesPerWrite) {
        super.setMaxMessagesPerWrite(maxMessagesPerWrite);
        return this;
    }
    
    @Override
    public KQueueDomainDatagramChannelConfig setMessageSizeEstimator(final MessageSizeEstimator estimator) {
        super.setMessageSizeEstimator(estimator);
        return this;
    }
    
    @Override
    public KQueueDomainDatagramChannelConfig setRcvAllocTransportProvidesGuess(final boolean transportProvidesGuess) {
        super.setRcvAllocTransportProvidesGuess(transportProvidesGuess);
        return this;
    }
    
    @Override
    public KQueueDomainDatagramChannelConfig setRecvByteBufAllocator(final RecvByteBufAllocator allocator) {
        super.setRecvByteBufAllocator(allocator);
        return this;
    }
    
    @Override
    public KQueueDomainDatagramChannelConfig setSendBufferSize(final int sendBufferSize) {
        try {
            ((KQueueDomainDatagramChannel)this.channel).socket.setSendBufferSize(sendBufferSize);
            return this;
        }
        catch (final IOException e) {
            throw new ChannelException(e);
        }
    }
    
    @Override
    public int getSendBufferSize() {
        try {
            return ((KQueueDomainDatagramChannel)this.channel).socket.getSendBufferSize();
        }
        catch (final IOException e) {
            throw new ChannelException(e);
        }
    }
    
    @Override
    public KQueueDomainDatagramChannelConfig setWriteBufferWaterMark(final WriteBufferWaterMark writeBufferWaterMark) {
        super.setWriteBufferWaterMark(writeBufferWaterMark);
        return this;
    }
    
    @Override
    public KQueueDomainDatagramChannelConfig setWriteSpinCount(final int writeSpinCount) {
        super.setWriteSpinCount(writeSpinCount);
        return this;
    }
    
    static {
        DEFAULT_RCVBUF_ALLOCATOR = new FixedRecvByteBufAllocator(2048);
    }
}
