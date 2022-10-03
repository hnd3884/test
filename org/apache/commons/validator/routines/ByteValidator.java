package org.apache.commons.validator.routines;

import java.text.Format;
import java.util.Locale;

public class ByteValidator extends AbstractNumberValidator
{
    private static final ByteValidator VALIDATOR;
    
    public static ByteValidator getInstance() {
        return ByteValidator.VALIDATOR;
    }
    
    public ByteValidator() {
        this(true, 0);
    }
    
    public ByteValidator(final boolean strict, final int formatType) {
        super(strict, formatType, false);
    }
    
    public Byte validate(final String value) {
        return (Byte)this.parse(value, null, null);
    }
    
    public Byte validate(final String value, final String pattern) {
        return (Byte)this.parse(value, pattern, null);
    }
    
    public Byte validate(final String value, final Locale locale) {
        return (Byte)this.parse(value, null, locale);
    }
    
    public Byte validate(final String value, final String pattern, final Locale locale) {
        return (Byte)this.parse(value, pattern, locale);
    }
    
    public boolean isInRange(final byte value, final byte min, final byte max) {
        return value >= min && value <= max;
    }
    
    public boolean isInRange(final Byte value, final byte min, final byte max) {
        return this.isInRange((byte)value, min, max);
    }
    
    public boolean minValue(final byte value, final byte min) {
        return value >= min;
    }
    
    public boolean minValue(final Byte value, final byte min) {
        return this.minValue((byte)value, min);
    }
    
    public boolean maxValue(final byte value, final byte max) {
        return value <= max;
    }
    
    public boolean maxValue(final Byte value, final byte max) {
        return this.maxValue((byte)value, max);
    }
    
    protected Object processParsedValue(final Object value, final Format formatter) {
        final long longValue = ((Number)value).longValue();
        if (longValue < -128L || longValue > 127L) {
            return null;
        }
        return new Byte((byte)longValue);
    }
    
    static {
        VALIDATOR = new ByteValidator();
    }
}
