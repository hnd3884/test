package org.apache.lucene.rangetree;

import org.apache.lucene.util.ToStringUtils;
import java.io.IOException;
import org.apache.lucene.search.DocIdSetIterator;
import org.apache.lucene.search.DocIdSet;
import org.apache.lucene.index.SortedNumericDocValues;
import org.apache.lucene.index.LeafReader;
import org.apache.lucene.search.ConstantScoreScorer;
import org.apache.lucene.search.Scorer;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.search.ConstantScoreWeight;
import org.apache.lucene.search.Weight;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;

public class NumericRangeTreeQuery extends Query
{
    final String field;
    final Long minValue;
    final Long maxValue;
    final boolean minInclusive;
    final boolean maxInclusive;
    
    public NumericRangeTreeQuery(final String field, final Long minValue, final boolean minInclusive, final Long maxValue, final boolean maxInclusive) {
        this.field = field;
        this.minInclusive = minInclusive;
        this.minValue = minValue;
        this.maxInclusive = maxInclusive;
        this.maxValue = maxValue;
    }
    
    public Weight createWeight(final IndexSearcher searcher, final boolean needsScores) throws IOException {
        return (Weight)new ConstantScoreWeight(this) {
            public Scorer scorer(final LeafReaderContext context) throws IOException {
                final LeafReader reader = context.reader();
                final SortedNumericDocValues sdv = reader.getSortedNumericDocValues(NumericRangeTreeQuery.this.field);
                if (sdv == null) {
                    return null;
                }
                if (!(sdv instanceof RangeTreeSortedNumericDocValues)) {
                    throw new IllegalStateException("field \"" + NumericRangeTreeQuery.this.field + "\" was not indexed with RangeTreeDocValuesFormat: got: " + sdv);
                }
                final RangeTreeSortedNumericDocValues treeDV = (RangeTreeSortedNumericDocValues)sdv;
                final RangeTreeReader tree = treeDV.getRangeTreeReader();
                long minBoundIncl = (NumericRangeTreeQuery.this.minValue == null) ? Long.MIN_VALUE : NumericRangeTreeQuery.this.minValue;
                if (!NumericRangeTreeQuery.this.minInclusive && NumericRangeTreeQuery.this.minValue != null) {
                    if (minBoundIncl == Long.MAX_VALUE) {
                        return null;
                    }
                    ++minBoundIncl;
                }
                long maxBoundIncl = (NumericRangeTreeQuery.this.maxValue == null) ? Long.MAX_VALUE : NumericRangeTreeQuery.this.maxValue;
                if (!NumericRangeTreeQuery.this.maxInclusive && NumericRangeTreeQuery.this.maxValue != null) {
                    if (maxBoundIncl == Long.MIN_VALUE) {
                        return null;
                    }
                    --maxBoundIncl;
                }
                if (maxBoundIncl < minBoundIncl) {
                    return null;
                }
                final DocIdSet result = tree.intersect(minBoundIncl, maxBoundIncl, treeDV.delegate, context.reader().maxDoc());
                final DocIdSetIterator disi = result.iterator();
                return (Scorer)new ConstantScoreScorer((Weight)this, this.score(), disi);
            }
        };
    }
    
    public int hashCode() {
        int hash = super.hashCode();
        if (this.minValue != null) {
            hash += (this.minValue.hashCode() ^ 0x14FA55FB);
        }
        if (this.maxValue != null) {
            hash += (this.maxValue.hashCode() ^ 0x733FA5FE);
        }
        return hash + (Boolean.valueOf(this.minInclusive).hashCode() ^ 0x14FA55FB) + (Boolean.valueOf(this.maxInclusive).hashCode() ^ 0x733FA5FE);
    }
    
    public boolean equals(final Object other) {
        if (super.equals(other)) {
            final NumericRangeTreeQuery q = (NumericRangeTreeQuery)other;
            if (q.minValue == null) {
                if (this.minValue != null) {
                    return false;
                }
            }
            else if (!q.minValue.equals(this.minValue)) {
                return false;
            }
            if (q.maxValue == null) {
                if (this.maxValue != null) {
                    return false;
                }
            }
            else if (!q.maxValue.equals(this.maxValue)) {
                return false;
            }
            if (this.minInclusive == q.minInclusive && this.maxInclusive == q.maxInclusive) {
                return true;
            }
            return false;
        }
        return false;
    }
    
    public String toString(final String field) {
        final StringBuilder sb = new StringBuilder();
        sb.append(this.getClass().getSimpleName());
        sb.append(':');
        if (!this.field.equals(field)) {
            sb.append("field=");
            sb.append(this.field);
            sb.append(':');
        }
        return sb.append(this.minInclusive ? '[' : '{').append((this.minValue == null) ? "*" : this.minValue.toString()).append(" TO ").append((this.maxValue == null) ? "*" : this.maxValue.toString()).append(this.maxInclusive ? ']' : '}').append(ToStringUtils.boost(this.getBoost())).toString();
    }
}
