package org.glassfish.hk2.utilities.cache;

import java.util.concurrent.TimeoutException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.Future;
import java.util.Iterator;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ExecutionException;
import java.util.Comparator;
import java.util.concurrent.ConcurrentHashMap;

public class LRUHybridCache<K, V> implements Computable<K, HybridCacheEntry<V>>
{
    private final CycleHandler<K> cycleHandler;
    private static final CycleHandler<Object> EMPTY_CYCLE_HANDLER;
    private final ConcurrentHashMap<K, OriginThreadAwareFuture> cache;
    private final Computable<K, HybridCacheEntry<V>> computable;
    private final Object prunningLock;
    private final int maxCacheSize;
    private static final Comparator<OriginThreadAwareFuture> COMPARATOR;
    
    public LRUHybridCache(final int maxCacheSize, final Computable<K, HybridCacheEntry<V>> computable) {
        this(maxCacheSize, (Computable<Object, HybridCacheEntry<V>>)computable, LRUHybridCache.EMPTY_CYCLE_HANDLER);
    }
    
    public LRUHybridCache(final int maxCacheSize, final Computable<K, HybridCacheEntry<V>> computable, final CycleHandler<K> cycleHandler) {
        this.cache = new ConcurrentHashMap<K, OriginThreadAwareFuture>();
        this.prunningLock = new Object();
        this.maxCacheSize = maxCacheSize;
        this.computable = computable;
        this.cycleHandler = cycleHandler;
    }
    
    public HybridCacheEntry<V> createCacheEntry(final K k, final V v, final boolean dropMe) {
        return new HybridCacheEntryImpl<V>(k, v, dropMe);
    }
    
    @Override
    public HybridCacheEntry<V> compute(final K key) {
        OriginThreadAwareFuture f = this.cache.get(key);
        if (f == null) {
            final OriginThreadAwareFuture ft = (LRUHybridCache<K, HybridCacheEntry<V>>)this.new OriginThreadAwareFuture(key);
            synchronized (this.prunningLock) {
                if (this.cache.size() + 1 > this.maxCacheSize) {
                    this.removeLRUItem();
                }
                f = this.cache.putIfAbsent(key, ft);
            }
            if (f == null) {
                f = ft;
                ft.run();
            }
        }
        else {
            final long tid = f.threadId;
            if (tid != -1L && Thread.currentThread().getId() == f.threadId) {
                this.cycleHandler.handleCycle(key);
            }
            f.lastHit = System.nanoTime();
        }
        try {
            final HybridCacheEntry result = f.get();
            if (result.dropMe()) {
                this.cache.remove(key);
            }
            return result;
        }
        catch (final InterruptedException ex) {
            throw new RuntimeException(ex);
        }
        catch (final ExecutionException ex2) {
            this.cache.remove(key);
            if (ex2.getCause() instanceof RuntimeException) {
                throw (RuntimeException)ex2.getCause();
            }
            throw new RuntimeException(ex2);
        }
    }
    
    public void clear() {
        this.cache.clear();
    }
    
    public int size() {
        return this.cache.size();
    }
    
    public int getMaximumCacheSize() {
        return this.maxCacheSize;
    }
    
    public boolean containsKey(final K key) {
        return this.cache.containsKey(key);
    }
    
    public void remove(final K key) {
        this.cache.remove(key);
    }
    
    private void removeLRUItem() {
        final Collection<OriginThreadAwareFuture> values = this.cache.values();
        this.cache.remove(Collections.min((Collection<? extends OriginThreadAwareFuture>)values, (Comparator<? super OriginThreadAwareFuture>)LRUHybridCache.COMPARATOR).key);
    }
    
    public void releaseMatching(final CacheKeyFilter<K> filter) {
        if (filter == null) {
            return;
        }
        for (final K key : this.cache.keySet()) {
            if (filter.matches(key)) {
                this.cache.remove(key);
            }
        }
    }
    
