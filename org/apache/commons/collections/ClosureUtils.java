package org.apache.commons.collections;

import java.util.Iterator;
import org.apache.commons.collections.functors.EqualPredicate;
import java.util.Map;
import org.apache.commons.collections.functors.SwitchClosure;
import org.apache.commons.collections.functors.IfClosure;
import java.util.Collection;
import org.apache.commons.collections.functors.ChainedClosure;
import org.apache.commons.collections.functors.InvokerTransformer;
import org.apache.commons.collections.functors.WhileClosure;
import org.apache.commons.collections.functors.ForClosure;
import org.apache.commons.collections.functors.TransformerClosure;
import org.apache.commons.collections.functors.NOPClosure;
import org.apache.commons.collections.functors.ExceptionClosure;

public class ClosureUtils
{
    public static Closure exceptionClosure() {
        return ExceptionClosure.INSTANCE;
    }
    
    public static Closure nopClosure() {
        return NOPClosure.INSTANCE;
    }
    
    public static Closure asClosure(final Transformer transformer) {
        return TransformerClosure.getInstance(transformer);
    }
    
    public static Closure forClosure(final int count, final Closure closure) {
        return ForClosure.getInstance(count, closure);
    }
    
    public static Closure whileClosure(final Predicate predicate, final Closure closure) {
        return WhileClosure.getInstance(predicate, closure, false);
    }
    
    public static Closure doWhileClosure(final Closure closure, final Predicate predicate) {
        return WhileClosure.getInstance(predicate, closure, true);
    }
    
    public static Closure invokerClosure(final String methodName) {
        return asClosure(InvokerTransformer.getInstance(methodName));
    }
    
    public static Closure invokerClosure(final String methodName, final Class[] paramTypes, final Object[] args) {
        return asClosure(InvokerTransformer.getInstance(methodName, paramTypes, args));
    }
    
    public static Closure chainedClosure(final Closure closure1, final Closure closure2) {
        return ChainedClosure.getInstance(closure1, closure2);
    }
    
    public static Closure chainedClosure(final Closure[] closures) {
        return ChainedClosure.getInstance(closures);
    }
    
    public static Closure chainedClosure(final Collection closures) {
        return ChainedClosure.getInstance(closures);
    }
    
    public static Closure ifClosure(final Predicate predicate, final Closure trueClosure) {
        return IfClosure.getInstance(predicate, trueClosure);
    }
    
    public static Closure ifClosure(final Predicate predicate, final Closure trueClosure, final Closure falseClosure) {
        return IfClosure.getInstance(predicate, trueClosure, falseClosure);
    }
    
    public static Closure switchClosure(final Predicate[] predicates, final Closure[] closures) {
        return SwitchClosure.getInstance(predicates, closures, null);
    }
    
    public static Closure switchClosure(final Predicate[] predicates, final Closure[] closures, final Closure defaultClosure) {
        return SwitchClosure.getInstance(predicates, closures, defaultClosure);
    }
    
    public static Closure switchClosure(final Map predicatesAndClosures) {
        return SwitchClosure.getInstance(predicatesAndClosures);
    }
    
    public static Closure switchMapClosure(final Map objectsAndClosures) {
        Closure[] trs = null;
        Predicate[] preds = null;
        if (objectsAndClosures == null) {
            throw new IllegalArgumentException("The object and closure map must not be null");
        }
        final Closure def = objectsAndClosures.remove(null);
        final int size = objectsAndClosures.size();
        trs = new Closure[size];
        preds = new Predicate[size];
        int i = 0;
        final Iterator it = objectsAndClosures.entrySet().iterator();
        while (it.hasNext()) {
            final Map.Entry entry = it.next();
            preds[i] = EqualPredicate.getInstance(entry.getKey());
            trs[i] = entry.getValue();
            ++i;
        }
        return switchClosure(preds, trs, def);
    }
}
