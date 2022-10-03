package com.adventnet.iam.security;

import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.TimeUnit;

public class LRUCacheMap<K, V>
{
    private int maxSize;
    private int evictionBatchSize;
    private int idleTime;
    private TimeUnit idleTimeUnit;
    private AtomicInteger currentSize;
    private Lock lock;
    private ConcurrentHashMap<K, ValueWrap<K, V>> conmap;
    private Enum full;
    private Metrics metrics;
    private static Logger logger;
    
    public LRUCacheMap(final int maxSize, final int evictionBatchSize, final int idleTime, final TimeUnit idleTimeUnit) {
        this.currentSize = new AtomicInteger();
        this.maxSize = maxSize;
        this.conmap = new ConcurrentHashMap<K, ValueWrap<K, V>>(maxSize);
        this.evictionBatchSize = evictionBatchSize;
        this.idleTime = idleTime;
        this.idleTimeUnit = idleTimeUnit;
        this.lock = new ReentrantLock();
    }
    
    protected void setMetric(final Metrics metrics, final Enum full) {
        this.full = full;
        this.metrics = metrics;
    }
    
    public int size() {
        return this.currentSize.get();
    }
    
    public Object put(final K key, final V value) {
        final ValueWrap<K, V> wrap = new ValueWrap<K, V>(key, value);
        final ValueWrap<K, V> oldWrap = this.conmap.put(key, wrap);
        if (oldWrap == null && this.currentSize.incrementAndGet() > this.maxSize) {
            this.lockAndClean(this.idleTime, this.idleTimeUnit);
        }
        return wrap;
    }
    
    public Object putIfAbsent(final K key, final V value) {
        ValueWrap<K, V> wrap = new ValueWrap<K, V>(key, value);
        final ValueWrap<K, V> oldWrap = this.conmap.putIfAbsent(key, wrap);
        if (oldWrap == null && this.currentSize.incrementAndGet() > this.maxSize) {
            this.lockAndClean(this.idleTime, this.idleTimeUnit);
        }
        wrap = ((oldWrap == null) ? wrap : oldWrap);
        return wrap.value;
    }
    
    public int removeIdleEntries(final long idleTime, final TimeUnit idleTimeUnit) {
        return this.removeIdleEntries(idleTime, idleTimeUnit, false);
    }
    
    private int removeIdleEntries(final long idleTime, final TimeUnit idleTimeUnit, final boolean forceClean) {
        int removedEntries = 0;
        try {
            final long idleTimeMillis = System.currentTimeMillis() - idleTimeUnit.toMillis(idleTime);
            long least_recent = Long.MAX_VALUE;
            long most_recent = Long.MIN_VALUE;
            final Set<Map.Entry<K, ValueWrap<K, V>>> set = this.conmap.entrySet();
            final Iterator<Map.Entry<K, ValueWrap<K, V>>> it = set.iterator();
            while (it.hasNext()) {
                final Map.Entry<K, ValueWrap<K, V>> entry = it.next();
                final long lastAccessTime = entry.getValue().getAccessTime();
                if (lastAccessTime < idleTimeMillis) {
                    it.remove();
                    ++removedEntries;
                }
                most_recent = ((lastAccessTime > most_recent) ? lastAccessTime : most_recent);
                least_recent = ((lastAccessTime < least_recent) ? lastAccessTime : least_recent);
            }
            if (forceClean && removedEntries < this.evictionBatchSize) {
                final int removed = this.splitIntoBucketsAndClean(least_recent, most_recent, this.evictionBatchSize - removedEntries);
                removedEntries += removed;
            }
        }
        catch (final Exception excp) {
            LRUCacheMap.logger.log(Level.INFO, "HARMLESS", excp);
        }
        finally {
            this.currentSize.set(this.conmap.size());
        }
        return removedEntries;
    }
    
