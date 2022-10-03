package org.jvnet.hk2.internal;

import java.lang.reflect.AccessibleObject;
import org.glassfish.hk2.api.HK2Invocation;
import java.security.AccessController;
import java.security.PrivilegedAction;
import org.aopalliance.intercept.MethodInvocation;
import java.util.HashMap;
import org.glassfish.hk2.utilities.reflection.Logger;
import java.util.Collection;
import java.util.ArrayList;
import java.util.RandomAccess;
import org.glassfish.hk2.utilities.reflection.ReflectionHelper;
import org.glassfish.hk2.api.ActiveDescriptor;
import org.aopalliance.intercept.MethodInterceptor;
import java.util.List;
import java.lang.reflect.Method;
import java.util.Map;
import javassist.util.proxy.MethodHandler;

public class MethodInterceptorHandler implements MethodHandler
{
    private static final boolean DEBUG_INTERCEPTION;
    private final ServiceLocatorImpl locator;
    private final Map<Method, List<MethodInterceptor>> interceptorLists;
    private final ActiveDescriptor<?> underlyingDescriptor;
    
    MethodInterceptorHandler(final ServiceLocatorImpl locator, final ActiveDescriptor<?> underlyingDescriptor, final Map<Method, List<MethodInterceptor>> interceptorLists) {
        this.locator = locator;
        this.interceptorLists = interceptorLists;
        this.underlyingDescriptor = underlyingDescriptor;
    }
    
    public Object invoke(final Object self, final Method thisMethod, final Method proceed, final Object[] args) throws Throwable {
        if (thisMethod.getName().equals("__getUnderlyingDescriptor")) {
            return this.underlyingDescriptor;
        }
        List<MethodInterceptor> interceptors = this.interceptorLists.get(thisMethod);
        if (interceptors == null || interceptors.isEmpty()) {
            return ReflectionHelper.invoke(self, proceed, args, this.locator.getNeutralContextClassLoader());
        }
        if (!(interceptors instanceof RandomAccess)) {
            interceptors = new ArrayList<MethodInterceptor>(interceptors);
        }
        final MethodInterceptor nextInterceptor = interceptors.get(0);
        long aggregateInterceptionTime = 0L;
        if (MethodInterceptorHandler.DEBUG_INTERCEPTION) {
            aggregateInterceptionTime = System.currentTimeMillis();
            Logger.getLogger().debug("Invoking interceptor " + nextInterceptor.getClass().getName() + " index 0 in stack of " + interceptors.size() + " of method " + thisMethod);
        }
        try {
            return nextInterceptor.invoke((MethodInvocation)new MethodInvocationImpl(args, thisMethod, self, (List)interceptors, 0, proceed, (HashMap)null));
        }
        finally {
            if (MethodInterceptorHandler.DEBUG_INTERCEPTION) {
                aggregateInterceptionTime = System.currentTimeMillis() - aggregateInterceptionTime;
                Logger.getLogger().debug("Interceptor " + nextInterceptor.getClass().getName() + " index 0 took an aggregate of " + aggregateInterceptionTime + " milliseconds");
            }
        }
    }
    
    static {
        DEBUG_INTERCEPTION = AccessController.doPrivileged((PrivilegedAction<Boolean>)new PrivilegedAction<Boolean>() {
            @Override
            public Boolean run() {
                return Boolean.parseBoolean(System.getProperty("org.jvnet.hk2.properties.tracing.interceptors", "false"));
            }
        });
    }
    
    private class MethodInvocationImpl implements MethodInvocation, HK2Invocation
    {
        private final Object[] arguments;
        private final Method method;
        private final Object myself;
        private final List<MethodInterceptor> interceptors;
        private final int index;
        private final Method proceed;
        private HashMap<String, Object> userData;
        
        private MethodInvocationImpl(final Object[] arguments, final Method method, final Object myself, final List<MethodInterceptor> interceptors, final int index, final Method proceed, final HashMap<String, Object> userData) {
            this.arguments = arguments;
            this.method = method;
            this.myself = myself;
            this.interceptors = interceptors;
            this.index = index;
            this.proceed = proceed;
            this.userData = userData;
        }
        
        public Object[] getArguments() {
            return this.arguments;
        }
        
        public AccessibleObject getStaticPart() {
            return this.method;
        }
        
        public Object getThis() {
            return this.myself;
        }
        
        public Method getMethod() {
            return this.method;
        }
        
        public Object proceed() throws Throwable {
            final int newIndex = this.index + 1;
            if (newIndex >= this.interceptors.size()) {
                long methodTime = 0L;
                if (MethodInterceptorHandler.DEBUG_INTERCEPTION) {
                    methodTime = System.currentTimeMillis();
                }
                try {
                    return ReflectionHelper.invoke(this.myself, this.proceed, this.arguments, MethodInterceptorHandler.this.locator.getNeutralContextClassLoader());
                }
                finally {
                    if (MethodInterceptorHandler.DEBUG_INTERCEPTION) {
                        methodTime = System.currentTimeMillis() - methodTime;
                        Logger.getLogger().debug("Time to call actual intercepted method " + this.method + " is " + methodTime + " milliseconds");
                    }
                }
            }
            final MethodInterceptor nextInterceptor = this.interceptors.get(newIndex);
            long aggregateInterceptionTime = 0L;
            if (MethodInterceptorHandler.DEBUG_INTERCEPTION) {
                aggregateInterceptionTime = System.currentTimeMillis();
                Logger.getLogger().debug("Invoking interceptor " + nextInterceptor.getClass().getName() + " index " + newIndex + " in stack of " + this.interceptors.size() + " of method " + this.method);
            }
            try {
                return nextInterceptor.invoke((MethodInvocation)new MethodInvocationImpl(this.arguments, this.method, this.myself, this.interceptors, newIndex, this.proceed, this.userData));
            }
            finally {
                if (MethodInterceptorHandler.DEBUG_INTERCEPTION) {
                    aggregateInterceptionTime = System.currentTimeMillis() - aggregateInterceptionTime;
                    Logger.getLogger().debug("Interceptor " + nextInterceptor.getClass().getName() + " index " + newIndex + " took an aggregate of " + aggregateInterceptionTime + " milliseconds");
                }
            }
        }
        
        public void setUserData(final String key, final Object data) {
            if (key == null) {
                throw new IllegalArgumentException();
            }
            if (this.userData == null) {
                this.userData = new HashMap<String, Object>();
            }
            if (data == null) {
                this.userData.remove(key);
            }
            else {
                this.userData.put(key, data);
            }
        }
        
        public Object getUserData(final String key) {
            if (key == null) {
                throw new IllegalArgumentException();
            }
            if (this.userData == null) {
                return null;
            }
            return this.userData.get(key);
        }
    }
}
