package org.apache.commons.collections.functors;

import org.apache.commons.collections.FunctorException;
import java.io.Serializable;
import org.apache.commons.collections.Predicate;

public final class NullIsExceptionPredicate implements Predicate, PredicateDecorator, Serializable
{
    private static final long serialVersionUID = 3243449850504576071L;
    private final Predicate iPredicate;
    
    public static Predicate getInstance(final Predicate predicate) {
        if (predicate == null) {
            throw new IllegalArgumentException("Predicate must not be null");
        }
        return new NullIsExceptionPredicate(predicate);
    }
    
    public NullIsExceptionPredicate(final Predicate predicate) {
        this.iPredicate = predicate;
    }
    
    public boolean evaluate(final Object object) {
        if (object == null) {
            throw new FunctorException("Input Object must not be null");
        }
        return this.iPredicate.evaluate(object);
    }
    
    public Predicate[] getPredicates() {
        return new Predicate[] { this.iPredicate };
    }
}
