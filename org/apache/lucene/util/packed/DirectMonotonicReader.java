package org.apache.lucene.util.packed;

import java.util.Collections;
import java.util.Collection;
import org.apache.lucene.util.RamUsageEstimator;
import org.apache.lucene.util.Accountable;
import org.apache.lucene.store.RandomAccessInput;
import java.io.IOException;
import org.apache.lucene.store.IndexInput;
import org.apache.lucene.util.LongValues;

public final class DirectMonotonicReader
{
    private static final LongValues EMPTY;
    
    public static Meta loadMeta(final IndexInput metaIn, final long numValues, final int blockShift) throws IOException {
        final Meta meta = new Meta(numValues, blockShift);
        for (int i = 0; i < meta.numBlocks; ++i) {
            meta.mins[i] = metaIn.readLong();
            meta.avgs[i] = Float.intBitsToFloat(metaIn.readInt());
            meta.offsets[i] = metaIn.readLong();
            meta.bpvs[i] = metaIn.readByte();
        }
        return meta;
    }
    
    public static LongValues getInstance(final Meta meta, final RandomAccessInput data) throws IOException {
        final LongValues[] readers = new LongValues[meta.numBlocks];
        for (int i = 0; i < meta.mins.length; ++i) {
            if (meta.bpvs[i] == 0) {
                readers[i] = DirectMonotonicReader.EMPTY;
            }
            else {
                readers[i] = DirectReader.getInstance(data, meta.bpvs[i], meta.offsets[i]);
            }
        }
        final int blockShift = meta.blockShift;
        final long[] mins = meta.mins;
        final float[] avgs = meta.avgs;
        return new LongValues() {
            @Override
            public long get(final long index) {
                final int block = (int)(index >>> blockShift);
                final long blockIndex = index & (long)((1 << blockShift) - 1);
                final long delta = readers[block].get(blockIndex);
                return mins[block] + (long)(avgs[block] * blockIndex) + delta;
            }
        };
    }
    
    static {
        EMPTY = new LongValues() {
            @Override
            public long get(final long index) {
                return 0L;
            }
        };
    }
    
    public static class Meta implements Accountable
    {
        private static final long BASE_RAM_BYTES_USED;
        final long numValues;
        final int blockShift;
        final int numBlocks;
        final long[] mins;
        final float[] avgs;
        final byte[] bpvs;
        final long[] offsets;
        
        Meta(final long numValues, final int blockShift) {
            this.numValues = numValues;
            this.blockShift = blockShift;
            long numBlocks = numValues >>> blockShift;
            if (numBlocks << blockShift < numValues) {
                ++numBlocks;
            }
            this.numBlocks = (int)numBlocks;
            this.mins = new long[this.numBlocks];
            this.avgs = new float[this.numBlocks];
            this.bpvs = new byte[this.numBlocks];
            this.offsets = new long[this.numBlocks];
        }
        
        @Override
        public long ramBytesUsed() {
            return Meta.BASE_RAM_BYTES_USED + RamUsageEstimator.sizeOf(this.mins) + RamUsageEstimator.sizeOf(this.avgs) + RamUsageEstimator.sizeOf(this.bpvs) + RamUsageEstimator.sizeOf(this.offsets);
        }
        
        @Override
        public Collection<Accountable> getChildResources() {
            return (Collection<Accountable>)Collections.emptyList();
        }
        
        static {
            BASE_RAM_BYTES_USED = RamUsageEstimator.shallowSizeOfInstance(Meta.class);
        }
    }
}
