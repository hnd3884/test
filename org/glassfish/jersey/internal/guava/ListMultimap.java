package org.glassfish.jersey.internal.guava;

import java.util.Collection;
import java.util.Map;
import java.util.List;

public interface ListMultimap<K, V> extends Multimap<K, V>
{
    List<V> get(final K p0);
    
    List<V> removeAll(final Object p0);
    
    Map<K, Collection<V>> asMap();
    
    boolean equals(final Object p0);
}
