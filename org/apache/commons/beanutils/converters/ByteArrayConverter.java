package org.apache.commons.beanutils.converters;

import java.util.List;
import org.apache.commons.beanutils.ConversionException;

@Deprecated
public final class ByteArrayConverter extends AbstractArrayConverter
{
    private static final byte[] MODEL;
    
    public ByteArrayConverter() {
        this.defaultValue = null;
        this.useDefault = false;
    }
    
    public ByteArrayConverter(final Object defaultValue) {
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
            if (ByteArrayConverter.MODEL.getClass() == value.getClass()) {
                return value;
            }
            if (ByteArrayConverter.strings.getClass() == value.getClass()) {
                try {
                    final String[] values = (String[])value;
                    final byte[] results = new byte[values.length];
                    for (int i = 0; i < values.length; ++i) {
                        results[i] = Byte.parseByte(values[i]);
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
                final byte[] results = new byte[list.size()];
                for (int i = 0; i < results.length; ++i) {
                    results[i] = Byte.parseByte(list.get(i));
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
        MODEL = new byte[0];
    }
}
