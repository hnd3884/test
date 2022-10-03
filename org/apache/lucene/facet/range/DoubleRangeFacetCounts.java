package org.apache.lucene.facet.range;

import org.apache.lucene.facet.FacetResult;
import org.apache.lucene.search.DocIdSetIterator;
import org.apache.lucene.search.Scorer;
import org.apache.lucene.search.Weight;
import org.apache.lucene.queries.function.FunctionValues;
import java.util.Iterator;
import org.apache.lucene.search.QueryCache;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.index.IndexReaderContext;
import org.apache.lucene.index.ReaderUtil;
import java.util.Map;
import java.util.Collections;
import org.apache.lucene.util.NumericUtils;
import java.util.List;
import org.apache.lucene.search.Query;
import java.io.IOException;
import org.apache.lucene.queries.function.ValueSource;
import org.apache.lucene.queries.function.valuesource.DoubleFieldSource;
import org.apache.lucene.facet.FacetsCollector;

public class DoubleRangeFacetCounts extends RangeFacetCounts
{
    public DoubleRangeFacetCounts(final String field, final FacetsCollector hits, final DoubleRange... ranges) throws IOException {
        this(field, (ValueSource)new DoubleFieldSource(field), hits, ranges);
    }
    
    public DoubleRangeFacetCounts(final String field, final ValueSource valueSource, final FacetsCollector hits, final DoubleRange... ranges) throws IOException {
        this(field, valueSource, hits, (Query)null, ranges);
    }
    
    public DoubleRangeFacetCounts(final String field, final ValueSource valueSource, final FacetsCollector hits, final Query fastMatchQuery, final DoubleRange... ranges) throws IOException {
        super(field, ranges, fastMatchQuery);
        this.count(valueSource, hits.getMatchingDocs());
    }
    
    private void count(final ValueSource valueSource, final List<FacetsCollector.MatchingDocs> matchingDocs) throws IOException {
        final DoubleRange[] ranges = (DoubleRange[])this.ranges;
        final LongRange[] longRanges = new LongRange[ranges.length];
        for (int i = 0; i < ranges.length; ++i) {
            final DoubleRange range = ranges[i];
            longRanges[i] = new LongRange(range.label, NumericUtils.doubleToSortableLong(range.minIncl), true, NumericUtils.doubleToSortableLong(range.maxIncl), true);
        }
        final LongRangeCounter counter = new LongRangeCounter(longRanges);
        int missingCount = 0;
        for (final FacetsCollector.MatchingDocs hits : matchingDocs) {
            final FunctionValues fv = valueSource.getValues((Map)Collections.emptyMap(), hits.context);
            this.totCount += hits.totalHits;
            DocIdSetIterator fastMatchDocs;
            if (this.fastMatchQuery != null) {
                final IndexReaderContext topLevelContext = ReaderUtil.getTopLevelContext((IndexReaderContext)hits.context);
                final IndexSearcher searcher = new IndexSearcher(topLevelContext);
                searcher.setQueryCache((QueryCache)null);
                final Weight fastMatchWeight = searcher.createNormalizedWeight(this.fastMatchQuery, false);
                final Scorer s = fastMatchWeight.scorer(hits.context);
                if (s == null) {
                    continue;
                }
                fastMatchDocs = s.iterator();
            }
            else {
                fastMatchDocs = null;
            }
            final DocIdSetIterator docs = hits.bits.iterator();
            int doc = docs.nextDoc();
            while (doc != Integer.MAX_VALUE) {
                if (fastMatchDocs != null) {
                    int fastMatchDoc = fastMatchDocs.docID();
                    if (fastMatchDoc < doc) {
                        fastMatchDoc = fastMatchDocs.advance(doc);
                    }
                    if (doc != fastMatchDoc) {
                        doc = docs.advance(fastMatchDoc);
                        continue;
                    }
                }
                if (fv.exists(doc)) {
                    counter.add(NumericUtils.doubleToSortableLong(fv.doubleVal(doc)));
                }
                else {
                    ++missingCount;
                }
                doc = docs.nextDoc();
            }
        }
        missingCount += counter.fillCounts(this.counts);
        this.totCount -= missingCount;
    }
}
