package org.apache.lucene.search;

import java.io.IOException;
import org.apache.lucene.util.Bits;
import org.apache.lucene.util.Accountable;
import java.util.Collection;
import org.apache.lucene.util.RamUsageEstimator;

@Deprecated
public abstract class FilteredDocIdSet extends DocIdSet
{
    private final DocIdSet _innerSet;
    
    public FilteredDocIdSet(final DocIdSet innerSet) {
        this._innerSet = innerSet;
    }
    
    public DocIdSet getDelegate() {
        return this._innerSet;
    }
    
    @Override
    public boolean isCacheable() {
        return this._innerSet.isCacheable();
    }
    
    @Override
    public long ramBytesUsed() {
        return RamUsageEstimator.NUM_BYTES_OBJECT_REF + this._innerSet.ramBytesUsed();
    }
    
    @Override
    public Collection<Accountable> getChildResources() {
        return this._innerSet.getChildResources();
    }
    
    @Override
    public Bits bits() throws IOException {
        final Bits bits = this._innerSet.bits();
        return (bits == null) ? null : new Bits() {
            @Override
            public boolean get(final int docid) {
                return bits.get(docid) && FilteredDocIdSet.this.match(docid);
            }
            
            @Override
            public int length() {
                return bits.length();
            }
        };
    }
    
    protected abstract boolean match(final int p0);
    
    @Override
    public DocIdSetIterator iterator() throws IOException {
        final DocIdSetIterator iterator = this._innerSet.iterator();
        if (iterator == null) {
            return null;
        }
        return new FilteredDocIdSetIterator(iterator) {
            @Override
            protected boolean match(final int docid) {
                return FilteredDocIdSet.this.match(docid);
            }
        };
    }
}
