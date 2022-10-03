package org.apache.commons.collections4.iterators;

import java.util.NoSuchElementException;
import java.util.ArrayDeque;
import org.apache.commons.collections4.Transformer;
import java.util.Deque;
import java.util.Iterator;

public class ObjectGraphIterator<E> implements Iterator<E>
{
    private final Deque<Iterator<? extends E>> stack;
    private E root;
    private final Transformer<? super E, ? extends E> transformer;
    private boolean hasNext;
    private Iterator<? extends E> currentIterator;
    private E currentValue;
    private Iterator<? extends E> lastUsedIterator;
    
    public ObjectGraphIterator(final E root, final Transformer<? super E, ? extends E> transformer) {
        this.stack = new ArrayDeque<Iterator<? extends E>>(8);
        this.hasNext = false;
        if (root instanceof Iterator) {
            this.currentIterator = (Iterator)root;
        }
        else {
            this.root = root;
        }
        this.transformer = transformer;
    }
    
    public ObjectGraphIterator(final Iterator<? extends E> rootIterator) {
        this.stack = new ArrayDeque<Iterator<? extends E>>(8);
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
    
    protected void findNext(final E value) {
        if (value instanceof Iterator) {
            this.findNextByIterator((Iterator<? extends E>)value);
        }
        else {
            this.currentValue = value;
            this.hasNext = true;
        }
    }
    
    protected void findNextByIterator(final Iterator<? extends E> iterator) {
        if (iterator != this.currentIterator) {
            if (this.currentIterator != null) {
                this.stack.push(this.currentIterator);
            }
            this.currentIterator = iterator;
        }
        while (this.currentIterator.hasNext() && !this.hasNext) {
            E next = (E)this.currentIterator.next();
            if (this.transformer != null) {
                next = (E)this.transformer.transform(next);
            }
            this.findNext(next);
        }
        if (!this.hasNext && !this.stack.isEmpty()) {
            this.findNextByIterator(this.currentIterator = this.stack.pop());
        }
    }
    
    @Override
    public boolean hasNext() {
        this.updateCurrentIterator();
        return this.hasNext;
    }
    
    @Override
    public E next() {
        this.updateCurrentIterator();
        if (!this.hasNext) {
            throw new NoSuchElementException("No more elements in the iteration");
        }
        this.lastUsedIterator = this.currentIterator;
        final E result = this.currentValue;
        this.currentValue = null;
        this.hasNext = false;
        return result;
    }
    
    @Override
    public void remove() {
        if (this.lastUsedIterator == null) {
            throw new IllegalStateException("Iterator remove() cannot be called at this time");
        }
        this.lastUsedIterator.remove();
        this.lastUsedIterator = null;
    }
}
