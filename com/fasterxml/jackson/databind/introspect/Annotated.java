package com.fasterxml.jackson.databind.introspect;

import com.fasterxml.jackson.databind.JavaType;
import java.lang.reflect.Modifier;
import java.lang.reflect.AnnotatedElement;
import java.lang.annotation.Annotation;

public abstract class Annotated
{
    protected Annotated() {
    }
    
    public abstract <A extends Annotation> A getAnnotation(final Class<A> p0);
    
    public abstract boolean hasAnnotation(final Class<?> p0);
    
    public abstract boolean hasOneOf(final Class<? extends Annotation>[] p0);
    
    public abstract AnnotatedElement getAnnotated();
    
    protected abstract int getModifiers();
    
    public boolean isPublic() {
        return Modifier.isPublic(this.getModifiers());
    }
    
    public abstract String getName();
    
    public abstract JavaType getType();
    
    public abstract Class<?> getRawType();
    
    @Deprecated
    public abstract Iterable<Annotation> annotations();
    
    @Override
    public abstract boolean equals(final Object p0);
    
    @Override
    public abstract int hashCode();
    
    @Override
    public abstract String toString();
}
