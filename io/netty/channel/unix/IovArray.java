package io.netty.channel.unix;

import io.netty.util.internal.ObjectUtil;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import io.netty.util.internal.PlatformDependent;
import io.netty.buffer.Unpooled;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelOutboundBuffer;

public final class IovArray implements ChannelOutboundBuffer.MessageProcessor
{
    private static final int ADDRESS_SIZE;
    public static final int IOV_SIZE;
    private static final int MAX_CAPACITY;
    private final long memoryAddress;
    private final ByteBuf memory;
    private int count;
    private long size;
    private long maxBytes;
    
    public IovArray() {
        this(Unpooled.wrappedBuffer(Buffer.allocateDirectWithNativeOrder(IovArray.MAX_CAPACITY)).setIndex(0, 0));
    }
    
    public IovArray(final ByteBuf memory) {
        this.maxBytes = Limits.SSIZE_MAX;
        assert memory.writerIndex() == 0;
        assert memory.readerIndex() == 0;
        this.memory = (PlatformDependent.hasUnsafe() ? memory : memory.order(PlatformDependent.BIG_ENDIAN_NATIVE_ORDER ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN));
        if (memory.hasMemoryAddress()) {
            this.memoryAddress = memory.memoryAddress();
        }
        else {
            this.memoryAddress = Buffer.memoryAddress(memory.internalNioBuffer(0, memory.capacity()));
        }
    }
    
    public void clear() {
        this.count = 0;
        this.size = 0L;
    }
    
    @Deprecated
    public boolean add(final ByteBuf buf) {
        return this.add(buf, buf.readerIndex(), buf.readableBytes());
    }
    
    public boolean add(final ByteBuf buf, final int offset, final int len) {
        if (this.count == Limits.IOV_MAX) {
            return false;
        }
        if (buf.nioBufferCount() != 1) {
            final ByteBuffer[] nioBuffers;
            final ByteBuffer[] buffers = nioBuffers = buf.nioBuffers(offset, len);
            for (final ByteBuffer nioBuffer : nioBuffers) {
                final int remaining = nioBuffer.remaining();
                if (remaining != 0 && (!this.add(this.memoryAddress, Buffer.memoryAddress(nioBuffer) + nioBuffer.position(), remaining) || this.count == Limits.IOV_MAX)) {
                    return false;
                }
            }
            return true;
        }
        if (len == 0) {
            return true;
        }
        if (buf.hasMemoryAddress()) {
            return this.add(this.memoryAddress, buf.memoryAddress() + offset, len);
        }
        final ByteBuffer nioBuffer2 = buf.internalNioBuffer(offset, len);
        return this.add(this.memoryAddress, Buffer.memoryAddress(nioBuffer2) + nioBuffer2.position(), len);
    }
    
    private boolean add(final long memoryAddress, final long addr, final int len) {
        assert addr != 0L;
        if ((this.maxBytes - len < this.size && this.count > 0) || this.memory.capacity() < (this.count + 1) * IovArray.IOV_SIZE) {
            return false;
        }
        final int baseOffset = idx(this.count);
        final int lengthOffset = baseOffset + IovArray.ADDRESS_SIZE;
        this.size += len;
        ++this.count;
        if (IovArray.ADDRESS_SIZE == 8) {
            if (PlatformDependent.hasUnsafe()) {
                PlatformDependent.putLong(baseOffset + memoryAddress, addr);
                PlatformDependent.putLong(lengthOffset + memoryAddress, len);
            }
            else {
                this.memory.setLong(baseOffset, addr);
                this.memory.setLong(lengthOffset, len);
            }
        }
        else {
            assert IovArray.ADDRESS_SIZE == 4;
            if (PlatformDependent.hasUnsafe()) {
                PlatformDependent.putInt(baseOffset + memoryAddress, (int)addr);
                PlatformDependent.putInt(lengthOffset + memoryAddress, len);
            }
            else {
                this.memory.setInt(baseOffset, (int)addr);
                this.memory.setInt(lengthOffset, len);
            }
        }
        return true;
    }
    
    public int count() {
        return this.count;
    }
    
    public long size() {
        return this.size;
    }
    
    public void maxBytes(final long maxBytes) {
        this.maxBytes = Math.min(Limits.SSIZE_MAX, ObjectUtil.checkPositive(maxBytes, "maxBytes"));
    }
    
    public long maxBytes() {
        return this.maxBytes;
    }
    
    public long memoryAddress(final int offset) {
        return this.memoryAddress + idx(offset);
    }
    
    public void release() {
        this.memory.release();
    }
    
    @Override
    public boolean processMessage(final Object msg) throws Exception {
        if (msg instanceof ByteBuf) {
            final ByteBuf buffer = (ByteBuf)msg;
            return this.add(buffer, buffer.readerIndex(), buffer.readableBytes());
        }
        return false;
    }
    
    private static int idx(final int index) {
        return IovArray.IOV_SIZE * index;
    }
    
    static {
        ADDRESS_SIZE = Buffer.addressSize();
        IOV_SIZE = 2 * IovArray.ADDRESS_SIZE;
        MAX_CAPACITY = Limits.IOV_MAX * IovArray.IOV_SIZE;
    }
}
