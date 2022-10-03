package org.apache.lucene.util.packed;

import java.io.IOException;
import org.apache.lucene.store.IndexOutput;

public final class DirectMonotonicWriter
{
    public static final int MIN_BLOCK_SHIFT = 2;
    public static final int MAX_BLOCK_SHIFT = 22;
    final IndexOutput meta;
    final IndexOutput data;
    final long numValues;
    final long baseDataPointer;
    final long[] buffer;
    int bufferSize;
    long count;
    boolean finished;
    long previous;
    
    DirectMonotonicWriter(final IndexOutput metaOut, final IndexOutput dataOut, final long numValues, final int blockShift) {
        this.previous = Long.MIN_VALUE;
        this.meta = metaOut;
        this.data = dataOut;
        this.numValues = numValues;
        if (blockShift < 2 || blockShift > 30) {
            throw new IllegalArgumentException("blockShift must be in [3-30], got " + blockShift);
        }
        final int blockSize = 1 << blockShift;
        this.buffer = new long[blockSize];
        this.bufferSize = 0;
        this.baseDataPointer = dataOut.getFilePointer();
    }
    
    private void flush() throws IOException {
        assert this.bufferSize != 0;
        final float avgInc = (float)((this.buffer[this.bufferSize - 1] - this.buffer[0]) / (double)Math.max(1, this.bufferSize - 1));
        for (int i = 0; i < this.bufferSize; ++i) {
            final long expected = (long)(avgInc * i);
            final long[] buffer = this.buffer;
            final int n = i;
            buffer[n] -= expected;
        }
        long min = this.buffer[0];
        for (int j = 1; j < this.bufferSize; ++j) {
            min = Math.min(this.buffer[j], min);
        }
        long maxDelta = 0L;
        for (int k = 0; k < this.bufferSize; ++k) {
            final long[] buffer2 = this.buffer;
            final int n2 = k;
            buffer2[n2] -= min;
            maxDelta |= this.buffer[k];
        }
        this.meta.writeLong(min);
        this.meta.writeInt(Float.floatToIntBits(avgInc));
        this.meta.writeLong(this.data.getFilePointer() - this.baseDataPointer);
        if (maxDelta == 0L) {
            this.meta.writeByte((byte)0);
        }
        else {
            final int bitsRequired = DirectWriter.unsignedBitsRequired(maxDelta);
            final DirectWriter writer = DirectWriter.getInstance(this.data, this.bufferSize, bitsRequired);
            for (int l = 0; l < this.bufferSize; ++l) {
                writer.add(this.buffer[l]);
            }
            writer.finish();
            this.meta.writeByte((byte)bitsRequired);
        }
        this.bufferSize = 0;
    }
    
    public void add(final long v) throws IOException {
        if (v < this.previous) {
            throw new IllegalArgumentException("Values do not come in order: " + this.previous + ", " + v);
        }
        if (this.bufferSize == this.buffer.length) {
            this.flush();
        }
        this.buffer[this.bufferSize++] = v;
        this.previous = v;
        ++this.count;
    }
    
    public void finish() throws IOException {
        if (this.count != this.numValues) {
            throw new IllegalStateException("Wrong number of values added, expected: " + this.numValues + ", got: " + this.count);
        }
        if (this.finished) {
            throw new IllegalStateException("#finish has been called already");
        }
        if (this.bufferSize > 0) {
            this.flush();
        }
        this.finished = true;
    }
    
    public static DirectMonotonicWriter getInstance(final IndexOutput metaOut, final IndexOutput dataOut, final long numValues, final int blockShift) {
        return new DirectMonotonicWriter(metaOut, dataOut, numValues, blockShift);
    }
}
