package org.apache.commons.collections4.set;

import java.util.Collection;
import java.util.SortedSet;
import java.util.Iterator;
import java.util.Set;
import java.util.NavigableSet;

public abstract class AbstractNavigableSetDecorator<E> extends AbstractSortedSetDecorator<E> implements NavigableSet<E>
{
    private static final long serialVersionUID = 20150528L;
    
    protected AbstractNavigableSetDecorator() {
    }
    
    protected AbstractNavigableSetDecorator(final NavigableSet<E> set) {
        super(set);
    }
    
    @Override
    protected NavigableSet<E> decorated() {
        return (NavigableSet)super.decorated();
    }
    
    @Override
    public E lower(final E e) {
        return this.decorated().lower(e);
    }
    
    @Override
    public E floor(final E e) {
        return this.decorated().floor(e);
    }
    
    @Override
    public E ceiling(final E e) {
        return this.decorated().ceiling(e);
    }
    
    @Override
    public E higher(final E e) {
        return this.decorated().higher(e);
    }
    
    @Override
    public E pollFirst() {
        return this.decorated().pollFirst();
    }
    
    @Override
    public E pollLast() {
        return this.decorated().pollLast();
    }
    
    @Override
    public NavigableSet<E> descendingSet() {
        return this.decorated().descendingSet();
    }
    
    @Override
    public Iterator<E> descendingIterator() {
        return this.decorated().descendingIterator();
    }
    
    @Override
    public NavigableSet<E> subSet(final E fromElement, final boolean fromInclusive, final E toElement, final boolean toInclusive) {
        return this.decorated().subSet(fromElement, fromInclusive, toElement, toInclusive);
    }
    
    @Override
    public NavigableSet<E> headSet(final E toElement, final boolean inclusive) {
        return this.decorated().headSet(toElement, inclusive);
    }
    
    @Override
    public NavigableSet<E> tailSet(final E fromElement, final boolean inclusive) {
        return this.decorated().tailSet(fromElement, inclusive);
    }
}
