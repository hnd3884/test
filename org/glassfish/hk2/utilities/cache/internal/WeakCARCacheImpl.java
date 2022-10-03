package org.glassfish.hk2.utilities.cache.internal;

import org.glassfish.hk2.utilities.cache.CacheKeyFilter;
import java.util.Map;
import org.glassfish.hk2.utilities.cache.ComputationErrorException;
import org.glassfish.hk2.utilities.general.GeneralUtilities;
import java.util.concurrent.atomic.AtomicLong;
import org.glassfish.hk2.utilities.general.WeakHashLRU;
import org.glassfish.hk2.utilities.general.WeakHashClock;
import org.glassfish.hk2.utilities.cache.Computable;
import org.glassfish.hk2.utilities.cache.WeakCARCache;

public class WeakCARCacheImpl<K, V> implements WeakCARCache<K, V>
{
    private final Computable<K, V> computable;
    private final int maxSize;
    private final WeakHashClock<K, CarValue<V>> t1;
    private final WeakHashClock<K, CarValue<V>> t2;
    private final WeakHashLRU<K> b1;
    private final WeakHashLRU<K> b2;
    private int p;
    private final AtomicLong hits;
    private final AtomicLong tries;
    
    public WeakCARCacheImpl(final Computable<K, V> computable, final int maxSize, final boolean isWeak) {
        this.p = 0;
        this.hits = new AtomicLong(0L);
        this.tries = new AtomicLong(0L);
        this.computable = computable;
        this.maxSize = maxSize;
        this.t1 = GeneralUtilities.getWeakHashClock(isWeak);
        this.t2 = GeneralUtilities.getWeakHashClock(isWeak);
        this.b1 = GeneralUtilities.getWeakHashLRU(isWeak);
        this.b2 = GeneralUtilities.getWeakHashLRU(isWeak);
    }
    
    private V getValueFromT(final K key) {
        CarValue<V> cValue = this.t1.get(key);
        if (cValue != null) {
            ((CarValue<Object>)cValue).referenceBit = true;
            return (V)((CarValue<Object>)cValue).value;
        }
        cValue = this.t2.get(key);
        if (cValue != null) {
            ((CarValue<Object>)cValue).referenceBit = true;
            return (V)((CarValue<Object>)cValue).value;
        }
        return null;
    }
    
    @Override
    public V compute(final K key) {
        this.tries.getAndIncrement();
        V value = this.getValueFromT(key);
        if (value != null) {
            this.hits.getAndIncrement();
            return value;
        }
        synchronized (this) {
            value = this.getValueFromT(key);
            if (value != null) {
                this.hits.getAndIncrement();
                return value;
            }
            try {
                value = this.computable.compute(key);
            }
            catch (final ComputationErrorException cee) {
                return (V)cee.getComputation();
            }
            final int cacheSize = this.getValueSize();
            if (cacheSize >= this.maxSize) {
                this.replace();
                final boolean inB1 = this.b1.contains(key);
                final boolean inB2 = this.b2.contains(key);
                if (!inB1 && !inB2) {
                    if (this.t1.size() + this.b1.size() >= this.maxSize) {
                        this.b1.remove();
                    }
                    else if (this.t1.size() + this.t2.size() + this.b1.size() + this.b2.size() >= 2 * this.maxSize) {
                        this.b2.remove();
                    }
                }
            }
            final boolean inB1 = this.b1.contains(key);
            final boolean inB2 = this.b2.contains(key);
            if (!inB1 && !inB2) {
                this.t1.put(key, new CarValue<V>((Object)value));
            }
            else if (inB1) {
                int b1size = this.b1.size();
                if (b1size == 0) {
                    b1size = 1;
                }
                final int b2size = this.b2.size();
                int ratio = b2size / b1size;
                if (ratio <= 0) {
                    ratio = 1;
                }
                this.p += ratio;
                if (this.p > this.maxSize) {
                    this.p = this.maxSize;
                }
                this.b1.remove(key);
                this.t2.put(key, new CarValue<V>((Object)value));
            }
            else {
                int b2size2 = this.b2.size();
                if (b2size2 == 0) {
                    b2size2 = 1;
                }
                final int b1size2 = this.b1.size();
                int ratio = b1size2 / b2size2;
                if (ratio <= 0) {
                    ratio = 1;
                }
                this.p -= ratio;
                if (this.p < 0) {
                    this.p = 0;
                }
                this.b2.remove(key);
                this.t2.put(key, new CarValue<V>((Object)value));
            }
        }
        return value;
    }
    
