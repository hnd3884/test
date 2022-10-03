package org.apache.commons.validator.routines;

import java.text.Format;
import java.util.Locale;

public class DoubleValidator extends AbstractNumberValidator
{
    private static final DoubleValidator VALIDATOR;
    
    public static DoubleValidator getInstance() {
        return DoubleValidator.VALIDATOR;
    }
    
    public DoubleValidator() {
        this(true, 0);
    }
    
    public DoubleValidator(final boolean strict, final int formatType) {
        super(strict, formatType, true);
    }
    
    public Double validate(final String value) {
        return (Double)this.parse(value, null, null);
    }
    
    public Double validate(final String value, final String pattern) {
        return (Double)this.parse(value, pattern, null);
    }
    
    public Double validate(final String value, final Locale locale) {
        return (Double)this.parse(value, null, locale);
    }
    
    public Double validate(final String value, final String pattern, final Locale locale) {
        return (Double)this.parse(value, pattern, locale);
    }
    
    public boolean isInRange(final double value, final double min, final double max) {
        return value >= min && value <= max;
    }
    
    public boolean isInRange(final Double value, final double min, final double max) {
        return this.isInRange((double)value, min, max);
    }
    
    public boolean minValue(final double value, final double min) {
        return value >= min;
    }
    
    public boolean minValue(final Double value, final double min) {
        return this.minValue((double)value, min);
    }
    
    public boolean maxValue(final double value, final double max) {
        return value <= max;
    }
    
    public boolean maxValue(final Double value, final double max) {
        return this.maxValue((double)value, max);
    }
    
    protected Object processParsedValue(final Object value, final Format formatter) {
        if (value instanceof Double) {
            return value;
        }
        return new Double(((Number)value).doubleValue());
    }
    
    static {
        VALIDATOR = new DoubleValidator();
    }
}
