package sun.security.util;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.lang.ref.ReferenceQueue;
import java.util.Map;

class MemoryCache<K, V> extends Cache<K, V>
{
    private static final float LOAD_FACTOR = 0.75f;
    private static final boolean DEBUG = false;
    private final Map<K, CacheEntry<K, V>> cacheMap;
    private int maxSize;
    private long lifetime;
    private final ReferenceQueue<V> queue;
    
    public MemoryCache(final boolean b, final int n) {
        this(b, n, 0);
    }
    
    public MemoryCache(final boolean b, final int maxSize, final int n) {
        this.maxSize = maxSize;
        this.lifetime = n * 1000;
        if (b) {
            this.queue = new ReferenceQueue<V>();
        }
        else {
            this.queue = null;
        }
        this.cacheMap = new LinkedHashMap<K, CacheEntry<K, V>>(1, 0.75f, true);
    }
    
    private void emptyQueue() {
        if (this.queue == null) {
            return;
        }
        this.cacheMap.size();
        while (true) {
            final CacheEntry cacheEntry = (CacheEntry)this.queue.poll();
            if (cacheEntry == null) {
                break;
            }
            final Object key = cacheEntry.getKey();
            if (key == null) {
                continue;
            }
            final CacheEntry cacheEntry2 = this.cacheMap.remove(key);
            if (cacheEntry2 == null || cacheEntry == cacheEntry2) {
                continue;
            }
            this.cacheMap.put((K)key, cacheEntry2);
        }
    }
    
    private void expungeExpiredEntries() {
        this.emptyQueue();
        if (this.lifetime == 0L) {
            return;
        }
        int n = 0;
        final long currentTimeMillis = System.currentTimeMillis();
        final Iterator<CacheEntry<K, V>> iterator = this.cacheMap.values().iterator();
        while (iterator.hasNext()) {
            if (!iterator.next().isValid(currentTimeMillis)) {
                iterator.remove();
                ++n;
            }
        }
    }
    
    @Override
    public synchronized int size() {
        this.expungeExpiredEntries();
        return this.cacheMap.size();
    }
    
    @Override
    public synchronized void clear() {
        if (this.queue != null) {
            final Iterator<CacheEntry<K, V>> iterator = this.cacheMap.values().iterator();
            while (iterator.hasNext()) {
                iterator.next().invalidate();
            }
            while (this.queue.poll() != null) {}
        }
        this.cacheMap.clear();
    }
    
    @Override
    public synchronized void put(final K k, final V v) {
        this.emptyQueue();
        final CacheEntry<K, V> cacheEntry = this.cacheMap.put(k, this.newEntry(k, v, (this.lifetime == 0L) ? 0L : (System.currentTimeMillis() + this.lifetime), this.queue));
        if (cacheEntry != null) {
            cacheEntry.invalidate();
            return;
        }
        if (this.maxSize > 0 && this.cacheMap.size() > this.maxSize) {
            this.expungeExpiredEntries();
            if (this.cacheMap.size() > this.maxSize) {
                final Iterator<CacheEntry<K, V>> iterator = this.cacheMap.values().iterator();
                final CacheEntry cacheEntry2 = iterator.next();
                iterator.remove();
                cacheEntry2.invalidate();
            }
        }
    }
    
    @Override
    public synchronized V get(final Object o) {
        this.emptyQueue();
        final CacheEntry cacheEntry = this.cacheMap.get(o);
        if (cacheEntry == null) {
            return null;
        }
        if (!cacheEntry.isValid((this.lifetime == 0L) ? 0L : System.currentTimeMillis())) {
            this.cacheMap.remove(o);
            return null;
        }
        return (V)cacheEntry.getValue();
    }
    
    @Override
    public synchronized void remove(final Object o) {
        this.emptyQueue();
        final CacheEntry cacheEntry = this.cacheMap.remove(o);
        if (cacheEntry != null) {
            cacheEntry.invalidate();
        }
    }
    
    @Override
    public synchronized void setCapacity(final int n) {
        this.expungeExpiredEntries();
        if (n > 0 && this.cacheMap.size() > n) {
            final Iterator<CacheEntry<K, V>> iterator = this.cacheMap.values().iterator();
            for (int i = this.cacheMap.size() - n; i > 0; --i) {
                final CacheEntry cacheEntry = iterator.next();
                iterator.remove();
                cacheEntry.invalidate();
            }
        }
        this.maxSize = ((n > 0) ? n : 0);
    }
    
    @Override
    public synchronized void setTimeout(final int n) {
        this.emptyQueue();
        this.lifetime = ((n > 0) ? (n * 1000L) : 0L);
    }
    
    @Override
    public synchronized void accept(final CacheVisitor<K, V> cacheVisitor) {
        this.expungeExpiredEntries();
        cacheVisitor.visit(this.getCachedEntries());
    }
    
    private Map<K, V> getCachedEntries() {
        final HashMap hashMap = new HashMap(this.cacheMap.size());
        for (final CacheEntry cacheEntry : this.cacheMap.values()) {
            hashMap.put(cacheEntry.getKey(), cacheEntry.getValue());
        }
        return hashMap;
    }
    
    protected CacheEntry<K, V> newEntry(final K k, final V v, final long n, final ReferenceQueue<V> referenceQueue) {
        if (referenceQueue != null) {
            return new SoftCacheEntry<K, V>(k, v, n, referenceQueue);
        }
        return new HardCacheEntry<K, V>(k, v, n);
    }
    
    private static class HardCacheEntry<K, V> implements CacheEntry<K, V>
    {
        private K key;
        private V value;
        private long expirationTime;
        
        HardCacheEntry(final K key, final V value, final long expirationTime) {
            this.key = key;
            this.value = value;
            this.expirationTime = expirationTime;
        }
        
        @Override
        public K getKey() {
            return this.key;
        }
        
        @Override
        public V getValue() {
            return this.value;
        }
        
        @Override
        public boolean isValid(final long n) {
            final boolean b = n <= this.expirationTime;
            if (!b) {
                this.invalidate();
            }
            return b;
        }
        
        @Override
        public void invalidate() {
            this.key = null;
            this.value = null;
            this.expirationTime = -1L;
        }
    }
    
    private static class SoftCacheEntry<K, V> extends SoftReference<V> implements CacheEntry<K, V>
    {
        private K key;
        private long expirationTime;
        
        SoftCacheEntry(final K key, final V v, final long expirationTime, final ReferenceQueue<V> referenceQueue) {
            super(v, referenceQueue);
            this.key = key;
            this.expirationTime = expirationTime;
        }
        
        @Override
        public K getKey() {
            return this.key;
        }
        
        @Override
        public V getValue() {
            return this.get();
        }
        
        @Override
        public boolean isValid(final long n) {
            final boolean b = n <= this.expirationTime && this.get() != null;
            if (!b) {
                this.invalidate();
            }
            return b;
        }
        
        @Override
        public void invalidate() {
            this.clear();
            this.key = null;
            this.expirationTime = -1L;
        }
    }
    
    private interface CacheEntry<K, V>
    {
        boolean isValid(final long p0);
        
        void invalidate();
        
        K getKey();
        
        V getValue();
    }
}
