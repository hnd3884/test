package org.apache.commons.collections4.functors;

import java.util.Collection;
import org.apache.commons.collections4.Predicate;

public final class AllPredicate<T> extends AbstractQuantifierPredicate<T>
{
    private static final long serialVersionUID = -3094696765038308799L;
    
    public static <T> Predicate<T> allPredicate(final Predicate<? super T>... predicates) {
        FunctorUtils.validate((Predicate<?>[])predicates);
        if (predicates.length == 0) {
            return TruePredicate.truePredicate();
        }
        if (predicates.length == 1) {
            return FunctorUtils.coerce(predicates[0]);
        }
        return new AllPredicate<T>(FunctorUtils.copy(predicates));
    }
    
    public static <T> Predicate<T> allPredicate(final Collection<? extends Predicate<? super T>> predicates) {
        final Predicate<? super T>[] preds = FunctorUtils.validate((Collection<? extends Predicate<? super Object>>)predicates);
        if (preds.length == 0) {
            return TruePredicate.truePredicate();
        }
        if (preds.length == 1) {
            return FunctorUtils.coerce(preds[0]);
        }
        return new AllPredicate<T>(preds);
    }
    
    public AllPredicate(final Predicate<? super T>... predicates) {
        super((Predicate[])predicates);
    }
    
    @Override
    public boolean evaluate(final T object) {
        for (final Predicate<? super T> iPredicate : this.iPredicates) {
            if (!iPredicate.evaluate(object)) {
                return false;
            }
        }
        return true;
    }
}
