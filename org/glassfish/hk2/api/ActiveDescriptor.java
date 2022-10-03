package org.glassfish.hk2.api;

import java.util.List;
import java.lang.annotation.Annotation;
import java.util.Set;
import java.lang.reflect.Type;

public interface ActiveDescriptor<T> extends Descriptor, SingleCache<T>
{
    boolean isReified();
    
    Class<?> getImplementationClass();
    
    Type getImplementationType();
    
    Set<Type> getContractTypes();
    
    Annotation getScopeAsAnnotation();
    
    Class<? extends Annotation> getScopeAnnotation();
    
    Set<Annotation> getQualifierAnnotations();
    
    List<Injectee> getInjectees();
    
    Long getFactoryServiceId();
    
    Long getFactoryLocatorId();
    
    T create(final ServiceHandle<?> p0);
    
    void dispose(final T p0);
}
