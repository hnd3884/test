package org.apache.commons.collections4.bidimap;

import org.apache.commons.collections4.MapIterator;
import java.util.Collection;
import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.OrderedBidiMap;
import org.apache.commons.collections4.map.UnmodifiableSortedMap;
import java.util.SortedMap;
import org.apache.commons.collections4.iterators.UnmodifiableOrderedMapIterator;
import org.apache.commons.collections4.OrderedMapIterator;
import org.apache.commons.collections4.set.UnmodifiableSet;
import org.apache.commons.collections4.map.UnmodifiableEntrySet;
import java.util.Set;
import java.util.Map;
import org.apache.commons.collections4.SortedBidiMap;
import org.apache.commons.collections4.Unmodifiable;

public final class UnmodifiableSortedBidiMap<K, V> extends AbstractSortedBidiMapDecorator<K, V> implements Unmodifiable
{
    private UnmodifiableSortedBidiMap<V, K> inverse;
    
    public static <K, V> SortedBidiMap<K, V> unmodifiableSortedBidiMap(final SortedBidiMap<K, ? extends V> map) {
        if (map instanceof Unmodifiable) {
            final SortedBidiMap<K, V> tmpMap = (SortedBidiMap<K, V>)map;
            return tmpMap;
        }
        return new UnmodifiableSortedBidiMap<K, V>(map);
    }
    
    private UnmodifiableSortedBidiMap(final SortedBidiMap<K, ? extends V> map) {
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
    public OrderedMapIterator<K, V> mapIterator() {
        final OrderedMapIterator<K, V> it = this.decorated().mapIterator();
        return UnmodifiableOrderedMapIterator.unmodifiableOrderedMapIterator((OrderedMapIterator<K, ? extends V>)it);
    }
    
    @Override
    public SortedBidiMap<V, K> inverseBidiMap() {
        if (this.inverse == null) {
            this.inverse = (UnmodifiableSortedBidiMap<V, K>)new UnmodifiableSortedBidiMap((SortedBidiMap<Object, ?>)this.decorated().inverseBidiMap());
            this.inverse.inverse = (UnmodifiableSortedBidiMap<V, K>)this;
        }
        return (SortedBidiMap<V, K>)this.inverse;
    }
    
    @Override
    public SortedMap<K, V> subMap(final K fromKey, final K toKey) {
        final SortedMap<K, V> sm = this.decorated().subMap(fromKey, toKey);
        return UnmodifiableSortedMap.unmodifiableSortedMap((SortedMap<K, ? extends V>)sm);
    }
    
    @Override
    public SortedMap<K, V> headMap(final K toKey) {
        final SortedMap<K, V> sm = this.decorated().headMap(toKey);
        return UnmodifiableSortedMap.unmodifiableSortedMap((SortedMap<K, ? extends V>)sm);
    }
    
    @Override
    public SortedMap<K, V> tailMap(final K fromKey) {
        final SortedMap<K, V> sm = this.decorated().tailMap(fromKey);
        return UnmodifiableSortedMap.unmodifiableSortedMap((SortedMap<K, ? extends V>)sm);
    }
}
