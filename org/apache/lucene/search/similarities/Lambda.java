package org.apache.lucene.search.similarities;

import org.apache.lucene.search.Explanation;

public abstract class Lambda
{
    public abstract float lambda(final BasicStats p0);
    
    public abstract Explanation explain(final BasicStats p0);
    
    @Override
    public abstract String toString();
}
