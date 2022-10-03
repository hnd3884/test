package io.netty.channel.socket;

import io.netty.channel.WriteBufferWaterMark;
import io.netty.channel.MessageSizeEstimator;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelConfig;

public interface DuplexChannelConfig extends ChannelConfig
{
    boolean isAllowHalfClosure();
    
    DuplexChannelConfig setAllowHalfClosure(final boolean p0);
    
    @Deprecated
    DuplexChannelConfig setMaxMessagesPerRead(final int p0);
    
    DuplexChannelConfig setWriteSpinCount(final int p0);
    
    DuplexChannelConfig setAllocator(final ByteBufAllocator p0);
    
    DuplexChannelConfig setRecvByteBufAllocator(final RecvByteBufAllocator p0);
    
    DuplexChannelConfig setAutoRead(final boolean p0);
    
    DuplexChannelConfig setAutoClose(final boolean p0);
    
    DuplexChannelConfig setMessageSizeEstimator(final MessageSizeEstimator p0);
    
    DuplexChannelConfig setWriteBufferWaterMark(final WriteBufferWaterMark p0);
}
