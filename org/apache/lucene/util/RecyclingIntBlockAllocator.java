package org.apache.lucene.util;

public final class RecyclingIntBlockAllocator extends IntBlockPool.Allocator
{
    private int[][] freeByteBlocks;
    private final int maxBufferedBlocks;
    private int freeBlocks;
    private final Counter bytesUsed;
    public static final int DEFAULT_BUFFERED_BLOCKS = 64;
    
    public RecyclingIntBlockAllocator(final int blockSize, final int maxBufferedBlocks, final Counter bytesUsed) {
        super(blockSize);
        this.freeBlocks = 0;
        this.freeByteBlocks = new int[maxBufferedBlocks][];
        this.maxBufferedBlocks = maxBufferedBlocks;
        this.bytesUsed = bytesUsed;
    }
    
    public RecyclingIntBlockAllocator(final int blockSize, final int maxBufferedBlocks) {
        this(blockSize, maxBufferedBlocks, Counter.newCounter(false));
    }
    
    public RecyclingIntBlockAllocator() {
        this(8192, 64, Counter.newCounter(false));
    }
    
    @Override
    public int[] getIntBlock() {
        if (this.freeBlocks == 0) {
            this.bytesUsed.addAndGet(this.blockSize * 4);
            return new int[this.blockSize];
        }
        final int[][] freeByteBlocks = this.freeByteBlocks;
        final int freeBlocks = this.freeBlocks - 1;
        this.freeBlocks = freeBlocks;
        final int[] b = freeByteBlocks[freeBlocks];
        this.freeByteBlocks[this.freeBlocks] = null;
        return b;
    }
    
    @Override
    public void recycleIntBlocks(final int[][] blocks, final int start, final int end) {
        final int numBlocks = Math.min(this.maxBufferedBlocks - this.freeBlocks, end - start);
        final int size = this.freeBlocks + numBlocks;
        if (size >= this.freeByteBlocks.length) {
            final int[][] newBlocks = new int[ArrayUtil.oversize(size, RamUsageEstimator.NUM_BYTES_OBJECT_REF)][];
            System.arraycopy(this.freeByteBlocks, 0, newBlocks, 0, this.freeBlocks);
            this.freeByteBlocks = newBlocks;
        }
        final int stop = start + numBlocks;
        for (int i = start; i < stop; ++i) {
            this.freeByteBlocks[this.freeBlocks++] = blocks[i];
            blocks[i] = null;
        }
        for (int i = stop; i < end; ++i) {
            blocks[i] = null;
        }
        this.bytesUsed.addAndGet(-(end - stop) * (this.blockSize * 4));
        assert this.bytesUsed.get() >= 0L;
    }
    
    public int numBufferedBlocks() {
        return this.freeBlocks;
    }
    
    public long bytesUsed() {
        return this.bytesUsed.get();
    }
    
    public int maxBufferedBlocks() {
        return this.maxBufferedBlocks;
    }
    
    public int freeBlocks(final int num) {
        assert num >= 0 : "free blocks must be >= 0 but was: " + num;
        int stop;
        int count;
        if (num > this.freeBlocks) {
            stop = 0;
            count = this.freeBlocks;
        }
        else {
            stop = this.freeBlocks - num;
            count = num;
        }
        while (this.freeBlocks > stop) {
            this.freeByteBlocks[--this.freeBlocks] = null;
        }
        this.bytesUsed.addAndGet(-count * this.blockSize * 4);
        assert this.bytesUsed.get() >= 0L;
        return count;
    }
}
