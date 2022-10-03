package org.apache.lucene.rangetree;

import org.apache.lucene.index.SortedNumericDocValues;

class RangeTreeSortedNumericDocValues extends SortedNumericDocValues
{
    final RangeTreeReader rangeTreeReader;
    final SortedNumericDocValues delegate;
    
    public RangeTreeSortedNumericDocValues(final RangeTreeReader rangeTreeReader, final SortedNumericDocValues delegate) {
        this.rangeTreeReader = rangeTreeReader;
        this.delegate = delegate;
    }
    
    public RangeTreeReader getRangeTreeReader() {
        return this.rangeTreeReader;
    }
    
    public void setDocument(final int doc) {
        this.delegate.setDocument(doc);
    }
    
    public long valueAt(final int index) {
        return this.delegate.valueAt(index);
    }
    
    public int count() {
        return this.delegate.count();
    }
}
