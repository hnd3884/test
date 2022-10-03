package org.apache.commons.pool2.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import org.apache.commons.pool2.UsageTracking;

public class JdkProxySource<T> implements ProxySource<T>
{
    private final ClassLoader classLoader;
    private final Class<?>[] interfaces;
    
    public JdkProxySource(final ClassLoader classLoader, final Class<?>[] interfaces) {
        this.classLoader = classLoader;
        System.arraycopy(interfaces, 0, this.interfaces = new Class[interfaces.length], 0, interfaces.length);
    }
    
    @Override
    public T createProxy(final T pooledObject, final UsageTracking<T> usageTracking) {
        final T proxy = (T)Proxy.newProxyInstance(this.classLoader, this.interfaces, new JdkProxyHandler<Object>(pooledObject, usageTracking));
        return proxy;
    }
    
    @Override
    public T resolveProxy(final T proxy) {
        final JdkProxyHandler<T> jdkProxyHandler = (JdkProxyHandler<T>)Proxy.getInvocationHandler(proxy);
        final T pooledObject = jdkProxyHandler.disableProxy();
        return pooledObject;
    }
}
