package javax.management.modelmbean;

import javax.management.MBeanOperationInfo;
import javax.management.MBeanNotificationInfo;
import javax.management.MBeanConstructorInfo;
import javax.management.MBeanAttributeInfo;
import javax.management.RuntimeOperationsException;
import javax.management.MBeanException;
import javax.management.Descriptor;

public interface ModelMBeanInfo
{
    Descriptor[] getDescriptors(final String p0) throws MBeanException, RuntimeOperationsException;
    
    void setDescriptors(final Descriptor[] p0) throws MBeanException, RuntimeOperationsException;
    
    Descriptor getDescriptor(final String p0, final String p1) throws MBeanException, RuntimeOperationsException;
    
    void setDescriptor(final Descriptor p0, final String p1) throws MBeanException, RuntimeOperationsException;
    
    Descriptor getMBeanDescriptor() throws MBeanException, RuntimeOperationsException;
    
    void setMBeanDescriptor(final Descriptor p0) throws MBeanException, RuntimeOperationsException;
    
    ModelMBeanAttributeInfo getAttribute(final String p0) throws MBeanException, RuntimeOperationsException;
    
    ModelMBeanOperationInfo getOperation(final String p0) throws MBeanException, RuntimeOperationsException;
    
    ModelMBeanNotificationInfo getNotification(final String p0) throws MBeanException, RuntimeOperationsException;
    
    Object clone();
    
    MBeanAttributeInfo[] getAttributes();
    
    String getClassName();
    
    MBeanConstructorInfo[] getConstructors();
    
    String getDescription();
    
    MBeanNotificationInfo[] getNotifications();
    
    MBeanOperationInfo[] getOperations();
}
