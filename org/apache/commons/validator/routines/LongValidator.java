package org.apache.commons.validator.routines;

import java.text.Format;
import java.util.Locale;

public class LongValidator extends AbstractNumberValidator
{
    private static final LongValidator VALIDATOR;
    
    public static LongValidator getInstance() {
        return LongValidator.VALIDATOR;
    }
    
    public LongValidator() {
        this(true, 0);
    }
    
    public LongValidator(final boolean strict, final int formatType) {
        super(strict, formatType, false);
    }
    
    public Long validate(final String value) {
        return (Long)this.parse(value, null, null);
    }
    
    public Long validate(final String value, final String pattern) {
        return (Long)this.parse(value, pattern, null);
    }
    
    public Long validate(final String value, final Locale locale) {
        return (Long)this.parse(value, null, locale);
    }
    
    public Long validate(final String value, final String pattern, final Locale locale) {
        return (Long)this.parse(value, pattern, locale);
    }
    
    public boolean isInRange(final long value, final long min, final long max) {
        return value >= min && value <= max;
    }
    
    public boolean isInRange(final Long value, final long min, final long max) {
        return this.isInRange((long)value, min, max);
    }
    
    public boolean minValue(final long value, final long min) {
        return value >= min;
    }
    
    public boolean minValue(final Long value, final long min) {
        return this.minValue((long)value, min);
    }
    
    public boolean maxValue(final long value, final long max) {
        return value <= max;
    }
    
    public boolean maxValue(final Long value, final long max) {
        return this.maxValue((long)value, max);
    }
    
    protected Object processParsedValue(final Object value, final Format formatter) {
        if (value instanceof Long) {
            return value;
        }
        return new Long(((Number)value).longValue());
    }
    
    static {
        VALIDATOR = new LongValidator();
    }
}
