package javax.naming.ldap;

import javax.naming.event.NamingListener;

public interface UnsolicitedNotificationListener extends NamingListener
{
    void notificationReceived(final UnsolicitedNotificationEvent p0);
}
