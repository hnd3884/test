package org.apache.commons.collections4;

import java.util.Iterator;
import org.apache.commons.collections4.functors.EqualPredicate;
import java.util.Map;
import org.apache.commons.collections4.functors.SwitchClosure;
import org.apache.commons.collections4.functors.IfClosure;
import java.util.Collection;
import org.apache.commons.collections4.functors.ChainedClosure;
import org.apache.commons.collections4.functors.InvokerTransformer;
import org.apache.commons.collections4.functors.WhileClosure;
import org.apache.commons.collections4.functors.ForClosure;
import org.apache.commons.collections4.functors.TransformerClosure;
import org.apache.commons.collections4.functors.NOPClosure;
import org.apache.commons.collections4.functors.ExceptionClosure;

public class ClosureUtils
{
    private ClosureUtils() {
    }
    
    public static <E> Closure<E> exceptionClosure() {
        return ExceptionClosure.exceptionClosure();
    }
    
    public static <E> Closure<E> nopClosure() {
        return NOPClosure.nopClosure();
    }
    
    public static <E> Closure<E> asClosure(final Transformer<? super E, ?> transformer) {
        return TransformerClosure.transformerClosure(transformer);
    }
    
    public static <E> Closure<E> forClosure(final int count, final Closure<? super E> closure) {
        return ForClosure.forClosure(count, closure);
    }
    
    public static <E> Closure<E> whileClosure(final Predicate<? super E> predicate, final Closure<? super E> closure) {
        return WhileClosure.whileClosure(predicate, closure, false);
    }
    
    public static <E> Closure<E> doWhileClosure(final Closure<? super E> closure, final Predicate<? super E> predicate) {
        return WhileClosure.whileClosure(predicate, closure, true);
    }
    
    public static <E> Closure<E> invokerClosure(final String methodName) {
        return asClosure(InvokerTransformer.invokerTransformer(methodName));
    }
    
    public static <E> Closure<E> invokerClosure(final String methodName, final Class<?>[] paramTypes, final Object[] args) {
        return asClosure(InvokerTransformer.invokerTransformer(methodName, paramTypes, args));
    }
    
    public static <E> Closure<E> chainedClosure(final Closure<? super E>... closures) {
        return ChainedClosure.chainedClosure(closures);
    }
    
    public static <E> Closure<E> chainedClosure(final Collection<? extends Closure<? super E>> closures) {
        return ChainedClosure.chainedClosure(closures);
    }
    
    public static <E> Closure<E> ifClosure(final Predicate<? super E> predicate, final Closure<? super E> trueClosure) {
        return IfClosure.ifClosure(predicate, trueClosure);
    }
    
    public static <E> Closure<E> ifClosure(final Predicate<? super E> predicate, final Closure<? super E> trueClosure, final Closure<? super E> falseClosure) {
        return IfClosure.ifClosure(predicate, trueClosure, falseClosure);
    }
    
    public static <E> Closure<E> switchClosure(final Predicate<? super E>[] predicates, final Closure<? super E>[] closures) {
        return SwitchClosure.switchClosure(predicates, closures, (Closure<? super E>)null);
    }
    
    public static <E> Closure<E> switchClosure(final Predicate<? super E>[] predicates, final Closure<? super E>[] closures, final Closure<? super E> defaultClosure) {
        return SwitchClosure.switchClosure(predicates, closures, defaultClosure);
    }
    
    public static <E> Closure<E> switchClosure(final Map<Predicate<E>, Closure<E>> predicatesAndClosures) {
        return SwitchClosure.switchClosure(predicatesAndClosures);
    }
    
    public static <E> Closure<E> switchMapClosure(final Map<? extends E, Closure<E>> objectsAndClosures) {
        if (objectsAndClosures == null) {
            throw new NullPointerException("The object and closure map must not be null");
        }
        final Closure<? super E> def = objectsAndClosures.remove(null);
        final int size = objectsAndClosures.size();
        final Closure<? super E>[] trs = new Closure[size];
        final Predicate<E>[] preds = new Predicate[size];
        int i = 0;
        for (final Map.Entry<? extends E, Closure<E>> entry : objectsAndClosures.entrySet()) {
            preds[i] = EqualPredicate.equalPredicate((E)entry.getKey());
            trs[i] = entry.getValue();
            ++i;
        }
        return switchClosure((Predicate<? super E>[])preds, trs, def);
    }
}
