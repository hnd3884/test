package org.apache.commons.collections.iterators;

import java.lang.reflect.Array;
import java.util.NoSuchElementException;
import org.apache.commons.collections.ResettableListIterator;
import java.util.ListIterator;

public class ArrayListIterator extends ArrayIterator implements ListIterator, ResettableListIterator
{
    protected int lastItemIndex;
    
    public ArrayListIterator() {
        this.lastItemIndex = -1;
    }
    
    public ArrayListIterator(final Object array) {
        super(array);
        this.lastItemIndex = -1;
    }
    
    public ArrayListIterator(final Object array, final int startIndex) {
        super(array, startIndex);
        this.lastItemIndex = -1;
        this.startIndex = startIndex;
    }
    
    public ArrayListIterator(final Object array, final int startIndex, final int endIndex) {
        super(array, startIndex, endIndex);
        this.lastItemIndex = -1;
        this.startIndex = startIndex;
    }
    
    public boolean hasPrevious() {
        return this.index > this.startIndex;
    }
    
    public Object previous() {
        if (!this.hasPrevious()) {
            throw new NoSuchElementException();
        }
        final int n = this.index - 1;
        this.index = n;
        this.lastItemIndex = n;
        return Array.get(this.array, this.index);
    }
    
    public Object next() {
        if (!this.hasNext()) {
            throw new NoSuchElementException();
        }
        this.lastItemIndex = this.index;
        return Array.get(this.array, this.index++);
    }
    
    public int nextIndex() {
        return this.index - this.startIndex;
    }
    
    public int previousIndex() {
        return this.index - this.startIndex - 1;
    }
    
    public void add(final Object o) {
        throw new UnsupportedOperationException("add() method is not supported");
    }
    
    public void set(final Object o) {
        if (this.lastItemIndex == -1) {
            throw new IllegalStateException("must call next() or previous() before a call to set()");
        }
        Array.set(this.array, this.lastItemIndex, o);
    }
    
    public void reset() {
        super.reset();
        this.lastItemIndex = -1;
    }
}
