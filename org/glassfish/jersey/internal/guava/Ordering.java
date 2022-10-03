package org.glassfish.jersey.internal.guava;

import java.util.Iterator;
import java.util.Comparator;

public abstract class Ordering<T> implements Comparator<T>
{
    static final int LEFT_IS_GREATER = 1;
    static final int RIGHT_IS_GREATER = -1;
    
    Ordering() {
    }
    
    public static <C extends Comparable> Ordering<C> natural() {
        return (Ordering<C>)NaturalOrdering.INSTANCE;
    }
    
    public static <T> Ordering<T> from(final Comparator<T> comparator) {
        return (comparator instanceof Ordering) ? ((Ordering)comparator) : new ComparatorOrdering<T>(comparator);
    }
    
    public <S extends T> Ordering<S> reverse() {
        return new ReverseOrdering<S>(this);
    }
    
     <S extends T> Ordering<S> nullsFirst() {
        return new NullsFirstOrdering<S>(this);
    }
    
     <S extends T> Ordering<S> nullsLast() {
        return new NullsLastOrdering<S>(this);
    }
    
    @Override
    public abstract int compare(final T p0, final T p1);
    
     <E extends T> E min(final Iterator<E> iterator) {
        E minSoFar = iterator.next();
        while (iterator.hasNext()) {
            minSoFar = this.min(minSoFar, iterator.next());
        }
        return minSoFar;
    }
    
     <E extends T> E min(final Iterable<E> iterable) {
        return this.min(iterable.iterator());
    }
    
     <E extends T> E min(final E a, final E b) {
        return (this.compare(a, b) <= 0) ? a : b;
    }
    
     <E extends T> E min(final E a, final E b, final E c, final E... rest) {
        E minSoFar = this.min(this.min(a, b), c);
        for (final E r : rest) {
            minSoFar = this.min(minSoFar, r);
        }
        return minSoFar;
    }
    
     <E extends T> E max(final Iterator<E> iterator) {
        E maxSoFar = iterator.next();
        while (iterator.hasNext()) {
            maxSoFar = this.max(maxSoFar, iterator.next());
        }
        return maxSoFar;
    }
    
     <E extends T> E max(final Iterable<E> iterable) {
        return this.max(iterable.iterator());
    }
    
     <E extends T> E max(final E a, final E b) {
        return (this.compare(a, b) >= 0) ? a : b;
    }
    
     <E extends T> E max(final E a, final E b, final E c, final E... rest) {
        E maxSoFar = this.max(this.max(a, b), c);
        for (final E r : rest) {
            maxSoFar = this.max(maxSoFar, r);
        }
        return maxSoFar;
    }
}
