package org.apache.lucene.util;

import java.util.Arrays;

public final class IntBlockPool
{
    public static final int INT_BLOCK_SHIFT = 13;
    public static final int INT_BLOCK_SIZE = 8192;
    public static final int INT_BLOCK_MASK = 8191;
    public int[][] buffers;
    private int bufferUpto;
    public int intUpto;
    public int[] buffer;
    public int intOffset;
    private final Allocator allocator;
    private static final int[] NEXT_LEVEL_ARRAY;
    private static final int[] LEVEL_SIZE_ARRAY;
    private static final int FIRST_LEVEL_SIZE;
    
    public IntBlockPool() {
        this(new DirectAllocator());
    }
    
    public IntBlockPool(final Allocator allocator) {
        this.buffers = new int[10][];
        this.bufferUpto = -1;
        this.intUpto = 8192;
        this.intOffset = -8192;
        this.allocator = allocator;
    }
    
    public void reset() {
        this.reset(true, true);
    }
    
    public void reset(final boolean zeroFillBuffers, final boolean reuseFirst) {
        if (this.bufferUpto != -1) {
            if (zeroFillBuffers) {
                for (int i = 0; i < this.bufferUpto; ++i) {
                    Arrays.fill(this.buffers[i], 0);
                }
                Arrays.fill(this.buffers[this.bufferUpto], 0, this.intUpto, 0);
            }
            if (this.bufferUpto > 0 || !reuseFirst) {
                final int offset = reuseFirst ? 1 : 0;
                this.allocator.recycleIntBlocks(this.buffers, offset, 1 + this.bufferUpto);
                Arrays.fill(this.buffers, offset, this.bufferUpto + 1, null);
            }
            if (reuseFirst) {
                this.bufferUpto = 0;
                this.intUpto = 0;
                this.intOffset = 0;
                this.buffer = this.buffers[0];
            }
            else {
                this.bufferUpto = -1;
                this.intUpto = 8192;
                this.intOffset = -8192;
                this.buffer = null;
            }
        }
    }
    
    public void nextBuffer() {
        if (1 + this.bufferUpto == this.buffers.length) {
            final int[][] newBuffers = new int[(int)(this.buffers.length * 1.5)][];
            System.arraycopy(this.buffers, 0, newBuffers, 0, this.buffers.length);
            this.buffers = newBuffers;
        }
        final int[][] buffers = this.buffers;
        final int n = 1 + this.bufferUpto;
        final int[] intBlock = this.allocator.getIntBlock();
        buffers[n] = intBlock;
        this.buffer = intBlock;
        ++this.bufferUpto;
        this.intUpto = 0;
        this.intOffset += 8192;
    }
    
    private int newSlice(final int size) {
        if (this.intUpto > 8192 - size) {
            this.nextBuffer();
            assert assertSliceBuffer(this.buffer);
        }
        final int upto = this.intUpto;
        this.intUpto += size;
        this.buffer[this.intUpto - 1] = 1;
        return upto;
    }
    
    private static final boolean assertSliceBuffer(final int[] buffer) {
        int count = 0;
        for (int i = 0; i < buffer.length; ++i) {
            count += buffer[i];
        }
        return count == 0;
    }
    
    private int allocSlice(final int[] slice, final int sliceOffset) {
        final int level = slice[sliceOffset];
        final int newLevel = IntBlockPool.NEXT_LEVEL_ARRAY[level - 1];
        final int newSize = IntBlockPool.LEVEL_SIZE_ARRAY[newLevel];
        if (this.intUpto > 8192 - newSize) {
            this.nextBuffer();
            assert assertSliceBuffer(this.buffer);
        }
        final int newUpto = this.intUpto;
        final int offset = newUpto + this.intOffset;
        this.intUpto += newSize;
        slice[sliceOffset] = offset;
        this.buffer[this.intUpto - 1] = newLevel;
        return newUpto;
    }
    
