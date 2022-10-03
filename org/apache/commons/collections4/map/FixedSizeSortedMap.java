package org.apache.commons.collections4.map;

import org.apache.commons.collections4.collection.UnmodifiableCollection;
import org.apache.commons.collections4.set.UnmodifiableSet;
import java.util.Set;
import java.util.Collection;
import org.apache.commons.collections4.CollectionUtils;
import java.util.Map;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.SortedMap;
import java.io.Serializable;
import org.apache.commons.collections4.BoundedMap;

public class FixedSizeSortedMap<K, V> extends AbstractSortedMapDecorator<K, V> implements BoundedMap<K, V>, Serializable
{
    private static final long serialVersionUID = 3126019624511683653L;
    
    public static <K, V> FixedSizeSortedMap<K, V> fixedSizeSortedMap(final SortedMap<K, V> map) {
        return new FixedSizeSortedMap<K, V>(map);
    }
    
    protected FixedSizeSortedMap(final SortedMap<K, V> map) {
        super(map);
    }
    
    protected SortedMap<K, V> getSortedMap() {
        return (SortedMap)this.map;
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
    public V put(final K key, final V value) {
        if (!this.map.containsKey(key)) {
            throw new IllegalArgumentException("Cannot put new key/value pair - Map is fixed size");
        }
        return this.map.put(key, value);
    }
    
    @Override
    public void putAll(final Map<? extends K, ? extends V> mapToCopy) {
        if (CollectionUtils.isSubCollection(mapToCopy.keySet(), this.keySet())) {
            throw new IllegalArgumentException("Cannot put new key/value pair - Map is fixed size");
        }
        this.map.putAll(mapToCopy);
    }
    
    @Override
    public void clear() {
        throw new UnsupportedOperationException("Map is fixed size");
    }
    
    @Override
    public V remove(final Object key) {
        throw new UnsupportedOperationException("Map is fixed size");
    }
    
    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        return UnmodifiableSet.unmodifiableSet((Set<? extends Map.Entry<K, V>>)this.map.entrySet());
    }
    
    @Override
    public Set<K> keySet() {
        return UnmodifiableSet.unmodifiableSet((Set<? extends K>)this.map.keySet());
    }
    
    @Override
    public Collection<V> values() {
        return UnmodifiableCollection.unmodifiableCollection((Collection<? extends V>)this.map.values());
    }
    
    @Override
    public SortedMap<K, V> subMap(final K fromKey, final K toKey) {
        return new FixedSizeSortedMap((SortedMap<Object, Object>)this.getSortedMap().subMap(fromKey, toKey));
    }
    
    @Override
    public SortedMap<K, V> headMap(final K toKey) {
        return new FixedSizeSortedMap((SortedMap<Object, Object>)this.getSortedMap().headMap(toKey));
    }
    
    @Override
    public SortedMap<K, V> tailMap(final K fromKey) {
        return new FixedSizeSortedMap((SortedMap<Object, Object>)this.getSortedMap().tailMap(fromKey));
    }
    
    @Override
    public boolean isFull() {
        return true;
    }
    
    @Override
    public int maxSize() {
        return this.size();
    }
}
