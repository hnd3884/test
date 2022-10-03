package org.apache.catalina.mbeans;

import javax.management.Attribute;
import javax.management.ReflectionException;
import javax.management.MBeanException;
import javax.management.AttributeNotFoundException;
import org.apache.tomcat.util.IntrospectionUtils;
import javax.management.RuntimeOperationsException;
import org.apache.catalina.connector.Connector;

public class ConnectorMBean extends ClassNameMBean<Connector>
{
    public Object getAttribute(final String name) throws AttributeNotFoundException, MBeanException, ReflectionException {
        if (name == null) {
            throw new RuntimeOperationsException(new IllegalArgumentException("Attribute name is null"), "Attribute name is null");
        }
        final Connector connector = this.doGetManagedResource();
        return IntrospectionUtils.getProperty((Object)connector, name);
    }
    
    public void setAttribute(final Attribute attribute) throws AttributeNotFoundException, MBeanException, ReflectionException {
        if (attribute == null) {
            throw new RuntimeOperationsException(new IllegalArgumentException("Attribute is null"), "Attribute is null");
        }
        final String name = attribute.getName();
        final Object value = attribute.getValue();
        if (name == null) {
            throw new RuntimeOperationsException(new IllegalArgumentException("Attribute name is null"), "Attribute name is null");
        }
        final Connector connector = this.doGetManagedResource();
        IntrospectionUtils.setProperty((Object)connector, name, String.valueOf(value));
    }
}
