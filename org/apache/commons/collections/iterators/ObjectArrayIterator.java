package org.apache.commons.collections.iterators;

import java.util.NoSuchElementException;
import org.apache.commons.collections.ResettableIterator;
import java.util.Iterator;

public class ObjectArrayIterator implements Iterator, ResettableIterator
{
    protected Object[] array;
    protected int startIndex;
    protected int endIndex;
    protected int index;
    
    public ObjectArrayIterator() {
        this.array = null;
        this.startIndex = 0;
        this.endIndex = 0;
        this.index = 0;
    }
    
    public ObjectArrayIterator(final Object[] array) {
        this(array, 0, array.length);
    }
    
    public ObjectArrayIterator(final Object[] array, final int start) {
        this(array, start, array.length);
    }
    
    public ObjectArrayIterator(final Object[] array, final int start, final int end) {
        this.array = null;
        this.startIndex = 0;
        this.endIndex = 0;
        this.index = 0;
        if (start < 0) {
            throw new ArrayIndexOutOfBoundsException("Start index must not be less than zero");
        }
        if (end > array.length) {
            throw new ArrayIndexOutOfBoundsException("End index must not be greater than the array length");
        }
        if (start > array.length) {
            throw new ArrayIndexOutOfBoundsException("Start index must not be greater than the array length");
        }
        if (end < start) {
            throw new IllegalArgumentException("End index must not be less than start index");
        }
        this.array = array;
        this.startIndex = start;
        this.endIndex = end;
        this.index = start;
    }
    
    public boolean hasNext() {
        return this.index < this.endIndex;
    }
    
    public Object next() {
        if (!this.hasNext()) {
            throw new NoSuchElementException();
        }
        return this.array[this.index++];
    }
    
    public void remove() {
        throw new UnsupportedOperationException("remove() method is not supported for an ObjectArrayIterator");
    }
    
    public Object[] getArray() {
        return this.array;
    }
    
    public void setArray(final Object[] array) {
        if (this.array != null) {
            throw new IllegalStateException("The array to iterate over has already been set");
        }
        this.array = array;
        this.startIndex = 0;
        this.endIndex = array.length;
        this.index = 0;
    }
    
    public int getStartIndex() {
        return this.startIndex;
    }
    
    public int getEndIndex() {
        return this.endIndex;
    }
    
    public void reset() {
        this.index = this.startIndex;
    }
}
