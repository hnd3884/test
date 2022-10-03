package org.apache.commons.collections.iterators;

import java.util.NoSuchElementException;
import org.apache.commons.collections.Predicate;
import java.util.ListIterator;

public class FilterListIterator implements ListIterator
{
    private ListIterator iterator;
    private Predicate predicate;
    private Object nextObject;
    private boolean nextObjectSet;
    private Object previousObject;
    private boolean previousObjectSet;
    private int nextIndex;
    
    public FilterListIterator() {
        this.nextObjectSet = false;
        this.previousObjectSet = false;
        this.nextIndex = 0;
    }
    
    public FilterListIterator(final ListIterator iterator) {
        this.nextObjectSet = false;
        this.previousObjectSet = false;
        this.nextIndex = 0;
        this.iterator = iterator;
    }
    
    public FilterListIterator(final ListIterator iterator, final Predicate predicate) {
        this.nextObjectSet = false;
        this.previousObjectSet = false;
        this.nextIndex = 0;
        this.iterator = iterator;
        this.predicate = predicate;
    }
    
    public FilterListIterator(final Predicate predicate) {
        this.nextObjectSet = false;
        this.previousObjectSet = false;
        this.nextIndex = 0;
        this.predicate = predicate;
    }
    
    public void add(final Object o) {
        throw new UnsupportedOperationException("FilterListIterator.add(Object) is not supported.");
    }
    
    public boolean hasNext() {
        return this.nextObjectSet || this.setNextObject();
    }
    
    public boolean hasPrevious() {
        return this.previousObjectSet || this.setPreviousObject();
    }
    
    public Object next() {
        if (!this.nextObjectSet && !this.setNextObject()) {
            throw new NoSuchElementException();
        }
        ++this.nextIndex;
        final Object temp = this.nextObject;
        this.clearNextObject();
        return temp;
    }
    
    public int nextIndex() {
        return this.nextIndex;
    }
    
    public Object previous() {
        if (!this.previousObjectSet && !this.setPreviousObject()) {
            throw new NoSuchElementException();
        }
        --this.nextIndex;
        final Object temp = this.previousObject;
        this.clearPreviousObject();
        return temp;
    }
    
    public int previousIndex() {
        return this.nextIndex - 1;
    }
    
    public void remove() {
        throw new UnsupportedOperationException("FilterListIterator.remove() is not supported.");
    }
    
    public void set(final Object o) {
        throw new UnsupportedOperationException("FilterListIterator.set(Object) is not supported.");
    }
    
    public ListIterator getListIterator() {
        return this.iterator;
    }
    
    public void setListIterator(final ListIterator iterator) {
        this.iterator = iterator;
    }
    
    public Predicate getPredicate() {
        return this.predicate;
    }
    
    public void setPredicate(final Predicate predicate) {
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
        while (this.iterator.hasNext()) {
            final Object object = this.iterator.next();
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
        while (this.iterator.hasPrevious()) {
            final Object object = this.iterator.previous();
            if (this.predicate.evaluate(object)) {
                this.previousObject = object;
                return this.previousObjectSet = true;
            }
        }
        return false;
    }
}
