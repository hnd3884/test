package javax.management;

import java.util.Set;
import java.io.IOException;

public interface MBeanServerConnection
{
    ObjectInstance createMBean(final String p0, final ObjectName p1) throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException, IOException;
    
    ObjectInstance createMBean(final String p0, final ObjectName p1, final ObjectName p2) throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException, InstanceNotFoundException, IOException;
    
    ObjectInstance createMBean(final String p0, final ObjectName p1, final Object[] p2, final String[] p3) throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException, IOException;
    
    ObjectInstance createMBean(final String p0, final ObjectName p1, final ObjectName p2, final Object[] p3, final String[] p4) throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException, InstanceNotFoundException, IOException;
    
    void unregisterMBean(final ObjectName p0) throws InstanceNotFoundException, MBeanRegistrationException, IOException;
    
    ObjectInstance getObjectInstance(final ObjectName p0) throws InstanceNotFoundException, IOException;
    
    Set<ObjectInstance> queryMBeans(final ObjectName p0, final QueryExp p1) throws IOException;
    
    Set<ObjectName> queryNames(final ObjectName p0, final QueryExp p1) throws IOException;
    
    boolean isRegistered(final ObjectName p0) throws IOException;
    
    Integer getMBeanCount() throws IOException;
    
    Object getAttribute(final ObjectName p0, final String p1) throws MBeanException, AttributeNotFoundException, InstanceNotFoundException, ReflectionException, IOException;
    
    AttributeList getAttributes(final ObjectName p0, final String[] p1) throws InstanceNotFoundException, ReflectionException, IOException;
    
    void setAttribute(final ObjectName p0, final Attribute p1) throws InstanceNotFoundException, AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException, IOException;
    
    AttributeList setAttributes(final ObjectName p0, final AttributeList p1) throws InstanceNotFoundException, ReflectionException, IOException;
    
    Object invoke(final ObjectName p0, final String p1, final Object[] p2, final String[] p3) throws InstanceNotFoundException, MBeanException, ReflectionException, IOException;
    
    String getDefaultDomain() throws IOException;
    
    String[] getDomains() throws IOException;
    
    void addNotificationListener(final ObjectName p0, final NotificationListener p1, final NotificationFilter p2, final Object p3) throws InstanceNotFoundException, IOException;
    
    void addNotificationListener(final ObjectName p0, final ObjectName p1, final NotificationFilter p2, final Object p3) throws InstanceNotFoundException, IOException;
    
    void removeNotificationListener(final ObjectName p0, final ObjectName p1) throws InstanceNotFoundException, ListenerNotFoundException, IOException;
    
    void removeNotificationListener(final ObjectName p0, final ObjectName p1, final NotificationFilter p2, final Object p3) throws InstanceNotFoundException, ListenerNotFoundException, IOException;
    
    void removeNotificationListener(final ObjectName p0, final NotificationListener p1) throws InstanceNotFoundException, ListenerNotFoundException, IOException;
    
    void removeNotificationListener(final ObjectName p0, final NotificationListener p1, final NotificationFilter p2, final Object p3) throws InstanceNotFoundException, ListenerNotFoundException, IOException;
    
    MBeanInfo getMBeanInfo(final ObjectName p0) throws InstanceNotFoundException, IntrospectionException, ReflectionException, IOException;
    
    boolean isInstanceOf(final ObjectName p0, final String p1) throws InstanceNotFoundException, IOException;
}
