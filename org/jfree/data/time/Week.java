package org.jfree.data.time;

import java.util.Calendar;
import java.util.TimeZone;
import java.util.Date;
import java.io.Serializable;

public class Week extends RegularTimePeriod implements Serializable
{
    private static final long serialVersionUID = 1856387786939865061L;
    public static final int FIRST_WEEK_IN_YEAR = 1;
    public static final int LAST_WEEK_IN_YEAR = 53;
    private Year year;
    private int week;
    
    public Week() {
        this(new Date());
    }
    
    public Week(final int week, final int year) {
        this(week, new Year(year));
    }
    
    public Week(final int week, final Year year) {
        if (week < 1 && week > 53) {
            throw new IllegalArgumentException("The 'week' argument must be in the range 1 - 53.");
        }
        this.week = week;
        this.year = year;
    }
    
    public Week(final Date time) {
        this(time, RegularTimePeriod.DEFAULT_TIME_ZONE);
    }
    
    public Week(final Date time, final TimeZone zone) {
        if (time == null) {
            throw new IllegalArgumentException("Null 'time' argument.");
        }
        if (zone == null) {
            throw new IllegalArgumentException("Null 'zone' argument.");
        }
        final Calendar calendar = Calendar.getInstance(zone);
        calendar.setTime(time);
        final int tempWeek = calendar.get(3);
        if (tempWeek == 1 && calendar.get(2) == 11) {
            this.week = 1;
            this.year = new Year(calendar.get(1) + 1);
        }
        else {
            this.week = Math.min(tempWeek, 53);
            int yyyy = calendar.get(1);
            if (calendar.get(2) == 0 && this.week >= 52) {
                --yyyy;
            }
            this.year = new Year(yyyy);
        }
    }
    
    public Year getYear() {
        return this.year;
    }
    
    public int getYearValue() {
        return this.year.getYear();
    }
    
    public int getWeek() {
        return this.week;
    }
    
    public RegularTimePeriod previous() {
        Week result;
        if (this.week != 1) {
            result = new Week(this.week - 1, this.year);
        }
        else {
            final Year prevYear = (Year)this.year.previous();
            if (prevYear != null) {
                final int yy = prevYear.getYear();
                final Calendar prevYearCalendar = Calendar.getInstance();
                prevYearCalendar.set(yy, 11, 31);
                result = new Week(prevYearCalendar.getActualMaximum(3), prevYear);
            }
            else {
                result = null;
            }
        }
        return result;
    }
    
    public RegularTimePeriod next() {
        Week result;
        if (this.week < 52) {
            result = new Week(this.week + 1, this.year);
        }
        else {
            final Calendar calendar = Calendar.getInstance();
            calendar.set(this.year.getYear(), 11, 31);
            final int actualMaxWeek = calendar.getActualMaximum(3);
            if (this.week != actualMaxWeek) {
                result = new Week(this.week + 1, this.year);
            }
            else {
                final Year nextYear = (Year)this.year.next();
                if (nextYear != null) {
                    result = new Week(1, nextYear);
                }
                else {
                    result = null;
                }
            }
        }
        return result;
    }
    
    public long getSerialIndex() {
        return this.year.getYear() * 53L + this.week;
    }
    
    public long getFirstMillisecond(final Calendar calendar) {
        final Calendar c = (Calendar)calendar.clone();
        c.clear();
        c.set(1, this.year.getYear());
        c.set(3, this.week);
        c.set(7, c.getFirstDayOfWeek());
        c.set(10, 0);
        c.set(12, 0);
        c.set(13, 0);
        c.set(14, 0);
        return c.getTime().getTime();
    }
    
    public long getLastMillisecond(final Calendar calendar) {
        final RegularTimePeriod next = this.next();
        return next.getFirstMillisecond(calendar) - 1L;
    }
    
    public String toString() {
        return "Week " + this.week + ", " + this.year;
    }
    
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Week)) {
            return false;
        }
        final Week that = (Week)obj;
        return this.week == that.week && this.year.equals(that.year);
    }
    
    public int hashCode() {
        int result = 17;
        result = 37 * result + this.week;
        result = 37 * result + this.year.hashCode();
        return result;
    }
    
    public int compareTo(final Object o1) {
        int result;
        if (o1 instanceof Week) {
            final Week w = (Week)o1;
            result = this.year.getYear() - w.getYear().getYear();
            if (result == 0) {
                result = this.week - w.getWeek();
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
    
    public static Week parseWeek(String s) {
        Week result = null;
        if (s != null) {
            s = s.trim();
            final int i = findSeparator(s);
            if (i == -1) {
                throw new TimePeriodFormatException("Could not find separator.");
            }
            final String s2 = s.substring(0, i).trim();
            final String s3 = s.substring(i + 1, s.length()).trim();
            Year y = evaluateAsYear(s2);
            if (y != null) {
                final int w = stringToWeek(s3);
                if (w == -1) {
                    throw new TimePeriodFormatException("Can't evaluate the week.");
                }
                result = new Week(w, y);
            }
            else {
                y = evaluateAsYear(s3);
                if (y == null) {
                    throw new TimePeriodFormatException("Can't evaluate the year.");
                }
                final int w = stringToWeek(s2);
                if (w == -1) {
                    throw new TimePeriodFormatException("Can't evaluate the week.");
                }
                result = new Week(w, y);
            }
        }
        return result;
    }
    
    private static int findSeparator(final String s) {
        int result = s.indexOf(45);
        if (result == -1) {
            result = s.indexOf(44);
        }
        if (result == -1) {
            result = s.indexOf(32);
        }
        if (result == -1) {
            result = s.indexOf(46);
        }
        return result;
    }
    
    private static Year evaluateAsYear(final String s) {
        Year result = null;
        try {
            result = Year.parseYear(s);
        }
        catch (final TimePeriodFormatException ex) {}
        return result;
    }
    
    private static int stringToWeek(String s) {
        int result = -1;
        s = s.replace('W', ' ');
        s = s.trim();
        try {
            result = Integer.parseInt(s);
            if (result < 1 || result > 53) {
                result = -1;
            }
        }
        catch (final NumberFormatException ex) {}
        return result;
    }
}
