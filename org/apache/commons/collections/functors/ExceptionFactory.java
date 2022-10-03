package org.apache.commons.collections.functors;

import org.apache.commons.collections.FunctorException;
import java.io.Serializable;
import org.apache.commons.collections.Factory;

public final class ExceptionFactory implements Factory, Serializable
{
    private static final long serialVersionUID = 7179106032121985545L;
    public static final Factory INSTANCE;
    
    public static Factory getInstance() {
        return ExceptionFactory.INSTANCE;
    }
    
    private ExceptionFactory() {
    }
    
    public Object create() {
        throw new FunctorException("ExceptionFactory invoked");
    }
    
    static {
        INSTANCE = new ExceptionFactory();
    }
}
