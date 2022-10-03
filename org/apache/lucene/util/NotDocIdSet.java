package org.apache.lucene.util;

import org.apache.lucene.search.DocIdSetIterator;
import java.io.IOException;
import org.apache.lucene.search.DocIdSet;

public final class NotDocIdSet extends DocIdSet
{
    private static final long BASE_RAM_BYTES_USED;
    private final int maxDoc;
    private final DocIdSet in;
    
    public NotDocIdSet(final int maxDoc, final DocIdSet in) {
        this.maxDoc = maxDoc;
        this.in = in;
    }
    
    @Override
    public boolean isCacheable() {
        return this.in.isCacheable();
    }
    
    @Override
    public Bits bits() throws IOException {
        final Bits inBits = this.in.bits();
        if (inBits == null) {
            return null;
        }
        return new Bits() {
            @Override
            public boolean get(final int index) {
                return !inBits.get(index);
            }
            
            @Override
            public int length() {
                return inBits.length();
            }
        };
    }
    
    @Override
    public long ramBytesUsed() {
        return NotDocIdSet.BASE_RAM_BYTES_USED + this.in.ramBytesUsed();
    }
    
    @Override
    public DocIdSetIterator iterator() throws IOException {
        final DocIdSetIterator inIterator = this.in.iterator();
        return new DocIdSetIterator() {
            int doc = -1;
            int nextSkippedDoc = -1;
            
            @Override
            public int nextDoc() throws IOException {
                return this.advance(this.doc + 1);
            }
            
            @Override
            public int advance(final int target) throws IOException {
                this.doc = target;
                if (this.doc > this.nextSkippedDoc) {
                    this.nextSkippedDoc = inIterator.advance(this.doc);
                }
                while (this.doc < NotDocIdSet.this.maxDoc) {
                    assert this.doc <= this.nextSkippedDoc;
                    if (this.doc != this.nextSkippedDoc) {
                        return this.doc;
                    }
                    ++this.doc;
                    this.nextSkippedDoc = inIterator.nextDoc();
                }
                return this.doc = Integer.MAX_VALUE;
            }
            
            @Override
            public int docID() {
                return this.doc;
            }
            
            @Override
            public long cost() {
                return NotDocIdSet.this.maxDoc;
            }
        };
    }
    
    static {
        BASE_RAM_BYTES_USED = RamUsageEstimator.shallowSizeOfInstance(NotDocIdSet.class);
    }
}
