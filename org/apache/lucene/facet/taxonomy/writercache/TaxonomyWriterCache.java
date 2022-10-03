package org.apache.lucene.facet.taxonomy.writercache;

import org.apache.lucene.facet.taxonomy.FacetLabel;

public interface TaxonomyWriterCache
{
    void close();
    
    int get(final FacetLabel p0);
    
    boolean put(final FacetLabel p0, final int p1);
    
    boolean isFull();
    
    void clear();
}
