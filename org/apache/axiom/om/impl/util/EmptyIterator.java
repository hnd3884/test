package org.apache.axiom.om.impl.util;

import java.util.NoSuchElementException;
import java.util.Iterator;

public class EmptyIterator implements Iterator
{
    public void remove() {
        throw new UnsupportedOperationException();
    }
    
    public boolean hasNext() {
        return false;
    }
    
    public Object next() {
        throw new NoSuchElementException();
    }
}
