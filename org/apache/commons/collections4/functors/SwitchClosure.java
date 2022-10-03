package org.apache.commons.collections4.functors;

import java.util.Iterator;
import java.util.Map;
import org.apache.commons.collections4.Predicate;
import java.io.Serializable;
import org.apache.commons.collections4.Closure;

public class SwitchClosure<E> implements Closure<E>, Serializable
{
    private static final long serialVersionUID = 3518477308466486130L;
    private final Predicate<? super E>[] iPredicates;
    private final Closure<? super E>[] iClosures;
    private final Closure<? super E> iDefault;
    
    public static <E> Closure<E> switchClosure(final Predicate<? super E>[] predicates, final Closure<? super E>[] closures, final Closure<? super E> defaultClosure) {
        FunctorUtils.validate((Predicate<?>[])predicates);
        FunctorUtils.validate((Closure<?>[])closures);
        if (predicates.length != closures.length) {
            throw new IllegalArgumentException("The predicate and closure arrays must be the same size");
        }
        if (predicates.length == 0) {
            return (Closure<E>)((defaultClosure == null) ? NOPClosure.nopClosure() : defaultClosure);
        }
        return new SwitchClosure<E>(predicates, closures, defaultClosure);
    }
    
    public static <E> Closure<E> switchClosure(final Map<Predicate<E>, Closure<E>> predicatesAndClosures) {
        if (predicatesAndClosures == null) {
            throw new NullPointerException("The predicate and closure map must not be null");
        }
        final Closure<? super E> defaultClosure = predicatesAndClosures.remove(null);
        final int size = predicatesAndClosures.size();
        if (size == 0) {
            return (Closure<E>)((defaultClosure == null) ? NOPClosure.nopClosure() : defaultClosure);
        }
        final Closure<E>[] closures = new Closure[size];
        final Predicate<E>[] preds = new Predicate[size];
        int i = 0;
        for (final Map.Entry<Predicate<E>, Closure<E>> entry : predicatesAndClosures.entrySet()) {
            preds[i] = entry.getKey();
            closures[i] = entry.getValue();
            ++i;
        }
        return new SwitchClosure<E>(false, preds, closures, defaultClosure);
    }
    
    private SwitchClosure(final boolean clone, final Predicate<? super E>[] predicates, final Closure<? super E>[] closures, final Closure<? super E> defaultClosure) {
        this.iPredicates = (clone ? FunctorUtils.copy(predicates) : predicates);
        this.iClosures = (clone ? FunctorUtils.copy(closures) : closures);
        this.iDefault = ((defaultClosure == null) ? NOPClosure.nopClosure() : defaultClosure);
    }
    
    public SwitchClosure(final Predicate<? super E>[] predicates, final Closure<? super E>[] closures, final Closure<? super E> defaultClosure) {
        this(true, predicates, closures, defaultClosure);
    }
    
    @Override
    public void execute(final E input) {
        for (int i = 0; i < this.iPredicates.length; ++i) {
            if (this.iPredicates[i].evaluate(input)) {
                this.iClosures[i].execute(input);
                return;
            }
        }
        this.iDefault.execute(input);
    }
    
    public Predicate<? super E>[] getPredicates() {
        return FunctorUtils.copy(this.iPredicates);
    }
    
    public Closure<? super E>[] getClosures() {
        return FunctorUtils.copy(this.iClosures);
    }
    
    public Closure<? super E> getDefaultClosure() {
        return this.iDefault;
    }
}
