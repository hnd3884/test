package org.apache.lucene.util.packed;

import java.util.Arrays;
import java.io.IOException;
import java.io.EOFException;
import org.apache.lucene.store.IndexOutput;

public final class DirectWriter
{
    final int bitsPerValue;
    final long numValues;
    final IndexOutput output;
    long count;
    boolean finished;
    int off;
    final byte[] nextBlocks;
    final long[] nextValues;
    final BulkOperation encoder;
    final int iterations;
    static final int[] SUPPORTED_BITS_PER_VALUE;
    
    DirectWriter(final IndexOutput output, final long numValues, final int bitsPerValue) {
        this.output = output;
        this.numValues = numValues;
        this.bitsPerValue = bitsPerValue;
        this.encoder = BulkOperation.of(PackedInts.Format.PACKED, bitsPerValue);
        this.iterations = this.encoder.computeIterations((int)Math.min(numValues, 2147483647L), 1024);
        this.nextBlocks = new byte[this.iterations * this.encoder.byteBlockCount()];
        this.nextValues = new long[this.iterations * this.encoder.byteValueCount()];
    }
    
    public void add(final long l) throws IOException {
        assert l >= 0L && l <= PackedInts.maxValue(this.bitsPerValue) : this.bitsPerValue;
        assert !this.finished;
        if (this.count >= this.numValues) {
            throw new EOFException("Writing past end of stream");
        }
        this.nextValues[this.off++] = l;
        if (this.off == this.nextValues.length) {
            this.flush();
        }
        ++this.count;
    }
    
    private void flush() throws IOException {
        this.encoder.encode(this.nextValues, 0, this.nextBlocks, 0, this.iterations);
        final int blockCount = (int)PackedInts.Format.PACKED.byteCount(2, this.off, this.bitsPerValue);
        this.output.writeBytes(this.nextBlocks, blockCount);
        Arrays.fill(this.nextValues, 0L);
        this.off = 0;
    }
    
    public void finish() throws IOException {
        if (this.count != this.numValues) {
            throw new IllegalStateException("Wrong number of values added, expected: " + this.numValues + ", got: " + this.count);
        }
        assert !this.finished;
        this.flush();
        for (int i = 0; i < 3; ++i) {
            this.output.writeByte((byte)0);
        }
        this.finished = true;
    }
    
    public static DirectWriter getInstance(final IndexOutput output, final long numValues, final int bitsPerValue) {
        if (Arrays.binarySearch(DirectWriter.SUPPORTED_BITS_PER_VALUE, bitsPerValue) < 0) {
            throw new IllegalArgumentException("Unsupported bitsPerValue " + bitsPerValue + ". Did you use bitsRequired?");
        }
        return new DirectWriter(output, numValues, bitsPerValue);
    }
    
    private static int roundBits(final int bitsRequired) {
        final int index = Arrays.binarySearch(DirectWriter.SUPPORTED_BITS_PER_VALUE, bitsRequired);
        if (index < 0) {
            return DirectWriter.SUPPORTED_BITS_PER_VALUE[-index - 1];
        }
        return bitsRequired;
    }
    
    public static int bitsRequired(final long maxValue) {
        return roundBits(PackedInts.bitsRequired(maxValue));
    }
    
    public static int unsignedBitsRequired(final long maxValue) {
        return roundBits(PackedInts.unsignedBitsRequired(maxValue));
    }
    
    static {
        SUPPORTED_BITS_PER_VALUE = new int[] { 1, 2, 4, 8, 12, 16, 20, 24, 28, 32, 40, 48, 56, 64 };
    }
}
