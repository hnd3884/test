package org.apache.commons.collections.iterators;

import java.util.Enumeration;
import java.util.Collection;
import java.util.Iterator;

public class EnumerationIterator implements Iterator
{
    private Collection collection;
    private Enumeration enumeration;
    private Object last;
    
    public EnumerationIterator() {
        this(null, null);
    }
    
    public EnumerationIterator(final Enumeration enumeration) {
        this(enumeration, null);
    }
    
    public EnumerationIterator(final Enumeration enumeration, final Collection collection) {
        this.enumeration = enumeration;
        this.collection = collection;
        this.last = null;
    }
    
    public boolean hasNext() {
        return this.enumeration.hasMoreElements();
    }
    
    public Object next() {
        return this.last = this.enumeration.nextElement();
    }
    
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
    
    public Enumeration getEnumeration() {
        return this.enumeration;
    }
    
    public void setEnumeration(final Enumeration enumeration) {
        this.enumeration = enumeration;
    }
}
