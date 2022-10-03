package javax.management.modelmbean;

import javax.management.ListenerNotFoundException;
import javax.management.NotificationListener;
import javax.management.Attribute;
import javax.management.AttributeChangeNotification;
import javax.management.RuntimeOperationsException;
import javax.management.MBeanException;
import javax.management.Notification;
import javax.management.NotificationBroadcaster;

public interface ModelMBeanNotificationBroadcaster extends NotificationBroadcaster
{
    void sendNotification(final Notification p0) throws MBeanException, RuntimeOperationsException;
    
    void sendNotification(final String p0) throws MBeanException, RuntimeOperationsException;
    
    void sendAttributeChangeNotification(final AttributeChangeNotification p0) throws MBeanException, RuntimeOperationsException;
    
    void sendAttributeChangeNotification(final Attribute p0, final Attribute p1) throws MBeanException, RuntimeOperationsException;
    
    void addAttributeChangeNotificationListener(final NotificationListener p0, final String p1, final Object p2) throws MBeanException, RuntimeOperationsException, IllegalArgumentException;
    
    void removeAttributeChangeNotificationListener(final NotificationListener p0, final String p1) throws MBeanException, RuntimeOperationsException, ListenerNotFoundException;
}
