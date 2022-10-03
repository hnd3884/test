package io.netty.buffer;

import java.nio.ByteBuffer;
import io.netty.util.internal.PlatformDependent;

final class WrappedUnpooledUnsafeDirectByteBuf extends UnpooledUnsafeDirectByteBuf
{
    WrappedUnpooledUnsafeDirectByteBuf(final ByteBufAllocator alloc, final long memoryAddress, final int size, final boolean doFree) {
        super(alloc, PlatformDependent.directBuffer(memoryAddress, size), size, doFree);
    }
    
    @Override
    protected void freeDirect(final ByteBuffer buffer) {
        PlatformDependent.freeMemory(this.memoryAddress);
    }
}
