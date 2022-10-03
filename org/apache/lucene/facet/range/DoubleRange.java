package org.apache.lucene.facet.range;

import org.apache.lucene.search.ConstantScoreScorer;
import org.apache.lucene.queries.function.FunctionValues;
import org.apache.lucene.search.TwoPhaseIterator;
import java.util.Map;
import java.util.Collections;
import org.apache.lucene.search.DocIdSetIterator;
import org.apache.lucene.search.Scorer;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.search.ConstantScoreWeight;
import org.apache.lucene.search.Weight;
import org.apache.lucene.search.IndexSearcher;
import java.io.IOException;
import org.apache.lucene.index.IndexReader;
import java.util.Objects;
import org.apache.lucene.queries.function.ValueSource;
import org.apache.lucene.search.Query;
import org.apache.lucene.util.NumericUtils;

public final class DoubleRange extends Range
{
    final double minIncl;
    final double maxIncl;
    public final double min;
    public final double max;
    public final boolean minInclusive;
    public final boolean maxInclusive;
    
    public DoubleRange(final String label, double minIn, final boolean minInclusive, double maxIn, final boolean maxInclusive) {
        super(label);
        this.min = minIn;
        this.max = maxIn;
        this.minInclusive = minInclusive;
        this.maxInclusive = maxInclusive;
        if (Double.isNaN(this.min)) {
            throw new IllegalArgumentException("min cannot be NaN");
        }
        if (!minInclusive) {
            minIn = Math.nextUp(minIn);
        }
        if (Double.isNaN(this.max)) {
            throw new IllegalArgumentException("max cannot be NaN");
        }
        if (!maxInclusive) {
            maxIn = Math.nextAfter(maxIn, Double.NEGATIVE_INFINITY);
        }
        if (minIn > maxIn) {
            this.failNoMatch();
        }
        this.minIncl = minIn;
        this.maxIncl = maxIn;
    }
    
    public boolean accept(final double value) {
        return value >= this.minIncl && value <= this.maxIncl;
    }
    
    LongRange toLongRange() {
        return new LongRange(this.label, NumericUtils.doubleToSortableLong(this.minIncl), true, NumericUtils.doubleToSortableLong(this.maxIncl), true);
    }
    
    @Override
    public String toString() {
        return "DoubleRange(" + this.minIncl + " to " + this.maxIncl + ")";
    }
    
    @Override
    public Query getQuery(final Query fastMatchQuery, final ValueSource valueSource) {
        return new ValueSourceQuery(this, fastMatchQuery, valueSource);
    }
    
    private static class ValueSourceQuery extends Query
    {
        private final DoubleRange range;
        private final Query fastMatchQuery;
        private final ValueSource valueSource;
        
        ValueSourceQuery(final DoubleRange range, final Query fastMatchQuery, final ValueSource valueSource) {
            this.range = range;
            this.fastMatchQuery = fastMatchQuery;
            this.valueSource = valueSource;
        }
        
        public boolean equals(final Object obj) {
            if (!super.equals(obj)) {
                return false;
            }
            final ValueSourceQuery other = (ValueSourceQuery)obj;
            return this.range.equals(other.range) && Objects.equals(this.fastMatchQuery, other.fastMatchQuery) && this.valueSource.equals((Object)other.valueSource);
        }
        
        public int hashCode() {
            return 31 * Objects.hash(this.range, this.fastMatchQuery, this.valueSource) + super.hashCode();
        }
        
        public String toString(final String field) {
            return "Filter(" + this.range.toString() + ")";
        }
        
        public Query rewrite(final IndexReader reader) throws IOException {
            if (this.getBoost() != 1.0f) {
                return super.rewrite(reader);
            }
            if (this.fastMatchQuery != null) {
                final Query fastMatchRewritten = this.fastMatchQuery.rewrite(reader);
                if (fastMatchRewritten != this.fastMatchQuery) {
                    return new ValueSourceQuery(this.range, fastMatchRewritten, this.valueSource);
                }
            }
            return super.rewrite(reader);
        }
        
        public Weight createWeight(final IndexSearcher searcher, final boolean needsScores) throws IOException {
            final Weight fastMatchWeight = (this.fastMatchQuery == null) ? null : searcher.createWeight(this.fastMatchQuery, false);
            return (Weight)new ConstantScoreWeight(this) {
                public Scorer scorer(final LeafReaderContext context) throws IOException {
                    final int maxDoc = context.reader().maxDoc();
                    DocIdSetIterator approximation;
                    if (fastMatchWeight == null) {
                        approximation = DocIdSetIterator.all(maxDoc);
                    }
                    else {
                        final Scorer s = fastMatchWeight.scorer(context);
                        if (s == null) {
                            return null;
                        }
                        approximation = s.iterator();
                    }
                    final FunctionValues values = ValueSourceQuery.this.valueSource.getValues((Map)Collections.emptyMap(), context);
                    final TwoPhaseIterator twoPhase = new TwoPhaseIterator(approximation) {
                        public boolean matches() throws IOException {
                            return ValueSourceQuery.this.range.accept(values.doubleVal(this.approximation.docID()));
                        }
                        
                        public float matchCost() {
                            return 100.0f;
                        }
                    };
                    return (Scorer)new ConstantScoreScorer((Weight)this, this.score(), twoPhase);
                }
            };
        }
    }
}
