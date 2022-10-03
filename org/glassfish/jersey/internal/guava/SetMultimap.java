package org.glassfish.jersey.internal.guava;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public interface SetMultimap<K, V> extends Multimap<K, V>
{
    Set<V> get(final K p0);
    
    Set<V> removeAll(final Object p0);
    
    Set<Map.Entry<K, V>> entries();
    
    Map<K, Collection<V>> asMap();
    
    boolean equals(final Object p0);
}
