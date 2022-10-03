package org.apache.commons.collections4.functors;

import org.apache.commons.collections4.Predicate;
import java.io.Serializable;

public abstract class AbstractQuantifierPredicate<T> implements PredicateDecorator<T>, Serializable
{
    private static final long serialVersionUID = -3094696765038308799L;
    protected final Predicate<? super T>[] iPredicates;
    
    public AbstractQuantifierPredicate(final Predicate<? super T>... predicates) {
        this.iPredicates = predicates;
    }
    
    @Override
    public Predicate<? super T>[] getPredicates() {
        return FunctorUtils.copy(this.iPredicates);
    }
}
