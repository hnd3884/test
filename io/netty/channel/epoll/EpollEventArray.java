package io.netty.channel.epoll;

import io.netty.util.internal.PlatformDependent;
import io.netty.channel.unix.Buffer;
import java.nio.ByteBuffer;

final class EpollEventArray
{
    private static final int EPOLL_EVENT_SIZE;
    private static final int EPOLL_DATA_OFFSET;
    private ByteBuffer memory;
    private long memoryAddress;
    private int length;
    
    EpollEventArray(final int length) {
        if (length < 1) {
            throw new IllegalArgumentException("length must be >= 1 but was " + length);
        }
        this.length = length;
        this.memory = Buffer.allocateDirectWithNativeOrder(calculateBufferCapacity(length));
        this.memoryAddress = Buffer.memoryAddress(this.memory);
    }
    
    long memoryAddress() {
        return this.memoryAddress;
    }
    
    int length() {
        return this.length;
    }
    
    void increase() {
        this.length <<= 1;
        final ByteBuffer buffer = Buffer.allocateDirectWithNativeOrder(calculateBufferCapacity(this.length));
        Buffer.free(this.memory);
        this.memory = buffer;
        this.memoryAddress = Buffer.memoryAddress(buffer);
    }
    
    void free() {
        Buffer.free(this.memory);
        this.memoryAddress = 0L;
    }
    
    int events(final int index) {
        return this.getInt(index, 0);
    }
    
    int fd(final int index) {
        return this.getInt(index, EpollEventArray.EPOLL_DATA_OFFSET);
    }
    
    private int getInt(final int index, final int offset) {
        if (PlatformDependent.hasUnsafe()) {
            final long n = index * (long)EpollEventArray.EPOLL_EVENT_SIZE;
            return PlatformDependent.getInt(this.memoryAddress + n + offset);
        }
        return this.memory.getInt(index * EpollEventArray.EPOLL_EVENT_SIZE + offset);
    }
    
    private static int calculateBufferCapacity(final int capacity) {
        return capacity * EpollEventArray.EPOLL_EVENT_SIZE;
    }
    
    static {
        EPOLL_EVENT_SIZE = Native.sizeofEpollEvent();
        EPOLL_DATA_OFFSET = Native.offsetofEpollData();
    }
}
