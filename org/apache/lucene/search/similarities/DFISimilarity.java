package org.apache.lucene.search.similarities;

public class DFISimilarity extends SimilarityBase
{
    private final Independence independence;
    
    public DFISimilarity(final Independence independenceMeasure) {
        this.independence = independenceMeasure;
    }
    
    @Override
    protected float score(final BasicStats stats, final float freq, final float docLen) {
        final float expected = (stats.getTotalTermFreq() + 1L) * docLen / (stats.getNumberOfFieldTokens() + 1L);
        if (freq <= expected) {
            return 0.0f;
        }
        final float measure = this.independence.score(freq, expected);
        return stats.getBoost() * (float)SimilarityBase.log2(measure + 1.0f);
    }
    
    public Independence getIndependence() {
        return this.independence;
    }
    
    @Override
    public String toString() {
        return "DFI(" + this.independence + ")";
    }
}
