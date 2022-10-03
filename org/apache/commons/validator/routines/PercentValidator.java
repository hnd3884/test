package org.apache.commons.validator.routines;

import java.text.DecimalFormat;
import java.text.Format;
import java.math.BigDecimal;

public class PercentValidator extends BigDecimalValidator
{
    private static final PercentValidator VALIDATOR;
    private static final char PERCENT_SYMBOL = '%';
    private static final BigDecimal POINT_ZERO_ONE;
    
    public static BigDecimalValidator getInstance() {
        return PercentValidator.VALIDATOR;
    }
    
    public PercentValidator() {
        this(true);
    }
    
    public PercentValidator(final boolean strict) {
        super(strict, 2, true);
    }
    
    protected Object parse(final String value, final Format formatter) {
        BigDecimal parsedValue = (BigDecimal)super.parse(value, formatter);
        if (parsedValue != null || !(formatter instanceof DecimalFormat)) {
            return parsedValue;
        }
        final DecimalFormat decimalFormat = (DecimalFormat)formatter;
        final String pattern = decimalFormat.toPattern();
        if (pattern.indexOf(37) >= 0) {
            final StringBuffer buffer = new StringBuffer(pattern.length());
            for (int i = 0; i < pattern.length(); ++i) {
                if (pattern.charAt(i) != '%') {
                    buffer.append(pattern.charAt(i));
                }
            }
            decimalFormat.applyPattern(buffer.toString());
            parsedValue = (BigDecimal)super.parse(value, decimalFormat);
            if (parsedValue != null) {
                parsedValue = parsedValue.multiply(PercentValidator.POINT_ZERO_ONE);
            }
        }
        return parsedValue;
    }
    
    static {
        VALIDATOR = new PercentValidator();
        POINT_ZERO_ONE = new BigDecimal("0.01");
    }
}
