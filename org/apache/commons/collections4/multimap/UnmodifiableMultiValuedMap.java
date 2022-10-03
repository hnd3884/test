package org.apache.commons.collections4.multimap;

import org.apache.commons.collections4.iterators.UnmodifiableMapIterator;
import org.apache.commons.collections4.MapIterator;
import org.apache.commons.collections4.map.UnmodifiableMap;
import org.apache.commons.collections4.multiset.UnmodifiableMultiSet;
import org.apache.commons.collections4.MultiSet;
import java.util.Map;
import org.apache.commons.collections4.set.UnmodifiableSet;
import java.util.Set;
import org.apache.commons.collections4.collection.UnmodifiableCollection;
import java.util.Collection;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.Unmodifiable;

public final class UnmodifiableMultiValuedMap<K, V> extends AbstractMultiValuedMapDecorator<K, V> implements Unmodifiable
{
    private static final long serialVersionUID = 20150612L;
    
    public static <K, V> UnmodifiableMultiValuedMap<K, V> unmodifiableMultiValuedMap(final MultiValuedMap<? extends K, ? extends V> map) {
        if (map instanceof Unmodifiable) {
            return (UnmodifiableMultiValuedMap)map;
        }
        return new UnmodifiableMultiValuedMap<K, V>(map);
    }
    
    private UnmodifiableMultiValuedMap(final MultiValuedMap<? extends K, ? extends V> map) {
        super(map);
    }
    
    @Override
    public Collection<V> remove(final Object key) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean removeMapping(final Object key, final Object item) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public Collection<V> get(final K key) {
        return UnmodifiableCollection.unmodifiableCollection((Collection<? extends V>)this.decorated().get(key));
    }
    
    @Override
    public boolean put(final K key, final V value) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public Set<K> keySet() {
        return UnmodifiableSet.unmodifiableSet((Set<? extends K>)this.decorated().keySet());
    }
    
    @Override
    public Collection<Map.Entry<K, V>> entries() {
        return UnmodifiableCollection.unmodifiableCollection((Collection<? extends Map.Entry<K, V>>)this.decorated().entries());
    }
    
    @Override
    public MultiSet<K> keys() {
        return UnmodifiableMultiSet.unmodifiableMultiSet((MultiSet<? extends K>)this.decorated().keys());
    }
    
    @Override
    public Collection<V> values() {
        return UnmodifiableCollection.unmodifiableCollection((Collection<? extends V>)this.decorated().values());
    }
    
    @Override
    public Map<K, Collection<V>> asMap() {
        return UnmodifiableMap.unmodifiableMap((Map<? extends K, ? extends Collection<V>>)this.decorated().asMap());
    }
    
    @Override
    public MapIterator<K, V> mapIterator() {
        return UnmodifiableMapIterator.unmodifiableMapIterator((MapIterator<? extends K, ? extends V>)this.decorated().mapIterator());
    }
    
    @Override
    public boolean putAll(final K key, final Iterable<? extends V> values) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean putAll(final Map<? extends K, ? extends V> map) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean putAll(final MultiValuedMap<? extends K, ? extends V> map) {
        throw new UnsupportedOperationException();
    }
}
