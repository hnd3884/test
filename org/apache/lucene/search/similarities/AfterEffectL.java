package org.apache.lucene.search.similarities;

import org.apache.lucene.search.Explanation;

public class AfterEffectL extends AfterEffect
{
    @Override
    public final float score(final BasicStats stats, final float tfn) {
        return 1.0f / (tfn + 1.0f);
    }
    
    @Override
    public final Explanation explain(final BasicStats stats, final float tfn) {
        return Explanation.match(this.score(stats, tfn), this.getClass().getSimpleName() + ", computed from: ", Explanation.match(tfn, "tfn", new Explanation[0]));
    }
    
    @Override
    public String toString() {
        return "L";
    }
}
