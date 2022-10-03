package org.apache.lucene.util.packed;

import org.apache.lucene.util.ArrayUtil;
import java.util.Arrays;
import org.apache.lucene.util.RamUsageEstimator;
import java.util.Collections;
import java.util.Collection;
import org.apache.lucene.util.Accountable;
import org.apache.lucene.util.LongValues;

public class PackedLongValues extends LongValues implements Accountable
{
    private static final long BASE_RAM_BYTES_USED;
    static final int DEFAULT_PAGE_SIZE = 1024;
    static final int MIN_PAGE_SIZE = 64;
    static final int MAX_PAGE_SIZE = 1048576;
    final PackedInts.Reader[] values;
    final int pageShift;
    final int pageMask;
    private final long size;
    private final long ramBytesUsed;
    
    public static Builder packedBuilder(final int pageSize, final float acceptableOverheadRatio) {
        return new Builder(pageSize, acceptableOverheadRatio);
    }
    
    public static Builder packedBuilder(final float acceptableOverheadRatio) {
        return packedBuilder(1024, acceptableOverheadRatio);
    }
    
    public static Builder deltaPackedBuilder(final int pageSize, final float acceptableOverheadRatio) {
        return new DeltaPackedLongValues.Builder(pageSize, acceptableOverheadRatio);
    }
    
    public static Builder deltaPackedBuilder(final float acceptableOverheadRatio) {
        return deltaPackedBuilder(1024, acceptableOverheadRatio);
    }
    
    public static Builder monotonicBuilder(final int pageSize, final float acceptableOverheadRatio) {
        return new MonotonicLongValues.Builder(pageSize, acceptableOverheadRatio);
    }
    
    public static Builder monotonicBuilder(final float acceptableOverheadRatio) {
        return monotonicBuilder(1024, acceptableOverheadRatio);
    }
    
    PackedLongValues(final int pageShift, final int pageMask, final PackedInts.Reader[] values, final long size, final long ramBytesUsed) {
        this.pageShift = pageShift;
        this.pageMask = pageMask;
        this.values = values;
        this.size = size;
        this.ramBytesUsed = ramBytesUsed;
    }
    
    public final long size() {
        return this.size;
    }
    
    int decodeBlock(final int block, final long[] dest) {
        final PackedInts.Reader vals = this.values[block];
        final int size = vals.size();
        for (int k = 0; k < size; k += vals.get(k, dest, k, size - k)) {}
        return size;
    }
    
    long get(final int block, final int element) {
        return this.values[block].get(element);
    }
    
    @Override
    public final long get(final long index) {
        assert index >= 0L && index < this.size();
        final int block = (int)(index >> this.pageShift);
        final int element = (int)(index & (long)this.pageMask);
        return this.get(block, element);
    }
    
    @Override
    public long ramBytesUsed() {
        return this.ramBytesUsed;
    }
    
    @Override
    public Collection<Accountable> getChildResources() {
        return (Collection<Accountable>)Collections.emptyList();
    }
    
    public Iterator iterator() {
        return new Iterator();
    }
    
    static {
        BASE_RAM_BYTES_USED = RamUsageEstimator.shallowSizeOfInstance(PackedLongValues.class);
    }
    
    public final class Iterator
    {
        final long[] currentValues;
        int vOff;
        int pOff;
        int currentCount;
        
        Iterator() {
            this.currentValues = new long[PackedLongValues.this.pageMask + 1];
            final int n = 0;
            this.pOff = n;
            this.vOff = n;
            this.fillBlock();
        }
        
        private void fillBlock() {
            if (this.vOff == PackedLongValues.this.values.length) {
                this.currentCount = 0;
            }
            else {
                this.currentCount = PackedLongValues.this.decodeBlock(this.vOff, this.currentValues);
                assert this.currentCount > 0;
            }
        }
        
        public final boolean hasNext() {
            return this.pOff < this.currentCount;
        }
        
        public final long next() {
            assert this.hasNext();
            final long result = this.currentValues[this.pOff++];
            if (this.pOff == this.currentCount) {
                ++this.vOff;
                this.pOff = 0;
                this.fillBlock();
            }
            return result;
        }
    }
    
