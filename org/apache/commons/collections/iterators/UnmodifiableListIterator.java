package org.apache.commons.collections.iterators;

import org.apache.commons.collections.Unmodifiable;
import java.util.ListIterator;

public final class UnmodifiableListIterator implements ListIterator, Unmodifiable
{
    private ListIterator iterator;
    
    public static ListIterator decorate(final ListIterator iterator) {
        if (iterator == null) {
            throw new IllegalArgumentException("ListIterator must not be null");
        }
        if (iterator instanceof Unmodifiable) {
            return iterator;
        }
        return new UnmodifiableListIterator(iterator);
    }
    
    private UnmodifiableListIterator(final ListIterator iterator) {
        this.iterator = iterator;
    }
    
    public boolean hasNext() {
        return this.iterator.hasNext();
    }
    
    public Object next() {
        return this.iterator.next();
    }
    
    public int nextIndex() {
        return this.iterator.nextIndex();
    }
    
    public boolean hasPrevious() {
        return this.iterator.hasPrevious();
    }
    
    public Object previous() {
        return this.iterator.previous();
    }
    
    public int previousIndex() {
        return this.iterator.previousIndex();
    }
    
    public void remove() {
        throw new UnsupportedOperationException("remove() is not supported");
    }
    
    public void set(final Object obj) {
        throw new UnsupportedOperationException("set() is not supported");
    }
    
    public void add(final Object obj) {
        throw new UnsupportedOperationException("add() is not supported");
    }
}
