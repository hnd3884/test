package org.apache.lucene.rangetree;

import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.index.SortedSetDocValues;

class RangeTreeSortedSetDocValues extends SortedSetDocValues
{
    final RangeTreeReader rangeTreeReader;
    final SortedSetDocValues delegate;
    
    public RangeTreeSortedSetDocValues(final RangeTreeReader rangeTreeReader, final SortedSetDocValues delegate) {
        this.rangeTreeReader = rangeTreeReader;
        this.delegate = delegate;
    }
    
    public RangeTreeReader getRangeTreeReader() {
        return this.rangeTreeReader;
    }
    
    public long nextOrd() {
        return this.delegate.nextOrd();
    }
    
    public void setDocument(final int doc) {
        this.delegate.setDocument(doc);
    }
    
    public BytesRef lookupOrd(final long ord) {
        return this.delegate.lookupOrd(ord);
    }
    
    public long getValueCount() {
        return this.delegate.getValueCount();
    }
    
    public long lookupTerm(final BytesRef key) {
        return this.delegate.lookupTerm(key);
    }
    
    public TermsEnum termsEnum() {
        return this.delegate.termsEnum();
    }
}
