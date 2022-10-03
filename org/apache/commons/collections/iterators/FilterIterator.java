package org.apache.commons.collections.iterators;

import java.util.NoSuchElementException;
import org.apache.commons.collections.Predicate;
import java.util.Iterator;

public class FilterIterator implements Iterator
{
    private Iterator iterator;
    private Predicate predicate;
    private Object nextObject;
    private boolean nextObjectSet;
    
    public FilterIterator() {
        this.nextObjectSet = false;
    }
    
    public FilterIterator(final Iterator iterator) {
        this.nextObjectSet = false;
        this.iterator = iterator;
    }
    
    public FilterIterator(final Iterator iterator, final Predicate predicate) {
        this.nextObjectSet = false;
        this.iterator = iterator;
        this.predicate = predicate;
    }
    
    public boolean hasNext() {
        return this.nextObjectSet || this.setNextObject();
    }
    
    public Object next() {
        if (!this.nextObjectSet && !this.setNextObject()) {
            throw new NoSuchElementException();
        }
        this.nextObjectSet = false;
        return this.nextObject;
    }
    
    public void remove() {
        if (this.nextObjectSet) {
            throw new IllegalStateException("remove() cannot be called");
        }
        this.iterator.remove();
    }
    
    public Iterator getIterator() {
        return this.iterator;
    }
    
    public void setIterator(final Iterator iterator) {
        this.iterator = iterator;
        this.nextObject = null;
        this.nextObjectSet = false;
    }
    
    public Predicate getPredicate() {
        return this.predicate;
    }
    
    public void setPredicate(final Predicate predicate) {
        this.predicate = predicate;
        this.nextObject = null;
        this.nextObjectSet = false;
    }
    
    private boolean setNextObject() {
        while (this.iterator.hasNext()) {
            final Object object = this.iterator.next();
            if (this.predicate.evaluate(object)) {
                this.nextObject = object;
                return this.nextObjectSet = true;
            }
        }
        return false;
    }
}
