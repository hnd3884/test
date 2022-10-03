package org.apache.commons.collections4.functors;

import org.apache.commons.collections4.Predicate;
import org.apache.commons.collections4.Transformer;
import java.io.Serializable;

public final class TransformedPredicate<T> implements PredicateDecorator<T>, Serializable
{
    private static final long serialVersionUID = -5596090919668315834L;
    private final Transformer<? super T, ? extends T> iTransformer;
    private final Predicate<? super T> iPredicate;
    
    public static <T> Predicate<T> transformedPredicate(final Transformer<? super T, ? extends T> transformer, final Predicate<? super T> predicate) {
        if (transformer == null) {
            throw new NullPointerException("The transformer to call must not be null");
        }
        if (predicate == null) {
            throw new NullPointerException("The predicate to call must not be null");
        }
        return new TransformedPredicate<T>(transformer, predicate);
    }
    
    public TransformedPredicate(final Transformer<? super T, ? extends T> transformer, final Predicate<? super T> predicate) {
        this.iTransformer = transformer;
        this.iPredicate = predicate;
    }
    
    @Override
    public boolean evaluate(final T object) {
        final T result = (T)this.iTransformer.transform(object);
        return this.iPredicate.evaluate(result);
    }
    
    @Override
    public Predicate<? super T>[] getPredicates() {
        return new Predicate[] { this.iPredicate };
    }
    
    public Transformer<? super T, ? extends T> getTransformer() {
        return this.iTransformer;
    }
}
