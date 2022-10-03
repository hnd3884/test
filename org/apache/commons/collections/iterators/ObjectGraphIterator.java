package org.apache.commons.collections.iterators;

import java.util.NoSuchElementException;
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.ArrayStack;
import java.util.Iterator;

public class ObjectGraphIterator implements Iterator
{
    protected final ArrayStack stack;
    protected Object root;
    protected Transformer transformer;
    protected boolean hasNext;
    protected Iterator currentIterator;
    protected Object currentValue;
    protected Iterator lastUsedIterator;
    
    public ObjectGraphIterator(final Object root, final Transformer transformer) {
        this.stack = new ArrayStack(8);
        this.hasNext = false;
        if (root instanceof Iterator) {
            this.currentIterator = (Iterator)root;
        }
        else {
            this.root = root;
        }
        this.transformer = transformer;
    }
    
    public ObjectGraphIterator(final Iterator rootIterator) {
        this.stack = new ArrayStack(8);
        this.hasNext = false;
        this.currentIterator = rootIterator;
        this.transformer = null;
    }
    
    protected void updateCurrentIterator() {
        if (this.hasNext) {
            return;
        }
        if (this.currentIterator == null) {
            if (this.root != null) {
                if (this.transformer == null) {
                    this.findNext(this.root);
                }
                else {
                    this.findNext(this.transformer.transform(this.root));
                }
                this.root = null;
            }
        }
        else {
            this.findNextByIterator(this.currentIterator);
        }
    }
    
    protected void findNext(final Object value) {
        if (value instanceof Iterator) {
            this.findNextByIterator((Iterator)value);
        }
        else {
            this.currentValue = value;
            this.hasNext = true;
        }
    }
    
    protected void findNextByIterator(final Iterator iterator) {
        if (iterator != this.currentIterator) {
            if (this.currentIterator != null) {
                this.stack.push(this.currentIterator);
            }
            this.currentIterator = iterator;
        }
        while (this.currentIterator.hasNext() && !this.hasNext) {
            Object next = this.currentIterator.next();
            if (this.transformer != null) {
                next = this.transformer.transform(next);
            }
            this.findNext(next);
        }
        if (!this.hasNext) {
            if (!this.stack.isEmpty()) {
                this.findNextByIterator(this.currentIterator = (Iterator)this.stack.pop());
            }
        }
    }
    
    public boolean hasNext() {
        this.updateCurrentIterator();
        return this.hasNext;
    }
    
    public Object next() {
        this.updateCurrentIterator();
        if (!this.hasNext) {
            throw new NoSuchElementException("No more elements in the iteration");
        }
        this.lastUsedIterator = this.currentIterator;
        final Object result = this.currentValue;
        this.currentValue = null;
        this.hasNext = false;
        return result;
    }
    
    public void remove() {
        if (this.lastUsedIterator == null) {
            throw new IllegalStateException("Iterator remove() cannot be called at this time");
        }
        this.lastUsedIterator.remove();
        this.lastUsedIterator = null;
    }
}
