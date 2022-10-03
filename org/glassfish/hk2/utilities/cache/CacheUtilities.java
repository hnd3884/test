package org.glassfish.hk2.utilities.cache;

import org.glassfish.hk2.utilities.cache.internal.WeakCARCacheImpl;

public class CacheUtilities
{
    public static <K, V> WeakCARCache<K, V> createWeakCARCache(final Computable<K, V> computable, final int maxSize, final boolean isWeak) {
        return new WeakCARCacheImpl<K, V>(computable, maxSize, isWeak);
    }
}
