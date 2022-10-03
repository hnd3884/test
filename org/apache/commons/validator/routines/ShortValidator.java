package org.apache.commons.validator.routines;

import java.text.Format;
import java.util.Locale;

public class ShortValidator extends AbstractNumberValidator
{
    private static final ShortValidator VALIDATOR;
    
    public static ShortValidator getInstance() {
        return ShortValidator.VALIDATOR;
    }
    
    public ShortValidator() {
        this(true, 0);
    }
    
    public ShortValidator(final boolean strict, final int formatType) {
        super(strict, formatType, false);
    }
    
    public Short validate(final String value) {
        return (Short)this.parse(value, null, null);
    }
    
    public Short validate(final String value, final String pattern) {
        return (Short)this.parse(value, pattern, null);
    }
    
    public Short validate(final String value, final Locale locale) {
        return (Short)this.parse(value, null, locale);
    }
    
    public Short validate(final String value, final String pattern, final Locale locale) {
        return (Short)this.parse(value, pattern, locale);
    }
    
    public boolean isInRange(final short value, final short min, final short max) {
        return value >= min && value <= max;
    }
    
    public boolean isInRange(final Short value, final short min, final short max) {
        return this.isInRange((short)value, min, max);
    }
    
    public boolean minValue(final short value, final short min) {
        return value >= min;
    }
    
    public boolean minValue(final Short value, final short min) {
        return this.minValue((short)value, min);
    }
    
    public boolean maxValue(final short value, final short max) {
        return value <= max;
    }
    
    public boolean maxValue(final Short value, final short max) {
        return this.maxValue((short)value, max);
    }
    
    protected Object processParsedValue(final Object value, final Format formatter) {
        final long longValue = ((Number)value).longValue();
        if (longValue < -32768L || longValue > 32767L) {
            return null;
        }
        return new Short((short)longValue);
    }
    
    static {
        VALIDATOR = new ShortValidator();
    }
}
