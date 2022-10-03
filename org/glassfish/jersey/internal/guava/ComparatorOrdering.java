package org.glassfish.jersey.internal.guava;

import java.util.Comparator;
import java.io.Serializable;

final class ComparatorOrdering<T> extends Ordering<T> implements Serializable
{
    private static final long serialVersionUID = 0L;
    private final Comparator<T> comparator;
    
    ComparatorOrdering(final Comparator<T> comparator) {
        this.comparator = Preconditions.checkNotNull(comparator);
    }
    
    @Override
    public int compare(final T a, final T b) {
        return this.comparator.compare(a, b);
    }
    
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof ComparatorOrdering) {
            final ComparatorOrdering<?> that = (ComparatorOrdering<?>)object;
            return this.comparator.equals(that.comparator);
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return this.comparator.hashCode();
    }
    
    @Override
    public String toString() {
        return this.comparator.toString();
    }
}
