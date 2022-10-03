package io.netty.channel.epoll;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.util.UncheckedBooleanSupplier;
import io.netty.channel.unix.PreferredDirectByteBufAllocator;
import io.netty.channel.RecvByteBufAllocator;

class EpollRecvByteAllocatorHandle extends RecvByteBufAllocator.DelegatingHandle implements RecvByteBufAllocator.ExtendedHandle
{
    private final PreferredDirectByteBufAllocator preferredDirectByteBufAllocator;
    private final UncheckedBooleanSupplier defaultMaybeMoreDataSupplier;
    private boolean isEdgeTriggered;
    private boolean receivedRdHup;
    
    EpollRecvByteAllocatorHandle(final RecvByteBufAllocator.ExtendedHandle handle) {
        super(handle);
        this.preferredDirectByteBufAllocator = new PreferredDirectByteBufAllocator();
        this.defaultMaybeMoreDataSupplier = new UncheckedBooleanSupplier() {
            @Override
            public boolean get() {
                return EpollRecvByteAllocatorHandle.this.maybeMoreDataToRead();
            }
        };
    }
    
    final void receivedRdHup() {
        this.receivedRdHup = true;
    }
    
    final boolean isReceivedRdHup() {
        return this.receivedRdHup;
    }
    
    boolean maybeMoreDataToRead() {
        return (this.isEdgeTriggered && this.lastBytesRead() > 0) || (!this.isEdgeTriggered && this.lastBytesRead() == this.attemptedBytesRead());
    }
    
    final void edgeTriggered(final boolean edgeTriggered) {
        this.isEdgeTriggered = edgeTriggered;
    }
    
    final boolean isEdgeTriggered() {
        return this.isEdgeTriggered;
    }
    
    @Override
    public final ByteBuf allocate(final ByteBufAllocator alloc) {
        this.preferredDirectByteBufAllocator.updateAllocator(alloc);
        return this.delegate().allocate(this.preferredDirectByteBufAllocator);
    }
    
    @Override
    public final boolean continueReading(final UncheckedBooleanSupplier maybeMoreDataSupplier) {
        return ((RecvByteBufAllocator.ExtendedHandle)this.delegate()).continueReading(maybeMoreDataSupplier);
    }
    
    @Override
    public final boolean continueReading() {
        return this.continueReading(this.defaultMaybeMoreDataSupplier);
    }
}
