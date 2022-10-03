package org.apache.commons.validator.routines;

import java.text.DateFormat;
import java.text.Format;
import java.util.TimeZone;
import java.util.Locale;
import java.util.Calendar;

public class CalendarValidator extends AbstractCalendarValidator
{
    private static final CalendarValidator VALIDATOR;
    
    public static CalendarValidator getInstance() {
        return CalendarValidator.VALIDATOR;
    }
    
    public CalendarValidator() {
        this(true, 3);
    }
    
    public CalendarValidator(final boolean strict, final int dateStyle) {
        super(strict, dateStyle, -1);
    }
    
    public Calendar validate(final String value) {
        return (Calendar)this.parse(value, null, null, null);
    }
    
    public Calendar validate(final String value, final TimeZone timeZone) {
        return (Calendar)this.parse(value, null, null, timeZone);
    }
    
    public Calendar validate(final String value, final String pattern) {
        return (Calendar)this.parse(value, pattern, null, null);
    }
    
    public Calendar validate(final String value, final String pattern, final TimeZone timeZone) {
        return (Calendar)this.parse(value, pattern, null, timeZone);
    }
    
    public Calendar validate(final String value, final Locale locale) {
        return (Calendar)this.parse(value, null, locale, null);
    }
    
    public Calendar validate(final String value, final Locale locale, final TimeZone timeZone) {
        return (Calendar)this.parse(value, null, locale, timeZone);
    }
    
    public Calendar validate(final String value, final String pattern, final Locale locale) {
        return (Calendar)this.parse(value, pattern, locale, null);
    }
    
    public Calendar validate(final String value, final String pattern, final Locale locale, final TimeZone timeZone) {
        return (Calendar)this.parse(value, pattern, locale, timeZone);
    }
    
    public static void adjustToTimeZone(final Calendar value, final TimeZone timeZone) {
        if (value.getTimeZone().hasSameRules(timeZone)) {
            value.setTimeZone(timeZone);
        }
        else {
            final int year = value.get(1);
            final int month = value.get(2);
            final int date = value.get(5);
            final int hour = value.get(11);
            final int minute = value.get(12);
            value.setTimeZone(timeZone);
            value.set(year, month, date, hour, minute);
        }
    }
    
    public int compareDates(final Calendar value, final Calendar compare) {
        return this.compare(value, compare, 5);
    }
    
    public int compareWeeks(final Calendar value, final Calendar compare) {
        return this.compare(value, compare, 3);
    }
    
    public int compareMonths(final Calendar value, final Calendar compare) {
        return this.compare(value, compare, 2);
    }
    
    public int compareQuarters(final Calendar value, final Calendar compare) {
        return this.compareQuarters(value, compare, 1);
    }
    
    public int compareQuarters(final Calendar value, final Calendar compare, final int monthOfFirstQuarter) {
        return super.compareQuarters(value, compare, monthOfFirstQuarter);
    }
    
    public int compareYears(final Calendar value, final Calendar compare) {
        return this.compare(value, compare, 1);
    }
    
    protected Object processParsedValue(final Object value, final Format formatter) {
        return ((DateFormat)formatter).getCalendar();
    }
    
    static {
        VALIDATOR = new CalendarValidator();
    }
}
