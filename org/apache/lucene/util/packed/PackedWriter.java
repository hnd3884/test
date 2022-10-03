package org.apache.lucene.util.packed;

import java.util.Arrays;
import java.io.IOException;
import java.io.EOFException;
import org.apache.lucene.store.DataOutput;

final class PackedWriter extends PackedInts.Writer
{
    boolean finished;
    final PackedInts.Format format;
    final BulkOperation encoder;
    final byte[] nextBlocks;
    final long[] nextValues;
    final int iterations;
    int off;
    int written;
    
    PackedWriter(final PackedInts.Format format, final DataOutput out, final int valueCount, final int bitsPerValue, final int mem) {
        super(out, valueCount, bitsPerValue);
        this.format = format;
        this.encoder = BulkOperation.of(format, bitsPerValue);
        this.iterations = this.encoder.computeIterations(valueCount, mem);
        this.nextBlocks = new byte[this.iterations * this.encoder.byteBlockCount()];
        this.nextValues = new long[this.iterations * this.encoder.byteValueCount()];
        this.off = 0;
        this.written = 0;
        this.finished = false;
    }
    
    @Override
    protected PackedInts.Format getFormat() {
        return this.format;
    }
    
    @Override
    public void add(final long v) throws IOException {
        assert PackedInts.unsignedBitsRequired(v) <= this.bitsPerValue;
        assert !this.finished;
        if (this.valueCount != -1 && this.written >= this.valueCount) {
            throw new EOFException("Writing past end of stream");
        }
        this.nextValues[this.off++] = v;
        if (this.off == this.nextValues.length) {
            this.flush();
        }
        ++this.written;
    }
    
    @Override
    public void finish() throws IOException {
        assert !this.finished;
        if (this.valueCount != -1) {
            while (this.written < this.valueCount) {
                this.add(0L);
            }
        }
        this.flush();
        this.finished = true;
    }
    
    private void flush() throws IOException {
        this.encoder.encode(this.nextValues, 0, this.nextBlocks, 0, this.iterations);
        final int blockCount = (int)this.format.byteCount(2, this.off, this.bitsPerValue);
        this.out.writeBytes(this.nextBlocks, blockCount);
        Arrays.fill(this.nextValues, 0L);
        this.off = 0;
    }
    
    @Override
    public int ord() {
        return this.written - 1;
    }
}
