package org.glassfish.jersey.internal.guava;

import java.util.Iterator;
import java.io.Serializable;

final class ReverseNaturalOrdering extends Ordering<Comparable> implements Serializable
{
    static final ReverseNaturalOrdering INSTANCE;
    private static final long serialVersionUID = 0L;
    
    private ReverseNaturalOrdering() {
    }
    
    @Override
    public int compare(final Comparable left, final Comparable right) {
        Preconditions.checkNotNull(left);
        if (left == right) {
            return 0;
        }
        return right.compareTo(left);
    }
    
    @Override
    public <S extends Comparable> Ordering<S> reverse() {
        return Ordering.natural();
    }
    
    public <E extends Comparable> E min(final E a, final E b) {
        return NaturalOrdering.INSTANCE.max(a, b);
    }
    
    public <E extends Comparable> E min(final E a, final E b, final E c, final E... rest) {
        return NaturalOrdering.INSTANCE.max(a, b, c, rest);
    }
    
    public <E extends Comparable> E min(final Iterator<E> iterator) {
        return NaturalOrdering.INSTANCE.max(iterator);
    }
    
    public <E extends Comparable> E min(final Iterable<E> iterable) {
        return NaturalOrdering.INSTANCE.max(iterable);
    }
    
    public <E extends Comparable> E max(final E a, final E b) {
        return NaturalOrdering.INSTANCE.min(a, b);
    }
    
    public <E extends Comparable> E max(final E a, final E b, final E c, final E... rest) {
        return NaturalOrdering.INSTANCE.min(a, b, c, rest);
    }
    
    public <E extends Comparable> E max(final Iterator<E> iterator) {
        return NaturalOrdering.INSTANCE.min(iterator);
    }
    
    public <E extends Comparable> E max(final Iterable<E> iterable) {
        return NaturalOrdering.INSTANCE.min(iterable);
    }
    
    private Object readResolve() {
        return ReverseNaturalOrdering.INSTANCE;
    }
    
    @Override
    public String toString() {
        return "Ordering.natural().reverse()";
    }
    
    static {
        INSTANCE = new ReverseNaturalOrdering();
    }
}
