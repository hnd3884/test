package org.apache.lucene.search.similarities;

import org.apache.lucene.search.Explanation;

public class BasicModelIn extends BasicModel
{
    @Override
    public final float score(final BasicStats stats, final float tfn) {
        final long N = stats.getNumberOfDocuments();
        final long n = stats.getDocFreq();
        return tfn * (float)SimilarityBase.log2((N + 1L) / (n + 0.5));
    }
    
    @Override
    public final Explanation explain(final BasicStats stats, final float tfn) {
        return Explanation.match(this.score(stats, tfn), this.getClass().getSimpleName() + ", computed from: ", Explanation.match((float)stats.getNumberOfDocuments(), "numberOfDocuments", new Explanation[0]), Explanation.match((float)stats.getDocFreq(), "docFreq", new Explanation[0]));
    }
    
    @Override
    public String toString() {
        return "I(n)";
    }
}
