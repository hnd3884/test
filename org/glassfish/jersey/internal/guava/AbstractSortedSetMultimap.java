package org.glassfish.jersey.internal.guava;

import java.util.Set;
import java.util.Collections;
import java.util.SortedSet;
import java.util.Collection;
import java.util.Map;

abstract class AbstractSortedSetMultimap<K, V> extends AbstractSetMultimap<K, V> implements SortedSetMultimap<K, V>
{
    private static final long serialVersionUID = 430848587173315748L;
    
    AbstractSortedSetMultimap(final Map<K, Collection<V>> map) {
        super(map);
    }
    
    @Override
    abstract SortedSet<V> createCollection();
    
    @Override
    SortedSet<V> createUnmodifiableEmptyCollection() {
        return Collections.unmodifiableSortedSet(this.createCollection());
    }
    
    @Override
    public SortedSet<V> get(final K key) {
        return (SortedSet)super.get(key);
    }
    
    @Override
    public SortedSet<V> removeAll(final Object key) {
        return (SortedSet)super.removeAll(key);
    }
    
    @Override
    public Map<K, Collection<V>> asMap() {
        return super.asMap();
    }
    
    @Override
    public Collection<V> values() {
        return super.values();
    }
}
