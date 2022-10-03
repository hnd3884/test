package org.jfree.chart.axis;

import org.jfree.util.ObjectUtilities;
import java.util.Calendar;
import java.util.Date;
import java.text.DateFormat;
import java.io.Serializable;

public class DateTickUnit extends TickUnit implements Serializable
{
    private static final long serialVersionUID = -7289292157229621901L;
    public static final int YEAR = 0;
    public static final int MONTH = 1;
    public static final int DAY = 2;
    public static final int HOUR = 3;
    public static final int MINUTE = 4;
    public static final int SECOND = 5;
    public static final int MILLISECOND = 6;
    private int unit;
    private int count;
    private int rollUnit;
    private int rollCount;
    private DateFormat formatter;
    
    public DateTickUnit(final int unit, final int count) {
        this(unit, count, null);
    }
    
    public DateTickUnit(final int unit, final int count, final DateFormat formatter) {
        this(unit, count, unit, count, formatter);
    }
    
    public DateTickUnit(final int unit, final int count, final int rollUnit, final int rollCount, final DateFormat formatter) {
        super((double)getMillisecondCount(unit, count));
        this.unit = unit;
        this.count = count;
        this.rollUnit = rollUnit;
        this.rollCount = rollCount;
        this.formatter = formatter;
        if (formatter == null) {
            this.formatter = DateFormat.getDateInstance(3);
        }
    }
    
    public int getUnit() {
        return this.unit;
    }
    
    public int getCount() {
        return this.count;
    }
    
    public int getRollUnit() {
        return this.rollUnit;
    }
    
    public int getRollCount() {
        return this.rollCount;
    }
    
    public String valueToString(final double milliseconds) {
        return this.formatter.format(new Date((long)milliseconds));
    }
    
    public String dateToString(final Date date) {
        return this.formatter.format(date);
    }
    
    public Date addToDate(final Date base) {
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(base);
        calendar.add(this.getCalendarField(this.unit), this.count);
        return calendar.getTime();
    }
    
    public Date rollDate(final Date base) {
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(base);
        calendar.add(this.getCalendarField(this.rollUnit), this.rollCount);
        return calendar.getTime();
    }
    
    public int getCalendarField() {
        return this.getCalendarField(this.unit);
    }
    
    private int getCalendarField(final int tickUnit) {
        switch (tickUnit) {
            case 0: {
                return 1;
            }
            case 1: {
                return 2;
            }
            case 2: {
                return 5;
            }
            case 3: {
                return 11;
            }
            case 4: {
                return 12;
            }
            case 5: {
                return 13;
            }
            case 6: {
                return 14;
            }
            default: {
                return 14;
            }
        }
    }
    
    private static long getMillisecondCount(final int unit, final int count) {
        switch (unit) {
            case 0: {
                return 31536000000L * count;
            }
            case 1: {
                return 2678400000L * count;
            }
            case 2: {
                return 86400000L * count;
            }
            case 3: {
                return 3600000L * count;
            }
            case 4: {
                return 60000L * count;
            }
            case 5: {
                return 1000L * count;
            }
            case 6: {
                return count;
            }
            default: {
                throw new IllegalArgumentException("DateTickUnit.getMillisecondCount() : unit must be one of the constants YEAR, MONTH, DAY, HOUR, MINUTE, SECOND or MILLISECOND defined in the DateTickUnit class. Do *not* use the constants defined in java.util.Calendar.");
            }
        }
    }
    
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof DateTickUnit)) {
            return false;
        }
        if (!super.equals(obj)) {
            return false;
        }
        final DateTickUnit that = (DateTickUnit)obj;
        return this.unit == that.unit && this.count == that.count && ObjectUtilities.equal((Object)this.formatter, (Object)that.formatter);
    }
    
    public int hashCode() {
        int result = 19;
        result = 37 * result + this.unit;
        result = 37 * result + this.count;
        result = 37 * result + this.formatter.hashCode();
        return result;
    }
}
