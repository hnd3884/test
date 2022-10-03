package org.glassfish.jersey.internal.guava;

import java.util.Set;
import java.util.Comparator;
import java.util.Collection;
import java.util.Map;
import java.util.SortedSet;

public interface SortedSetMultimap<K, V> extends SetMultimap<K, V>
{
    SortedSet<V> get(final K p0);
    
    SortedSet<V> removeAll(final Object p0);
    
    Map<K, Collection<V>> asMap();
    
    Comparator<? super V> valueComparator();
}