    private int splitIntoBucketsAndClean(final long least_recent, final long most_recent, final int more_to_be_removed) {
        int removed = 0;
        final int no_of_buckets = 20;
        long range = most_recent - least_recent;
        range = ((range == 0L) ? 1L : range);
        final int[] bucket_counter = new int[no_of_buckets];
        final Object[][] buckets = new Object[no_of_buckets][more_to_be_removed];
        final Set<Map.Entry<K, ValueWrap<K, V>>> set = this.conmap.entrySet();
        for (final Map.Entry<K, ValueWrap<K, V>> entry : set) {
            final long n = entry.getValue().getAccessTime() - least_recent;
            int bucketNo = (int)(n * no_of_buckets / range);
            bucketNo = ((bucketNo < 0) ? 0 : bucketNo);
            bucketNo = ((bucketNo >= no_of_buckets) ? (no_of_buckets - 1) : bucketNo);
            int pos = bucket_counter[bucketNo];
            if (pos < more_to_be_removed) {
                buckets[bucketNo][pos] = entry;
                bucket_counter[bucketNo] = ++pos;
            }
            if (bucket_counter[0] > more_to_be_removed) {
                break;
            }
        }
        for (int i = 0; i < no_of_buckets; ++i) {
            for (int j = 0; j < bucket_counter[i]; ++j) {
                final Map.Entry<K, ValueWrap<K, V>> entry2 = (Map.Entry<K, ValueWrap<K, V>>)buckets[i][j];
                final Object val = this.conmap.remove(entry2.getKey());
                if (val != null) {
                    ++removed;
                }
                if (removed >= more_to_be_removed) {
                    return removed;
                }
            }
        }
        return removed;
    }
    
    public boolean containsKey(final K key) {
        return this.getWrap(key) != null;
    }
    
    public V get(final K key) {
        final ValueWrap<K, V> wrap = this.getWrap(key);
        return (wrap == null) ? null : wrap.value;
    }
    
    ValueWrap<K, V> getWrap(final K key) {
        final ValueWrap<K, V> wrap = this.conmap.get(key);
        if (wrap == null) {
            return null;
        }
        wrap.updateAccessTime();
        return wrap;
    }
    
    public V remove(final K key) {
        final ValueWrap<K, V> wrap = this.conmap.remove(key);
        if (wrap != null) {
            this.currentSize.decrementAndGet();
        }
        return (wrap == null) ? null : wrap.value;
    }
    
    private void lockAndClean(final long idleTime, final TimeUnit idleTimeUnit) {
        final boolean didLock = this.lock.tryLock();
        if (!didLock) {
            return;
        }
        try {
            if (this.metrics != null && this.full != null) {
                this.metrics.inc(this.full);
            }
            final int totalEntriesCount = this.size();
            final int totalRemoved = this.removeIdleEntries(idleTime, idleTimeUnit, true);
            LRUCacheMap.logger.log(Level.INFO, "Total Entries Count : {0}, Removed Entries Count : {1}", new Object[] { totalEntriesCount, totalRemoved });
        }
        finally {
            this.lock.unlock();
        }
    }
    
    @Override
    public String toString() {
        return "LRUCacheMap [maxSize=" + this.maxSize + ", evictionBatchSize=" + this.evictionBatchSize + ", idleTime=" + this.idleTime + ", idleTimeUnit=" + this.idleTimeUnit + ", currentSize=" + this.currentSize + "]";
    }
    
    Set<Map.Entry<K, ValueWrap<K, V>>> getAllEntriesSet() {
        return this.conmap.entrySet();
    }
    
    static {
        LRUCacheMap.logger = Logger.getLogger(LRUCacheMap.class.getName());
    }
    
    static class ValueWrap<K, V> implements Comparable<ValueWrap<K, V>>
    {
        V value;
        K key;
        private long accessTime;
        private long prevCheckTime;
        private long createdTime;
        private long cacheVersion;
        
        ValueWrap(final K key, final V value) {
            this.createdTime = System.currentTimeMillis();
            this.key = key;
            this.value = value;
            this.updateAccessTime();
            this.prevCheckTime = this.createdTime;
        }
        
        void updateAccessTime() {
            this.accessTime = System.currentTimeMillis();
        }
        
        public void setPreviousCheckTime(final long prevCheckTime) {
            this.prevCheckTime = prevCheckTime;
        }
        
        public long getPreviousCheckTime() {
            return this.prevCheckTime;
        }
        
        public void setCacheVersion(final Long cacheVersion) {
            this.cacheVersion = cacheVersion;
        }
        
        public long getCacheVersion() {
            return this.cacheVersion;
        }
        
        public long getCreatedTime() {
            return this.createdTime;
        }
        
        public long getAccessTime() {
            return this.accessTime;
        }
        
        @Override
        public int compareTo(final ValueWrap<K, V> val) {
            return (int)(this.accessTime - val.accessTime);
        }
        
        @Override
        public String toString() {
            return "ValueWrap [value=" + this.value + ", key=" + this.key + ", accessTime=" + this.accessTime + "cacheVersion  " + this.cacheVersion + "]";
        }
    }
}
