package org.apache.commons.collections;

import org.apache.commons.collections.functors.InstantiateFactory;
import org.apache.commons.collections.functors.PrototypeFactory;
import org.apache.commons.collections.functors.ConstantFactory;
import org.apache.commons.collections.functors.ExceptionFactory;

public class FactoryUtils
{
    public static Factory exceptionFactory() {
        return ExceptionFactory.INSTANCE;
    }
    
    public static Factory nullFactory() {
        return ConstantFactory.NULL_INSTANCE;
    }
    
    public static Factory constantFactory(final Object constantToReturn) {
        return ConstantFactory.getInstance(constantToReturn);
    }
    
    public static Factory prototypeFactory(final Object prototype) {
        return PrototypeFactory.getInstance(prototype);
    }
    
    public static Factory instantiateFactory(final Class classToInstantiate) {
        return InstantiateFactory.getInstance(classToInstantiate, null, null);
    }
    
    public static Factory instantiateFactory(final Class classToInstantiate, final Class[] paramTypes, final Object[] args) {
        return InstantiateFactory.getInstance(classToInstantiate, paramTypes, args);
    }
}
