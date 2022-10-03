package org.apache.lucene.search.spans;

import java.util.Collections;
import java.util.Collection;
import java.util.HashSet;
import java.util.Arrays;
import org.apache.lucene.search.Weight;
import org.apache.lucene.search.Scorer;
import org.apache.lucene.search.Explanation;
import org.apache.lucene.index.LeafReaderContext;
import java.util.Map;
import org.apache.lucene.index.TermContext;
import org.apache.lucene.index.Term;
import java.util.TreeMap;
import org.apache.lucene.search.IndexSearcher;
import java.io.IOException;
import org.apache.lucene.search.Query;
import org.apache.lucene.index.IndexReader;
import java.util.Objects;
import java.util.Set;

public final class SpanBoostQuery extends SpanQuery
{
    private static final Set<Class<? extends SpanQuery>> NO_PARENS_REQUIRED_QUERIES;
    private final SpanQuery query;
    
    public SpanBoostQuery(final SpanQuery query, final float boost) {
        this.query = Objects.requireNonNull(query);
        this.setBoost(boost);
    }
    
    public SpanQuery getQuery() {
        return this.query;
    }
    
    @Override
    public float getBoost() {
        return super.getBoost();
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (!super.equals(obj)) {
            return false;
        }
        final SpanBoostQuery that = (SpanBoostQuery)obj;
        return this.query.equals(that.query);
    }
    
    @Override
    public int hashCode() {
        int h = super.hashCode();
        h = 31 * h + this.query.hashCode();
        return h;
    }
    
    @Override
    public Query rewrite(final IndexReader reader) throws IOException {
        final SpanQuery rewritten = (SpanQuery)this.query.rewrite(reader);
        if (this.getBoost() == 1.0f) {
            return rewritten;
        }
        if (rewritten.getClass() == SpanBoostQuery.class) {
            final SpanBoostQuery in = (SpanBoostQuery)rewritten;
            return new SpanBoostQuery(in.query, this.getBoost() * in.getBoost());
        }
        if (this.query != rewritten) {
            return new SpanBoostQuery(rewritten, this.getBoost());
        }
        return this;
    }
    
    @Override
    public String toString(final String field) {
        final boolean needsParens = !SpanBoostQuery.NO_PARENS_REQUIRED_QUERIES.contains(this.query.getClass());
        final StringBuilder builder = new StringBuilder();
        if (needsParens) {
            builder.append("(");
        }
        builder.append(this.query.toString(field));
        if (needsParens) {
            builder.append(")");
        }
        builder.append("^");
        builder.append(this.getBoost());
        return builder.toString();
    }
    
    @Override
    public String getField() {
        return this.query.getField();
    }
    
    @Override
    public SpanWeight createWeight(final IndexSearcher searcher, final boolean needsScores) throws IOException {
        final SpanWeight weight = this.query.createWeight(searcher, needsScores);
        if (!needsScores) {
            return weight;
        }
        final Map<Term, TermContext> terms = new TreeMap<Term, TermContext>();
        weight.extractTermContexts(terms);
        weight.normalize(1.0f, this.getBoost());
        return new SpanWeight(this, searcher, terms) {
            @Override
            public void extractTerms(final Set<Term> terms) {
                weight.extractTerms(terms);
            }
            
            @Override
            public Explanation explain(final LeafReaderContext context, final int doc) throws IOException {
                return weight.explain(context, doc);
            }
            
            @Override
            public float getValueForNormalization() throws IOException {
                return weight.getValueForNormalization();
            }
            
            @Override
            public void normalize(final float norm, final float boost) {
                weight.normalize(norm, SpanBoostQuery.this.getBoost() * boost);
            }
            
            @Override
            public Spans getSpans(final LeafReaderContext ctx, final Postings requiredPostings) throws IOException {
                return weight.getSpans(ctx, requiredPostings);
            }
            
            @Override
            public SpanScorer scorer(final LeafReaderContext context) throws IOException {
                return weight.scorer(context);
            }
            
            @Override
            public void extractTermContexts(final Map<Term, TermContext> contexts) {
                weight.extractTermContexts(contexts);
            }
        };
    }
    
    static {
        NO_PARENS_REQUIRED_QUERIES = Collections.unmodifiableSet((Set<? extends Class<? extends SpanQuery>>)new HashSet<Class<? extends SpanQuery>>(Arrays.asList(SpanTermQuery.class, SpanNearQuery.class, SpanOrQuery.class, SpanFirstQuery.class, SpanContainingQuery.class, SpanContainQuery.class, SpanNotQuery.class, SpanWithinQuery.class)));
    }
}
