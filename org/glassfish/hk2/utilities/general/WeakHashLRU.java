package org.glassfish.hk2.utilities.general;

import org.glassfish.hk2.utilities.cache.CacheKeyFilter;

public interface WeakHashLRU<K>
{
    void add(final K p0);
    
    boolean contains(final K p0);
    
    boolean remove(final K p0);
    
    void releaseMatching(final CacheKeyFilter<K> p0);
    
    int size();
    
    K remove();
    
    void clear();
    
    void clearStaleReferences();
}
