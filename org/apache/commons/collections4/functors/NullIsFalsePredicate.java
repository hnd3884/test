package org.apache.commons.collections4.functors;

import org.apache.commons.collections4.Predicate;
import java.io.Serializable;

public final class NullIsFalsePredicate<T> implements PredicateDecorator<T>, Serializable
{
    private static final long serialVersionUID = -2997501534564735525L;
    private final Predicate<? super T> iPredicate;
    
    public static <T> Predicate<T> nullIsFalsePredicate(final Predicate<? super T> predicate) {
        if (predicate == null) {
            throw new NullPointerException("Predicate must not be null");
        }
        return new NullIsFalsePredicate<T>(predicate);
    }
    
    public NullIsFalsePredicate(final Predicate<? super T> predicate) {
        this.iPredicate = predicate;
    }
    
    @Override
    public boolean evaluate(final T object) {
        return object != null && this.iPredicate.evaluate(object);
    }
    
    @Override
    public Predicate<? super T>[] getPredicates() {
        return new Predicate[] { this.iPredicate };
    }
}
