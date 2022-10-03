package io.netty.channel.unix;

import java.nio.ByteOrder;
import io.netty.util.internal.PlatformDependent;
import java.nio.ByteBuffer;

public final class Buffer
{
    private Buffer() {
    }
    
    public static void free(final ByteBuffer buffer) {
        PlatformDependent.freeDirectBuffer(buffer);
    }
    
    public static ByteBuffer allocateDirectWithNativeOrder(final int capacity) {
        return ByteBuffer.allocateDirect(capacity).order(PlatformDependent.BIG_ENDIAN_NATIVE_ORDER ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN);
    }
    
    public static long memoryAddress(final ByteBuffer buffer) {
        assert buffer.isDirect();
        if (PlatformDependent.hasUnsafe()) {
            return PlatformDependent.directBufferAddress(buffer);
        }
        return memoryAddress0(buffer);
    }
    
    public static int addressSize() {
        if (PlatformDependent.hasUnsafe()) {
            return PlatformDependent.addressSize();
        }
        return addressSize0();
    }
    
    private static native int addressSize0();
    
    private static native long memoryAddress0(final ByteBuffer p0);
}
