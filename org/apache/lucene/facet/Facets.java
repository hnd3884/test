package org.apache.lucene.facet;

import java.util.List;
import java.io.IOException;

public abstract class Facets
{
    public abstract FacetResult getTopChildren(final int p0, final String p1, final String... p2) throws IOException;
    
    public abstract Number getSpecificValue(final String p0, final String... p1) throws IOException;
    
    public abstract List<FacetResult> getAllDims(final int p0) throws IOException;
}
