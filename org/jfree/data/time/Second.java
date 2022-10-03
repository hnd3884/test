package org.jfree.data.time;

import java.util.Calendar;
import java.util.TimeZone;
import java.util.Date;
import java.io.Serializable;

public class Second extends RegularTimePeriod implements Serializable
{
    private static final long serialVersionUID = -6536564190712383466L;
    public static final int FIRST_SECOND_IN_MINUTE = 0;
    public static final int LAST_SECOND_IN_MINUTE = 59;
    private Minute minute;
    private int second;
    
    public Second() {
        this(new Date());
    }
    
    public Second(final int second, final Minute minute) {
        if (minute == null) {
            throw new IllegalArgumentException("Null 'minute' argument.");
        }
        this.minute = minute;
        this.second = second;
    }
    
    public Second(final int second, final int minute, final int hour, final int day, final int month, final int year) {
        this(second, new Minute(minute, hour, day, month, year));
    }
    
    public Second(final Date time) {
        this(time, RegularTimePeriod.DEFAULT_TIME_ZONE);
    }
    
    public Second(final Date time, final TimeZone zone) {
        this.minute = new Minute(time, zone);
        final Calendar calendar = Calendar.getInstance(zone);
        calendar.setTime(time);
        this.second = calendar.get(13);
    }
    
    public int getSecond() {
        return this.second;
    }
    
    public Minute getMinute() {
        return this.minute;
    }
    
    public RegularTimePeriod previous() {
        Second result = null;
        if (this.second != 0) {
            result = new Second(this.second - 1, this.minute);
        }
        else {
            final Minute previous = (Minute)this.minute.previous();
            if (previous != null) {
                result = new Second(59, previous);
            }
        }
        return result;
    }
    
    public RegularTimePeriod next() {
        Second result = null;
        if (this.second != 59) {
            result = new Second(this.second + 1, this.minute);
        }
        else {
            final Minute next = (Minute)this.minute.next();
            if (next != null) {
                result = new Second(0, next);
            }
        }
        return result;
    }
    
    public long getSerialIndex() {
        return this.minute.getSerialIndex() * 60L + this.second;
    }
    
    public long getFirstMillisecond(final Calendar calendar) {
        return this.minute.getFirstMillisecond(calendar) + this.second * 1000L;
    }
    
    public long getLastMillisecond(final Calendar calendar) {
        return this.minute.getFirstMillisecond(calendar) + this.second * 1000L + 999L;
    }
    
    public boolean equals(final Object obj) {
        if (obj instanceof Second) {
            final Second s = (Second)obj;
            return this.second == s.getSecond() && this.minute.equals(s.getMinute());
        }
        return false;
    }
    
    public int hashCode() {
        int result = 17;
        result = 37 * result + this.second;
        result = 37 * result + this.minute.hashCode();
        return result;
    }
    
    public int compareTo(final Object o1) {
        int result;
        if (o1 instanceof Second) {
            final Second s = (Second)o1;
            result = this.minute.compareTo(s.minute);
            if (result == 0) {
                result = this.second - s.second;
            }
        }
        else if (o1 instanceof RegularTimePeriod) {
            result = 0;
        }
        else {
            result = 1;
        }
        return result;
    }
    
    public static Second parseSecond(String s) {
        Second result = null;
        s = s.trim();
        final String daystr = s.substring(0, Math.min(10, s.length()));
        final Day day = Day.parseDay(daystr);
        if (day != null) {
            String hmsstr = s.substring(Math.min(daystr.length() + 1, s.length()), s.length());
            hmsstr = hmsstr.trim();
            final int l = hmsstr.length();
            final String hourstr = hmsstr.substring(0, Math.min(2, l));
            final String minstr = hmsstr.substring(Math.min(3, l), Math.min(5, l));
            final String secstr = hmsstr.substring(Math.min(6, l), Math.min(8, l));
            final int hour = Integer.parseInt(hourstr);
            if (hour >= 0 && hour <= 23) {
                final int minute = Integer.parseInt(minstr);
                if (minute >= 0 && minute <= 59) {
                    final Minute m = new Minute(minute, new Hour(hour, day));
                    final int second = Integer.parseInt(secstr);
                    if (second >= 0 && second <= 59) {
                        result = new Second(second, m);
                    }
                }
            }
        }
        return result;
    }
}
