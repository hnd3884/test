package org.apache.commons.validator.routines;

import java.text.ParsePosition;
import java.text.Format;
import java.util.Locale;
import java.io.Serializable;

public abstract class AbstractFormatValidator implements Serializable
{
    private boolean strict;
    
    public AbstractFormatValidator(final boolean strict) {
        this.strict = true;
        this.strict = strict;
    }
    
    public boolean isStrict() {
        return this.strict;
    }
    
    public boolean isValid(final String value) {
        return this.isValid(value, null, null);
    }
    
    public boolean isValid(final String value, final String pattern) {
        return this.isValid(value, pattern, null);
    }
    
    public boolean isValid(final String value, final Locale locale) {
        return this.isValid(value, null, locale);
    }
    
    public abstract boolean isValid(final String p0, final String p1, final Locale p2);
    
    public String format(final Object value) {
        return this.format(value, null, null);
    }
    
    public String format(final Object value, final String pattern) {
        return this.format(value, pattern, null);
    }
    
    public String format(final Object value, final Locale locale) {
        return this.format(value, null, locale);
    }
    
    public String format(final Object value, final String pattern, final Locale locale) {
        final Format formatter = this.getFormat(pattern, locale);
        return this.format(value, formatter);
    }
    
    protected String format(final Object value, final Format formatter) {
        return formatter.format(value);
    }
    
    protected Object parse(final String value, final Format formatter) {
        final ParsePosition pos = new ParsePosition(0);
        Object parsedValue = formatter.parseObject(value, pos);
        if (pos.getErrorIndex() > -1) {
            return null;
        }
        if (this.isStrict() && pos.getIndex() < value.length()) {
            return null;
        }
        if (parsedValue != null) {
            parsedValue = this.processParsedValue(parsedValue, formatter);
        }
        return parsedValue;
    }
    
    protected abstract Object processParsedValue(final Object p0, final Format p1);
    
    protected abstract Format getFormat(final String p0, final Locale p1);
}
