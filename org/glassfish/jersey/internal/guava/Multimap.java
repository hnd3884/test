package org.glassfish.jersey.internal.guava;

import java.util.Map;
import java.util.Set;
import java.util.Collection;

public interface Multimap<K, V>
{
    int size();
    
    boolean containsKey(final Object p0);
    
    boolean containsValue(final Object p0);
    
    boolean containsEntry(final Object p0, final Object p1);
    
    boolean put(final K p0, final V p1);
    
    boolean remove(final Object p0, final Object p1);
    
    boolean putAll(final K p0, final Iterable<? extends V> p1);
    
    Collection<V> removeAll(final Object p0);
    
    void clear();
    
    Collection<V> get(final K p0);
    
    Set<K> keySet();
    
    Collection<V> values();
    
    Collection<Map.Entry<K, V>> entries();
    
    Map<K, Collection<V>> asMap();
    
    boolean equals(final Object p0);
    
    int hashCode();
}
