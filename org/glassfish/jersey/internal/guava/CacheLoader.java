package org.glassfish.jersey.internal.guava;

public abstract class CacheLoader<K, V>
{
    protected CacheLoader() {
    }
    
    public abstract V load(final K p0) throws Exception;
    
    public ListenableFuture<V> reload(final K key, final V oldValue) throws Exception {
        Preconditions.checkNotNull(key);
        Preconditions.checkNotNull(oldValue);
        return Futures.immediateFuture(this.load(key));
    }
    
    public static final class InvalidCacheLoadException extends RuntimeException
    {
        public InvalidCacheLoadException(final String message) {
            super(message);
        }
    }
}
