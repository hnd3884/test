package org.apache.commons.collections4.functors;

import java.util.Iterator;
import java.util.Map;
import org.apache.commons.collections4.Predicate;
import java.io.Serializable;
import org.apache.commons.collections4.Transformer;

public class SwitchTransformer<I, O> implements Transformer<I, O>, Serializable
{
    private static final long serialVersionUID = -6404460890903469332L;
    private final Predicate<? super I>[] iPredicates;
    private final Transformer<? super I, ? extends O>[] iTransformers;
    private final Transformer<? super I, ? extends O> iDefault;
    
    public static <I, O> Transformer<I, O> switchTransformer(final Predicate<? super I>[] predicates, final Transformer<? super I, ? extends O>[] transformers, final Transformer<? super I, ? extends O> defaultTransformer) {
        FunctorUtils.validate((Predicate<?>[])predicates);
        FunctorUtils.validate((Transformer<?, ?>[])transformers);
        if (predicates.length != transformers.length) {
            throw new IllegalArgumentException("The predicate and transformer arrays must be the same size");
        }
        if (predicates.length == 0) {
            return (Transformer<I, O>)((defaultTransformer == null) ? ConstantTransformer.nullTransformer() : defaultTransformer);
        }
        return new SwitchTransformer<I, O>(predicates, transformers, defaultTransformer);
    }
    
    public static <I, O> Transformer<I, O> switchTransformer(final Map<? extends Predicate<? super I>, ? extends Transformer<? super I, ? extends O>> map) {
        if (map == null) {
            throw new NullPointerException("The predicate and transformer map must not be null");
        }
        if (map.size() == 0) {
            return ConstantTransformer.nullTransformer();
        }
        final Transformer<? super I, ? extends O> defaultTransformer = (Transformer<? super I, ? extends O>)map.remove(null);
        final int size = map.size();
        if (size == 0) {
            return (Transformer<I, O>)((defaultTransformer == null) ? ConstantTransformer.nullTransformer() : defaultTransformer);
        }
        final Transformer<? super I, ? extends O>[] transformers = new Transformer[size];
        final Predicate<? super I>[] preds = new Predicate[size];
        int i = 0;
        for (final Map.Entry<? extends Predicate<? super I>, ? extends Transformer<? super I, ? extends O>> entry : map.entrySet()) {
            preds[i] = (Predicate)entry.getKey();
            transformers[i] = (Transformer)entry.getValue();
            ++i;
        }
        return new SwitchTransformer<I, O>(false, preds, transformers, defaultTransformer);
    }
    
    private SwitchTransformer(final boolean clone, final Predicate<? super I>[] predicates, final Transformer<? super I, ? extends O>[] transformers, final Transformer<? super I, ? extends O> defaultTransformer) {
        this.iPredicates = (clone ? FunctorUtils.copy(predicates) : predicates);
        this.iTransformers = (clone ? FunctorUtils.copy(transformers) : transformers);
        this.iDefault = ((defaultTransformer == null) ? ConstantTransformer.nullTransformer() : defaultTransformer);
    }
    
    public SwitchTransformer(final Predicate<? super I>[] predicates, final Transformer<? super I, ? extends O>[] transformers, final Transformer<? super I, ? extends O> defaultTransformer) {
        this(true, predicates, transformers, defaultTransformer);
    }
    
    @Override
    public O transform(final I input) {
        for (int i = 0; i < this.iPredicates.length; ++i) {
            if (this.iPredicates[i].evaluate(input)) {
                return (O)this.iTransformers[i].transform(input);
            }
        }
        return (O)this.iDefault.transform(input);
    }
    
    public Predicate<? super I>[] getPredicates() {
        return FunctorUtils.copy(this.iPredicates);
    }
    
    public Transformer<? super I, ? extends O>[] getTransformers() {
        return FunctorUtils.copy(this.iTransformers);
    }
    
    public Transformer<? super I, ? extends O> getDefaultTransformer() {
        return this.iDefault;
    }
}
