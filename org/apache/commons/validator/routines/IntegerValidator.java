package org.apache.commons.validator.routines;

import java.text.Format;
import java.util.Locale;

public class IntegerValidator extends AbstractNumberValidator
{
    private static final IntegerValidator VALIDATOR;
    
    public static IntegerValidator getInstance() {
        return IntegerValidator.VALIDATOR;
    }
    
    public IntegerValidator() {
        this(true, 0);
    }
    
    public IntegerValidator(final boolean strict, final int formatType) {
        super(strict, formatType, false);
    }
    
    public Integer validate(final String value) {
        return (Integer)this.parse(value, null, null);
    }
    
    public Integer validate(final String value, final String pattern) {
        return (Integer)this.parse(value, pattern, null);
    }
    
    public Integer validate(final String value, final Locale locale) {
        return (Integer)this.parse(value, null, locale);
    }
    
    public Integer validate(final String value, final String pattern, final Locale locale) {
        return (Integer)this.parse(value, pattern, locale);
    }
    
    public boolean isInRange(final int value, final int min, final int max) {
        return value >= min && value <= max;
    }
    
    public boolean isInRange(final Integer value, final int min, final int max) {
        return this.isInRange((int)value, min, max);
    }
    
    public boolean minValue(final int value, final int min) {
        return value >= min;
    }
    
    public boolean minValue(final Integer value, final int min) {
        return this.minValue((int)value, min);
    }
    
    public boolean maxValue(final int value, final int max) {
        return value <= max;
    }
    
    public boolean maxValue(final Integer value, final int max) {
        return this.maxValue((int)value, max);
    }
    
    protected Object processParsedValue(final Object value, final Format formatter) {
        final long longValue = ((Number)value).longValue();
        if (longValue < -2147483648L || longValue > 2147483647L) {
            return null;
        }
        return new Integer((int)longValue);
    }
    
    static {
        VALIDATOR = new IntegerValidator();
    }
}
