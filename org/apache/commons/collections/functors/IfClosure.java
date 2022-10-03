package org.apache.commons.collections.functors;

import org.apache.commons.collections.Predicate;
import java.io.Serializable;
import org.apache.commons.collections.Closure;

public class IfClosure implements Closure, Serializable
{
    private static final long serialVersionUID = 3518477308466486130L;
    private final Predicate iPredicate;
    private final Closure iTrueClosure;
    private final Closure iFalseClosure;
    
    public static Closure getInstance(final Predicate predicate, final Closure trueClosure) {
        return getInstance(predicate, trueClosure, NOPClosure.INSTANCE);
    }
    
    public static Closure getInstance(final Predicate predicate, final Closure trueClosure, final Closure falseClosure) {
        if (predicate == null) {
            throw new IllegalArgumentException("Predicate must not be null");
        }
        if (trueClosure == null || falseClosure == null) {
            throw new IllegalArgumentException("Closures must not be null");
        }
        return new IfClosure(predicate, trueClosure, falseClosure);
    }
    
    public IfClosure(final Predicate predicate, final Closure trueClosure) {
        this(predicate, trueClosure, NOPClosure.INSTANCE);
    }
    
    public IfClosure(final Predicate predicate, final Closure trueClosure, final Closure falseClosure) {
        this.iPredicate = predicate;
        this.iTrueClosure = trueClosure;
        this.iFalseClosure = falseClosure;
    }
    
    public void execute(final Object input) {
        if (this.iPredicate.evaluate(input)) {
            this.iTrueClosure.execute(input);
        }
        else {
            this.iFalseClosure.execute(input);
        }
    }
    
    public Predicate getPredicate() {
        return this.iPredicate;
    }
    
    public Closure getTrueClosure() {
        return this.iTrueClosure;
    }
    
    public Closure getFalseClosure() {
        return this.iFalseClosure;
    }
}
