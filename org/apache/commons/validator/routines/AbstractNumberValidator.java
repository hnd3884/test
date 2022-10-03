package org.apache.commons.validator.routines;

import java.text.DecimalFormatSymbols;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.Format;
import java.util.Locale;

public abstract class AbstractNumberValidator extends AbstractFormatValidator
{
    public static final int STANDARD_FORMAT = 0;
    public static final int CURRENCY_FORMAT = 1;
    public static final int PERCENT_FORMAT = 2;
    private boolean allowFractions;
    private int formatType;
    
    public AbstractNumberValidator(final boolean strict, final int formatType, final boolean allowFractions) {
        super(strict);
        this.allowFractions = allowFractions;
        this.formatType = formatType;
    }
    
    public boolean isAllowFractions() {
        return this.allowFractions;
    }
    
    public int getFormatType() {
        return this.formatType;
    }
    
    public boolean isValid(final String value, final String pattern, final Locale locale) {
        final Object parsedValue = this.parse(value, pattern, locale);
        return parsedValue != null;
    }
    
    public boolean isInRange(final Number value, final Number min, final Number max) {
        return this.minValue(value, min) && this.maxValue(value, max);
    }
    
    public boolean minValue(final Number value, final Number min) {
        if (this.isAllowFractions()) {
            return value.doubleValue() >= min.doubleValue();
        }
        return value.longValue() >= min.longValue();
    }
    
    public boolean maxValue(final Number value, final Number max) {
        if (this.isAllowFractions()) {
            return value.doubleValue() <= max.doubleValue();
        }
        return value.longValue() <= max.longValue();
    }
    
    protected Object parse(String value, final String pattern, final Locale locale) {
        value = ((value == null) ? null : value.trim());
        if (value == null || value.length() == 0) {
            return null;
        }
        final Format formatter = this.getFormat(pattern, locale);
        return this.parse(value, formatter);
    }
    
    protected abstract Object processParsedValue(final Object p0, final Format p1);
    
    protected Format getFormat(final String pattern, final Locale locale) {
        NumberFormat formatter = null;
        final boolean usePattern = pattern != null && pattern.length() > 0;
        if (!usePattern) {
            formatter = (NumberFormat)this.getFormat(locale);
        }
        else if (locale == null) {
            formatter = new DecimalFormat(pattern);
        }
        else {
            final DecimalFormatSymbols symbols = new DecimalFormatSymbols(locale);
            formatter = new DecimalFormat(pattern, symbols);
        }
        if (this.determineScale(formatter) == 0) {
            formatter.setParseIntegerOnly(true);
        }
        return formatter;
    }
    
    protected int determineScale(final NumberFormat format) {
        if (!this.isStrict()) {
            return -1;
        }
        if (!this.isAllowFractions() || format.isParseIntegerOnly()) {
            return 0;
        }
        final int minimumFraction = format.getMinimumFractionDigits();
        final int maximumFraction = format.getMaximumFractionDigits();
        if (minimumFraction != maximumFraction) {
            return -1;
        }
        int scale = minimumFraction;
        if (format instanceof DecimalFormat) {
            final int multiplier = ((DecimalFormat)format).getMultiplier();
            if (multiplier == 100) {
                scale += 2;
            }
            else if (multiplier == 1000) {
                scale += 3;
            }
        }
        else if (this.formatType == 2) {
            scale += 2;
        }
        return scale;
    }
    
    protected Format getFormat(final Locale locale) {
        NumberFormat formatter = null;
        switch (this.formatType) {
            case 1: {
                if (locale == null) {
                    formatter = NumberFormat.getCurrencyInstance();
                    break;
                }
                formatter = NumberFormat.getCurrencyInstance(locale);
                break;
            }
            case 2: {
                if (locale == null) {
                    formatter = NumberFormat.getPercentInstance();
                    break;
                }
                formatter = NumberFormat.getPercentInstance(locale);
                break;
            }
            default: {
                if (locale == null) {
                    formatter = NumberFormat.getInstance();
                    break;
                }
                formatter = NumberFormat.getInstance(locale);
                break;
            }
        }
        return formatter;
    }
}
