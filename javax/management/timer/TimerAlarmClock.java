package javax.management.timer;

import java.util.logging.Level;
import com.sun.jmx.defaults.JmxProperties;
import java.util.Date;
import java.util.TimerTask;

class TimerAlarmClock extends TimerTask
{
    Timer listener;
    long timeout;
    Date next;
    
    public TimerAlarmClock(final Timer listener, final long n) {
        this.listener = null;
        this.timeout = 10000L;
        this.next = null;
        this.listener = listener;
        this.timeout = Math.max(0L, n);
    }
    
    public TimerAlarmClock(final Timer listener, final Date next) {
        this.listener = null;
        this.timeout = 10000L;
        this.next = null;
        this.listener = listener;
        this.next = next;
    }
    
    @Override
    public void run() {
        try {
            this.listener.notifyAlarmClock(new TimerAlarmClockNotification(this));
        }
        catch (final Exception ex) {
            JmxProperties.TIMER_LOGGER.logp(Level.FINEST, Timer.class.getName(), "run", "Got unexpected exception when sending a notification", ex);
        }
    }
}
