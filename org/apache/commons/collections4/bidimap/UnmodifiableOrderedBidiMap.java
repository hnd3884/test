package org.apache.commons.collections4.bidimap;

import org.apache.commons.collections4.MapIterator;
import java.util.Collection;
import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.iterators.UnmodifiableOrderedMapIterator;
import org.apache.commons.collections4.OrderedMapIterator;
import org.apache.commons.collections4.set.UnmodifiableSet;
import org.apache.commons.collections4.map.UnmodifiableEntrySet;
import java.util.Set;
import java.util.Map;
import org.apache.commons.collections4.OrderedBidiMap;
import org.apache.commons.collections4.Unmodifiable;

public final class UnmodifiableOrderedBidiMap<K, V> extends AbstractOrderedBidiMapDecorator<K, V> implements Unmodifiable
{
    private UnmodifiableOrderedBidiMap<V, K> inverse;
    
    public static <K, V> OrderedBidiMap<K, V> unmodifiableOrderedBidiMap(final OrderedBidiMap<? extends K, ? extends V> map) {
        if (map instanceof Unmodifiable) {
            final OrderedBidiMap<K, V> tmpMap = (OrderedBidiMap<K, V>)map;
            return tmpMap;
        }
        return new UnmodifiableOrderedBidiMap<K, V>(map);
    }
    
    private UnmodifiableOrderedBidiMap(final OrderedBidiMap<? extends K, ? extends V> map) {
        super(map);
    }
    
    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public V put(final K key, final V value) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void putAll(final Map<? extends K, ? extends V> mapToCopy) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public V remove(final Object key) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        final Set<Map.Entry<K, V>> set = super.entrySet();
        return UnmodifiableEntrySet.unmodifiableEntrySet(set);
    }
    
    @Override
    public Set<K> keySet() {
        final Set<K> set = super.keySet();
        return UnmodifiableSet.unmodifiableSet((Set<? extends K>)set);
    }
    
    @Override
    public Set<V> values() {
        final Set<V> set = super.values();
        return UnmodifiableSet.unmodifiableSet((Set<? extends V>)set);
    }
    
    @Override
    public K removeValue(final Object value) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public OrderedBidiMap<V, K> inverseBidiMap() {
        return this.inverseOrderedBidiMap();
    }
    
    @Override
    public OrderedMapIterator<K, V> mapIterator() {
        final OrderedMapIterator<K, V> it = this.decorated().mapIterator();
        return UnmodifiableOrderedMapIterator.unmodifiableOrderedMapIterator((OrderedMapIterator<K, ? extends V>)it);
    }
    
    public OrderedBidiMap<V, K> inverseOrderedBidiMap() {
        if (this.inverse == null) {
            this.inverse = (UnmodifiableOrderedBidiMap<V, K>)new UnmodifiableOrderedBidiMap(this.decorated().inverseBidiMap());
            this.inverse.inverse = (UnmodifiableOrderedBidiMap<V, K>)this;
        }
        return (OrderedBidiMap<V, K>)this.inverse;
    }
}
