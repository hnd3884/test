package com.unboundid.ldap.sdk.unboundidds.monitors;

import java.util.Collections;
import java.util.Arrays;
import java.util.List;
import java.util.Date;
import com.unboundid.util.Validator;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import java.io.Serializable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class MonitorAttribute implements Serializable
{
    private static final long serialVersionUID = 7931725606171964572L;
    private final Class<?> dataType;
    private final Object[] values;
    private final String description;
    private final String displayName;
    private final String name;
    
    public MonitorAttribute(final String name, final String displayName, final String description, final Boolean value) {
        this(name, displayName, description, Boolean.class, new Object[] { value });
        Validator.ensureNotNull(value);
    }
    
    public MonitorAttribute(final String name, final String displayName, final String description, final Date value) {
        this(name, displayName, description, Date.class, new Object[] { value });
        Validator.ensureNotNull(value);
    }
    
    public MonitorAttribute(final String name, final String displayName, final String description, final Date[] values) {
        this(name, displayName, description, Date.class, values);
    }
    
    public MonitorAttribute(final String name, final String displayName, final String description, final Double value) {
        this(name, displayName, description, Double.class, new Object[] { value });
        Validator.ensureNotNull(value);
    }
    
    public MonitorAttribute(final String name, final String displayName, final String description, final Double[] values) {
        this(name, displayName, description, Double.class, values);
    }
    
    public MonitorAttribute(final String name, final String displayName, final String description, final Integer value) {
        this(name, displayName, description, Integer.class, new Object[] { value });
        Validator.ensureNotNull(value);
    }
    
    public MonitorAttribute(final String name, final String displayName, final String description, final Integer[] values) {
        this(name, displayName, description, Integer.class, values);
    }
    
    public MonitorAttribute(final String name, final String displayName, final String description, final Long value) {
        this(name, displayName, description, Long.class, new Object[] { value });
        Validator.ensureNotNull(value);
    }
    
    public MonitorAttribute(final String name, final String displayName, final String description, final Long[] values) {
        this(name, displayName, description, Long.class, values);
    }
    
    public MonitorAttribute(final String name, final String displayName, final String description, final String value) {
        this(name, displayName, description, String.class, new Object[] { value });
        Validator.ensureNotNull(value);
    }
    
    public MonitorAttribute(final String name, final String displayName, final String description, final String[] values) {
        this(name, displayName, description, String.class, values);
    }
    
    private MonitorAttribute(final String name, final String displayName, final String description, final Class<?> dataType, final Object[] values) {
        Validator.ensureNotNull(name, displayName, dataType, values);
        Validator.ensureFalse(values.length == 0, "MonitorAttribute.values must not be empty.");
        this.name = name;
        this.displayName = displayName;
        this.description = description;
        this.dataType = dataType;
        this.values = values;
    }
    
    public String getName() {
        return this.name;
    }
    
    public String getDisplayName() {
        return this.displayName;
    }
    
    public String getDescription() {
        return this.description;
    }
    
    public Class<?> getDataType() {
        return this.dataType;
    }
    
    public boolean hasMultipleValues() {
        return this.values.length > 1;
    }
    
    public Object getValue() {
        return this.values[0];
    }
    
    public List<Object> getValues() {
        return Collections.unmodifiableList((List<?>)Arrays.asList((T[])this.values));
    }
    
    public Boolean getBooleanValue() throws ClassCastException {
        return (Boolean)this.values[0];
    }
    
    public Date getDateValue() throws ClassCastException {
        return (Date)this.values[0];
    }
    
    public List<Date> getDateValues() throws ClassCastException {
        return Collections.unmodifiableList((List<? extends Date>)Arrays.asList((T[])this.values));
    }
    
    public Double getDoubleValue() throws ClassCastException {
        return (Double)this.values[0];
    }
    
    public List<Double> getDoubleValues() throws ClassCastException {
        return Collections.unmodifiableList((List<? extends Double>)Arrays.asList((T[])this.values));
    }
    
    public Integer getIntegerValue() throws ClassCastException {
        return (Integer)this.values[0];
    }
    
    public List<Integer> getIntegerValues() throws ClassCastException {
        return Collections.unmodifiableList((List<? extends Integer>)Arrays.asList((T[])this.values));
    }
    
    public Long getLongValue() throws ClassCastException {
        return (Long)this.values[0];
    }
    
    public List<Long> getLongValues() throws ClassCastException {
        return Collections.unmodifiableList((List<? extends Long>)Arrays.asList((T[])this.values));
    }
    
    public String getStringValue() throws ClassCastException {
        return (String)this.values[0];
    }
    
    public List<String> getStringValues() throws ClassCastException {
        return Collections.unmodifiableList((List<? extends String>)Arrays.asList((T[])this.values));
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        this.toString(buffer);
        return buffer.toString();
    }
    
    public void toString(final StringBuilder buffer) {
        buffer.append("MonitorAttribute(name='");
        buffer.append(this.name);
        buffer.append("', values={");
        for (int i = 0; i < this.values.length; ++i) {
            if (i > 0) {
                buffer.append(", ");
            }
            buffer.append('\'');
            buffer.append(String.valueOf(this.values[i]));
            buffer.append('\'');
        }
        buffer.append("})");
    }
}
