package org.apache.commons.collections4;

import java.util.Set;

public interface BidiMap<K, V> extends IterableMap<K, V>
{
    V put(final K p0, final V p1);
    
    K getKey(final Object p0);
    
    K removeValue(final Object p0);
    
    BidiMap<V, K> inverseBidiMap();
    
    Set<V> values();
}
