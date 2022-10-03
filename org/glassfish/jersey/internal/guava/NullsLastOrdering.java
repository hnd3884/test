package org.glassfish.jersey.internal.guava;

import java.io.Serializable;

final class NullsLastOrdering<T> extends Ordering<T> implements Serializable
{
    private static final long serialVersionUID = 0L;
    private final Ordering<? super T> ordering;
    
    NullsLastOrdering(final Ordering<? super T> ordering) {
        this.ordering = ordering;
    }
    
    @Override
    public int compare(final T left, final T right) {
        if (left == right) {
            return 0;
        }
        if (left == null) {
            return 1;
        }
        if (right == null) {
            return -1;
        }
        return this.ordering.compare((Object)left, (Object)right);
    }
    
    @Override
    public <S extends T> Ordering<S> reverse() {
        return this.ordering.reverse().nullsFirst();
    }
    
    public <S extends T> Ordering<S> nullsFirst() {
        return this.ordering.nullsFirst();
    }
    
    public <S extends T> Ordering<S> nullsLast() {
        return (Ordering<S>)this;
    }
    
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof NullsLastOrdering) {
            final NullsLastOrdering<?> that = (NullsLastOrdering<?>)object;
            return this.ordering.equals(that.ordering);
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return this.ordering.hashCode() ^ 0xC9177248;
    }
    
    @Override
    public String toString() {
        return this.ordering + ".nullsLast()";
    }
}
