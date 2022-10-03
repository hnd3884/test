package org.apache.commons.collections4.functors;

import org.apache.commons.collections4.Transformer;

public class CloneTransformer<T> implements Transformer<T, T>
{
    public static final Transformer INSTANCE;
    
    public static <T> Transformer<T, T> cloneTransformer() {
        return CloneTransformer.INSTANCE;
    }
    
    private CloneTransformer() {
    }
    
    @Override
    public T transform(final T input) {
        if (input == null) {
            return null;
        }
        return PrototypeFactory.prototypeFactory(input).create();
    }
    
    static {
        INSTANCE = new CloneTransformer();
    }
}
