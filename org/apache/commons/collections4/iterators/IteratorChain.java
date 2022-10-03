package org.apache.commons.collections4.iterators;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Iterator;

public class IteratorChain<E> implements Iterator<E>
{
    private final Queue<Iterator<? extends E>> iteratorChain;
    private Iterator<? extends E> currentIterator;
    private Iterator<? extends E> lastUsedIterator;
    private boolean isLocked;
    
    public IteratorChain() {
        this.iteratorChain = new LinkedList<Iterator<? extends E>>();
        this.currentIterator = null;
        this.lastUsedIterator = null;
        this.isLocked = false;
    }
    
    public IteratorChain(final Iterator<? extends E> iterator) {
        this.iteratorChain = new LinkedList<Iterator<? extends E>>();
        this.currentIterator = null;
        this.lastUsedIterator = null;
        this.isLocked = false;
        this.addIterator(iterator);
    }
    
    public IteratorChain(final Iterator<? extends E> first, final Iterator<? extends E> second) {
        this.iteratorChain = new LinkedList<Iterator<? extends E>>();
        this.currentIterator = null;
        this.lastUsedIterator = null;
        this.isLocked = false;
        this.addIterator(first);
        this.addIterator(second);
    }
    
    public IteratorChain(final Iterator<? extends E>... iteratorChain) {
        this.iteratorChain = new LinkedList<Iterator<? extends E>>();
        this.currentIterator = null;
        this.lastUsedIterator = null;
        this.isLocked = false;
        for (final Iterator<? extends E> element : iteratorChain) {
            this.addIterator(element);
        }
    }
    
    public IteratorChain(final Collection<Iterator<? extends E>> iteratorChain) {
        this.iteratorChain = new LinkedList<Iterator<? extends E>>();
        this.currentIterator = null;
        this.lastUsedIterator = null;
        this.isLocked = false;
        for (final Iterator<? extends E> iterator : iteratorChain) {
            this.addIterator(iterator);
        }
    }
    
    public void addIterator(final Iterator<? extends E> iterator) {
        this.checkLocked();
        if (iterator == null) {
            throw new NullPointerException("Iterator must not be null");
        }
        this.iteratorChain.add(iterator);
    }
    
    public int size() {
        return this.iteratorChain.size();
    }
    
    public boolean isLocked() {
        return this.isLocked;
    }
    
    private void checkLocked() {
        if (this.isLocked) {
            throw new UnsupportedOperationException("IteratorChain cannot be changed after the first use of a method from the Iterator interface");
        }
    }
    
    private void lockChain() {
        if (!this.isLocked) {
            this.isLocked = true;
        }
    }
    
    protected void updateCurrentIterator() {
        if (this.currentIterator == null) {
            if (this.iteratorChain.isEmpty()) {
                this.currentIterator = EmptyIterator.emptyIterator();
            }
            else {
                this.currentIterator = this.iteratorChain.remove();
            }
            this.lastUsedIterator = this.currentIterator;
        }
        while (!this.currentIterator.hasNext() && !this.iteratorChain.isEmpty()) {
            this.currentIterator = this.iteratorChain.remove();
        }
    }
    
    @Override
    public boolean hasNext() {
        this.lockChain();
        this.updateCurrentIterator();
        this.lastUsedIterator = this.currentIterator;
        return this.currentIterator.hasNext();
    }
    
    @Override
    public E next() {
        this.lockChain();
        this.updateCurrentIterator();
        this.lastUsedIterator = this.currentIterator;
        return (E)this.currentIterator.next();
    }
    
    @Override
    public void remove() {
        this.lockChain();
        if (this.currentIterator == null) {
            this.updateCurrentIterator();
        }
        this.lastUsedIterator.remove();
    }
}
