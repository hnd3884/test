package org.glassfish.jersey.internal.guava;

import java.util.concurrent.ExecutionException;
import java.util.function.Function;

public interface LoadingCache<K, V> extends Cache<K, V>, Function<K, V>
{
    V get(final K p0) throws ExecutionException;
    
    @Deprecated
    V apply(final K p0);
}
