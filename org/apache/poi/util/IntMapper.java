package org.apache.poi.util;

import java.util.Iterator;
import java.util.Collection;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map;
import java.util.List;
import org.apache.poi.common.Duplicatable;

public class IntMapper<T> implements Duplicatable
{
    private final List<T> elements;
    private final Map<T, Integer> valueKeyMap;
    private static final int _default_size = 10;
    
    public IntMapper() {
        this(10);
    }
    
    public IntMapper(final int initialCapacity) {
        this.elements = new ArrayList<T>(initialCapacity);
        this.valueKeyMap = new HashMap<T, Integer>(initialCapacity);
    }
    
    public IntMapper(final IntMapper<T> other) {
        this.elements = new ArrayList<T>((Collection<? extends T>)other.elements);
        this.valueKeyMap = new HashMap<T, Integer>((Map<? extends T, ? extends Integer>)other.valueKeyMap);
    }
    
    public boolean add(final T value) {
        final int index = this.elements.size();
        this.elements.add(value);
        this.valueKeyMap.put(value, index);
        return true;
    }
    
    public int size() {
        return this.elements.size();
    }
    
    public T get(final int index) {
        return this.elements.get(index);
    }
    
    public int getIndex(final T o) {
        return this.valueKeyMap.getOrDefault(o, -1);
    }
    
    public Iterator<T> iterator() {
        return this.elements.iterator();
    }
    
    @Override
    public IntMapper<T> copy() {
        return new IntMapper<T>(this);
    }
}
