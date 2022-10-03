package javax.management;

import javax.management.loading.ClassLoaderRepository;
import java.io.ObjectInputStream;
import java.util.Set;

public interface MBeanServer extends MBeanServerConnection
{
    ObjectInstance createMBean(final String p0, final ObjectName p1) throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException;
    
    ObjectInstance createMBean(final String p0, final ObjectName p1, final ObjectName p2) throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException, InstanceNotFoundException;
    
    ObjectInstance createMBean(final String p0, final ObjectName p1, final Object[] p2, final String[] p3) throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException;
    
    ObjectInstance createMBean(final String p0, final ObjectName p1, final ObjectName p2, final Object[] p3, final String[] p4) throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException, InstanceNotFoundException;
    
    ObjectInstance registerMBean(final Object p0, final ObjectName p1) throws InstanceAlreadyExistsException, MBeanRegistrationException, NotCompliantMBeanException;
    
    void unregisterMBean(final ObjectName p0) throws InstanceNotFoundException, MBeanRegistrationException;
    
    ObjectInstance getObjectInstance(final ObjectName p0) throws InstanceNotFoundException;
    
    Set<ObjectInstance> queryMBeans(final ObjectName p0, final QueryExp p1);
    
    Set<ObjectName> queryNames(final ObjectName p0, final QueryExp p1);
    
    boolean isRegistered(final ObjectName p0);
    
    Integer getMBeanCount();
    
    Object getAttribute(final ObjectName p0, final String p1) throws MBeanException, AttributeNotFoundException, InstanceNotFoundException, ReflectionException;
    
    AttributeList getAttributes(final ObjectName p0, final String[] p1) throws InstanceNotFoundException, ReflectionException;
    
    void setAttribute(final ObjectName p0, final Attribute p1) throws InstanceNotFoundException, AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException;
    
    AttributeList setAttributes(final ObjectName p0, final AttributeList p1) throws InstanceNotFoundException, ReflectionException;
    
    Object invoke(final ObjectName p0, final String p1, final Object[] p2, final String[] p3) throws InstanceNotFoundException, MBeanException, ReflectionException;
    
    String getDefaultDomain();
    
    String[] getDomains();
    
    void addNotificationListener(final ObjectName p0, final NotificationListener p1, final NotificationFilter p2, final Object p3) throws InstanceNotFoundException;
    
    void addNotificationListener(final ObjectName p0, final ObjectName p1, final NotificationFilter p2, final Object p3) throws InstanceNotFoundException;
    
    void removeNotificationListener(final ObjectName p0, final ObjectName p1) throws InstanceNotFoundException, ListenerNotFoundException;
    
    void removeNotificationListener(final ObjectName p0, final ObjectName p1, final NotificationFilter p2, final Object p3) throws InstanceNotFoundException, ListenerNotFoundException;
    
    void removeNotificationListener(final ObjectName p0, final NotificationListener p1) throws InstanceNotFoundException, ListenerNotFoundException;
    
    void removeNotificationListener(final ObjectName p0, final NotificationListener p1, final NotificationFilter p2, final Object p3) throws InstanceNotFoundException, ListenerNotFoundException;
    
    MBeanInfo getMBeanInfo(final ObjectName p0) throws InstanceNotFoundException, IntrospectionException, ReflectionException;
    
    boolean isInstanceOf(final ObjectName p0, final String p1) throws InstanceNotFoundException;
    
    Object instantiate(final String p0) throws ReflectionException, MBeanException;
    
    Object instantiate(final String p0, final ObjectName p1) throws ReflectionException, MBeanException, InstanceNotFoundException;
    
    Object instantiate(final String p0, final Object[] p1, final String[] p2) throws ReflectionException, MBeanException;
    
    Object instantiate(final String p0, final ObjectName p1, final Object[] p2, final String[] p3) throws ReflectionException, MBeanException, InstanceNotFoundException;
    
    @Deprecated
    ObjectInputStream deserialize(final ObjectName p0, final byte[] p1) throws InstanceNotFoundException, OperationsException;
    
    @Deprecated
    ObjectInputStream deserialize(final String p0, final byte[] p1) throws OperationsException, ReflectionException;
    
    @Deprecated
    ObjectInputStream deserialize(final String p0, final ObjectName p1, final byte[] p2) throws InstanceNotFoundException, OperationsException, ReflectionException;
    
    ClassLoader getClassLoaderFor(final ObjectName p0) throws InstanceNotFoundException;
    
    ClassLoader getClassLoader(final ObjectName p0) throws InstanceNotFoundException;
    
    ClassLoaderRepository getClassLoaderRepository();
}
