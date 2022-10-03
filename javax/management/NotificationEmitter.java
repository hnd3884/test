package javax.management;

public interface NotificationEmitter extends NotificationBroadcaster
{
    void removeNotificationListener(final NotificationListener p0, final NotificationFilter p1, final Object p2) throws ListenerNotFoundException;
}
