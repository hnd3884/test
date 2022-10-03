package org.apache.lucene.util;

public final class RecyclingByteBlockAllocator extends ByteBlockPool.Allocator
{
    private byte[][] freeByteBlocks;
    private final int maxBufferedBlocks;
    private int freeBlocks;
    private final Counter bytesUsed;
    public static final int DEFAULT_BUFFERED_BLOCKS = 64;
    
    public RecyclingByteBlockAllocator(final int blockSize, final int maxBufferedBlocks, final Counter bytesUsed) {
        super(blockSize);
        this.freeBlocks = 0;
        this.freeByteBlocks = new byte[maxBufferedBlocks][];
        this.maxBufferedBlocks = maxBufferedBlocks;
        this.bytesUsed = bytesUsed;
    }
    
    public RecyclingByteBlockAllocator(final int blockSize, final int maxBufferedBlocks) {
        this(blockSize, maxBufferedBlocks, Counter.newCounter(false));
    }
    
    public RecyclingByteBlockAllocator() {
        this(32768, 64, Counter.newCounter(false));
    }
    
    @Override
    public byte[] getByteBlock() {
        if (this.freeBlocks == 0) {
            this.bytesUsed.addAndGet(this.blockSize);
            return new byte[this.blockSize];
        }
        final byte[][] freeByteBlocks = this.freeByteBlocks;
        final int freeBlocks = this.freeBlocks - 1;
        this.freeBlocks = freeBlocks;
        final byte[] b = freeByteBlocks[freeBlocks];
        this.freeByteBlocks[this.freeBlocks] = null;
        return b;
    }
    
    @Override
    public void recycleByteBlocks(final byte[][] blocks, final int start, final int end) {
        final int numBlocks = Math.min(this.maxBufferedBlocks - this.freeBlocks, end - start);
        final int size = this.freeBlocks + numBlocks;
        if (size >= this.freeByteBlocks.length) {
            final byte[][] newBlocks = new byte[ArrayUtil.oversize(size, RamUsageEstimator.NUM_BYTES_OBJECT_REF)][];
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
        this.bytesUsed.addAndGet(-(end - stop) * this.blockSize);
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
        this.bytesUsed.addAndGet(-count * this.blockSize);
        assert this.bytesUsed.get() >= 0L;
        return count;
    }
}
