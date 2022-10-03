package org.apache.lucene.index;

public abstract class RandomAccessOrds extends SortedSetDocValues
{
    protected RandomAccessOrds() {
    }
    
    public abstract long ordAt(final int p0);
    
    public abstract int cardinality();
}
