package org.apache.commons.beanutils.converters;

import java.util.List;
import org.apache.commons.beanutils.ConversionException;

@Deprecated
public final class StringArrayConverter extends AbstractArrayConverter
{
    private static final String[] MODEL;
    private static final int[] INT_MODEL;
    
    public StringArrayConverter() {
        this.defaultValue = null;
        this.useDefault = false;
    }
    
    public StringArrayConverter(final Object defaultValue) {
        this.defaultValue = defaultValue;
        this.useDefault = true;
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
            if (StringArrayConverter.MODEL.getClass() == value.getClass()) {
                return value;
            }
            if (StringArrayConverter.INT_MODEL.getClass() == value.getClass()) {
                final int[] values = (int[])value;
                final String[] results = new String[values.length];
                for (int i = 0; i < values.length; ++i) {
                    results[i] = Integer.toString(values[i]);
                }
                return results;
            }
            try {
                final List list = this.parseElements(value.toString());
                final String[] results = new String[list.size()];
                for (int i = 0; i < results.length; ++i) {
                    results[i] = list.get(i);
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
        MODEL = new String[0];
        INT_MODEL = new int[0];
    }
}
