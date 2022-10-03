package org.apache.commons.collections4.set;

import java.util.Collection;
import java.util.Comparator;
import java.util.Set;
import org.apache.commons.collections4.Predicate;
import java.util.SortedSet;

public class PredicatedSortedSet<E> extends PredicatedSet<E> implements SortedSet<E>
{
    private static final long serialVersionUID = -9110948148132275052L;
    
    public static <E> PredicatedSortedSet<E> predicatedSortedSet(final SortedSet<E> set, final Predicate<? super E> predicate) {
        return new PredicatedSortedSet<E>(set, predicate);
    }
    
    protected PredicatedSortedSet(final SortedSet<E> set, final Predicate<? super E> predicate) {
        super(set, predicate);
    }
    
    @Override
    protected SortedSet<E> decorated() {
        return (SortedSet)super.decorated();
    }
    
    @Override
    public Comparator<? super E> comparator() {
        return this.decorated().comparator();
    }
    
    @Override
    public E first() {
        return this.decorated().first();
    }
    
    @Override
    public E last() {
        return this.decorated().last();
    }
    
    @Override
    public SortedSet<E> subSet(final E fromElement, final E toElement) {
        final SortedSet<E> sub = this.decorated().subSet(fromElement, toElement);
        return new PredicatedSortedSet((SortedSet<Object>)sub, (Predicate<? super Object>)this.predicate);
    }
    
    @Override
    public SortedSet<E> headSet(final E toElement) {
        final SortedSet<E> head = this.decorated().headSet(toElement);
        return new PredicatedSortedSet((SortedSet<Object>)head, (Predicate<? super Object>)this.predicate);
    }
    
    @Override
    public SortedSet<E> tailSet(final E fromElement) {
        final SortedSet<E> tail = this.decorated().tailSet(fromElement);
        return new PredicatedSortedSet((SortedSet<Object>)tail, (Predicate<? super Object>)this.predicate);
    }
}
