package org.apache.commons.beanutils;

import java.util.Set;
import java.util.HashMap;
import java.util.Map;
import java.beans.PropertyDescriptor;

class DefaultIntrospectionContext implements IntrospectionContext
{
    private static final PropertyDescriptor[] EMPTY_DESCRIPTORS;
    private final Class<?> currentClass;
    private final Map<String, PropertyDescriptor> descriptors;
    
    public DefaultIntrospectionContext(final Class<?> cls) {
        this.currentClass = cls;
        this.descriptors = new HashMap<String, PropertyDescriptor>();
    }
    
    @Override
    public Class<?> getTargetClass() {
        return this.currentClass;
    }
    
    @Override
    public void addPropertyDescriptor(final PropertyDescriptor desc) {
        if (desc == null) {
            throw new IllegalArgumentException("Property descriptor must not be null!");
        }
        this.descriptors.put(desc.getName(), desc);
    }
    
    @Override
    public void addPropertyDescriptors(final PropertyDescriptor[] descs) {
        if (descs == null) {
            throw new IllegalArgumentException("Array with descriptors must not be null!");
        }
        for (final PropertyDescriptor desc : descs) {
            this.addPropertyDescriptor(desc);
        }
    }
    
    @Override
    public boolean hasProperty(final String name) {
        return this.descriptors.containsKey(name);
    }
    
    @Override
    public PropertyDescriptor getPropertyDescriptor(final String name) {
        return this.descriptors.get(name);
    }
    
    @Override
    public void removePropertyDescriptor(final String name) {
        this.descriptors.remove(name);
    }
    
    @Override
    public Set<String> propertyNames() {
        return this.descriptors.keySet();
    }
    
    public PropertyDescriptor[] getPropertyDescriptors() {
        return this.descriptors.values().toArray(DefaultIntrospectionContext.EMPTY_DESCRIPTORS);
    }
    
    static {
        EMPTY_DESCRIPTORS = new PropertyDescriptor[0];
    }
}
