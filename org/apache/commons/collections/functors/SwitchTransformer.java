package org.apache.commons.collections.functors;

import java.util.Iterator;
import java.util.Map;
import org.apache.commons.collections.Predicate;
import java.io.Serializable;
import org.apache.commons.collections.Transformer;

public class SwitchTransformer implements Transformer, Serializable
{
    private static final long serialVersionUID = -6404460890903469332L;
    private final Predicate[] iPredicates;
    private final Transformer[] iTransformers;
    private final Transformer iDefault;
    
    public static Transformer getInstance(Predicate[] predicates, Transformer[] transformers, final Transformer defaultTransformer) {
        FunctorUtils.validate(predicates);
        FunctorUtils.validate(transformers);
        if (predicates.length != transformers.length) {
            throw new IllegalArgumentException("The predicate and transformer arrays must be the same size");
        }
        if (predicates.length == 0) {
            return (defaultTransformer == null) ? ConstantTransformer.NULL_INSTANCE : defaultTransformer;
        }
        predicates = FunctorUtils.copy(predicates);
        transformers = FunctorUtils.copy(transformers);
        return new SwitchTransformer(predicates, transformers, defaultTransformer);
    }
    
    public static Transformer getInstance(final Map predicatesAndTransformers) {
        Transformer[] transformers = null;
        Predicate[] preds = null;
        if (predicatesAndTransformers == null) {
            throw new IllegalArgumentException("The predicate and transformer map must not be null");
        }
        if (predicatesAndTransformers.size() == 0) {
            return ConstantTransformer.NULL_INSTANCE;
        }
        final Transformer defaultTransformer = predicatesAndTransformers.remove(null);
        final int size = predicatesAndTransformers.size();
        if (size == 0) {
            return (defaultTransformer == null) ? ConstantTransformer.NULL_INSTANCE : defaultTransformer;
        }
        transformers = new Transformer[size];
        preds = new Predicate[size];
        int i = 0;
        final Iterator it = predicatesAndTransformers.entrySet().iterator();
        while (it.hasNext()) {
            final Map.Entry entry = it.next();
            preds[i] = entry.getKey();
            transformers[i] = entry.getValue();
            ++i;
        }
        return new SwitchTransformer(preds, transformers, defaultTransformer);
    }
    
    public SwitchTransformer(final Predicate[] predicates, final Transformer[] transformers, final Transformer defaultTransformer) {
        this.iPredicates = predicates;
        this.iTransformers = transformers;
        this.iDefault = ((defaultTransformer == null) ? ConstantTransformer.NULL_INSTANCE : defaultTransformer);
    }
    
    public Object transform(final Object input) {
        for (int i = 0; i < this.iPredicates.length; ++i) {
            if (this.iPredicates[i].evaluate(input)) {
                return this.iTransformers[i].transform(input);
            }
        }
        return this.iDefault.transform(input);
    }
    
    public Predicate[] getPredicates() {
        return this.iPredicates;
    }
    
    public Transformer[] getTransformers() {
        return this.iTransformers;
    }
    
    public Transformer getDefaultTransformer() {
        return this.iDefault;
    }
}
