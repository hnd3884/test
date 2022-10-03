package com.sun.jmx.remote.security;

import javax.management.Notification;
import javax.security.auth.Subject;
import javax.management.ObjectName;

public interface NotificationAccessController
{
    void addNotificationListener(final String p0, final ObjectName p1, final Subject p2) throws SecurityException;
    
    void removeNotificationListener(final String p0, final ObjectName p1, final Subject p2) throws SecurityException;
    
    void fetchNotification(final String p0, final ObjectName p1, final Notification p2, final Subject p3) throws SecurityException;
}
