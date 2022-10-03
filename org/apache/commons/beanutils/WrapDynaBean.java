package org.apache.commons.beanutils;

import java.lang.reflect.InvocationTargetException;
import java.io.Serializable;

public class WrapDynaBean implements DynaBean, Serializable
{
    protected transient WrapDynaClass dynaClass;
    protected Object instance;
    
    public WrapDynaBean(final Object instance) {
        this(instance, null);
    }
    
    public WrapDynaBean(final Object instance, final WrapDynaClass cls) {
        this.dynaClass = null;
        this.instance = null;
        this.instance = instance;
        this.dynaClass = (WrapDynaClass)((cls != null) ? cls : this.getDynaClass());
    }
    
    @Override
    public boolean contains(final String name, final String key) {
        throw new UnsupportedOperationException("WrapDynaBean does not support contains()");
    }
    
    @Override
    public Object get(final String name) {
        Object value = null;
        try {
            value = this.getPropertyUtils().getSimpleProperty(this.instance, name);
        }
        catch (final InvocationTargetException ite) {
            final Throwable cause = ite.getTargetException();
            throw new IllegalArgumentException("Error reading property '" + name + "' nested exception - " + cause);
        }
        catch (final Throwable t) {
            throw new IllegalArgumentException("Error reading property '" + name + "', exception - " + t);
        }
        return value;
    }
    
    @Override
    public Object get(final String name, final int index) {
        Object value = null;
        try {
            value = this.getPropertyUtils().getIndexedProperty(this.instance, name, index);
        }
        catch (final IndexOutOfBoundsException e) {
            throw e;
        }
        catch (final InvocationTargetException ite) {
            final Throwable cause = ite.getTargetException();
            throw new IllegalArgumentException("Error reading indexed property '" + name + "' nested exception - " + cause);
        }
        catch (final Throwable t) {
            throw new IllegalArgumentException("Error reading indexed property '" + name + "', exception - " + t);
        }
        return value;
    }
    
    @Override
    public Object get(final String name, final String key) {
        Object value = null;
        try {
            value = this.getPropertyUtils().getMappedProperty(this.instance, name, key);
        }
        catch (final InvocationTargetException ite) {
            final Throwable cause = ite.getTargetException();
            throw new IllegalArgumentException("Error reading mapped property '" + name + "' nested exception - " + cause);
        }
        catch (final Throwable t) {
            throw new IllegalArgumentException("Error reading mapped property '" + name + "', exception - " + t);
        }
        return value;
    }
    
    @Override
    public DynaClass getDynaClass() {
        if (this.dynaClass == null) {
            this.dynaClass = WrapDynaClass.createDynaClass(this.instance.getClass());
        }
        return this.dynaClass;
    }
    
    @Override
    public void remove(final String name, final String key) {
        throw new UnsupportedOperationException("WrapDynaBean does not support remove()");
    }
    
    @Override
    public void set(final String name, final Object value) {
        try {
            this.getPropertyUtils().setSimpleProperty(this.instance, name, value);
        }
        catch (final InvocationTargetException ite) {
            final Throwable cause = ite.getTargetException();
            throw new IllegalArgumentException("Error setting property '" + name + "' nested exception -" + cause);
        }
        catch (final Throwable t) {
            throw new IllegalArgumentException("Error setting property '" + name + "', exception - " + t);
        }
    }
    
    @Override
    public void set(final String name, final int index, final Object value) {
        try {
            this.getPropertyUtils().setIndexedProperty(this.instance, name, index, value);
        }
        catch (final IndexOutOfBoundsException e) {
            throw e;
        }
        catch (final InvocationTargetException ite) {
            final Throwable cause = ite.getTargetException();
            throw new IllegalArgumentException("Error setting indexed property '" + name + "' nested exception - " + cause);
        }
        catch (final Throwable t) {
            throw new IllegalArgumentException("Error setting indexed property '" + name + "', exception - " + t);
        }
    }
    
    @Override
    public void set(final String name, final String key, final Object value) {
        try {
            this.getPropertyUtils().setMappedProperty(this.instance, name, key, value);
        }
        catch (final InvocationTargetException ite) {
            final Throwable cause = ite.getTargetException();
            throw new IllegalArgumentException("Error setting mapped property '" + name + "' nested exception - " + cause);
        }
        catch (final Throwable t) {
            throw new IllegalArgumentException("Error setting mapped property '" + name + "', exception - " + t);
        }
    }
    
    public Object getInstance() {
        return this.instance;
    }
    
    protected DynaProperty getDynaProperty(final String name) {
        final DynaProperty descriptor = this.getDynaClass().getDynaProperty(name);
        if (descriptor == null) {
            throw new IllegalArgumentException("Invalid property name '" + name + "'");
        }
        return descriptor;
    }
    
    private PropertyUtilsBean getPropertyUtils() {
        PropertyUtilsBean propUtils = null;
        if (this.dynaClass != null) {
            propUtils = this.dynaClass.getPropertyUtilsBean();
        }
        return (propUtils != null) ? propUtils : PropertyUtilsBean.getInstance();
    }
}
