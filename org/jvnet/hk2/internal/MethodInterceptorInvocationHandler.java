package org.jvnet.hk2.internal;

import java.lang.reflect.Method;
import javassist.util.proxy.MethodHandler;
import java.lang.reflect.InvocationHandler;

public class MethodInterceptorInvocationHandler implements InvocationHandler
{
    private final MethodHandler interceptor;
    
    public MethodInterceptorInvocationHandler(final MethodHandler interceptor) {
        this.interceptor = interceptor;
    }
    
    @Override
    public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
        return this.interceptor.invoke(proxy, method, (Method)null, args);
    }
}
