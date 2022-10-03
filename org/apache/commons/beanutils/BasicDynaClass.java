package org.apache.commons.beanutils;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.lang.reflect.Constructor;
import java.io.Serializable;

public class BasicDynaClass implements DynaClass, Serializable
{
    protected transient Constructor<?> constructor;
    protected static Class<?>[] constructorTypes;
    protected Object[] constructorValues;
    protected Class<?> dynaBeanClass;
    protected String name;
    protected DynaProperty[] properties;
    protected HashMap<String, DynaProperty> propertiesMap;
    
    public BasicDynaClass() {
        this(null, null, null);
    }
    
    public BasicDynaClass(final String name, final Class<?> dynaBeanClass) {
        this(name, dynaBeanClass, null);
    }
    
    public BasicDynaClass(final String name, Class<?> dynaBeanClass, final DynaProperty[] properties) {
        this.constructor = null;
        this.constructorValues = new Object[] { this };
        this.dynaBeanClass = BasicDynaBean.class;
        this.name = this.getClass().getName();
        this.properties = new DynaProperty[0];
        this.propertiesMap = new HashMap<String, DynaProperty>();
        if (name != null) {
            this.name = name;
        }
        if (dynaBeanClass == null) {
            dynaBeanClass = BasicDynaBean.class;
        }
        this.setDynaBeanClass(dynaBeanClass);
        if (properties != null) {
            this.setProperties(properties);
        }
    }
    
    @Override
    public String getName() {
        return this.name;
    }
    
    @Override
    public DynaProperty getDynaProperty(final String name) {
        if (name == null) {
            throw new IllegalArgumentException("No property name specified");
        }
        return this.propertiesMap.get(name);
    }
    
    @Override
    public DynaProperty[] getDynaProperties() {
        return this.properties;
    }
    
    @Override
    public DynaBean newInstance() throws IllegalAccessException, InstantiationException {
        try {
            if (this.constructor == null) {
                this.setDynaBeanClass(this.dynaBeanClass);
            }
            return (DynaBean)this.constructor.newInstance(this.constructorValues);
        }
        catch (final InvocationTargetException e) {
            throw new InstantiationException(e.getTargetException().getMessage());
        }
    }
    
    public Class<?> getDynaBeanClass() {
        return this.dynaBeanClass;
    }
    
    protected void setDynaBeanClass(final Class<?> dynaBeanClass) {
        if (dynaBeanClass.isInterface()) {
            throw new IllegalArgumentException("Class " + dynaBeanClass.getName() + " is an interface, not a class");
        }
        if (!DynaBean.class.isAssignableFrom(dynaBeanClass)) {
            throw new IllegalArgumentException("Class " + dynaBeanClass.getName() + " does not implement DynaBean");
        }
        try {
            this.constructor = dynaBeanClass.getConstructor(BasicDynaClass.constructorTypes);
        }
        catch (final NoSuchMethodException e) {
            throw new IllegalArgumentException("Class " + dynaBeanClass.getName() + " does not have an appropriate constructor");
        }
        this.dynaBeanClass = dynaBeanClass;
    }
    
    protected void setProperties(final DynaProperty[] properties) {
        this.properties = properties;
        this.propertiesMap.clear();
        for (final DynaProperty propertie : properties) {
            this.propertiesMap.put(propertie.getName(), propertie);
        }
    }
    
    static {
        BasicDynaClass.constructorTypes = new Class[] { DynaClass.class };
    }
}
