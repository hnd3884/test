package org.apache.commons.collections4.iterators;

import java.util.NoSuchElementException;
import org.apache.commons.collections4.Predicate;
import java.util.Iterator;

public class FilterIterator<E> implements Iterator<E>
{
    private Iterator<? extends E> iterator;
    private Predicate<? super E> predicate;
    private E nextObject;
    private boolean nextObjectSet;
    
    public FilterIterator() {
        this.nextObjectSet = false;
    }
    
    public FilterIterator(final Iterator<? extends E> iterator) {
        this.nextObjectSet = false;
        this.iterator = iterator;
    }
    
    public FilterIterator(final Iterator<? extends E> iterator, final Predicate<? super E> predicate) {
        this.nextObjectSet = false;
        this.iterator = iterator;
        this.predicate = predicate;
    }
    
    @Override
    public boolean hasNext() {
        return this.nextObjectSet || this.setNextObject();
    }
    
    @Override
    public E next() {
        if (!this.nextObjectSet && !this.setNextObject()) {
            throw new NoSuchElementException();
        }
        this.nextObjectSet = false;
        return this.nextObject;
    }
    
    @Override
    public void remove() {
        if (this.nextObjectSet) {
            throw new IllegalStateException("remove() cannot be called");
        }
        this.iterator.remove();
    }
    
    public Iterator<? extends E> getIterator() {
        return this.iterator;
    }
    
    public void setIterator(final Iterator<? extends E> iterator) {
        this.iterator = iterator;
        this.nextObject = null;
        this.nextObjectSet = false;
    }
    
    public Predicate<? super E> getPredicate() {
        return this.predicate;
    }
    
    public void setPredicate(final Predicate<? super E> predicate) {
        this.predicate = predicate;
        this.nextObject = null;
        this.nextObjectSet = false;
    }
    
    private boolean setNextObject() {
        while (this.iterator.hasNext()) {
            final E object = (E)this.iterator.next();
            if (this.predicate.evaluate(object)) {
                this.nextObject = object;
                return this.nextObjectSet = true;
            }
        }
        return false;
    }
}
