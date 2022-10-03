package javax.management;

import java.io.Serializable;

public interface ValueExp extends Serializable
{
    ValueExp apply(final ObjectName p0) throws BadStringOperationException, BadBinaryOpValueExpException, BadAttributeValueExpException, InvalidApplicationException;
    
    @Deprecated
    void setMBeanServer(final MBeanServer p0);
}
