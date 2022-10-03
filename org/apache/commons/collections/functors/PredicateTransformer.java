package org.apache.commons.collections.functors;

import org.apache.commons.collections.Predicate;
import java.io.Serializable;
import org.apache.commons.collections.Transformer;

public class PredicateTransformer implements Transformer, Serializable
{
    private static final long serialVersionUID = 5278818408044349346L;
    private final Predicate iPredicate;
    
    public static Transformer getInstance(final Predicate predicate) {
        if (predicate == null) {
            throw new IllegalArgumentException("Predicate must not be null");
        }
        return new PredicateTransformer(predicate);
    }
    
    public PredicateTransformer(final Predicate predicate) {
        this.iPredicate = predicate;
    }
    
    public Object transform(final Object input) {
        return this.iPredicate.evaluate(input) ? Boolean.TRUE : Boolean.FALSE;
    }
    
    public Predicate getPredicate() {
        return this.iPredicate;
    }
}