    static {
        EMPTY_CYCLE_HANDLER = new CycleHandler<Object>() {
            @Override
            public void handleCycle(final Object key) {
            }
        };
        COMPARATOR = new CacheEntryImplComparator<Object, Object>();
    }
    
    private class OriginThreadAwareFuture implements Future<HybridCacheEntry<V>>
    {
        private final K key;
        private final FutureTask<HybridCacheEntry<V>> future;
        private volatile long threadId;
        private volatile long lastHit;
        
        OriginThreadAwareFuture(final LRUHybridCache<K, HybridCacheEntry<V>> cache, final K key) {
            this.key = key;
            this.threadId = Thread.currentThread().getId();
            final Callable<HybridCacheEntry<V>> eval = new Callable<HybridCacheEntry<V>>() {
                @Override
                public HybridCacheEntry<V> call() throws Exception {
                    try {
                        final HybridCacheEntry<V> result = LRUHybridCache.this.computable.compute(key);
                        return result;
                    }
                    finally {
                        OriginThreadAwareFuture.this.threadId = -1L;
                    }
                }
            };
            this.future = new FutureTask<HybridCacheEntry<V>>(eval);
            this.lastHit = System.nanoTime();
        }
        
        @Override
        public int hashCode() {
            return this.future.hashCode();
        }
        
        @Override
        public boolean equals(final Object obj) {
            if (obj == null) {
                return false;
            }
            if (this.getClass() != obj.getClass()) {
                return false;
            }
            final OriginThreadAwareFuture other = (OriginThreadAwareFuture)obj;
            return this.future == other.future || (this.future != null && this.future.equals(other.future));
        }
        
        @Override
        public boolean cancel(final boolean mayInterruptIfRunning) {
            return this.future.cancel(mayInterruptIfRunning);
        }
        
        @Override
        public boolean isCancelled() {
            return this.future.isCancelled();
        }
        
        @Override
        public boolean isDone() {
            return this.future.isDone();
        }
        
        @Override
        public HybridCacheEntry<V> get() throws InterruptedException, ExecutionException {
            return this.future.get();
        }
        
        @Override
        public HybridCacheEntry<V> get(final long timeout, final TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
            return this.future.get(timeout, unit);
        }
        
        public void run() {
            this.future.run();
        }
    }
    
    private final class HybridCacheEntryImpl<V1> implements HybridCacheEntry<V1>
    {
        private final K key;
        private final V1 value;
        private final boolean dropMe;
        
        public HybridCacheEntryImpl(final K key, final V1 value, final boolean dropMe) {
            this.key = key;
            this.value = value;
            this.dropMe = dropMe;
        }
        
        @Override
        public V1 getValue() {
            return this.value;
        }
        
        @Override
        public boolean dropMe() {
            return this.dropMe;
        }
        
        @Override
        public void removeFromCache() {
            LRUHybridCache.this.remove(this.key);
        }
        
        @Override
        public int hashCode() {
            int hash = 5;
            hash = 23 * hash + ((this.key != null) ? this.key.hashCode() : 0);
            return hash;
        }
        
        @Override
        public boolean equals(final Object obj) {
            if (obj == null) {
                return false;
            }
            if (this.getClass() != obj.getClass()) {
                return false;
            }
            final HybridCacheEntryImpl<V1> other = (HybridCacheEntryImpl<V1>)obj;
            return this.key == other.key || (this.key != null && this.key.equals(other.key));
        }
    }
    
    private static class CacheEntryImplComparator<K, V> implements Comparator<OriginThreadAwareFuture>
    {
        @Override
        public int compare(final OriginThreadAwareFuture first, final OriginThreadAwareFuture second) {
            final long diff = first.lastHit - second.lastHit;
            return (diff > 0L) ? 1 : ((diff == 0L) ? 0 : -1);
        }
    }
    
    public interface CycleHandler<K>
    {
        void handleCycle(final K p0);
    }
}
