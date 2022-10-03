package com.adventnet.persistence.cache;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.HashMap;

public class LRUMap<K, V> extends HashMap<K, Object>
{
    private static final Logger LOGGER;
    private int maxSize;
    private CacheStatsUtil cacheStats;
    private boolean isCacheStatusEnabled;
    
    public LRUMap(final int maxSize, final CacheStatsUtil cacheStats) {
        super(maxSize);
        this.maxSize = 1000;
        this.cacheStats = null;
        this.isCacheStatusEnabled = false;
        this.maxSize = maxSize;
        if (cacheStats == null) {
            LRUMap.LOGGER.log(Level.INFO, "CacheStatsUtil is null");
        }
        else {
            this.cacheStats = cacheStats;
            this.isCacheStatusEnabled = true;
            LRUMap.LOGGER.log(Level.INFO, "CacheStatsUtil is " + this.cacheStats + " and CacheStatusEnabled is " + this.isCacheStatusEnabled);
        }
    }
    
    @Override
    public V get(final Object key) {
        final ValueWrap<K, V> wrap = super.get(key);
        if (wrap != null) {
            wrap.accessTime = System.currentTimeMillis();
            return wrap.value;
        }
        return null;
    }
    
    @Override
    public Object put(final K key, final Object value) {
        final ValueWrap<K, V> wrap = new ValueWrap<K, V>(key, (V)value);
        wrap.accessTime = System.currentTimeMillis();
        if (super.size() >= this.maxSize) {
            this.sortAndRemoveOldestKey();
        }
        super.put(key, wrap);
        return wrap.value;
    }
    
    private void sortAndRemoveOldestKey() {
        long oldestTime = System.currentTimeMillis();
        final List<K> keysToBeRemoved = new ArrayList<K>();
        ValueWrap<K, V> vw = null;
        for (final Object o : super.values()) {
            vw = (ValueWrap)o;
            if (vw.accessTime < oldestTime) {
                oldestTime = vw.accessTime;
                keysToBeRemoved.clear();
                keysToBeRemoved.add(vw.key);
            }
            else {
                if (vw.accessTime != oldestTime) {
                    continue;
                }
                keysToBeRemoved.add(vw.key);
            }
        }
        LRUMap.LOGGER.log(Level.FINE, "Clearing [{0}] {1} entries from the LRUMap", new Object[] { keysToBeRemoved.size(), vw.value.getClass() });
        for (final K key : keysToBeRemoved) {
            super.remove(key);
        }
    }
    
    static {
        LOGGER = Logger.getLogger(LRUMap.class.getName());
    }
    
    static class ValueWrap<K, V> implements Comparable
    {
        V value;
        K key;
        long accessTime;
        
        ValueWrap(final K key, final V value) {
            this.accessTime = -1L;
            this.key = key;
            this.value = value;
        }
        
        @Override
        public String toString() {
            final StringBuilder buff = new StringBuilder();
            buff.append("[KEY = ").append(this.key);
            buff.append(", VALUE = ").append(this.value);
            buff.append(" AccessTime = ").append(String.valueOf(this.accessTime));
            buff.append("]");
            return buff.toString();
        }
        
        @Override
        public int compareTo(final Object obj) {
            if (obj == null) {
                return -1;
            }
            if (!(obj instanceof ValueWrap)) {
                return -1;
            }
            final ValueWrap val = (ValueWrap)obj;
            if (val.accessTime == this.accessTime) {
                return 0;
            }
            return (val.accessTime < this.accessTime) ? 1 : -1;
        }
    }
}
