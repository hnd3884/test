package com.sun.xml.internal.ws.spi.db;

import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.security.AccessController;
import java.lang.reflect.Field;

public class FieldGetter extends PropertyGetterBase
{
    protected Field field;
    
    public FieldGetter(final Field f) {
        this.field = f;
        this.type = f.getType();
    }
    
    public Field getField() {
        return this.field;
    }
    
    @Override
    public Object get(final Object instance) {
        if (this.field.isAccessible()) {
            try {
                return this.field.get(instance);
            }
            catch (final Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        final PrivilegedGetter privilegedGetter = new PrivilegedGetter(this.field, instance);
        try {
            AccessController.doPrivileged((PrivilegedExceptionAction<Object>)privilegedGetter);
        }
        catch (final PrivilegedActionException e2) {
            e2.printStackTrace();
        }
        return privilegedGetter.value;
    }
    
    @Override
    public <A> A getAnnotation(final Class<A> annotationType) {
        final Class c = annotationType;
        return this.field.getAnnotation((Class<A>)c);
    }
    
    static class PrivilegedGetter implements PrivilegedExceptionAction
    {
        private Object value;
        private Field field;
        private Object instance;
        
        public PrivilegedGetter(final Field field, final Object instance) {
            this.field = field;
            this.instance = instance;
        }
        
        @Override
        public Object run() throws IllegalAccessException {
            if (!this.field.isAccessible()) {
                this.field.setAccessible(true);
            }
            this.value = this.field.get(this.instance);
            return null;
        }
    }
}
