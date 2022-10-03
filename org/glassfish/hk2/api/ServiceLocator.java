package org.glassfish.hk2.api;

import java.lang.reflect.Method;
import java.util.List;
import java.lang.reflect.Type;
import java.lang.annotation.Annotation;
import org.jvnet.hk2.annotations.Contract;

@Contract
public interface ServiceLocator
{
     <T> T getService(final Class<T> p0, final Annotation... p1) throws MultiException;
    
     <T> T getService(final Type p0, final Annotation... p1) throws MultiException;
    
     <T> T getService(final Class<T> p0, final String p1, final Annotation... p2) throws MultiException;
    
     <T> T getService(final Type p0, final String p1, final Annotation... p2) throws MultiException;
    
     <T> List<T> getAllServices(final Class<T> p0, final Annotation... p1) throws MultiException;
    
     <T> List<T> getAllServices(final Type p0, final Annotation... p1) throws MultiException;
    
     <T> List<T> getAllServices(final Annotation p0, final Annotation... p1) throws MultiException;
    
    List<?> getAllServices(final Filter p0) throws MultiException;
    
     <T> ServiceHandle<T> getServiceHandle(final Class<T> p0, final Annotation... p1) throws MultiException;
    
     <T> ServiceHandle<T> getServiceHandle(final Type p0, final Annotation... p1) throws MultiException;
    
     <T> ServiceHandle<T> getServiceHandle(final Class<T> p0, final String p1, final Annotation... p2) throws MultiException;
    
     <T> ServiceHandle<T> getServiceHandle(final Type p0, final String p1, final Annotation... p2) throws MultiException;
    
     <T> List<ServiceHandle<T>> getAllServiceHandles(final Class<T> p0, final Annotation... p1) throws MultiException;
    
    List<ServiceHandle<?>> getAllServiceHandles(final Type p0, final Annotation... p1) throws MultiException;
    
    List<ServiceHandle<?>> getAllServiceHandles(final Annotation p0, final Annotation... p1) throws MultiException;
    
    List<ServiceHandle<?>> getAllServiceHandles(final Filter p0) throws MultiException;
    
    List<ActiveDescriptor<?>> getDescriptors(final Filter p0);
    
    ActiveDescriptor<?> getBestDescriptor(final Filter p0);
    
    ActiveDescriptor<?> reifyDescriptor(final Descriptor p0, final Injectee p1) throws MultiException;
    
    ActiveDescriptor<?> reifyDescriptor(final Descriptor p0) throws MultiException;
    
    ActiveDescriptor<?> getInjecteeDescriptor(final Injectee p0) throws MultiException;
    
     <T> ServiceHandle<T> getServiceHandle(final ActiveDescriptor<T> p0, final Injectee p1) throws MultiException;
    
     <T> ServiceHandle<T> getServiceHandle(final ActiveDescriptor<T> p0) throws MultiException;
    
    @Deprecated
     <T> T getService(final ActiveDescriptor<T> p0, final ServiceHandle<?> p1) throws MultiException;
    
     <T> T getService(final ActiveDescriptor<T> p0, final ServiceHandle<?> p1, final Injectee p2) throws MultiException;
    
    String getDefaultClassAnalyzerName();
    
    void setDefaultClassAnalyzerName(final String p0);
    
    Unqualified getDefaultUnqualified();
    
    void setDefaultUnqualified(final Unqualified p0);
    
    String getName();
    
    long getLocatorId();
    
    ServiceLocator getParent();
    
    void shutdown();
    
    ServiceLocatorState getState();
    
    boolean getNeutralContextClassLoader();
    
    void setNeutralContextClassLoader(final boolean p0);
    
     <T> T create(final Class<T> p0);
    
     <T> T create(final Class<T> p0, final String p1);
    
    void inject(final Object p0);
    
    void inject(final Object p0, final String p1);
    
    Object assistedInject(final Object p0, final Method p1, final MethodParameter... p2);
    
    Object assistedInject(final Object p0, final Method p1, final ServiceHandle<?> p2, final MethodParameter... p3);
    
    void postConstruct(final Object p0);
    
    void postConstruct(final Object p0, final String p1);
    
    void preDestroy(final Object p0);
    
    void preDestroy(final Object p0, final String p1);
    
     <U> U createAndInitialize(final Class<U> p0);
    
     <U> U createAndInitialize(final Class<U> p0, final String p1);
}
