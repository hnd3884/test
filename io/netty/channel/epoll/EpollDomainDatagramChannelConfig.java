package io.netty.channel.epoll;

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

public final class EpollDomainDatagramChannelConfig extends EpollChannelConfig implements DomainDatagramChannelConfig
{
    private static final RecvByteBufAllocator DEFAULT_RCVBUF_ALLOCATOR;
    private boolean activeOnOpen;
    
    EpollDomainDatagramChannelConfig(final EpollDomainDatagramChannel channel) {
        super(channel);
        this.setRecvByteBufAllocator(EpollDomainDatagramChannelConfig.DEFAULT_RCVBUF_ALLOCATOR);
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
    public EpollDomainDatagramChannelConfig setAllocator(final ByteBufAllocator allocator) {
        super.setAllocator(allocator);
        return this;
    }
    
    @Override
    public EpollDomainDatagramChannelConfig setAutoClose(final boolean autoClose) {
        super.setAutoClose(autoClose);
        return this;
    }
    
    @Override
    public EpollDomainDatagramChannelConfig setAutoRead(final boolean autoRead) {
        super.setAutoRead(autoRead);
        return this;
    }
    
    @Override
    public EpollDomainDatagramChannelConfig setConnectTimeoutMillis(final int connectTimeoutMillis) {
        super.setConnectTimeoutMillis(connectTimeoutMillis);
        return this;
    }
    
    @Override
    public EpollDomainDatagramChannelConfig setEpollMode(final EpollMode mode) {
        super.setEpollMode(mode);
        return this;
    }
    
    @Deprecated
    @Override
    public EpollDomainDatagramChannelConfig setMaxMessagesPerRead(final int maxMessagesPerRead) {
        super.setMaxMessagesPerRead(maxMessagesPerRead);
        return this;
    }
    
    @Override
    public EpollDomainDatagramChannelConfig setMaxMessagesPerWrite(final int maxMessagesPerWrite) {
        super.setMaxMessagesPerWrite(maxMessagesPerWrite);
        return this;
    }
    
    @Override
    public EpollDomainDatagramChannelConfig setMessageSizeEstimator(final MessageSizeEstimator estimator) {
        super.setMessageSizeEstimator(estimator);
        return this;
    }
    
    @Override
    public EpollDomainDatagramChannelConfig setRecvByteBufAllocator(final RecvByteBufAllocator allocator) {
        super.setRecvByteBufAllocator(allocator);
        return this;
    }
    
    @Override
    public EpollDomainDatagramChannelConfig setSendBufferSize(final int sendBufferSize) {
        try {
            ((EpollDomainDatagramChannel)this.channel).socket.setSendBufferSize(sendBufferSize);
            return this;
        }
        catch (final IOException e) {
            throw new ChannelException(e);
        }
    }
    
    @Override
    public int getSendBufferSize() {
        try {
            return ((EpollDomainDatagramChannel)this.channel).socket.getSendBufferSize();
        }
        catch (final IOException e) {
            throw new ChannelException(e);
        }
    }
    
    @Override
    public EpollDomainDatagramChannelConfig setWriteBufferWaterMark(final WriteBufferWaterMark writeBufferWaterMark) {
        super.setWriteBufferWaterMark(writeBufferWaterMark);
        return this;
    }
    
    @Override
    public EpollDomainDatagramChannelConfig setWriteSpinCount(final int writeSpinCount) {
        super.setWriteSpinCount(writeSpinCount);
        return this;
    }
    
    static {
        DEFAULT_RCVBUF_ALLOCATOR = new FixedRecvByteBufAllocator(2048);
    }
}
