package org.apache.lucene.util;

import java.util.Arrays;
import java.io.IOException;
import org.apache.lucene.search.DocIdSetIterator;
import org.apache.lucene.search.DocIdSet;

final class IntArrayDocIdSet extends DocIdSet
{
    private static final long BASE_RAM_BYTES_USED;
    private final int[] docs;
    private final int length;
    
    IntArrayDocIdSet(final int[] docs, final int length) {
        if (docs[length] != Integer.MAX_VALUE) {
            throw new IllegalArgumentException();
        }
        this.docs = docs;
        this.length = length;
    }
    
    @Override
    public long ramBytesUsed() {
        return IntArrayDocIdSet.BASE_RAM_BYTES_USED + RamUsageEstimator.sizeOf(this.docs);
    }
    
    @Override
    public DocIdSetIterator iterator() throws IOException {
        return new IntArrayDocIdSetIterator(this.docs, this.length);
    }
    
    static {
        BASE_RAM_BYTES_USED = RamUsageEstimator.shallowSizeOfInstance(IntArrayDocIdSet.class);
    }
    
    static class IntArrayDocIdSetIterator extends DocIdSetIterator
    {
        private final int[] docs;
        private final int length;
        private int i;
        private int doc;
        
        IntArrayDocIdSetIterator(final int[] docs, final int length) {
            this.i = -1;
            this.doc = -1;
            this.docs = docs;
            this.length = length;
        }
        
        @Override
        public int docID() {
            return this.doc;
        }
        
        @Override
        public int nextDoc() throws IOException {
            return this.doc = this.docs[++this.i];
        }
        
        @Override
        public int advance(final int target) throws IOException {
            this.i = Arrays.binarySearch(this.docs, this.i + 1, this.length, target);
            if (this.i < 0) {
                this.i = -1 - this.i;
            }
            return this.doc = this.docs[this.i];
        }
        
        @Override
        public long cost() {
            return this.length;
        }
    }
}
