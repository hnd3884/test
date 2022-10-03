package javax.management.timer;

import javax.management.Notification;

public class TimerNotification extends Notification
{
    private static final long serialVersionUID = 1798492029603825750L;
    private Integer notificationID;
    
    public TimerNotification(final String s, final Object o, final long n, final long n2, final String s2, final Integer notificationID) {
        super(s, o, n, n2, s2);
        this.notificationID = notificationID;
    }
    
    public Integer getNotificationID() {
        return this.notificationID;
    }
    
    Object cloneTimerNotification() {
        final TimerNotification timerNotification = new TimerNotification(this.getType(), this.getSource(), this.getSequenceNumber(), this.getTimeStamp(), this.getMessage(), this.notificationID);
        timerNotification.setUserData(this.getUserData());
        return timerNotification;
    }
}
