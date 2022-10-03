package javax.management.remote.rmi;

import javax.management.remote.NotificationResult;
import javax.management.ListenerNotFoundException;
import javax.management.IntrospectionException;
import javax.management.MBeanInfo;
import javax.management.InvalidAttributeValueException;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import java.util.Set;
import java.rmi.MarshalledObject;
import javax.management.InstanceNotFoundException;
import javax.management.NotCompliantMBeanException;
import javax.management.MBeanException;
import javax.management.MBeanRegistrationException;
import javax.management.InstanceAlreadyExistsException;
import javax.management.ReflectionException;
import javax.management.ObjectInstance;
import javax.security.auth.Subject;
import javax.management.ObjectName;
import java.io.IOException;
import java.rmi.Remote;
import java.io.Closeable;

public interface RMIConnection extends Closeable, Remote
{
    String getConnectionId() throws IOException;
    
    void close() throws IOException;
    
    ObjectInstance createMBean(final String p0, final ObjectName p1, final Subject p2) throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException, IOException;
    
    ObjectInstance createMBean(final String p0, final ObjectName p1, final ObjectName p2, final Subject p3) throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException, InstanceNotFoundException, IOException;
    
    ObjectInstance createMBean(final String p0, final ObjectName p1, final MarshalledObject p2, final String[] p3, final Subject p4) throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException, IOException;
    
    ObjectInstance createMBean(final String p0, final ObjectName p1, final ObjectName p2, final MarshalledObject p3, final String[] p4, final Subject p5) throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException, InstanceNotFoundException, IOException;
    
    void unregisterMBean(final ObjectName p0, final Subject p1) throws InstanceNotFoundException, MBeanRegistrationException, IOException;
    
    ObjectInstance getObjectInstance(final ObjectName p0, final Subject p1) throws InstanceNotFoundException, IOException;
    
    Set<ObjectInstance> queryMBeans(final ObjectName p0, final MarshalledObject p1, final Subject p2) throws IOException;
    
    Set<ObjectName> queryNames(final ObjectName p0, final MarshalledObject p1, final Subject p2) throws IOException;
    
    boolean isRegistered(final ObjectName p0, final Subject p1) throws IOException;
    
    Integer getMBeanCount(final Subject p0) throws IOException;
    
    Object getAttribute(final ObjectName p0, final String p1, final Subject p2) throws MBeanException, AttributeNotFoundException, InstanceNotFoundException, ReflectionException, IOException;
    
    AttributeList getAttributes(final ObjectName p0, final String[] p1, final Subject p2) throws InstanceNotFoundException, ReflectionException, IOException;
    
    void setAttribute(final ObjectName p0, final MarshalledObject p1, final Subject p2) throws InstanceNotFoundException, AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException, IOException;
    
    AttributeList setAttributes(final ObjectName p0, final MarshalledObject p1, final Subject p2) throws InstanceNotFoundException, ReflectionException, IOException;
    
    Object invoke(final ObjectName p0, final String p1, final MarshalledObject p2, final String[] p3, final Subject p4) throws InstanceNotFoundException, MBeanException, ReflectionException, IOException;
    
    String getDefaultDomain(final Subject p0) throws IOException;
    
    String[] getDomains(final Subject p0) throws IOException;
    
    MBeanInfo getMBeanInfo(final ObjectName p0, final Subject p1) throws InstanceNotFoundException, IntrospectionException, ReflectionException, IOException;
    
    boolean isInstanceOf(final ObjectName p0, final String p1, final Subject p2) throws InstanceNotFoundException, IOException;
    
    void addNotificationListener(final ObjectName p0, final ObjectName p1, final MarshalledObject p2, final MarshalledObject p3, final Subject p4) throws InstanceNotFoundException, IOException;
    
    void removeNotificationListener(final ObjectName p0, final ObjectName p1, final Subject p2) throws InstanceNotFoundException, ListenerNotFoundException, IOException;
    
    void removeNotificationListener(final ObjectName p0, final ObjectName p1, final MarshalledObject p2, final MarshalledObject p3, final Subject p4) throws InstanceNotFoundException, ListenerNotFoundException, IOException;
    
    Integer[] addNotificationListeners(final ObjectName[] p0, final MarshalledObject[] p1, final Subject[] p2) throws InstanceNotFoundException, IOException;
    
    void removeNotificationListeners(final ObjectName p0, final Integer[] p1, final Subject p2) throws InstanceNotFoundException, ListenerNotFoundException, IOException;
    
    NotificationResult fetchNotifications(final long p0, final int p1, final long p2) throws IOException;
}
