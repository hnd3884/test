package org.apache.commons.collections4.functors;

import org.apache.commons.collections4.Predicate;
import java.io.Serializable;

public final class NullIsTruePredicate<T> implements PredicateDecorator<T>, Serializable
{
    private static final long serialVersionUID = -7625133768987126273L;
    private final Predicate<? super T> iPredicate;
    
    public static <T> Predicate<T> nullIsTruePredicate(final Predicate<? super T> predicate) {
        if (predicate == null) {
            throw new NullPointerException("Predicate must not be null");
        }
        return new NullIsTruePredicate<T>(predicate);
    }
    
    public NullIsTruePredicate(final Predicate<? super T> predicate) {
        this.iPredicate = predicate;
    }
    
    @Override
    public boolean evaluate(final T object) {
        return object == null || this.iPredicate.evaluate(object);
    }
    
    @Override
    public Predicate<? super T>[] getPredicates() {
        return new Predicate[] { this.iPredicate };
    }
}
