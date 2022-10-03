package org.apache.commons.beanutils.converters;

import java.io.IOException;
import java.util.Collections;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import org.apache.commons.beanutils.ConversionException;
import java.util.Collection;
import java.util.Iterator;
import java.lang.reflect.Array;
import org.apache.commons.beanutils.Converter;

public class ArrayConverter extends AbstractConverter
{
    private final Class<?> defaultType;
    private final Converter elementConverter;
    private int defaultSize;
    private char delimiter;
    private char[] allowedChars;
    private boolean onlyFirstToString;
    
    public ArrayConverter(final Class<?> defaultType, final Converter elementConverter) {
        this.delimiter = ',';
        this.allowedChars = new char[] { '.', '-' };
        this.onlyFirstToString = true;
        if (defaultType == null) {
            throw new IllegalArgumentException("Default type is missing");
        }
        if (!defaultType.isArray()) {
            throw new IllegalArgumentException("Default type must be an array.");
        }
        if (elementConverter == null) {
            throw new IllegalArgumentException("Component Converter is missing.");
        }
        this.defaultType = defaultType;
        this.elementConverter = elementConverter;
    }
    
    public ArrayConverter(final Class<?> defaultType, final Converter elementConverter, final int defaultSize) {
        this(defaultType, elementConverter);
        this.defaultSize = defaultSize;
        Object defaultValue = null;
        if (defaultSize >= 0) {
            defaultValue = Array.newInstance(defaultType.getComponentType(), defaultSize);
        }
        this.setDefaultValue(defaultValue);
    }
    
    public void setDelimiter(final char delimiter) {
        this.delimiter = delimiter;
    }
    
    public void setAllowedChars(final char[] allowedChars) {
        this.allowedChars = allowedChars;
    }
    
    public void setOnlyFirstToString(final boolean onlyFirstToString) {
        this.onlyFirstToString = onlyFirstToString;
    }
    
    @Override
    protected Class<?> getDefaultType() {
        return this.defaultType;
    }
    
    @Override
    protected String convertToString(final Object value) throws Throwable {
        int size = 0;
        Iterator<?> iterator = null;
        final Class<?> type = value.getClass();
        if (type.isArray()) {
            size = Array.getLength(value);
        }
        else {
            final Collection<?> collection = this.convertToCollection(type, value);
            size = collection.size();
            iterator = collection.iterator();
        }
        if (size == 0) {
            return (String)this.getDefault(String.class);
        }
        if (this.onlyFirstToString) {
            size = 1;
        }
        final StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < size; ++i) {
            if (i > 0) {
                buffer.append(this.delimiter);
            }
            Object element = (iterator == null) ? Array.get(value, i) : iterator.next();
            element = this.elementConverter.convert(String.class, element);
            if (element != null) {
                buffer.append(element);
            }
        }
        return buffer.toString();
    }
    
    @Override
    protected <T> T convertToType(final Class<T> type, final Object value) throws Throwable {
        if (!type.isArray()) {
            throw new ConversionException(this.toString(this.getClass()) + " cannot handle conversion to '" + this.toString(type) + "' (not an array).");
        }
        int size = 0;
        Iterator<?> iterator = null;
        if (value.getClass().isArray()) {
            size = Array.getLength(value);
        }
        else {
            final Collection<?> collection = this.convertToCollection(type, value);
            size = collection.size();
            iterator = collection.iterator();
        }
        final Class<?> componentType = type.getComponentType();
        final Object newArray = Array.newInstance(componentType, size);
        for (int i = 0; i < size; ++i) {
            Object element = (iterator == null) ? Array.get(value, i) : iterator.next();
            element = this.elementConverter.convert(componentType, element);
            Array.set(newArray, i, element);
        }
        final T result = (T)newArray;
        return result;
    }
    
    @Override
    protected Object convertArray(final Object value) {
        return value;
    }
    
    protected Collection<?> convertToCollection(final Class<?> type, final Object value) {
        if (value instanceof Collection) {
            return (Collection)value;
        }
        if (value instanceof Number || value instanceof Boolean || value instanceof Date) {
            final List<Object> list = new ArrayList<Object>(1);
            list.add(value);
            return list;
        }
        return this.parseElements(type, value.toString());
    }
    
    @Override
    protected Object getDefault(final Class<?> type) {
        if (type.equals(String.class)) {
            return null;
        }
        final Object defaultValue = super.getDefault(type);
        if (defaultValue == null) {
            return null;
        }
        if (defaultValue.getClass().equals(type)) {
            return defaultValue;
        }
        return Array.newInstance(type.getComponentType(), this.defaultSize);
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        buffer.append(this.toString(this.getClass()));
        buffer.append("[UseDefault=");
        buffer.append(this.isUseDefault());
        buffer.append(", ");
        buffer.append(this.elementConverter.toString());
        buffer.append(']');
        return buffer.toString();
    }
    
    private List<String> parseElements(final Class<?> type, String value) {
        if (this.log().isDebugEnabled()) {
            this.log().debug((Object)("Parsing elements, delimiter=[" + this.delimiter + "], value=[" + value + "]"));
        }
        value = value.trim();
        if (value.startsWith("{") && value.endsWith("}")) {
            value = value.substring(1, value.length() - 1);
        }
        try {
            final StreamTokenizer st = new StreamTokenizer(new StringReader(value));
            st.whitespaceChars(this.delimiter, this.delimiter);
            st.ordinaryChars(48, 57);
            st.wordChars(48, 57);
            for (final char allowedChar : this.allowedChars) {
                st.ordinaryChars(allowedChar, allowedChar);
                st.wordChars(allowedChar, allowedChar);
            }
            List<String> list = null;
            int ttype;
            while (true) {
                ttype = st.nextToken();
                if (ttype != -3 && ttype <= 0) {
                    break;
                }
                if (st.sval == null) {
                    continue;
                }
                if (list == null) {
                    list = new ArrayList<String>();
                }
                list.add(st.sval);
            }
            if (ttype == -1) {
                if (list == null) {
                    list = Collections.emptyList();
                }
                if (this.log().isDebugEnabled()) {
                    this.log().debug((Object)(list.size() + " elements parsed"));
                }
                return list;
            }
            throw new ConversionException("Encountered token of type " + ttype + " parsing elements to '" + this.toString(type) + ".");
        }
        catch (final IOException e) {
            throw new ConversionException("Error converting from String to '" + this.toString(type) + "': " + e.getMessage(), e);
        }
    }
}
