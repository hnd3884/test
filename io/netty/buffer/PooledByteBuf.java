package io.netty.buffer;

import java.nio.channels.ClosedChannelException;
import java.nio.channels.ScatteringByteChannel;
import java.nio.channels.FileChannel;
import java.io.IOException;
import java.nio.channels.GatheringByteChannel;
import java.nio.ByteOrder;
import java.nio.ByteBuffer;
import io.netty.util.internal.ObjectPool;

abstract class PooledByteBuf<T> extends AbstractReferenceCountedByteBuf
{
    private final ObjectPool.Handle<PooledByteBuf<T>> recyclerHandle;
    protected PoolChunk<T> chunk;
    protected long handle;
    protected T memory;
    protected int offset;
    protected int length;
    int maxLength;
    PoolThreadCache cache;
    ByteBuffer tmpNioBuf;
    private ByteBufAllocator allocator;
    
    protected PooledByteBuf(final ObjectPool.Handle<? extends PooledByteBuf<T>> recyclerHandle, final int maxCapacity) {
        super(maxCapacity);
        this.recyclerHandle = (ObjectPool.Handle<PooledByteBuf<T>>)recyclerHandle;
    }
    
    void init(final PoolChunk<T> chunk, final ByteBuffer nioBuffer, final long handle, final int offset, final int length, final int maxLength, final PoolThreadCache cache) {
        this.init0(chunk, nioBuffer, handle, offset, length, maxLength, cache);
    }
    
    void initUnpooled(final PoolChunk<T> chunk, final int length) {
        this.init0(chunk, null, 0L, 0, length, length, null);
    }
    
    private void init0(final PoolChunk<T> chunk, final ByteBuffer nioBuffer, final long handle, final int offset, final int length, final int maxLength, final PoolThreadCache cache) {
        assert handle >= 0L;
        assert chunk != null;
        this.chunk = chunk;
        this.memory = chunk.memory;
        this.tmpNioBuf = nioBuffer;
        this.allocator = chunk.arena.parent;
        this.cache = cache;
        this.handle = handle;
        this.offset = offset;
        this.length = length;
        this.maxLength = maxLength;
    }
    
    final void reuse(final int maxCapacity) {
        this.maxCapacity(maxCapacity);
        this.resetRefCnt();
        this.setIndex0(0, 0);
        this.discardMarks();
    }
    
    @Override
    public final int capacity() {
        return this.length;
    }
    
    @Override
    public int maxFastWritableBytes() {
        return Math.min(this.maxLength, this.maxCapacity()) - this.writerIndex;
    }
    
    @Override
    public final ByteBuf capacity(final int newCapacity) {
        if (newCapacity == this.length) {
            this.ensureAccessible();
            return this;
        }
        this.checkNewCapacity(newCapacity);
        if (!this.chunk.unpooled) {
            if (newCapacity > this.length) {
                if (newCapacity <= this.maxLength) {
                    this.length = newCapacity;
                    return this;
                }
            }
            else if (newCapacity > this.maxLength >>> 1 && (this.maxLength > 512 || newCapacity > this.maxLength - 16)) {
                this.trimIndicesToCapacity(this.length = newCapacity);
                return this;
            }
        }
        this.chunk.arena.reallocate(this, newCapacity, true);
        return this;
    }
    
    @Override
    public final ByteBufAllocator alloc() {
        return this.allocator;
    }
    
    @Override
    public final ByteOrder order() {
        return ByteOrder.BIG_ENDIAN;
    }
    
    @Override
    public final ByteBuf unwrap() {
        return null;
    }
    
    @Override
    public final ByteBuf retainedDuplicate() {
        return PooledDuplicatedByteBuf.newInstance(this, this, this.readerIndex(), this.writerIndex());
    }
    
    @Override
    public final ByteBuf retainedSlice() {
        final int index = this.readerIndex();
        return this.retainedSlice(index, this.writerIndex() - index);
    }
    
    @Override
    public final ByteBuf retainedSlice(final int index, final int length) {
        return PooledSlicedByteBuf.newInstance(this, this, index, length);
    }
    
    protected final ByteBuffer internalNioBuffer() {
        ByteBuffer tmpNioBuf = this.tmpNioBuf;
        if (tmpNioBuf == null) {
            tmpNioBuf = (this.tmpNioBuf = this.newInternalNioBuffer(this.memory));
        }
        else {
            tmpNioBuf.clear();
        }
        return tmpNioBuf;
    }
    
    protected abstract ByteBuffer newInternalNioBuffer(final T p0);
    
    @Override
    protected final void deallocate() {
        if (this.handle >= 0L) {
            final long handle = this.handle;
            this.handle = -1L;
            this.memory = null;
            this.chunk.arena.free(this.chunk, this.tmpNioBuf, handle, this.maxLength, this.cache);
            this.tmpNioBuf = null;
            this.chunk = null;
            this.recycle();
        }
    }
    
    private void recycle() {
        this.recyclerHandle.recycle(this);
    }
    
    protected final int idx(final int index) {
        return this.offset + index;
    }
    
    final ByteBuffer _internalNioBuffer(int index, final int length, final boolean duplicate) {
        index = this.idx(index);
        final ByteBuffer buffer = duplicate ? this.newInternalNioBuffer(this.memory) : this.internalNioBuffer();
        buffer.limit(index + length).position(index);
        return buffer;
    }
    
    ByteBuffer duplicateInternalNioBuffer(final int index, final int length) {
        this.checkIndex(index, length);
        return this._internalNioBuffer(index, length, true);
    }
    
    @Override
    public final ByteBuffer internalNioBuffer(final int index, final int length) {
        this.checkIndex(index, length);
        return this._internalNioBuffer(index, length, false);
    }
    
    @Override
    public final int nioBufferCount() {
        return 1;
    }
    
    @Override
    public final ByteBuffer nioBuffer(final int index, final int length) {
        return this.duplicateInternalNioBuffer(index, length).slice();
    }
    
    @Override
    public final ByteBuffer[] nioBuffers(final int index, final int length) {
        return new ByteBuffer[] { this.nioBuffer(index, length) };
    }
    
    @Override
    public final boolean isContiguous() {
        return true;
    }
    
    @Override
    public final int getBytes(final int index, final GatheringByteChannel out, final int length) throws IOException {
        return out.write(this.duplicateInternalNioBuffer(index, length));
    }
    
    @Override
    public final int readBytes(final GatheringByteChannel out, final int length) throws IOException {
        this.checkReadableBytes(length);
        final int readBytes = out.write(this._internalNioBuffer(this.readerIndex, length, false));
        this.readerIndex += readBytes;
        return readBytes;
    }
    
    @Override
    public final int getBytes(final int index, final FileChannel out, final long position, final int length) throws IOException {
        return out.write(this.duplicateInternalNioBuffer(index, length), position);
    }
    
    @Override
    public final int readBytes(final FileChannel out, final long position, final int length) throws IOException {
        this.checkReadableBytes(length);
        final int readBytes = out.write(this._internalNioBuffer(this.readerIndex, length, false), position);
        this.readerIndex += readBytes;
        return readBytes;
    }
    
    @Override
    public final int setBytes(final int index, final ScatteringByteChannel in, final int length) throws IOException {
        try {
            return in.read(this.internalNioBuffer(index, length));
        }
        catch (final ClosedChannelException ignored) {
            return -1;
        }
    }
    
    @Override
    public final int setBytes(final int index, final FileChannel in, final long position, final int length) throws IOException {
        try {
            return in.read(this.internalNioBuffer(index, length), position);
        }
        catch (final ClosedChannelException ignored) {
            return -1;
        }
    }
}
