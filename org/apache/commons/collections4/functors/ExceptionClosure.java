package org.apache.commons.collections4.functors;

import org.apache.commons.collections4.FunctorException;
import java.io.Serializable;
import org.apache.commons.collections4.Closure;

public final class ExceptionClosure<E> implements Closure<E>, Serializable
{
    private static final long serialVersionUID = 7179106032121985545L;
    public static final Closure INSTANCE;
    
    public static <E> Closure<E> exceptionClosure() {
        return ExceptionClosure.INSTANCE;
    }
    
    private ExceptionClosure() {
    }
    
    @Override
    public void execute(final E input) {
        throw new FunctorException("ExceptionClosure invoked");
    }
    
    private Object readResolve() {
        return ExceptionClosure.INSTANCE;
    }
    
    static {
        INSTANCE = new ExceptionClosure();
    }
}
