package org.glassfish.jersey.servlet.internal;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationHandler;

public class ThreadLocalInvoker<T> implements InvocationHandler
{
    private ThreadLocal<T> threadLocalInstance;
    
    public ThreadLocalInvoker() {
        this.threadLocalInstance = new ThreadLocal<T>();
    }
    
    public void set(final T threadLocalInstance) {
        this.threadLocalInstance.set(threadLocalInstance);
    }
    
    public T get() {
        return this.threadLocalInstance.get();
    }
    
    @Override
    public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
        if (this.threadLocalInstance.get() == null) {
            throw new IllegalStateException(LocalizationMessages.PERSISTENCE_UNIT_NOT_CONFIGURED(proxy.getClass()));
        }
        try {
            return method.invoke(this.threadLocalInstance.get(), args);
        }
        catch (final IllegalAccessException ex) {
            throw new IllegalStateException(ex);
        }
        catch (final InvocationTargetException ex2) {
            throw ex2.getTargetException();
        }
    }
}
