package org.apache.commons.pool2.proxy;

import java.lang.reflect.Method;
import org.apache.commons.pool2.UsageTracking;
import java.lang.reflect.InvocationHandler;

class JdkProxyHandler<T> extends BaseProxyHandler<T> implements InvocationHandler
{
    JdkProxyHandler(final T pooledObject, final UsageTracking<T> usageTracking) {
        super(pooledObject, usageTracking);
    }
    
    @Override
    public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
        return this.doInvoke(method, args);
    }
}
