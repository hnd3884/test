package org.apache.commons.collections.iterators;

import java.util.ListIterator;
import java.util.List;
import org.apache.commons.collections.ResettableListIterator;

public class ReverseListIterator implements ResettableListIterator
{
    private final List list;
    private ListIterator iterator;
    private boolean validForUpdate;
    
    public ReverseListIterator(final List list) {
        this.validForUpdate = true;
        this.list = list;
        this.iterator = list.listIterator(list.size());
    }
    
    public boolean hasNext() {
        return this.iterator.hasPrevious();
    }
    
    public Object next() {
        final Object obj = this.iterator.previous();
        this.validForUpdate = true;
        return obj;
    }
    
    public int nextIndex() {
        return this.iterator.previousIndex();
    }
    
    public boolean hasPrevious() {
        return this.iterator.hasNext();
    }
    
    public Object previous() {
        final Object obj = this.iterator.next();
        this.validForUpdate = true;
        return obj;
    }
    
    public int previousIndex() {
        return this.iterator.nextIndex();
    }
    
    public void remove() {
        if (!this.validForUpdate) {
            throw new IllegalStateException("Cannot remove from list until next() or previous() called");
        }
        this.iterator.remove();
    }
    
    public void set(final Object obj) {
        if (!this.validForUpdate) {
            throw new IllegalStateException("Cannot set to list until next() or previous() called");
        }
        this.iterator.set(obj);
    }
    
    public void add(final Object obj) {
        if (!this.validForUpdate) {
            throw new IllegalStateException("Cannot add to list until next() or previous() called");
        }
        this.validForUpdate = false;
        this.iterator.add(obj);
        this.iterator.previous();
    }
    
    public void reset() {
        this.iterator = this.list.listIterator(this.list.size());
    }
}
