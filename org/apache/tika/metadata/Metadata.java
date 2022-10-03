package org.apache.tika.metadata;

import java.util.Arrays;
import java.util.Objects;
import java.util.Iterator;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.DateFormatSymbols;
import java.util.Locale;
import java.text.DateFormat;
import java.util.TimeZone;
import java.util.HashMap;
import java.util.Map;
import org.apache.tika.utils.DateUtils;
import java.io.Serializable;

public class Metadata implements CreativeCommons, Geographic, HttpHeaders, Message, ClimateForcast, TIFF, TikaMimeKeys, Serializable
{
    private static final long serialVersionUID = 5623926545693153182L;
    private static final DateUtils DATE_UTILS;
    private Map<String, String[]> metadata;
    
    public Metadata() {
        this.metadata = null;
        this.metadata = new HashMap<String, String[]>();
    }
    
    private static DateFormat createDateFormat(final String format, final TimeZone timezone) {
        final SimpleDateFormat sdf = new SimpleDateFormat(format, new DateFormatSymbols(Locale.US));
        if (timezone != null) {
            sdf.setTimeZone(timezone);
        }
        return sdf;
    }
    
    private static synchronized Date parseDate(final String date) {
        return Metadata.DATE_UTILS.tryToParse(date);
    }
    
    public boolean isMultiValued(final Property property) {
        return this.metadata.get(property.getName()) != null && this.metadata.get(property.getName()).length > 1;
    }
    
    public boolean isMultiValued(final String name) {
        return this.metadata.get(name) != null && this.metadata.get(name).length > 1;
    }
    
    public String[] names() {
        return this.metadata.keySet().toArray(new String[0]);
    }
    
    public String get(final String name) {
        final String[] values = this.metadata.get(name);
        if (values == null) {
            return null;
        }
        return values[0];
    }
    
    public String get(final Property property) {
        return this.get(property.getName());
    }
    
    public Integer getInt(final Property property) {
        if (property.getPrimaryProperty().getPropertyType() != Property.PropertyType.SIMPLE) {
            return null;
        }
        if (property.getPrimaryProperty().getValueType() != Property.ValueType.INTEGER) {
            return null;
        }
        final String v = this.get(property);
        if (v == null) {
            return null;
        }
        try {
            return Integer.valueOf(v);
        }
        catch (final NumberFormatException e) {
            return null;
        }
    }
    
    public Date getDate(final Property property) {
        if (property.getPrimaryProperty().getPropertyType() != Property.PropertyType.SIMPLE) {
            return null;
        }
        if (property.getPrimaryProperty().getValueType() != Property.ValueType.DATE) {
            return null;
        }
        final String v = this.get(property);
        if (v != null) {
            return parseDate(v);
        }
        return null;
    }
    
    public String[] getValues(final Property property) {
        return this._getValues(property.getName());
    }
    
    public String[] getValues(final String name) {
        return this._getValues(name);
    }
    
    private String[] _getValues(final String name) {
        String[] values = this.metadata.get(name);
        if (values == null) {
            values = new String[0];
        }
        return values;
    }
    
    private String[] appendedValues(final String[] values, final String value) {
        final String[] newValues = new String[values.length + 1];
        System.arraycopy(values, 0, newValues, 0, values.length);
        newValues[newValues.length - 1] = value;
        return newValues;
    }
    
    public void add(final String name, final String value) {
        final String[] values = this.metadata.get(name);
        if (values == null) {
            this.set(name, value);
        }
        else {
            this.metadata.put(name, this.appendedValues(values, value));
        }
    }
    
    public void add(final Property property, final String value) {
        if (property == null) {
            throw new NullPointerException("property must not be null");
        }
        if (property.getPropertyType() == Property.PropertyType.COMPOSITE) {
            this.add(property.getPrimaryProperty(), value);
            if (property.getSecondaryExtractProperties() != null) {
                for (final Property secondaryExtractProperty : property.getSecondaryExtractProperties()) {
                    this.add(secondaryExtractProperty, value);
                }
            }
        }
        else {
            final String[] values = this.metadata.get(property.getName());
            if (values == null) {
                this.set(property, value);
            }
            else {
                if (!property.isMultiValuePermitted()) {
                    throw new PropertyTypeException(property.getName() + " : " + property.getPropertyType());
                }
                this.set(property, this.appendedValues(values, value));
            }
        }
    }
    
    public void setAll(final Properties properties) {
        final Enumeration<String> names = (Enumeration<String>)properties.propertyNames();
        while (names.hasMoreElements()) {
            final String name = names.nextElement();
            this.metadata.put(name, new String[] { properties.getProperty(name) });
        }
    }
    
    public void set(final String name, final String value) {
        if (value != null) {
            this.metadata.put(name, new String[] { value });
        }
        else {
            this.metadata.remove(name);
        }
    }
    
    public void set(final Property property, final String value) {
        if (property == null) {
            throw new NullPointerException("property must not be null");
        }
        if (property.getPropertyType() == Property.PropertyType.COMPOSITE) {
            this.set(property.getPrimaryProperty(), value);
            if (property.getSecondaryExtractProperties() != null) {
                for (final Property secondaryExtractProperty : property.getSecondaryExtractProperties()) {
                    this.set(secondaryExtractProperty, value);
                }
            }
        }
        else {
            this.set(property.getName(), value);
        }
    }
    
    public void set(final Property property, final String[] values) {
        if (property == null) {
            throw new NullPointerException("property must not be null");
        }
        if (property.getPropertyType() == Property.PropertyType.COMPOSITE) {
            this.set(property.getPrimaryProperty(), values);
            if (property.getSecondaryExtractProperties() != null) {
                for (final Property secondaryExtractProperty : property.getSecondaryExtractProperties()) {
                    this.set(secondaryExtractProperty, values);
                }
            }
        }
        else {
            this.metadata.put(property.getName(), values);
        }
    }
    
