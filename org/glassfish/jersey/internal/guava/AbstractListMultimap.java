package org.glassfish.jersey.internal.guava;

import java.util.List;
import java.util.Collection;
import java.util.Map;

abstract class AbstractListMultimap<K, V> extends AbstractMapBasedMultimap<K, V> implements ListMultimap<K, V>
{
    private static final long serialVersionUID = 6588350623831699109L;
    
    AbstractListMultimap(final Map<K, Collection<V>> map) {
        super(map);
    }
    
    @Override
    abstract List<V> createCollection();
    
    @Override
    public List<V> get(final K key) {
        return (List)super.get(key);
    }
    
    @Override
    public List<V> removeAll(final Object key) {
        return (List)super.removeAll(key);
    }
    
    @Override
    public boolean put(final K key, final V value) {
        return super.put(key, value);
    }
    
    @Override
    public Map<K, Collection<V>> asMap() {
        return super.asMap();
    }
    
    @Override
    public boolean equals(final Object object) {
        return super.equals(object);
    }
}
