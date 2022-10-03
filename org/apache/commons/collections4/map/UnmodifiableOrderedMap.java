package org.apache.commons.collections4.map;

import org.apache.commons.collections4.MapIterator;
import org.apache.commons.collections4.collection.UnmodifiableCollection;
import java.util.Collection;
import org.apache.commons.collections4.set.UnmodifiableSet;
import java.util.Set;
import org.apache.commons.collections4.iterators.UnmodifiableOrderedMapIterator;
import org.apache.commons.collections4.OrderedMapIterator;
import java.util.Map;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import org.apache.commons.collections4.OrderedMap;
import java.io.Serializable;
import org.apache.commons.collections4.Unmodifiable;

public final class UnmodifiableOrderedMap<K, V> extends AbstractOrderedMapDecorator<K, V> implements Unmodifiable, Serializable
{
    private static final long serialVersionUID = 8136428161720526266L;
    
    public static <K, V> OrderedMap<K, V> unmodifiableOrderedMap(final OrderedMap<? extends K, ? extends V> map) {
        if (map instanceof Unmodifiable) {
            final OrderedMap<K, V> tmpMap = (OrderedMap<K, V>)map;
            return tmpMap;
        }
        return new UnmodifiableOrderedMap<K, V>(map);
    }
    
    private UnmodifiableOrderedMap(final OrderedMap<? extends K, ? extends V> map) {
        super(map);
    }
    
    private void writeObject(final ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeObject(this.map);
    }
    
    private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.map = (Map)in.readObject();
    }
    
    @Override
    public OrderedMapIterator<K, V> mapIterator() {
        final OrderedMapIterator<K, V> it = this.decorated().mapIterator();
        return UnmodifiableOrderedMapIterator.unmodifiableOrderedMapIterator((OrderedMapIterator<K, ? extends V>)it);
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
    public Collection<V> values() {
        final Collection<V> coll = super.values();
        return UnmodifiableCollection.unmodifiableCollection((Collection<? extends V>)coll);
    }
}
