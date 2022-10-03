package org.apache.commons.collections4.iterators;

import java.util.NoSuchElementException;
import org.apache.commons.collections4.ResettableIterator;

public class SingletonIterator<E> implements ResettableIterator<E>
{
    private final boolean removeAllowed;
    private boolean beforeFirst;
    private boolean removed;
    private E object;
    
    public SingletonIterator(final E object) {
        this(object, true);
    }
    
    public SingletonIterator(final E object, final boolean removeAllowed) {
        this.beforeFirst = true;
        this.removed = false;
        this.object = object;
        this.removeAllowed = removeAllowed;
    }
    
    @Override
    public boolean hasNext() {
        return this.beforeFirst && !this.removed;
    }
    
    @Override
    public E next() {
        if (!this.beforeFirst || this.removed) {
            throw new NoSuchElementException();
        }
        this.beforeFirst = false;
        return this.object;
    }
    
    @Override
    public void remove() {
        if (!this.removeAllowed) {
            throw new UnsupportedOperationException();
        }
        if (this.removed || this.beforeFirst) {
            throw new IllegalStateException();
        }
        this.object = null;
        this.removed = true;
    }
    
    @Override
    public void reset() {
        this.beforeFirst = true;
    }
}
