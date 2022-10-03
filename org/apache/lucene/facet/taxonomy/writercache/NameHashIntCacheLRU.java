package org.apache.lucene.facet.taxonomy.writercache;

import org.apache.lucene.facet.taxonomy.FacetLabel;

public class NameHashIntCacheLRU extends NameIntCacheLRU
{
    NameHashIntCacheLRU(final int maxCacheSize) {
        super(maxCacheSize);
    }
    
    @Override
    Object key(final FacetLabel name) {
        return new Long(name.longHashCode());
    }
    
    @Override
    Object key(final FacetLabel name, final int prefixLen) {
        return new Long(name.subpath(prefixLen).longHashCode());
    }
}
