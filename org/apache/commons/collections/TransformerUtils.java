package org.apache.commons.collections;

import org.apache.commons.collections.functors.StringValueTransformer;
import org.apache.commons.collections.functors.InvokerTransformer;
import org.apache.commons.collections.functors.MapTransformer;
import org.apache.commons.collections.functors.InstantiateTransformer;
import java.util.Iterator;
import org.apache.commons.collections.functors.EqualPredicate;
import java.util.Map;
import org.apache.commons.collections.functors.SwitchTransformer;
import java.util.Collection;
import org.apache.commons.collections.functors.ChainedTransformer;
import org.apache.commons.collections.functors.FactoryTransformer;
import org.apache.commons.collections.functors.PredicateTransformer;
import org.apache.commons.collections.functors.ClosureTransformer;
import org.apache.commons.collections.functors.CloneTransformer;
import org.apache.commons.collections.functors.NOPTransformer;
import org.apache.commons.collections.functors.ConstantTransformer;
import org.apache.commons.collections.functors.ExceptionTransformer;

public class TransformerUtils
{
    public static Transformer exceptionTransformer() {
        return ExceptionTransformer.INSTANCE;
    }
    
    public static Transformer nullTransformer() {
        return ConstantTransformer.NULL_INSTANCE;
    }
    
    public static Transformer nopTransformer() {
        return NOPTransformer.INSTANCE;
    }
    
    public static Transformer cloneTransformer() {
        return CloneTransformer.INSTANCE;
    }
    
    public static Transformer constantTransformer(final Object constantToReturn) {
        return ConstantTransformer.getInstance(constantToReturn);
    }
    
    public static Transformer asTransformer(final Closure closure) {
        return ClosureTransformer.getInstance(closure);
    }
    
    public static Transformer asTransformer(final Predicate predicate) {
        return PredicateTransformer.getInstance(predicate);
    }
    
    public static Transformer asTransformer(final Factory factory) {
        return FactoryTransformer.getInstance(factory);
    }
    
    public static Transformer chainedTransformer(final Transformer transformer1, final Transformer transformer2) {
        return ChainedTransformer.getInstance(transformer1, transformer2);
    }
    
    public static Transformer chainedTransformer(final Transformer[] transformers) {
        return ChainedTransformer.getInstance(transformers);
    }
    
    public static Transformer chainedTransformer(final Collection transformers) {
        return ChainedTransformer.getInstance(transformers);
    }
    
    public static Transformer switchTransformer(final Predicate predicate, final Transformer trueTransformer, final Transformer falseTransformer) {
        return SwitchTransformer.getInstance(new Predicate[] { predicate }, new Transformer[] { trueTransformer }, falseTransformer);
    }
    
    public static Transformer switchTransformer(final Predicate[] predicates, final Transformer[] transformers) {
        return SwitchTransformer.getInstance(predicates, transformers, null);
    }
    
    public static Transformer switchTransformer(final Predicate[] predicates, final Transformer[] transformers, final Transformer defaultTransformer) {
        return SwitchTransformer.getInstance(predicates, transformers, defaultTransformer);
    }
    
    public static Transformer switchTransformer(final Map predicatesAndTransformers) {
        return SwitchTransformer.getInstance(predicatesAndTransformers);
    }
    
    public static Transformer switchMapTransformer(final Map objectsAndTransformers) {
        Transformer[] trs = null;
        Predicate[] preds = null;
        if (objectsAndTransformers == null) {
            throw new IllegalArgumentException("The object and transformer map must not be null");
        }
        final Transformer def = objectsAndTransformers.remove(null);
        final int size = objectsAndTransformers.size();
        trs = new Transformer[size];
        preds = new Predicate[size];
        int i = 0;
        final Iterator it = objectsAndTransformers.entrySet().iterator();
        while (it.hasNext()) {
            final Map.Entry entry = it.next();
            preds[i] = EqualPredicate.getInstance(entry.getKey());
            trs[i] = entry.getValue();
            ++i;
        }
        return switchTransformer(preds, trs, def);
    }
    
    public static Transformer instantiateTransformer() {
        return InstantiateTransformer.NO_ARG_INSTANCE;
    }
    
    public static Transformer instantiateTransformer(final Class[] paramTypes, final Object[] args) {
        return InstantiateTransformer.getInstance(paramTypes, args);
    }
    
    public static Transformer mapTransformer(final Map map) {
        return MapTransformer.getInstance(map);
    }
    
    public static Transformer invokerTransformer(final String methodName) {
        return InvokerTransformer.getInstance(methodName, null, null);
    }
    
    public static Transformer invokerTransformer(final String methodName, final Class[] paramTypes, final Object[] args) {
        return InvokerTransformer.getInstance(methodName, paramTypes, args);
    }
    
    public static Transformer stringValueTransformer() {
        return StringValueTransformer.INSTANCE;
    }
}
