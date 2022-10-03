package org.apache.lucene.util.packed;

import org.apache.lucene.util.Accountable;
import java.util.Arrays;
import org.apache.lucene.util.RamUsageEstimator;

class MonotonicLongValues extends DeltaPackedLongValues
{
    private static final long BASE_RAM_BYTES_USED;
    final float[] averages;
    
    MonotonicLongValues(final int pageShift, final int pageMask, final PackedInts.Reader[] values, final long[] mins, final float[] averages, final long size, final long ramBytesUsed) {
        super(pageShift, pageMask, values, mins, size, ramBytesUsed);
        assert values.length == averages.length;
        this.averages = averages;
    }
    
    @Override
    long get(final int block, final int element) {
        return MonotonicBlockPackedReader.expected(this.mins[block], this.averages[block], element) + this.values[block].get(element);
    }
    
    @Override
    int decodeBlock(final int block, final long[] dest) {
        final int count = super.decodeBlock(block, dest);
        final float average = this.averages[block];
        for (int i = 0; i < count; ++i) {
            final int n = i;
            dest[n] += MonotonicBlockPackedReader.expected(0L, average, i);
        }
        return count;
    }
    
    static {
        BASE_RAM_BYTES_USED = RamUsageEstimator.shallowSizeOfInstance(MonotonicLongValues.class);
    }
    
    static class Builder extends DeltaPackedLongValues.Builder
    {
        private static final long BASE_RAM_BYTES_USED;
        float[] averages;
        
        Builder(final int pageSize, final float acceptableOverheadRatio) {
            super(pageSize, acceptableOverheadRatio);
            this.averages = new float[this.values.length];
            this.ramBytesUsed += RamUsageEstimator.sizeOf(this.averages);
        }
        
        @Override
        long baseRamBytesUsed() {
            return Builder.BASE_RAM_BYTES_USED;
        }
        
        @Override
        public MonotonicLongValues build() {
            this.finish();
            this.pending = null;
            final PackedInts.Reader[] values = Arrays.copyOf(this.values, this.valuesOff);
            final long[] mins = Arrays.copyOf(this.mins, this.valuesOff);
            final float[] averages = Arrays.copyOf(this.averages, this.valuesOff);
            final long ramBytesUsed = MonotonicLongValues.BASE_RAM_BYTES_USED + RamUsageEstimator.sizeOf(values) + RamUsageEstimator.sizeOf(mins) + RamUsageEstimator.sizeOf(averages);
            return new MonotonicLongValues(this.pageShift, this.pageMask, values, mins, averages, this.size, ramBytesUsed);
        }
        
        @Override
        void pack(final long[] values, final int numValues, final int block, final float acceptableOverheadRatio) {
            final float average = (numValues == 1) ? 0.0f : ((values[numValues - 1] - values[0]) / (float)(numValues - 1));
            for (int i = 0; i < numValues; ++i) {
                final int n = i;
                values[n] -= MonotonicBlockPackedReader.expected(0L, average, i);
            }
            super.pack(values, numValues, block, acceptableOverheadRatio);
            this.averages[block] = average;
        }
        
        @Override
        void grow(final int newBlockCount) {
            super.grow(newBlockCount);
            this.ramBytesUsed -= RamUsageEstimator.sizeOf(this.averages);
            this.averages = Arrays.copyOf(this.averages, newBlockCount);
            this.ramBytesUsed += RamUsageEstimator.sizeOf(this.averages);
        }
        
        static {
            BASE_RAM_BYTES_USED = RamUsageEstimator.shallowSizeOfInstance(Builder.class);
        }
    }
}
