package com.sun.jmx.mbeanserver;

import javax.management.MalformedObjectNameException;
import javax.management.RuntimeOperationsException;
import javax.management.DynamicMBean;
import javax.management.ObjectName;

public class NamedObject
{
    private final ObjectName name;
    private final DynamicMBean object;
    
    public NamedObject(final ObjectName name, final DynamicMBean object) {
        if (name.isPattern()) {
            throw new RuntimeOperationsException(new IllegalArgumentException("Invalid name->" + name.toString()));
        }
        this.name = name;
        this.object = object;
    }
    
    public NamedObject(final String s, final DynamicMBean object) throws MalformedObjectNameException {
        final ObjectName name = new ObjectName(s);
        if (name.isPattern()) {
            throw new RuntimeOperationsException(new IllegalArgumentException("Invalid name->" + name.toString()));
        }
        this.name = name;
        this.object = object;
    }
    
    @Override
    public boolean equals(final Object o) {
        return this == o || (o != null && o instanceof NamedObject && this.name.equals(((NamedObject)o).getName()));
    }
    
    @Override
    public int hashCode() {
        return this.name.hashCode();
    }
    
    public ObjectName getName() {
        return this.name;
    }
    
    public DynamicMBean getObject() {
        return this.object;
    }
}
