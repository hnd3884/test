package org.apache.lucene.search;

import org.apache.lucene.util.Bits;
import org.apache.lucene.util.ToStringUtils;
import java.util.Collections;
import java.util.Collection;
import org.apache.lucene.index.LeafReaderContext;
import java.io.IOException;
import org.apache.lucene.index.IndexReader;
import java.util.Objects;

public final class ConstantScoreQuery extends Query
{
    private final Query query;
    
    public ConstantScoreQuery(final Query query) {
        this.query = Objects.requireNonNull(query, "Query must not be null");
    }
    
    public Query getQuery() {
        return this.query;
    }
    
    @Override
    public Query rewrite(final IndexReader reader) throws IOException {
        if (this.getBoost() != 1.0f) {
            return super.rewrite(reader);
        }
        final Query rewritten = this.query.rewrite(reader);
        if (rewritten != this.query) {
            return new ConstantScoreQuery(rewritten);
        }
        if (rewritten.getClass() == ConstantScoreQuery.class) {
            return rewritten;
        }
        if (rewritten.getClass() == BoostQuery.class) {
            return new ConstantScoreQuery(((BoostQuery)rewritten).getQuery());
        }
        return super.rewrite(reader);
    }
    
    @Override
    public Weight createWeight(final IndexSearcher searcher, final boolean needsScores) throws IOException {
        final Weight innerWeight = searcher.createWeight(this.query, false);
        if (needsScores) {
            return new ConstantScoreWeight(this) {
                @Override
                public BulkScorer bulkScorer(final LeafReaderContext context) throws IOException {
                    final BulkScorer innerScorer = innerWeight.bulkScorer(context);
                    if (innerScorer == null) {
                        return null;
                    }
                    return new ConstantBulkScorer(innerScorer, this, this.score());
                }
                
                @Override
                public Scorer scorer(final LeafReaderContext context) throws IOException {
                    final Scorer innerScorer = innerWeight.scorer(context);
                    if (innerScorer == null) {
                        return null;
                    }
                    final float score = this.score();
                    return new FilterScorer(innerScorer) {
                        @Override
                        public float score() throws IOException {
                            return score;
                        }
                        
                        @Override
                        public int freq() throws IOException {
                            return 1;
                        }
                        
                        @Override
                        public Collection<ChildScorer> getChildren() {
                            return Collections.singleton(new ChildScorer(innerScorer, "constant"));
                        }
                    };
                }
            };
        }
        return innerWeight;
    }
    
    @Override
    public String toString(final String field) {
        return "ConstantScore(" + this.query.toString(field) + ')' + ToStringUtils.boost(this.getBoost());
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!super.equals(o)) {
            return false;
        }
        if (o instanceof ConstantScoreQuery) {
            final ConstantScoreQuery other = (ConstantScoreQuery)o;
            return this.query.equals(other.query);
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return 31 * super.hashCode() + this.query.hashCode();
    }
    
    protected class ConstantBulkScorer extends BulkScorer
    {
        final BulkScorer bulkScorer;
        final Weight weight;
        final float theScore;
        
        public ConstantBulkScorer(final BulkScorer bulkScorer, final Weight weight, final float theScore) {
            this.bulkScorer = bulkScorer;
            this.weight = weight;
            this.theScore = theScore;
        }
        
        @Override
        public int score(final LeafCollector collector, final Bits acceptDocs, final int min, final int max) throws IOException {
            return this.bulkScorer.score(this.wrapCollector(collector), acceptDocs, min, max);
        }
        
        private LeafCollector wrapCollector(final LeafCollector collector) {
            return new FilterLeafCollector(collector) {
                @Override
                public void setScorer(final Scorer scorer) throws IOException {
                    this.in.setScorer(new FilterScorer(scorer) {
                        @Override
                        public float score() throws IOException {
                            return ConstantBulkScorer.this.theScore;
                        }
                        
                        @Override
                        public int freq() throws IOException {
                            return 1;
                        }
                    });
                }
            };
        }
        
        @Override
        public long cost() {
            return this.bulkScorer.cost();
        }
    }
}
