package org.jvnet.hk2.internal;

import org.glassfish.hk2.api.AOPProxyCtl;
import java.security.AccessController;
import java.lang.reflect.InvocationTargetException;
import org.glassfish.hk2.utilities.reflection.Logger;
import javassist.util.proxy.MethodHandler;
import java.security.PrivilegedExceptionAction;
import javassist.util.proxy.ProxyFactory;
import java.lang.reflect.Constructor;
import org.aopalliance.intercept.MethodInterceptor;
import java.util.List;
import java.lang.reflect.Method;
import java.util.Map;
import javassist.util.proxy.MethodFilter;

final class ConstructorActionImpl<T> implements ConstructorAction
{
    private static final Class<?>[] ADDED_INTERFACES;
    private static final MethodFilter METHOD_FILTER;
    private final ClazzCreator<T> clazzCreator;
    private final Map<Method, List<MethodInterceptor>> methodInterceptors;
    
    ConstructorActionImpl(final ClazzCreator<T> clazzCreator, final Map<Method, List<MethodInterceptor>> methodInterceptors) {
        this.clazzCreator = clazzCreator;
        this.methodInterceptors = methodInterceptors;
    }
    
    @Override
    public Object makeMe(final Constructor<?> c, final Object[] args, final boolean neutralCCL) throws Throwable {
        final MethodInterceptorHandler methodInterceptor = new MethodInterceptorHandler(this.clazzCreator.getServiceLocator(), this.clazzCreator.getUnderlyingDescriptor(), this.methodInterceptors);
        final ProxyFactory proxyFactory = new ProxyFactory();
        proxyFactory.setSuperclass((Class)this.clazzCreator.getImplClass());
        proxyFactory.setFilter(ConstructorActionImpl.METHOD_FILTER);
        proxyFactory.setInterfaces((Class[])ConstructorActionImpl.ADDED_INTERFACES);
        return AccessController.doPrivileged((PrivilegedExceptionAction<Object>)new PrivilegedExceptionAction<Object>() {
            @Override
            public Object run() throws Exception {
                ClassLoader currentCCL = null;
                if (neutralCCL) {
                    currentCCL = Thread.currentThread().getContextClassLoader();
                }
                try {
                    return proxyFactory.create((Class[])c.getParameterTypes(), args, (MethodHandler)methodInterceptor);
                }
                catch (final InvocationTargetException ite) {
                    final Throwable targetException = ite.getTargetException();
                    Logger.getLogger().debug(c.getDeclaringClass().getName(), c.getName(), targetException);
                    if (targetException instanceof Exception) {
                        throw (Exception)targetException;
                    }
                    throw new RuntimeException(targetException);
                }
                finally {
                    if (neutralCCL) {
                        Thread.currentThread().setContextClassLoader(currentCCL);
                    }
                }
            }
        });
    }
    
    static {
        ADDED_INTERFACES = new Class[] { AOPProxyCtl.class };
        METHOD_FILTER = (MethodFilter)new MethodFilter() {
            public boolean isHandled(final Method method) {
                return !method.getName().equals("finalize");
            }
        };
    }
}
