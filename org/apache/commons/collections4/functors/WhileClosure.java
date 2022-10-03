package org.apache.commons.collections4.functors;

import org.apache.commons.collections4.Predicate;
import org.apache.commons.collections4.Closure;

public class WhileClosure<E> implements Closure<E>
{
    private final Predicate<? super E> iPredicate;
    private final Closure<? super E> iClosure;
    private final boolean iDoLoop;
    
    public static <E> Closure<E> whileClosure(final Predicate<? super E> predicate, final Closure<? super E> closure, final boolean doLoop) {
        if (predicate == null) {
            throw new NullPointerException("Predicate must not be null");
        }
        if (closure == null) {
            throw new NullPointerException("Closure must not be null");
        }
        return new WhileClosure<E>(predicate, closure, doLoop);
    }
    
    public WhileClosure(final Predicate<? super E> predicate, final Closure<? super E> closure, final boolean doLoop) {
        this.iPredicate = predicate;
        this.iClosure = closure;
        this.iDoLoop = doLoop;
    }
    
    @Override
    public void execute(final E input) {
        if (this.iDoLoop) {
            this.iClosure.execute(input);
        }
        while (this.iPredicate.evaluate(input)) {
            this.iClosure.execute(input);
        }
    }
    
    public Predicate<? super E> getPredicate() {
        return this.iPredicate;
    }
    
    public Closure<? super E> getClosure() {
        return this.iClosure;
    }
    
    public boolean isDoLoop() {
        return this.iDoLoop;
    }
}
