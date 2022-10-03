package org.apache.lucene.util;

import java.util.Arrays;
import java.io.IOException;
import org.apache.lucene.search.DocIdSetIterator;
import org.apache.lucene.search.DocIdSet;

public class RoaringDocIdSet extends DocIdSet
{
    private static final int BLOCK_SIZE = 65536;
    private static final int MAX_ARRAY_LENGTH = 4096;
    private static final long BASE_RAM_BYTES_USED;
    private final DocIdSet[] docIdSets;
    private final int cardinality;
    private final long ramBytesUsed;
    
    private RoaringDocIdSet(final DocIdSet[] docIdSets, final int cardinality) {
        this.docIdSets = docIdSets;
        long ramBytesUsed = RoaringDocIdSet.BASE_RAM_BYTES_USED + RamUsageEstimator.shallowSizeOf(docIdSets);
        for (final DocIdSet set : this.docIdSets) {
            if (set != null) {
                ramBytesUsed += set.ramBytesUsed();
            }
        }
        this.ramBytesUsed = ramBytesUsed;
        this.cardinality = cardinality;
    }
    
    @Override
    public boolean isCacheable() {
        return true;
    }
    
    @Override
    public long ramBytesUsed() {
        return this.ramBytesUsed;
    }
    
    @Override
    public DocIdSetIterator iterator() throws IOException {
        if (this.cardinality == 0) {
            return null;
        }
        return new Iterator();
    }
    
    public int cardinality() {
        return this.cardinality;
    }
    
    @Override
    public String toString() {
        return "RoaringDocIdSet(cardinality=" + this.cardinality + ")";
    }
    
    static {
        BASE_RAM_BYTES_USED = RamUsageEstimator.shallowSizeOfInstance(RoaringDocIdSet.class);
    }
    
    public static class Builder
    {
        private final int maxDoc;
        private final DocIdSet[] sets;
        private int cardinality;
        private int lastDocId;
        private int currentBlock;
        private int currentBlockCardinality;
        private final short[] buffer;
        private FixedBitSet denseBuffer;
        
        public Builder(final int maxDoc) {
            this.maxDoc = maxDoc;
            this.sets = new DocIdSet[maxDoc + 65536 - 1 >>> 16];
            this.lastDocId = -1;
            this.currentBlock = -1;
            this.buffer = new short[4096];
        }
        
        private void flush() {
            assert this.currentBlockCardinality <= 65536;
            if (this.currentBlockCardinality <= 4096) {
                assert this.denseBuffer == null;
                if (this.currentBlockCardinality > 0) {
                    this.sets[this.currentBlock] = new ShortArrayDocIdSet(Arrays.copyOf(this.buffer, this.currentBlockCardinality));
                }
            }
            else {
                assert this.denseBuffer != null;
                assert this.denseBuffer.cardinality() == this.currentBlockCardinality;
                if (this.denseBuffer.length() == 65536 && 65536 - this.currentBlockCardinality < 4096) {
                    final short[] excludedDocs = new short[65536 - this.currentBlockCardinality];
                    this.denseBuffer.flip(0, this.denseBuffer.length());
                    int excludedDoc = -1;
                    for (int i = 0; i < excludedDocs.length; ++i) {
                        excludedDoc = this.denseBuffer.nextSetBit(excludedDoc + 1);
                        assert excludedDoc != Integer.MAX_VALUE;
                        excludedDocs[i] = (short)excludedDoc;
                    }
                    assert this.denseBuffer.nextSetBit(excludedDoc + 1) == Integer.MAX_VALUE;
                    this.sets[this.currentBlock] = new NotDocIdSet(65536, new ShortArrayDocIdSet(excludedDocs));
                }
                else {
                    this.sets[this.currentBlock] = new BitDocIdSet(this.denseBuffer, this.currentBlockCardinality);
                }
                this.denseBuffer = null;
            }
            this.cardinality += this.currentBlockCardinality;
            this.denseBuffer = null;
            this.currentBlockCardinality = 0;
        }
        
        public Builder add(final int docId) {
            if (docId <= this.lastDocId) {
                throw new IllegalArgumentException("Doc ids must be added in-order, got " + docId + " which is <= lastDocID=" + this.lastDocId);
            }
            final int block = docId >>> 16;
            if (block != this.currentBlock) {
                this.flush();
                this.currentBlock = block;
            }
            if (this.currentBlockCardinality < 4096) {
                this.buffer[this.currentBlockCardinality] = (short)docId;
            }
            else {
                if (this.denseBuffer == null) {
                    final int numBits = Math.min(65536, this.maxDoc - (block << 16));
                    this.denseBuffer = new FixedBitSet(numBits);
                    for (final short doc : this.buffer) {
                        this.denseBuffer.set(doc & 0xFFFF);
                    }
                }
                this.denseBuffer.set(docId & 0xFFFF);
            }
            this.lastDocId = docId;
            ++this.currentBlockCardinality;
            return this;
        }
        
