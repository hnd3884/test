package com.sun.jmx.remote.internal;

import javax.management.Notification;
import javax.management.ObjectName;
import javax.management.remote.TargetedNotification;
import java.util.List;

public interface NotificationBufferFilter
{
    void apply(final List<TargetedNotification> p0, final ObjectName p1, final Notification p2);
}
