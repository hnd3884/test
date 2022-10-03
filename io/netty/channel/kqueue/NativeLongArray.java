package io.netty.channel.kqueue;

import io.netty.util.internal.PlatformDependent;
import io.netty.channel.unix.Limits;
import io.netty.channel.unix.Buffer;
import io.netty.util.internal.ObjectUtil;
import java.nio.ByteBuffer;

final class NativeLongArray
{
    private ByteBuffer memory;
    private long memoryAddress;
    private int capacity;
    private int size;
    
    NativeLongArray(final int capacity) {
        this.capacity = ObjectUtil.checkPositive(capacity, "capacity");
        this.memory = Buffer.allocateDirectWithNativeOrder(calculateBufferCapacity(capacity));
        this.memoryAddress = Buffer.memoryAddress(this.memory);
    }
    
    private static int idx(final int index) {
        return index * Limits.SIZEOF_JLONG;
    }
    
    private static int calculateBufferCapacity(final int capacity) {
        return capacity * Limits.SIZEOF_JLONG;
    }
    
    void add(final long value) {
        this.reallocIfNeeded();
        if (PlatformDependent.hasUnsafe()) {
            PlatformDependent.putLong(this.memoryOffset(this.size), value);
        }
        else {
            this.memory.putLong(idx(this.size), value);
        }
        ++this.size;
    }
    
    void clear() {
        this.size = 0;
    }
    
    boolean isEmpty() {
        return this.size == 0;
    }
    
    int size() {
        return this.size;
    }
    
    void free() {
        Buffer.free(this.memory);
        this.memoryAddress = 0L;
    }
    
    long memoryAddress() {
        return this.memoryAddress;
    }
    
    long memoryAddressEnd() {
        return this.memoryOffset(this.size);
    }
    
    private long memoryOffset(final int index) {
        return this.memoryAddress + idx(index);
    }
    
    private void reallocIfNeeded() {
        if (this.size == this.capacity) {
            final int newLength = (this.capacity <= 65536) ? (this.capacity << 1) : (this.capacity + this.capacity >> 1);
            final ByteBuffer buffer = Buffer.allocateDirectWithNativeOrder(calculateBufferCapacity(newLength));
            this.memory.position(0).limit(this.size);
            buffer.put(this.memory);
            buffer.position(0);
            Buffer.free(this.memory);
            this.memory = buffer;
            this.memoryAddress = Buffer.memoryAddress(buffer);
            this.capacity = newLength;
        }
    }
    
    @Override
    public String toString() {
        return "memoryAddress: " + this.memoryAddress + " capacity: " + this.capacity + " size: " + this.size;
    }
}
