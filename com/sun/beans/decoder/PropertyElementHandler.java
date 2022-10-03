package com.sun.beans.decoder;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.beans.IndexedPropertyDescriptor;
import com.sun.beans.finder.MethodFinder;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import java.beans.IntrospectionException;
import java.lang.reflect.Array;
import sun.reflect.misc.MethodUtil;

final class PropertyElementHandler extends AccessorElementHandler
{
    static final String GETTER = "get";
    static final String SETTER = "set";
    private Integer index;
    
    @Override
    public void addAttribute(final String s, final String s2) {
        if (s.equals("index")) {
            this.index = Integer.valueOf(s2);
        }
        else {
            super.addAttribute(s, s2);
        }
    }
    
    @Override
    protected boolean isArgument() {
        return false;
    }
    
    @Override
    protected Object getValue(final String s) {
        try {
            return getPropertyValue(this.getContextBean(), s, this.index);
        }
        catch (final Exception ex) {
            this.getOwner().handleException(ex);
            return null;
        }
    }
    
    @Override
    protected void setValue(final String s, final Object o) {
        try {
            setPropertyValue(this.getContextBean(), s, this.index, o);
        }
        catch (final Exception ex) {
            this.getOwner().handleException(ex);
        }
    }
    
    private static Object getPropertyValue(final Object o, final String s, final Integer n) throws IllegalAccessException, IntrospectionException, InvocationTargetException, NoSuchMethodException {
        final Class<?> class1 = o.getClass();
        if (n == null) {
            return MethodUtil.invoke(findGetter(class1, s, (Class<?>[])new Class[0]), o, new Object[0]);
        }
        if (class1.isArray() && s == null) {
            return Array.get(o, n);
        }
        return MethodUtil.invoke(findGetter(class1, s, Integer.TYPE), o, new Object[] { n });
    }
    
    private static void setPropertyValue(final Object o, final String s, final Integer n, final Object o2) throws IllegalAccessException, IntrospectionException, InvocationTargetException, NoSuchMethodException {
        final Class<?> class1 = o.getClass();
        final Class<?> clazz = (o2 != null) ? o2.getClass() : null;
        if (n == null) {
            MethodUtil.invoke(findSetter(class1, s, clazz), o, new Object[] { o2 });
        }
        else if (class1.isArray() && s == null) {
            Array.set(o, n, o2);
        }
        else {
            MethodUtil.invoke(findSetter(class1, s, Integer.TYPE, clazz), o, new Object[] { n, o2 });
        }
    }
    
    private static Method findGetter(final Class<?> clazz, final String s, final Class<?>... array) throws IntrospectionException, NoSuchMethodException {
        if (s == null) {
            return MethodFinder.findInstanceMethod(clazz, "get", array);
        }
        final PropertyDescriptor property = getProperty(clazz, s);
        if (array.length == 0) {
            final Method readMethod = property.getReadMethod();
            if (readMethod != null) {
                return readMethod;
            }
        }
        else if (property instanceof IndexedPropertyDescriptor) {
            final Method indexedReadMethod = ((IndexedPropertyDescriptor)property).getIndexedReadMethod();
            if (indexedReadMethod != null) {
                return indexedReadMethod;
            }
        }
        throw new IntrospectionException("Could not find getter for the " + s + " property");
    }
    
    private static Method findSetter(final Class<?> clazz, final String s, final Class<?>... array) throws IntrospectionException, NoSuchMethodException {
        if (s == null) {
            return MethodFinder.findInstanceMethod(clazz, "set", array);
        }
        final PropertyDescriptor property = getProperty(clazz, s);
        if (array.length == 1) {
            final Method writeMethod = property.getWriteMethod();
            if (writeMethod != null) {
                return writeMethod;
            }
        }
        else if (property instanceof IndexedPropertyDescriptor) {
            final Method indexedWriteMethod = ((IndexedPropertyDescriptor)property).getIndexedWriteMethod();
            if (indexedWriteMethod != null) {
                return indexedWriteMethod;
            }
        }
        throw new IntrospectionException("Could not find setter for the " + s + " property");
    }
    
    private static PropertyDescriptor getProperty(final Class<?> clazz, final String s) throws IntrospectionException {
        for (final PropertyDescriptor propertyDescriptor : Introspector.getBeanInfo(clazz).getPropertyDescriptors()) {
            if (s.equals(propertyDescriptor.getName())) {
                return propertyDescriptor;
            }
        }
        throw new IntrospectionException("Could not find the " + s + " property descriptor");
    }
}
