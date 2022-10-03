package org.glassfish.hk2.api;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import javax.inject.Provider;

public interface IterableProvider<T> extends Provider<T>, Iterable<T>
{
    ServiceHandle<T> getHandle();
    
    int getSize();
    
    IterableProvider<T> named(final String p0);
    
     <U> IterableProvider<U> ofType(final Type p0);
    
    IterableProvider<T> qualifiedWith(final Annotation... p0);
    
    Iterable<ServiceHandle<T>> handleIterator();
}
