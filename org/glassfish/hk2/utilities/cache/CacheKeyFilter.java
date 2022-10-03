package org.glassfish.hk2.utilities.cache;

public interface CacheKeyFilter<K>
{
    boolean matches(final K p0);
}
