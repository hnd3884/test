package org.apache.commons.collections.iterators;

import java.util.Iterator;

public class AbstractIteratorDecorator implements Iterator
{
    protected final Iterator iterator;
    
    public AbstractIteratorDecorator(final Iterator iterator) {
        if (iterator == null) {
            throw new IllegalArgumentException("Iterator must not be null");
        }
        this.iterator = iterator;
    }
    
    protected Iterator getIterator() {
        return this.iterator;
    }
    
    public boolean hasNext() {
        return this.iterator.hasNext();
    }
    
    public Object next() {
        return this.iterator.next();
    }
    
    public void remove() {
        this.iterator.remove();
    }
}
