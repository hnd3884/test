package org.apache.commons.validator.routines;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.text.Format;
import java.util.Calendar;
import java.text.DateFormat;
import java.util.TimeZone;
import java.util.Locale;

public abstract class AbstractCalendarValidator extends AbstractFormatValidator
{
    private int dateStyle;
    private int timeStyle;
    
    public AbstractCalendarValidator(final boolean strict, final int dateStyle, final int timeStyle) {
        super(strict);
        this.dateStyle = -1;
        this.timeStyle = -1;
        this.dateStyle = dateStyle;
        this.timeStyle = timeStyle;
    }
    
    public boolean isValid(final String value, final String pattern, final Locale locale) {
        final Object parsedValue = this.parse(value, pattern, locale, null);
        return parsedValue != null;
    }
    
    public String format(final Object value, final TimeZone timeZone) {
        return this.format(value, null, null, timeZone);
    }
    
    public String format(final Object value, final String pattern, final TimeZone timeZone) {
        return this.format(value, pattern, null, timeZone);
    }
    
    public String format(final Object value, final Locale locale, final TimeZone timeZone) {
        return this.format(value, null, locale, timeZone);
    }
    
    public String format(final Object value, final String pattern, final Locale locale) {
        return this.format(value, pattern, locale, null);
    }
    
    public String format(final Object value, final String pattern, final Locale locale, final TimeZone timeZone) {
        final DateFormat formatter = (DateFormat)this.getFormat(pattern, locale);
        if (timeZone != null) {
            formatter.setTimeZone(timeZone);
        }
        else if (value instanceof Calendar) {
            formatter.setTimeZone(((Calendar)value).getTimeZone());
        }
        return this.format(value, formatter);
    }
    
    protected String format(Object value, final Format formatter) {
        if (value == null) {
            return null;
        }
        if (value instanceof Calendar) {
            value = ((Calendar)value).getTime();
        }
        return formatter.format(value);
    }
    
    protected Object parse(String value, final String pattern, final Locale locale, final TimeZone timeZone) {
        value = ((value == null) ? null : value.trim());
        if (value == null || value.length() == 0) {
            return null;
        }
        final DateFormat formatter = (DateFormat)this.getFormat(pattern, locale);
        if (timeZone != null) {
            formatter.setTimeZone(timeZone);
        }
        return this.parse(value, formatter);
    }
    
    protected abstract Object processParsedValue(final Object p0, final Format p1);
    
    protected Format getFormat(final String pattern, final Locale locale) {
        DateFormat formatter = null;
        final boolean usePattern = pattern != null && pattern.length() > 0;
        if (!usePattern) {
            formatter = (DateFormat)this.getFormat(locale);
        }
        else if (locale == null) {
            formatter = new SimpleDateFormat(pattern);
        }
        else {
            final DateFormatSymbols symbols = new DateFormatSymbols(locale);
            formatter = new SimpleDateFormat(pattern, symbols);
        }
        formatter.setLenient(false);
        return formatter;
    }
    
    protected Format getFormat(final Locale locale) {
        DateFormat formatter = null;
        if (this.dateStyle >= 0 && this.timeStyle >= 0) {
            if (locale == null) {
                formatter = DateFormat.getDateTimeInstance(this.dateStyle, this.timeStyle);
            }
            else {
                formatter = DateFormat.getDateTimeInstance(this.dateStyle, this.timeStyle, locale);
            }
        }
        else if (this.timeStyle >= 0) {
            if (locale == null) {
                formatter = DateFormat.getTimeInstance(this.timeStyle);
            }
            else {
                formatter = DateFormat.getTimeInstance(this.timeStyle, locale);
            }
        }
        else {
            final int useDateStyle = (this.dateStyle >= 0) ? this.dateStyle : 3;
            if (locale == null) {
                formatter = DateFormat.getDateInstance(useDateStyle);
            }
            else {
                formatter = DateFormat.getDateInstance(useDateStyle, locale);
            }
        }
        formatter.setLenient(false);
        return formatter;
    }
    
    protected int compare(final Calendar value, final Calendar compare, final int field) {
        int result = 0;
        result = this.calculateCompareResult(value, compare, 1);
        if (result != 0 || field == 1) {
            return result;
        }
        if (field == 3) {
            return this.calculateCompareResult(value, compare, 3);
        }
        if (field == 6) {
            return this.calculateCompareResult(value, compare, 6);
        }
        result = this.calculateCompareResult(value, compare, 2);
        if (result != 0 || field == 2) {
            return result;
        }
        if (field == 4) {
            return this.calculateCompareResult(value, compare, 4);
        }
        result = this.calculateCompareResult(value, compare, 5);
        if (result != 0 || field == 5 || field == 5 || field == 7 || field == 8) {
            return result;
        }
        return this.compareTime(value, compare, field);
    }
    
    protected int compareTime(final Calendar value, final Calendar compare, final int field) {
        int result = 0;
        result = this.calculateCompareResult(value, compare, 11);
        if (result != 0 || field == 10 || field == 11) {
            return result;
        }
        result = this.calculateCompareResult(value, compare, 12);
        if (result != 0 || field == 12) {
            return result;
        }
        result = this.calculateCompareResult(value, compare, 13);
        if (result != 0 || field == 13) {
            return result;
        }
        if (field == 14) {
            return this.calculateCompareResult(value, compare, 14);
        }
        throw new IllegalArgumentException("Invalid field: " + field);
    }
    
    protected int compareQuarters(final Calendar value, final Calendar compare, final int monthOfFirstQuarter) {
        final int valueQuarter = this.calculateQuarter(value, monthOfFirstQuarter);
        final int compareQuarter = this.calculateQuarter(compare, monthOfFirstQuarter);
        if (valueQuarter < compareQuarter) {
            return -1;
        }
        if (valueQuarter > compareQuarter) {
            return 1;
        }
        return 0;
    }
    
    private int calculateQuarter(final Calendar calendar, final int monthOfFirstQuarter) {
        int year = calendar.get(1);
        final int month = calendar.get(2) + 1;
        final int relativeMonth = (month >= monthOfFirstQuarter) ? (month - monthOfFirstQuarter) : (month + (12 - monthOfFirstQuarter));
        final int quarter = relativeMonth / 3 + 1;
        if (month < monthOfFirstQuarter) {
            --year;
        }
        return year * 10 + quarter;
    }
    
    private int calculateCompareResult(final Calendar value, final Calendar compare, final int field) {
        final int difference = value.get(field) - compare.get(field);
        if (difference < 0) {
            return -1;
        }
        if (difference > 0) {
            return 1;
        }
        return 0;
    }
}
