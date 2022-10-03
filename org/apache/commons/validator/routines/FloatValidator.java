package org.apache.commons.validator.routines;

import java.text.Format;
import java.util.Locale;

public class FloatValidator extends AbstractNumberValidator
{
    private static final FloatValidator VALIDATOR;
    
    public static FloatValidator getInstance() {
        return FloatValidator.VALIDATOR;
    }
    
    public FloatValidator() {
        this(true, 0);
    }
    
    public FloatValidator(final boolean strict, final int formatType) {
        super(strict, formatType, true);
    }
    
    public Float validate(final String value) {
        return (Float)this.parse(value, null, null);
    }
    
    public Float validate(final String value, final String pattern) {
        return (Float)this.parse(value, pattern, null);
    }
    
    public Float validate(final String value, final Locale locale) {
        return (Float)this.parse(value, null, locale);
    }
    
    public Float validate(final String value, final String pattern, final Locale locale) {
        return (Float)this.parse(value, pattern, locale);
    }
    
    public boolean isInRange(final float value, final float min, final float max) {
        return value >= min && value <= max;
    }
    
    public boolean isInRange(final Float value, final float min, final float max) {
        return this.isInRange((float)value, min, max);
    }
    
    public boolean minValue(final float value, final float min) {
        return value >= min;
    }
    
    public boolean minValue(final Float value, final float min) {
        return this.minValue((float)value, min);
    }
    
    public boolean maxValue(final float value, final float max) {
        return value <= max;
    }
    
    public boolean maxValue(final Float value, final float max) {
        return this.maxValue((float)value, max);
    }
    
    protected Object processParsedValue(final Object value, final Format formatter) {
        final double doubleValue = ((Number)value).doubleValue();
        if (doubleValue > 0.0) {
            if (doubleValue < 1.401298464324817E-45) {
                return null;
            }
            if (doubleValue > 3.4028234663852886E38) {
                return null;
            }
        }
        else if (doubleValue < 0.0) {
            final double posDouble = doubleValue * -1.0;
            if (posDouble < 1.401298464324817E-45) {
                return null;
            }
            if (posDouble > 3.4028234663852886E38) {
                return null;
            }
        }
        return new Float((float)doubleValue);
    }
    
    static {
        VALIDATOR = new FloatValidator();
    }
}
