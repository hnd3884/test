package org.apache.lucene.util.packed;

import java.io.IOException;
import org.apache.lucene.store.DataOutput;
import org.apache.lucene.util.RamUsageEstimator;

public class GrowableWriter extends PackedInts.Mutable
{
    private long currentMask;
    private PackedInts.Mutable current;
    private final float acceptableOverheadRatio;
    
    public GrowableWriter(final int startBitsPerValue, final int valueCount, final float acceptableOverheadRatio) {
        this.acceptableOverheadRatio = acceptableOverheadRatio;
        this.current = PackedInts.getMutable(valueCount, startBitsPerValue, this.acceptableOverheadRatio);
        this.currentMask = mask(this.current.getBitsPerValue());
    }
    
    private static long mask(final int bitsPerValue) {
        return (bitsPerValue == 64) ? -1L : PackedInts.maxValue(bitsPerValue);
    }
    
    @Override
    public long get(final int index) {
        return this.current.get(index);
    }
    
    @Override
    public int size() {
        return this.current.size();
    }
    
    @Override
    public int getBitsPerValue() {
        return this.current.getBitsPerValue();
    }
    
    public PackedInts.Mutable getMutable() {
        return this.current;
    }
    
    private void ensureCapacity(final long value) {
        if ((value & this.currentMask) == value) {
            return;
        }
        final int bitsRequired = PackedInts.unsignedBitsRequired(value);
        assert bitsRequired > this.current.getBitsPerValue();
        final int valueCount = this.size();
        final PackedInts.Mutable next = PackedInts.getMutable(valueCount, bitsRequired, this.acceptableOverheadRatio);
        PackedInts.copy(this.current, 0, next, 0, valueCount, 1024);
        this.current = next;
        this.currentMask = mask(this.current.getBitsPerValue());
    }
    
    @Override
    public void set(final int index, final long value) {
        this.ensureCapacity(value);
        this.current.set(index, value);
    }
    
    @Override
    public void clear() {
        this.current.clear();
    }
    
    public GrowableWriter resize(final int newSize) {
        final GrowableWriter next = new GrowableWriter(this.getBitsPerValue(), newSize, this.acceptableOverheadRatio);
        final int limit = Math.min(this.size(), newSize);
        PackedInts.copy(this.current, 0, next, 0, limit, 1024);
        return next;
    }
    
    @Override
    public int get(final int index, final long[] arr, final int off, final int len) {
        return this.current.get(index, arr, off, len);
    }
    
    @Override
    public int set(final int index, final long[] arr, final int off, final int len) {
        long max = 0L;
        for (int i = off, end = off + len; i < end; ++i) {
            max |= arr[i];
        }
        this.ensureCapacity(max);
        return this.current.set(index, arr, off, len);
    }
    
    @Override
    public void fill(final int fromIndex, final int toIndex, final long val) {
        this.ensureCapacity(val);
        this.current.fill(fromIndex, toIndex, val);
    }
    
    @Override
    public long ramBytesUsed() {
        return RamUsageEstimator.alignObjectSize(RamUsageEstimator.NUM_BYTES_OBJECT_HEADER + RamUsageEstimator.NUM_BYTES_OBJECT_REF + 8 + 4) + this.current.ramBytesUsed();
    }
    
    @Override
    public void save(final DataOutput out) throws IOException {
        this.current.save(out);
    }
}
