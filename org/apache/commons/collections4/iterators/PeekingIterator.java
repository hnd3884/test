package org.apache.commons.collections4.iterators;

import java.util.NoSuchElementException;
import java.util.Iterator;

public class PeekingIterator<E> implements Iterator<E>
{
    private final Iterator<? extends E> iterator;
    private boolean exhausted;
    private boolean slotFilled;
    private E slot;
    
    public static <E> PeekingIterator<E> peekingIterator(final Iterator<? extends E> iterator) {
        if (iterator == null) {
            throw new NullPointerException("Iterator must not be null");
        }
        if (iterator instanceof PeekingIterator) {
            final PeekingIterator<E> it = (PeekingIterator)iterator;
            return it;
        }
        return new PeekingIterator<E>(iterator);
    }
    
    public PeekingIterator(final Iterator<? extends E> iterator) {
        this.exhausted = false;
        this.slotFilled = false;
        this.iterator = iterator;
    }
    
    private void fill() {
        if (this.exhausted || this.slotFilled) {
            return;
        }
        if (this.iterator.hasNext()) {
            this.slot = (E)this.iterator.next();
            this.slotFilled = true;
        }
        else {
            this.exhausted = true;
            this.slot = null;
            this.slotFilled = false;
        }
    }
    
    @Override
    public boolean hasNext() {
        return !this.exhausted && (this.slotFilled || this.iterator.hasNext());
    }
    
    public E peek() {
        this.fill();
        return this.exhausted ? null : this.slot;
    }
    
    public E element() {
        this.fill();
        if (this.exhausted) {
            throw new NoSuchElementException();
        }
        return this.slot;
    }
    
    @Override
    public E next() {
        if (!this.hasNext()) {
            throw new NoSuchElementException();
        }
        final E x = this.slotFilled ? this.slot : this.iterator.next();
        this.slot = null;
        this.slotFilled = false;
        return x;
    }
    
    @Override
    public void remove() {
        if (this.slotFilled) {
            throw new IllegalStateException("peek() or element() called before remove()");
        }
        this.iterator.remove();
    }
}
