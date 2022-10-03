package org.apache.commons.collections4.functors;

import org.apache.commons.collections4.Predicate;
import java.io.Serializable;

public final class AndPredicate<T> implements PredicateDecorator<T>, Serializable
{
    private static final long serialVersionUID = 4189014213763186912L;
    private final Predicate<? super T> iPredicate1;
    private final Predicate<? super T> iPredicate2;
    
    public static <T> Predicate<T> andPredicate(final Predicate<? super T> predicate1, final Predicate<? super T> predicate2) {
        if (predicate1 == null || predicate2 == null) {
            throw new NullPointerException("Predicate must not be null");
        }
        return new AndPredicate<T>(predicate1, predicate2);
    }
    
    public AndPredicate(final Predicate<? super T> predicate1, final Predicate<? super T> predicate2) {
        this.iPredicate1 = predicate1;
        this.iPredicate2 = predicate2;
    }
    
    @Override
    public boolean evaluate(final T object) {
        return this.iPredicate1.evaluate(object) && this.iPredicate2.evaluate(object);
    }
    
    @Override
    public Predicate<? super T>[] getPredicates() {
        return new Predicate[] { this.iPredicate1, this.iPredicate2 };
    }
}
