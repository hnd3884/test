package org.apache.lucene.facet;

import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopFieldCollector;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.FieldDoc;
import org.apache.lucene.search.FilterCollector;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.MultiCollector;
import org.apache.lucene.search.Collector;
import java.io.IOException;
import java.util.Map;
import org.apache.lucene.facet.sortedset.SortedSetDocValuesFacetCounts;
import org.apache.lucene.facet.taxonomy.FastTaxonomyFacetCounts;
import java.util.HashMap;
import org.apache.lucene.facet.sortedset.SortedSetDocValuesReaderState;
import org.apache.lucene.facet.taxonomy.TaxonomyReader;
import org.apache.lucene.search.IndexSearcher;

public class DrillSideways
{
    protected final IndexSearcher searcher;
    protected final TaxonomyReader taxoReader;
    protected final SortedSetDocValuesReaderState state;
    protected final FacetsConfig config;
    
    public DrillSideways(final IndexSearcher searcher, final FacetsConfig config, final TaxonomyReader taxoReader) {
        this(searcher, config, taxoReader, null);
    }
    
    public DrillSideways(final IndexSearcher searcher, final FacetsConfig config, final SortedSetDocValuesReaderState state) {
        this(searcher, config, null, state);
    }
    
    public DrillSideways(final IndexSearcher searcher, final FacetsConfig config, final TaxonomyReader taxoReader, final SortedSetDocValuesReaderState state) {
        this.searcher = searcher;
        this.config = config;
        this.taxoReader = taxoReader;
        this.state = state;
    }
    
    protected Facets buildFacetsResult(final FacetsCollector drillDowns, final FacetsCollector[] drillSideways, final String[] drillSidewaysDims) throws IOException {
        final Map<String, Facets> drillSidewaysFacets = new HashMap<String, Facets>();
        Facets drillDownFacets;
        if (this.taxoReader != null) {
            drillDownFacets = new FastTaxonomyFacetCounts(this.taxoReader, this.config, drillDowns);
            if (drillSideways != null) {
                for (int i = 0; i < drillSideways.length; ++i) {
                    drillSidewaysFacets.put(drillSidewaysDims[i], new FastTaxonomyFacetCounts(this.taxoReader, this.config, drillSideways[i]));
                }
            }
        }
        else {
            drillDownFacets = new SortedSetDocValuesFacetCounts(this.state, drillDowns);
            if (drillSideways != null) {
                for (int i = 0; i < drillSideways.length; ++i) {
                    drillSidewaysFacets.put(drillSidewaysDims[i], new SortedSetDocValuesFacetCounts(this.state, drillSideways[i]));
                }
            }
        }
        if (drillSidewaysFacets.isEmpty()) {
            return drillDownFacets;
        }
        return new MultiFacets(drillSidewaysFacets, drillDownFacets);
    }
    
    public DrillSidewaysResult search(final DrillDownQuery query, Collector hitCollector) throws IOException {
        final Map<String, Integer> drillDownDims = query.getDims();
        final FacetsCollector drillDownCollector = new FacetsCollector();
        if (drillDownDims.isEmpty()) {
            this.searcher.search((Query)query, MultiCollector.wrap(new Collector[] { hitCollector, (Collector)drillDownCollector }));
            return new DrillSidewaysResult(this.buildFacetsResult(drillDownCollector, null, null), null);
        }
        Query baseQuery = query.getBaseQuery();
        if (baseQuery == null) {
            baseQuery = (Query)new MatchAllDocsQuery();
        }
        final Query[] drillDownQueries = query.getDrillDownQueries();
        final FacetsCollector[] drillSidewaysCollectors = new FacetsCollector[drillDownDims.size()];
        for (int i = 0; i < drillSidewaysCollectors.length; ++i) {
            drillSidewaysCollectors[i] = new FacetsCollector();
        }
        final DrillSidewaysQuery dsq = new DrillSidewaysQuery(baseQuery, (Collector)drillDownCollector, (Collector[])drillSidewaysCollectors, drillDownQueries, this.scoreSubDocsAtOnce());
        if (!hitCollector.needsScores()) {
            hitCollector = (Collector)new FilterCollector(hitCollector) {
                public boolean needsScores() {
                    return true;
                }
            };
        }
        this.searcher.search((Query)dsq, hitCollector);
        return new DrillSidewaysResult(this.buildFacetsResult(drillDownCollector, drillSidewaysCollectors, drillDownDims.keySet().toArray(new String[drillDownDims.size()])), null);
    }
    
    public DrillSidewaysResult search(DrillDownQuery query, final Query filter, final FieldDoc after, int topN, final Sort sort, final boolean doDocScores, final boolean doMaxScore) throws IOException {
        if (filter != null) {
            query = new DrillDownQuery(this.config, filter, query);
        }
        if (sort != null) {
            int limit = this.searcher.getIndexReader().maxDoc();
            if (limit == 0) {
                limit = 1;
            }
            topN = Math.min(topN, limit);
            final TopFieldCollector hitCollector = TopFieldCollector.create(sort, topN, after, true, doDocScores, doMaxScore);
            final DrillSidewaysResult r = this.search(query, (Collector)hitCollector);
            return new DrillSidewaysResult(r.facets, (TopDocs)hitCollector.topDocs());
        }
        return this.search((ScoreDoc)after, query, topN);
    }
    
    public DrillSidewaysResult search(final DrillDownQuery query, final int topN) throws IOException {
        return this.search(null, query, topN);
    }
    
    public DrillSidewaysResult search(final ScoreDoc after, final DrillDownQuery query, int topN) throws IOException {
        int limit = this.searcher.getIndexReader().maxDoc();
        if (limit == 0) {
            limit = 1;
        }
        topN = Math.min(topN, limit);
        final TopScoreDocCollector hitCollector = TopScoreDocCollector.create(topN, after);
        final DrillSidewaysResult r = this.search(query, (Collector)hitCollector);
        return new DrillSidewaysResult(r.facets, hitCollector.topDocs());
    }
    
    protected boolean scoreSubDocsAtOnce() {
        return false;
    }
    
    public static class DrillSidewaysResult
    {
        public final Facets facets;
        public final TopDocs hits;
        
        public DrillSidewaysResult(final Facets facets, final TopDocs hits) {
            this.facets = facets;
            this.hits = hits;
        }
    }
}
