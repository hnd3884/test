package org.apache.lucene.search.similarities;

public class DistributionLL extends Distribution
{
    @Override
    public final float score(final BasicStats stats, final float tfn, final float lambda) {
        return (float)(-Math.log(lambda / (tfn + lambda)));
    }
    
    @Override
    public String toString() {
        return "LL";
    }
}