        public Builder add(final DocIdSetIterator disi) throws IOException {
            for (int doc = disi.nextDoc(); doc != Integer.MAX_VALUE; doc = disi.nextDoc()) {
                this.add(doc);
            }
            return this;
        }
        
        public RoaringDocIdSet build() {
            this.flush();
            return new RoaringDocIdSet(this.sets, this.cardinality, null);
        }
    }
    
    private static class ShortArrayDocIdSet extends DocIdSet
    {
        private static final long BASE_RAM_BYTES_USED;
        private final short[] docIDs;
        
        private ShortArrayDocIdSet(final short[] docIDs) {
            this.docIDs = docIDs;
        }
        
        @Override
        public long ramBytesUsed() {
            return ShortArrayDocIdSet.BASE_RAM_BYTES_USED + RamUsageEstimator.sizeOf(this.docIDs);
        }
        
        @Override
        public DocIdSetIterator iterator() throws IOException {
            return new DocIdSetIterator() {
                int i = -1;
                int doc = -1;
                
                private int docId(final int i) {
                    return ShortArrayDocIdSet.this.docIDs[i] & 0xFFFF;
                }
                
                @Override
                public int nextDoc() throws IOException {
                    if (++this.i >= ShortArrayDocIdSet.this.docIDs.length) {
                        return this.doc = Integer.MAX_VALUE;
                    }
                    return this.doc = this.docId(this.i);
                }
                
                @Override
                public int docID() {
                    return this.doc;
                }
                
                @Override
                public long cost() {
                    return ShortArrayDocIdSet.this.docIDs.length;
                }
                
                @Override
                public int advance(final int target) throws IOException {
                    int lo = this.i + 1;
                    int hi = ShortArrayDocIdSet.this.docIDs.length - 1;
                    while (lo <= hi) {
                        final int mid = lo + hi >>> 1;
                        final int midDoc = this.docId(mid);
                        if (midDoc < target) {
                            lo = mid + 1;
                        }
                        else {
                            hi = mid - 1;
                        }
                    }
                    if (lo == ShortArrayDocIdSet.this.docIDs.length) {
                        this.i = ShortArrayDocIdSet.this.docIDs.length;
                        return this.doc = Integer.MAX_VALUE;
                    }
                    this.i = lo;
                    return this.doc = this.docId(this.i);
                }
            };
        }
        
        static {
            BASE_RAM_BYTES_USED = RamUsageEstimator.shallowSizeOfInstance(ShortArrayDocIdSet.class);
        }
    }
    
    private class Iterator extends DocIdSetIterator
    {
        int block;
        DocIdSetIterator sub;
        int doc;
        
        Iterator() throws IOException {
            this.sub = null;
            this.doc = -1;
            this.block = -1;
            this.sub = DocIdSetIterator.empty();
        }
        
        @Override
        public int docID() {
            return this.doc;
        }
        
        @Override
        public int nextDoc() throws IOException {
            final int subNext = this.sub.nextDoc();
            if (subNext == Integer.MAX_VALUE) {
                return this.firstDocFromNextBlock();
            }
            return this.doc = (this.block << 16 | subNext);
        }
        
        @Override
        public int advance(final int target) throws IOException {
            final int targetBlock = target >>> 16;
            if (targetBlock != this.block) {
                this.block = targetBlock;
                if (this.block >= RoaringDocIdSet.this.docIdSets.length) {
                    this.sub = null;
                    return this.doc = Integer.MAX_VALUE;
                }
                if (RoaringDocIdSet.this.docIdSets[this.block] == null) {
                    return this.firstDocFromNextBlock();
                }
                this.sub = RoaringDocIdSet.this.docIdSets[this.block].iterator();
            }
            final int subNext = this.sub.advance(target & 0xFFFF);
            if (subNext == Integer.MAX_VALUE) {
                return this.firstDocFromNextBlock();
            }
            return this.doc = (this.block << 16 | subNext);
        }
        
        private int firstDocFromNextBlock() throws IOException {
            do {
                ++this.block;
                if (this.block >= RoaringDocIdSet.this.docIdSets.length) {
                    this.sub = null;
                    return this.doc = Integer.MAX_VALUE;
                }
            } while (RoaringDocIdSet.this.docIdSets[this.block] == null);
            this.sub = RoaringDocIdSet.this.docIdSets[this.block].iterator();
            final int subNext = this.sub.nextDoc();
            assert subNext != Integer.MAX_VALUE;
            return this.doc = (this.block << 16 | subNext);
        }
        
        @Override
        public long cost() {
            return RoaringDocIdSet.this.cardinality;
        }
    }
}
