package org.apache.lucene.search;

import java.util.Collections;
import java.util.Collection;
import java.util.HashSet;
import java.util.Arrays;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.index.Term;
import java.io.IOException;
import org.apache.lucene.index.IndexReader;
import java.util.Objects;
import java.util.Set;

public final class BoostQuery extends Query
{
    private static final Set<Class<? extends Query>> NO_PARENS_REQUIRED_QUERIES;
    private final Query query;
    
    public BoostQuery(final Query query, final float boost) {
        this.query = Objects.requireNonNull(query);
        this.setBoost(boost);
    }
    
    public Query getQuery() {
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
        final BoostQuery that = (BoostQuery)obj;
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
        final Query rewritten = this.query.rewrite(reader);
        if (this.getBoost() == 1.0f) {
            return rewritten;
        }
        if (rewritten.getClass() == BoostQuery.class) {
            final BoostQuery in = (BoostQuery)rewritten;
            return new BoostQuery(in.query, this.getBoost() * in.getBoost());
        }
        if (this.getBoost() == 0.0f && rewritten.getClass() != ConstantScoreQuery.class) {
            return new BoostQuery(new ConstantScoreQuery(rewritten), 0.0f);
        }
        if (this.query != rewritten) {
            return new BoostQuery(rewritten, this.getBoost());
        }
        return this;
    }
    
    @Override
    public String toString(final String field) {
        final boolean needsParens = !BoostQuery.NO_PARENS_REQUIRED_QUERIES.contains(this.query.getClass());
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
    public Weight createWeight(final IndexSearcher searcher, final boolean needsScores) throws IOException {
        final Weight weight = this.query.createWeight(searcher, needsScores);
        if (!needsScores) {
            return weight;
        }
        weight.normalize(1.0f, this.getBoost());
        return new Weight(this) {
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
                weight.normalize(norm, BoostQuery.this.getBoost() * boost);
            }
            
            @Override
            public Scorer scorer(final LeafReaderContext context) throws IOException {
                return weight.scorer(context);
            }
            
            @Override
            public BulkScorer bulkScorer(final LeafReaderContext context) throws IOException {
                return weight.bulkScorer(context);
            }
        };
    }
    
    static {
        NO_PARENS_REQUIRED_QUERIES = Collections.unmodifiableSet((Set<? extends Class<? extends Query>>)new HashSet<Class<? extends Query>>(Arrays.asList(MatchAllDocsQuery.class, TermQuery.class, PhraseQuery.class, MultiPhraseQuery.class, ConstantScoreQuery.class, TermRangeQuery.class, NumericRangeQuery.class, PrefixQuery.class, FuzzyQuery.class, WildcardQuery.class, RegexpQuery.class)));
    }
}