    public void set(final Property property, final int value) {
        if (property.getPrimaryProperty().getPropertyType() != Property.PropertyType.SIMPLE) {
            throw new PropertyTypeException(Property.PropertyType.SIMPLE, property.getPrimaryProperty().getPropertyType());
        }
        if (property.getPrimaryProperty().getValueType() != Property.ValueType.INTEGER) {
            throw new PropertyTypeException(Property.ValueType.INTEGER, property.getPrimaryProperty().getValueType());
        }
        this.set(property, Integer.toString(value));
    }
    
    public void add(final Property property, final int value) {
        if (property.getPrimaryProperty().getPropertyType() != Property.PropertyType.SEQ) {
            throw new PropertyTypeException(Property.PropertyType.SEQ, property.getPrimaryProperty().getPropertyType());
        }
        if (property.getPrimaryProperty().getValueType() != Property.ValueType.INTEGER) {
            throw new PropertyTypeException(Property.ValueType.INTEGER, property.getPrimaryProperty().getValueType());
        }
        this.add(property, Integer.toString(value));
    }
    
    public int[] getIntValues(final Property property) {
        if (property.getPrimaryProperty().getPropertyType() != Property.PropertyType.SEQ) {
            throw new PropertyTypeException(Property.PropertyType.SEQ, property.getPrimaryProperty().getPropertyType());
        }
        if (property.getPrimaryProperty().getValueType() != Property.ValueType.INTEGER) {
            throw new PropertyTypeException(Property.ValueType.INTEGER, property.getPrimaryProperty().getValueType());
        }
        final String[] vals = this.getValues(property);
        final int[] ret = new int[vals.length];
        for (int i = 0; i < vals.length; ++i) {
            ret[i] = Integer.parseInt(vals[i]);
        }
        return ret;
    }
    
    public void set(final Property property, final double value) {
        if (property.getPrimaryProperty().getPropertyType() != Property.PropertyType.SIMPLE) {
            throw new PropertyTypeException(Property.PropertyType.SIMPLE, property.getPrimaryProperty().getPropertyType());
        }
        if (property.getPrimaryProperty().getValueType() != Property.ValueType.REAL && property.getPrimaryProperty().getValueType() != Property.ValueType.RATIONAL) {
            throw new PropertyTypeException(Property.ValueType.REAL, property.getPrimaryProperty().getValueType());
        }
        this.set(property, Double.toString(value));
    }
    
    public void set(final Property property, final Date date) {
        if (property.getPrimaryProperty().getPropertyType() != Property.PropertyType.SIMPLE) {
            throw new PropertyTypeException(Property.PropertyType.SIMPLE, property.getPrimaryProperty().getPropertyType());
        }
        if (property.getPrimaryProperty().getValueType() != Property.ValueType.DATE) {
            throw new PropertyTypeException(Property.ValueType.DATE, property.getPrimaryProperty().getValueType());
        }
        String dateString = null;
        if (date != null) {
            dateString = DateUtils.formatDate(date);
        }
        this.set(property, dateString);
    }
    
    public void set(final Property property, final Calendar date) {
        if (property.getPrimaryProperty().getPropertyType() != Property.PropertyType.SIMPLE) {
            throw new PropertyTypeException(Property.PropertyType.SIMPLE, property.getPrimaryProperty().getPropertyType());
        }
        if (property.getPrimaryProperty().getValueType() != Property.ValueType.DATE) {
            throw new PropertyTypeException(Property.ValueType.DATE, property.getPrimaryProperty().getValueType());
        }
        String dateString = null;
        if (date != null) {
            dateString = DateUtils.formatDate(date);
        }
        this.set(property, dateString);
    }
    
    public void remove(final String name) {
        this.metadata.remove(name);
    }
    
    public int size() {
        return this.metadata.size();
    }
    
    @Override
    public int hashCode() {
        int h = 0;
        for (final Map.Entry<String, String[]> stringEntry : this.metadata.entrySet()) {
            h += this.getMetadataEntryHashCode(stringEntry);
        }
        return h;
    }
    
    private int getMetadataEntryHashCode(final Map.Entry<String, String[]> e) {
        return Objects.hashCode(e.getKey()) ^ Arrays.hashCode(e.getValue());
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == null) {
            return false;
        }
        Metadata other = null;
        try {
            other = (Metadata)o;
        }
        catch (final ClassCastException cce) {
            return false;
        }
        if (other.size() != this.size()) {
            return false;
        }
        final String[] names2;
        final String[] names = names2 = this.names();
        for (final String name : names2) {
            final String[] otherValues = other._getValues(name);
            final String[] thisValues = this._getValues(name);
            if (otherValues.length != thisValues.length) {
                return false;
            }
            for (int j = 0; j < otherValues.length; ++j) {
                if (!otherValues[j].equals(thisValues[j])) {
                    return false;
                }
            }
        }
        return true;
    }
    
    @Override
    public String toString() {
        final StringBuffer buf = new StringBuffer();
        final String[] names2;
        final String[] names = names2 = this.names();
        for (final String name : names2) {
            final String[] getValues;
            final String[] values = getValues = this._getValues(name);
            for (final String value : getValues) {
                if (buf.length() > 0) {
                    buf.append(" ");
                }
                buf.append(name).append("=").append(value);
            }
        }
        return buf.toString();
    }
    
    static {
        DATE_UTILS = new DateUtils();
    }
}
