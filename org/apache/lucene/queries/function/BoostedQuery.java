package org.apache.lucene.queries.function;

import java.util.Collections;
import java.util.Collection;
import org.apache.lucene.search.FilterScorer;
import org.apache.lucene.search.Explanation;
import org.apache.lucene.search.Scorer;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.index.Term;
import java.util.Set;
import java.util.Map;
import org.apache.lucene.util.ToStringUtils;
import org.apache.lucene.search.Weight;
import org.apache.lucene.search.IndexSearcher;
import java.io.IOException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.Query;

public final class BoostedQuery extends Query
{
    private final Query q;
    private final ValueSource boostVal;
    
    public BoostedQuery(final Query subQuery, final ValueSource boostVal) {
        this.q = subQuery;
        this.boostVal = boostVal;
    }
    
    public Query getQuery() {
        return this.q;
    }
    
    public ValueSource getValueSource() {
        return this.boostVal;
    }
    
    public Query rewrite(final IndexReader reader) throws IOException {
        if (this.getBoost() != 1.0f) {
            return super.rewrite(reader);
        }
        final Query newQ = this.q.rewrite(reader);
        if (newQ != this.q) {
            return new BoostedQuery(newQ, this.boostVal);
        }
        return super.rewrite(reader);
    }
    
    public Weight createWeight(final IndexSearcher searcher, final boolean needsScores) throws IOException {
        return new BoostedWeight(searcher, needsScores);
    }
    
    public String toString(final String field) {
        final StringBuilder sb = new StringBuilder();
        sb.append("boost(").append(this.q.toString(field)).append(',').append(this.boostVal).append(')');
        sb.append(ToStringUtils.boost(this.getBoost()));
        return sb.toString();
    }
    
    public boolean equals(final Object o) {
        if (!super.equals(o)) {
            return false;
        }
        final BoostedQuery other = (BoostedQuery)o;
        return this.q.equals((Object)other.q) && this.boostVal.equals(other.boostVal);
    }
    
    public int hashCode() {
        int h = super.hashCode();
        h = 31 * h + this.q.hashCode();
        h = 31 * h + this.boostVal.hashCode();
        return h;
    }
    
    private class BoostedWeight extends Weight
    {
        Weight qWeight;
        Map fcontext;
        
        public BoostedWeight(final IndexSearcher searcher, final boolean needsScores) throws IOException {
            super((Query)BoostedQuery.this);
            this.qWeight = searcher.createWeight(BoostedQuery.this.q, needsScores);
            this.fcontext = ValueSource.newContext(searcher);
            BoostedQuery.this.boostVal.createWeight(this.fcontext, searcher);
        }
        
        public void extractTerms(final Set<Term> terms) {
            this.qWeight.extractTerms((Set)terms);
        }
        
        public float getValueForNormalization() throws IOException {
            return this.qWeight.getValueForNormalization();
        }
        
        public void normalize(final float norm, final float boost) {
            this.qWeight.normalize(norm, boost);
        }
        
        public Scorer scorer(final LeafReaderContext context) throws IOException {
            final Scorer subQueryScorer = this.qWeight.scorer(context);
            if (subQueryScorer == null) {
                return null;
            }
            return (Scorer)new CustomScorer(context, this, subQueryScorer, BoostedQuery.this.boostVal);
        }
        
        public Explanation explain(final LeafReaderContext readerContext, final int doc) throws IOException {
            final Explanation subQueryExpl = this.qWeight.explain(readerContext, doc);
            if (!subQueryExpl.isMatch()) {
                return subQueryExpl;
            }
            final FunctionValues vals = BoostedQuery.this.boostVal.getValues(this.fcontext, readerContext);
            final float sc = subQueryExpl.getValue() * vals.floatVal(doc);
            return Explanation.match(sc, BoostedQuery.this.toString() + ", product of:", new Explanation[] { subQueryExpl, vals.explain(doc) });
        }
    }
    
    private class CustomScorer extends FilterScorer
    {
        private final BoostedWeight weight;
        private final FunctionValues vals;
        private final LeafReaderContext readerContext;
        
        private CustomScorer(final LeafReaderContext readerContext, final BoostedWeight w, final Scorer scorer, final ValueSource vs) throws IOException {
            super(scorer);
            this.weight = w;
            this.readerContext = readerContext;
            this.vals = vs.getValues(this.weight.fcontext, readerContext);
        }
        
        public float score() throws IOException {
            final float score = this.in.score() * this.vals.floatVal(this.in.docID());
            return (score > Float.NEGATIVE_INFINITY) ? score : -3.4028235E38f;
        }
        
        public Collection<Scorer.ChildScorer> getChildren() {
            return Collections.singleton(new Scorer.ChildScorer(this.in, "CUSTOM"));
        }
        
        public Explanation explain(final int doc) throws IOException {
            final Explanation subQueryExpl = this.weight.qWeight.explain(this.readerContext, doc);
            if (!subQueryExpl.isMatch()) {
                return subQueryExpl;
            }
            final float sc = subQueryExpl.getValue() * this.vals.floatVal(doc);
            return Explanation.match(sc, BoostedQuery.this.toString() + ", product of:", new Explanation[] { subQueryExpl, this.vals.explain(doc) });
        }
    }
}
