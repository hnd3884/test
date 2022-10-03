package org.apache.commons.collections.functors;

import org.apache.commons.collections.FunctorException;
import java.io.Serializable;
import org.apache.commons.collections.Closure;

public final class ExceptionClosure implements Closure, Serializable
{
    private static final long serialVersionUID = 7179106032121985545L;
    public static final Closure INSTANCE;
    
    public static Closure getInstance() {
        return ExceptionClosure.INSTANCE;
    }
    
    private ExceptionClosure() {
    }
    
    public void execute(final Object input) {
        throw new FunctorException("ExceptionClosure invoked");
    }
    
    static {
        INSTANCE = new ExceptionClosure();
    }
}
