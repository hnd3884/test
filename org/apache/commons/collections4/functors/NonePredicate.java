package org.apache.commons.collections4.functors;

import java.util.Collection;
import org.apache.commons.collections4.Predicate;

public final class NonePredicate<T> extends AbstractQuantifierPredicate<T>
{
    private static final long serialVersionUID = 2007613066565892961L;
    
    public static <T> Predicate<T> nonePredicate(final Predicate<? super T>... predicates) {
        FunctorUtils.validate((Predicate<?>[])predicates);
        if (predicates.length == 0) {
            return TruePredicate.truePredicate();
        }
        return new NonePredicate<T>(FunctorUtils.copy(predicates));
    }
    
    public static <T> Predicate<T> nonePredicate(final Collection<? extends Predicate<? super T>> predicates) {
        final Predicate<? super T>[] preds = FunctorUtils.validate((Collection<? extends Predicate<? super Object>>)predicates);
        if (preds.length == 0) {
            return TruePredicate.truePredicate();
        }
        return new NonePredicate<T>(preds);
    }
    
    public NonePredicate(final Predicate<? super T>... predicates) {
        super((Predicate[])predicates);
    }
    
    @Override
    public boolean evaluate(final T object) {
        for (final Predicate<? super T> iPredicate : this.iPredicates) {
            if (iPredicate.evaluate(object)) {
                return false;
            }
        }
        return true;
    }
}