    private void replace() {
        boolean found = false;
        while (!found) {
            int trySize = this.p;
            if (trySize < 1) {
                trySize = 1;
            }
            if (this.t1.size() >= trySize) {
                final Map.Entry<K, CarValue<V>> entry = this.t1.next();
                if (!((CarValue<Object>)entry.getValue()).referenceBit) {
                    found = true;
                    this.t1.remove(entry.getKey());
                    this.b1.add(entry.getKey());
                }
                else {
                    final CarValue<V> entryValue = entry.getValue();
                    ((CarValue<Object>)entryValue).referenceBit = false;
                    this.t1.remove(entry.getKey());
                    this.t2.put(entry.getKey(), entryValue);
                }
            }
            else {
                final Map.Entry<K, CarValue<V>> entry = this.t2.next();
                if (!((CarValue<Object>)entry.getValue()).referenceBit) {
                    found = true;
                    this.t2.remove(entry.getKey());
                    this.b2.add(entry.getKey());
                }
                else {
                    final CarValue<V> entryValue = entry.getValue();
                    ((CarValue<Object>)entryValue).referenceBit = false;
                }
            }
        }
    }
    
    @Override
    public synchronized int getKeySize() {
        return this.t1.size() + this.t2.size() + this.b1.size() + this.b2.size();
    }
    
    @Override
    public synchronized int getValueSize() {
        return this.t1.size() + this.t2.size();
    }
    
    @Override
    public synchronized void clear() {
        this.t1.clear();
        this.t2.clear();
        this.b1.clear();
        this.b2.clear();
        this.p = 0;
        this.tries.set(0L);
        this.hits.set(0L);
    }
    
    @Override
    public int getMaxSize() {
        return this.maxSize;
    }
    
    @Override
    public Computable<K, V> getComputable() {
        return this.computable;
    }
    
    @Override
    public synchronized boolean remove(final K key) {
        return this.t1.remove(key) != null || this.t2.remove(key) != null || this.b1.remove(key) || this.b2.remove(key);
    }
    
    @Override
    public synchronized void releaseMatching(final CacheKeyFilter<K> filter) {
        if (filter == null) {
            return;
        }
        this.b2.releaseMatching(filter);
        this.b1.releaseMatching(filter);
        this.t1.releaseMatching(filter);
        this.t2.releaseMatching(filter);
    }
    
    @Override
    public synchronized void clearStaleReferences() {
        this.t1.clearStaleReferences();
        this.t2.clearStaleReferences();
        this.b1.clearStaleReferences();
        this.b2.clearStaleReferences();
    }
    
    @Override
    public int getT1Size() {
        return this.t1.size();
    }
    
    @Override
    public int getT2Size() {
        return this.t2.size();
    }
    
    @Override
    public int getB1Size() {
        return this.b1.size();
    }
    
    @Override
    public int getB2Size() {
        return this.b2.size();
    }
    
    @Override
    public int getP() {
        return this.p;
    }
    
    @Override
    public String dumpAllLists() {
        final StringBuffer sb = new StringBuffer("p=" + this.p + "\nT1: " + this.t1.toString() + "\n");
        sb.append("T2: " + this.t2.toString() + "\n");
        sb.append("B1: " + this.b1.toString() + "\n");
        sb.append("B2: " + this.b2.toString() + "\n");
        return sb.toString();
    }
    
    @Override
    public double getHitRate() {
        final long localHits = this.hits.get();
        long localTries = this.tries.get();
        if (localTries == 0L) {
            localTries = 1L;
        }
        return localHits / (double)localTries * 100.0;
    }
    
    @Override
    public String toString() {
        return "WeakCARCacheImpl(t1size=" + this.t1.size() + ",t2Size=" + this.t2.size() + ",b1Size=" + this.b1.size() + ",b2Size=" + this.b2.size() + ",p=" + this.p + ",hitRate=" + this.getHitRate() + "%," + System.identityHashCode(this) + ")";
    }
    
    private static class CarValue<V>
    {
        private final V value;
        private volatile boolean referenceBit;
        
        private CarValue(final V value) {
            this.referenceBit = false;
            this.value = value;
        }
    }
}
