package org.apache.commons.validator.routines;

import java.text.DateFormat;
import java.text.Format;
import java.util.TimeZone;
import java.util.Locale;
import java.util.Calendar;

public class TimeValidator extends AbstractCalendarValidator
{
    private static final TimeValidator VALIDATOR;
    
    public static TimeValidator getInstance() {
        return TimeValidator.VALIDATOR;
    }
    
    public TimeValidator() {
        this(true, 3);
    }
    
    public TimeValidator(final boolean strict, final int timeStyle) {
        super(strict, -1, timeStyle);
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
    
    public int compareTime(final Calendar value, final Calendar compare) {
        return this.compareTime(value, compare, 14);
    }
    
    public int compareSeconds(final Calendar value, final Calendar compare) {
        return this.compareTime(value, compare, 13);
    }
    
    public int compareMinutes(final Calendar value, final Calendar compare) {
        return this.compareTime(value, compare, 12);
    }
    
    public int compareHours(final Calendar value, final Calendar compare) {
        return this.compareTime(value, compare, 11);
    }
    
    protected Object processParsedValue(final Object value, final Format formatter) {
        return ((DateFormat)formatter).getCalendar();
    }
    
    static {
        VALIDATOR = new TimeValidator();
    }
}
