package javax.management.timer;

import javax.management.Notification;

class TimerAlarmClockNotification extends Notification
{
    private static final long serialVersionUID = -4841061275673620641L;
    
    public TimerAlarmClockNotification(final TimerAlarmClock timerAlarmClock) {
        super("", timerAlarmClock, 0L);
    }
}
