package org.glassfish.hk2.api;

import java.lang.reflect.AnnotatedElement;
import java.lang.annotation.Annotation;
import java.util.Set;
import java.lang.reflect.Type;

public interface Injectee
{
    Type getRequiredType();
    
    Set<Annotation> getRequiredQualifiers();
    
    int getPosition();
    
    Class<?> getInjecteeClass();
    
    AnnotatedElement getParent();
    
    boolean isOptional();
    
    boolean isSelf();
    
    Unqualified getUnqualified();
    
    ActiveDescriptor<?> getInjecteeDescriptor();
}
