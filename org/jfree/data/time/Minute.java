package org.jfree.data.time;

import java.util.Calendar;
import java.util.TimeZone;
import java.util.Date;
import java.io.Serializable;

public class Minute extends RegularTimePeriod implements Serializable
{
    private static final long serialVersionUID = 2144572840034842871L;
    public static final int FIRST_MINUTE_IN_HOUR = 0;
    public static final int LAST_MINUTE_IN_HOUR = 59;
    private Hour hour;
    private int minute;
    
    public Minute() {
        this(new Date());
    }
    
    public Minute(final int minute, final Hour hour) {
        if (hour == null) {
            throw new IllegalArgumentException("Null 'hour' argument.");
        }
        this.minute = minute;
        this.hour = hour;
    }
    
    public Minute(final Date time) {
        this(time, RegularTimePeriod.DEFAULT_TIME_ZONE);
    }
    
    public Minute(final Date time, final TimeZone zone) {
        if (time == null) {
            throw new IllegalArgumentException("Null 'time' argument.");
        }
        if (zone == null) {
            throw new IllegalArgumentException("Null 'zone' argument.");
        }
        final Calendar calendar = Calendar.getInstance(zone);
        calendar.setTime(time);
        final int min = calendar.get(12);
        this.minute = min;
        this.hour = new Hour(time, zone);
    }
    
    public Minute(final int minute, final int hour, final int day, final int month, final int year) {
        this(minute, new Hour(hour, new Day(day, month, year)));
    }
    
    public Hour getHour() {
        return this.hour;
    }
    
    public int getMinute() {
        return this.minute;
    }
    
    public RegularTimePeriod previous() {
        Minute result;
        if (this.minute != 0) {
            result = new Minute(this.minute - 1, this.hour);
        }
        else {
            final Hour prevHour = (Hour)this.hour.previous();
            if (prevHour != null) {
                result = new Minute(59, prevHour);
            }
            else {
                result = null;
            }
        }
        return result;
    }
    
    public RegularTimePeriod next() {
        Minute result;
        if (this.minute != 59) {
            result = new Minute(this.minute + 1, this.hour);
        }
        else {
            final Hour nextHour = (Hour)this.hour.next();
            if (nextHour != null) {
                result = new Minute(0, nextHour);
            }
            else {
                result = null;
            }
        }
        return result;
    }
    
    public long getSerialIndex() {
        return this.hour.getSerialIndex() * 60L + this.minute;
    }
    
    public long getFirstMillisecond(final Calendar calendar) {
        final int year = this.hour.getDay().getYear();
        final int month = this.hour.getDay().getMonth() - 1;
        final int day = this.hour.getDay().getDayOfMonth();
        calendar.clear();
        calendar.set(year, month, day, this.hour.getHour(), this.minute, 0);
        calendar.set(14, 0);
        return calendar.getTime().getTime();
    }
    
    public long getLastMillisecond(final Calendar calendar) {
        final int year = this.hour.getDay().getYear();
        final int month = this.hour.getDay().getMonth() - 1;
        final int day = this.hour.getDay().getDayOfMonth();
        calendar.clear();
        calendar.set(year, month, day, this.hour.getHour(), this.minute, 59);
        calendar.set(14, 999);
        return calendar.getTime().getTime();
    }
    
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Minute)) {
            return false;
        }
        final Minute that = (Minute)obj;
        return this.minute == that.minute && this.hour.equals(that.hour);
    }
    
    public int hashCode() {
        int result = 17;
        result = 37 * result + this.minute;
        result = 37 * result + this.hour.hashCode();
        return result;
    }
    
    public int compareTo(final Object o1) {
        int result;
        if (o1 instanceof Minute) {
            final Minute m = (Minute)o1;
            result = this.getHour().compareTo(m.getHour());
            if (result == 0) {
                result = this.minute - m.getMinute();
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
    
    public static Minute parseMinute(String s) {
        Minute result = null;
        s = s.trim();
        final String daystr = s.substring(0, Math.min(10, s.length()));
        final Day day = Day.parseDay(daystr);
        if (day != null) {
            String hmstr = s.substring(Math.min(daystr.length() + 1, s.length()), s.length());
            hmstr = hmstr.trim();
            final String hourstr = hmstr.substring(0, Math.min(2, hmstr.length()));
            final int hour = Integer.parseInt(hourstr);
            if (hour >= 0 && hour <= 23) {
                final String minstr = hmstr.substring(Math.min(hourstr.length() + 1, hmstr.length()), hmstr.length());
                final int minute = Integer.parseInt(minstr);
                if (minute >= 0 && minute <= 59) {
                    result = new Minute(minute, new Hour(hour, day));
                }
            }
        }
        return result;
    }
}
