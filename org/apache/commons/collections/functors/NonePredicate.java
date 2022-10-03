package org.apache.commons.collections.functors;

import java.util.Collection;
import java.io.Serializable;
import org.apache.commons.collections.Predicate;

public final class NonePredicate implements Predicate, PredicateDecorator, Serializable
{
    private static final long serialVersionUID = 2007613066565892961L;
    private final Predicate[] iPredicates;
    
    public static Predicate getInstance(Predicate[] predicates) {
        FunctorUtils.validate(predicates);
        if (predicates.length == 0) {
            return TruePredicate.INSTANCE;
        }
        predicates = FunctorUtils.copy(predicates);
        return new NonePredicate(predicates);
    }
    
    public static Predicate getInstance(final Collection predicates) {
        final Predicate[] preds = FunctorUtils.validate(predicates);
        if (preds.length == 0) {
            return TruePredicate.INSTANCE;
        }
        return new NonePredicate(preds);
    }
    
    public NonePredicate(final Predicate[] predicates) {
        this.iPredicates = predicates;
    }
    
    public boolean evaluate(final Object object) {
        for (int i = 0; i < this.iPredicates.length; ++i) {
            if (this.iPredicates[i].evaluate(object)) {
                return false;
            }
        }
        return true;
    }
    
    public Predicate[] getPredicates() {
        return this.iPredicates;
    }
}
