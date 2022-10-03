package org.apache.commons.collections4.iterators;

import java.util.Enumeration;
import java.util.Collection;
import java.util.Iterator;

public class EnumerationIterator<E> implements Iterator<E>
{
    private final Collection<? super E> collection;
    private Enumeration<? extends E> enumeration;
    private E last;
    
    public EnumerationIterator() {
        this(null, null);
    }
    
    public EnumerationIterator(final Enumeration<? extends E> enumeration) {
        this(enumeration, null);
    }
    
    public EnumerationIterator(final Enumeration<? extends E> enumeration, final Collection<? super E> collection) {
        this.enumeration = enumeration;
        this.collection = collection;
        this.last = null;
    }
    
    @Override
    public boolean hasNext() {
        return this.enumeration.hasMoreElements();
    }
    
    @Override
    public E next() {
        return this.last = (E)this.enumeration.nextElement();
    }
    
    @Override
    public void remove() {
        if (this.collection == null) {
            throw new UnsupportedOperationException("No Collection associated with this Iterator");
        }
        if (this.last != null) {
            this.collection.remove(this.last);
            return;
        }
        throw new IllegalStateException("next() must have been called for remove() to function");
    }
    
    public Enumeration<? extends E> getEnumeration() {
        return this.enumeration;
    }
    
    public void setEnumeration(final Enumeration<? extends E> enumeration) {
        this.enumeration = enumeration;
    }
}
