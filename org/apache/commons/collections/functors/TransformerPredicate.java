package org.apache.commons.collections.functors;

import org.apache.commons.collections.FunctorException;
import org.apache.commons.collections.Transformer;
import java.io.Serializable;
import org.apache.commons.collections.Predicate;

public final class TransformerPredicate implements Predicate, Serializable
{
    private static final long serialVersionUID = -2407966402920578741L;
    private final Transformer iTransformer;
    
    public static Predicate getInstance(final Transformer transformer) {
        if (transformer == null) {
            throw new IllegalArgumentException("The transformer to call must not be null");
        }
        return new TransformerPredicate(transformer);
    }
    
    public TransformerPredicate(final Transformer transformer) {
        this.iTransformer = transformer;
    }
    
    public boolean evaluate(final Object object) {
        final Object result = this.iTransformer.transform(object);
        if (!(result instanceof Boolean)) {
            throw new FunctorException("Transformer must return an instanceof Boolean, it was a " + ((result == null) ? "null object" : result.getClass().getName()));
        }
        return (boolean)result;
    }
    
    public Transformer getTransformer() {
        return this.iTransformer;
    }
}
