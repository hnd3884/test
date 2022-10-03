package javax.management.timer;

import java.util.Vector;
import javax.management.InstanceNotFoundException;
import java.util.Date;

public interface TimerMBean
{
    void start();
    
    void stop();
    
    Integer addNotification(final String p0, final String p1, final Object p2, final Date p3, final long p4, final long p5, final boolean p6) throws IllegalArgumentException;
    
    Integer addNotification(final String p0, final String p1, final Object p2, final Date p3, final long p4, final long p5) throws IllegalArgumentException;
    
    Integer addNotification(final String p0, final String p1, final Object p2, final Date p3, final long p4) throws IllegalArgumentException;
    
    Integer addNotification(final String p0, final String p1, final Object p2, final Date p3) throws IllegalArgumentException;
    
    void removeNotification(final Integer p0) throws InstanceNotFoundException;
    
    void removeNotifications(final String p0) throws InstanceNotFoundException;
    
    void removeAllNotifications();
    
    int getNbNotifications();
    
    Vector<Integer> getAllNotificationIDs();
    
    Vector<Integer> getNotificationIDs(final String p0);
    
    String getNotificationType(final Integer p0);
    
    String getNotificationMessage(final Integer p0);
    
    Object getNotificationUserData(final Integer p0);
    
    Date getDate(final Integer p0);
    
    Long getPeriod(final Integer p0);
    
    Long getNbOccurences(final Integer p0);
    
    Boolean getFixedRate(final Integer p0);
    
    boolean getSendPastNotifications();
    
    void setSendPastNotifications(final boolean p0);
    
    boolean isActive();
    
    boolean isEmpty();
}
