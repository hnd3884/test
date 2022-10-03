package org.apache.commons.collections4.functors;

import org.apache.commons.collections4.FunctorException;
import java.io.Serializable;
import org.apache.commons.collections4.Transformer;

public final class ExceptionTransformer<I, O> implements Transformer<I, O>, Serializable
{
    private static final long serialVersionUID = 7179106032121985545L;
    public static final Transformer INSTANCE;
    
    public static <I, O> Transformer<I, O> exceptionTransformer() {
        return ExceptionTransformer.INSTANCE;
    }
    
    private ExceptionTransformer() {
    }
    
    @Override
    public O transform(final I input) {
        throw new FunctorException("ExceptionTransformer invoked");
    }
    
    private Object readResolve() {
        return ExceptionTransformer.INSTANCE;
    }
    
    static {
        INSTANCE = new ExceptionTransformer();
    }
}
