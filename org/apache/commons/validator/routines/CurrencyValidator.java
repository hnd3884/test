package org.apache.commons.validator.routines;

import java.text.DecimalFormat;
import java.text.Format;

public class CurrencyValidator extends BigDecimalValidator
{
    private static final CurrencyValidator VALIDATOR;
    private static final char CURRENCY_SYMBOL = '¤';
    
    public static BigDecimalValidator getInstance() {
        return CurrencyValidator.VALIDATOR;
    }
    
    public CurrencyValidator() {
        this(true, true);
    }
    
    public CurrencyValidator(final boolean strict, final boolean allowFractions) {
        super(strict, 1, allowFractions);
    }
    
    protected Object parse(final String value, final Format formatter) {
        Object parsedValue = super.parse(value, formatter);
        if (parsedValue != null || !(formatter instanceof DecimalFormat)) {
            return parsedValue;
        }
        final DecimalFormat decimalFormat = (DecimalFormat)formatter;
        final String pattern = decimalFormat.toPattern();
        if (pattern.indexOf(164) >= 0) {
            final StringBuffer buffer = new StringBuffer(pattern.length());
            for (int i = 0; i < pattern.length(); ++i) {
                if (pattern.charAt(i) != '¤') {
                    buffer.append(pattern.charAt(i));
                }
            }
            decimalFormat.applyPattern(buffer.toString());
            parsedValue = super.parse(value, decimalFormat);
        }
        return parsedValue;
    }
    
    static {
        VALIDATOR = new CurrencyValidator();
    }
}
