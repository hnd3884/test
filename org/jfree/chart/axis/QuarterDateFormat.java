package org.jfree.chart.axis;

import java.util.Arrays;
import java.text.ParsePosition;
import java.text.FieldPosition;
import java.util.Date;
import java.text.NumberFormat;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.io.Serializable;
import java.text.DateFormat;

public class QuarterDateFormat extends DateFormat implements Cloneable, Serializable
{
    private static final long serialVersionUID = -6738465248529797176L;
    public static final String[] REGULAR_QUARTERS;
    public static final String[] ROMAN_QUARTERS;
    private String[] quarters;
    
    public QuarterDateFormat() {
        this(TimeZone.getDefault());
    }
    
    public QuarterDateFormat(final TimeZone zone) {
        this(zone, QuarterDateFormat.REGULAR_QUARTERS);
    }
    
    public QuarterDateFormat(final TimeZone zone, final String[] quarterSymbols) {
        this.quarters = QuarterDateFormat.REGULAR_QUARTERS;
        if (zone == null) {
            throw new IllegalArgumentException("Null 'zone' argument.");
        }
        this.calendar = new GregorianCalendar(zone);
        this.quarters = quarterSymbols;
        this.numberFormat = NumberFormat.getNumberInstance();
    }
    
    public StringBuffer format(final Date date, final StringBuffer toAppendTo, final FieldPosition fieldPosition) {
        this.calendar.setTime(date);
        final int year = this.calendar.get(1);
        final int month = this.calendar.get(2);
        toAppendTo.append(year);
        toAppendTo.append(" ");
        final int quarter = month / 3;
        toAppendTo.append(this.quarters[quarter]);
        return toAppendTo;
    }
    
    public Date parse(final String source, final ParsePosition pos) {
        return null;
    }
    
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof QuarterDateFormat)) {
            return false;
        }
        if (!super.equals(obj)) {
            return false;
        }
        final QuarterDateFormat that = (QuarterDateFormat)obj;
        return Arrays.equals(this.quarters, that.quarters);
    }
    
    static {
        REGULAR_QUARTERS = new String[] { "1", "2", "3", "4" };
        ROMAN_QUARTERS = new String[] { "I", "II", "III", "IV" };
    }
}
