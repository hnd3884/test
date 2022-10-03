package org.apache.commons.collections4.bag;

import java.util.Collection;
import java.util.Comparator;
import org.apache.commons.collections4.Bag;
import org.apache.commons.collections4.Predicate;
import org.apache.commons.collections4.SortedBag;

public class PredicatedSortedBag<E> extends PredicatedBag<E> implements SortedBag<E>
{
    private static final long serialVersionUID = 3448581314086406616L;
    
    public static <E> PredicatedSortedBag<E> predicatedSortedBag(final SortedBag<E> bag, final Predicate<? super E> predicate) {
        return new PredicatedSortedBag<E>(bag, predicate);
    }
    
    protected PredicatedSortedBag(final SortedBag<E> bag, final Predicate<? super E> predicate) {
        super(bag, predicate);
    }
    
    @Override
    protected SortedBag<E> decorated() {
        return (SortedBag)super.decorated();
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
    public Comparator<? super E> comparator() {
        return this.decorated().comparator();
    }
}
