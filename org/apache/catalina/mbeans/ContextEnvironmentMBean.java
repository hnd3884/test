package org.apache.catalina.mbeans;

import javax.management.ReflectionException;
import javax.management.MBeanException;
import javax.management.AttributeNotFoundException;
import org.apache.tomcat.util.descriptor.web.NamingResources;
import javax.management.Attribute;
import org.apache.tomcat.util.descriptor.web.ContextEnvironment;

public class ContextEnvironmentMBean extends BaseCatalinaMBean<ContextEnvironment>
{
    public void setAttribute(final Attribute attribute) throws AttributeNotFoundException, MBeanException, ReflectionException {
        super.setAttribute(attribute);
        final ContextEnvironment ce = this.doGetManagedResource();
        final NamingResources nr = ce.getNamingResources();
        nr.removeEnvironment(ce.getName());
        nr.addEnvironment(ce);
    }
}
