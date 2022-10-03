package io.netty.buffer;

import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.ObjectPool;
import java.nio.ByteBuffer;

final class PooledUnsafeDirectByteBuf extends PooledByteBuf<ByteBuffer>
{
    private static final ObjectPool<PooledUnsafeDirectByteBuf> RECYCLER;
    private long memoryAddress;
    
    static PooledUnsafeDirectByteBuf newInstance(final int maxCapacity) {
        final PooledUnsafeDirectByteBuf buf = PooledUnsafeDirectByteBuf.RECYCLER.get();
        buf.reuse(maxCapacity);
        return buf;
    }
    
    private PooledUnsafeDirectByteBuf(final ObjectPool.Handle<PooledUnsafeDirectByteBuf> recyclerHandle, final int maxCapacity) {
        super(recyclerHandle, maxCapacity);
    }
    
    @Override
    void init(final PoolChunk<ByteBuffer> chunk, final ByteBuffer nioBuffer, final long handle, final int offset, final int length, final int maxLength, final PoolThreadCache cache) {
        super.init(chunk, nioBuffer, handle, offset, length, maxLength, cache);
        this.initMemoryAddress();
    }
    
    @Override
    void initUnpooled(final PoolChunk<ByteBuffer> chunk, final int length) {
        super.initUnpooled(chunk, length);
        this.initMemoryAddress();
    }
    
    private void initMemoryAddress() {
        this.memoryAddress = PlatformDependent.directBufferAddress((ByteBuffer)this.memory) + this.offset;
    }
    
    @Override
    protected ByteBuffer newInternalNioBuffer(final ByteBuffer memory) {
        return memory.duplicate();
    }
    
    @Override
    public boolean isDirect() {
        return true;
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
    protected short _getShortLE(final int index) {
        return UnsafeByteBufUtil.getShortLE(this.addr(index));
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
    protected int _getInt(final int index) {
        return UnsafeByteBufUtil.getInt(this.addr(index));
    }
    
    @Override
    protected int _getIntLE(final int index) {
        return UnsafeByteBufUtil.getIntLE(this.addr(index));
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
    public ByteBuf getBytes(final int index, final byte[] dst, final int dstIndex, final int length) {
        UnsafeByteBufUtil.getBytes(this, this.addr(index), index, dst, dstIndex, length);
        return this;
    }
    
    @Override
    public ByteBuf getBytes(final int index, final ByteBuffer dst) {
        UnsafeByteBufUtil.getBytes(this, this.addr(index), index, dst);
        return this;
    }
    
    @Override
    public ByteBuf getBytes(final int index, final OutputStream out, final int length) throws IOException {
        UnsafeByteBufUtil.getBytes(this, this.addr(index), index, out, length);
        return this;
    }
    
    @Override
    protected void _setByte(final int index, final int value) {
        UnsafeByteBufUtil.setByte(this.addr(index), (byte)value);
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
    protected void _setMedium(final int index, final int value) {
        UnsafeByteBufUtil.setMedium(this.addr(index), value);
    }
    
    @Override
    protected void _setMediumLE(final int index, final int value) {
        UnsafeByteBufUtil.setMediumLE(this.addr(index), value);
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
    public int setBytes(final int index, final InputStream in, final int length) throws IOException {
        return UnsafeByteBufUtil.setBytes(this, this.addr(index), index, in, length);
    }
    
    @Override
    public ByteBuf copy(final int index, final int length) {
        return UnsafeByteBufUtil.copy(this, this.addr(index), index, length);
    }
    
    @Override
    public boolean hasArray() {
        return false;
    }
    
    @Override
    public byte[] array() {
        throw new UnsupportedOperationException("direct buffer");
    }
    
    @Override
    public int arrayOffset() {
        throw new UnsupportedOperationException("direct buffer");
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
    
    private long addr(final int index) {
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
    
    static {
        RECYCLER = ObjectPool.newPool((ObjectPool.ObjectCreator<PooledUnsafeDirectByteBuf>)new ObjectPool.ObjectCreator<PooledUnsafeDirectByteBuf>() {
            @Override
            public PooledUnsafeDirectByteBuf newObject(final ObjectPool.Handle<PooledUnsafeDirectByteBuf> handle) {
                return new PooledUnsafeDirectByteBuf(handle, 0, null);
            }
        });
    }
}
