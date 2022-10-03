package org.apache.lucene.queries;

import org.apache.lucene.util.ToStringUtils;
import java.util.Objects;
import org.apache.lucene.search.TwoPhaseIterator;
import org.apache.lucene.search.DocIdSetIterator;
import org.apache.lucene.search.FilterScorer;
import org.apache.lucene.search.Scorer;
import org.apache.lucene.search.Explanation;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.index.Term;
import java.util.Set;
import org.apache.lucene.search.Weight;
import org.apache.lucene.search.IndexSearcher;
import java.io.IOException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.Query;

public class BoostingQuery extends Query
{
    private final float boost;
    private final Query match;
    private final Query context;
    
    public BoostingQuery(final Query match, final Query context, final float boost) {
        this.match = match;
        this.context = context;
        this.boost = boost;
    }
    
    public Query rewrite(final IndexReader reader) throws IOException {
        if (this.getBoost() != 1.0f) {
            return super.rewrite(reader);
        }
        final Query matchRewritten = this.match.rewrite(reader);
        final Query contextRewritten = this.context.rewrite(reader);
        if (this.match != matchRewritten || this.context != contextRewritten) {
            return new BoostingQuery(matchRewritten, contextRewritten, this.boost);
        }
        return super.rewrite(reader);
    }
    
    public Weight createWeight(final IndexSearcher searcher, final boolean needsScores) throws IOException {
        if (!needsScores) {
            return this.match.createWeight(searcher, needsScores);
        }
        final Weight matchWeight = searcher.createWeight(this.match, needsScores);
        final Weight contextWeight = searcher.createWeight(this.context, false);
        return new Weight(this) {
            public void extractTerms(final Set<Term> terms) {
                matchWeight.extractTerms((Set)terms);
                if (BoostingQuery.this.boost >= 1.0f) {
                    contextWeight.extractTerms((Set)terms);
                }
            }
            
            public Explanation explain(final LeafReaderContext context, final int doc) throws IOException {
                final Explanation matchExplanation = matchWeight.explain(context, doc);
                final Explanation contextExplanation = contextWeight.explain(context, doc);
                if (!matchExplanation.isMatch() || !contextExplanation.isMatch()) {
                    return matchExplanation;
                }
                return Explanation.match(matchExplanation.getValue() * BoostingQuery.this.boost, "product of:", new Explanation[] { matchExplanation, Explanation.match(BoostingQuery.this.boost, "boost", new Explanation[0]) });
            }
            
            public float getValueForNormalization() throws IOException {
                return matchWeight.getValueForNormalization();
            }
            
            public void normalize(final float norm, final float boost) {
                matchWeight.normalize(norm, boost);
            }
            
            public Scorer scorer(final LeafReaderContext context) throws IOException {
                final Scorer matchScorer = matchWeight.scorer(context);
                if (matchScorer == null) {
                    return null;
                }
                final Scorer contextScorer = contextWeight.scorer(context);
                if (contextScorer == null) {
                    return matchScorer;
                }
                final TwoPhaseIterator contextTwoPhase = contextScorer.twoPhaseIterator();
                final DocIdSetIterator contextApproximation = (contextTwoPhase == null) ? contextScorer.iterator() : contextTwoPhase.approximation();
                return (Scorer)new FilterScorer(matchScorer) {
                    public float score() throws IOException {
                        if (contextApproximation.docID() < this.docID()) {
                            contextApproximation.advance(this.docID());
                        }
                        assert contextApproximation.docID() >= this.docID();
                        float score = super.score();
                        if (contextApproximation.docID() == this.docID() && (contextTwoPhase == null || contextTwoPhase.matches())) {
                            score *= BoostingQuery.this.boost;
                        }
                        return score;
                    }
                };
            }
        };
    }
    
    public int hashCode() {
        return 31 * super.hashCode() + Objects.hash(this.match, this.context, this.boost);
    }
    
    public boolean equals(final Object obj) {
        if (!super.equals(obj)) {
            return false;
        }
        final BoostingQuery that = (BoostingQuery)obj;
        return this.match.equals((Object)that.match) && this.context.equals((Object)that.context) && Float.floatToIntBits(this.boost) == Float.floatToIntBits(that.boost);
    }
    
    public String toString(final String field) {
        return this.match.toString(field) + "/" + this.context.toString(field) + ToStringUtils.boost(this.getBoost());
    }
}
