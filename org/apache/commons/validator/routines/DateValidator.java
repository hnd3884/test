package org.apache.commons.validator.routines;

import java.text.Format;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.Locale;
import java.util.Date;

public class DateValidator extends AbstractCalendarValidator
{
    private static final DateValidator VALIDATOR;
    
    public static DateValidator getInstance() {
        return DateValidator.VALIDATOR;
    }
    
    public DateValidator() {
        this(true, 3);
    }
    
    public DateValidator(final boolean strict, final int dateStyle) {
        super(strict, dateStyle, -1);
    }
    
    public Date validate(final String value) {
        return (Date)this.parse(value, null, null, null);
    }
    
    public Date validate(final String value, final TimeZone timeZone) {
        return (Date)this.parse(value, null, null, timeZone);
    }
    
    public Date validate(final String value, final String pattern) {
        return (Date)this.parse(value, pattern, null, null);
    }
    
    public Date validate(final String value, final String pattern, final TimeZone timeZone) {
        return (Date)this.parse(value, pattern, null, timeZone);
    }
    
    public Date validate(final String value, final Locale locale) {
        return (Date)this.parse(value, null, locale, null);
    }
    
    public Date validate(final String value, final Locale locale, final TimeZone timeZone) {
        return (Date)this.parse(value, null, locale, timeZone);
    }
    
    public Date validate(final String value, final String pattern, final Locale locale) {
        return (Date)this.parse(value, pattern, locale, null);
    }
    
    public Date validate(final String value, final String pattern, final Locale locale, final TimeZone timeZone) {
        return (Date)this.parse(value, pattern, locale, timeZone);
    }
    
    public int compareDates(final Date value, final Date compare, final TimeZone timeZone) {
        final Calendar calendarValue = this.getCalendar(value, timeZone);
        final Calendar calendarCompare = this.getCalendar(compare, timeZone);
        return this.compare(calendarValue, calendarCompare, 5);
    }
    
    public int compareWeeks(final Date value, final Date compare, final TimeZone timeZone) {
        final Calendar calendarValue = this.getCalendar(value, timeZone);
        final Calendar calendarCompare = this.getCalendar(compare, timeZone);
        return this.compare(calendarValue, calendarCompare, 3);
    }
    
    public int compareMonths(final Date value, final Date compare, final TimeZone timeZone) {
        final Calendar calendarValue = this.getCalendar(value, timeZone);
        final Calendar calendarCompare = this.getCalendar(compare, timeZone);
        return this.compare(calendarValue, calendarCompare, 2);
    }
    
    public int compareQuarters(final Date value, final Date compare, final TimeZone timeZone) {
        return this.compareQuarters(value, compare, timeZone, 1);
    }
    
    public int compareQuarters(final Date value, final Date compare, final TimeZone timeZone, final int monthOfFirstQuarter) {
        final Calendar calendarValue = this.getCalendar(value, timeZone);
        final Calendar calendarCompare = this.getCalendar(compare, timeZone);
        return super.compareQuarters(calendarValue, calendarCompare, monthOfFirstQuarter);
    }
    
    public int compareYears(final Date value, final Date compare, final TimeZone timeZone) {
        final Calendar calendarValue = this.getCalendar(value, timeZone);
        final Calendar calendarCompare = this.getCalendar(compare, timeZone);
        return this.compare(calendarValue, calendarCompare, 1);
    }
    
    protected Object processParsedValue(final Object value, final Format formatter) {
        return value;
    }
    
    private Calendar getCalendar(final Date value, final TimeZone timeZone) {
        Calendar calendar = null;
        if (timeZone != null) {
            calendar = Calendar.getInstance(timeZone);
        }
        else {
            calendar = Calendar.getInstance();
        }
        calendar.setTime(value);
        return calendar;
    }
    
    static {
        VALIDATOR = new DateValidator();
    }
}
