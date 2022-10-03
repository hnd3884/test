package io.netty.channel.unix;

import io.netty.channel.WriteBufferWaterMark;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.MessageSizeEstimator;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelConfig;

public interface DomainDatagramChannelConfig extends ChannelConfig
{
    DomainDatagramChannelConfig setAllocator(final ByteBufAllocator p0);
    
    DomainDatagramChannelConfig setAutoClose(final boolean p0);
    
    DomainDatagramChannelConfig setAutoRead(final boolean p0);
    
    DomainDatagramChannelConfig setConnectTimeoutMillis(final int p0);
    
    @Deprecated
    DomainDatagramChannelConfig setMaxMessagesPerRead(final int p0);
    
    DomainDatagramChannelConfig setMessageSizeEstimator(final MessageSizeEstimator p0);
    
    DomainDatagramChannelConfig setRecvByteBufAllocator(final RecvByteBufAllocator p0);
    
    DomainDatagramChannelConfig setSendBufferSize(final int p0);
    
    int getSendBufferSize();
    
    DomainDatagramChannelConfig setWriteBufferWaterMark(final WriteBufferWaterMark p0);
    
    DomainDatagramChannelConfig setWriteSpinCount(final int p0);
}
