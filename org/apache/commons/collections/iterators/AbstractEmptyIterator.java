package org.apache.commons.collections.iterators;

import java.util.NoSuchElementException;

abstract class AbstractEmptyIterator
{
    protected AbstractEmptyIterator() {
    }
    
    public boolean hasNext() {
        return false;
    }
    
    public Object next() {
        throw new NoSuchElementException("Iterator contains no elements");
    }
    
    public boolean hasPrevious() {
        return false;
    }
    
    public Object previous() {
        throw new NoSuchElementException("Iterator contains no elements");
    }
    
    public int nextIndex() {
        return 0;
    }
    
    public int previousIndex() {
        return -1;
    }
    
    public void add(final Object obj) {
        throw new UnsupportedOperationException("add() not supported for empty Iterator");
    }
    
    public void set(final Object obj) {
        throw new IllegalStateException("Iterator contains no elements");
    }
    
    public void remove() {
        throw new IllegalStateException("Iterator contains no elements");
    }
    
    public Object getKey() {
        throw new IllegalStateException("Iterator contains no elements");
    }
    
    public Object getValue() {
        throw new IllegalStateException("Iterator contains no elements");
    }
    
    public Object setValue(final Object value) {
        throw new IllegalStateException("Iterator contains no elements");
    }
    
    public void reset() {
    }
}
