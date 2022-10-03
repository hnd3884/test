package org.apache.lucene.util.packed;

import java.util.Arrays;
import org.apache.lucene.util.RamUsageEstimator;
import java.io.IOException;
import org.apache.lucene.store.DataInput;

final class Direct8 extends PackedInts.MutableImpl
{
    final byte[] values;
    
    Direct8(final int valueCount) {
        super(valueCount, 8);
        this.values = new byte[valueCount];
    }
    
    Direct8(final int packedIntsVersion, final DataInput in, final int valueCount) throws IOException {
        this(valueCount);
        in.readBytes(this.values, 0, valueCount);
        for (int remaining = (int)(PackedInts.Format.PACKED.byteCount(packedIntsVersion, valueCount, 8) - 1L * valueCount), i = 0; i < remaining; ++i) {
            in.readByte();
        }
    }
    
    @Override
    public long get(final int index) {
        return (long)this.values[index] & 0xFFL;
    }
    
    @Override
    public void set(final int index, final long value) {
        this.values[index] = (byte)value;
    }
    
    @Override
    public long ramBytesUsed() {
        return RamUsageEstimator.alignObjectSize(RamUsageEstimator.NUM_BYTES_OBJECT_HEADER + 8 + RamUsageEstimator.NUM_BYTES_OBJECT_REF) + RamUsageEstimator.sizeOf(this.values);
    }
    
    @Override
    public void clear() {
        Arrays.fill(this.values, (byte)0);
    }
    
    @Override
    public int get(final int index, final long[] arr, final int off, final int len) {
        assert len > 0 : "len must be > 0 (got " + len + ")";
        assert index >= 0 && index < this.valueCount;
        assert off + len <= arr.length;
        final int gets = Math.min(this.valueCount - index, len);
        for (int i = index, o = off, end = index + gets; i < end; ++i, ++o) {
            arr[o] = ((long)this.values[i] & 0xFFL);
        }
        return gets;
    }
    
    @Override
    public int set(final int index, final long[] arr, final int off, final int len) {
        assert len > 0 : "len must be > 0 (got " + len + ")";
        assert index >= 0 && index < this.valueCount;
        assert off + len <= arr.length;
        final int sets = Math.min(this.valueCount - index, len);
        for (int i = index, o = off, end = index + sets; i < end; ++i, ++o) {
            this.values[i] = (byte)arr[o];
        }
        return sets;
    }
    
    @Override
    public void fill(final int fromIndex, final int toIndex, final long val) {
        assert val == (val & 0xFFL);
        Arrays.fill(this.values, fromIndex, toIndex, (byte)val);
    }
}
