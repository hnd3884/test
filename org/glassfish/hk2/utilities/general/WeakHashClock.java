package org.glassfish.hk2.utilities.general;

import java.util.Map;
import org.glassfish.hk2.utilities.cache.CacheKeyFilter;

public interface WeakHashClock<K, V>
{
    void put(final K p0, final V p1);
    
    V get(final K p0);
    
    V remove(final K p0);
    
    void releaseMatching(final CacheKeyFilter<K> p0);
    
    int size();
    
    Map.Entry<K, V> next();
    
    void clear();
    
    void clearStaleReferences();
    
    boolean hasWeakKeys();
}
