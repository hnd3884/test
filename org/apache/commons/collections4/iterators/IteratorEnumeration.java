package org.apache.commons.collections4.iterators;

import java.util.Iterator;
import java.util.Enumeration;

public class IteratorEnumeration<E> implements Enumeration<E>
{
    private Iterator<? extends E> iterator;
    
    public IteratorEnumeration() {
    }
    
    public IteratorEnumeration(final Iterator<? extends E> iterator) {
        this.iterator = iterator;
    }
    
    @Override
    public boolean hasMoreElements() {
        return this.iterator.hasNext();
    }
    
    @Override
    public E nextElement() {
        return (E)this.iterator.next();
    }
    
    public Iterator<? extends E> getIterator() {
        return this.iterator;
    }
    
    public void setIterator(final Iterator<? extends E> iterator) {
        this.iterator = iterator;
    }
}
