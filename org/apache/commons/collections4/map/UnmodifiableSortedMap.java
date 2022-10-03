package org.apache.commons.collections4.map;

import java.util.Comparator;
import org.apache.commons.collections4.collection.UnmodifiableCollection;
import java.util.Collection;
import org.apache.commons.collections4.set.UnmodifiableSet;
import java.util.Set;
import java.util.Map;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.SortedMap;
import java.io.Serializable;
import org.apache.commons.collections4.Unmodifiable;

public final class UnmodifiableSortedMap<K, V> extends AbstractSortedMapDecorator<K, V> implements Unmodifiable, Serializable
{
    private static final long serialVersionUID = 5805344239827376360L;
    
    public static <K, V> SortedMap<K, V> unmodifiableSortedMap(final SortedMap<K, ? extends V> map) {
        if (map instanceof Unmodifiable) {
            final SortedMap<K, V> tmpMap = (SortedMap<K, V>)map;
            return tmpMap;
        }
        return new UnmodifiableSortedMap<K, V>(map);
    }
    
    private UnmodifiableSortedMap(final SortedMap<K, ? extends V> map) {
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
        return UnmodifiableEntrySet.unmodifiableEntrySet(super.entrySet());
    }
    
    @Override
    public Set<K> keySet() {
        return UnmodifiableSet.unmodifiableSet(super.keySet());
    }
    
    @Override
    public Collection<V> values() {
        return UnmodifiableCollection.unmodifiableCollection(super.values());
    }
    
    @Override
    public K firstKey() {
        return this.decorated().firstKey();
    }
    
    @Override
    public K lastKey() {
        return this.decorated().lastKey();
    }
    
    @Override
    public Comparator<? super K> comparator() {
        return this.decorated().comparator();
    }
    
    @Override
    public SortedMap<K, V> subMap(final K fromKey, final K toKey) {
        return new UnmodifiableSortedMap((SortedMap<Object, ?>)this.decorated().subMap(fromKey, toKey));
    }
    
    @Override
    public SortedMap<K, V> headMap(final K toKey) {
        return new UnmodifiableSortedMap((SortedMap<Object, ?>)this.decorated().headMap(toKey));
    }
    
    @Override
    public SortedMap<K, V> tailMap(final K fromKey) {
        return new UnmodifiableSortedMap((SortedMap<Object, ?>)this.decorated().tailMap(fromKey));
    }
}
