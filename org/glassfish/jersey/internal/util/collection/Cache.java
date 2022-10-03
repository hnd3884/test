package org.glassfish.jersey.internal.util.collection;

import java.util.concurrent.TimeoutException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.Future;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class Cache<K, V> implements Function<K, V>
{
    private static final CycleHandler<Object> EMPTY_HANDLER;
    private final CycleHandler<K> cycleHandler;
    private final ConcurrentHashMap<K, OriginThreadAwareFuture> cache;
    private final Function<K, V> computable;
    
    public Cache(final Function<K, V> computable) {
        this((Function<Object, V>)computable, Cache.EMPTY_HANDLER);
    }
    
    public Cache(final Function<K, V> computable, final CycleHandler<K> cycleHandler) {
        this.cache = new ConcurrentHashMap<K, OriginThreadAwareFuture>();
        this.computable = computable;
        this.cycleHandler = cycleHandler;
    }
    
    @Override
    public V apply(final K key) {
        OriginThreadAwareFuture f = this.cache.get(key);
        if (f == null) {
            final OriginThreadAwareFuture ft = new OriginThreadAwareFuture(key);
            f = this.cache.putIfAbsent(key, ft);
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
        }
        try {
            return f.get();
        }
        catch (final InterruptedException ex) {
            throw new RuntimeException(ex);
        }
        catch (final ExecutionException ex2) {
            this.cache.remove(key);
            final Throwable cause = ex2.getCause();
            if (cause == null) {
                throw new RuntimeException(ex2);
            }
            if (cause instanceof RuntimeException) {
                throw (RuntimeException)cause;
            }
            throw new RuntimeException(cause);
        }
    }
    
    public void clear() {
        this.cache.clear();
    }
    
    public boolean containsKey(final K key) {
        return this.cache.containsKey(key);
    }
    
    public void remove(final K key) {
        this.cache.remove(key);
    }
    
    public int size() {
        return this.cache.size();
    }
    
    static {
        EMPTY_HANDLER = (key -> {});
    }
    
    private class OriginThreadAwareFuture implements Future<V>
    {
        private final FutureTask<V> future;
        private volatile long threadId;
        
        OriginThreadAwareFuture(final K key) {
            this.threadId = Thread.currentThread().getId();
            final Callable<V> eval = (Callable<V>)(() -> {
                try {
                    return Cache.this.computable.apply(key);
                }
                finally {
                    this.threadId = -1L;
                }
            });
            this.future = new FutureTask<V>(eval);
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
        public V get() throws InterruptedException, ExecutionException {
            return this.future.get();
        }
        
        @Override
        public V get(final long timeout, final TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
            return this.future.get(timeout, unit);
        }
        
        public void run() {
            this.future.run();
        }
    }
    
    public interface CycleHandler<K>
    {
        void handleCycle(final K p0);
    }
}
