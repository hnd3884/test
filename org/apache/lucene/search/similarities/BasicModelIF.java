package org.apache.lucene.search.similarities;

public class BasicModelIF extends BasicModel
{
    @Override
    public final float score(final BasicStats stats, final float tfn) {
        final long N = stats.getNumberOfDocuments();
        final long F = stats.getTotalTermFreq();
        return tfn * (float)SimilarityBase.log2(1.0 + (N + 1L) / (F + 0.5));
    }
    
    @Override
    public String toString() {
        return "I(F)";
    }
}
