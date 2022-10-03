package org.apache.lucene.util;

import java.security.AccessController;
import java.lang.reflect.Field;
import java.security.PrivilegedAction;
import java.lang.reflect.Modifier;

public abstract class AttributeImpl implements Cloneable, Attribute
{
    public abstract void clear();
    
    public final String reflectAsString(final boolean prependAttClass) {
        final StringBuilder buffer = new StringBuilder();
        this.reflectWith(new AttributeReflector() {
            @Override
            public void reflect(final Class<? extends Attribute> attClass, final String key, final Object value) {
                if (buffer.length() > 0) {
                    buffer.append(',');
                }
                if (prependAttClass) {
                    buffer.append(attClass.getName()).append('#');
                }
                buffer.append(key).append('=').append((value == null) ? "null" : value);
            }
        });
        return buffer.toString();
    }
    
    public void reflectWith(final AttributeReflector reflector) {
        final Class<? extends AttributeImpl> clazz = this.getClass();
        final Class<? extends Attribute>[] interfaces = AttributeSource.getAttributeInterfaces(clazz);
        if (interfaces.length != 1) {
            throw new UnsupportedOperationException(clazz.getName() + " implements more than one Attribute interface, the default reflectWith() implementation cannot handle this.");
        }
        final Class<? extends Attribute> interf = interfaces[0];
        final Field[] arr$;
        final Field[] fields = arr$ = clazz.getDeclaredFields();
        for (final Field f : arr$) {
            if (!Modifier.isStatic(f.getModifiers())) {
                reflector.reflect(interf, f.getName(), AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Object>() {
                    @SuppressForbidden(reason = "This methods needs to access private attribute fields. Method will be abstract in 6.x")
                    @Override
                    public Object run() {
                        try {
                            f.setAccessible(true);
                            return f.get(AttributeImpl.this);
                        }
                        catch (final IllegalAccessException e) {
                            throw new RuntimeException("Cannot access private fields.", e);
                        }
                    }
                }));
            }
        }
    }
    
    public abstract void copyTo(final AttributeImpl p0);
    
    public AttributeImpl clone() {
        AttributeImpl clone = null;
        try {
            clone = (AttributeImpl)super.clone();
        }
        catch (final CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
        return clone;
    }
}
