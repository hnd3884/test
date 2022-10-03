package org.antlr.v4.runtime.misc;

import java.util.Collection;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.LinkedHashSet;

public class OrderedHashSet<T> extends LinkedHashSet<T>
{
    protected ArrayList<T> elements;
    
    public OrderedHashSet() {
        this.elements = new ArrayList<T>();
    }
    
    public T get(final int i) {
        return this.elements.get(i);
    }
    
    public T set(final int i, final T value) {
        final T oldElement = this.elements.get(i);
        this.elements.set(i, value);
        super.remove(oldElement);
        super.add(value);
        return oldElement;
    }
    
    public boolean remove(final int i) {
        final T o = this.elements.remove(i);
        return super.remove(o);
    }
    
    @Override
    public boolean add(final T value) {
        final boolean result = super.add(value);
        if (result) {
            this.elements.add(value);
        }
        return result;
    }
    
    @Override
    public boolean remove(final Object o) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void clear() {
        this.elements.clear();
        super.clear();
    }
    
    @Override
    public int hashCode() {
        return this.elements.hashCode();
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof OrderedHashSet)) {
            return false;
        }
        final boolean same = this.elements != null && this.elements.equals(((OrderedHashSet)o).elements);
        return same;
    }
    
    @Override
    public Iterator<T> iterator() {
        return this.elements.iterator();
    }
    
    public List<T> elements() {
        return this.elements;
    }
    
    @Override
    public Object clone() {
        final OrderedHashSet<T> dup = (OrderedHashSet<T>)super.clone();
        dup.elements = new ArrayList<T>((Collection<? extends T>)this.elements);
        return dup;
    }
    
    @Override
    public Object[] toArray() {
        return this.elements.toArray();
    }
    
    @Override
    public String toString() {
        return this.elements.toString();
    }
}
