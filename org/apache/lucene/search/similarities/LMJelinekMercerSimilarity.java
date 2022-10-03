package org.apache.lucene.search.similarities;

import java.util.Locale;
import org.apache.lucene.search.Explanation;
import java.util.List;

public class LMJelinekMercerSimilarity extends LMSimilarity
{
    private final float lambda;
    
    public LMJelinekMercerSimilarity(final CollectionModel collectionModel, final float lambda) {
        super(collectionModel);
        this.lambda = lambda;
    }
    
    public LMJelinekMercerSimilarity(final float lambda) {
        this.lambda = lambda;
    }
    
    @Override
    protected float score(final BasicStats stats, final float freq, final float docLen) {
        return stats.getBoost() * (float)Math.log(1.0f + (1.0f - this.lambda) * freq / docLen / (this.lambda * ((LMStats)stats).getCollectionProbability()));
    }
    
    @Override
    protected void explain(final List<Explanation> subs, final BasicStats stats, final int doc, final float freq, final float docLen) {
        if (stats.getBoost() != 1.0f) {
            subs.add(Explanation.match(stats.getBoost(), "boost", new Explanation[0]));
        }
        subs.add(Explanation.match(this.lambda, "lambda", new Explanation[0]));
        super.explain(subs, stats, doc, freq, docLen);
    }
    
    public float getLambda() {
        return this.lambda;
    }
    
    @Override
    public String getName() {
        return String.format(Locale.ROOT, "Jelinek-Mercer(%f)", this.getLambda());
    }
}
