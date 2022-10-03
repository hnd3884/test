package com.sun.xml.internal.stream.util;

import java.util.Iterator;

public class ReadOnlyIterator implements Iterator
{
    Iterator iterator;
    
    public ReadOnlyIterator() {
        this.iterator = null;
    }
    
    public ReadOnlyIterator(final Iterator itr) {
        this.iterator = null;
        this.iterator = itr;
    }
    
    @Override
    public boolean hasNext() {
        return this.iterator != null && this.iterator.hasNext();
    }
    
    @Override
    public Object next() {
        if (this.iterator != null) {
            return this.iterator.next();
        }
        return null;
    }
    
    @Override
    public void remove() {
        throw new UnsupportedOperationException("Remove operation is not supported");
    }
}
