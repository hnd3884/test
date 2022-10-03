package org.glassfish.jersey.internal.inject;

import java.lang.annotation.Annotation;

public interface InjectionResolver<T extends Annotation>
{
    Object resolve(final Injectee p0);
    
    boolean isConstructorParameterIndicator();
    
    boolean isMethodParameterIndicator();
    
    Class<T> getAnnotation();
}
