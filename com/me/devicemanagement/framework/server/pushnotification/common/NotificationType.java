package com.me.devicemanagement.framework.server.pushnotification.common;

public enum NotificationType
{
    DEFAULT(0), 
    ALERT(1), 
    WAKEUP(3);
    
    public int id;
    
    private NotificationType(final int id) {
        this.id = id;
    }
}
