package org.apache.commons.collections4.bidimap;

import java.util.Collection;
import org.apache.commons.collections4.iterators.UnmodifiableMapIterator;
import org.apache.commons.collections4.MapIterator;
import org.apache.commons.collections4.set.UnmodifiableSet;
import org.apache.commons.collections4.map.UnmodifiableEntrySet;
import java.util.Set;
import java.util.Map;
import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.Unmodifiable;

public final class UnmodifiableBidiMap<K, V> extends AbstractBidiMapDecorator<K, V> implements Unmodifiable
{
    private UnmodifiableBidiMap<V, K> inverse;
    
    public static <K, V> BidiMap<K, V> unmodifiableBidiMap(final BidiMap<? extends K, ? extends V> map) {
        if (map instanceof Unmodifiable) {
            final BidiMap<K, V> tmpMap = (BidiMap<K, V>)map;
            return tmpMap;
        }
        return new UnmodifiableBidiMap<K, V>(map);
    }
    
    private UnmodifiableBidiMap(final BidiMap<? extends K, ? extends V> map) {
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
    public MapIterator<K, V> mapIterator() {
        final MapIterator<K, V> it = this.decorated().mapIterator();
        return UnmodifiableMapIterator.unmodifiableMapIterator((MapIterator<? extends K, ? extends V>)it);
    }
    
    @Override
    public synchronized BidiMap<V, K> inverseBidiMap() {
        if (this.inverse == null) {
            this.inverse = (UnmodifiableBidiMap<V, K>)new UnmodifiableBidiMap(this.decorated().inverseBidiMap());
            this.inverse.inverse = (UnmodifiableBidiMap<V, K>)this;
        }
        return (BidiMap<V, K>)this.inverse;
    }
}
