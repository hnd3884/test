package org.apache.commons.collections4.functors;

import org.apache.commons.collections4.FunctorException;
import org.apache.commons.collections4.Transformer;
import java.io.Serializable;
import org.apache.commons.collections4.Predicate;

public final class TransformerPredicate<T> implements Predicate<T>, Serializable
{
    private static final long serialVersionUID = -2407966402920578741L;
    private final Transformer<? super T, Boolean> iTransformer;
    
    public static <T> Predicate<T> transformerPredicate(final Transformer<? super T, Boolean> transformer) {
        if (transformer == null) {
            throw new NullPointerException("The transformer to call must not be null");
        }
        return new TransformerPredicate<T>(transformer);
    }
    
    public TransformerPredicate(final Transformer<? super T, Boolean> transformer) {
        this.iTransformer = transformer;
    }
    
    @Override
    public boolean evaluate(final T object) {
        final Boolean result = this.iTransformer.transform(object);
        if (result == null) {
            throw new FunctorException("Transformer must return an instanceof Boolean, it was a null object");
        }
        return result;
    }
    
    public Transformer<? super T, Boolean> getTransformer() {
        return this.iTransformer;
    }
}
