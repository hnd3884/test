package com.me.devicemanagement.framework.server.pushnotification.notifiers.android;

import com.me.devicemanagement.framework.server.pushnotification.device.NotificationDevice;
import java.util.ArrayList;
import com.me.devicemanagement.framework.server.pushnotification.notification.Notification;
import com.me.devicemanagement.framework.server.pushnotification.notifiers.Notifier;

public interface AndroidNotifier extends Notifier
{
    void onNotificationSuccess(final Notification p0, final ArrayList<NotificationDevice> p1);
    
    void onNotificationFailed(final Notification p0, final ArrayList<NotificationDevice> p1);
    
    void onReRegisterDevice(final Notification p0, final ArrayList<NotificationDevice> p1);
}
