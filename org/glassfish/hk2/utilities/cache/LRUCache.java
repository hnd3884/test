package org.glassfish.hk2.utilities.cache;

import org.glassfish.hk2.utilities.cache.internal.LRUCacheCheapRead;

public abstract class LRUCache<K, V>
{
    public static <K, V> LRUCache<K, V> createCache(final int maxCacheSize) {
        return new LRUCacheCheapRead<K, V>(maxCacheSize);
    }
    
    public abstract V get(final K p0);
    
    public abstract CacheEntry put(final K p0, final V p1);
    
    public abstract void releaseCache();
    
    public abstract int getMaxCacheSize();
    
    public abstract void releaseMatching(final CacheKeyFilter<K> p0);
}
