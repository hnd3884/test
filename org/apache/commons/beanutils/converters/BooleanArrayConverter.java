package org.apache.commons.beanutils.converters;

import java.util.List;
import org.apache.commons.beanutils.ConversionException;

@Deprecated
public final class BooleanArrayConverter extends AbstractArrayConverter
{
    public static final Class MODEL;
    private static final BooleanConverter DEFAULT_CONVERTER;
    protected final BooleanConverter booleanConverter;
    
    public BooleanArrayConverter() {
        this.booleanConverter = BooleanArrayConverter.DEFAULT_CONVERTER;
    }
    
    public BooleanArrayConverter(final Object defaultValue) {
        super(defaultValue);
        this.booleanConverter = BooleanArrayConverter.DEFAULT_CONVERTER;
    }
    
    public BooleanArrayConverter(final BooleanConverter converter, final Object defaultValue) {
        super(defaultValue);
        this.booleanConverter = converter;
    }
    
    @Override
    public Object convert(final Class type, final Object value) {
        if (value == null) {
            if (this.useDefault) {
                return this.defaultValue;
            }
            throw new ConversionException("No value specified");
        }
        else {
            if (BooleanArrayConverter.MODEL == value.getClass()) {
                return value;
            }
            if (BooleanArrayConverter.strings.getClass() == value.getClass()) {
                try {
                    final String[] values = (String[])value;
                    final boolean[] results = new boolean[values.length];
                    for (int i = 0; i < values.length; ++i) {
                        final String stringValue = values[i];
                        final Object result = this.booleanConverter.convert(Boolean.class, stringValue);
                        results[i] = (boolean)result;
                    }
                    return results;
                }
                catch (final Exception e) {
                    if (this.useDefault) {
                        return this.defaultValue;
                    }
                    throw new ConversionException(value.toString(), e);
                }
            }
            try {
                final List list = this.parseElements(value.toString());
                final boolean[] results = new boolean[list.size()];
                for (int i = 0; i < results.length; ++i) {
                    final String stringValue = list.get(i);
                    final Object result = this.booleanConverter.convert(Boolean.class, stringValue);
                    results[i] = (boolean)result;
                }
                return results;
            }
            catch (final Exception e) {
                if (this.useDefault) {
                    return this.defaultValue;
                }
                throw new ConversionException(value.toString(), e);
            }
        }
    }
    
    static {
        MODEL = new boolean[0].getClass();
        DEFAULT_CONVERTER = new BooleanConverter();
    }
}
