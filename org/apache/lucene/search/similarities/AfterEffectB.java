package org.apache.lucene.search.similarities;

import org.apache.lucene.search.Explanation;

public class AfterEffectB extends AfterEffect
{
    @Override
    public final float score(final BasicStats stats, final float tfn) {
        final long F = stats.getTotalTermFreq() + 1L;
        final long n = stats.getDocFreq() + 1L;
        return (F + 1L) / (n * (tfn + 1.0f));
    }
    
    @Override
    public final Explanation explain(final BasicStats stats, final float tfn) {
        return Explanation.match(this.score(stats, tfn), this.getClass().getSimpleName() + ", computed from: ", Explanation.match(tfn, "tfn", new Explanation[0]), Explanation.match((float)stats.getTotalTermFreq(), "totalTermFreq", new Explanation[0]), Explanation.match((float)stats.getDocFreq(), "docFreq", new Explanation[0]));
    }
    
    @Override
    public String toString() {
        return "B";
    }
}
