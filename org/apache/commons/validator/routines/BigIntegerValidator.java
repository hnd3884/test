package org.apache.commons.validator.routines;

import java.text.Format;
import java.util.Locale;
import java.math.BigInteger;

public class BigIntegerValidator extends AbstractNumberValidator
{
    private static final BigIntegerValidator VALIDATOR;
    
    public static BigIntegerValidator getInstance() {
        return BigIntegerValidator.VALIDATOR;
    }
    
    public BigIntegerValidator() {
        this(true, 0);
    }
    
    public BigIntegerValidator(final boolean strict, final int formatType) {
        super(strict, formatType, false);
    }
    
    public BigInteger validate(final String value) {
        return (BigInteger)this.parse(value, null, null);
    }
    
    public BigInteger validate(final String value, final String pattern) {
        return (BigInteger)this.parse(value, pattern, null);
    }
    
    public BigInteger validate(final String value, final Locale locale) {
        return (BigInteger)this.parse(value, null, locale);
    }
    
    public BigInteger validate(final String value, final String pattern, final Locale locale) {
        return (BigInteger)this.parse(value, pattern, locale);
    }
    
    public boolean isInRange(final BigInteger value, final long min, final long max) {
        return value.longValue() >= min && value.longValue() <= max;
    }
    
    public boolean minValue(final BigInteger value, final long min) {
        return value.longValue() >= min;
    }
    
    public boolean maxValue(final BigInteger value, final long max) {
        return value.longValue() <= max;
    }
    
    protected Object processParsedValue(final Object value, final Format formatter) {
        return BigInteger.valueOf(((Number)value).longValue());
    }
    
    static {
        VALIDATOR = new BigIntegerValidator();
    }
}
