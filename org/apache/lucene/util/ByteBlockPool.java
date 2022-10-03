package org.apache.lucene.util;

import java.util.List;
import java.util.Arrays;

public final class ByteBlockPool
{
    public static final int BYTE_BLOCK_SHIFT = 15;
    public static final int BYTE_BLOCK_SIZE = 32768;
    public static final int BYTE_BLOCK_MASK = 32767;
    public byte[][] buffers;
    private int bufferUpto;
    public int byteUpto;
    public byte[] buffer;
    public int byteOffset;
    private final Allocator allocator;
    public static final int[] NEXT_LEVEL_ARRAY;
    public static final int[] LEVEL_SIZE_ARRAY;
    public static final int FIRST_LEVEL_SIZE;
    
    public ByteBlockPool(final Allocator allocator) {
        this.buffers = new byte[10][];
        this.bufferUpto = -1;
        this.byteUpto = 32768;
        this.byteOffset = -32768;
        this.allocator = allocator;
    }
    
    public void reset() {
        this.reset(true, true);
    }
    
    public void reset(final boolean zeroFillBuffers, final boolean reuseFirst) {
        if (this.bufferUpto != -1) {
            if (zeroFillBuffers) {
                for (int i = 0; i < this.bufferUpto; ++i) {
                    Arrays.fill(this.buffers[i], (byte)0);
                }
                Arrays.fill(this.buffers[this.bufferUpto], 0, this.byteUpto, (byte)0);
            }
            if (this.bufferUpto > 0 || !reuseFirst) {
                final int offset = reuseFirst ? 1 : 0;
                this.allocator.recycleByteBlocks(this.buffers, offset, 1 + this.bufferUpto);
                Arrays.fill(this.buffers, offset, 1 + this.bufferUpto, null);
            }
            if (reuseFirst) {
                this.bufferUpto = 0;
                this.byteUpto = 0;
                this.byteOffset = 0;
                this.buffer = this.buffers[0];
            }
            else {
                this.bufferUpto = -1;
                this.byteUpto = 32768;
                this.byteOffset = -32768;
                this.buffer = null;
            }
        }
    }
    
    public void nextBuffer() {
        if (1 + this.bufferUpto == this.buffers.length) {
            final byte[][] newBuffers = new byte[ArrayUtil.oversize(this.buffers.length + 1, RamUsageEstimator.NUM_BYTES_OBJECT_REF)][];
            System.arraycopy(this.buffers, 0, newBuffers, 0, this.buffers.length);
            this.buffers = newBuffers;
        }
        final byte[][] buffers = this.buffers;
        final int n = 1 + this.bufferUpto;
        final byte[] byteBlock = this.allocator.getByteBlock();
        buffers[n] = byteBlock;
        this.buffer = byteBlock;
        ++this.bufferUpto;
        this.byteUpto = 0;
        this.byteOffset += 32768;
    }
    
    public int newSlice(final int size) {
        if (this.byteUpto > 32768 - size) {
            this.nextBuffer();
        }
        final int upto = this.byteUpto;
        this.byteUpto += size;
        this.buffer[this.byteUpto - 1] = 16;
        return upto;
    }
    
    public int allocSlice(final byte[] slice, final int upto) {
        final int level = slice[upto] & 0xF;
        final int newLevel = ByteBlockPool.NEXT_LEVEL_ARRAY[level];
        final int newSize = ByteBlockPool.LEVEL_SIZE_ARRAY[newLevel];
        if (this.byteUpto > 32768 - newSize) {
            this.nextBuffer();
        }
        final int newUpto = this.byteUpto;
        final int offset = newUpto + this.byteOffset;
        this.byteUpto += newSize;
        this.buffer[newUpto] = slice[upto - 3];
        this.buffer[newUpto + 1] = slice[upto - 2];
        this.buffer[newUpto + 2] = slice[upto - 1];
        slice[upto - 3] = (byte)(offset >>> 24);
        slice[upto - 2] = (byte)(offset >>> 16);
        slice[upto - 1] = (byte)(offset >>> 8);
        slice[upto] = (byte)offset;
        this.buffer[this.byteUpto - 1] = (byte)(0x10 | newLevel);
        return newUpto + 3;
    }
    
