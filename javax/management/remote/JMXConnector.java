package javax.management.remote;

import javax.management.ListenerNotFoundException;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;
import javax.security.auth.Subject;
import javax.management.MBeanServerConnection;
import java.util.Map;
import java.io.IOException;
import java.io.Closeable;

public interface JMXConnector extends Closeable
{
    public static final String CREDENTIALS = "jmx.remote.credentials";
    
    void connect() throws IOException;
    
    void connect(final Map<String, ?> p0) throws IOException;
    
    MBeanServerConnection getMBeanServerConnection() throws IOException;
    
    MBeanServerConnection getMBeanServerConnection(final Subject p0) throws IOException;
    
    void close() throws IOException;
    
    void addConnectionNotificationListener(final NotificationListener p0, final NotificationFilter p1, final Object p2);
    
    void removeConnectionNotificationListener(final NotificationListener p0) throws ListenerNotFoundException;
    
    void removeConnectionNotificationListener(final NotificationListener p0, final NotificationFilter p1, final Object p2) throws ListenerNotFoundException;
    
    String getConnectionId() throws IOException;
}
