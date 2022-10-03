package com.sun.nio.sctp;

import jdk.Exported;

@Exported
public interface NotificationHandler<T>
{
    HandlerResult handleNotification(final Notification p0, final T p1);
}
