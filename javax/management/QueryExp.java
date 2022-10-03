package javax.management;

import java.io.Serializable;

public interface QueryExp extends Serializable
{
    boolean apply(final ObjectName p0) throws BadStringOperationException, BadBinaryOpValueExpException, BadAttributeValueExpException, InvalidApplicationException;
    
    void setMBeanServer(final MBeanServer p0);
}
