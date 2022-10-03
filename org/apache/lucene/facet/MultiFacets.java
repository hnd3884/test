package org.apache.lucene.facet;

import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import java.util.Map;

public class MultiFacets extends Facets
{
    private final Map<String, Facets> dimToFacets;
    private final Facets defaultFacets;
    
    public MultiFacets(final Map<String, Facets> dimToFacets) {
        this(dimToFacets, null);
    }
    
    public MultiFacets(final Map<String, Facets> dimToFacets, final Facets defaultFacets) {
        this.dimToFacets = dimToFacets;
        this.defaultFacets = defaultFacets;
    }
    
    @Override
    public FacetResult getTopChildren(final int topN, final String dim, final String... path) throws IOException {
        Facets facets = this.dimToFacets.get(dim);
        if (facets == null) {
            if (this.defaultFacets == null) {
                throw new IllegalArgumentException("invalid dim \"" + dim + "\"");
            }
            facets = this.defaultFacets;
        }
        return facets.getTopChildren(topN, dim, path);
    }
    
    @Override
    public Number getSpecificValue(final String dim, final String... path) throws IOException {
        Facets facets = this.dimToFacets.get(dim);
        if (facets == null) {
            if (this.defaultFacets == null) {
                throw new IllegalArgumentException("invalid dim \"" + dim + "\"");
            }
            facets = this.defaultFacets;
        }
        return facets.getSpecificValue(dim, path);
    }
    
    @Override
    public List<FacetResult> getAllDims(final int topN) throws IOException {
        final List<FacetResult> results = new ArrayList<FacetResult>();
        for (final Map.Entry<String, Facets> ent : this.dimToFacets.entrySet()) {
            results.add(ent.getValue().getTopChildren(topN, ent.getKey(), new String[0]));
        }
        if (this.defaultFacets != null) {
            for (final FacetResult result : this.defaultFacets.getAllDims(topN)) {
                if (!this.dimToFacets.containsKey(result.dim)) {
                    results.add(result);
                }
            }
        }
        return results;
    }
}
