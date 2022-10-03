package org.apache.commons.collections.functors;

import java.io.Serializable;
import org.apache.commons.collections.Predicate;

public final class OrPredicate implements Predicate, PredicateDecorator, Serializable
{
    private static final long serialVersionUID = -8791518325735182855L;
    private final Predicate iPredicate1;
    private final Predicate iPredicate2;
    
    public static Predicate getInstance(final Predicate predicate1, final Predicate predicate2) {
        if (predicate1 == null || predicate2 == null) {
            throw new IllegalArgumentException("Predicate must not be null");
        }
        return new OrPredicate(predicate1, predicate2);
    }
    
    public OrPredicate(final Predicate predicate1, final Predicate predicate2) {
        this.iPredicate1 = predicate1;
        this.iPredicate2 = predicate2;
    }
    
    public boolean evaluate(final Object object) {
        return this.iPredicate1.evaluate(object) || this.iPredicate2.evaluate(object);
    }
    
    public Predicate[] getPredicates() {
        return new Predicate[] { this.iPredicate1, this.iPredicate2 };
    }
}
