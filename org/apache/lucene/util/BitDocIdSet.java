package org.apache.lucene.util;

import java.io.IOException;
import org.apache.lucene.search.DocIdSetIterator;
import org.apache.lucene.search.DocIdSet;

public class BitDocIdSet extends DocIdSet
{
    private static final long BASE_RAM_BYTES_USED;
    private final BitSet set;
    private final long cost;
    
    public BitDocIdSet(final BitSet set, final long cost) {
        this.set = set;
        this.cost = cost;
    }
    
    public BitDocIdSet(final BitSet set) {
        this(set, set.approximateCardinality());
    }
    
    @Override
    public DocIdSetIterator iterator() {
        return new BitSetIterator(this.set, this.cost);
    }
    
    @Override
    public BitSet bits() {
        return this.set;
    }
    
    @Override
    public boolean isCacheable() {
        return true;
    }
    
    @Override
    public long ramBytesUsed() {
        return BitDocIdSet.BASE_RAM_BYTES_USED + this.set.ramBytesUsed();
    }
    
    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "(set=" + this.set + ",cost=" + this.cost + ")";
    }
    
    static {
        BASE_RAM_BYTES_USED = RamUsageEstimator.shallowSizeOfInstance(BitDocIdSet.class);
    }
    
    public static final class Builder
    {
        private final int maxDoc;
        private final int threshold;
        private SparseFixedBitSet sparseSet;
        private FixedBitSet denseSet;
        private long costUpperBound;
        
        public Builder(final int maxDoc, final boolean full) {
            this.maxDoc = maxDoc;
            this.threshold = maxDoc >>> 10;
            if (full) {
                (this.denseSet = new FixedBitSet(maxDoc)).set(0, maxDoc);
            }
        }
        
        public Builder(final int maxDoc) {
            this(maxDoc, false);
        }
        
        boolean dense() {
            return this.denseSet != null;
        }
        
        public boolean isDefinitelyEmpty() {
            return this.sparseSet == null && this.denseSet == null;
        }
        
        public void or(final DocIdSetIterator it) throws IOException {
            if (this.denseSet != null) {
                this.denseSet.or(it);
                return;
            }
            final long itCost = it.cost();
            this.costUpperBound += itCost;
            if (this.costUpperBound >= this.threshold) {
                this.costUpperBound = ((this.sparseSet == null) ? 0 : this.sparseSet.approximateCardinality()) + itCost;
                if (this.costUpperBound >= this.threshold) {
                    (this.denseSet = new FixedBitSet(this.maxDoc)).or(it);
                    if (this.sparseSet != null) {
                        this.denseSet.or(new BitSetIterator(this.sparseSet, 0L));
                    }
                    return;
                }
            }
            if (this.sparseSet == null) {
                this.sparseSet = new SparseFixedBitSet(this.maxDoc);
            }
            this.sparseSet.or(it);
        }
        
        @Deprecated
        public void and(final DocIdSetIterator it) throws IOException {
            if (this.denseSet != null) {
                this.denseSet.and(it);
            }
            else if (this.sparseSet != null) {
                this.sparseSet.and(it);
            }
        }
        
        @Deprecated
        public void andNot(final DocIdSetIterator it) throws IOException {
            if (this.denseSet != null) {
                this.denseSet.andNot(it);
            }
            else if (this.sparseSet != null) {
                this.sparseSet.andNot(it);
            }
        }
        
        public BitDocIdSet build() {
            BitDocIdSet result;
            if (this.denseSet != null) {
                result = new BitDocIdSet(this.denseSet);
            }
            else if (this.sparseSet != null) {
                result = new BitDocIdSet(this.sparseSet);
            }
            else {
                result = null;
            }
            this.denseSet = null;
            this.sparseSet = null;
            this.costUpperBound = 0L;
            return result;
        }
    }
}
