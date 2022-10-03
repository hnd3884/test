package com.unboundid.util.json;

import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class JSONNull extends JSONValue
{
    public static final JSONNull NULL;
    private static final long serialVersionUID = -8359265286375144526L;
    
    @Override
    public int hashCode() {
        return 1;
    }
    
    @Override
    public boolean equals(final Object o) {
        return o == this || o instanceof JSONNull;
    }
    
    @Override
    public boolean equals(final JSONValue v, final boolean ignoreFieldNameCase, final boolean ignoreValueCase, final boolean ignoreArrayOrder) {
        return v instanceof JSONNull;
    }
    
    @Override
    public String toString() {
        return "null";
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("null");
    }
    
    @Override
    public String toSingleLineString() {
        return "null";
    }
    
    @Override
    public void toSingleLineString(final StringBuilder buffer) {
        buffer.append("null");
    }
    
    @Override
    public String toNormalizedString() {
        return "null";
    }
    
    @Override
    public void toNormalizedString(final StringBuilder buffer) {
        buffer.append("null");
    }
    
    @Override
    public String toNormalizedString(final boolean ignoreFieldNameCase, final boolean ignoreValueCase, final boolean ignoreArrayOrder) {
        return "null";
    }
    
    @Override
    public void toNormalizedString(final StringBuilder buffer, final boolean ignoreFieldNameCase, final boolean ignoreValueCase, final boolean ignoreArrayOrder) {
        buffer.append("null");
    }
    
    @Override
    public void appendToJSONBuffer(final JSONBuffer buffer) {
        buffer.appendNull();
    }
    
    @Override
    public void appendToJSONBuffer(final String fieldName, final JSONBuffer buffer) {
        buffer.appendNull(fieldName);
    }
    
    static {
        NULL = new JSONNull();
    }
}
