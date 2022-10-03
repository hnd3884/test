package org.apache.commons.collections4;

import org.apache.commons.collections4.functors.InstantiateFactory;
import org.apache.commons.collections4.functors.PrototypeFactory;
import org.apache.commons.collections4.functors.ConstantFactory;
import org.apache.commons.collections4.functors.ExceptionFactory;

public class FactoryUtils
{
    private FactoryUtils() {
    }
    
    public static <T> Factory<T> exceptionFactory() {
        return ExceptionFactory.exceptionFactory();
    }
    
    public static <T> Factory<T> nullFactory() {
        return ConstantFactory.constantFactory((T)null);
    }
    
    public static <T> Factory<T> constantFactory(final T constantToReturn) {
        return ConstantFactory.constantFactory(constantToReturn);
    }
    
    public static <T> Factory<T> prototypeFactory(final T prototype) {
        return PrototypeFactory.prototypeFactory(prototype);
    }
    
    public static <T> Factory<T> instantiateFactory(final Class<T> classToInstantiate) {
        return InstantiateFactory.instantiateFactory(classToInstantiate, null, null);
    }
    
    public static <T> Factory<T> instantiateFactory(final Class<T> classToInstantiate, final Class<?>[] paramTypes, final Object[] args) {
        return InstantiateFactory.instantiateFactory(classToInstantiate, paramTypes, args);
    }
}
