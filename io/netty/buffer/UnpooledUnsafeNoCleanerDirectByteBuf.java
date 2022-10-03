package io.netty.buffer;

import io.netty.util.internal.PlatformDependent;
import java.nio.ByteBuffer;

class UnpooledUnsafeNoCleanerDirectByteBuf extends UnpooledUnsafeDirectByteBuf
{
    UnpooledUnsafeNoCleanerDirectByteBuf(final ByteBufAllocator alloc, final int initialCapacity, final int maxCapacity) {
        super(alloc, initialCapacity, maxCapacity);
    }
    
    @Override
    protected ByteBuffer allocateDirect(final int initialCapacity) {
        return PlatformDependent.allocateDirectNoCleaner(initialCapacity);
    }
    
    ByteBuffer reallocateDirect(final ByteBuffer oldBuffer, final int initialCapacity) {
        return PlatformDependent.reallocateDirectNoCleaner(oldBuffer, initialCapacity);
    }
    
    @Override
    protected void freeDirect(final ByteBuffer buffer) {
        PlatformDependent.freeDirectNoCleaner(buffer);
    }
    
    @Override
    public ByteBuf capacity(final int newCapacity) {
        this.checkNewCapacity(newCapacity);
        final int oldCapacity = this.capacity();
        if (newCapacity == oldCapacity) {
            return this;
        }
        this.trimIndicesToCapacity(newCapacity);
        this.setByteBuffer(this.reallocateDirect(this.buffer, newCapacity), false);
        return this;
    }
}
