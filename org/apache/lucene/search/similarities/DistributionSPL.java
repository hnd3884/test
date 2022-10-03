package org.apache.lucene.search.similarities;

public class DistributionSPL extends Distribution
{
    @Override
    public final float score(final BasicStats stats, final float tfn, float lambda) {
        if (lambda == 1.0f) {
            lambda = 0.99f;
        }
        return (float)(-Math.log((Math.pow(lambda, tfn / (tfn + 1.0f)) - lambda) / (1.0f - lambda)));
    }
    
    @Override
    public String toString() {
        return "SPL";
    }
}
