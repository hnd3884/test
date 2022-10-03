package org.apache.commons.collections4.iterators;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;

public class PushbackIterator<E> implements Iterator<E>
{
    private final Iterator<? extends E> iterator;
    private Deque<E> items;
    
    public static <E> PushbackIterator<E> pushbackIterator(final Iterator<? extends E> iterator) {
        if (iterator == null) {
            throw new NullPointerException("Iterator must not be null");
        }
        if (iterator instanceof PushbackIterator) {
            final PushbackIterator<E> it = (PushbackIterator)iterator;
            return it;
        }
        return new PushbackIterator<E>(iterator);
    }
    
    public PushbackIterator(final Iterator<? extends E> iterator) {
        this.items = new ArrayDeque<E>();
        this.iterator = iterator;
    }
    
    public void pushback(final E item) {
        this.items.push(item);
    }
    
    @Override
    public boolean hasNext() {
        return !this.items.isEmpty() || this.iterator.hasNext();
    }
    
    @Override
    public E next() {
        return this.items.isEmpty() ? this.iterator.next() : this.items.pop();
    }
    
    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
