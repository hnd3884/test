package javax.management.openmbean;

import javax.management.MBeanParameterInfo;

public interface OpenMBeanConstructorInfo
{
    String getDescription();
    
    String getName();
    
    MBeanParameterInfo[] getSignature();
    
    boolean equals(final Object p0);
    
    int hashCode();
    
    String toString();
}
