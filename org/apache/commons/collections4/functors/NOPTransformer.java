package org.apache.commons.collections4.functors;

import java.io.Serializable;
import org.apache.commons.collections4.Transformer;

public class NOPTransformer<T> implements Transformer<T, T>, Serializable
{
    private static final long serialVersionUID = 2133891748318574490L;
    public static final Transformer INSTANCE;
    
    public static <T> Transformer<T, T> nopTransformer() {
        return NOPTransformer.INSTANCE;
    }
    
    private NOPTransformer() {
    }
    
    @Override
    public T transform(final T input) {
        return input;
    }
    
    private Object readResolve() {
        return NOPTransformer.INSTANCE;
    }
    
    static {
        INSTANCE = new NOPTransformer();
    }
}
