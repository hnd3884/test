package javax.management;

public interface NotificationBroadcaster
{
    void addNotificationListener(final NotificationListener p0, final NotificationFilter p1, final Object p2) throws IllegalArgumentException;
    
    void removeNotificationListener(final NotificationListener p0) throws ListenerNotFoundException;
    
    MBeanNotificationInfo[] getNotificationInfo();
}
