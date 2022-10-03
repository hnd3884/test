package org.apache.commons.validator.routines;

import java.text.NumberFormat;
import java.text.Format;
import java.util.Locale;
import java.math.BigDecimal;

public class BigDecimalValidator extends AbstractNumberValidator
{
    private static final BigDecimalValidator VALIDATOR;
    
    public static BigDecimalValidator getInstance() {
        return BigDecimalValidator.VALIDATOR;
    }
    
    public BigDecimalValidator() {
        this(true);
    }
    
    public BigDecimalValidator(final boolean strict) {
        this(strict, 0, true);
    }
    
    protected BigDecimalValidator(final boolean strict, final int formatType, final boolean allowFractions) {
        super(strict, formatType, allowFractions);
    }
    
    public BigDecimal validate(final String value) {
        return (BigDecimal)this.parse(value, null, null);
    }
    
    public BigDecimal validate(final String value, final String pattern) {
        return (BigDecimal)this.parse(value, pattern, null);
    }
    
    public BigDecimal validate(final String value, final Locale locale) {
        return (BigDecimal)this.parse(value, null, locale);
    }
    
    public BigDecimal validate(final String value, final String pattern, final Locale locale) {
        return (BigDecimal)this.parse(value, pattern, locale);
    }
    
    public boolean isInRange(final BigDecimal value, final double min, final double max) {
        return value.doubleValue() >= min && value.doubleValue() <= max;
    }
    
    public boolean minValue(final BigDecimal value, final double min) {
        return value.doubleValue() >= min;
    }
    
    public boolean maxValue(final BigDecimal value, final double max) {
        return value.doubleValue() <= max;
    }
    
    protected Object processParsedValue(final Object value, final Format formatter) {
        BigDecimal decimal = null;
        if (value instanceof Long) {
            decimal = BigDecimal.valueOf((long)value);
        }
        else {
            decimal = new BigDecimal(value.toString());
        }
        final int scale = this.determineScale((NumberFormat)formatter);
        if (scale >= 0) {
            decimal = decimal.setScale(scale, 1);
        }
        return decimal;
    }
    
    static {
        VALIDATOR = new BigDecimalValidator();
    }
}
