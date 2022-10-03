package org.apache.commons.collections4.iterators;

import org.apache.commons.collections4.Unmodifiable;
import java.util.Iterator;

public final class UnmodifiableIterator<E> implements Iterator<E>, Unmodifiable
{
    private final Iterator<? extends E> iterator;
    
    public static <E> Iterator<E> unmodifiableIterator(final Iterator<? extends E> iterator) {
        if (iterator == null) {
            throw new NullPointerException("Iterator must not be null");
        }
        if (iterator instanceof Unmodifiable) {
            final Iterator<E> tmpIterator = (Iterator<E>)iterator;
            return tmpIterator;
        }
        return new UnmodifiableIterator<E>(iterator);
    }
    
    private UnmodifiableIterator(final Iterator<? extends E> iterator) {
        this.iterator = iterator;
    }
    
    @Override
    public boolean hasNext() {
        return this.iterator.hasNext();
    }
    
    @Override
    public E next() {
        return (E)this.iterator.next();
    }
    
    @Override
    public void remove() {
        throw new UnsupportedOperationException("remove() is not supported");
    }
}
