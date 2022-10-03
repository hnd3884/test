package org.apache.lucene.facet.taxonomy;

import java.util.Collections;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import org.apache.lucene.facet.FacetsConfig;
import org.apache.lucene.facet.FacetResult;
import java.util.Comparator;
import org.apache.lucene.facet.Facets;

public abstract class TaxonomyFacets extends Facets
{
    private static final Comparator<FacetResult> BY_VALUE_THEN_DIM;
    protected final String indexFieldName;
    protected final TaxonomyReader taxoReader;
    protected final FacetsConfig config;
    protected final int[] children;
    protected final int[] siblings;
    
    protected TaxonomyFacets(final String indexFieldName, final TaxonomyReader taxoReader, final FacetsConfig config) throws IOException {
        this.indexFieldName = indexFieldName;
        this.taxoReader = taxoReader;
        this.config = config;
        final ParallelTaxonomyArrays pta = taxoReader.getParallelTaxonomyArrays();
        this.children = pta.children();
        this.siblings = pta.siblings();
    }
    
    protected FacetsConfig.DimConfig verifyDim(final String dim) {
        final FacetsConfig.DimConfig dimConfig = this.config.getDimConfig(dim);
        if (!dimConfig.indexFieldName.equals(this.indexFieldName)) {
            throw new IllegalArgumentException("dimension \"" + dim + "\" was not indexed into field \"" + this.indexFieldName);
        }
        return dimConfig;
    }
    
    @Override
    public List<FacetResult> getAllDims(final int topN) throws IOException {
        int ord = this.children[0];
        final List<FacetResult> results = new ArrayList<FacetResult>();
        while (ord != -1) {
            final String dim = this.taxoReader.getPath(ord).components[0];
            final FacetsConfig.DimConfig dimConfig = this.config.getDimConfig(dim);
            if (dimConfig.indexFieldName.equals(this.indexFieldName)) {
                final FacetResult result = this.getTopChildren(topN, dim, new String[0]);
                if (result != null) {
                    results.add(result);
                }
            }
            ord = this.siblings[ord];
        }
        Collections.sort(results, TaxonomyFacets.BY_VALUE_THEN_DIM);
        return results;
    }
    
    static {
        BY_VALUE_THEN_DIM = new Comparator<FacetResult>() {
            @Override
            public int compare(final FacetResult a, final FacetResult b) {
                if (a.value.doubleValue() > b.value.doubleValue()) {
                    return -1;
                }
                if (b.value.doubleValue() > a.value.doubleValue()) {
                    return 1;
                }
                return a.dim.compareTo(b.dim);
            }
        };
    }
}
