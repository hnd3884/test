package org.apache.lucene.search.similarities;

public class BasicModelD extends BasicModel
{
    @Override
    public final float score(final BasicStats stats, final float tfn) {
        final double F = stats.getTotalTermFreq() + 1L + tfn;
        final double phi = tfn / F;
        final double nphi = 1.0 - phi;
        final double p = 1.0 / (stats.getNumberOfDocuments() + 1L);
        final double D = phi * SimilarityBase.log2(phi / p) + nphi * SimilarityBase.log2(nphi / (1.0 - p));
        return (float)(D * F + 0.5 * SimilarityBase.log2(1.0 + 6.283185307179586 * tfn * nphi));
    }
    
    @Override
    public String toString() {
        return "D";
    }
}
