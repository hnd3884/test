package org.apache.commons.validator;

import java.text.DateFormat;
import java.util.Locale;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class DateValidator
{
    private static final DateValidator DATE_VALIDATOR;
    
    public static DateValidator getInstance() {
        return DateValidator.DATE_VALIDATOR;
    }
    
    protected DateValidator() {
    }
    
    public boolean isValid(final String value, final String datePattern, final boolean strict) {
        if (value == null || datePattern == null || datePattern.length() <= 0) {
            return false;
        }
        final SimpleDateFormat formatter = new SimpleDateFormat(datePattern);
        formatter.setLenient(false);
        try {
            formatter.parse(value);
        }
        catch (final ParseException e) {
            return false;
        }
        return !strict || datePattern.length() == value.length();
    }
    
    public boolean isValid(final String value, final Locale locale) {
        if (value == null) {
            return false;
        }
        DateFormat formatter = null;
        if (locale != null) {
            formatter = DateFormat.getDateInstance(3, locale);
        }
        else {
            formatter = DateFormat.getDateInstance(3, Locale.getDefault());
        }
        formatter.setLenient(false);
        try {
            formatter.parse(value);
        }
        catch (final ParseException e) {
            return false;
        }
        return true;
    }
    
    static {
        DATE_VALIDATOR = new DateValidator();
    }
}
