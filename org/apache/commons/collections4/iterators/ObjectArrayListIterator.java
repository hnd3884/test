package org.apache.commons.collections4.iterators;

import java.util.NoSuchElementException;
import org.apache.commons.collections4.ResettableListIterator;

public class ObjectArrayListIterator<E> extends ObjectArrayIterator<E> implements ResettableListIterator<E>
{
    private int lastItemIndex;
    
    public ObjectArrayListIterator(final E... array) {
        super(array);
        this.lastItemIndex = -1;
    }
    
    public ObjectArrayListIterator(final E[] array, final int start) {
        super(array, start);
        this.lastItemIndex = -1;
    }
    
    public ObjectArrayListIterator(final E[] array, final int start, final int end) {
        super(array, start, end);
        this.lastItemIndex = -1;
    }
    
    @Override
    public boolean hasPrevious() {
        return this.index > this.getStartIndex();
    }
    
    @Override
    public E previous() {
        if (!this.hasPrevious()) {
            throw new NoSuchElementException();
        }
        final int n = this.index - 1;
        this.index = n;
        this.lastItemIndex = n;
        return this.array[this.index];
    }
    
    @Override
    public E next() {
        if (!this.hasNext()) {
            throw new NoSuchElementException();
        }
        this.lastItemIndex = this.index;
        return this.array[this.index++];
    }
    
    @Override
    public int nextIndex() {
        return this.index - this.getStartIndex();
    }
    
    @Override
    public int previousIndex() {
        return this.index - this.getStartIndex() - 1;
    }
    
    @Override
    public void add(final E obj) {
        throw new UnsupportedOperationException("add() method is not supported");
    }
    
    @Override
    public void set(final E obj) {
        if (this.lastItemIndex == -1) {
            throw new IllegalStateException("must call next() or previous() before a call to set()");
        }
        this.array[this.lastItemIndex] = obj;
    }
    
    @Override
    public void reset() {
        super.reset();
        this.lastItemIndex = -1;
    }
}
