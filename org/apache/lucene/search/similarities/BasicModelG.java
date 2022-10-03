package org.apache.lucene.search.similarities;

public class BasicModelG extends BasicModel
{
    @Override
    public final float score(final BasicStats stats, final float tfn) {
        final double F = (double)(stats.getTotalTermFreq() + 1L);
        final double N = (double)stats.getNumberOfDocuments();
        final double lambda = F / (N + F);
        return (float)(SimilarityBase.log2(lambda + 1.0) + tfn * SimilarityBase.log2((1.0 + lambda) / lambda));
    }
    
    @Override
    public String toString() {
        return "G";
    }
}
