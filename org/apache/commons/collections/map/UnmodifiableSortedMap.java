package org.apache.commons.collections.map;

import java.util.Comparator;
import org.apache.commons.collections.collection.UnmodifiableCollection;
import java.util.Collection;
import org.apache.commons.collections.set.UnmodifiableSet;
import java.util.Set;
import java.util.Map;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.SortedMap;
import java.io.Serializable;
import org.apache.commons.collections.Unmodifiable;

public final class UnmodifiableSortedMap extends AbstractSortedMapDecorator implements Unmodifiable, Serializable
{
    private static final long serialVersionUID = 5805344239827376360L;
    
    public static SortedMap decorate(final SortedMap map) {
        if (map instanceof Unmodifiable) {
            return map;
        }
        return new UnmodifiableSortedMap(map);
    }
    
    private UnmodifiableSortedMap(final SortedMap map) {
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
    
    public void clear() {
        throw new UnsupportedOperationException();
    }
    
    public Object put(final Object key, final Object value) {
        throw new UnsupportedOperationException();
    }
    
    public void putAll(final Map mapToCopy) {
        throw new UnsupportedOperationException();
    }
    
    public Object remove(final Object key) {
        throw new UnsupportedOperationException();
    }
    
    public Set entrySet() {
        final Set set = super.entrySet();
        return UnmodifiableEntrySet.decorate(set);
    }
    
    public Set keySet() {
        final Set set = super.keySet();
        return UnmodifiableSet.decorate(set);
    }
    
    public Collection values() {
        final Collection coll = super.values();
        return UnmodifiableCollection.decorate(coll);
    }
    
    public Object firstKey() {
        return this.getSortedMap().firstKey();
    }
    
    public Object lastKey() {
        return this.getSortedMap().lastKey();
    }
    
    public Comparator comparator() {
        return this.getSortedMap().comparator();
    }
    
    public SortedMap subMap(final Object fromKey, final Object toKey) {
        final SortedMap map = this.getSortedMap().subMap(fromKey, toKey);
        return new UnmodifiableSortedMap(map);
    }
    
    public SortedMap headMap(final Object toKey) {
        final SortedMap map = this.getSortedMap().headMap(toKey);
        return new UnmodifiableSortedMap(map);
    }
    
    public SortedMap tailMap(final Object fromKey) {
        final SortedMap map = this.getSortedMap().tailMap(fromKey);
        return new UnmodifiableSortedMap(map);
    }
}
