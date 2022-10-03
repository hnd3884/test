package org.jfree.data.time;

import org.jfree.date.SerialDate;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.Date;
import java.io.Serializable;

public class Quarter extends RegularTimePeriod implements Serializable
{
    private static final long serialVersionUID = 3810061714380888671L;
    public static final int FIRST_QUARTER = 1;
    public static final int LAST_QUARTER = 4;
    public static final int[] FIRST_MONTH_IN_QUARTER;
    public static final int[] LAST_MONTH_IN_QUARTER;
    private Year year;
    private int quarter;
    
    public Quarter() {
        this(new Date());
    }
    
    public Quarter(final int quarter, final int year) {
        this(quarter, new Year(year));
    }
    
    public Quarter(final int quarter, final Year year) {
        if (quarter < 1 || quarter > 4) {
            throw new IllegalArgumentException("Quarter outside valid range.");
        }
        this.year = year;
        this.quarter = quarter;
    }
    
    public Quarter(final Date time) {
        this(time, RegularTimePeriod.DEFAULT_TIME_ZONE);
    }
    
    public Quarter(final Date time, final TimeZone zone) {
        final Calendar calendar = Calendar.getInstance(zone);
        calendar.setTime(time);
        final int month = calendar.get(2) + 1;
        this.quarter = SerialDate.monthCodeToQuarter(month);
        this.year = new Year(calendar.get(1));
    }
    
    public int getQuarter() {
        return this.quarter;
    }
    
    public Year getYear() {
        return this.year;
    }
    
    public RegularTimePeriod previous() {
        Quarter result;
        if (this.quarter > 1) {
            result = new Quarter(this.quarter - 1, this.year);
        }
        else {
            final Year prevYear = (Year)this.year.previous();
            if (prevYear != null) {
                result = new Quarter(4, prevYear);
            }
            else {
                result = null;
            }
        }
        return result;
    }
    
    public RegularTimePeriod next() {
        Quarter result;
        if (this.quarter < 4) {
            result = new Quarter(this.quarter + 1, this.year);
        }
        else {
            final Year nextYear = (Year)this.year.next();
            if (nextYear != null) {
                result = new Quarter(1, nextYear);
            }
            else {
                result = null;
            }
        }
        return result;
    }
    
    public long getSerialIndex() {
        return this.year.getYear() * 4L + this.quarter;
    }
    
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj instanceof Quarter) {
            final Quarter target = (Quarter)obj;
            return this.quarter == target.getQuarter() && this.year.equals(target.getYear());
        }
        return false;
    }
    
    public int hashCode() {
        int result = 17;
        result = 37 * result + this.quarter;
        result = 37 * result + this.year.hashCode();
        return result;
    }
    
    public int compareTo(final Object o1) {
        int result;
        if (o1 instanceof Quarter) {
            final Quarter q = (Quarter)o1;
            result = this.year.getYear() - q.getYear().getYear();
            if (result == 0) {
                result = this.quarter - q.getQuarter();
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
    
    public String toString() {
        return "Q" + this.quarter + "/" + this.year;
    }
    
    public long getFirstMillisecond(final Calendar calendar) {
        final int month = Quarter.FIRST_MONTH_IN_QUARTER[this.quarter];
        final Day first = new Day(1, month, this.year.getYear());
        return first.getFirstMillisecond(calendar);
    }
    
    public long getLastMillisecond(final Calendar calendar) {
        final int month = Quarter.LAST_MONTH_IN_QUARTER[this.quarter];
        final int eom = SerialDate.lastDayOfMonth(month, this.year.getYear());
        final Day last = new Day(eom, month, this.year.getYear());
        return last.getLastMillisecond(calendar);
    }
    
    public static Quarter parseQuarter(final String s) {
        final int i = s.indexOf("Q");
        if (i == -1) {
            throw new TimePeriodFormatException("Missing Q.");
        }
        if (i == s.length() - 1) {
            throw new TimePeriodFormatException("Q found at end of string.");
        }
        final String qstr = s.substring(i + 1, i + 2);
        final int quarter = Integer.parseInt(qstr);
        String remaining = s.substring(0, i) + s.substring(i + 2, s.length());
        remaining = remaining.replace('/', ' ');
        remaining = remaining.replace(',', ' ');
        remaining = remaining.replace('-', ' ');
        final Year year = Year.parseYear(remaining.trim());
        final Quarter result = new Quarter(quarter, year);
        return result;
    }
    
    static {
        FIRST_MONTH_IN_QUARTER = new int[] { 0, 1, 4, 7, 10 };
        LAST_MONTH_IN_QUARTER = new int[] { 0, 3, 6, 9, 12 };
    }
}
