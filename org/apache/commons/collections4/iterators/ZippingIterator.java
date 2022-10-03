package org.apache.commons.collections4.iterators;

import java.util.NoSuchElementException;
import java.util.List;
import org.apache.commons.collections4.FluentIterable;
import java.util.ArrayList;
import java.util.Iterator;

public class ZippingIterator<E> implements Iterator<E>
{
    private final Iterator<Iterator<? extends E>> iterators;
    private Iterator<? extends E> nextIterator;
    private Iterator<? extends E> lastReturned;
    
    public ZippingIterator(final Iterator<? extends E> a, final Iterator<? extends E> b) {
        this((Iterator[])new Iterator[] { a, b });
    }
    
    public ZippingIterator(final Iterator<? extends E> a, final Iterator<? extends E> b, final Iterator<? extends E> c) {
        this((Iterator[])new Iterator[] { a, b, c });
    }
    
    public ZippingIterator(final Iterator<? extends E>... iterators) {
        this.nextIterator = null;
        this.lastReturned = null;
        final List<Iterator<? extends E>> list = new ArrayList<Iterator<? extends E>>();
        for (final Iterator<? extends E> iterator : iterators) {
            if (iterator == null) {
                throw new NullPointerException("Iterator must not be null.");
            }
            list.add(iterator);
        }
        this.iterators = FluentIterable.of((Iterable<Iterator<? extends E>>)list).loop().iterator();
    }
    
    @Override
    public boolean hasNext() {
        if (this.nextIterator != null) {
            return true;
        }
        while (this.iterators.hasNext()) {
            final Iterator<? extends E> childIterator = this.iterators.next();
            if (childIterator.hasNext()) {
                this.nextIterator = childIterator;
                return true;
            }
            this.iterators.remove();
        }
        return false;
    }
    
    @Override
    public E next() throws NoSuchElementException {
        if (!this.hasNext()) {
            throw new NoSuchElementException();
        }
        final E val = (E)this.nextIterator.next();
        this.lastReturned = this.nextIterator;
        this.nextIterator = null;
        return val;
    }
    
    @Override
    public void remove() {
        if (this.lastReturned == null) {
            throw new IllegalStateException("No value can be removed at present");
        }
        this.lastReturned.remove();
        this.lastReturned = null;
    }
}
