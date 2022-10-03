package org.jfree.util;

import java.util.Iterator;

public class ReadOnlyIterator implements Iterator
{
    private Iterator base;
    
    public ReadOnlyIterator(final Iterator it) {
        if (it == null) {
            throw new NullPointerException("Base iterator is null.");
        }
        this.base = it;
    }
    
    public boolean hasNext() {
        return this.base.hasNext();
    }
    
    public Object next() {
        return this.base.next();
    }
    
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
