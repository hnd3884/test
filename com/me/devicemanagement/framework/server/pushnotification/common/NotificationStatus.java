package com.me.devicemanagement.framework.server.pushnotification.common;

public enum NotificationStatus
{
    UNREAD(0), 
    READ(1);
    
    public int id;
    
    private NotificationStatus(final int id) {
        this.id = id;
    }
}
