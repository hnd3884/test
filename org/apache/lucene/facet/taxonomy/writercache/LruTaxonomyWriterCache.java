package org.apache.lucene.facet.taxonomy.writercache;

import org.apache.lucene.facet.taxonomy.FacetLabel;

public class LruTaxonomyWriterCache implements TaxonomyWriterCache
{
    private NameIntCacheLRU cache;
    
    public LruTaxonomyWriterCache(final int cacheSize) {
        this(cacheSize, LRUType.LRU_HASHED);
    }
    
    public LruTaxonomyWriterCache(final int cacheSize, final LRUType lruType) {
        if (lruType == LRUType.LRU_HASHED) {
            this.cache = new NameHashIntCacheLRU(cacheSize);
        }
        else {
            this.cache = new NameIntCacheLRU(cacheSize);
        }
    }
    
    @Override
    public synchronized boolean isFull() {
        return this.cache.getSize() == this.cache.getMaxSize();
    }
    
    @Override
    public synchronized void clear() {
        this.cache.clear();
    }
    
    @Override
    public synchronized void close() {
        this.cache.clear();
        this.cache = null;
    }
    
    @Override
    public synchronized int get(final FacetLabel categoryPath) {
        final Integer res = this.cache.get(categoryPath);
        if (res == null) {
            return -1;
        }
        return res;
    }
    
    @Override
    public synchronized boolean put(final FacetLabel categoryPath, final int ordinal) {
        final boolean ret = this.cache.put(categoryPath, new Integer(ordinal));
        if (ret) {
            this.cache.makeRoomLRU();
        }
        return ret;
    }
    
    public enum LRUType
    {
        LRU_HASHED, 
        LRU_STRING;
    }
}
