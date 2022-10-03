package org.apache.commons.collections.functors;

import org.apache.commons.collections.Closure;
import java.io.Serializable;
import org.apache.commons.collections.Transformer;

public class ClosureTransformer implements Transformer, Serializable
{
    private static final long serialVersionUID = 478466901448617286L;
    private final Closure iClosure;
    
    public static Transformer getInstance(final Closure closure) {
        if (closure == null) {
            throw new IllegalArgumentException("Closure must not be null");
        }
        return new ClosureTransformer(closure);
    }
    
    public ClosureTransformer(final Closure closure) {
        this.iClosure = closure;
    }
    
    public Object transform(final Object input) {
        this.iClosure.execute(input);
        return input;
    }
    
    public Closure getClosure() {
        return this.iClosure;
    }
}
