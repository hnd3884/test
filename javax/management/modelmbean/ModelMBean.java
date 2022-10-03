package javax.management.modelmbean;

import javax.management.InstanceNotFoundException;
import javax.management.RuntimeOperationsException;
import javax.management.MBeanException;
import javax.management.PersistentMBean;
import javax.management.DynamicMBean;

public interface ModelMBean extends DynamicMBean, PersistentMBean, ModelMBeanNotificationBroadcaster
{
    void setModelMBeanInfo(final ModelMBeanInfo p0) throws MBeanException, RuntimeOperationsException;
    
    void setManagedResource(final Object p0, final String p1) throws MBeanException, RuntimeOperationsException, InstanceNotFoundException, InvalidTargetObjectTypeException;
}
