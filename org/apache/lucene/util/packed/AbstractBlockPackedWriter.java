package org.apache.lucene.util.packed;

import java.util.Arrays;
import java.io.IOException;
import org.apache.lucene.store.DataOutput;

abstract class AbstractBlockPackedWriter
{
    static final int MIN_BLOCK_SIZE = 64;
    static final int MAX_BLOCK_SIZE = 134217728;
    static final int MIN_VALUE_EQUALS_0 = 1;
    static final int BPV_SHIFT = 1;
    protected DataOutput out;
    protected final long[] values;
    protected byte[] blocks;
    protected int off;
    protected long ord;
    protected boolean finished;
    
    static void writeVLong(final DataOutput out, long i) throws IOException {
        for (int k = 0; (i & 0xFFFFFFFFFFFFFF80L) != 0x0L && k++ < 8; i >>>= 7) {
            out.writeByte((byte)((i & 0x7FL) | 0x80L));
        }
        out.writeByte((byte)i);
    }
    
    public AbstractBlockPackedWriter(final DataOutput out, final int blockSize) {
        PackedInts.checkBlockSize(blockSize, 64, 134217728);
        this.reset(out);
        this.values = new long[blockSize];
    }
    
    public void reset(final DataOutput out) {
        assert out != null;
        this.out = out;
        this.off = 0;
        this.ord = 0L;
        this.finished = false;
    }
    
    private void checkNotFinished() {
        if (this.finished) {
            throw new IllegalStateException("Already finished");
        }
    }
    
    public void add(final long l) throws IOException {
        this.checkNotFinished();
        if (this.off == this.values.length) {
            this.flush();
        }
        this.values[this.off++] = l;
        ++this.ord;
    }
    
    void addBlockOfZeros() throws IOException {
        this.checkNotFinished();
        if (this.off != 0 && this.off != this.values.length) {
            throw new IllegalStateException("" + this.off);
        }
        if (this.off == this.values.length) {
            this.flush();
        }
        Arrays.fill(this.values, 0L);
        this.off = this.values.length;
        this.ord += this.values.length;
    }
    
    public void finish() throws IOException {
        this.checkNotFinished();
        if (this.off > 0) {
            this.flush();
        }
        this.finished = true;
    }
    
    public long ord() {
        return this.ord;
    }
    
    protected abstract void flush() throws IOException;
    
    protected final void writeValues(final int bitsRequired) throws IOException {
        final PackedInts.Encoder encoder = PackedInts.getEncoder(PackedInts.Format.PACKED, 2, bitsRequired);
        final int iterations = this.values.length / encoder.byteValueCount();
        final int blockSize = encoder.byteBlockCount() * iterations;
        if (this.blocks == null || this.blocks.length < blockSize) {
            this.blocks = new byte[blockSize];
        }
        if (this.off < this.values.length) {
            Arrays.fill(this.values, this.off, this.values.length, 0L);
        }
        encoder.encode(this.values, 0, this.blocks, 0, iterations);
        final int blockCount = (int)PackedInts.Format.PACKED.byteCount(2, this.off, bitsRequired);
        this.out.writeBytes(this.blocks, blockCount);
    }
}
