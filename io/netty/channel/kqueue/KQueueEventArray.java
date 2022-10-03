package io.netty.channel.kqueue;

import io.netty.util.internal.PlatformDependent;
import io.netty.channel.unix.Buffer;
import java.nio.ByteBuffer;

final class KQueueEventArray
{
    private static final int KQUEUE_EVENT_SIZE;
    private static final int KQUEUE_IDENT_OFFSET;
    private static final int KQUEUE_FILTER_OFFSET;
    private static final int KQUEUE_FFLAGS_OFFSET;
    private static final int KQUEUE_FLAGS_OFFSET;
    private static final int KQUEUE_DATA_OFFSET;
    private ByteBuffer memory;
    private long memoryAddress;
    private int size;
    private int capacity;
    
    KQueueEventArray(final int capacity) {
        if (capacity < 1) {
            throw new IllegalArgumentException("capacity must be >= 1 but was " + capacity);
        }
        this.memory = Buffer.allocateDirectWithNativeOrder(calculateBufferCapacity(capacity));
        this.memoryAddress = Buffer.memoryAddress(this.memory);
        this.capacity = capacity;
    }
    
    long memoryAddress() {
        return this.memoryAddress;
    }
    
    int capacity() {
        return this.capacity;
    }
    
    int size() {
        return this.size;
    }
    
    void clear() {
        this.size = 0;
    }
    
    void evSet(final AbstractKQueueChannel ch, final short filter, final short flags, final int fflags) {
        this.reallocIfNeeded();
        evSet(getKEventOffset(this.size++) + this.memoryAddress, ch.socket.intValue(), filter, flags, fflags);
    }
    
    private void reallocIfNeeded() {
        if (this.size == this.capacity) {
            this.realloc(true);
        }
    }
    
    void realloc(final boolean throwIfFail) {
        final int newLength = (this.capacity <= 65536) ? (this.capacity << 1) : (this.capacity + this.capacity >> 1);
        try {
            final ByteBuffer buffer = Buffer.allocateDirectWithNativeOrder(calculateBufferCapacity(newLength));
            this.memory.position(0).limit(this.size);
            buffer.put(this.memory);
            buffer.position(0);
            Buffer.free(this.memory);
            this.memory = buffer;
            this.memoryAddress = Buffer.memoryAddress(buffer);
        }
        catch (final OutOfMemoryError e) {
            if (throwIfFail) {
                final OutOfMemoryError error = new OutOfMemoryError("unable to allocate " + newLength + " new bytes! Existing capacity is: " + this.capacity);
                error.initCause(e);
                throw error;
            }
        }
    }
    
    void free() {
        Buffer.free(this.memory);
        final int n = 0;
        this.capacity = n;
        this.size = n;
        this.memoryAddress = n;
    }
    
    private static int getKEventOffset(final int index) {
        return index * KQueueEventArray.KQUEUE_EVENT_SIZE;
    }
    
    private long getKEventOffsetAddress(final int index) {
        return getKEventOffset(index) + this.memoryAddress;
    }
    
    private short getShort(final int index, final int offset) {
        if (PlatformDependent.hasUnsafe()) {
            return PlatformDependent.getShort(this.getKEventOffsetAddress(index) + offset);
        }
        return this.memory.getShort(getKEventOffset(index) + offset);
    }
    
    short flags(final int index) {
        return this.getShort(index, KQueueEventArray.KQUEUE_FLAGS_OFFSET);
    }
    
    short filter(final int index) {
        return this.getShort(index, KQueueEventArray.KQUEUE_FILTER_OFFSET);
    }
    
    short fflags(final int index) {
        return this.getShort(index, KQueueEventArray.KQUEUE_FFLAGS_OFFSET);
    }
    
    int fd(final int index) {
        if (PlatformDependent.hasUnsafe()) {
            return PlatformDependent.getInt(this.getKEventOffsetAddress(index) + KQueueEventArray.KQUEUE_IDENT_OFFSET);
        }
        return this.memory.getInt(getKEventOffset(index) + KQueueEventArray.KQUEUE_IDENT_OFFSET);
    }
    
    long data(final int index) {
        if (PlatformDependent.hasUnsafe()) {
            return PlatformDependent.getLong(this.getKEventOffsetAddress(index) + KQueueEventArray.KQUEUE_DATA_OFFSET);
        }
        return this.memory.getLong(getKEventOffset(index) + KQueueEventArray.KQUEUE_DATA_OFFSET);
    }
    
    private static int calculateBufferCapacity(final int capacity) {
        return capacity * KQueueEventArray.KQUEUE_EVENT_SIZE;
    }
    
    private static native void evSet(final long p0, final int p1, final short p2, final short p3, final int p4);
    
    static {
        KQUEUE_EVENT_SIZE = Native.sizeofKEvent();
        KQUEUE_IDENT_OFFSET = Native.offsetofKEventIdent();
        KQUEUE_FILTER_OFFSET = Native.offsetofKEventFilter();
        KQUEUE_FFLAGS_OFFSET = Native.offsetofKEventFFlags();
        KQUEUE_FLAGS_OFFSET = Native.offsetofKEventFlags();
        KQUEUE_DATA_OFFSET = Native.offsetofKeventData();
    }
}
