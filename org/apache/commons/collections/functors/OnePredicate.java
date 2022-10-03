package org.apache.commons.collections.functors;

import java.util.Collection;
import java.io.Serializable;
import org.apache.commons.collections.Predicate;

public final class OnePredicate implements Predicate, PredicateDecorator, Serializable
{
    private static final long serialVersionUID = -8125389089924745785L;
    private final Predicate[] iPredicates;
    
    public static Predicate getInstance(Predicate[] predicates) {
        FunctorUtils.validate(predicates);
        if (predicates.length == 0) {
            return FalsePredicate.INSTANCE;
        }
        if (predicates.length == 1) {
            return predicates[0];
        }
        predicates = FunctorUtils.copy(predicates);
        return new OnePredicate(predicates);
    }
    
    public static Predicate getInstance(final Collection predicates) {
        final Predicate[] preds = FunctorUtils.validate(predicates);
        return new OnePredicate(preds);
    }
    
    public OnePredicate(final Predicate[] predicates) {
        this.iPredicates = predicates;
    }
    
    public boolean evaluate(final Object object) {
        boolean match = false;
        for (int i = 0; i < this.iPredicates.length; ++i) {
            if (this.iPredicates[i].evaluate(object)) {
                if (match) {
                    return false;
                }
                match = true;
            }
        }
        return match;
    }
    
    public Predicate[] getPredicates() {
        return this.iPredicates;
    }
}
