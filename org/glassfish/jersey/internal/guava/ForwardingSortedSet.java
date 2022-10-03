package org.glassfish.jersey.internal.guava;

import java.util.Collection;
import java.util.Set;
import java.util.Comparator;
import java.util.SortedSet;

public abstract class ForwardingSortedSet<E> extends ForwardingSet<E> implements SortedSet<E>
{
    ForwardingSortedSet() {
    }
    
    @Override
    protected abstract SortedSet<E> delegate();
    
    @Override
    public Comparator<? super E> comparator() {
        return this.delegate().comparator();
    }
    
    @Override
    public E first() {
        return this.delegate().first();
    }
    
    @Override
    public SortedSet<E> headSet(final E toElement) {
        return this.delegate().headSet(toElement);
    }
    
    @Override
    public E last() {
        return this.delegate().last();
    }
    
    @Override
    public SortedSet<E> subSet(final E fromElement, final E toElement) {
        return this.delegate().subSet(fromElement, toElement);
    }
    
    @Override
    public SortedSet<E> tailSet(final E fromElement) {
        return this.delegate().tailSet(fromElement);
    }
}
