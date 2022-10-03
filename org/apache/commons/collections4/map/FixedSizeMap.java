package org.apache.commons.collections4.map;

import org.apache.commons.collections4.collection.UnmodifiableCollection;
import java.util.Collection;
import org.apache.commons.collections4.set.UnmodifiableSet;
import java.util.Set;
import java.util.Iterator;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Map;
import java.io.Serializable;
import org.apache.commons.collections4.BoundedMap;

public class FixedSizeMap<K, V> extends AbstractMapDecorator<K, V> implements BoundedMap<K, V>, Serializable
{
    private static final long serialVersionUID = 7450927208116179316L;
    
    public static <K, V> FixedSizeMap<K, V> fixedSizeMap(final Map<K, V> map) {
        return new FixedSizeMap<K, V>(map);
    }
    
    protected FixedSizeMap(final Map<K, V> map) {
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
    public V put(final K key, final V value) {
        if (!this.map.containsKey(key)) {
            throw new IllegalArgumentException("Cannot put new key/value pair - Map is fixed size");
        }
        return this.map.put(key, value);
    }
    
    @Override
    public void putAll(final Map<? extends K, ? extends V> mapToCopy) {
        for (final K key : mapToCopy.keySet()) {
            if (!this.containsKey(key)) {
                throw new IllegalArgumentException("Cannot put new key/value pair - Map is fixed size");
            }
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
        final Set<Map.Entry<K, V>> set = this.map.entrySet();
        return UnmodifiableSet.unmodifiableSet((Set<? extends Map.Entry<K, V>>)set);
    }
    
    @Override
    public Set<K> keySet() {
        final Set<K> set = this.map.keySet();
        return UnmodifiableSet.unmodifiableSet((Set<? extends K>)set);
    }
    
    @Override
    public Collection<V> values() {
        final Collection<V> coll = this.map.values();
        return UnmodifiableCollection.unmodifiableCollection((Collection<? extends V>)coll);
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
