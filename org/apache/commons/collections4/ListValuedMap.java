package org.apache.commons.collections4;

import java.util.List;

public interface ListValuedMap<K, V> extends MultiValuedMap<K, V>
{
    List<V> get(final K p0);
    
    List<V> remove(final Object p0);
}
