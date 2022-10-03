package org.apache.commons.collections4.bag;

import java.util.Comparator;
import org.apache.commons.collections4.Bag;
import org.apache.commons.collections4.SortedBag;

public class SynchronizedSortedBag<E> extends SynchronizedBag<E> implements SortedBag<E>
{
    private static final long serialVersionUID = 722374056718497858L;
    
    public static <E> SynchronizedSortedBag<E> synchronizedSortedBag(final SortedBag<E> bag) {
        return new SynchronizedSortedBag<E>(bag);
    }
    
    protected SynchronizedSortedBag(final SortedBag<E> bag) {
        super(bag);
    }
    
    protected SynchronizedSortedBag(final Bag<E> bag, final Object lock) {
        super(bag, lock);
    }
    
    protected SortedBag<E> getSortedBag() {
        return (SortedBag)this.decorated();
    }
    
    @Override
    public synchronized E first() {
        synchronized (this.lock) {
            return this.getSortedBag().first();
        }
    }
    
    @Override
    public synchronized E last() {
        synchronized (this.lock) {
            return this.getSortedBag().last();
        }
    }
    
    @Override
    public synchronized Comparator<? super E> comparator() {
        synchronized (this.lock) {
            return this.getSortedBag().comparator();
        }
    }
}
