package org.apache.commons.collections4.iterators;

import java.util.NoSuchElementException;
import org.apache.commons.collections4.Predicate;
import java.util.ListIterator;

public class FilterListIterator<E> implements ListIterator<E>
{
    private ListIterator<? extends E> iterator;
    private Predicate<? super E> predicate;
    private E nextObject;
    private boolean nextObjectSet;
    private E previousObject;
    private boolean previousObjectSet;
    private int nextIndex;
    
    public FilterListIterator() {
        this.nextObjectSet = false;
        this.previousObjectSet = false;
        this.nextIndex = 0;
    }
    
    public FilterListIterator(final ListIterator<? extends E> iterator) {
        this.nextObjectSet = false;
        this.previousObjectSet = false;
        this.nextIndex = 0;
        this.iterator = iterator;
    }
    
    public FilterListIterator(final ListIterator<? extends E> iterator, final Predicate<? super E> predicate) {
        this.nextObjectSet = false;
        this.previousObjectSet = false;
        this.nextIndex = 0;
        this.iterator = iterator;
        this.predicate = predicate;
    }
    
    public FilterListIterator(final Predicate<? super E> predicate) {
        this.nextObjectSet = false;
        this.previousObjectSet = false;
        this.nextIndex = 0;
        this.predicate = predicate;
    }
    
    @Override
    public void add(final E o) {
        throw new UnsupportedOperationException("FilterListIterator.add(Object) is not supported.");
    }
    
    @Override
    public boolean hasNext() {
        return this.nextObjectSet || this.setNextObject();
    }
    
    @Override
    public boolean hasPrevious() {
        return this.previousObjectSet || this.setPreviousObject();
    }
    
    @Override
    public E next() {
        if (!this.nextObjectSet && !this.setNextObject()) {
            throw new NoSuchElementException();
        }
        ++this.nextIndex;
        final E temp = this.nextObject;
        this.clearNextObject();
        return temp;
    }
    
    @Override
    public int nextIndex() {
        return this.nextIndex;
    }
    
    @Override
    public E previous() {
        if (!this.previousObjectSet && !this.setPreviousObject()) {
            throw new NoSuchElementException();
        }
        --this.nextIndex;
        final E temp = this.previousObject;
        this.clearPreviousObject();
        return temp;
    }
    
    @Override
    public int previousIndex() {
        return this.nextIndex - 1;
    }
    
    @Override
    public void remove() {
        throw new UnsupportedOperationException("FilterListIterator.remove() is not supported.");
    }
    
    @Override
    public void set(final E o) {
        throw new UnsupportedOperationException("FilterListIterator.set(Object) is not supported.");
    }
    
    public ListIterator<? extends E> getListIterator() {
        return this.iterator;
    }
    
    public void setListIterator(final ListIterator<? extends E> iterator) {
        this.iterator = iterator;
    }
    
    public Predicate<? super E> getPredicate() {
        return this.predicate;
    }
    
    public void setPredicate(final Predicate<? super E> predicate) {
        this.predicate = predicate;
    }
    
    private void clearNextObject() {
        this.nextObject = null;
        this.nextObjectSet = false;
    }
    
    private boolean setNextObject() {
        if (this.previousObjectSet) {
            this.clearPreviousObject();
            if (!this.setNextObject()) {
                return false;
            }
            this.clearNextObject();
        }
        if (this.iterator == null) {
            return false;
        }
        while (this.iterator.hasNext()) {
            final E object = (E)this.iterator.next();
            if (this.predicate.evaluate(object)) {
                this.nextObject = object;
                return this.nextObjectSet = true;
            }
        }
        return false;
    }
    
    private void clearPreviousObject() {
        this.previousObject = null;
        this.previousObjectSet = false;
    }
    
    private boolean setPreviousObject() {
        if (this.nextObjectSet) {
            this.clearNextObject();
            if (!this.setPreviousObject()) {
                return false;
            }
            this.clearPreviousObject();
        }
        if (this.iterator == null) {
            return false;
        }
        while (this.iterator.hasPrevious()) {
            final E object = (E)this.iterator.previous();
            if (this.predicate.evaluate(object)) {
                this.previousObject = object;
                return this.previousObjectSet = true;
            }
        }
        return false;
    }
}
