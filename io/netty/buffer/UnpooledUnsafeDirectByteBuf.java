package io.netty.buffer;

import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import io.netty.util.internal.PlatformDependent;
import java.nio.ByteBuffer;

public class UnpooledUnsafeDirectByteBuf extends UnpooledDirectByteBuf
{
    long memoryAddress;
    
    public UnpooledUnsafeDirectByteBuf(final ByteBufAllocator alloc, final int initialCapacity, final int maxCapacity) {
        super(alloc, initialCapacity, maxCapacity);
    }
    
    protected UnpooledUnsafeDirectByteBuf(final ByteBufAllocator alloc, final ByteBuffer initialBuffer, final int maxCapacity) {
        super(alloc, initialBuffer, maxCapacity, false, true);
    }
    
    UnpooledUnsafeDirectByteBuf(final ByteBufAllocator alloc, final ByteBuffer initialBuffer, final int maxCapacity, final boolean doFree) {
        super(alloc, initialBuffer, maxCapacity, doFree, false);
    }
    
    @Override
    final void setByteBuffer(final ByteBuffer buffer, final boolean tryFree) {
        super.setByteBuffer(buffer, tryFree);
        this.memoryAddress = PlatformDependent.directBufferAddress(buffer);
    }
    
    @Override
    public boolean hasMemoryAddress() {
        return true;
    }
    
    @Override
    public long memoryAddress() {
        this.ensureAccessible();
        return this.memoryAddress;
    }
    
    @Override
    public byte getByte(final int index) {
        this.checkIndex(index);
        return this._getByte(index);
    }
    
    @Override
    protected byte _getByte(final int index) {
        return UnsafeByteBufUtil.getByte(this.addr(index));
    }
    
    @Override
    public short getShort(final int index) {
        this.checkIndex(index, 2);
        return this._getShort(index);
    }
    
    @Override
    protected short _getShort(final int index) {
        return UnsafeByteBufUtil.getShort(this.addr(index));
    }
    
    @Override
    protected short _getShortLE(final int index) {
        return UnsafeByteBufUtil.getShortLE(this.addr(index));
    }
    
    @Override
    public int getUnsignedMedium(final int index) {
        this.checkIndex(index, 3);
        return this._getUnsignedMedium(index);
    }
    
    @Override
    protected int _getUnsignedMedium(final int index) {
        return UnsafeByteBufUtil.getUnsignedMedium(this.addr(index));
    }
    
    @Override
    protected int _getUnsignedMediumLE(final int index) {
        return UnsafeByteBufUtil.getUnsignedMediumLE(this.addr(index));
    }
    
    @Override
    public int getInt(final int index) {
        this.checkIndex(index, 4);
        return this._getInt(index);
    }
    
    @Override
    protected int _getInt(final int index) {
        return UnsafeByteBufUtil.getInt(this.addr(index));
    }
    
    @Override
    protected int _getIntLE(final int index) {
        return UnsafeByteBufUtil.getIntLE(this.addr(index));
    }
    
    @Override
    public long getLong(final int index) {
        this.checkIndex(index, 8);
        return this._getLong(index);
    }
    
    @Override
    protected long _getLong(final int index) {
        return UnsafeByteBufUtil.getLong(this.addr(index));
    }
    
    @Override
    protected long _getLongLE(final int index) {
        return UnsafeByteBufUtil.getLongLE(this.addr(index));
    }
    
    @Override
    public ByteBuf getBytes(final int index, final ByteBuf dst, final int dstIndex, final int length) {
        UnsafeByteBufUtil.getBytes(this, this.addr(index), index, dst, dstIndex, length);
        return this;
    }
    
    @Override
    void getBytes(final int index, final byte[] dst, final int dstIndex, final int length, final boolean internal) {
        UnsafeByteBufUtil.getBytes(this, this.addr(index), index, dst, dstIndex, length);
    }
    
    @Override
    void getBytes(final int index, final ByteBuffer dst, final boolean internal) {
        UnsafeByteBufUtil.getBytes(this, this.addr(index), index, dst);
    }
    
