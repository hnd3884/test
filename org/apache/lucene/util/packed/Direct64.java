package org.apache.lucene.util.packed;

import java.util.Arrays;
import org.apache.lucene.util.RamUsageEstimator;
import java.io.IOException;
import org.apache.lucene.store.DataInput;

final class Direct64 extends PackedInts.MutableImpl
{
    final long[] values;
    
    Direct64(final int valueCount) {
        super(valueCount, 64);
        this.values = new long[valueCount];
    }
    
    Direct64(final int packedIntsVersion, final DataInput in, final int valueCount) throws IOException {
        this(valueCount);
        for (int i = 0; i < valueCount; ++i) {
            this.values[i] = in.readLong();
        }
    }
    
    @Override
    public long get(final int index) {
        return this.values[index];
    }
    
    @Override
    public void set(final int index, final long value) {
        this.values[index] = value;
    }
    
    @Override
    public long ramBytesUsed() {
        return RamUsageEstimator.alignObjectSize(RamUsageEstimator.NUM_BYTES_OBJECT_HEADER + 8 + RamUsageEstimator.NUM_BYTES_OBJECT_REF) + RamUsageEstimator.sizeOf(this.values);
    }
    
    @Override
    public void clear() {
        Arrays.fill(this.values, 0L);
    }
    
    @Override
    public int get(final int index, final long[] arr, final int off, final int len) {
        assert len > 0 : "len must be > 0 (got " + len + ")";
        assert index >= 0 && index < this.valueCount;
        assert off + len <= arr.length;
        final int gets = Math.min(this.valueCount - index, len);
        System.arraycopy(this.values, index, arr, off, gets);
        return gets;
    }
    
    @Override
    public int set(final int index, final long[] arr, final int off, final int len) {
        assert len > 0 : "len must be > 0 (got " + len + ")";
        assert index >= 0 && index < this.valueCount;
        assert off + len <= arr.length;
        final int sets = Math.min(this.valueCount - index, len);
        System.arraycopy(arr, off, this.values, index, sets);
        return sets;
    }
    
    @Override
    public void fill(final int fromIndex, final int toIndex, final long val) {
        Arrays.fill(this.values, fromIndex, toIndex, val);
    }
}
