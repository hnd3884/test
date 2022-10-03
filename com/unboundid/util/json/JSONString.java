package com.unboundid.util.json;

import com.unboundid.util.StaticUtils;
import com.unboundid.util.ByteStringBuffer;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class JSONString extends JSONValue
{
    private static final long serialVersionUID = -4677194657299153890L;
    private String jsonStringRepresentation;
    private final String value;
    
    public JSONString(final String value) {
        this.value = value;
        this.jsonStringRepresentation = null;
    }
    
    JSONString(final String javaString, final String jsonString) {
        this.value = javaString;
        this.jsonStringRepresentation = jsonString;
    }
    
    public String stringValue() {
        return this.value;
    }
    
    @Override
    public int hashCode() {
        return this.stringValue().hashCode();
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof JSONString) {
            final JSONString s = (JSONString)o;
            return this.value.equals(s.value);
        }
        return false;
    }
    
    public boolean equals(final JSONString s, final boolean ignoreCase) {
        if (ignoreCase) {
            return this.value.equalsIgnoreCase(s.value);
        }
        return this.value.equals(s.value);
    }
    
    @Override
    public boolean equals(final JSONValue v, final boolean ignoreFieldNameCase, final boolean ignoreValueCase, final boolean ignoreArrayOrder) {
        return v instanceof JSONString && this.equals((JSONString)v, ignoreValueCase);
    }
    
    @Override
    public String toString() {
        if (this.jsonStringRepresentation == null) {
            final StringBuilder buffer = new StringBuilder();
            this.toString(buffer);
            this.jsonStringRepresentation = buffer.toString();
        }
        return this.jsonStringRepresentation;
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        if (this.jsonStringRepresentation != null) {
            buffer.append(this.jsonStringRepresentation);
        }
        else {
            final boolean emptyBufferProvided = buffer.length() == 0;
            encodeString(this.value, buffer);
            if (emptyBufferProvided) {
                this.jsonStringRepresentation = buffer.toString();
            }
        }
    }
    
    @Override
    public String toSingleLineString() {
        return this.toString();
    }
    
    @Override
    public void toSingleLineString(final StringBuilder buffer) {
        this.toString(buffer);
    }
    
    static void encodeString(final String s, final StringBuilder buffer) {
        buffer.append('\"');
        for (final char c : s.toCharArray()) {
            switch (c) {
                case '\"': {
                    buffer.append("\\\"");
                    break;
                }
                case '\\': {
                    buffer.append("\\\\");
                    break;
                }
                case '\b': {
                    buffer.append("\\b");
                    break;
                }
                case '\f': {
                    buffer.append("\\f");
                    break;
                }
                case '\n': {
                    buffer.append("\\n");
                    break;
                }
                case '\r': {
                    buffer.append("\\r");
                    break;
                }
                case '\t': {
                    buffer.append("\\t");
                    break;
                }
                default: {
                    if (c <= '\u001f') {
                        buffer.append("\\u");
                        buffer.append(String.format("%04X", (int)c));
                        break;
                    }
                    buffer.append(c);
                    break;
                }
            }
        }
        buffer.append('\"');
    }
    
    static void encodeString(final String s, final ByteStringBuffer buffer) {
        buffer.append('\"');
        for (final char c : s.toCharArray()) {
            switch (c) {
                case '\"': {
                    buffer.append((CharSequence)"\\\"");
                    break;
                }
                case '\\': {
                    buffer.append((CharSequence)"\\\\");
                    break;
                }
                case '\b': {
                    buffer.append((CharSequence)"\\b");
                    break;
                }
                case '\f': {
                    buffer.append((CharSequence)"\\f");
                    break;
                }
                case '\n': {
                    buffer.append((CharSequence)"\\n");
                    break;
                }
                case '\r': {
                    buffer.append((CharSequence)"\\r");
                    break;
                }
                case '\t': {
                    buffer.append((CharSequence)"\\t");
                    break;
                }
                default: {
                    if (c <= '\u001f') {
                        buffer.append((CharSequence)"\\u");
                        buffer.append((CharSequence)String.format("%04X", (int)c));
                        break;
                    }
                    buffer.append(c);
                    break;
                }
            }
        }
        buffer.append('\"');
    }
    
    @Override
    public String toNormalizedString() {
        final StringBuilder buffer = new StringBuilder();
        this.toNormalizedString(buffer);
        return buffer.toString();
    }
    
    @Override
    public void toNormalizedString(final StringBuilder buffer) {
        this.toNormalizedString(buffer, false, true, false);
    }
    
    @Override
    public String toNormalizedString(final boolean ignoreFieldNameCase, final boolean ignoreValueCase, final boolean ignoreArrayOrder) {
        final StringBuilder buffer = new StringBuilder();
        this.toNormalizedString(buffer, ignoreFieldNameCase, ignoreValueCase, ignoreArrayOrder);
        return buffer.toString();
    }
    
    @Override
    public void toNormalizedString(final StringBuilder buffer, final boolean ignoreFieldNameCase, final boolean ignoreValueCase, final boolean ignoreArrayOrder) {
        buffer.append('\"');
        char[] charArray;
        if (ignoreValueCase) {
            charArray = StaticUtils.toLowerCase(this.value).toCharArray();
        }
        else {
            charArray = this.value.toCharArray();
        }
        for (final char c : charArray) {
            if (StaticUtils.isPrintable(c)) {
                buffer.append(c);
            }
            else {
                buffer.append("\\u");
                buffer.append(String.format("%04X", (int)c));
            }
        }
        buffer.append('\"');
    }
    
    @Override
    public void appendToJSONBuffer(final JSONBuffer buffer) {
        buffer.appendString(this.value);
    }
    
    @Override
    public void appendToJSONBuffer(final String fieldName, final JSONBuffer buffer) {
        buffer.appendString(fieldName, this.value);
    }
}
