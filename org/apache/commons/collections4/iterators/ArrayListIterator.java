package org.apache.commons.collections4.iterators;

import java.lang.reflect.Array;
import java.util.NoSuchElementException;
import org.apache.commons.collections4.ResettableListIterator;

public class ArrayListIterator<E> extends ArrayIterator<E> implements ResettableListIterator<E>
{
    private int lastItemIndex;
    
    public ArrayListIterator(final Object array) {
        super(array);
        this.lastItemIndex = -1;
    }
    
    public ArrayListIterator(final Object array, final int startIndex) {
        super(array, startIndex);
        this.lastItemIndex = -1;
    }
    
    public ArrayListIterator(final Object array, final int startIndex, final int endIndex) {
        super(array, startIndex, endIndex);
        this.lastItemIndex = -1;
    }
    
    @Override
    public boolean hasPrevious() {
        return this.index > this.startIndex;
    }
    
    @Override
    public E previous() {
        if (!this.hasPrevious()) {
            throw new NoSuchElementException();
        }
        final int n = this.index - 1;
        this.index = n;
        this.lastItemIndex = n;
        return (E)Array.get(this.array, this.index);
    }
    
    @Override
    public E next() {
        if (!this.hasNext()) {
            throw new NoSuchElementException();
        }
        this.lastItemIndex = this.index;
        return (E)Array.get(this.array, this.index++);
    }
    
    @Override
    public int nextIndex() {
        return this.index - this.startIndex;
    }
    
    @Override
    public int previousIndex() {
        return this.index - this.startIndex - 1;
    }
    
    @Override
    public void add(final Object o) {
        throw new UnsupportedOperationException("add() method is not supported");
    }
    
    @Override
    public void set(final Object o) {
        if (this.lastItemIndex == -1) {
            throw new IllegalStateException("must call next() or previous() before a call to set()");
        }
        Array.set(this.array, this.lastItemIndex, o);
    }
    
    @Override
    public void reset() {
        super.reset();
        this.lastItemIndex = -1;
    }
}
