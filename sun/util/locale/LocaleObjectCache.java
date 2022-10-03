package sun.util.locale;

import java.lang.ref.SoftReference;
import java.util.concurrent.ConcurrentHashMap;
import java.lang.ref.ReferenceQueue;
import java.util.concurrent.ConcurrentMap;

public abstract class LocaleObjectCache<K, V>
{
    private ConcurrentMap<K, CacheEntry<K, V>> map;
    private ReferenceQueue<V> queue;
    
    public LocaleObjectCache() {
        this(16, 0.75f, 16);
    }
    
    public LocaleObjectCache(final int n, final float n2, final int n3) {
        this.queue = new ReferenceQueue<V>();
        this.map = new ConcurrentHashMap<K, CacheEntry<K, V>>(n, n2, n3);
    }
    
    public V get(final K k) {
        Object o = null;
        this.cleanStaleEntries();
        final CacheEntry cacheEntry = this.map.get(k);
        if (cacheEntry != null) {
            o = cacheEntry.get();
        }
        if (o == null) {
            final V object = this.createObject(k);
            final Object normalizeKey = this.normalizeKey(k);
            if (normalizeKey == null || object == null) {
                return null;
            }
            final CacheEntry cacheEntry2 = new CacheEntry<K, V>(normalizeKey, object, (ReferenceQueue<Object>)this.queue);
            final CacheEntry<K, V> cacheEntry3 = this.map.putIfAbsent((K)normalizeKey, (CacheEntry<K, V>)cacheEntry2);
            if (cacheEntry3 == null) {
                o = object;
            }
            else {
                o = cacheEntry3.get();
                if (o == null) {
                    this.map.put((K)normalizeKey, (CacheEntry<K, V>)cacheEntry2);
                    o = object;
                }
            }
        }
        return (V)o;
    }
    
    protected V put(final K k, final V v) {
        final CacheEntry<K, V> cacheEntry = this.map.put(k, new CacheEntry<K, V>(k, v, this.queue));
        return (cacheEntry == null) ? null : cacheEntry.get();
    }
    
    private void cleanStaleEntries() {
        CacheEntry cacheEntry;
        while ((cacheEntry = (CacheEntry)this.queue.poll()) != null) {
            this.map.remove(cacheEntry.getKey());
        }
    }
    
    protected abstract V createObject(final K p0);
    
    protected K normalizeKey(final K k) {
        return k;
    }
    
    private static class CacheEntry<K, V> extends SoftReference<V>
    {
        private K key;
        
        CacheEntry(final K key, final V v, final ReferenceQueue<V> referenceQueue) {
            super(v, referenceQueue);
            this.key = key;
        }
        
        K getKey() {
            return this.key;
        }
    }
}
