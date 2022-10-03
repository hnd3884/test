package org.apache.commons.collections4.functors;

import java.util.Collection;
import org.apache.commons.collections4.Predicate;

public final class OnePredicate<T> extends AbstractQuantifierPredicate<T>
{
    private static final long serialVersionUID = -8125389089924745785L;
    
    public static <T> Predicate<T> onePredicate(final Predicate<? super T>... predicates) {
        FunctorUtils.validate((Predicate<?>[])predicates);
        if (predicates.length == 0) {
            return FalsePredicate.falsePredicate();
        }
        if (predicates.length == 1) {
            return (Predicate<T>)predicates[0];
        }
        return new OnePredicate<T>(FunctorUtils.copy(predicates));
    }
    
    public static <T> Predicate<T> onePredicate(final Collection<? extends Predicate<? super T>> predicates) {
        final Predicate<? super T>[] preds = FunctorUtils.validate((Collection<? extends Predicate<? super Object>>)predicates);
        return new OnePredicate<T>(preds);
    }
    
    public OnePredicate(final Predicate<? super T>... predicates) {
        super((Predicate[])predicates);
    }
    
    @Override
    public boolean evaluate(final T object) {
        boolean match = false;
        for (final Predicate<? super T> iPredicate : this.iPredicates) {
            if (iPredicate.evaluate(object)) {
                if (match) {
                    return false;
                }
                match = true;
            }
        }
        return match;
    }
}
