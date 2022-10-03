package org.apache.lucene.facet.taxonomy;

import org.apache.lucene.util.BytesRef;
import org.apache.lucene.search.DocIdSetIterator;
import org.apache.lucene.index.BinaryDocValues;
import java.util.Iterator;
import java.util.List;
import java.io.IOException;
import org.apache.lucene.facet.FacetsCollector;
import org.apache.lucene.facet.FacetsConfig;

public class TaxonomyFacetSumIntAssociations extends IntTaxonomyFacets
{
    public TaxonomyFacetSumIntAssociations(final TaxonomyReader taxoReader, final FacetsConfig config, final FacetsCollector fc) throws IOException {
        this("$facets", taxoReader, config, fc);
    }
    
    public TaxonomyFacetSumIntAssociations(final String indexFieldName, final TaxonomyReader taxoReader, final FacetsConfig config, final FacetsCollector fc) throws IOException {
        super(indexFieldName, taxoReader, config);
        this.sumValues(fc.getMatchingDocs());
    }
    
    private final void sumValues(final List<FacetsCollector.MatchingDocs> matchingDocs) throws IOException {
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
                int ord;
                int value;
                int[] values;
                int n;
                for (int end = bytesRef.offset + bytesRef.length, offset = bytesRef.offset; offset < end; offset += 4, value = ((bytes[offset] & 0xFF) << 24 | (bytes[offset + 1] & 0xFF) << 16 | (bytes[offset + 2] & 0xFF) << 8 | (bytes[offset + 3] & 0xFF)), offset += 4, values = this.values, n = ord, values[n] += value) {
                    ord = ((bytes[offset] & 0xFF) << 24 | (bytes[offset + 1] & 0xFF) << 16 | (bytes[offset + 2] & 0xFF) << 8 | (bytes[offset + 3] & 0xFF));
                }
            }
        }
    }
}
