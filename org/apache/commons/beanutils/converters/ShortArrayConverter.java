package org.apache.commons.beanutils.converters;

import java.util.List;
import org.apache.commons.beanutils.ConversionException;

@Deprecated
public final class ShortArrayConverter extends AbstractArrayConverter
{
    private static final short[] MODEL;
    
    public ShortArrayConverter() {
        this.defaultValue = null;
        this.useDefault = false;
    }
    
    public ShortArrayConverter(final Object defaultValue) {
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
            if (ShortArrayConverter.MODEL.getClass() == value.getClass()) {
                return value;
            }
            if (ShortArrayConverter.strings.getClass() == value.getClass()) {
                try {
                    final String[] values = (String[])value;
                    final short[] results = new short[values.length];
                    for (int i = 0; i < values.length; ++i) {
                        results[i] = Short.parseShort(values[i]);
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
                final short[] results = new short[list.size()];
                for (int i = 0; i < results.length; ++i) {
                    results[i] = Short.parseShort(list.get(i));
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
        MODEL = new short[0];
    }
}
