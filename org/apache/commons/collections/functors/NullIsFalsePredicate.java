package org.apache.commons.collections.functors;

import java.io.Serializable;
import org.apache.commons.collections.Predicate;

public final class NullIsFalsePredicate implements Predicate, PredicateDecorator, Serializable
{
    private static final long serialVersionUID = -2997501534564735525L;
    private final Predicate iPredicate;
    
    public static Predicate getInstance(final Predicate predicate) {
        if (predicate == null) {
            throw new IllegalArgumentException("Predicate must not be null");
        }
        return new NullIsFalsePredicate(predicate);
    }
    
    public NullIsFalsePredicate(final Predicate predicate) {
        this.iPredicate = predicate;
    }
    
    public boolean evaluate(final Object object) {
        return object != null && this.iPredicate.evaluate(object);
    }
    
    public Predicate[] getPredicates() {
        return new Predicate[] { this.iPredicate };
    }
}
