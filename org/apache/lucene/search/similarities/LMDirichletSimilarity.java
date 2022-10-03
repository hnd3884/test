package org.apache.lucene.search.similarities;

import java.util.Locale;
import org.apache.lucene.search.Explanation;
import java.util.List;

public class LMDirichletSimilarity extends LMSimilarity
{
    private final float mu;
    
    public LMDirichletSimilarity(final CollectionModel collectionModel, final float mu) {
        super(collectionModel);
        this.mu = mu;
    }
    
    public LMDirichletSimilarity(final float mu) {
        this.mu = mu;
    }
    
    public LMDirichletSimilarity(final CollectionModel collectionModel) {
        this(collectionModel, 2000.0f);
    }
    
    public LMDirichletSimilarity() {
        this(2000.0f);
    }
    
    @Override
    protected float score(final BasicStats stats, final float freq, final float docLen) {
        final float score = stats.getBoost() * (float)(Math.log(1.0f + freq / (this.mu * ((LMStats)stats).getCollectionProbability())) + Math.log(this.mu / (docLen + this.mu)));
        return (score > 0.0f) ? score : 0.0f;
    }
    
    @Override
    protected void explain(final List<Explanation> subs, final BasicStats stats, final int doc, final float freq, final float docLen) {
        if (stats.getBoost() != 1.0f) {
            subs.add(Explanation.match(stats.getBoost(), "boost", new Explanation[0]));
        }
        subs.add(Explanation.match(this.mu, "mu", new Explanation[0]));
        final Explanation weightExpl = Explanation.match((float)Math.log(1.0f + freq / (this.mu * ((LMStats)stats).getCollectionProbability())), "term weight", new Explanation[0]);
        subs.add(weightExpl);
        subs.add(Explanation.match((float)Math.log(this.mu / (docLen + this.mu)), "document norm", new Explanation[0]));
        super.explain(subs, stats, doc, freq, docLen);
    }
    
    public float getMu() {
        return this.mu;
    }
    
    @Override
    public String getName() {
        return String.format(Locale.ROOT, "Dirichlet(%f)", this.getMu());
    }
}
