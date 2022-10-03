package org.apache.commons.collections4;

import java.util.Set;

public interface SetValuedMap<K, V> extends MultiValuedMap<K, V>
{
    Set<V> get(final K p0);
    
    Set<V> remove(final Object p0);
}
