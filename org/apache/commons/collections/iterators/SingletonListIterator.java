package org.apache.commons.collections.iterators;

import java.util.NoSuchElementException;
import org.apache.commons.collections.ResettableListIterator;
import java.util.ListIterator;

public class SingletonListIterator implements ListIterator, ResettableListIterator
{
    private boolean beforeFirst;
    private boolean nextCalled;
    private boolean removed;
    private Object object;
    
    public SingletonListIterator(final Object object) {
        this.beforeFirst = true;
        this.nextCalled = false;
        this.removed = false;
        this.object = object;
    }
    
    public boolean hasNext() {
        return this.beforeFirst && !this.removed;
    }
    
    public boolean hasPrevious() {
        return !this.beforeFirst && !this.removed;
    }
    
    public int nextIndex() {
        return this.beforeFirst ? 0 : 1;
    }
    
    public int previousIndex() {
        return this.beforeFirst ? -1 : 0;
    }
    
    public Object next() {
        if (!this.beforeFirst || this.removed) {
            throw new NoSuchElementException();
        }
        this.beforeFirst = false;
        this.nextCalled = true;
        return this.object;
    }
    
    public Object previous() {
        if (this.beforeFirst || this.removed) {
            throw new NoSuchElementException();
        }
        this.beforeFirst = true;
        return this.object;
    }
    
    public void remove() {
        if (!this.nextCalled || this.removed) {
            throw new IllegalStateException();
        }
        this.object = null;
        this.removed = true;
    }
    
    public void add(final Object obj) {
        throw new UnsupportedOperationException("add() is not supported by this iterator");
    }
    
    public void set(final Object obj) {
        if (!this.nextCalled || this.removed) {
            throw new IllegalStateException();
        }
        this.object = obj;
    }
    
    public void reset() {
        this.beforeFirst = true;
        this.nextCalled = false;
    }
}
