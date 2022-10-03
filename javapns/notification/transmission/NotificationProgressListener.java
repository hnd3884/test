package javapns.notification.transmission;

public interface NotificationProgressListener
{
    void eventAllThreadsStarted(final NotificationThreads p0);
    
    void eventThreadStarted(final NotificationThread p0);
    
    void eventThreadFinished(final NotificationThread p0);
    
    void eventConnectionRestarted(final NotificationThread p0);
    
    void eventAllThreadsFinished(final NotificationThreads p0);
    
    void eventCriticalException(final NotificationThread p0, final Exception p1);
}
