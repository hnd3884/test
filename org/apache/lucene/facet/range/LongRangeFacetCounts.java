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
import java.util.List;
import org.apache.lucene.search.Query;
import java.io.IOException;
import org.apache.lucene.queries.function.ValueSource;
import org.apache.lucene.queries.function.valuesource.LongFieldSource;
import org.apache.lucene.facet.FacetsCollector;

public class LongRangeFacetCounts extends RangeFacetCounts
{
    public LongRangeFacetCounts(final String field, final FacetsCollector hits, final LongRange... ranges) throws IOException {
        this(field, (ValueSource)new LongFieldSource(field), hits, ranges);
    }
    
    public LongRangeFacetCounts(final String field, final ValueSource valueSource, final FacetsCollector hits, final LongRange... ranges) throws IOException {
        this(field, valueSource, hits, (Query)null, ranges);
    }
    
    public LongRangeFacetCounts(final String field, final ValueSource valueSource, final FacetsCollector hits, final Query fastMatchQuery, final LongRange... ranges) throws IOException {
        super(field, ranges, fastMatchQuery);
        this.count(valueSource, hits.getMatchingDocs());
    }
    
    private void count(final ValueSource valueSource, final List<FacetsCollector.MatchingDocs> matchingDocs) throws IOException {
        final LongRange[] ranges = (LongRange[])this.ranges;
        final LongRangeCounter counter = new LongRangeCounter(ranges);
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
                    counter.add(fv.longVal(doc));
                }
                else {
                    ++missingCount;
                }
                doc = docs.nextDoc();
            }
        }
        final int x = counter.fillCounts(this.counts);
        missingCount += x;
        this.totCount -= missingCount;
    }
}
