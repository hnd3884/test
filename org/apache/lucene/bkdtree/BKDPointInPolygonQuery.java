package org.apache.lucene.bkdtree;

import java.util.Arrays;
import java.io.IOException;
import org.apache.lucene.search.DocIdSetIterator;
import org.apache.lucene.search.DocIdSet;
import org.apache.lucene.index.SortedNumericDocValues;
import org.apache.lucene.index.LeafReader;
import org.apache.lucene.search.ConstantScoreScorer;
import org.apache.lucene.spatial.util.GeoRelationUtils;
import org.apache.lucene.search.Scorer;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.search.ConstantScoreWeight;
import org.apache.lucene.search.Weight;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;

@Deprecated
public class BKDPointInPolygonQuery extends Query
{
    final String field;
    final double minLat;
    final double maxLat;
    final double minLon;
    final double maxLon;
    final double[] polyLats;
    final double[] polyLons;
    
    public BKDPointInPolygonQuery(final String field, final double[] polyLats, final double[] polyLons) {
        this.field = field;
        if (polyLats.length != polyLons.length) {
            throw new IllegalArgumentException("polyLats and polyLons must be equal length");
        }
        if (polyLats.length < 4) {
            throw new IllegalArgumentException("at least 4 polygon points required");
        }
        if (polyLats[0] != polyLats[polyLats.length - 1]) {
            throw new IllegalArgumentException("first and last points of the polygon must be the same (it must close itself): polyLats[0]=" + polyLats[0] + " polyLats[" + (polyLats.length - 1) + "]=" + polyLats[polyLats.length - 1]);
        }
        if (polyLons[0] != polyLons[polyLons.length - 1]) {
            throw new IllegalArgumentException("first and last points of the polygon must be the same (it must close itself): polyLons[0]=" + polyLons[0] + " polyLons[" + (polyLons.length - 1) + "]=" + polyLons[polyLons.length - 1]);
        }
        this.polyLats = polyLats;
        this.polyLons = polyLons;
        double minLon = Double.POSITIVE_INFINITY;
        double minLat = Double.POSITIVE_INFINITY;
        double maxLon = Double.NEGATIVE_INFINITY;
        double maxLat = Double.NEGATIVE_INFINITY;
        for (int i = 0; i < polyLats.length; ++i) {
            final double lat = polyLats[i];
            if (!BKDTreeWriter.validLat(lat)) {
                throw new IllegalArgumentException("polyLats[" + i + "]=" + lat + " is not a valid latitude");
            }
            minLat = Math.min(minLat, lat);
            maxLat = Math.max(maxLat, lat);
            final double lon = polyLons[i];
            if (!BKDTreeWriter.validLon(lon)) {
                throw new IllegalArgumentException("polyLons[" + i + "]=" + lat + " is not a valid longitude");
            }
            minLon = Math.min(minLon, lon);
            maxLon = Math.max(maxLon, lon);
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
                final SortedNumericDocValues sdv = reader.getSortedNumericDocValues(BKDPointInPolygonQuery.this.field);
                if (sdv == null) {
                    return null;
                }
                if (!(sdv instanceof BKDTreeSortedNumericDocValues)) {
                    throw new IllegalStateException("field \"" + BKDPointInPolygonQuery.this.field + "\" was not indexed with BKDTreeDocValuesFormat: got: " + sdv);
                }
                final BKDTreeSortedNumericDocValues treeDV = (BKDTreeSortedNumericDocValues)sdv;
                final BKDTreeReader tree = treeDV.getBKDTreeReader();
                final DocIdSet result = tree.intersect(BKDPointInPolygonQuery.this.minLat, BKDPointInPolygonQuery.this.maxLat, BKDPointInPolygonQuery.this.minLon, BKDPointInPolygonQuery.this.maxLon, new BKDTreeReader.LatLonFilter() {
                    @Override
                    public boolean accept(final double lat, final double lon) {
                        return GeoRelationUtils.pointInPolygon(BKDPointInPolygonQuery.this.polyLons, BKDPointInPolygonQuery.this.polyLats, lat, lon);
                    }
                    
                    @Override
                    public BKDTreeReader.Relation compare(final double cellLatMin, final double cellLatMax, final double cellLonMin, final double cellLonMax) {
                        if (GeoRelationUtils.rectWithinPolyPrecise(cellLonMin, cellLatMin, cellLonMax, cellLatMax, BKDPointInPolygonQuery.this.polyLons, BKDPointInPolygonQuery.this.polyLats, BKDPointInPolygonQuery.this.minLon, BKDPointInPolygonQuery.this.minLat, BKDPointInPolygonQuery.this.maxLon, BKDPointInPolygonQuery.this.maxLat)) {
                            return BKDTreeReader.Relation.CELL_INSIDE_SHAPE;
                        }
                        if (GeoRelationUtils.rectCrossesPolyPrecise(cellLonMin, cellLatMin, cellLonMax, cellLatMax, BKDPointInPolygonQuery.this.polyLons, BKDPointInPolygonQuery.this.polyLats, BKDPointInPolygonQuery.this.minLon, BKDPointInPolygonQuery.this.minLat, BKDPointInPolygonQuery.this.maxLon, BKDPointInPolygonQuery.this.maxLat)) {
                            return BKDTreeReader.Relation.SHAPE_CROSSES_CELL;
                        }
                        return BKDTreeReader.Relation.SHAPE_OUTSIDE_CELL;
                    }
                }, treeDV.delegate);
                final DocIdSetIterator disi = result.iterator();
                return (Scorer)new ConstantScoreScorer((Weight)this, this.score(), disi);
            }
        };
    }
    
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        final BKDPointInPolygonQuery that = (BKDPointInPolygonQuery)o;
        return Arrays.equals(this.polyLons, that.polyLons) && Arrays.equals(this.polyLats, that.polyLats);
    }
    
    public final int hashCode() {
        int result = super.hashCode();
        result = 31 * result + Arrays.hashCode(this.polyLons);
        result = 31 * result + Arrays.hashCode(this.polyLats);
        return result;
    }
    
    public String toString(final String field) {
        final StringBuilder sb = new StringBuilder();
        sb.append(this.getClass().getSimpleName());
        sb.append(':');
        if (!this.field.equals(field)) {
            sb.append(" field=");
            sb.append(this.field);
            sb.append(':');
        }
        sb.append(" Points: ");
        for (int i = 0; i < this.polyLons.length; ++i) {
            sb.append("[").append(this.polyLons[i]).append(", ").append(this.polyLats[i]).append("] ");
        }
        return sb.toString();
    }
}
