package org.jfree.data.time;

import java.util.Calendar;
import java.util.TimeZone;
import java.util.Date;
import java.io.Serializable;

public class Hour extends RegularTimePeriod implements Serializable
{
    private static final long serialVersionUID = -835471579831937652L;
    public static final int FIRST_HOUR_IN_DAY = 0;
    public static final int LAST_HOUR_IN_DAY = 23;
    private Day day;
    private int hour;
    
    public Hour() {
        this(new Date());
    }
    
    public Hour(final int hour, final Day day) {
        if (day == null) {
            throw new IllegalArgumentException("Null 'day' argument.");
        }
        this.hour = hour;
        this.day = day;
    }
    
    public Hour(final int hour, final int day, final int month, final int year) {
        this(hour, new Day(day, month, year));
    }
    
    public Hour(final Date time) {
        this(time, RegularTimePeriod.DEFAULT_TIME_ZONE);
    }
    
    public Hour(final Date time, final TimeZone zone) {
        if (time == null) {
            throw new IllegalArgumentException("Null 'time' argument.");
        }
        if (zone == null) {
            throw new IllegalArgumentException("Null 'zone' argument.");
        }
        final Calendar calendar = Calendar.getInstance(zone);
        calendar.setTime(time);
        this.hour = calendar.get(11);
        this.day = new Day(time, zone);
    }
    
    public int getHour() {
        return this.hour;
    }
    
    public Day getDay() {
        return this.day;
    }
    
    public int getYear() {
        return this.day.getYear();
    }
    
    public int getMonth() {
        return this.day.getMonth();
    }
    
    public int getDayOfMonth() {
        return this.day.getDayOfMonth();
    }
    
    public RegularTimePeriod previous() {
        Hour result;
        if (this.hour != 0) {
            result = new Hour(this.hour - 1, this.day);
        }
        else {
            final Day prevDay = (Day)this.day.previous();
            if (prevDay != null) {
                result = new Hour(23, prevDay);
            }
            else {
                result = null;
            }
        }
        return result;
    }
    
    public RegularTimePeriod next() {
        Hour result;
        if (this.hour != 23) {
            result = new Hour(this.hour + 1, this.day);
        }
        else {
            final Day nextDay = (Day)this.day.next();
            if (nextDay != null) {
                result = new Hour(0, nextDay);
            }
            else {
                result = null;
            }
        }
        return result;
    }
    
    public long getSerialIndex() {
        return this.day.getSerialIndex() * 24L + this.hour;
    }
    
    public long getFirstMillisecond(final Calendar calendar) {
        final int year = this.day.getYear();
        final int month = this.day.getMonth() - 1;
        final int dom = this.day.getDayOfMonth();
        calendar.set(year, month, dom, this.hour, 0, 0);
        calendar.set(14, 0);
        return calendar.getTime().getTime();
    }
    
    public long getLastMillisecond(final Calendar calendar) {
        final int year = this.day.getYear();
        final int month = this.day.getMonth() - 1;
        final int dom = this.day.getDayOfMonth();
        calendar.set(year, month, dom, this.hour, 59, 59);
        calendar.set(14, 999);
        return calendar.getTime().getTime();
    }
    
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Hour)) {
            return false;
        }
        final Hour that = (Hour)obj;
        return this.hour == that.hour && this.day.equals(that.day);
    }
    
    public int hashCode() {
        int result = 17;
        result = 37 * result + this.hour;
        result = 37 * result + this.day.hashCode();
        return result;
    }
    
    public int compareTo(final Object o1) {
        int result;
        if (o1 instanceof Hour) {
            final Hour h = (Hour)o1;
            result = this.getDay().compareTo(h.getDay());
            if (result == 0) {
                result = this.hour - h.getHour();
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
    
    public static Hour parseHour(String s) {
        Hour result = null;
        s = s.trim();
        final String daystr = s.substring(0, Math.min(10, s.length()));
        final Day day = Day.parseDay(daystr);
        if (day != null) {
            String hourstr = s.substring(Math.min(daystr.length() + 1, s.length()), s.length());
            hourstr = hourstr.trim();
            final int hour = Integer.parseInt(hourstr);
            if (hour >= 0 && hour <= 23) {
                result = new Hour(hour, day);
            }
        }
        return result;
    }
}
