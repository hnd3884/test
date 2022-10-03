package com.sun.jmx.remote.internal;

import javax.security.auth.Subject;
import javax.management.NotificationListener;
import javax.management.NotificationFilter;
import javax.management.ObjectName;

public class ClientListenerInfo
{
    private final ObjectName name;
    private final Integer listenerID;
    private final NotificationFilter filter;
    private final NotificationListener listener;
    private final Object handback;
    private final Subject delegationSubject;
    
    public ClientListenerInfo(final Integer listenerID, final ObjectName name, final NotificationListener listener, final NotificationFilter filter, final Object handback, final Subject delegationSubject) {
        this.listenerID = listenerID;
        this.name = name;
        this.listener = listener;
        this.filter = filter;
        this.handback = handback;
        this.delegationSubject = delegationSubject;
    }
    
    public ObjectName getObjectName() {
        return this.name;
    }
    
    public Integer getListenerID() {
        return this.listenerID;
    }
    
    public NotificationFilter getNotificationFilter() {
        return this.filter;
    }
    
    public NotificationListener getListener() {
        return this.listener;
    }
    
    public Object getHandback() {
        return this.handback;
    }
    
    public Subject getDelegationSubject() {
        return this.delegationSubject;
    }
    
    public boolean sameAs(final ObjectName objectName) {
        return this.getObjectName().equals(objectName);
    }
    
    public boolean sameAs(final ObjectName objectName, final NotificationListener notificationListener) {
        return this.getObjectName().equals(objectName) && this.getListener() == notificationListener;
    }
    
    public boolean sameAs(final ObjectName objectName, final NotificationListener notificationListener, final NotificationFilter notificationFilter, final Object o) {
        return this.getObjectName().equals(objectName) && this.getListener() == notificationListener && this.getNotificationFilter() == notificationFilter && this.getHandback() == o;
    }
}
