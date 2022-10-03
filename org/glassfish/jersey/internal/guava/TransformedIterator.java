package org.glassfish.jersey.internal.guava;

import java.util.Iterator;

abstract class TransformedIterator<F, T> implements Iterator<T>
{
    private final Iterator<? extends F> backingIterator;
    
    TransformedIterator(final Iterator<? extends F> backingIterator) {
        this.backingIterator = Preconditions.checkNotNull(backingIterator);
    }
    
    abstract T transform(final F p0);
    
    @Override
    public final boolean hasNext() {
        return this.backingIterator.hasNext();
    }
    
    @Override
    public final T next() {
        return this.transform(this.backingIterator.next());
    }
    
    @Override
    public final void remove() {
        this.backingIterator.remove();
    }
}