    @Override
    public ByteBuf setByte(final int index, final int value) {
        this.checkIndex(index);
        this._setByte(index, value);
        return this;
    }
    
    @Override
    protected void _setByte(final int index, final int value) {
        UnsafeByteBufUtil.setByte(this.addr(index), value);
    }
    
    @Override
    public ByteBuf setShort(final int index, final int value) {
        this.checkIndex(index, 2);
        this._setShort(index, value);
        return this;
    }
    
    @Override
    protected void _setShort(final int index, final int value) {
        UnsafeByteBufUtil.setShort(this.addr(index), value);
    }
    
    @Override
    protected void _setShortLE(final int index, final int value) {
        UnsafeByteBufUtil.setShortLE(this.addr(index), value);
    }
    
    @Override
    public ByteBuf setMedium(final int index, final int value) {
        this.checkIndex(index, 3);
        this._setMedium(index, value);
        return this;
    }
    
    @Override
    protected void _setMedium(final int index, final int value) {
        UnsafeByteBufUtil.setMedium(this.addr(index), value);
    }
    
    @Override
    protected void _setMediumLE(final int index, final int value) {
        UnsafeByteBufUtil.setMediumLE(this.addr(index), value);
    }
    
    @Override
    public ByteBuf setInt(final int index, final int value) {
        this.checkIndex(index, 4);
        this._setInt(index, value);
        return this;
    }
    
    @Override
    protected void _setInt(final int index, final int value) {
        UnsafeByteBufUtil.setInt(this.addr(index), value);
    }
    
    @Override
    protected void _setIntLE(final int index, final int value) {
        UnsafeByteBufUtil.setIntLE(this.addr(index), value);
    }
    
    @Override
    public ByteBuf setLong(final int index, final long value) {
        this.checkIndex(index, 8);
        this._setLong(index, value);
        return this;
    }
    
    @Override
    protected void _setLong(final int index, final long value) {
        UnsafeByteBufUtil.setLong(this.addr(index), value);
    }
    
    @Override
    protected void _setLongLE(final int index, final long value) {
        UnsafeByteBufUtil.setLongLE(this.addr(index), value);
    }
    
    @Override
    public ByteBuf setBytes(final int index, final ByteBuf src, final int srcIndex, final int length) {
        UnsafeByteBufUtil.setBytes(this, this.addr(index), index, src, srcIndex, length);
        return this;
    }
    
    @Override
    public ByteBuf setBytes(final int index, final byte[] src, final int srcIndex, final int length) {
        UnsafeByteBufUtil.setBytes(this, this.addr(index), index, src, srcIndex, length);
        return this;
    }
    
    @Override
    public ByteBuf setBytes(final int index, final ByteBuffer src) {
        UnsafeByteBufUtil.setBytes(this, this.addr(index), index, src);
        return this;
    }
    
    @Override
    void getBytes(final int index, final OutputStream out, final int length, final boolean internal) throws IOException {
        UnsafeByteBufUtil.getBytes(this, this.addr(index), index, out, length);
    }
    
    @Override
    public int setBytes(final int index, final InputStream in, final int length) throws IOException {
        return UnsafeByteBufUtil.setBytes(this, this.addr(index), index, in, length);
    }
    
    @Override
    public ByteBuf copy(final int index, final int length) {
        return UnsafeByteBufUtil.copy(this, this.addr(index), index, length);
    }
    
    final long addr(final int index) {
        return this.memoryAddress + index;
    }
    
    @Override
    protected SwappedByteBuf newSwappedByteBuf() {
        if (PlatformDependent.isUnaligned()) {
            return new UnsafeDirectSwappedByteBuf(this);
        }
        return super.newSwappedByteBuf();
    }
    
    @Override
    public ByteBuf setZero(final int index, final int length) {
        this.checkIndex(index, length);
        UnsafeByteBufUtil.setZero(this.addr(index), length);
        return this;
    }
    
    @Override
    public ByteBuf writeZero(final int length) {
        this.ensureWritable(length);
        final int wIndex = this.writerIndex;
        UnsafeByteBufUtil.setZero(this.addr(wIndex), length);
        this.writerIndex = wIndex + length;
        return this;
    }
}
