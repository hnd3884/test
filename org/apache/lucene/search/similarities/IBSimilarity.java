package org.apache.lucene.search.similarities;

import org.apache.lucene.search.Explanation;
import java.util.List;

public class IBSimilarity extends SimilarityBase
{
    protected final Distribution distribution;
    protected final Lambda lambda;
    protected final Normalization normalization;
    
    public IBSimilarity(final Distribution distribution, final Lambda lambda, final Normalization normalization) {
        this.distribution = distribution;
        this.lambda = lambda;
        this.normalization = normalization;
    }
    
    @Override
    protected float score(final BasicStats stats, final float freq, final float docLen) {
        return stats.getBoost() * this.distribution.score(stats, this.normalization.tfn(stats, freq, docLen), this.lambda.lambda(stats));
    }
    
    @Override
    protected void explain(final List<Explanation> subs, final BasicStats stats, final int doc, final float freq, final float docLen) {
        if (stats.getBoost() != 1.0f) {
            subs.add(Explanation.match(stats.getBoost(), "boost", new Explanation[0]));
        }
        final Explanation normExpl = this.normalization.explain(stats, freq, docLen);
        final Explanation lambdaExpl = this.lambda.explain(stats);
        subs.add(normExpl);
        subs.add(lambdaExpl);
        subs.add(this.distribution.explain(stats, normExpl.getValue(), lambdaExpl.getValue()));
    }
    
    @Override
    public String toString() {
        return "IB " + this.distribution.toString() + "-" + this.lambda.toString() + this.normalization.toString();
    }
    
    public Distribution getDistribution() {
        return this.distribution;
    }
    
    public Lambda getLambda() {
        return this.lambda;
    }
    
    public Normalization getNormalization() {
        return this.normalization;
    }
}
