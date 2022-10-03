package com.me.devicemanagement.framework.server.pushnotification.notifiers;

import com.me.devicemanagement.framework.server.pushnotification.notification.Notification;

public interface Notifier
{
    void notify(final Notification p0) throws Exception;
    
    void notifyAsync(final Notification p0) throws Exception;
}
