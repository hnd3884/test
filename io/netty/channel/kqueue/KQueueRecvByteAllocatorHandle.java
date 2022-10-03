package io.netty.channel.kqueue;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelConfig;
import io.netty.util.UncheckedBooleanSupplier;
import io.netty.channel.unix.PreferredDirectByteBufAllocator;
import io.netty.channel.RecvByteBufAllocator;

final class KQueueRecvByteAllocatorHandle extends RecvByteBufAllocator.DelegatingHandle implements RecvByteBufAllocator.ExtendedHandle
{
    private final PreferredDirectByteBufAllocator preferredDirectByteBufAllocator;
    private final UncheckedBooleanSupplier defaultMaybeMoreDataSupplier;
    private boolean overrideGuess;
    private boolean readEOF;
    private long numberBytesPending;
    
    KQueueRecvByteAllocatorHandle(final RecvByteBufAllocator.ExtendedHandle handle) {
        super(handle);
        this.preferredDirectByteBufAllocator = new PreferredDirectByteBufAllocator();
        this.defaultMaybeMoreDataSupplier = new UncheckedBooleanSupplier() {
            @Override
            public boolean get() {
                return KQueueRecvByteAllocatorHandle.this.maybeMoreDataToRead();
            }
        };
    }
    
    @Override
    public int guess() {
        return this.overrideGuess ? this.guess0() : this.delegate().guess();
    }
    
    @Override
    public void reset(final ChannelConfig config) {
        this.overrideGuess = ((KQueueChannelConfig)config).getRcvAllocTransportProvidesGuess();
        this.delegate().reset(config);
    }
    
    @Override
    public ByteBuf allocate(final ByteBufAllocator alloc) {
        this.preferredDirectByteBufAllocator.updateAllocator(alloc);
        return this.overrideGuess ? this.preferredDirectByteBufAllocator.ioBuffer(this.guess0()) : this.delegate().allocate(this.preferredDirectByteBufAllocator);
    }
    
    @Override
    public void lastBytesRead(final int bytes) {
        this.numberBytesPending = ((bytes < 0) ? 0L : Math.max(0L, this.numberBytesPending - bytes));
        this.delegate().lastBytesRead(bytes);
    }
    
    @Override
    public boolean continueReading(final UncheckedBooleanSupplier maybeMoreDataSupplier) {
        return ((RecvByteBufAllocator.ExtendedHandle)this.delegate()).continueReading(maybeMoreDataSupplier);
    }
    
    @Override
    public boolean continueReading() {
        return this.continueReading(this.defaultMaybeMoreDataSupplier);
    }
    
    void readEOF() {
        this.readEOF = true;
    }
    
    boolean isReadEOF() {
        return this.readEOF;
    }
    
    void numberBytesPending(final long numberBytesPending) {
        this.numberBytesPending = numberBytesPending;
    }
    
    boolean maybeMoreDataToRead() {
        return this.numberBytesPending != 0L;
    }
    
    private int guess0() {
        return (int)Math.min(this.numberBytesPending, 2147483647L);
    }
}
