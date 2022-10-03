package org.apache.commons.collections4.set;

import java.util.Collection;
import java.util.Iterator;
import java.util.SortedSet;
import org.apache.commons.collections4.Transformer;
import java.util.NavigableSet;

public class TransformedNavigableSet<E> extends TransformedSortedSet<E> implements NavigableSet<E>
{
    private static final long serialVersionUID = 20150528L;
    
    public static <E> TransformedNavigableSet<E> transformingNavigableSet(final NavigableSet<E> set, final Transformer<? super E, ? extends E> transformer) {
        return new TransformedNavigableSet<E>(set, transformer);
    }
    
    public static <E> TransformedNavigableSet<E> transformedNavigableSet(final NavigableSet<E> set, final Transformer<? super E, ? extends E> transformer) {
        final TransformedNavigableSet<E> decorated = new TransformedNavigableSet<E>(set, transformer);
        if (set.size() > 0) {
            final E[] values = (E[])set.toArray();
            set.clear();
            for (final E value : values) {
                decorated.decorated().add((E)transformer.transform(value));
            }
        }
        return decorated;
    }
    
    protected TransformedNavigableSet(final NavigableSet<E> set, final Transformer<? super E, ? extends E> transformer) {
        super(set, transformer);
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
        return transformingNavigableSet(this.decorated().descendingSet(), this.transformer);
    }
    
    @Override
    public Iterator<E> descendingIterator() {
        return this.decorated().descendingIterator();
    }
    
    @Override
    public NavigableSet<E> subSet(final E fromElement, final boolean fromInclusive, final E toElement, final boolean toInclusive) {
        final NavigableSet<E> sub = this.decorated().subSet(fromElement, fromInclusive, toElement, toInclusive);
        return transformingNavigableSet(sub, this.transformer);
    }
    
    @Override
    public NavigableSet<E> headSet(final E toElement, final boolean inclusive) {
        final NavigableSet<E> head = this.decorated().headSet(toElement, inclusive);
        return transformingNavigableSet(head, this.transformer);
    }
    
    @Override
    public NavigableSet<E> tailSet(final E fromElement, final boolean inclusive) {
        final NavigableSet<E> tail = this.decorated().tailSet(fromElement, inclusive);
        return transformingNavigableSet(tail, this.transformer);
    }
}
