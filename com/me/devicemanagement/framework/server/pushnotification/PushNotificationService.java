package com.me.devicemanagement.framework.server.pushnotification;

import com.me.devicemanagement.framework.server.pushnotification.notifiers.Notifier;
import com.me.devicemanagement.framework.server.pushnotification.common.NotificationPlatform;
import com.me.devicemanagement.framework.server.pushnotification.notification.Notification;
import com.me.devicemanagement.framework.server.pushnotification.notifiers.web.WebNotifier;
import com.me.devicemanagement.framework.server.pushnotification.notifiers.ios.iOSNotifier;
import com.me.devicemanagement.framework.server.pushnotification.notifiers.android.AndroidNotifier;

public abstract class PushNotificationService
{
    protected abstract AndroidNotifier getAndroidNotifier();
    
    protected abstract iOSNotifier getIOSNotifier();
    
    protected abstract WebNotifier getWebNotifier();
    
    protected abstract boolean isNotificationServiceEnabled();
    
    public void pushNotifyAsync(final Notification notification) throws Exception {
        if (this.isNotificationServiceEnabled()) {
            this.pushNotify(notification, true);
        }
    }
    
    public void pushNotify(final Notification notification) throws Exception {
        if (this.isNotificationServiceEnabled()) {
            this.pushNotify(notification, false);
        }
    }
    
    private void pushNotify(final Notification notification, final boolean async) throws Exception {
        if (this.isNotificationServiceEnabled()) {
            for (final NotificationPlatform platform : notification.getPlatforms()) {
                final Notifier notifier = this.getNotifier(platform);
                if (notifier != null) {
                    if (async) {
                        notifier.notifyAsync(notification);
                    }
                    else {
                        notifier.notify(notification);
                    }
                }
            }
        }
    }
    
    private Notifier getNotifier(final NotificationPlatform platform) {
        switch (platform) {
            case WEB: {
                return this.getWebNotifier();
            }
            case IOS: {
                return this.getIOSNotifier();
            }
            case ANDROID: {
                return this.getAndroidNotifier();
            }
            default: {
                return null;
            }
        }
    }
}
