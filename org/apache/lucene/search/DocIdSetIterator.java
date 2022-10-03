package org.apache.lucene.search;

import java.io.IOException;

public abstract class DocIdSetIterator
{
    public static final int NO_MORE_DOCS = Integer.MAX_VALUE;
    
    public static final DocIdSetIterator empty() {
        return new DocIdSetIterator() {
            boolean exhausted = false;
            
            @Override
            public int advance(final int target) {
                assert !this.exhausted;
                assert target >= 0;
                this.exhausted = true;
                return Integer.MAX_VALUE;
            }
            
            @Override
            public int docID() {
                return this.exhausted ? Integer.MAX_VALUE : -1;
            }
            
            @Override
            public int nextDoc() {
                assert !this.exhausted;
                this.exhausted = true;
                return Integer.MAX_VALUE;
            }
            
            @Override
            public long cost() {
                return 0L;
            }
        };
    }
    
    public static final DocIdSetIterator all(final int maxDoc) {
        return new DocIdSetIterator() {
            int doc = -1;
            
            @Override
            public int docID() {
                return this.doc;
            }
            
            @Override
            public int nextDoc() throws IOException {
                return this.advance(this.doc + 1);
            }
            
            @Override
            public int advance(final int target) throws IOException {
                this.doc = target;
                if (this.doc >= maxDoc) {
                    this.doc = Integer.MAX_VALUE;
                }
                return this.doc;
            }
            
            @Override
            public long cost() {
                return maxDoc;
            }
        };
    }
    
    public abstract int docID();
    
    public abstract int nextDoc() throws IOException;
    
    public abstract int advance(final int p0) throws IOException;
    
    protected final int slowAdvance(final int target) throws IOException {
        assert this.docID() < target;
        int doc;
        do {
            doc = this.nextDoc();
        } while (doc < target);
        return doc;
    }
    
    public abstract long cost();
}
