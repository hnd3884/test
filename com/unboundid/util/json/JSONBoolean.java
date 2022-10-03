package com.unboundid.util.json;

import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class JSONBoolean extends JSONValue
{
    public static final JSONBoolean FALSE;
    public static final JSONBoolean TRUE;
    private static final long serialVersionUID = -5090296701442873481L;
    private final boolean booleanValue;
    private final String stringRepresentation;
    
    public JSONBoolean(final boolean booleanValue) {
        this.booleanValue = booleanValue;
        this.stringRepresentation = (booleanValue ? "true" : "false");
    }
    
    public boolean booleanValue() {
        return this.booleanValue;
    }
    
    @Override
    public int hashCode() {
        return this.booleanValue ? Boolean.TRUE.hashCode() : Boolean.FALSE.hashCode();
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof JSONBoolean) {
            final JSONBoolean b = (JSONBoolean)o;
            return b.booleanValue == this.booleanValue;
        }
        return false;
    }
    
    @Override
    public boolean equals(final JSONValue v, final boolean ignoreFieldNameCase, final boolean ignoreValueCase, final boolean ignoreArrayOrder) {
        return v instanceof JSONBoolean && this.booleanValue == ((JSONBoolean)v).booleanValue;
    }
    
    @Override
    public String toString() {
        return this.stringRepresentation;
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append(this.stringRepresentation);
    }
    
    @Override
    public String toSingleLineString() {
        return this.stringRepresentation;
    }
    
    @Override
    public void toSingleLineString(final StringBuilder buffer) {
        buffer.append(this.stringRepresentation);
    }
    
    @Override
    public String toNormalizedString() {
        return this.stringRepresentation;
    }
    
    @Override
    public void toNormalizedString(final StringBuilder buffer) {
        buffer.append(this.stringRepresentation);
    }
    
    @Override
    public String toNormalizedString(final boolean ignoreFieldNameCase, final boolean ignoreValueCase, final boolean ignoreArrayOrder) {
        return this.stringRepresentation;
    }
    
    @Override
    public void toNormalizedString(final StringBuilder buffer, final boolean ignoreFieldNameCase, final boolean ignoreValueCase, final boolean ignoreArrayOrder) {
        buffer.append(this.stringRepresentation);
    }
    
    @Override
    public void appendToJSONBuffer(final JSONBuffer buffer) {
        buffer.appendBoolean(this.booleanValue);
    }
    
    @Override
    public void appendToJSONBuffer(final String fieldName, final JSONBuffer buffer) {
        buffer.appendBoolean(fieldName, this.booleanValue);
    }
    
    static {
        FALSE = new JSONBoolean(false);
        TRUE = new JSONBoolean(true);
    }
}
