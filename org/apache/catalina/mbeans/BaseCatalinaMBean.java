package org.apache.catalina.mbeans;

import javax.management.modelmbean.InvalidTargetObjectTypeException;
import javax.management.RuntimeOperationsException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import org.apache.tomcat.util.modeler.BaseModelMBean;

public abstract class BaseCatalinaMBean<T> extends BaseModelMBean
{
    protected T doGetManagedResource() throws MBeanException {
        try {
            final T resource = (T)this.getManagedResource();
            return resource;
        }
        catch (final InstanceNotFoundException | RuntimeOperationsException | InvalidTargetObjectTypeException e) {
            throw new MBeanException(e);
        }
    }
    
    protected static Object newInstance(final String type) throws MBeanException {
        try {
            return Class.forName(type).newInstance();
        }
        catch (final InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            throw new MBeanException(e);
        }
    }
}
