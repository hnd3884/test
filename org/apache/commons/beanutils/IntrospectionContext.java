package org.apache.commons.beanutils;

import java.util.Set;
import java.beans.PropertyDescriptor;

public interface IntrospectionContext
{
    Class<?> getTargetClass();
    
    void addPropertyDescriptor(final PropertyDescriptor p0);
    
    void addPropertyDescriptors(final PropertyDescriptor[] p0);
    
    boolean hasProperty(final String p0);
    
    PropertyDescriptor getPropertyDescriptor(final String p0);
    
    void removePropertyDescriptor(final String p0);
    
    Set<String> propertyNames();
}
