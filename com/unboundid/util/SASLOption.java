package com.unboundid.util;

import java.io.Serializable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class SASLOption implements Serializable
{
    private static final long serialVersionUID = -683675804002105357L;
    private final boolean isMultiValued;
    private final boolean isRequired;
    private final String description;
    private final String name;
    
    public SASLOption(final String name, final String description, final boolean isRequired, final boolean isMultiValued) {
        this.name = name;
        this.description = description;
        this.isRequired = isRequired;
        this.isMultiValued = isMultiValued;
    }
    
    public String getName() {
        return this.name;
    }
    
    public String getDescription() {
        return this.description;
    }
    
    public boolean isRequired() {
        return this.isRequired;
    }
    
    public boolean isMultiValued() {
        return this.isMultiValued;
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        this.toString(buffer);
        return buffer.toString();
    }
    
    public void toString(final StringBuilder buffer) {
        buffer.append("SASLOption(name='");
        buffer.append(this.name);
        buffer.append("', description='");
        buffer.append(this.description);
        buffer.append("', isRequired=");
        buffer.append(this.isRequired);
        buffer.append(", isMultiValued=");
        buffer.append(this.isMultiValued);
        buffer.append(')');
    }
}
