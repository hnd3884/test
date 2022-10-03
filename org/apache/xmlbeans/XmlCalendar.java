package org.apache.xmlbeans;

import java.util.Calendar;
import java.math.BigDecimal;
import java.util.TimeZone;
import java.util.Date;
import java.util.GregorianCalendar;

public class XmlCalendar extends GregorianCalendar
{
    private static int defaultYear;
    private static final int DEFAULT_DEFAULT_YEAR = 0;
    private static Date _beginningOfTime;
    
    public XmlCalendar(final String xmlSchemaDateString) {
        this(new GDate(xmlSchemaDateString));
    }
    
    public XmlCalendar(final GDateSpecification date) {
        this(GDate.timeZoneForGDate(date), date);
    }
    
    private XmlCalendar(final TimeZone tz, final GDateSpecification date) {
        super(tz);
        this.setGregorianChange(XmlCalendar._beginningOfTime);
        this.clear();
        if (date.hasYear()) {
            int y = date.getYear();
            if (y > 0) {
                this.set(0, 1);
            }
            else {
                this.set(0, 0);
                y = -y;
            }
            this.set(1, y);
        }
        if (date.hasMonth()) {
            this.set(2, date.getMonth() - 1);
        }
        if (date.hasDay()) {
            this.set(5, date.getDay());
        }
        if (date.hasTime()) {
            this.set(11, date.getHour());
            this.set(12, date.getMinute());
            this.set(13, date.getSecond());
            if (date.getFraction().scale() > 0) {
                this.set(14, date.getMillisecond());
            }
        }
        if (date.hasTimeZone()) {
            this.set(15, date.getTimeZoneSign() * 1000 * 60 * (date.getTimeZoneHour() * 60 + date.getTimeZoneMinute()));
            this.set(16, 0);
        }
    }
    
    public XmlCalendar(final Date date) {
        this(TimeZone.getDefault(), new GDate(date));
        this.complete();
    }
    
    public XmlCalendar(final int year, final int month, final int day, final int hour, final int minute, final int second, final BigDecimal fraction) {
        this(TimeZone.getDefault(), new GDate(year, month, day, hour, minute, second, fraction));
    }
    
    public XmlCalendar(final int year, final int month, final int day, final int hour, final int minute, final int second, final BigDecimal fraction, final int tzSign, final int tzHour, final int tzMinute) {
        this(new GDate(year, month, day, hour, minute, second, fraction, tzSign, tzHour, tzMinute));
    }
    
    @Override
    public int get(final int field) {
        if (!this.isSet(field) || this.isTimeSet) {
            return super.get(field);
        }
        return this.internalGet(field);
    }
    
    public XmlCalendar() {
        this.setGregorianChange(XmlCalendar._beginningOfTime);
        this.clear();
    }
    
    public static int getDefaultYear() {
        if (XmlCalendar.defaultYear == Integer.MIN_VALUE) {
            try {
                final String yearstring = SystemProperties.getProperty("user.defaultyear");
                if (yearstring != null) {
                    XmlCalendar.defaultYear = Integer.parseInt(yearstring);
                }
                else {
                    XmlCalendar.defaultYear = 0;
                }
            }
            catch (final Throwable t) {
                XmlCalendar.defaultYear = 0;
            }
        }
        return XmlCalendar.defaultYear;
    }
    
    public static void setDefaultYear(final int year) {
        XmlCalendar.defaultYear = year;
    }
    
    @Override
    protected void computeTime() {
        final boolean unsetYear = !this.isSet(1);
        if (unsetYear) {
            this.set(1, getDefaultYear());
        }
        try {
            super.computeTime();
        }
        finally {
            if (unsetYear) {
                this.clear(1);
            }
        }
    }
    
    @Override
    public String toString() {
        return new GDate(this).toString();
    }
    
    static {
        XmlCalendar.defaultYear = Integer.MIN_VALUE;
        XmlCalendar._beginningOfTime = new Date(Long.MIN_VALUE);
    }
}
