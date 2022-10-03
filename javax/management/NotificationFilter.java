package javax.management;

import java.io.Serializable;

public interface NotificationFilter extends Serializable
{
    boolean isNotificationEnabled(final Notification p0);
}
