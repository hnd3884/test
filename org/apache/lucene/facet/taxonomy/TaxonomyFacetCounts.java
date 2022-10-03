package org.apache.lucene.facet.taxonomy;

import org.apache.lucene.search.DocIdSetIterator;
import java.util.Iterator;
import org.apache.lucene.util.IntsRef;
import java.util.List;
import java.io.IOException;
import org.apache.lucene.facet.FacetsCollector;
import org.apache.lucene.facet.FacetsConfig;

public class TaxonomyFacetCounts extends IntTaxonomyFacets
{
    private final OrdinalsReader ordinalsReader;
    
    public TaxonomyFacetCounts(final OrdinalsReader ordinalsReader, final TaxonomyReader taxoReader, final FacetsConfig config, final FacetsCollector fc) throws IOException {
        super(ordinalsReader.getIndexFieldName(), taxoReader, config);
        this.ordinalsReader = ordinalsReader;
        this.count(fc.getMatchingDocs());
    }
    
    private final void count(final List<FacetsCollector.MatchingDocs> matchingDocs) throws IOException {
        final IntsRef scratch = new IntsRef();
        for (final FacetsCollector.MatchingDocs hits : matchingDocs) {
            final OrdinalsReader.OrdinalsSegmentReader ords = this.ordinalsReader.getReader(hits.context);
            final DocIdSetIterator docs = hits.bits.iterator();
            int doc;
            while ((doc = docs.nextDoc()) != Integer.MAX_VALUE) {
                ords.get(doc, scratch);
                for (int i = 0; i < scratch.length; ++i) {
                    final int[] values = this.values;
                    final int n = scratch.ints[scratch.offset + i];
                    ++values[n];
                }
            }
        }
        this.rollup();
    }
}
