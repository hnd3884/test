package org.apache.commons.beanutils.locale.converters;

import java.text.ParseException;
import org.apache.commons.beanutils.ConversionException;
import java.util.Locale;

public class FloatLocaleConverter extends DecimalLocaleConverter
{
    public FloatLocaleConverter() {
        this(false);
    }
    
    public FloatLocaleConverter(final boolean locPattern) {
        this(Locale.getDefault(), locPattern);
    }
    
    public FloatLocaleConverter(final Locale locale) {
        this(locale, false);
    }
    
    public FloatLocaleConverter(final Locale locale, final boolean locPattern) {
        this(locale, null, locPattern);
    }
    
    public FloatLocaleConverter(final Locale locale, final String pattern) {
        this(locale, pattern, false);
    }
    
    public FloatLocaleConverter(final Locale locale, final String pattern, final boolean locPattern) {
        super(locale, pattern, locPattern);
    }
    
    public FloatLocaleConverter(final Object defaultValue) {
        this(defaultValue, false);
    }
    
    public FloatLocaleConverter(final Object defaultValue, final boolean locPattern) {
        this(defaultValue, Locale.getDefault(), locPattern);
    }
    
    public FloatLocaleConverter(final Object defaultValue, final Locale locale) {
        this(defaultValue, locale, false);
    }
    
    public FloatLocaleConverter(final Object defaultValue, final Locale locale, final boolean locPattern) {
        this(defaultValue, locale, null, locPattern);
    }
    
    public FloatLocaleConverter(final Object defaultValue, final Locale locale, final String pattern) {
        this(defaultValue, locale, pattern, false);
    }
    
    public FloatLocaleConverter(final Object defaultValue, final Locale locale, final String pattern, final boolean locPattern) {
        super(defaultValue, locale, pattern, locPattern);
    }
    
    @Override
    protected Object parse(final Object value, final String pattern) throws ParseException {
        final Number parsed = (Number)super.parse(value, pattern);
        final double doubleValue = parsed.doubleValue();
        final double posDouble = (doubleValue >= 0.0) ? doubleValue : (doubleValue * -1.0);
        if (posDouble != 0.0 && (posDouble < 1.401298464324817E-45 || posDouble > 3.4028234663852886E38)) {
            throw new ConversionException("Supplied number is not of type Float: " + parsed);
        }
        return new Float(parsed.floatValue());
    }
}
