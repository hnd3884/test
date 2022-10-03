package javax.management;

public interface DynamicMBean
{
    Object getAttribute(final String p0) throws AttributeNotFoundException, MBeanException, ReflectionException;
    
    void setAttribute(final Attribute p0) throws AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException;
    
    AttributeList getAttributes(final String[] p0);
    
    AttributeList setAttributes(final AttributeList p0);
    
    Object invoke(final String p0, final Object[] p1, final String[] p2) throws MBeanException, ReflectionException;
    
    MBeanInfo getMBeanInfo();
}
