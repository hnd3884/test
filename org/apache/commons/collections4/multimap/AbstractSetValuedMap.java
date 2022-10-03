package org.apache.commons.collections4.multimap;

import java.util.Collections;
import org.apache.commons.collections4.SetUtils;
import java.util.Collection;
import java.util.Set;
import java.util.Map;
import org.apache.commons.collections4.SetValuedMap;

public abstract class AbstractSetValuedMap<K, V> extends AbstractMultiValuedMap<K, V> implements SetValuedMap<K, V>
{
    protected AbstractSetValuedMap() {
    }
    
    protected AbstractSetValuedMap(final Map<K, ? extends Set<V>> map) {
        super((Map<K, ? extends Collection<Object>>)map);
    }
    
    @Override
    protected Map<K, Set<V>> getMap() {
        return (Map<K, Set<V>>)super.getMap();
    }
    
    @Override
    protected abstract Set<V> createCollection();
    
    @Override
    public Set<V> get(final K key) {
        return this.wrappedCollection(key);
    }
    
    @Override
    Set<V> wrappedCollection(final K key) {
        return new WrappedSet(key);
    }
    
    @Override
    public Set<V> remove(final Object key) {
        return SetUtils.emptyIfNull(this.getMap().remove(key));
    }
    
    private class WrappedSet extends WrappedCollection implements Set<V>
    {
        public WrappedSet(final K key) {
            AbstractSetValuedMap.this.super(key);
        }
        
        @Override
        public boolean equals(final Object other) {
            final Set<V> set = (Set)this.getMapping();
            if (set == null) {
                return Collections.emptySet().equals(other);
            }
            if (!(other instanceof Set)) {
                return false;
            }
            final Set<?> otherSet = (Set<?>)other;
            return SetUtils.isEqualSet(set, otherSet);
        }
        
        @Override
        public int hashCode() {
            final Set<V> set = (Set)this.getMapping();
            return SetUtils.hashCodeForSet(set);
        }
    }
}
