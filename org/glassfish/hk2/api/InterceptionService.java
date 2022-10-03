package org.glassfish.hk2.api;

import org.aopalliance.intercept.ConstructorInterceptor;
import java.lang.reflect.Constructor;
import org.aopalliance.intercept.MethodInterceptor;
import java.util.List;
import java.lang.reflect.Method;
import org.jvnet.hk2.annotations.Contract;

@Contract
public interface InterceptionService
{
    Filter getDescriptorFilter();
    
    List<MethodInterceptor> getMethodInterceptors(final Method p0);
    
    List<ConstructorInterceptor> getConstructorInterceptors(final Constructor<?> p0);
}
