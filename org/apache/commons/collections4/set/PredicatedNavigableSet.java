package org.apache.commons.collections4.set;

import java.util.Collection;
import java.util.Set;
import java.util.Iterator;
import java.util.SortedSet;
import org.apache.commons.collections4.Predicate;
import java.util.NavigableSet;

public class PredicatedNavigableSet<E> extends PredicatedSortedSet<E> implements NavigableSet<E>
{
    private static final long serialVersionUID = 20150528L;
    
    public static <E> PredicatedNavigableSet<E> predicatedNavigableSet(final NavigableSet<E> set, final Predicate<? super E> predicate) {
        return new PredicatedNavigableSet<E>(set, predicate);
    }
    
    protected PredicatedNavigableSet(final NavigableSet<E> set, final Predicate<? super E> predicate) {
        super(set, predicate);
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
        return predicatedNavigableSet(this.decorated().descendingSet(), this.predicate);
    }
    
    @Override
    public Iterator<E> descendingIterator() {
        return this.decorated().descendingIterator();
    }
    
    @Override
    public NavigableSet<E> subSet(final E fromElement, final boolean fromInclusive, final E toElement, final boolean toInclusive) {
        final NavigableSet<E> sub = this.decorated().subSet(fromElement, fromInclusive, toElement, toInclusive);
        return predicatedNavigableSet(sub, this.predicate);
    }
    
    @Override
    public NavigableSet<E> headSet(final E toElement, final boolean inclusive) {
        final NavigableSet<E> head = this.decorated().headSet(toElement, inclusive);
        return predicatedNavigableSet(head, this.predicate);
    }
    
    @Override
    public NavigableSet<E> tailSet(final E fromElement, final boolean inclusive) {
        final NavigableSet<E> tail = this.decorated().tailSet(fromElement, inclusive);
        return predicatedNavigableSet(tail, this.predicate);
    }
}
