package org.apache.commons.collections4.functors;

import org.apache.commons.collections4.Predicate;
import java.io.Serializable;
import org.apache.commons.collections4.Transformer;

public class IfTransformer<I, O> implements Transformer<I, O>, Serializable
{
    private static final long serialVersionUID = 8069309411242014252L;
    private final Predicate<? super I> iPredicate;
    private final Transformer<? super I, ? extends O> iTrueTransformer;
    private final Transformer<? super I, ? extends O> iFalseTransformer;
    
    public static <I, O> Transformer<I, O> ifTransformer(final Predicate<? super I> predicate, final Transformer<? super I, ? extends O> trueTransformer, final Transformer<? super I, ? extends O> falseTransformer) {
        if (predicate == null) {
            throw new NullPointerException("Predicate must not be null");
        }
        if (trueTransformer == null || falseTransformer == null) {
            throw new NullPointerException("Transformers must not be null");
        }
        return new IfTransformer<I, O>(predicate, trueTransformer, falseTransformer);
    }
    
    public static <T> Transformer<T, T> ifTransformer(final Predicate<? super T> predicate, final Transformer<? super T, ? extends T> trueTransformer) {
        if (predicate == null) {
            throw new NullPointerException("Predicate must not be null");
        }
        if (trueTransformer == null) {
            throw new NullPointerException("Transformer must not be null");
        }
        return new IfTransformer<T, T>(predicate, trueTransformer, (Transformer<? super T, ? extends T>)NOPTransformer.nopTransformer());
    }
    
    public IfTransformer(final Predicate<? super I> predicate, final Transformer<? super I, ? extends O> trueTransformer, final Transformer<? super I, ? extends O> falseTransformer) {
        this.iPredicate = predicate;
        this.iTrueTransformer = trueTransformer;
        this.iFalseTransformer = falseTransformer;
    }
    
    @Override
    public O transform(final I input) {
        if (this.iPredicate.evaluate(input)) {
            return (O)this.iTrueTransformer.transform(input);
        }
        return (O)this.iFalseTransformer.transform(input);
    }
    
    public Predicate<? super I> getPredicate() {
        return this.iPredicate;
    }
    
    public Transformer<? super I, ? extends O> getTrueTransformer() {
        return this.iTrueTransformer;
    }
    
    public Transformer<? super I, ? extends O> getFalseTransformer() {
        return this.iFalseTransformer;
    }
}
