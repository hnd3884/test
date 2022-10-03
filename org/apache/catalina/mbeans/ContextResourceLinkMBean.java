package org.apache.catalina.mbeans;

import org.apache.tomcat.util.descriptor.web.NamingResources;
import javax.management.Attribute;
import javax.management.ReflectionException;
import javax.management.MBeanException;
import javax.management.AttributeNotFoundException;
import javax.management.RuntimeOperationsException;
import org.apache.tomcat.util.descriptor.web.ContextResourceLink;

public class ContextResourceLinkMBean extends BaseCatalinaMBean<ContextResourceLink>
{
    public Object getAttribute(final String name) throws AttributeNotFoundException, MBeanException, ReflectionException {
        if (name == null) {
            throw new RuntimeOperationsException(new IllegalArgumentException("Attribute name is null"), "Attribute name is null");
        }
        final ContextResourceLink cl = this.doGetManagedResource();
        String value = null;
        if ("global".equals(name)) {
            return cl.getGlobal();
        }
        if ("description".equals(name)) {
            return cl.getDescription();
        }
        if ("name".equals(name)) {
            return cl.getName();
        }
        if ("type".equals(name)) {
            return cl.getType();
        }
        value = (String)cl.getProperty(name);
        if (value == null) {
            throw new AttributeNotFoundException("Cannot find attribute [" + name + "]");
        }
        return value;
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
        final ContextResourceLink crl = this.doGetManagedResource();
        if ("global".equals(name)) {
            crl.setGlobal((String)value);
        }
        else if ("description".equals(name)) {
            crl.setDescription((String)value);
        }
        else if ("name".equals(name)) {
            crl.setName((String)value);
        }
        else if ("type".equals(name)) {
            crl.setType((String)value);
        }
        else {
            crl.setProperty(name, (Object)("" + value));
        }
        final NamingResources nr = crl.getNamingResources();
        nr.removeResourceLink(crl.getName());
        nr.addResourceLink(crl);
    }
}
