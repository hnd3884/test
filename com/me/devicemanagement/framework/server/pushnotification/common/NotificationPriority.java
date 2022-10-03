package com.me.devicemanagement.framework.server.pushnotification.common;

public enum NotificationPriority
{
    LOW(0), 
    MEDIUM(1), 
    HIGH(3);
    
    public int id;
    
    private NotificationPriority(final int id) {
        this.id = id;
    }
}
