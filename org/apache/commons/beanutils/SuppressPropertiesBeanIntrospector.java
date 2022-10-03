package org.apache.commons.beanutils;

import java.beans.IntrospectionException;
import java.util.Iterator;
import java.util.Collections;
import java.util.HashSet;
import java.util.Collection;
import java.util.Set;

public class SuppressPropertiesBeanIntrospector implements BeanIntrospector
{
    public static final SuppressPropertiesBeanIntrospector SUPPRESS_CLASS;
    private final Set<String> propertyNames;
    
    public SuppressPropertiesBeanIntrospector(final Collection<String> propertiesToSuppress) {
        if (propertiesToSuppress == null) {
            throw new IllegalArgumentException("Property names must not be null!");
        }
        this.propertyNames = Collections.unmodifiableSet((Set<? extends String>)new HashSet<String>(propertiesToSuppress));
    }
    
    public Set<String> getSuppressedProperties() {
        return this.propertyNames;
    }
    
    @Override
    public void introspect(final IntrospectionContext icontext) throws IntrospectionException {
        for (final String property : this.getSuppressedProperties()) {
            icontext.removePropertyDescriptor(property);
        }
    }
    
    static {
        SUPPRESS_CLASS = new SuppressPropertiesBeanIntrospector(Collections.singleton("class"));
    }
}
