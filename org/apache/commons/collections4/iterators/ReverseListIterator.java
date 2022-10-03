package org.apache.commons.collections4.iterators;

import java.util.ListIterator;
import java.util.List;
import org.apache.commons.collections4.ResettableListIterator;

public class ReverseListIterator<E> implements ResettableListIterator<E>
{
    private final List<E> list;
    private ListIterator<E> iterator;
    private boolean validForUpdate;
    
    public ReverseListIterator(final List<E> list) {
        this.validForUpdate = true;
        if (list == null) {
            throw new NullPointerException("List must not be null.");
        }
        this.list = list;
        this.iterator = list.listIterator(list.size());
    }
    
    @Override
    public boolean hasNext() {
        return this.iterator.hasPrevious();
    }
    
    @Override
    public E next() {
        final E obj = this.iterator.previous();
        this.validForUpdate = true;
        return obj;
    }
    
    @Override
    public int nextIndex() {
        return this.iterator.previousIndex();
    }
    
    @Override
    public boolean hasPrevious() {
        return this.iterator.hasNext();
    }
    
    @Override
    public E previous() {
        final E obj = this.iterator.next();
        this.validForUpdate = true;
        return obj;
    }
    
    @Override
    public int previousIndex() {
        return this.iterator.nextIndex();
    }
    
    @Override
    public void remove() {
        if (!this.validForUpdate) {
            throw new IllegalStateException("Cannot remove from list until next() or previous() called");
        }
        this.iterator.remove();
    }
    
    @Override
    public void set(final E obj) {
        if (!this.validForUpdate) {
            throw new IllegalStateException("Cannot set to list until next() or previous() called");
        }
        this.iterator.set(obj);
    }
    
    @Override
    public void add(final E obj) {
        if (!this.validForUpdate) {
            throw new IllegalStateException("Cannot add to list until next() or previous() called");
        }
        this.validForUpdate = false;
        this.iterator.add(obj);
        this.iterator.previous();
    }
    
    @Override
    public void reset() {
        this.iterator = this.list.listIterator(this.list.size());
    }
}
