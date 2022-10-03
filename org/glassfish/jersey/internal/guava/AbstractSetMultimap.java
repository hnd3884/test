package org.glassfish.jersey.internal.guava;

import java.util.Collections;
import java.util.Set;
import java.util.Collection;
import java.util.Map;

abstract class AbstractSetMultimap<K, V> extends AbstractMapBasedMultimap<K, V> implements SetMultimap<K, V>
{
    private static final long serialVersionUID = 7431625294878419160L;
    
    AbstractSetMultimap(final Map<K, Collection<V>> map) {
        super(map);
    }
    
    @Override
    abstract Set<V> createCollection();
    
    @Override
    Set<V> createUnmodifiableEmptyCollection() {
        return Collections.emptySet();
    }
    
    @Override
    public Set<V> get(final K key) {
        return (Set)super.get(key);
    }
    
    @Override
    public Set<Map.Entry<K, V>> entries() {
        return (Set)super.entries();
    }
    
    @Override
    public Set<V> removeAll(final Object key) {
        return (Set)super.removeAll(key);
    }
    
    @Override
    public Map<K, Collection<V>> asMap() {
        return super.asMap();
    }
    
    @Override
    public boolean put(final K key, final V value) {
        return super.put(key, value);
    }
    
    @Override
    public boolean equals(final Object object) {
        return super.equals(object);
    }
}