    static {
        NEXT_LEVEL_ARRAY = new int[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 9 };
        LEVEL_SIZE_ARRAY = new int[] { 2, 4, 8, 16, 32, 64, 128, 256, 512, 1024 };
        FIRST_LEVEL_SIZE = IntBlockPool.LEVEL_SIZE_ARRAY[0];
    }
    
    public abstract static class Allocator
    {
        protected final int blockSize;
        
        public Allocator(final int blockSize) {
            this.blockSize = blockSize;
        }
        
        public abstract void recycleIntBlocks(final int[][] p0, final int p1, final int p2);
        
        public int[] getIntBlock() {
            return new int[this.blockSize];
        }
    }
    
    public static final class DirectAllocator extends Allocator
    {
        public DirectAllocator() {
            super(8192);
        }
        
        @Override
        public void recycleIntBlocks(final int[][] blocks, final int start, final int end) {
        }
    }
    
    public static class SliceWriter
    {
        private int offset;
        private final IntBlockPool pool;
        
        public SliceWriter(final IntBlockPool pool) {
            this.pool = pool;
        }
        
        public void reset(final int sliceOffset) {
            this.offset = sliceOffset;
        }
        
        public void writeInt(final int value) {
            int[] ints = this.pool.buffers[this.offset >> 13];
            assert ints != null;
            int relativeOffset = this.offset & 0x1FFF;
            if (ints[relativeOffset] != 0) {
                relativeOffset = this.pool.allocSlice(ints, relativeOffset);
                ints = this.pool.buffer;
                this.offset = relativeOffset + this.pool.intOffset;
            }
            ints[relativeOffset] = value;
            ++this.offset;
        }
        
        public int startNewSlice() {
            return this.offset = this.pool.newSlice(IntBlockPool.FIRST_LEVEL_SIZE) + this.pool.intOffset;
        }
        
        public int getCurrentOffset() {
            return this.offset;
        }
    }
    
    public static final class SliceReader
    {
        private final IntBlockPool pool;
        private int upto;
        private int bufferUpto;
        private int bufferOffset;
        private int[] buffer;
        private int limit;
        private int level;
        private int end;
        
        public SliceReader(final IntBlockPool pool) {
            this.pool = pool;
        }
        
        public void reset(final int startOffset, final int endOffset) {
            this.bufferUpto = startOffset / 8192;
            this.bufferOffset = this.bufferUpto * 8192;
            this.end = endOffset;
            this.upto = startOffset;
            this.level = 1;
            this.buffer = this.pool.buffers[this.bufferUpto];
            this.upto = (startOffset & 0x1FFF);
            final int firstSize = IntBlockPool.LEVEL_SIZE_ARRAY[0];
            if (startOffset + firstSize >= endOffset) {
                this.limit = (endOffset & 0x1FFF);
            }
            else {
                this.limit = this.upto + firstSize - 1;
            }
        }
        
        public boolean endOfSlice() {
            assert this.upto + this.bufferOffset <= this.end;
            return this.upto + this.bufferOffset == this.end;
        }
        
        public int readInt() {
            assert !this.endOfSlice();
            assert this.upto <= this.limit;
            if (this.upto == this.limit) {
                this.nextSlice();
            }
            return this.buffer[this.upto++];
        }
        
        private void nextSlice() {
            final int nextIndex = this.buffer[this.limit];
            this.level = IntBlockPool.NEXT_LEVEL_ARRAY[this.level - 1];
            final int newSize = IntBlockPool.LEVEL_SIZE_ARRAY[this.level];
            this.bufferUpto = nextIndex / 8192;
            this.bufferOffset = this.bufferUpto * 8192;
            this.buffer = this.pool.buffers[this.bufferUpto];
            this.upto = (nextIndex & 0x1FFF);
            if (nextIndex + newSize >= this.end) {
                assert this.end - nextIndex > 0;
                this.limit = this.end - this.bufferOffset;
            }
            else {
                this.limit = this.upto + newSize - 1;
            }
        }
    }
}
