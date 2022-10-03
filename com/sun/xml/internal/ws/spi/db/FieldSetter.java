package com.sun.xml.internal.ws.spi.db;

import java.security.PrivilegedActionException;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;
import java.lang.reflect.Field;

public class FieldSetter extends PropertySetterBase
{
    protected Field field;
    
    public FieldSetter(final Field f) {
        this.field = f;
        this.type = f.getType();
    }
    
    public Field getField() {
        return this.field;
    }
    
    @Override
    public void set(final Object instance, final Object resource) {
        if (this.field.isAccessible()) {
            try {
                this.field.set(instance, resource);
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
                        if (!FieldSetter.this.field.isAccessible()) {
                            FieldSetter.this.field.setAccessible(true);
                        }
                        FieldSetter.this.field.set(instance, resource);
                        return null;
                    }
                });
            }
            catch (final PrivilegedActionException e2) {
                e2.printStackTrace();
            }
        }
    }
    
    @Override
    public <A> A getAnnotation(final Class<A> annotationType) {
        final Class c = annotationType;
        return this.field.getAnnotation((Class<A>)c);
    }
}
