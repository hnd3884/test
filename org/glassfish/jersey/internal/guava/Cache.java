package org.glassfish.jersey.internal.guava;

public interface Cache<K, V>
{
    V getIfPresent(final Object p0);
    
    void put(final K p0, final V p1);
}
