package org.apache.commons.collections.iterators;

import java.util.Iterator;
import java.util.Enumeration;

public class IteratorEnumeration implements Enumeration
{
    private Iterator iterator;
    
    public IteratorEnumeration() {
    }
    
    public IteratorEnumeration(final Iterator iterator) {
        this.iterator = iterator;
    }
    
    public boolean hasMoreElements() {
        return this.iterator.hasNext();
    }
    
    public Object nextElement() {
        return this.iterator.next();
    }
    
    public Iterator getIterator() {
        return this.iterator;
    }
    
    public void setIterator(final Iterator iterator) {
        this.iterator = iterator;
    }
}
