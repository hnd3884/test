package com.unboundid.ldap.sdk.unboundidds.tasks;

import java.util.Date;
import com.unboundid.util.Validator;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import java.io.Serializable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class TaskProperty implements Serializable
{
    private static final long serialVersionUID = 8438462010090371903L;
    private final boolean advanced;
    private final boolean multiValued;
    private final boolean required;
    private final Class<?> dataType;
    private final Object[] allowedValues;
    private final String attributeName;
    private final String description;
    private final String displayName;
    
    public TaskProperty(final String attributeName, final String displayName, final String description, final Class<?> dataType, final boolean required, final boolean multiValued, final boolean advanced) {
        this(attributeName, displayName, description, dataType, required, multiValued, advanced, null);
    }
    
    public TaskProperty(final String attributeName, final String displayName, final String description, final Class<?> dataType, final boolean required, final boolean multiValued, final boolean advanced, final Object[] allowedValues) {
        Validator.ensureNotNull(attributeName, displayName, description, dataType);
        Validator.ensureTrue(dataType.equals(Boolean.class) || dataType.equals(Date.class) || dataType.equals(Long.class) || dataType.equals(String.class));
        Validator.ensureFalse(required && advanced, "TaskProperty.required and advanced must not both be true.");
        this.attributeName = attributeName;
        this.displayName = displayName;
        this.description = description;
        this.dataType = dataType;
        this.required = required;
        this.multiValued = multiValued;
        this.advanced = advanced;
        if (allowedValues == null || allowedValues.length == 0) {
            this.allowedValues = null;
        }
        else {
            for (final Object o : allowedValues) {
                Validator.ensureTrue(dataType.equals(o.getClass()));
            }
            this.allowedValues = allowedValues;
        }
    }
    
    public String getAttributeName() {
        return this.attributeName;
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
    
    public boolean isRequired() {
        return this.required;
    }
    
    public boolean isMultiValued() {
        return this.multiValued;
    }
    
    public boolean isAdvanced() {
        return this.advanced;
    }
    
    public Object[] getAllowedValues() {
        return this.allowedValues;
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        this.toString(buffer);
        return buffer.toString();
    }
    
    public void toString(final StringBuilder buffer) {
        buffer.append("TaskProperty(attrName='");
        buffer.append(this.attributeName);
        buffer.append("', displayName='");
        buffer.append(this.displayName);
        buffer.append("', description='");
        buffer.append(this.description);
        buffer.append("', dataType='");
        buffer.append(this.dataType.getName());
        buffer.append("', required=");
        buffer.append(this.required);
        buffer.append("', multiValued=");
        buffer.append(this.multiValued);
        buffer.append("', advanced=");
        buffer.append(this.advanced);
        if (this.allowedValues != null) {
            buffer.append(", allowedValues={");
            for (int i = 0; i < this.allowedValues.length; ++i) {
                if (i > 0) {
                    buffer.append(", ");
                }
                buffer.append('\'');
                buffer.append(this.allowedValues[i]);
                buffer.append('\'');
            }
            buffer.append('}');
        }
        buffer.append(')');
    }
}
