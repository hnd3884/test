package org.apache.lucene.search;

import java.io.IOException;

public abstract class FilteredDocIdSetIterator extends DocIdSetIterator
{
    protected DocIdSetIterator _innerIter;
    private int doc;
    
    public FilteredDocIdSetIterator(final DocIdSetIterator innerIter) {
        if (innerIter == null) {
            throw new IllegalArgumentException("null iterator");
        }
        this._innerIter = innerIter;
        this.doc = -1;
    }
    
    public DocIdSetIterator getDelegate() {
        return this._innerIter;
    }
    
    protected abstract boolean match(final int p0);
    
    @Override
    public int docID() {
        return this.doc;
    }
    
    @Override
    public int nextDoc() throws IOException {
        while ((this.doc = this._innerIter.nextDoc()) != Integer.MAX_VALUE) {
            if (this.match(this.doc)) {
                return this.doc;
            }
        }
        return this.doc;
    }
    
    @Override
    public int advance(final int target) throws IOException {
        this.doc = this._innerIter.advance(target);
        if (this.doc == Integer.MAX_VALUE) {
            return this.doc;
        }
        if (this.match(this.doc)) {
            return this.doc;
        }
        while ((this.doc = this._innerIter.nextDoc()) != Integer.MAX_VALUE) {
            if (this.match(this.doc)) {
                return this.doc;
            }
        }
        return this.doc;
    }
    
    @Override
    public long cost() {
        return this._innerIter.cost();
    }
}
