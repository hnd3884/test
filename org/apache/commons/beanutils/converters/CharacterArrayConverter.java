package org.apache.commons.beanutils.converters;

import java.util.List;
import org.apache.commons.beanutils.ConversionException;

@Deprecated
public final class CharacterArrayConverter extends AbstractArrayConverter
{
    private static final char[] MODEL;
    
    public CharacterArrayConverter() {
        this.defaultValue = null;
        this.useDefault = false;
    }
    
    public CharacterArrayConverter(final Object defaultValue) {
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
            if (CharacterArrayConverter.MODEL.getClass() == value.getClass()) {
                return value;
            }
            if (CharacterArrayConverter.strings.getClass() == value.getClass()) {
                try {
                    final String[] values = (String[])value;
                    final char[] results = new char[values.length];
                    for (int i = 0; i < values.length; ++i) {
                        results[i] = values[i].charAt(0);
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
                final char[] results = new char[list.size()];
                for (int i = 0; i < results.length; ++i) {
                    results[i] = list.get(i).charAt(0);
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
        MODEL = new char[0];
    }
}
