package org.apache.lucene.bkdtree;

import org.apache.lucene.index.SortedNumericDocValues;

@Deprecated
class BKDTreeSortedNumericDocValues extends SortedNumericDocValues
{
    final BKDTreeReader bkdTreeReader;
    final SortedNumericDocValues delegate;
    
    public BKDTreeSortedNumericDocValues(final BKDTreeReader bkdTreeReader, final SortedNumericDocValues delegate) {
        this.bkdTreeReader = bkdTreeReader;
        this.delegate = delegate;
    }
    
    public BKDTreeReader getBKDTreeReader() {
        return this.bkdTreeReader;
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
