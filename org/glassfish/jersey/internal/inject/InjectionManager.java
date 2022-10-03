package org.glassfish.jersey.internal.inject;

import java.lang.reflect.Type;
import java.util.List;
import java.lang.annotation.Annotation;

public interface InjectionManager
{
    void completeRegistration();
    
    void shutdown();
    
    void register(final Binding p0);
    
    void register(final Iterable<Binding> p0);
    
    void register(final Binder p0);
    
    void register(final Object p0) throws IllegalArgumentException;
    
    boolean isRegistrable(final Class<?> p0);
    
     <T> T createAndInitialize(final Class<T> p0);
    
     <T> List<ServiceHolder<T>> getAllServiceHolders(final Class<T> p0, final Annotation... p1);
    
     <T> T getInstance(final Class<T> p0, final Annotation... p1);
    
     <T> T getInstance(final Class<T> p0, final String p1);
    
     <T> T getInstance(final Class<T> p0);
    
     <T> T getInstance(final Type p0);
    
    Object getInstance(final ForeignDescriptor p0);
    
    ForeignDescriptor createForeignDescriptor(final Binding p0);
    
     <T> List<T> getAllInstances(final Type p0);
    
    void inject(final Object p0);
    
    void inject(final Object p0, final String p1);
    
    void preDestroy(final Object p0);
}
