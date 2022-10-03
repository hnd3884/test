package org.apache.lucene.search.similarities;

public class BasicModelP extends BasicModel
{
    protected static double LOG2_E;
    
    @Override
    public final float score(final BasicStats stats, final float tfn) {
        final float lambda = (stats.getTotalTermFreq() + 1L) / (float)(stats.getNumberOfDocuments() + 1L);
        return (float)(tfn * SimilarityBase.log2(tfn / lambda) + (lambda + 1.0f / (12.0f * tfn) - tfn) * BasicModelP.LOG2_E + 0.5 * SimilarityBase.log2(6.283185307179586 * tfn));
    }
    
    @Override
    public String toString() {
        return "P";
    }
    
    static {
        BasicModelP.LOG2_E = SimilarityBase.log2(2.718281828459045);
    }
}
