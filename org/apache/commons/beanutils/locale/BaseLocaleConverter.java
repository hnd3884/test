package org.apache.commons.beanutils.locale;

import org.apache.commons.beanutils.ConversionException;
import org.apache.commons.beanutils.ConvertUtils;
import java.text.ParseException;
import org.apache.commons.logging.LogFactory;
import java.util.Locale;
import org.apache.commons.logging.Log;

public abstract class BaseLocaleConverter implements LocaleConverter
{
    private final Log log;
    private Object defaultValue;
    protected boolean useDefault;
    protected Locale locale;
    protected String pattern;
    protected boolean locPattern;
    
    protected BaseLocaleConverter(final Locale locale, final String pattern) {
        this(null, locale, pattern, false, false);
    }
    
    protected BaseLocaleConverter(final Locale locale, final String pattern, final boolean locPattern) {
        this(null, locale, pattern, false, locPattern);
    }
    
    protected BaseLocaleConverter(final Object defaultValue, final Locale locale, final String pattern) {
        this(defaultValue, locale, pattern, false);
    }
    
    protected BaseLocaleConverter(final Object defaultValue, final Locale locale, final String pattern, final boolean locPattern) {
        this(defaultValue, locale, pattern, true, locPattern);
    }
    
    private BaseLocaleConverter(final Object defaultValue, final Locale locale, final String pattern, final boolean useDefault, final boolean locPattern) {
        this.log = LogFactory.getLog((Class)BaseLocaleConverter.class);
        this.defaultValue = null;
        this.useDefault = false;
        this.locale = Locale.getDefault();
        this.pattern = null;
        this.locPattern = false;
        if (useDefault) {
            this.defaultValue = defaultValue;
            this.useDefault = true;
        }
        if (locale != null) {
            this.locale = locale;
        }
        this.pattern = pattern;
        this.locPattern = locPattern;
    }
    
    protected abstract Object parse(final Object p0, final String p1) throws ParseException;
    
    public Object convert(final Object value) {
        return this.convert(value, null);
    }
    
    public Object convert(final Object value, final String pattern) {
        return this.convert((Class<Object>)null, value, pattern);
    }
    
    @Override
    public <T> T convert(final Class<T> type, final Object value) {
        return this.convert(type, value, null);
    }
    
    @Override
    public <T> T convert(final Class<T> type, final Object value, final String pattern) {
        final Class<T> targetType = ConvertUtils.primitiveToWrapper(type);
        if (value == null) {
            if (this.useDefault) {
                return this.getDefaultAs(targetType);
            }
            this.log.debug((Object)"Null value specified for conversion, returing null");
            return null;
        }
        else {
            try {
                if (pattern != null) {
                    return checkConversionResult(targetType, this.parse(value, pattern));
                }
                return checkConversionResult(targetType, this.parse(value, this.pattern));
            }
            catch (final Exception e) {
                if (this.useDefault) {
                    return this.getDefaultAs(targetType);
                }
                if (e instanceof ConversionException) {
                    throw (ConversionException)e;
                }
                throw new ConversionException(e);
            }
        }
    }
    
    private <T> T getDefaultAs(final Class<T> type) {
        return checkConversionResult(type, this.defaultValue);
    }
    
    private static <T> T checkConversionResult(final Class<T> type, final Object result) {
        if (type == null) {
            final T temp = (T)result;
            return temp;
        }
        if (result == null) {
            return null;
        }
        if (type.isInstance(result)) {
            return type.cast(result);
        }
        throw new ConversionException("Unsupported target type: " + type);
    }
}
