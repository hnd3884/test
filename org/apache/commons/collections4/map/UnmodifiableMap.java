package org.apache.commons.collections4.map;

import org.apache.commons.collections4.collection.UnmodifiableCollection;
import java.util.Collection;
import org.apache.commons.collections4.set.UnmodifiableSet;
import java.util.Set;
import org.apache.commons.collections4.iterators.EntrySetMapIterator;
import org.apache.commons.collections4.iterators.UnmodifiableMapIterator;
import org.apache.commons.collections4.IterableMap;
import org.apache.commons.collections4.MapIterator;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Map;
import java.io.Serializable;
import org.apache.commons.collections4.Unmodifiable;

public final class UnmodifiableMap<K, V> extends AbstractMapDecorator<K, V> implements Unmodifiable, Serializable
{
    private static final long serialVersionUID = 2737023427269031941L;
    
    public static <K, V> Map<K, V> unmodifiableMap(final Map<? extends K, ? extends V> map) {
        if (map instanceof Unmodifiable) {
            final Map<K, V> tmpMap = (Map<K, V>)map;
            return tmpMap;
        }
        return new UnmodifiableMap<K, V>(map);
    }
    
    private UnmodifiableMap(final Map<? extends K, ? extends V> map) {
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
    public MapIterator<K, V> mapIterator() {
        if (this.map instanceof IterableMap) {
            final MapIterator<K, V> it = ((IterableMap)this.map).mapIterator();
            return UnmodifiableMapIterator.unmodifiableMapIterator((MapIterator<? extends K, ? extends V>)it);
        }
        final MapIterator<K, V> it = new EntrySetMapIterator<K, V>(this.map);
        return UnmodifiableMapIterator.unmodifiableMapIterator((MapIterator<? extends K, ? extends V>)it);
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