    public static class Builder implements Accountable
    {
        private static final int INITIAL_PAGE_COUNT = 16;
        private static final long BASE_RAM_BYTES_USED;
        final int pageShift;
        final int pageMask;
        final float acceptableOverheadRatio;
        long[] pending;
        long size;
        PackedInts.Reader[] values;
        long ramBytesUsed;
        int valuesOff;
        int pendingOff;
        
        Builder(final int pageSize, final float acceptableOverheadRatio) {
            this.pageShift = PackedInts.checkBlockSize(pageSize, 64, 1048576);
            this.pageMask = pageSize - 1;
            this.acceptableOverheadRatio = acceptableOverheadRatio;
            this.values = new PackedInts.Reader[16];
            this.pending = new long[pageSize];
            this.valuesOff = 0;
            this.pendingOff = 0;
            this.size = 0L;
            this.ramBytesUsed = this.baseRamBytesUsed() + RamUsageEstimator.sizeOf(this.pending) + RamUsageEstimator.shallowSizeOf(this.values);
        }
        
        public PackedLongValues build() {
            this.finish();
            this.pending = null;
            final PackedInts.Reader[] values = Arrays.copyOf(this.values, this.valuesOff);
            final long ramBytesUsed = PackedLongValues.BASE_RAM_BYTES_USED + RamUsageEstimator.sizeOf(values);
            return new PackedLongValues(this.pageShift, this.pageMask, values, this.size, ramBytesUsed);
        }
        
        long baseRamBytesUsed() {
            return Builder.BASE_RAM_BYTES_USED;
        }
        
        @Override
        public final long ramBytesUsed() {
            return this.ramBytesUsed;
        }
        
        @Override
        public Collection<Accountable> getChildResources() {
            return (Collection<Accountable>)Collections.emptyList();
        }
        
        public final long size() {
            return this.size;
        }
        
        public Builder add(final long l) {
            if (this.pending == null) {
                throw new IllegalStateException("Cannot be reused after build()");
            }
            if (this.pendingOff == this.pending.length) {
                if (this.values.length == this.valuesOff) {
                    final int newLength = ArrayUtil.oversize(this.valuesOff + 1, 8);
                    this.grow(newLength);
                }
                this.pack();
            }
            this.pending[this.pendingOff++] = l;
            ++this.size;
            return this;
        }
        
        final void finish() {
            if (this.pendingOff > 0) {
                if (this.values.length == this.valuesOff) {
                    this.grow(this.valuesOff + 1);
                }
                this.pack();
            }
        }
        
        private void pack() {
            this.pack(this.pending, this.pendingOff, this.valuesOff, this.acceptableOverheadRatio);
            this.ramBytesUsed += this.values[this.valuesOff].ramBytesUsed();
            ++this.valuesOff;
            this.pendingOff = 0;
        }
        
        void pack(final long[] values, final int numValues, final int block, final float acceptableOverheadRatio) {
            assert numValues > 0;
            long minValue = values[0];
            long maxValue = values[0];
            for (int i = 1; i < numValues; ++i) {
                minValue = Math.min(minValue, values[i]);
                maxValue = Math.max(maxValue, values[i]);
            }
            if (minValue == 0L && maxValue == 0L) {
                this.values[block] = new PackedInts.NullReader(numValues);
            }
            else {
                final int bitsRequired = (minValue < 0L) ? 64 : PackedInts.bitsRequired(maxValue);
                final PackedInts.Mutable mutable = PackedInts.getMutable(numValues, bitsRequired, acceptableOverheadRatio);
                for (int j = 0; j < numValues; j += mutable.set(j, values, j, numValues - j)) {}
                this.values[block] = mutable;
            }
        }
        
        void grow(final int newBlockCount) {
            this.ramBytesUsed -= RamUsageEstimator.shallowSizeOf(this.values);
            this.values = Arrays.copyOf(this.values, newBlockCount);
            this.ramBytesUsed += RamUsageEstimator.shallowSizeOf(this.values);
        }
        
        static {
            BASE_RAM_BYTES_USED = RamUsageEstimator.shallowSizeOfInstance(Builder.class);
        }
    }
}
