package javax.management.openmbean;

import javax.management.MBeanNotificationInfo;
import javax.management.MBeanConstructorInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanAttributeInfo;

public interface OpenMBeanInfo
{
    String getClassName();
    
    String getDescription();
    
    MBeanAttributeInfo[] getAttributes();
    
    MBeanOperationInfo[] getOperations();
    
    MBeanConstructorInfo[] getConstructors();
    
    MBeanNotificationInfo[] getNotifications();
    
    boolean equals(final Object p0);
    
    int hashCode();
    
    String toString();
}
