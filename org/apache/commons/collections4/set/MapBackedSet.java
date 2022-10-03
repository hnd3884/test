package org.apache.commons.collections4.set;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.io.Serializable;
import java.util.Set;

public final class MapBackedSet<E, V> implements Set<E>, Serializable
{
    private static final long serialVersionUID = 6723912213766056587L;
    private final Map<E, ? super V> map;
    private final V dummyValue;
    
    public static <E, V> MapBackedSet<E, V> mapBackedSet(final Map<E, ? super V> map) {
        return mapBackedSet(map, (V)null);
    }
    
    public static <E, V> MapBackedSet<E, V> mapBackedSet(final Map<E, ? super V> map, final V dummyValue) {
        return new MapBackedSet<E, V>(map, dummyValue);
    }
    
    private MapBackedSet(final Map<E, ? super V> map, final V dummyValue) {
        if (map == null) {
            throw new NullPointerException("The map must not be null");
        }
        this.map = map;
        this.dummyValue = dummyValue;
    }
    
    @Override
    public int size() {
        return this.map.size();
    }
    
    @Override
    public boolean isEmpty() {
        return this.map.isEmpty();
    }
    
    @Override
    public Iterator<E> iterator() {
        return this.map.keySet().iterator();
    }
    
    @Override
    public boolean contains(final Object obj) {
        return this.map.containsKey(obj);
    }
    
    @Override
    public boolean containsAll(final Collection<?> coll) {
        return this.map.keySet().containsAll(coll);
    }
    
    @Override
    public boolean add(final E obj) {
        final int size = this.map.size();
        this.map.put(obj, this.dummyValue);
        return this.map.size() != size;
    }
    
    @Override
    public boolean addAll(final Collection<? extends E> coll) {
        final int size = this.map.size();
        for (final E e : coll) {
            this.map.put(e, this.dummyValue);
        }
        return this.map.size() != size;
    }
    
    @Override
    public boolean remove(final Object obj) {
        final int size = this.map.size();
        this.map.remove(obj);
        return this.map.size() != size;
    }
    
    @Override
    public boolean removeAll(final Collection<?> coll) {
        return this.map.keySet().removeAll(coll);
    }
    
    @Override
    public boolean retainAll(final Collection<?> coll) {
        return this.map.keySet().retainAll(coll);
    }
    
    @Override
    public void clear() {
        this.map.clear();
    }
    
    @Override
    public Object[] toArray() {
        return this.map.keySet().toArray();
    }
    
    @Override
    public <T> T[] toArray(final T[] array) {
        return this.map.keySet().toArray(array);
    }
    
    @Override
    public boolean equals(final Object obj) {
        return this.map.keySet().equals(obj);
    }
    
    @Override
    public int hashCode() {
        return this.map.keySet().hashCode();
    }
}
