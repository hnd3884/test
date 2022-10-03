package io.netty.channel;

public interface MaxMessagesRecvByteBufAllocator extends RecvByteBufAllocator
{
    int maxMessagesPerRead();
    
    MaxMessagesRecvByteBufAllocator maxMessagesPerRead(final int p0);
}
