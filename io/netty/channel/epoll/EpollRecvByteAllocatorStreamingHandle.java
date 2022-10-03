package io.netty.channel.epoll;

import io.netty.channel.RecvByteBufAllocator;

final class EpollRecvByteAllocatorStreamingHandle extends EpollRecvByteAllocatorHandle
{
    EpollRecvByteAllocatorStreamingHandle(final RecvByteBufAllocator.ExtendedHandle handle) {
        super(handle);
    }
    
    @Override
    boolean maybeMoreDataToRead() {
        return this.lastBytesRead() == this.attemptedBytesRead() || this.isReceivedRdHup();
    }
}
