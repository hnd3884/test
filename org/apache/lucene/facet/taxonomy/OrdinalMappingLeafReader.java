package org.apache.lucene.facet.taxonomy;

import java.io.IOException;
import org.apache.lucene.index.BinaryDocValues;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.IntsRef;
import java.util.Iterator;
import java.util.HashSet;
import org.apache.lucene.facet.FacetsConfig;
import org.apache.lucene.index.LeafReader;
import java.util.Set;
import org.apache.lucene.index.FilterLeafReader;

public class OrdinalMappingLeafReader extends FilterLeafReader
{
    private final int[] ordinalMap;
    private final InnerFacetsConfig facetsConfig;
    private final Set<String> facetFields;
    
    public OrdinalMappingLeafReader(final LeafReader in, final int[] ordinalMap, final FacetsConfig srcConfig) {
        super(in);
        this.ordinalMap = ordinalMap;
        this.facetsConfig = new InnerFacetsConfig();
        this.facetFields = new HashSet<String>();
        for (final FacetsConfig.DimConfig dc : srcConfig.getDimConfigs().values()) {
            this.facetFields.add(dc.indexFieldName);
        }
        this.facetFields.add(FacetsConfig.DEFAULT_DIM_CONFIG.indexFieldName);
    }
    
    protected BytesRef encode(final IntsRef ordinals) {
        return this.facetsConfig.dedupAndEncode(ordinals);
    }
    
    protected OrdinalsReader getOrdinalsReader(final String field) {
        return new DocValuesOrdinalsReader(field);
    }
    
    public BinaryDocValues getBinaryDocValues(final String field) throws IOException {
        if (this.facetFields.contains(field)) {
            final OrdinalsReader ordsReader = this.getOrdinalsReader(field);
            return new OrdinalMappingBinaryDocValues(ordsReader.getReader(this.in.getContext()));
        }
        return this.in.getBinaryDocValues(field);
    }
    
    private static class InnerFacetsConfig extends FacetsConfig
    {
        InnerFacetsConfig() {
        }
        
        public BytesRef dedupAndEncode(final IntsRef ordinals) {
            return super.dedupAndEncode(ordinals);
        }
    }
    
    private class OrdinalMappingBinaryDocValues extends BinaryDocValues
    {
        private final IntsRef ordinals;
        private final OrdinalsReader.OrdinalsSegmentReader ordsReader;
        
        OrdinalMappingBinaryDocValues(final OrdinalsReader.OrdinalsSegmentReader ordsReader) throws IOException {
            this.ordinals = new IntsRef(32);
            this.ordsReader = ordsReader;
        }
        
        public BytesRef get(final int docID) {
            try {
                this.ordsReader.get(docID, this.ordinals);
                for (int i = 0; i < this.ordinals.length; ++i) {
                    this.ordinals.ints[i] = OrdinalMappingLeafReader.this.ordinalMap[this.ordinals.ints[i]];
                }
                return OrdinalMappingLeafReader.this.encode(this.ordinals);
            }
            catch (final IOException e) {
                throw new RuntimeException("error reading category ordinals for doc " + docID, e);
            }
        }
    }
}
