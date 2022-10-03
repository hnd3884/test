package org.apache.commons.collections4;

import org.apache.commons.collections4.functors.StringValueTransformer;
import org.apache.commons.collections4.functors.InvokerTransformer;
import org.apache.commons.collections4.functors.MapTransformer;
import org.apache.commons.collections4.functors.InstantiateTransformer;
import java.util.Iterator;
import org.apache.commons.collections4.functors.EqualPredicate;
import java.util.Map;
import org.apache.commons.collections4.functors.SwitchTransformer;
import org.apache.commons.collections4.functors.IfTransformer;
import java.util.Collection;
import org.apache.commons.collections4.functors.ChainedTransformer;
import org.apache.commons.collections4.functors.FactoryTransformer;
import org.apache.commons.collections4.functors.PredicateTransformer;
import org.apache.commons.collections4.functors.ClosureTransformer;
import org.apache.commons.collections4.functors.CloneTransformer;
import org.apache.commons.collections4.functors.NOPTransformer;
import org.apache.commons.collections4.functors.ConstantTransformer;
import org.apache.commons.collections4.functors.ExceptionTransformer;

public class TransformerUtils
{
    private TransformerUtils() {
    }
    
    public static <I, O> Transformer<I, O> exceptionTransformer() {
        return ExceptionTransformer.exceptionTransformer();
    }
    
    public static <I, O> Transformer<I, O> nullTransformer() {
        return ConstantTransformer.nullTransformer();
    }
    
    public static <T> Transformer<T, T> nopTransformer() {
        return NOPTransformer.nopTransformer();
    }
    
    public static <T> Transformer<T, T> cloneTransformer() {
        return CloneTransformer.cloneTransformer();
    }
    
    public static <I, O> Transformer<I, O> constantTransformer(final O constantToReturn) {
        return ConstantTransformer.constantTransformer(constantToReturn);
    }
    
    public static <T> Transformer<T, T> asTransformer(final Closure<? super T> closure) {
        return ClosureTransformer.closureTransformer(closure);
    }
    
    public static <T> Transformer<T, Boolean> asTransformer(final Predicate<? super T> predicate) {
        return PredicateTransformer.predicateTransformer(predicate);
    }
    
    public static <I, O> Transformer<I, O> asTransformer(final Factory<? extends O> factory) {
        return FactoryTransformer.factoryTransformer(factory);
    }
    
    public static <T> Transformer<T, T> chainedTransformer(final Transformer<? super T, ? extends T>... transformers) {
        return ChainedTransformer.chainedTransformer(transformers);
    }
    
    public static <T> Transformer<T, T> chainedTransformer(final Collection<? extends Transformer<? super T, ? extends T>> transformers) {
        return ChainedTransformer.chainedTransformer(transformers);
    }
    
    public static <T> Transformer<T, T> ifTransformer(final Predicate<? super T> predicate, final Transformer<? super T, ? extends T> trueTransformer) {
        return IfTransformer.ifTransformer(predicate, trueTransformer);
    }
    
    public static <I, O> Transformer<I, O> ifTransformer(final Predicate<? super I> predicate, final Transformer<? super I, ? extends O> trueTransformer, final Transformer<? super I, ? extends O> falseTransformer) {
        return IfTransformer.ifTransformer(predicate, trueTransformer, falseTransformer);
    }
    
    @Deprecated
    public static <I, O> Transformer<I, O> switchTransformer(final Predicate<? super I> predicate, final Transformer<? super I, ? extends O> trueTransformer, final Transformer<? super I, ? extends O> falseTransformer) {
        return SwitchTransformer.switchTransformer((Predicate<? super I>[])new Predicate[] { predicate }, (Transformer<? super I, ? extends O>[])new Transformer[] { trueTransformer }, falseTransformer);
    }
    
    public static <I, O> Transformer<I, O> switchTransformer(final Predicate<? super I>[] predicates, final Transformer<? super I, ? extends O>[] transformers) {
        return SwitchTransformer.switchTransformer(predicates, transformers, (Transformer<? super I, ? extends O>)null);
    }
    
    public static <I, O> Transformer<I, O> switchTransformer(final Predicate<? super I>[] predicates, final Transformer<? super I, ? extends O>[] transformers, final Transformer<? super I, ? extends O> defaultTransformer) {
        return SwitchTransformer.switchTransformer(predicates, transformers, defaultTransformer);
    }
    
    public static <I, O> Transformer<I, O> switchTransformer(final Map<Predicate<I>, Transformer<I, O>> predicatesAndTransformers) {
        return SwitchTransformer.switchTransformer((Map<? extends Predicate<? super I>, ? extends Transformer<? super I, ? extends O>>)predicatesAndTransformers);
    }
    
    public static <I, O> Transformer<I, O> switchMapTransformer(final Map<I, Transformer<I, O>> objectsAndTransformers) {
        if (objectsAndTransformers == null) {
            throw new NullPointerException("The object and transformer map must not be null");
        }
        final Transformer<? super I, ? extends O> def = (Transformer<? super I, ? extends O>)objectsAndTransformers.remove(null);
        final int size = objectsAndTransformers.size();
        final Transformer<? super I, ? extends O>[] trs = new Transformer[size];
        final Predicate<I>[] preds = new Predicate[size];
        int i = 0;
        for (final Map.Entry<I, Transformer<I, O>> entry : objectsAndTransformers.entrySet()) {
            preds[i] = EqualPredicate.equalPredicate(entry.getKey());
            trs[i++] = (Transformer)entry.getValue();
        }
        return switchTransformer((Predicate<? super I>[])preds, trs, def);
    }
    
    public static <T> Transformer<Class<? extends T>, T> instantiateTransformer() {
        return InstantiateTransformer.instantiateTransformer();
    }
    
    public static <T> Transformer<Class<? extends T>, T> instantiateTransformer(final Class<?>[] paramTypes, final Object[] args) {
        return InstantiateTransformer.instantiateTransformer(paramTypes, args);
    }
    
    public static <I, O> Transformer<I, O> mapTransformer(final Map<? super I, ? extends O> map) {
        return MapTransformer.mapTransformer(map);
    }
    
    public static <I, O> Transformer<I, O> invokerTransformer(final String methodName) {
        return InvokerTransformer.invokerTransformer(methodName, null, null);
    }
    
    public static <I, O> Transformer<I, O> invokerTransformer(final String methodName, final Class<?>[] paramTypes, final Object[] args) {
        return InvokerTransformer.invokerTransformer(methodName, paramTypes, args);
    }
    
    public static <T> Transformer<T, String> stringValueTransformer() {
        return StringValueTransformer.stringValueTransformer();
    }
}
