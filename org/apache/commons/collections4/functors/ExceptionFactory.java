package org.apache.commons.collections4.functors;

import org.apache.commons.collections4.FunctorException;
import java.io.Serializable;
import org.apache.commons.collections4.Factory;

public final class ExceptionFactory<T> implements Factory<T>, Serializable
{
    private static final long serialVersionUID = 7179106032121985545L;
    public static final Factory INSTANCE;
    
    public static <T> Factory<T> exceptionFactory() {
        return ExceptionFactory.INSTANCE;
    }
    
    private ExceptionFactory() {
    }
    
    @Override
    public T create() {
        throw new FunctorException("ExceptionFactory invoked");
    }
    
    private Object readResolve() {
        return ExceptionFactory.INSTANCE;
    }
    
    static {
        INSTANCE = new ExceptionFactory();
    }
}
