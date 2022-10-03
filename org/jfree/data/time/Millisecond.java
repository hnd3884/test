package org.jfree.data.time;

import java.util.Calendar;
import java.util.TimeZone;
import java.util.Date;
import java.io.Serializable;

public class Millisecond extends RegularTimePeriod implements Serializable
{
    static final long serialVersionUID = -5316836467277638485L;
    public static final int FIRST_MILLISECOND_IN_SECOND = 0;
    public static final int LAST_MILLISECOND_IN_SECOND = 999;
    private int millisecond;
    private Second second;
    
    public Millisecond() {
        this(new Date());
    }
    
    public Millisecond(final int millisecond, final Second second) {
        this.millisecond = millisecond;
        this.second = second;
    }
    
    public Millisecond(final int millisecond, final int second, final int minute, final int hour, final int day, final int month, final int year) {
        this(millisecond, new Second(second, minute, hour, day, month, year));
    }
    
    public Millisecond(final Date time) {
        this(time, RegularTimePeriod.DEFAULT_TIME_ZONE);
    }
    
    public Millisecond(final Date time, final TimeZone zone) {
        this.second = new Second(time, zone);
        final Calendar calendar = Calendar.getInstance(zone);
        calendar.setTime(time);
        this.millisecond = calendar.get(14);
    }
    
    public Second getSecond() {
        return this.second;
    }
    
    public long getMillisecond() {
        return this.millisecond;
    }
    
    public RegularTimePeriod previous() {
        RegularTimePeriod result = null;
        if (this.millisecond != 0) {
            result = new Millisecond(this.millisecond - 1, this.second);
        }
        else {
            final Second previous = (Second)this.second.previous();
            if (previous != null) {
                result = new Millisecond(999, previous);
            }
        }
        return result;
    }
    
    public RegularTimePeriod next() {
        RegularTimePeriod result = null;
        if (this.millisecond != 999) {
            result = new Millisecond(this.millisecond + 1, this.second);
        }
        else {
            final Second next = (Second)this.second.next();
            if (next != null) {
                result = new Millisecond(0, next);
            }
        }
        return result;
    }
    
    public long getSerialIndex() {
        return this.second.getSerialIndex() * 1000L + this.millisecond;
    }
    
    public boolean equals(final Object obj) {
        if (obj instanceof Millisecond) {
            final Millisecond m = (Millisecond)obj;
            return this.millisecond == m.getMillisecond() && this.second.equals(m.getSecond());
        }
        return false;
    }
    
    public int hashCode() {
        int result = 17;
        result = 37 * result + this.millisecond;
        result = 37 * result + this.second.hashCode();
        return result;
    }
    
    public int compareTo(final Object obj) {
        int result;
        if (obj instanceof Millisecond) {
            final Millisecond ms = (Millisecond)obj;
            final long difference = this.getFirstMillisecond() - ms.getFirstMillisecond();
            if (difference > 0L) {
                result = 1;
            }
            else if (difference < 0L) {
                result = -1;
            }
            else {
                result = 0;
            }
        }
        else if (obj instanceof RegularTimePeriod) {
            result = 0;
        }
        else {
            result = 1;
        }
        return result;
    }
    
    public long getFirstMillisecond() {
        return this.second.getFirstMillisecond() + this.millisecond;
    }
    
    public long getFirstMillisecond(final Calendar calendar) {
        return this.second.getFirstMillisecond(calendar) + this.millisecond;
    }
    
    public long getLastMillisecond() {
        return this.second.getFirstMillisecond() + this.millisecond;
    }
    
    public long getLastMillisecond(final Calendar calendar) {
        return this.second.getFirstMillisecond(calendar) + this.millisecond;
    }
}
