package com.sun.xml.internal.bind.v2.util;

import java.util.NoSuchElementException;
import java.util.Map;
import java.util.Iterator;

public final class FlattenIterator<T> implements Iterator<T>
{
    private final Iterator<? extends Map<?, ? extends T>> parent;
    private Iterator<? extends T> child;
    private T next;
    
    public FlattenIterator(final Iterable<? extends Map<?, ? extends T>> core) {
        this.child = null;
        this.parent = core.iterator();
    }
    
    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean hasNext() {
        this.getNext();
        return this.next != null;
    }
    
    @Override
    public T next() {
        final T r = this.next;
        this.next = null;
        if (r == null) {
            throw new NoSuchElementException();
        }
        return r;
    }
    
    private void getNext() {
        if (this.next != null) {
            return;
        }
        if (this.child != null && this.child.hasNext()) {
            this.next = (T)this.child.next();
            return;
        }
        if (this.parent.hasNext()) {
            this.child = ((Map)this.parent.next()).values().iterator();
            this.getNext();
        }
    }
}
