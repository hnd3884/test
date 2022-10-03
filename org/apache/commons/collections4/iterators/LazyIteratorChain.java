package org.apache.commons.collections4.iterators;

import java.util.Iterator;

public abstract class LazyIteratorChain<E> implements Iterator<E>
{
    private int callCounter;
    private boolean chainExhausted;
    private Iterator<? extends E> currentIterator;
    private Iterator<? extends E> lastUsedIterator;
    
    public LazyIteratorChain() {
        this.callCounter = 0;
        this.chainExhausted = false;
        this.currentIterator = null;
        this.lastUsedIterator = null;
    }
    
    protected abstract Iterator<? extends E> nextIterator(final int p0);
    
    private void updateCurrentIterator() {
        if (this.callCounter == 0) {
            this.currentIterator = this.nextIterator(++this.callCounter);
            if (this.currentIterator == null) {
                this.currentIterator = EmptyIterator.emptyIterator();
                this.chainExhausted = true;
            }
            this.lastUsedIterator = this.currentIterator;
        }
        while (!this.currentIterator.hasNext() && !this.chainExhausted) {
            final Iterator<? extends E> nextIterator = this.nextIterator(++this.callCounter);
            if (nextIterator != null) {
                this.currentIterator = nextIterator;
            }
            else {
                this.chainExhausted = true;
            }
        }
    }
    
    @Override
    public boolean hasNext() {
        this.updateCurrentIterator();
        this.lastUsedIterator = this.currentIterator;
        return this.currentIterator.hasNext();
    }
    
    @Override
    public E next() {
        this.updateCurrentIterator();
        this.lastUsedIterator = this.currentIterator;
        return (E)this.currentIterator.next();
    }
    
    @Override
    public void remove() {
        if (this.currentIterator == null) {
            this.updateCurrentIterator();
        }
        this.lastUsedIterator.remove();
    }
}
