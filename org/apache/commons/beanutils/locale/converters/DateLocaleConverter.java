package org.apache.commons.beanutils.locale.converters;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import org.apache.commons.beanutils.ConversionException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import org.apache.commons.logging.LogFactory;
import java.util.Locale;
import org.apache.commons.logging.Log;
import org.apache.commons.beanutils.locale.BaseLocaleConverter;

public class DateLocaleConverter extends BaseLocaleConverter
{
    private final Log log;
    boolean isLenient;
    private static final String DEFAULT_PATTERN_CHARS;
    
    public DateLocaleConverter() {
        this(false);
    }
    
    public DateLocaleConverter(final boolean locPattern) {
        this(Locale.getDefault(), locPattern);
    }
    
    public DateLocaleConverter(final Locale locale) {
        this(locale, false);
    }
    
    public DateLocaleConverter(final Locale locale, final boolean locPattern) {
        this(locale, null, locPattern);
    }
    
    public DateLocaleConverter(final Locale locale, final String pattern) {
        this(locale, pattern, false);
    }
    
    public DateLocaleConverter(final Locale locale, final String pattern, final boolean locPattern) {
        super(locale, pattern, locPattern);
        this.log = LogFactory.getLog((Class)DateLocaleConverter.class);
        this.isLenient = false;
    }
    
    public DateLocaleConverter(final Object defaultValue) {
        this(defaultValue, false);
    }
    
    public DateLocaleConverter(final Object defaultValue, final boolean locPattern) {
        this(defaultValue, Locale.getDefault(), locPattern);
    }
    
    public DateLocaleConverter(final Object defaultValue, final Locale locale) {
        this(defaultValue, locale, false);
    }
    
    public DateLocaleConverter(final Object defaultValue, final Locale locale, final boolean locPattern) {
        this(defaultValue, locale, null, locPattern);
    }
    
    public DateLocaleConverter(final Object defaultValue, final Locale locale, final String pattern) {
        this(defaultValue, locale, pattern, false);
    }
    
    public DateLocaleConverter(final Object defaultValue, final Locale locale, final String pattern, final boolean locPattern) {
        super(defaultValue, locale, pattern, locPattern);
        this.log = LogFactory.getLog((Class)DateLocaleConverter.class);
        this.isLenient = false;
    }
    
    public boolean isLenient() {
        return this.isLenient;
    }
    
    public void setLenient(final boolean lenient) {
        this.isLenient = lenient;
    }
    
    @Override
    protected Object parse(final Object value, String pattern) throws ParseException {
        if (value instanceof Date) {
            return value;
        }
        if (value instanceof Calendar) {
            return ((Calendar)value).getTime();
        }
        if (this.locPattern) {
            pattern = this.convertLocalizedPattern(pattern, this.locale);
        }
        final DateFormat formatter = (pattern == null) ? DateFormat.getDateInstance(3, this.locale) : new SimpleDateFormat(pattern, this.locale);
        formatter.setLenient(this.isLenient);
        final ParsePosition pos = new ParsePosition(0);
        final String strValue = value.toString();
        final Object parsedValue = formatter.parseObject(strValue, pos);
        if (pos.getErrorIndex() > -1) {
            throw new ConversionException("Error parsing date '" + value + "' at position=" + pos.getErrorIndex());
        }
        if (pos.getIndex() < strValue.length()) {
            throw new ConversionException("Date '" + value + "' contains unparsed characters from position=" + pos.getIndex());
        }
        return parsedValue;
    }
    
    private String convertLocalizedPattern(final String localizedPattern, final Locale locale) {
        if (localizedPattern == null) {
            return null;
        }
        final DateFormatSymbols localizedSymbols = new DateFormatSymbols(locale);
        final String localChars = localizedSymbols.getLocalPatternChars();
        if (DateLocaleConverter.DEFAULT_PATTERN_CHARS.equals(localChars)) {
            return localizedPattern;
        }
        String convertedPattern = null;
        try {
            convertedPattern = this.convertPattern(localizedPattern, localChars, DateLocaleConverter.DEFAULT_PATTERN_CHARS);
        }
        catch (final Exception ex) {
            this.log.debug((Object)("Converting pattern '" + localizedPattern + "' for " + locale), (Throwable)ex);
        }
        return convertedPattern;
    }
    
    private String convertPattern(final String pattern, final String fromChars, final String toChars) {
        final StringBuilder converted = new StringBuilder();
        boolean quoted = false;
        for (int i = 0; i < pattern.length(); ++i) {
            char thisChar = pattern.charAt(i);
            if (quoted) {
                if (thisChar == '\'') {
                    quoted = false;
                }
            }
            else if (thisChar == '\'') {
                quoted = true;
            }
            else if ((thisChar >= 'a' && thisChar <= 'z') || (thisChar >= 'A' && thisChar <= 'Z')) {
                final int index = fromChars.indexOf(thisChar);
                if (index == -1) {
                    throw new IllegalArgumentException("Illegal pattern character '" + thisChar + "'");
                }
                thisChar = toChars.charAt(index);
            }
            converted.append(thisChar);
        }
        if (quoted) {
            throw new IllegalArgumentException("Unfinished quote in pattern");
        }
        return converted.toString();
    }
    
    private static String initDefaultChars() {
        final DateFormatSymbols defaultSymbols = new DateFormatSymbols(Locale.US);
        return defaultSymbols.getLocalPatternChars();
    }
    
    static {
        DEFAULT_PATTERN_CHARS = initDefaultChars();
    }
}
