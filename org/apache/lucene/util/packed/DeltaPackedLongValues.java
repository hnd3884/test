package org.apache.lucene.util.packed;

import org.apache.lucene.util.Accountable;
import java.util.Arrays;
import org.apache.lucene.util.RamUsageEstimator;

class DeltaPackedLongValues extends PackedLongValues
{
    private static final long BASE_RAM_BYTES_USED;
    final long[] mins;
    
    DeltaPackedLongValues(final int pageShift, final int pageMask, final PackedInts.Reader[] values, final long[] mins, final long size, final long ramBytesUsed) {
        super(pageShift, pageMask, values, size, ramBytesUsed);
        assert values.length == mins.length;
        this.mins = mins;
    }
    
    @Override
    long get(final int block, final int element) {
        return this.mins[block] + this.values[block].get(element);
    }
    
    @Override
    int decodeBlock(final int block, final long[] dest) {
        final int count = super.decodeBlock(block, dest);
        final long min = this.mins[block];
        for (int i = 0; i < count; ++i) {
            final int n = i;
            dest[n] += min;
        }
        return count;
    }
    
    static {
        BASE_RAM_BYTES_USED = RamUsageEstimator.shallowSizeOfInstance(DeltaPackedLongValues.class);
    }
    
    static class Builder extends PackedLongValues.Builder
    {
        private static final long BASE_RAM_BYTES_USED;
        long[] mins;
        
        Builder(final int pageSize, final float acceptableOverheadRatio) {
            super(pageSize, acceptableOverheadRatio);
            this.mins = new long[this.values.length];
            this.ramBytesUsed += RamUsageEstimator.sizeOf(this.mins);
        }
        
        @Override
        long baseRamBytesUsed() {
            return Builder.BASE_RAM_BYTES_USED;
        }
        
        @Override
        public DeltaPackedLongValues build() {
            this.finish();
            this.pending = null;
            final PackedInts.Reader[] values = Arrays.copyOf(this.values, this.valuesOff);
            final long[] mins = Arrays.copyOf(this.mins, this.valuesOff);
            final long ramBytesUsed = DeltaPackedLongValues.BASE_RAM_BYTES_USED + RamUsageEstimator.sizeOf(values) + RamUsageEstimator.sizeOf(mins);
            return new DeltaPackedLongValues(this.pageShift, this.pageMask, values, mins, this.size, ramBytesUsed);
        }
        
        @Override
        void pack(final long[] values, final int numValues, final int block, final float acceptableOverheadRatio) {
            long min = values[0];
            for (int i = 1; i < numValues; ++i) {
                min = Math.min(min, values[i]);
            }
            for (int i = 0; i < numValues; ++i) {
                final int n = i;
                values[n] -= min;
            }
            super.pack(values, numValues, block, acceptableOverheadRatio);
            this.mins[block] = min;
        }
        
        @Override
        void grow(final int newBlockCount) {
            super.grow(newBlockCount);
            this.ramBytesUsed -= RamUsageEstimator.sizeOf(this.mins);
            this.mins = Arrays.copyOf(this.mins, newBlockCount);
            this.ramBytesUsed += RamUsageEstimator.sizeOf(this.mins);
        }
        
        static {
            BASE_RAM_BYTES_USED = RamUsageEstimator.shallowSizeOfInstance(Builder.class);
        }
    }
}
