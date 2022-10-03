package org.apache.commons.collections.functors;

import java.util.Collection;
import java.io.Serializable;
import org.apache.commons.collections.Predicate;

public final class AnyPredicate implements Predicate, PredicateDecorator, Serializable
{
    private static final long serialVersionUID = 7429999530934647542L;
    private final Predicate[] iPredicates;
    
    public static Predicate getInstance(final Predicate[] predicates) {
        FunctorUtils.validate(predicates);
        if (predicates.length == 0) {
            return FalsePredicate.INSTANCE;
        }
        if (predicates.length == 1) {
            return predicates[0];
        }
        return new AnyPredicate(FunctorUtils.copy(predicates));
    }
    
    public static Predicate getInstance(final Collection predicates) {
        final Predicate[] preds = FunctorUtils.validate(predicates);
        if (preds.length == 0) {
            return FalsePredicate.INSTANCE;
        }
        if (preds.length == 1) {
            return preds[0];
        }
        return new AnyPredicate(preds);
    }
    
    public AnyPredicate(final Predicate[] predicates) {
        this.iPredicates = predicates;
    }
    
    public boolean evaluate(final Object object) {
        for (int i = 0; i < this.iPredicates.length; ++i) {
            if (this.iPredicates[i].evaluate(object)) {
                return true;
            }
        }
        return false;
    }
    
    public Predicate[] getPredicates() {
        return this.iPredicates;
    }
}
