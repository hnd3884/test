package org.glassfish.hk2.utilities.cache.internal;

import java.util.Comparator;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.HashMap;
import org.glassfish.hk2.utilities.cache.CacheKeyFilter;
import org.glassfish.hk2.utilities.cache.CacheEntry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import org.glassfish.hk2.utilities.cache.LRUCache;

public class LRUCacheCheapRead<K, V> extends LRUCache<K, V>
{
    final Object prunningLock;
    final int maxCacheSize;
    Map<K, CacheEntryImpl<K, V>> cache;
    private static final CacheEntryImplComparator COMPARATOR;
    
    public LRUCacheCheapRead(final int maxCacheSize) {
        this.prunningLock = new Object();
        this.cache = new ConcurrentHashMap<K, CacheEntryImpl<K, V>>();
        this.maxCacheSize = maxCacheSize;
    }
    
    @Override
    public V get(final K key) {
        final CacheEntryImpl<K, V> entry = this.cache.get(key);
        return (entry != null) ? entry.hit().value : null;
    }
    
    @Override
    public CacheEntry put(final K key, final V value) {
        final CacheEntryImpl<K, V> entry = new CacheEntryImpl<K, V>(key, value, this);
        synchronized (this.prunningLock) {
            if (this.cache.size() + 1 > this.maxCacheSize) {
                this.removeLRUItem();
            }
            this.cache.put(key, entry);
            return entry;
        }
    }
    
    @Override
    public void releaseCache() {
        this.cache.clear();
    }
    
    @Override
    public int getMaxCacheSize() {
        return this.maxCacheSize;
    }
    
    @Override
    public void releaseMatching(final CacheKeyFilter<K> filter) {
        if (filter == null) {
            return;
        }
        for (final Map.Entry<K, CacheEntryImpl<K, V>> entry : new HashMap(this.cache).entrySet()) {
            if (filter.matches(entry.getKey())) {
                entry.getValue().removeFromCache();
            }
        }
    }
    
    private void removeLRUItem() {
        final Collection<CacheEntryImpl<K, V>> values = this.cache.values();
        Collections.min((Collection<? extends CacheEntryImpl>)values, (Comparator<? super CacheEntryImpl>)LRUCacheCheapRead.COMPARATOR).removeFromCache();
    }
    
    static {
        COMPARATOR = new CacheEntryImplComparator();
    }
    
    private static class CacheEntryImplComparator implements Comparator<CacheEntryImpl<?, ?>>
    {
        @Override
        public int compare(final CacheEntryImpl<?, ?> first, final CacheEntryImpl<?, ?> second) {
            final long diff = first.lastHit - second.lastHit;
            return (diff > 0L) ? 1 : ((diff == 0L) ? 0 : -1);
        }
    }
    
    private static class CacheEntryImpl<K, V> implements CacheEntry
    {
        final K key;
        final V value;
        final LRUCacheCheapRead<K, V> parent;
        long lastHit;
        
        public CacheEntryImpl(final K k, final V v, final LRUCacheCheapRead<K, V> cache) {
            this.parent = cache;
            this.key = k;
            this.value = v;
            this.lastHit = System.nanoTime();
        }
        
        @Override
        public void removeFromCache() {
            this.parent.cache.remove(this.key);
        }
        
        public CacheEntryImpl<K, V> hit() {
            this.lastHit = System.nanoTime();
            return this;
        }
    }
}
