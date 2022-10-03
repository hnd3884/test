package org.apache.commons.collections4.iterators;

import java.util.NoSuchElementException;
import java.lang.reflect.Array;
import org.apache.commons.collections4.ResettableIterator;

public class ArrayIterator<E> implements ResettableIterator<E>
{
    final Object array;
    final int startIndex;
    final int endIndex;
    int index;
    
    public ArrayIterator(final Object array) {
        this(array, 0);
    }
    
    public ArrayIterator(final Object array, final int startIndex) {
        this(array, startIndex, Array.getLength(array));
    }
    
    public ArrayIterator(final Object array, final int startIndex, final int endIndex) {
        this.index = 0;
        this.array = array;
        this.startIndex = startIndex;
        this.endIndex = endIndex;
        this.index = startIndex;
        final int len = Array.getLength(array);
        this.checkBound(startIndex, len, "start");
        this.checkBound(endIndex, len, "end");
        if (endIndex < startIndex) {
            throw new IllegalArgumentException("End index must not be less than start index.");
        }
    }
    
    protected void checkBound(final int bound, final int len, final String type) {
        if (bound > len) {
            throw new ArrayIndexOutOfBoundsException("Attempt to make an ArrayIterator that " + type + "s beyond the end of the array. ");
        }
        if (bound < 0) {
            throw new ArrayIndexOutOfBoundsException("Attempt to make an ArrayIterator that " + type + "s before the start of the array. ");
        }
    }
    
    @Override
    public boolean hasNext() {
        return this.index < this.endIndex;
    }
    
    @Override
    public E next() {
        if (!this.hasNext()) {
            throw new NoSuchElementException();
        }
        return (E)Array.get(this.array, this.index++);
    }
    
    @Override
    public void remove() {
        throw new UnsupportedOperationException("remove() method is not supported");
    }
    
    public Object getArray() {
        return this.array;
    }
    
    public int getStartIndex() {
        return this.startIndex;
    }
    
    public int getEndIndex() {
        return this.endIndex;
    }
    
    @Override
    public void reset() {
        this.index = this.startIndex;
    }
}
