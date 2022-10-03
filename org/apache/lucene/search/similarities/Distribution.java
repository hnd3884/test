package org.apache.lucene.search.similarities;

import org.apache.lucene.search.Explanation;

public abstract class Distribution
{
    public abstract float score(final BasicStats p0, final float p1, final float p2);
    
    public Explanation explain(final BasicStats stats, final float tfn, final float lambda) {
        return Explanation.match(this.score(stats, tfn, lambda), this.getClass().getSimpleName(), new Explanation[0]);
    }
    
    @Override
    public abstract String toString();
}
