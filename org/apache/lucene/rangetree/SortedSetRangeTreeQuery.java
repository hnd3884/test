package org.apache.lucene.rangetree;

import org.apache.lucene.util.ToStringUtils;
import java.io.IOException;
import org.apache.lucene.search.DocIdSetIterator;
import org.apache.lucene.search.DocIdSet;
import org.apache.lucene.index.LeafReader;
import org.apache.lucene.search.ConstantScoreScorer;
import org.apache.lucene.util.ArrayUtil;
import org.apache.lucene.index.SortedSetDocValues;
import org.apache.lucene.index.SortedNumericDocValues;
import org.apache.lucene.search.Scorer;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.search.ConstantScoreWeight;
import org.apache.lucene.search.Weight;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.search.Query;

public class SortedSetRangeTreeQuery extends Query
{
    final String field;
    final BytesRef minValue;
    final BytesRef maxValue;
    final boolean minInclusive;
    final boolean maxInclusive;
    
    public SortedSetRangeTreeQuery(final String field, final BytesRef minValue, final boolean minInclusive, final BytesRef maxValue, final boolean maxInclusive) {
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
                final SortedSetDocValues ssdv = reader.getSortedSetDocValues(SortedSetRangeTreeQuery.this.field);
                if (ssdv == null) {
                    return null;
                }
                if (!(ssdv instanceof RangeTreeSortedSetDocValues)) {
                    throw new IllegalStateException("field \"" + SortedSetRangeTreeQuery.this.field + "\" was not indexed with RangeTreeDocValuesFormat: got: " + ssdv);
                }
                final RangeTreeSortedSetDocValues treeDV = (RangeTreeSortedSetDocValues)ssdv;
                final RangeTreeReader tree = treeDV.getRangeTreeReader();
                long minOrdIncl;
                if (SortedSetRangeTreeQuery.this.minValue == null) {
                    minOrdIncl = 0L;
                }
                else {
                    final long ord = ssdv.lookupTerm(SortedSetRangeTreeQuery.this.minValue);
                    if (ord >= 0L) {
                        if (SortedSetRangeTreeQuery.this.minInclusive) {
                            minOrdIncl = ord;
                        }
                        else {
                            minOrdIncl = ord + 1L;
                        }
                    }
                    else {
                        minOrdIncl = -ord - 1L;
                    }
                }
                long maxOrdIncl;
                if (SortedSetRangeTreeQuery.this.maxValue == null) {
                    maxOrdIncl = Long.MAX_VALUE;
                }
                else {
                    final long ord2 = ssdv.lookupTerm(SortedSetRangeTreeQuery.this.maxValue);
                    if (ord2 >= 0L) {
                        if (SortedSetRangeTreeQuery.this.maxInclusive) {
                            maxOrdIncl = ord2;
                        }
                        else {
                            maxOrdIncl = ord2 - 1L;
                        }
                    }
                    else {
                        maxOrdIncl = -ord2 - 2L;
                    }
                }
                if (maxOrdIncl < minOrdIncl) {
                    return null;
                }
                final SortedNumericDocValues ords = new SortedNumericDocValues() {
                    private long[] ords = new long[2];
                    private int count;
                    
                    public void setDocument(final int doc) {
                        ssdv.setDocument(doc);
                        this.count = 0;
                        long ord;
                        while ((ord = ssdv.nextOrd()) != -1L) {
                            if (this.count == this.ords.length) {
                                this.ords = ArrayUtil.grow(this.ords, this.count + 1);
                            }
                            this.ords[this.count++] = ord;
                        }
                    }
                    
                    public int count() {
                        return this.count;
                    }
                    
                    public long valueAt(final int index) {
                        return this.ords[index];
                    }
                };
                final DocIdSet result = tree.intersect(minOrdIncl, maxOrdIncl, ords, context.reader().maxDoc());
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
            final SortedSetRangeTreeQuery q = (SortedSetRangeTreeQuery)other;
            if (q.minValue == null) {
                if (this.minValue != null) {
                    return false;
                }
            }
            else if (!q.minValue.equals((Object)this.minValue)) {
                return false;
            }
            if (q.maxValue == null) {
                if (this.maxValue != null) {
                    return false;
                }
            }
            else if (!q.maxValue.equals((Object)this.maxValue)) {
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
