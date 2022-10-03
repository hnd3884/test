package org.apache.commons.collections4.functors;

import java.util.Collection;
import org.apache.commons.collections4.Predicate;

public final class AnyPredicate<T> extends AbstractQuantifierPredicate<T>
{
    private static final long serialVersionUID = 7429999530934647542L;
    
    public static <T> Predicate<T> anyPredicate(final Predicate<? super T>... predicates) {
        FunctorUtils.validate((Predicate<?>[])predicates);
        if (predicates.length == 0) {
            return FalsePredicate.falsePredicate();
        }
        if (predicates.length == 1) {
            return (Predicate<T>)predicates[0];
        }
        return new AnyPredicate<T>(FunctorUtils.copy(predicates));
    }
    
    public static <T> Predicate<T> anyPredicate(final Collection<? extends Predicate<? super T>> predicates) {
        final Predicate<? super T>[] preds = FunctorUtils.validate((Collection<? extends Predicate<? super Object>>)predicates);
        if (preds.length == 0) {
            return FalsePredicate.falsePredicate();
        }
        if (preds.length == 1) {
            return (Predicate<T>)preds[0];
        }
        return new AnyPredicate<T>(preds);
    }
    
    public AnyPredicate(final Predicate<? super T>... predicates) {
        super((Predicate[])predicates);
    }
    
    @Override
    public boolean evaluate(final T object) {
        for (final Predicate<? super T> iPredicate : this.iPredicates) {
            if (iPredicate.evaluate(object)) {
                return true;
            }
        }
        return false;
    }
}
