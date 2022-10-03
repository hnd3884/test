package org.apache.lucene.search.similarities;

public abstract class Independence
{
    public abstract float score(final float p0, final float p1);
    
    @Override
    public abstract String toString();
}
