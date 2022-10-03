package org.apache.lucene.search.similarities;

public class BasicModelBE extends BasicModel
{
    @Override
    public final float score(final BasicStats stats, final float tfn) {
        final double F = stats.getTotalTermFreq() + 1L + tfn;
        final double N = F + stats.getNumberOfDocuments();
        return (float)(-SimilarityBase.log2((N - 1.0) * 2.718281828459045) + this.f(N + F - 1.0, N + F - tfn - 2.0) - this.f(F, F - tfn));
    }
    
    private final double f(final double n, final double m) {
        return (m + 0.5) * SimilarityBase.log2(n / m) + (n - m) * SimilarityBase.log2(n);
    }
    
    @Override
    public String toString() {
        return "Be";
    }
}
