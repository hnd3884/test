package org.apache.lucene.search;

import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.index.Term;
import java.util.Set;
import org.apache.lucene.util.ToStringUtils;
import java.util.List;
import org.apache.lucene.index.IndexReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.Objects;
import java.util.Collection;
import java.util.ArrayList;

public final class DisjunctionMaxQuery extends Query implements Iterable<Query>
{
    private final ArrayList<Query> disjuncts;
    private final float tieBreakerMultiplier;
    
    @Deprecated
    public DisjunctionMaxQuery(final float tieBreakerMultiplier) {
        this.disjuncts = new ArrayList<Query>();
        this.tieBreakerMultiplier = tieBreakerMultiplier;
    }
    
    public DisjunctionMaxQuery(final Collection<Query> disjuncts, final float tieBreakerMultiplier) {
        this.disjuncts = new ArrayList<Query>();
        Objects.requireNonNull(disjuncts, "Collection of Querys must not be null");
        this.tieBreakerMultiplier = tieBreakerMultiplier;
        this.add(disjuncts);
    }
    
    @Deprecated
    public void add(final Query query) {
        this.disjuncts.add(Objects.requireNonNull(query, "Query must not be null"));
    }
    
    @Deprecated
    public void add(final Collection<Query> disjuncts) {
        this.disjuncts.addAll(Objects.requireNonNull(disjuncts, "Query connection must not be null"));
    }
    
    @Override
    public Iterator<Query> iterator() {
        return this.disjuncts.iterator();
    }
    
    public ArrayList<Query> getDisjuncts() {
        return this.disjuncts;
    }
    
    public float getTieBreakerMultiplier() {
        return this.tieBreakerMultiplier;
    }
    
    @Override
    public Weight createWeight(final IndexSearcher searcher, final boolean needsScores) throws IOException {
        return new DisjunctionMaxWeight(searcher, needsScores);
    }
    
    @Override
    public Query rewrite(final IndexReader reader) throws IOException {
        if (this.getBoost() != 1.0f) {
            return super.rewrite(reader);
        }
        final int numDisjunctions = this.disjuncts.size();
        if (numDisjunctions == 1) {
            return this.disjuncts.get(0);
        }
        boolean actuallyRewritten = false;
        final List<Query> rewrittenDisjuncts = new ArrayList<Query>();
        for (final Query sub : this.disjuncts) {
            final Query rewrittenSub = sub.rewrite(reader);
            actuallyRewritten |= (rewrittenSub != sub);
            rewrittenDisjuncts.add(rewrittenSub);
        }
        if (actuallyRewritten) {
            return new DisjunctionMaxQuery(rewrittenDisjuncts, this.tieBreakerMultiplier);
        }
        return super.rewrite(reader);
    }
    
    @Override
    public String toString(final String field) {
        final StringBuilder buffer = new StringBuilder();
        buffer.append("(");
        for (int numDisjunctions = this.disjuncts.size(), i = 0; i < numDisjunctions; ++i) {
            final Query subquery = this.disjuncts.get(i);
            if (subquery instanceof BooleanQuery) {
                buffer.append("(");
                buffer.append(subquery.toString(field));
                buffer.append(")");
            }
            else {
                buffer.append(subquery.toString(field));
            }
            if (i != numDisjunctions - 1) {
                buffer.append(" | ");
            }
        }
        buffer.append(")");
        if (this.tieBreakerMultiplier != 0.0f) {
            buffer.append("~");
            buffer.append(this.tieBreakerMultiplier);
        }
        buffer.append(ToStringUtils.boost(this.getBoost()));
        return buffer.toString();
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof DisjunctionMaxQuery)) {
            return false;
        }
        final DisjunctionMaxQuery other = (DisjunctionMaxQuery)o;
        return super.equals(o) && this.tieBreakerMultiplier == other.tieBreakerMultiplier && this.disjuncts.equals(other.disjuncts);
    }
    
    @Override
    public int hashCode() {
        int h = super.hashCode();
        h = 31 * h + Float.floatToIntBits(this.tieBreakerMultiplier);
        h = 31 * h + this.disjuncts.hashCode();
        return h;
    }
    
    protected class DisjunctionMaxWeight extends Weight
    {
        protected final ArrayList<Weight> weights;
        private final boolean needsScores;
        
        public DisjunctionMaxWeight(final IndexSearcher searcher, final boolean needsScores) throws IOException {
            super(DisjunctionMaxQuery.this);
            this.weights = new ArrayList<Weight>();
            for (final Query disjunctQuery : DisjunctionMaxQuery.this.disjuncts) {
                this.weights.add(searcher.createWeight(disjunctQuery, needsScores));
            }
            this.needsScores = needsScores;
        }
        
        @Override
        public void extractTerms(final Set<Term> terms) {
            for (final Weight weight : this.weights) {
                weight.extractTerms(terms);
            }
        }
        
        @Override
        public float getValueForNormalization() throws IOException {
            float max = 0.0f;
            float sum = 0.0f;
            for (final Weight currentWeight : this.weights) {
                final float sub = currentWeight.getValueForNormalization();
                sum += sub;
                max = Math.max(max, sub);
            }
            return (sum - max) * DisjunctionMaxQuery.this.tieBreakerMultiplier * DisjunctionMaxQuery.this.tieBreakerMultiplier + max;
        }
        
        @Override
        public void normalize(final float norm, final float boost) {
            for (final Weight wt : this.weights) {
                wt.normalize(norm, boost);
            }
        }
        
        @Override
        public Scorer scorer(final LeafReaderContext context) throws IOException {
            final List<Scorer> scorers = new ArrayList<Scorer>();
            for (final Weight w : this.weights) {
                final Scorer subScorer = w.scorer(context);
                if (subScorer != null) {
                    scorers.add(subScorer);
                }
            }
            if (scorers.isEmpty()) {
                return null;
            }
            if (scorers.size() == 1) {
                return scorers.get(0);
            }
            return new DisjunctionMaxScorer(this, DisjunctionMaxQuery.this.tieBreakerMultiplier, scorers, this.needsScores);
        }
        
        @Override
        public Explanation explain(final LeafReaderContext context, final int doc) throws IOException {
            boolean match = false;
            float max = 0.0f;
            float sum = 0.0f;
            final List<Explanation> subs = new ArrayList<Explanation>();
            for (final Weight wt : this.weights) {
                final Explanation e = wt.explain(context, doc);
                if (e.isMatch()) {
                    match = true;
                    subs.add(e);
                    sum += e.getValue();
                    max = Math.max(max, e.getValue());
                }
            }
            if (match) {
                final float score = max + (sum - max) * DisjunctionMaxQuery.this.tieBreakerMultiplier;
                final String desc = (DisjunctionMaxQuery.this.tieBreakerMultiplier == 0.0f) ? "max of:" : ("max plus " + DisjunctionMaxQuery.this.tieBreakerMultiplier + " times others of:");
                return Explanation.match(score, desc, subs);
            }
            return Explanation.noMatch("No matching clause", new Explanation[0]);
        }
    }
}
