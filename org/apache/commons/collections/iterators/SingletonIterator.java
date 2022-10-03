package org.apache.commons.collections.iterators;

import java.util.NoSuchElementException;
import org.apache.commons.collections.ResettableIterator;
import java.util.Iterator;

public class SingletonIterator implements Iterator, ResettableIterator
{
    private final boolean removeAllowed;
    private boolean beforeFirst;
    private boolean removed;
    private Object object;
    
    public SingletonIterator(final Object object) {
        this(object, true);
    }
    
    public SingletonIterator(final Object object, final boolean removeAllowed) {
        this.beforeFirst = true;
        this.removed = false;
        this.object = object;
        this.removeAllowed = removeAllowed;
    }
    
    public boolean hasNext() {
        return this.beforeFirst && !this.removed;
    }
    
    public Object next() {
        if (!this.beforeFirst || this.removed) {
            throw new NoSuchElementException();
        }
        this.beforeFirst = false;
        return this.object;
    }
    
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
    
    public void reset() {
        this.beforeFirst = true;
    }
}
