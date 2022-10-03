package org.apache.commons.collections4.functors;

import org.apache.commons.collections4.Predicate;
import java.io.Serializable;
import org.apache.commons.collections4.Closure;

public class IfClosure<E> implements Closure<E>, Serializable
{
    private static final long serialVersionUID = 3518477308466486130L;
    private final Predicate<? super E> iPredicate;
    private final Closure<? super E> iTrueClosure;
    private final Closure<? super E> iFalseClosure;
    
    public static <E> Closure<E> ifClosure(final Predicate<? super E> predicate, final Closure<? super E> trueClosure) {
        return ifClosure(predicate, trueClosure, NOPClosure.nopClosure());
    }
    
    public static <E> Closure<E> ifClosure(final Predicate<? super E> predicate, final Closure<? super E> trueClosure, final Closure<? super E> falseClosure) {
        if (predicate == null) {
            throw new NullPointerException("Predicate must not be null");
        }
        if (trueClosure == null || falseClosure == null) {
            throw new NullPointerException("Closures must not be null");
        }
        return new IfClosure<E>(predicate, trueClosure, falseClosure);
    }
    
    public IfClosure(final Predicate<? super E> predicate, final Closure<? super E> trueClosure) {
        this(predicate, trueClosure, NOPClosure.nopClosure());
    }
    
    public IfClosure(final Predicate<? super E> predicate, final Closure<? super E> trueClosure, final Closure<? super E> falseClosure) {
        this.iPredicate = predicate;
        this.iTrueClosure = trueClosure;
        this.iFalseClosure = falseClosure;
    }
    
    @Override
    public void execute(final E input) {
        if (this.iPredicate.evaluate(input)) {
            this.iTrueClosure.execute(input);
        }
        else {
            this.iFalseClosure.execute(input);
        }
    }
    
    public Predicate<? super E> getPredicate() {
        return this.iPredicate;
    }
    
    public Closure<? super E> getTrueClosure() {
        return this.iTrueClosure;
    }
    
    public Closure<? super E> getFalseClosure() {
        return this.iFalseClosure;
    }
}
