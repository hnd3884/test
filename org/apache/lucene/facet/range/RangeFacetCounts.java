package org.apache.lucene.facet.range;

import java.util.Collections;
import java.util.List;
import org.apache.lucene.facet.LabelAndValue;
import org.apache.lucene.facet.FacetResult;
import java.io.IOException;
import org.apache.lucene.search.Query;
import org.apache.lucene.facet.Facets;

abstract class RangeFacetCounts extends Facets
{
    protected final Range[] ranges;
    protected final int[] counts;
    protected final Query fastMatchQuery;
    protected final String field;
    protected int totCount;
    
    protected RangeFacetCounts(final String field, final Range[] ranges, final Query fastMatchQuery) throws IOException {
        this.field = field;
        this.ranges = ranges;
        this.fastMatchQuery = fastMatchQuery;
        this.counts = new int[ranges.length];
    }
    
    @Override
    public FacetResult getTopChildren(final int topN, final String dim, final String... path) {
        if (!dim.equals(this.field)) {
            throw new IllegalArgumentException("invalid dim \"" + dim + "\"; should be \"" + this.field + "\"");
        }
        if (path.length != 0) {
            throw new IllegalArgumentException("path.length should be 0");
        }
        final LabelAndValue[] labelValues = new LabelAndValue[this.counts.length];
        for (int i = 0; i < this.counts.length; ++i) {
            labelValues[i] = new LabelAndValue(this.ranges[i].label, this.counts[i]);
        }
        return new FacetResult(dim, path, this.totCount, labelValues, labelValues.length);
    }
    
    @Override
    public Number getSpecificValue(final String dim, final String... path) throws IOException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public List<FacetResult> getAllDims(final int topN) throws IOException {
        return Collections.singletonList(this.getTopChildren(topN, null, new String[0]));
    }
}
