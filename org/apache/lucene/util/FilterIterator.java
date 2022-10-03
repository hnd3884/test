package org.apache.lucene.util;

import java.util.NoSuchElementException;
import java.util.Iterator;

public abstract class FilterIterator<T, InnerT extends T> implements Iterator<T>
{
    private final Iterator<InnerT> iterator;
    private T next;
    private boolean nextIsSet;
    
    protected abstract boolean predicateFunction(final InnerT p0);
    
    public FilterIterator(final Iterator<InnerT> baseIterator) {
        this.next = null;
        this.nextIsSet = false;
        this.iterator = baseIterator;
    }
    
    @Override
    public final boolean hasNext() {
        return this.nextIsSet || this.setNext();
    }
    
    @Override
    public final T next() {
        if (!this.hasNext()) {
            throw new NoSuchElementException();
        }
        assert this.nextIsSet;
        try {
            return this.next;
        }
        finally {
            this.nextIsSet = false;
            this.next = null;
        }
    }
    
    @Override
    public final void remove() {
        throw new UnsupportedOperationException();
    }
    
    private boolean setNext() {
        while (this.iterator.hasNext()) {
            final InnerT object = this.iterator.next();
            if (this.predicateFunction(object)) {
                this.next = object;
                return this.nextIsSet = true;
            }
        }
        return false;
    }
}
