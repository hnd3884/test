package org.apache.lucene.search;

import org.apache.lucene.index.LeafReaderContext;
import java.io.IOException;
import org.apache.lucene.index.Term;
import java.util.Set;

public abstract class ConstantScoreWeight extends Weight
{
    private float boost;
    private float queryNorm;
    private float queryWeight;
    
    protected ConstantScoreWeight(final Query query) {
        super(query);
        this.normalize(1.0f, 1.0f);
    }
    
    @Override
    public void extractTerms(final Set<Term> terms) {
    }
    
    @Override
    public final float getValueForNormalization() throws IOException {
        return this.queryWeight * this.queryWeight;
    }
    
    @Override
    public void normalize(final float norm, final float boost) {
        this.boost = boost;
        this.queryNorm = norm;
        this.queryWeight = this.queryNorm * boost;
    }
    
    protected final float queryNorm() {
        return this.queryNorm;
    }
    
    protected final float boost() {
        return this.boost;
    }
    
    protected final float score() {
        return this.queryWeight;
    }
    
    @Override
    public Explanation explain(final LeafReaderContext context, final int doc) throws IOException {
        final Scorer s = this.scorer(context);
        boolean exists;
        if (s == null) {
            exists = false;
        }
        else {
            final TwoPhaseIterator twoPhase = s.twoPhaseIterator();
            if (twoPhase == null) {
                exists = (s.iterator().advance(doc) == doc);
            }
            else {
                exists = (twoPhase.approximation().advance(doc) == doc && twoPhase.matches());
            }
        }
        if (exists) {
            return Explanation.match(this.queryWeight, this.getQuery().toString() + ", product of:", Explanation.match(this.boost, "boost", new Explanation[0]), Explanation.match(this.queryNorm, "queryNorm", new Explanation[0]));
        }
        return Explanation.noMatch(this.getQuery().toString() + " doesn't match id " + doc, new Explanation[0]);
    }
}
