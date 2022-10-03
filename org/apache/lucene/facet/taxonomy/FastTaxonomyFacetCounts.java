package org.apache.lucene.facet.taxonomy;

import org.apache.lucene.util.BytesRef;
import org.apache.lucene.search.DocIdSetIterator;
import org.apache.lucene.index.BinaryDocValues;
import java.util.Iterator;
import java.util.List;
import java.io.IOException;
import org.apache.lucene.facet.FacetsCollector;
import org.apache.lucene.facet.FacetsConfig;

public class FastTaxonomyFacetCounts extends IntTaxonomyFacets
{
    public FastTaxonomyFacetCounts(final TaxonomyReader taxoReader, final FacetsConfig config, final FacetsCollector fc) throws IOException {
        this("$facets", taxoReader, config, fc);
    }
    
    public FastTaxonomyFacetCounts(final String indexFieldName, final TaxonomyReader taxoReader, final FacetsConfig config, final FacetsCollector fc) throws IOException {
        super(indexFieldName, taxoReader, config);
        this.count(fc.getMatchingDocs());
    }
    
    private final void count(final List<FacetsCollector.MatchingDocs> matchingDocs) throws IOException {
        for (final FacetsCollector.MatchingDocs hits : matchingDocs) {
            final BinaryDocValues dv = hits.context.reader().getBinaryDocValues(this.indexFieldName);
            if (dv == null) {
                continue;
            }
            final DocIdSetIterator docs = hits.bits.iterator();
            int doc;
            while ((doc = docs.nextDoc()) != Integer.MAX_VALUE) {
                final BytesRef bytesRef = dv.get(doc);
                final byte[] bytes = bytesRef.bytes;
                final int end = bytesRef.offset + bytesRef.length;
                int ord = 0;
                int offset = bytesRef.offset;
                int prev = 0;
                while (offset < end) {
                    final byte b = bytes[offset++];
                    if (b >= 0) {
                        ord = (prev += (ord << 7 | b));
                        final int[] values = this.values;
                        final int n = ord;
                        ++values[n];
                        ord = 0;
                    }
                    else {
                        ord = (ord << 7 | (b & 0x7F));
                    }
                }
            }
        }
        this.rollup();
    }
}
