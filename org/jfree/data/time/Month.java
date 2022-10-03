package org.jfree.data.time;

import org.jfree.date.SerialDate;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.Date;
import java.io.Serializable;

public class Month extends RegularTimePeriod implements Serializable
{
    private static final long serialVersionUID = -5090216912548722570L;
    private int month;
    private Year year;
    
    public Month() {
        this(new Date());
    }
    
    public Month(final int month, final int year) {
        this(month, new Year(year));
    }
    
    public Month(final int month, final Year year) {
        if (month < 1 || month > 12) {
            throw new IllegalArgumentException("Month outside valid range.");
        }
        this.month = month;
        this.year = year;
    }
    
    public Month(final Date time) {
        this(time, RegularTimePeriod.DEFAULT_TIME_ZONE);
    }
    
    public Month(final Date time, final TimeZone zone) {
        final Calendar calendar = Calendar.getInstance(zone);
        calendar.setTime(time);
        this.month = calendar.get(2) + 1;
        this.year = new Year(calendar.get(1));
    }
    
    public Year getYear() {
        return this.year;
    }
    
    public int getYearValue() {
        return this.year.getYear();
    }
    
    public int getMonth() {
        return this.month;
    }
    
    public RegularTimePeriod previous() {
        Month result;
        if (this.month != 1) {
            result = new Month(this.month - 1, this.year);
        }
        else {
            final Year prevYear = (Year)this.year.previous();
            if (prevYear != null) {
                result = new Month(12, prevYear);
            }
            else {
                result = null;
            }
        }
        return result;
    }
    
    public RegularTimePeriod next() {
        Month result;
        if (this.month != 12) {
            result = new Month(this.month + 1, this.year);
        }
        else {
            final Year nextYear = (Year)this.year.next();
            if (nextYear != null) {
                result = new Month(1, nextYear);
            }
            else {
                result = null;
            }
        }
        return result;
    }
    
    public long getSerialIndex() {
        return this.year.getYear() * 12L + this.month;
    }
    
    public String toString() {
        return SerialDate.monthCodeToString(this.month) + " " + this.year;
    }
    
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj instanceof Month) {
            final Month target = (Month)obj;
            return this.month == target.getMonth() && this.year.equals(target.getYear());
        }
        return false;
    }
    
    public int hashCode() {
        int result = 17;
        result = 37 * result + this.month;
        result = 37 * result + this.year.hashCode();
        return result;
    }
    
    public int compareTo(final Object o1) {
        int result;
        if (o1 instanceof Month) {
            final Month m = (Month)o1;
            result = this.year.getYear() - m.getYear().getYear();
            if (result == 0) {
                result = this.month - m.getMonth();
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
    
    public long getFirstMillisecond(final Calendar calendar) {
        final Day first = new Day(1, this.month, this.year.getYear());
        return first.getFirstMillisecond(calendar);
    }
    
    public long getLastMillisecond(final Calendar calendar) {
        final int eom = SerialDate.lastDayOfMonth(this.month, this.year.getYear());
        final Day last = new Day(eom, this.month, this.year.getYear());
        return last.getLastMillisecond(calendar);
    }
    
    public static Month parseMonth(String s) {
        Month result = null;
        if (s != null) {
            s = s.trim();
            final int i = findSeparator(s);
            if (i == -1) {
                throw new TimePeriodFormatException("Could not find separator.");
            }
            final String s2 = s.substring(0, i).trim();
            final String s3 = s.substring(i + 1, s.length()).trim();
            Year year = evaluateAsYear(s2);
            if (year != null) {
                final int month = SerialDate.stringToMonthCode(s3);
                if (month == -1) {
                    throw new TimePeriodFormatException("Can't evaluate the month.");
                }
                result = new Month(month, year);
            }
            else {
                year = evaluateAsYear(s3);
                if (year == null) {
                    throw new TimePeriodFormatException("Can't evaluate the year.");
                }
                final int month = SerialDate.stringToMonthCode(s2);
                if (month == -1) {
                    throw new TimePeriodFormatException("Can't evaluate the month.");
                }
                result = new Month(month, year);
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
}
