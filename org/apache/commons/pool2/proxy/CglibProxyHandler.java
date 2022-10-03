package org.apache.commons.pool2.proxy;

import net.sf.cglib.proxy.MethodProxy;
import java.lang.reflect.Method;
import org.apache.commons.pool2.UsageTracking;
import net.sf.cglib.proxy.MethodInterceptor;

class CglibProxyHandler<T> extends BaseProxyHandler<T> implements MethodInterceptor
{
    CglibProxyHandler(final T pooledObject, final UsageTracking<T> usageTracking) {
        super(pooledObject, usageTracking);
    }
    
    public Object intercept(final Object object, final Method method, final Object[] args, final MethodProxy methodProxy) throws Throwable {
        return this.doInvoke(method, args);
    }
}
