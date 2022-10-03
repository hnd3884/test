package io.netty.buffer;

import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import java.nio.ByteBuffer;

final class ReadOnlyUnsafeDirectByteBuf extends ReadOnlyByteBufferBuf
{
    private final long memoryAddress;
    
    ReadOnlyUnsafeDirectByteBuf(final ByteBufAllocator allocator, final ByteBuffer byteBuffer) {
        super(allocator, byteBuffer);
        this.memoryAddress = PlatformDependent.directBufferAddress(this.buffer);
    }
    
    @Override
    protected byte _getByte(final int index) {
        return UnsafeByteBufUtil.getByte(this.addr(index));
    }
    
    @Override
    protected short _getShort(final int index) {
        return UnsafeByteBufUtil.getShort(this.addr(index));
    }
    
    @Override
    protected int _getUnsignedMedium(final int index) {
        return UnsafeByteBufUtil.getUnsignedMedium(this.addr(index));
    }
    
    @Override
    protected int _getInt(final int index) {
        return UnsafeByteBufUtil.getInt(this.addr(index));
    }
    
    @Override
    protected long _getLong(final int index) {
        return UnsafeByteBufUtil.getLong(this.addr(index));
    }
    
    @Override
    public ByteBuf getBytes(final int index, final ByteBuf dst, final int dstIndex, final int length) {
        this.checkIndex(index, length);
        ObjectUtil.checkNotNull(dst, "dst");
        if (dstIndex < 0 || dstIndex > dst.capacity() - length) {
            throw new IndexOutOfBoundsException("dstIndex: " + dstIndex);
        }
        if (dst.hasMemoryAddress()) {
            PlatformDependent.copyMemory(this.addr(index), dst.memoryAddress() + dstIndex, length);
        }
        else if (dst.hasArray()) {
            PlatformDependent.copyMemory(this.addr(index), dst.array(), dst.arrayOffset() + dstIndex, length);
        }
        else {
            dst.setBytes(dstIndex, this, index, length);
        }
        return this;
    }
    
    @Override
    public ByteBuf getBytes(final int index, final byte[] dst, final int dstIndex, final int length) {
        this.checkIndex(index, length);
        ObjectUtil.checkNotNull(dst, "dst");
        if (dstIndex < 0 || dstIndex > dst.length - length) {
            throw new IndexOutOfBoundsException(String.format("dstIndex: %d, length: %d (expected: range(0, %d))", dstIndex, length, dst.length));
        }
        if (length != 0) {
            PlatformDependent.copyMemory(this.addr(index), dst, dstIndex, length);
        }
        return this;
    }
    
    @Override
    public ByteBuf copy(final int index, final int length) {
        this.checkIndex(index, length);
        final ByteBuf copy = this.alloc().directBuffer(length, this.maxCapacity());
        if (length != 0) {
            if (copy.hasMemoryAddress()) {
                PlatformDependent.copyMemory(this.addr(index), copy.memoryAddress(), length);
                copy.setIndex(0, length);
            }
            else {
                copy.writeBytes(this, index, length);
            }
        }
        return copy;
    }
    
    @Override
    public boolean hasMemoryAddress() {
        return true;
    }
    
    @Override
    public long memoryAddress() {
        return this.memoryAddress;
    }
    
    private long addr(final int index) {
        return this.memoryAddress + index;
    }
}
