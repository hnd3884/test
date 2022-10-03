package org.apache.lucene.search.similarities;

import org.apache.lucene.search.Explanation;
import java.util.List;

public class DFRSimilarity extends SimilarityBase
{
    protected final BasicModel basicModel;
    protected final AfterEffect afterEffect;
    protected final Normalization normalization;
    
    public DFRSimilarity(final BasicModel basicModel, final AfterEffect afterEffect, final Normalization normalization) {
        if (basicModel == null || afterEffect == null || normalization == null) {
            throw new NullPointerException("null parameters not allowed.");
        }
        this.basicModel = basicModel;
        this.afterEffect = afterEffect;
        this.normalization = normalization;
    }
    
    @Override
    protected float score(final BasicStats stats, final float freq, final float docLen) {
        final float tfn = this.normalization.tfn(stats, freq, docLen);
        return stats.getBoost() * this.basicModel.score(stats, tfn) * this.afterEffect.score(stats, tfn);
    }
    
    @Override
    protected void explain(final List<Explanation> subs, final BasicStats stats, final int doc, final float freq, final float docLen) {
        if (stats.getBoost() != 1.0f) {
            subs.add(Explanation.match(stats.getBoost(), "boost", new Explanation[0]));
        }
        final Explanation normExpl = this.normalization.explain(stats, freq, docLen);
        final float tfn = normExpl.getValue();
        subs.add(normExpl);
        subs.add(this.basicModel.explain(stats, tfn));
        subs.add(this.afterEffect.explain(stats, tfn));
    }
    
    @Override
    public String toString() {
        return "DFR " + this.basicModel.toString() + this.afterEffect.toString() + this.normalization.toString();
    }
    
    public BasicModel getBasicModel() {
        return this.basicModel;
    }
    
    public AfterEffect getAfterEffect() {
        return this.afterEffect;
    }
    
    public Normalization getNormalization() {
        return this.normalization;
    }
}
