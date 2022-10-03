package org.apache.lucene.index;

public abstract class NumericDocValues
{
    protected NumericDocValues() {
    }
    
    public abstract long get(final int p0);
}
