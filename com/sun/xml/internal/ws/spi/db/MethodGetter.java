package com.sun.xml.internal.ws.spi.db;

import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.security.AccessController;
import java.lang.reflect.Method;

public class MethodGetter extends PropertyGetterBase
{
    private Method method;
    
    public MethodGetter(final Method m) {
        this.method = m;
        this.type = m.getReturnType();
    }
    
    public Method getMethod() {
        return this.method;
    }
    
    @Override
    public <A> A getAnnotation(final Class<A> annotationType) {
        final Class c = annotationType;
        return this.method.getAnnotation((Class<A>)c);
    }
    
    @Override
    public Object get(final Object instance) {
        final Object[] args = new Object[0];
        try {
            if (this.method.isAccessible()) {
                return this.method.invoke(instance, args);
            }
            final PrivilegedGetter privilegedGetter = new PrivilegedGetter(this.method, instance);
            try {
                AccessController.doPrivileged((PrivilegedExceptionAction<Object>)privilegedGetter);
            }
            catch (final PrivilegedActionException e) {
                e.printStackTrace();
            }
            return privilegedGetter.value;
        }
        catch (final Exception e2) {
            e2.printStackTrace();
            return null;
        }
    }
    
    static class PrivilegedGetter implements PrivilegedExceptionAction
    {
        private Object value;
        private Method method;
        private Object instance;
        
        public PrivilegedGetter(final Method m, final Object instance) {
            this.method = m;
            this.instance = instance;
        }
        
        @Override
        public Object run() throws IllegalAccessException {
            if (!this.method.isAccessible()) {
                this.method.setAccessible(true);
            }
            try {
                this.value = this.method.invoke(this.instance, new Object[0]);
            }
            catch (final Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
