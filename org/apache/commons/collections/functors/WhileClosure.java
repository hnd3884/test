package org.apache.commons.collections.functors;

import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import org.apache.commons.collections.Predicate;
import java.io.Serializable;
import org.apache.commons.collections.Closure;

public class WhileClosure implements Closure, Serializable
{
    private static final long serialVersionUID = -3110538116913760108L;
    private final Predicate iPredicate;
    private final Closure iClosure;
    private final boolean iDoLoop;
    
    public static Closure getInstance(final Predicate predicate, final Closure closure, final boolean doLoop) {
        if (predicate == null) {
            throw new IllegalArgumentException("Predicate must not be null");
        }
        if (closure == null) {
            throw new IllegalArgumentException("Closure must not be null");
        }
        return new WhileClosure(predicate, closure, doLoop);
    }
    
    public WhileClosure(final Predicate predicate, final Closure closure, final boolean doLoop) {
        this.iPredicate = predicate;
        this.iClosure = closure;
        this.iDoLoop = doLoop;
    }
    
    public void execute(final Object input) {
        if (this.iDoLoop) {
            this.iClosure.execute(input);
        }
        while (this.iPredicate.evaluate(input)) {
            this.iClosure.execute(input);
        }
    }
    
    public Predicate getPredicate() {
        return this.iPredicate;
    }
    
    public Closure getClosure() {
        return this.iClosure;
    }
    
    public boolean isDoLoop() {
        return this.iDoLoop;
    }
    
    private void writeObject(final ObjectOutputStream os) throws IOException {
        FunctorUtils.checkUnsafeSerialization(WhileClosure.class);
        os.defaultWriteObject();
    }
    
    private void readObject(final ObjectInputStream is) throws ClassNotFoundException, IOException {
        FunctorUtils.checkUnsafeSerialization(WhileClosure.class);
        is.defaultReadObject();
    }
}
