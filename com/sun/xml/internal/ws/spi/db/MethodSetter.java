package com.sun.xml.internal.ws.spi.db;

import java.security.PrivilegedActionException;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;
import java.lang.reflect.Method;

public class MethodSetter extends PropertySetterBase
{
    private Method method;
    
    public MethodSetter(final Method m) {
        this.method = m;
        this.type = m.getParameterTypes()[0];
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
    public void set(final Object instance, final Object resource) {
        final Object[] args = { resource };
        if (this.method.isAccessible()) {
            try {
                this.method.invoke(instance, args);
            }
            catch (final Exception e) {
                e.printStackTrace();
            }
        }
        else {
            try {
                AccessController.doPrivileged((PrivilegedExceptionAction<Object>)new PrivilegedExceptionAction<Object>() {
                    @Override
                    public Object run() throws IllegalAccessException {
                        if (!MethodSetter.this.method.isAccessible()) {
                            MethodSetter.this.method.setAccessible(true);
                        }
                        try {
                            MethodSetter.this.method.invoke(instance, args);
                        }
                        catch (final Exception e) {
                            e.printStackTrace();
                        }
                        return null;
                    }
                });
            }
            catch (final PrivilegedActionException e2) {
                e2.printStackTrace();
            }
        }
    }
}
