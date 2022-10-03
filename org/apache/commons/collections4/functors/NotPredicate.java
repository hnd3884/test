package org.apache.commons.collections4.functors;

import org.apache.commons.collections4.Predicate;
import java.io.Serializable;

public final class NotPredicate<T> implements PredicateDecorator<T>, Serializable
{
    private static final long serialVersionUID = -2654603322338049674L;
    private final Predicate<? super T> iPredicate;
    
    public static <T> Predicate<T> notPredicate(final Predicate<? super T> predicate) {
        if (predicate == null) {
            throw new NullPointerException("Predicate must not be null");
        }
        return new NotPredicate<T>(predicate);
    }
    
    public NotPredicate(final Predicate<? super T> predicate) {
        this.iPredicate = predicate;
    }
    
    @Override
    public boolean evaluate(final T object) {
        return !this.iPredicate.evaluate(object);
    }
    
    @Override
    public Predicate<? super T>[] getPredicates() {
        return new Predicate[] { this.iPredicate };
    }
}
