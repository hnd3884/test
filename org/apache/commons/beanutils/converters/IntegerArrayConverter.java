package org.apache.commons.beanutils.converters;

import java.util.List;
import org.apache.commons.beanutils.ConversionException;

@Deprecated
public final class IntegerArrayConverter extends AbstractArrayConverter
{
    private static final int[] MODEL;
    
    public IntegerArrayConverter() {
        this.defaultValue = null;
        this.useDefault = false;
    }
    
    public IntegerArrayConverter(final Object defaultValue) {
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
            if (IntegerArrayConverter.MODEL.getClass() == value.getClass()) {
                return value;
            }
            if (IntegerArrayConverter.strings.getClass() == value.getClass()) {
                try {
                    final String[] values = (String[])value;
                    final int[] results = new int[values.length];
                    for (int i = 0; i < values.length; ++i) {
                        results[i] = Integer.parseInt(values[i]);
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
                final int[] results = new int[list.size()];
                for (int i = 0; i < results.length; ++i) {
                    results[i] = Integer.parseInt(list.get(i));
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
        MODEL = new int[0];
    }
}
