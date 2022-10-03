package org.apache.lucene.search;

import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.index.Term;
import java.util.Set;
import org.apache.lucene.util.Bits;
import org.apache.lucene.util.ToStringUtils;
import java.io.IOException;
import org.apache.lucene.index.IndexReader;
import java.util.Objects;

@Deprecated
public class FilteredQuery extends Query
{
    private final Query query;
    private final Filter filter;
    private final FilterStrategy strategy;
    public static final FilterStrategy RANDOM_ACCESS_FILTER_STRATEGY;
    public static final FilterStrategy LEAP_FROG_FILTER_FIRST_STRATEGY;
    public static final FilterStrategy LEAP_FROG_QUERY_FIRST_STRATEGY;
    public static final FilterStrategy QUERY_FIRST_FILTER_STRATEGY;
    
    public FilteredQuery(final Query query, final Filter filter) {
        this(query, filter, FilteredQuery.RANDOM_ACCESS_FILTER_STRATEGY);
    }
    
    public FilteredQuery(final Query query, final Filter filter, final FilterStrategy strategy) {
        this.strategy = Objects.requireNonNull(strategy, "FilterStrategy must not be null");
        this.query = Objects.requireNonNull(query, "Query must not be null");
        this.filter = Objects.requireNonNull(filter, "Filter must not be null");
    }
    
    @Override
    public Query rewrite(final IndexReader reader) throws IOException {
        if (this.getBoost() != 1.0f) {
            return super.rewrite(reader);
        }
        final BooleanQuery.Builder builder = new BooleanQuery.Builder();
        builder.add(this.query, BooleanClause.Occur.MUST);
        builder.add(this.strategy.rewrite(this.filter), BooleanClause.Occur.FILTER);
        return builder.build();
    }
    
    public final Query getQuery() {
        return this.query;
    }
    
    public final Filter getFilter() {
        return this.filter;
    }
    
    public FilterStrategy getFilterStrategy() {
        return this.strategy;
    }
    
    @Override
    public String toString(final String s) {
        final StringBuilder buffer = new StringBuilder();
        buffer.append("filtered(");
        buffer.append(this.query.toString(s));
        buffer.append(")->");
        buffer.append(this.filter);
        buffer.append(ToStringUtils.boost(this.getBoost()));
        return buffer.toString();
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (!super.equals(o)) {
            return false;
        }
        assert o instanceof FilteredQuery;
        final FilteredQuery fq = (FilteredQuery)o;
        return fq.query.equals(this.query) && fq.filter.equals(this.filter) && fq.strategy.equals(this.strategy);
    }
    
    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash = hash * 31 + this.strategy.hashCode();
        hash = hash * 31 + this.query.hashCode();
        hash = hash * 31 + this.filter.hashCode();
        return hash;
    }
    
    static {
        RANDOM_ACCESS_FILTER_STRATEGY = new RandomAccessFilterStrategy();
        LEAP_FROG_FILTER_FIRST_STRATEGY = new RandomAccessFilterStrategy() {
            @Override
            protected boolean useRandomAccess(final Bits bits, final long filterCost) {
                return false;
            }
        };
        LEAP_FROG_QUERY_FIRST_STRATEGY = FilteredQuery.LEAP_FROG_FILTER_FIRST_STRATEGY;
        QUERY_FIRST_FILTER_STRATEGY = new RandomAccessFilterStrategy() {
            @Override
            boolean alwaysUseRandomAccess() {
                return true;
            }
        };
    }
    
    public abstract static class FilterStrategy
    {
        public abstract Query rewrite(final Filter p0);
    }
    
    public static class RandomAccessFilterStrategy extends FilterStrategy
    {
        @Override
        public Query rewrite(final Filter filter) {
            return new RandomAccessFilterWrapperQuery(filter, this);
        }
        
        protected boolean useRandomAccess(final Bits bits, final long filterCost) {
            return filterCost * 100L > bits.length();
        }
        
        boolean alwaysUseRandomAccess() {
            return false;
        }
    }
    
    private static class RandomAccessFilterWrapperQuery extends Query
    {
        final Filter filter;
        final RandomAccessFilterStrategy strategy;
        
        private RandomAccessFilterWrapperQuery(final Filter filter, final RandomAccessFilterStrategy strategy) {
            this.filter = Objects.requireNonNull(filter);
            this.strategy = Objects.requireNonNull(strategy);
        }
        
        @Override
        public boolean equals(final Object obj) {
            if (!super.equals(obj)) {
                return false;
            }
            final RandomAccessFilterWrapperQuery that = (RandomAccessFilterWrapperQuery)obj;
            return this.filter.equals(that.filter) && this.strategy.equals(that.strategy);
        }
        
        @Override
        public int hashCode() {
            return 31 * super.hashCode() + Objects.hash(this.filter, this.strategy);
        }
        
        @Override
        public String toString(final String field) {
            return this.filter.toString(field);
        }
        
        @Override
        public Weight createWeight(final IndexSearcher searcher, final boolean needsScores) throws IOException {
            return new Weight(this) {
                @Override
                public void extractTerms(final Set<Term> terms) {
                }
                
                @Override
                public float getValueForNormalization() throws IOException {
                    return 0.0f;
                }
                
                @Override
                public void normalize(final float norm, final float topLevelBoost) {
                }
                
                @Override
                public Explanation explain(final LeafReaderContext context, final int doc) throws IOException {
                    final Scorer s = this.scorer(context);
                    boolean match;
                    if (s == null) {
                        match = false;
                    }
                    else {
                        final TwoPhaseIterator twoPhase = s.twoPhaseIterator();
                        if (twoPhase == null) {
                            match = (s.iterator().advance(doc) == doc);
                        }
                        else {
                            match = (twoPhase.approximation().advance(doc) == doc && twoPhase.matches());
                        }
                    }
                    if (!match) {
                        return Explanation.match(0.0f, "No match on id " + doc, new Explanation[0]);
                    }
                    assert s.score() == 0.0f;
                    return Explanation.match(0.0f, "Match on id " + doc, new Explanation[0]);
                }
                
                @Override
                public Scorer scorer(final LeafReaderContext context) throws IOException {
                    final DocIdSet set = RandomAccessFilterWrapperQuery.this.filter.getDocIdSet(context, null);
                    if (set == null) {
                        return null;
                    }
                    final Bits bits = set.bits();
                    boolean useRandomAccess = bits != null && RandomAccessFilterWrapperQuery.this.strategy.alwaysUseRandomAccess();
                    DocIdSetIterator iterator;
                    if (useRandomAccess) {
                        iterator = null;
                    }
                    else {
                        iterator = set.iterator();
                        if (iterator == null) {
                            return null;
                        }
                        if (bits != null) {
                            useRandomAccess = RandomAccessFilterWrapperQuery.this.strategy.useRandomAccess(bits, iterator.cost());
                        }
                    }
                    if (useRandomAccess) {
                        final DocIdSetIterator approximation = DocIdSetIterator.all(context.reader().maxDoc());
                        final TwoPhaseIterator twoPhase = new TwoPhaseIterator(approximation) {
                            @Override
                            public boolean matches() throws IOException {
                                final int doc = this.approximation.docID();
                                return bits.get(doc);
                            }
                            
                            @Override
                            public float matchCost() {
                                return 10.0f;
                            }
                        };
                        return new ConstantScoreScorer(this, 0.0f, twoPhase);
                    }
                    return new ConstantScoreScorer(this, 0.0f, iterator);
                }
            };
        }
    }
}
