package org.apache.lucene.search.similarities;

public class BasicModelIne extends BasicModel
{
    @Override
    public final float score(final BasicStats stats, final float tfn) {
        final long N = stats.getNumberOfDocuments();
        final long F = stats.getTotalTermFreq();
        final double ne = N * (1.0 - Math.pow((N - 1L) / (double)N, (double)F));
        return tfn * (float)SimilarityBase.log2((N + 1L) / (ne + 0.5));
    }
    
    @Override
    public String toString() {
        return "I(ne)";
    }
}
