package org.apache.commons.collections.functors;

import org.apache.commons.collections.FunctorException;
import java.io.Serializable;
import org.apache.commons.collections.Transformer;

public final class ExceptionTransformer implements Transformer, Serializable
{
    private static final long serialVersionUID = 7179106032121985545L;
    public static final Transformer INSTANCE;
    
    public static Transformer getInstance() {
        return ExceptionTransformer.INSTANCE;
    }
    
    private ExceptionTransformer() {
    }
    
    public Object transform(final Object input) {
        throw new FunctorException("ExceptionTransformer invoked");
    }
    
    static {
        INSTANCE = new ExceptionTransformer();
    }
}
