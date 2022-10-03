package org.apache.lucene.index;

public abstract class SortedNumericDocValues
{
    protected SortedNumericDocValues() {
    }
    
    public abstract void setDocument(final int p0);
    
    public abstract long valueAt(final int p0);
    
    public abstract int count();
}
