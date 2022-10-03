package com.google.api.services.androidenterprise.model;

import com.google.api.client.util.Data;
import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import java.util.List;
import com.google.api.client.json.GenericJson;

public final class NotificationSet extends GenericJson
{
    @Key
    private List<Notification> notification;
    @Key
    private String notificationSetId;
    
    public List<Notification> getNotification() {
        return this.notification;
    }
    
    public NotificationSet setNotification(final List<Notification> notification) {
        this.notification = notification;
        return this;
    }
    
    public String getNotificationSetId() {
        return this.notificationSetId;
    }
    
    public NotificationSet setNotificationSetId(final String notificationSetId) {
        this.notificationSetId = notificationSetId;
        return this;
    }
    
    public NotificationSet set(final String fieldName, final Object value) {
        return (NotificationSet)super.set(fieldName, value);
    }
    
    public NotificationSet clone() {
        return (NotificationSet)super.clone();
    }
    
    static {
        Data.nullOf((Class)Notification.class);
    }
}
