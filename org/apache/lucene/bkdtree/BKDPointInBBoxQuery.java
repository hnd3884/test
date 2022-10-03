package org.apache.lucene.bkdtree;

import org.apache.lucene.util.ToStringUtils;
import org.apache.lucene.search.ConstantScoreQuery;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.index.IndexReader;
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

@Deprecated
public class BKDPointInBBoxQuery extends Query
{
    final String field;
    final double minLat;
    final double maxLat;
    final double minLon;
    final double maxLon;
    
    public BKDPointInBBoxQuery(final String field, final double minLat, final double maxLat, final double minLon, final double maxLon) {
        this.field = field;
        if (!BKDTreeWriter.validLat(minLat)) {
            throw new IllegalArgumentException("minLat=" + minLat + " is not a valid latitude");
        }
        if (!BKDTreeWriter.validLat(maxLat)) {
            throw new IllegalArgumentException("maxLat=" + maxLat + " is not a valid latitude");
        }
        if (!BKDTreeWriter.validLon(minLon)) {
            throw new IllegalArgumentException("minLon=" + minLon + " is not a valid longitude");
        }
        if (!BKDTreeWriter.validLon(maxLon)) {
            throw new IllegalArgumentException("maxLon=" + maxLon + " is not a valid longitude");
        }
        this.minLon = minLon;
        this.maxLon = maxLon;
        this.minLat = minLat;
        this.maxLat = maxLat;
    }
    
    public Weight createWeight(final IndexSearcher searcher, final boolean needsScores) throws IOException {
        return (Weight)new ConstantScoreWeight(this) {
            public Scorer scorer(final LeafReaderContext context) throws IOException {
                final LeafReader reader = context.reader();
                final SortedNumericDocValues sdv = reader.getSortedNumericDocValues(BKDPointInBBoxQuery.this.field);
                if (sdv == null) {
                    return null;
                }
                if (!(sdv instanceof BKDTreeSortedNumericDocValues)) {
                    throw new IllegalStateException("field \"" + BKDPointInBBoxQuery.this.field + "\" was not indexed with BKDTreeDocValuesFormat: got: " + sdv);
                }
                final BKDTreeSortedNumericDocValues treeDV = (BKDTreeSortedNumericDocValues)sdv;
                final BKDTreeReader tree = treeDV.getBKDTreeReader();
                final DocIdSet result = tree.intersect(BKDPointInBBoxQuery.this.minLat, BKDPointInBBoxQuery.this.maxLat, BKDPointInBBoxQuery.this.minLon, BKDPointInBBoxQuery.this.maxLon, null, treeDV.delegate);
                final DocIdSetIterator disi = result.iterator();
                return (Scorer)new ConstantScoreScorer((Weight)this, this.score(), disi);
            }
        };
    }
    
    public Query rewrite(final IndexReader reader) throws IOException {
        if (this.getBoost() != 1.0f) {
            return super.rewrite(reader);
        }
        if (this.maxLon < this.minLon) {
            final BooleanQuery.Builder q = new BooleanQuery.Builder();
            q.setDisableCoord(true);
            final BKDPointInBBoxQuery left = new BKDPointInBBoxQuery(this.field, this.minLat, this.maxLat, -180.0, this.maxLon);
            q.add(new BooleanClause((Query)left, BooleanClause.Occur.SHOULD));
            final BKDPointInBBoxQuery right = new BKDPointInBBoxQuery(this.field, this.minLat, this.maxLat, this.minLon, BKDTreeWriter.MAX_LON_INCL);
            q.add(new BooleanClause((Query)right, BooleanClause.Occur.SHOULD));
            return (Query)new ConstantScoreQuery((Query)q.build());
        }
        return super.rewrite(reader);
    }
    
    public int hashCode() {
        int hash = super.hashCode();
        hash += ((int)Double.doubleToRawLongBits(this.minLat) ^ 0x14FA55FB);
        hash += ((int)Double.doubleToRawLongBits(this.maxLat) ^ 0x733FA5FE);
        hash += ((int)Double.doubleToRawLongBits(this.minLon) ^ 0x14FA55FB);
        hash += ((int)Double.doubleToRawLongBits(this.maxLon) ^ 0x733FA5FE);
        return hash;
    }
    
    public boolean equals(final Object other) {
        if (super.equals(other) && other instanceof BKDPointInBBoxQuery) {
            final BKDPointInBBoxQuery q = (BKDPointInBBoxQuery)other;
            return this.field.equals(q.field) && this.minLat == q.minLat && this.maxLat == q.maxLat && this.minLon == q.minLon && this.maxLon == q.maxLon;
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
        return sb.append(" Lower Left: [").append(this.minLon).append(',').append(this.minLat).append(']').append(" Upper Right: [").append(this.maxLon).append(',').append(this.maxLat).append("]").append(ToStringUtils.boost(this.getBoost())).toString();
    }
}
