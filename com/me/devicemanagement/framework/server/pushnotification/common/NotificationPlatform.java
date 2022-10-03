package com.me.devicemanagement.framework.server.pushnotification.common;

import java.util.Iterator;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Arrays;

public enum NotificationPlatform
{
    ANDROID(0, true), 
    IOS(1, true), 
    WEB(3, false);
    
    public int id;
    public boolean isMobilePlatform;
    
    private NotificationPlatform(final int id, final boolean isMobilePlatform) {
        this.id = id;
        this.isMobilePlatform = isMobilePlatform;
    }
    
    public static NotificationPlatform getPlatform(final int id) {
        for (final NotificationPlatform platform : values()) {
            if (platform.id == id) {
                return platform;
            }
        }
        return null;
    }
    
    public static NotificationPlatform[] getMobileOnlyPlatforms(final NotificationPlatform[] platforms) {
        final ArrayList<NotificationPlatform> list = new ArrayList<NotificationPlatform>(Arrays.asList(platforms));
        final Iterator<NotificationPlatform> it = list.iterator();
        while (it.hasNext()) {
            final NotificationPlatform platform = it.next();
            if (!platform.isMobilePlatform) {
                it.remove();
            }
        }
        return list.toArray(new NotificationPlatform[list.size()]);
    }
}
