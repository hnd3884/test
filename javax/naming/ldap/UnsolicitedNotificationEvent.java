package javax.naming.ldap;

import java.util.EventObject;

public class UnsolicitedNotificationEvent extends EventObject
{
    private UnsolicitedNotification notice;
    private static final long serialVersionUID = -2382603380799883705L;
    
    public UnsolicitedNotificationEvent(final Object o, final UnsolicitedNotification notice) {
        super(o);
        this.notice = notice;
    }
    
    public UnsolicitedNotification getNotification() {
        return this.notice;
    }
    
    public void dispatch(final UnsolicitedNotificationListener unsolicitedNotificationListener) {
        unsolicitedNotificationListener.notificationReceived(this);
    }
}
