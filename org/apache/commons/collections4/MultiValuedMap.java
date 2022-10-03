package org.apache.commons.collections4;

import java.util.Set;
import java.util.Map;
import java.util.Collection;

public interface MultiValuedMap<K, V>
{
    int size();
    
    boolean isEmpty();
    
    boolean containsKey(final Object p0);
    
    boolean containsValue(final Object p0);
    
    boolean containsMapping(final Object p0, final Object p1);
    
    Collection<V> get(final K p0);
    
    boolean put(final K p0, final V p1);
    
    boolean putAll(final K p0, final Iterable<? extends V> p1);
    
    boolean putAll(final Map<? extends K, ? extends V> p0);
    
    boolean putAll(final MultiValuedMap<? extends K, ? extends V> p0);
    
    Collection<V> remove(final Object p0);
    
    boolean removeMapping(final Object p0, final Object p1);
    
    void clear();
    
    Collection<Map.Entry<K, V>> entries();
    
    MultiSet<K> keys();
    
    Set<K> keySet();
    
    Collection<V> values();
    
    Map<K, Collection<V>> asMap();
    
    MapIterator<K, V> mapIterator();
}
