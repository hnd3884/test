package org.glassfish.jersey.internal.guava;

import java.util.Iterator;
import java.io.Serializable;

final class ReverseOrdering<T> extends Ordering<T> implements Serializable
{
    private static final long serialVersionUID = 0L;
    private final Ordering<? super T> forwardOrder;
    
    ReverseOrdering(final Ordering<? super T> forwardOrder) {
        this.forwardOrder = Preconditions.checkNotNull(forwardOrder);
    }
    
    @Override
    public int compare(final T a, final T b) {
        return this.forwardOrder.compare((Object)b, (Object)a);
    }
    
    @Override
    public <S extends T> Ordering<S> reverse() {
        return (Ordering<S>)this.forwardOrder;
    }
    
    public <E extends T> E min(final E a, final E b) {
        return this.forwardOrder.max(a, b);
    }
    
    public <E extends T> E min(final E a, final E b, final E c, final E... rest) {
        return this.forwardOrder.max(a, b, c, rest);
    }
    
    public <E extends T> E min(final Iterator<E> iterator) {
        return this.forwardOrder.max(iterator);
    }
    
    public <E extends T> E min(final Iterable<E> iterable) {
        return this.forwardOrder.max(iterable);
    }
    
    public <E extends T> E max(final E a, final E b) {
        return this.forwardOrder.min(a, b);
    }
    
    public <E extends T> E max(final E a, final E b, final E c, final E... rest) {
        return this.forwardOrder.min(a, b, c, rest);
    }
    
    public <E extends T> E max(final Iterator<E> iterator) {
        return this.forwardOrder.min(iterator);
    }
    
    public <E extends T> E max(final Iterable<E> iterable) {
        return this.forwardOrder.min(iterable);
    }
    
    @Override
    public int hashCode() {
        return -this.forwardOrder.hashCode();
    }
    
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof ReverseOrdering) {
            final ReverseOrdering<?> that = (ReverseOrdering<?>)object;
            return this.forwardOrder.equals(that.forwardOrder);
        }
        return false;
    }
    
    @Override
    public String toString() {
        return this.forwardOrder + ".reverse()";
    }
}
