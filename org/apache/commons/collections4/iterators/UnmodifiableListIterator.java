package org.apache.commons.collections4.iterators;

import org.apache.commons.collections4.Unmodifiable;
import java.util.ListIterator;

public final class UnmodifiableListIterator<E> implements ListIterator<E>, Unmodifiable
{
    private final ListIterator<? extends E> iterator;
    
    public static <E> ListIterator<E> umodifiableListIterator(final ListIterator<? extends E> iterator) {
        if (iterator == null) {
            throw new NullPointerException("ListIterator must not be null");
        }
        if (iterator instanceof Unmodifiable) {
            final ListIterator<E> tmpIterator = (ListIterator<E>)iterator;
            return tmpIterator;
        }
        return new UnmodifiableListIterator<E>(iterator);
    }
    
    private UnmodifiableListIterator(final ListIterator<? extends E> iterator) {
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
    public int nextIndex() {
        return this.iterator.nextIndex();
    }
    
    @Override
    public boolean hasPrevious() {
        return this.iterator.hasPrevious();
    }
    
    @Override
    public E previous() {
        return (E)this.iterator.previous();
    }
    
    @Override
    public int previousIndex() {
        return this.iterator.previousIndex();
    }
    
    @Override
    public void remove() {
        throw new UnsupportedOperationException("remove() is not supported");
    }
    
    @Override
    public void set(final E obj) {
        throw new UnsupportedOperationException("set() is not supported");
    }
    
    @Override
    public void add(final E obj) {
        throw new UnsupportedOperationException("add() is not supported");
    }
}
