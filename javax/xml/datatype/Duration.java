package javax.xml.datatype;

import java.math.BigInteger;
import java.math.BigDecimal;
import java.util.GregorianCalendar;
import java.util.Date;
import java.util.Calendar;
import javax.xml.namespace.QName;

public abstract class Duration
{
    public QName getXMLSchemaType() {
        final boolean set = this.isSet(DatatypeConstants.YEARS);
        final boolean set2 = this.isSet(DatatypeConstants.MONTHS);
        final boolean set3 = this.isSet(DatatypeConstants.DAYS);
        final boolean set4 = this.isSet(DatatypeConstants.HOURS);
        final boolean set5 = this.isSet(DatatypeConstants.MINUTES);
        final boolean set6 = this.isSet(DatatypeConstants.SECONDS);
        if (set && set2 && set3 && set4 && set5 && set6) {
            return DatatypeConstants.DURATION;
        }
        if (!set && !set2 && set3 && set4 && set5 && set6) {
            return DatatypeConstants.DURATION_DAYTIME;
        }
        if (set && set2 && !set3 && !set4 && !set5 && !set6) {
            return DatatypeConstants.DURATION_YEARMONTH;
        }
        throw new IllegalStateException("javax.xml.datatype.Duration#getXMLSchemaType(): this Duration does not match one of the XML Schema date/time datatypes: year set = " + set + " month set = " + set2 + " day set = " + set3 + " hour set = " + set4 + " minute set = " + set5 + " second set = " + set6);
    }
    
    public abstract int getSign();
    
    public int getYears() {
        return this.getFieldValueAsInt(DatatypeConstants.YEARS);
    }
    
    public int getMonths() {
        return this.getFieldValueAsInt(DatatypeConstants.MONTHS);
    }
    
    public int getDays() {
        return this.getFieldValueAsInt(DatatypeConstants.DAYS);
    }
    
    public int getHours() {
        return this.getFieldValueAsInt(DatatypeConstants.HOURS);
    }
    
    public int getMinutes() {
        return this.getFieldValueAsInt(DatatypeConstants.MINUTES);
    }
    
    public int getSeconds() {
        return this.getFieldValueAsInt(DatatypeConstants.SECONDS);
    }
    
    public long getTimeInMillis(final Calendar calendar) {
        final Calendar calendar2 = (Calendar)calendar.clone();
        this.addTo(calendar2);
        return getCalendarTimeInMillis(calendar2) - getCalendarTimeInMillis(calendar);
    }
    
    public long getTimeInMillis(final Date time) {
        final GregorianCalendar gregorianCalendar = new GregorianCalendar();
        gregorianCalendar.setTime(time);
        this.addTo(gregorianCalendar);
        return getCalendarTimeInMillis(gregorianCalendar) - time.getTime();
    }
    
    public abstract Number getField(final DatatypeConstants.Field p0);
    
    private int getFieldValueAsInt(final DatatypeConstants.Field field) {
        final Number field2 = this.getField(field);
        if (field2 != null) {
            return field2.intValue();
        }
        return 0;
    }
    
    public abstract boolean isSet(final DatatypeConstants.Field p0);
    
    public abstract Duration add(final Duration p0);
    
    public abstract void addTo(final Calendar p0);
    
    public void addTo(final Date time) {
        if (time == null) {
            throw new NullPointerException("Cannot call " + this.getClass().getName() + "#addTo(Date date) with date == null.");
        }
        final GregorianCalendar gregorianCalendar = new GregorianCalendar();
        gregorianCalendar.setTime(time);
        this.addTo(gregorianCalendar);
        time.setTime(getCalendarTimeInMillis(gregorianCalendar));
    }
    
    public Duration subtract(final Duration duration) {
        return this.add(duration.negate());
    }
    
    public Duration multiply(final int n) {
        return this.multiply(BigDecimal.valueOf(n));
    }
    
    public abstract Duration multiply(final BigDecimal p0);
    
    public abstract Duration negate();
    
    public abstract Duration normalizeWith(final Calendar p0);
    
    public abstract int compare(final Duration p0);
    
    public boolean isLongerThan(final Duration duration) {
        return this.compare(duration) == 1;
    }
    
    public boolean isShorterThan(final Duration duration) {
        return this.compare(duration) == -1;
    }
    
    public boolean equals(final Object o) {
        return o == this || (o instanceof Duration && this.compare((Duration)o) == 0);
    }
    
    public abstract int hashCode();
    
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        if (this.getSign() < 0) {
            sb.append('-');
        }
        sb.append('P');
        final BigInteger bigInteger = (BigInteger)this.getField(DatatypeConstants.YEARS);
        if (bigInteger != null) {
            sb.append(bigInteger).append('Y');
        }
        final BigInteger bigInteger2 = (BigInteger)this.getField(DatatypeConstants.MONTHS);
        if (bigInteger2 != null) {
            sb.append(bigInteger2).append('M');
        }
        final BigInteger bigInteger3 = (BigInteger)this.getField(DatatypeConstants.DAYS);
        if (bigInteger3 != null) {
            sb.append(bigInteger3).append('D');
        }
        final BigInteger bigInteger4 = (BigInteger)this.getField(DatatypeConstants.HOURS);
        final BigInteger bigInteger5 = (BigInteger)this.getField(DatatypeConstants.MINUTES);
        final BigDecimal bigDecimal = (BigDecimal)this.getField(DatatypeConstants.SECONDS);
        if (bigInteger4 != null || bigInteger5 != null || bigDecimal != null) {
            sb.append('T');
            if (bigInteger4 != null) {
                sb.append(bigInteger4).append('H');
            }
            if (bigInteger5 != null) {
                sb.append(bigInteger5).append('M');
            }
            if (bigDecimal != null) {
                sb.append(this.toString(bigDecimal)).append('S');
            }
        }
        return sb.toString();
    }
    
    private String toString(final BigDecimal bigDecimal) {
        final String string = bigDecimal.unscaledValue().toString();
        final int scale = bigDecimal.scale();
        if (scale == 0) {
            return string;
        }
        final int n = string.length() - scale;
        if (n == 0) {
            return "0." + string;
        }
        StringBuffer sb;
        if (n > 0) {
            sb = new StringBuffer(string);
            sb.insert(n, '.');
        }
        else {
            sb = new StringBuffer(3 - n + string.length());
            sb.append("0.");
            for (int i = 0; i < -n; ++i) {
                sb.append('0');
            }
            sb.append(string);
        }
        return sb.toString();
    }
    
    private static long getCalendarTimeInMillis(final Calendar calendar) {
        return calendar.getTime().getTime();
    }
}
