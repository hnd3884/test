package com.unboundid.util.json;

import com.unboundid.util.Validator;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import java.io.Serializable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class JSONField implements Serializable
{
    private static final long serialVersionUID = -1397826405959590851L;
    private final JSONValue value;
    private final String name;
    
    public JSONField(final String name, final JSONValue value) {
        Validator.ensureNotNull(name);
        Validator.ensureNotNull(value);
        this.name = name;
        this.value = value;
    }
    
    public JSONField(final String name, final boolean value) {
        this(name, value ? JSONBoolean.TRUE : JSONBoolean.FALSE);
    }
    
    public JSONField(final String name, final long value) {
        this(name, new JSONNumber(value));
    }
    
    public JSONField(final String name, final double value) {
        this(name, new JSONNumber(value));
    }
    
    public JSONField(final String name, final String value) {
        this(name, new JSONString(value));
    }
    
    public String getName() {
        return this.name;
    }
    
    public JSONValue getValue() {
        return this.value;
    }
    
    @Override
    public int hashCode() {
        return this.name.hashCode() + this.value.hashCode();
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof JSONField) {
            final JSONField f = (JSONField)o;
            return this.name.equals(f.name) && this.value.equals(f.value);
        }
        return false;
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        this.toString(buffer);
        return buffer.toString();
    }
    
    public void toString(final StringBuilder buffer) {
        JSONString.encodeString(this.name, buffer);
        buffer.append(':');
        this.value.toString(buffer);
    }
}