    public void setBytesRef(final BytesRef term, final int textStart) {
        final byte[] bytes2 = this.buffers[textStart >> 15];
        term.bytes = bytes2;
        final byte[] bytes = bytes2;
        final int pos = textStart & 0x7FFF;
        if ((bytes[pos] & 0x80) == 0x0) {
            term.length = bytes[pos];
            term.offset = pos + 1;
        }
        else {
            term.length = (bytes[pos] & 0x7F) + ((bytes[pos + 1] & 0xFF) << 7);
            term.offset = pos + 2;
        }
        assert term.length >= 0;
    }
    
    public void append(final BytesRef bytes) {
        int length = bytes.length;
        if (length == 0) {
            return;
        }
        int offset = bytes.offset;
        for (int overflow = length + this.byteUpto - 32768; overflow > 0; overflow -= 32768) {
            final int bytesToCopy = length - overflow;
            if (bytesToCopy > 0) {
                System.arraycopy(bytes.bytes, offset, this.buffer, this.byteUpto, bytesToCopy);
                offset += bytesToCopy;
                length -= bytesToCopy;
            }
            this.nextBuffer();
        }
        System.arraycopy(bytes.bytes, offset, this.buffer, this.byteUpto, length);
        this.byteUpto += length;
    }
    
    public void readBytes(final long offset, final byte[] bytes, final int off, final int length) {
        if (length == 0) {
            return;
        }
        int bytesOffset = off;
        int bytesLength = length;
        int bufferIndex = (int)(offset >> 15);
        byte[] buffer = this.buffers[bufferIndex];
        int pos = (int)(offset & 0x7FFFL);
        for (int overflow = pos + length - 32768; overflow > 0; overflow -= 32768) {
            final int bytesToCopy = length - overflow;
            System.arraycopy(buffer, pos, bytes, bytesOffset, bytesToCopy);
            pos = 0;
            bytesLength -= bytesToCopy;
            bytesOffset += bytesToCopy;
            buffer = this.buffers[++bufferIndex];
        }
        System.arraycopy(buffer, pos, bytes, bytesOffset, bytesLength);
    }
    
    static {
        NEXT_LEVEL_ARRAY = new int[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 9 };
        LEVEL_SIZE_ARRAY = new int[] { 5, 14, 20, 30, 40, 40, 80, 80, 120, 200 };
        FIRST_LEVEL_SIZE = ByteBlockPool.LEVEL_SIZE_ARRAY[0];
    }
    
    public abstract static class Allocator
    {
        protected final int blockSize;
        
        public Allocator(final int blockSize) {
            this.blockSize = blockSize;
        }
        
        public abstract void recycleByteBlocks(final byte[][] p0, final int p1, final int p2);
        
        public void recycleByteBlocks(final List<byte[]> blocks) {
            final byte[][] b = blocks.toArray(new byte[blocks.size()][]);
            this.recycleByteBlocks(b, 0, b.length);
        }
        
        public byte[] getByteBlock() {
            return new byte[this.blockSize];
        }
    }
    
    public static final class DirectAllocator extends Allocator
    {
        public DirectAllocator() {
            this(32768);
        }
        
        public DirectAllocator(final int blockSize) {
            super(blockSize);
        }
        
        @Override
        public void recycleByteBlocks(final byte[][] blocks, final int start, final int end) {
        }
    }
    
    public static class DirectTrackingAllocator extends Allocator
    {
        private final Counter bytesUsed;
        
        public DirectTrackingAllocator(final Counter bytesUsed) {
            this(32768, bytesUsed);
        }
        
        public DirectTrackingAllocator(final int blockSize, final Counter bytesUsed) {
            super(blockSize);
            this.bytesUsed = bytesUsed;
        }
        
        @Override
        public byte[] getByteBlock() {
            this.bytesUsed.addAndGet(this.blockSize);
            return new byte[this.blockSize];
        }
        
        @Override
        public void recycleByteBlocks(final byte[][] blocks, final int start, final int end) {
            this.bytesUsed.addAndGet(-((end - start) * this.blockSize));
            for (int i = start; i < end; ++i) {
                blocks[i] = null;
            }
        }
    }
}
