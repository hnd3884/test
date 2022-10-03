package org.apache.lucene.facet.taxonomy.writercache;

import java.util.Iterator;
import org.apache.lucene.facet.taxonomy.FacetLabel;
import java.util.LinkedHashMap;
import java.util.HashMap;

class NameIntCacheLRU
{
    private HashMap<Object, Integer> cache;
    long nMisses;
    long nHits;
    private int maxCacheSize;
    
    NameIntCacheLRU(final int maxCacheSize) {
        this.nMisses = 0L;
        this.nHits = 0L;
        this.createCache(this.maxCacheSize = maxCacheSize);
    }
    
    public int getMaxSize() {
        return this.maxCacheSize;
    }
    
    public int getSize() {
        return this.cache.size();
    }
    
    private void createCache(final int maxSize) {
        if (maxSize < Integer.MAX_VALUE) {
            this.cache = new LinkedHashMap<Object, Integer>(1000, 0.7f, true);
        }
        else {
            this.cache = new HashMap<Object, Integer>(1000, 0.7f);
        }
    }
    
    Integer get(final FacetLabel name) {
        final Integer res = this.cache.get(this.key(name));
        if (res == null) {
            ++this.nMisses;
        }
        else {
            ++this.nHits;
        }
        return res;
    }
    
    Object key(final FacetLabel name) {
        return name;
    }
    
    Object key(final FacetLabel name, final int prefixLen) {
        return name.subpath(prefixLen);
    }
    
    boolean put(final FacetLabel name, final Integer val) {
        this.cache.put(this.key(name), val);
        return this.isCacheFull();
    }
    
    boolean put(final FacetLabel name, final int prefixLen, final Integer val) {
        this.cache.put(this.key(name, prefixLen), val);
        return this.isCacheFull();
    }
    
    private boolean isCacheFull() {
        return this.cache.size() > this.maxCacheSize;
    }
    
    void clear() {
        this.cache.clear();
    }
    
    String stats() {
        return "#miss=" + this.nMisses + " #hit=" + this.nHits;
    }
    
    boolean makeRoomLRU() {
        if (!this.isCacheFull()) {
            return false;
        }
        final int n = this.cache.size() - 2 * this.maxCacheSize / 3;
        if (n <= 0) {
            return false;
        }
        final Iterator<Object> it = this.cache.keySet().iterator();
        for (int i = 0; i < n && it.hasNext(); ++i) {
            it.next();
            it.remove();
        }
        return true;
    }
}
