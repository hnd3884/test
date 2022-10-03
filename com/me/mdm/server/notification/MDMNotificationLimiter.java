package com.me.mdm.server.notification;

public interface MDMNotificationLimiter
{
    void checkAndLimitNotification(final String p0, final int p1);
    
    int getNotificationBatchCount(final String p0);
}
