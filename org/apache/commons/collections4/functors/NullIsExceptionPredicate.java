package org.apache.commons.collections4.functors;

import org.apache.commons.collections4.FunctorException;
import org.apache.commons.collections4.Predicate;
import java.io.Serializable;

public final class NullIsExceptionPredicate<T> implements PredicateDecorator<T>, Serializable
{
    private static final long serialVersionUID = 3243449850504576071L;
    private final Predicate<? super T> iPredicate;
    
    public static <T> Predicate<T> nullIsExceptionPredicate(final Predicate<? super T> predicate) {
        if (predicate == null) {
            throw new NullPointerException("Predicate must not be null");
        }
        return new NullIsExceptionPredicate<T>(predicate);
    }
    
    public NullIsExceptionPredicate(final Predicate<? super T> predicate) {
        this.iPredicate = predicate;
    }
    
    @Override
    public boolean evaluate(final T object) {
        if (object == null) {
            throw new FunctorException("Input Object must not be null");
        }
        return this.iPredicate.evaluate(object);
    }
    
    @Override
    public Predicate<? super T>[] getPredicates() {
        return new Predicate[] { this.iPredicate };
    }
}
