package com.unboundid.util.json;

import java.util.Arrays;
import com.unboundid.util.StaticUtils;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.LinkedList;
import com.unboundid.util.ByteStringBuffer;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.Mutable;
import java.io.Serializable;

@Mutable
@ThreadSafety(level = ThreadSafetyLevel.NOT_THREADSAFE)
public final class JSONBuffer implements Serializable
{
    private static final int DEFAULT_MAX_BUFFER_SIZE = 1048576;
    private static final long serialVersionUID = 5946166401452532693L;
    private final boolean multiLine;
    private boolean needComma;
    private ByteStringBuffer buffer;
    private final int maxBufferSize;
    private final LinkedList<String> indents;
    
    public JSONBuffer() {
        this(1048576);
    }
    
    public JSONBuffer(final int maxBufferSize) {
        this(null, maxBufferSize, false);
    }
    
    public JSONBuffer(final ByteStringBuffer buffer, final int maxBufferSize, final boolean multiLine) {
        this.needComma = false;
        this.multiLine = multiLine;
        this.maxBufferSize = maxBufferSize;
        this.indents = new LinkedList<String>();
        this.needComma = false;
        if (buffer == null) {
            this.buffer = new ByteStringBuffer();
        }
        else {
            this.buffer = buffer;
        }
    }
    
    public void clear() {
        this.buffer.clear();
        if (this.maxBufferSize > 0 && this.buffer.capacity() > this.maxBufferSize) {
            this.buffer.setCapacity(this.maxBufferSize);
        }
        this.needComma = false;
        this.indents.clear();
    }
    
    public void setBuffer(final ByteStringBuffer buffer) {
        if (buffer == null) {
            this.buffer = new ByteStringBuffer();
        }
        else {
            this.buffer = buffer;
        }
        this.needComma = false;
        this.indents.clear();
    }
    
    public int length() {
        return this.buffer.length();
    }
    
    public void beginObject() {
        this.addComma();
        this.buffer.append((CharSequence)"{ ");
        this.needComma = false;
        this.addIndent(2);
    }
    
    public void beginObject(final String fieldName) {
        this.addComma();
        final int startPos = this.buffer.length();
        JSONString.encodeString(fieldName, this.buffer);
        final int fieldNameLength = this.buffer.length() - startPos;
        this.buffer.append((CharSequence)":{ ");
        this.needComma = false;
        this.addIndent(fieldNameLength + 3);
    }
    
    public void endObject() {
        if (this.needComma) {
            this.buffer.append(' ');
        }
        this.buffer.append('}');
        this.needComma = true;
        this.removeIndent();
    }
    
    public void beginArray() {
        this.addComma();
        this.buffer.append((CharSequence)"[ ");
        this.needComma = false;
        this.addIndent(2);
    }
    
    public void beginArray(final String fieldName) {
        this.addComma();
        final int startPos = this.buffer.length();
        JSONString.encodeString(fieldName, this.buffer);
        final int fieldNameLength = this.buffer.length() - startPos;
        this.buffer.append((CharSequence)":[ ");
        this.needComma = false;
        this.addIndent(fieldNameLength + 3);
    }
    
    public void endArray() {
        if (this.needComma) {
            this.buffer.append(' ');
        }
        this.buffer.append(']');
        this.needComma = true;
        this.removeIndent();
    }
    
    public void appendBoolean(final boolean value) {
        this.addComma();
        if (value) {
            this.buffer.append((CharSequence)"true");
        }
        else {
            this.buffer.append((CharSequence)"false");
        }
        this.needComma = true;
    }
    
    public void appendBoolean(final String fieldName, final boolean value) {
        this.addComma();
        JSONString.encodeString(fieldName, this.buffer);
        if (value) {
            this.buffer.append((CharSequence)":true");
        }
        else {
            this.buffer.append((CharSequence)":false");
        }
        this.needComma = true;
    }
    
    public void appendNull() {
        this.addComma();
        this.buffer.append((CharSequence)"null");
        this.needComma = true;
    }
    
    public void appendNull(final String fieldName) {
        this.addComma();
        JSONString.encodeString(fieldName, this.buffer);
        this.buffer.append((CharSequence)":null");
        this.needComma = true;
    }
    
    public void appendNumber(final BigDecimal value) {
        this.addComma();
        this.buffer.append((CharSequence)value.toPlainString());
        this.needComma = true;
    }
    
    public void appendNumber(final int value) {
        this.addComma();
        this.buffer.append(value);
        this.needComma = true;
    }
    
    public void appendNumber(final long value) {
        this.addComma();
        this.buffer.append(value);
        this.needComma = true;
    }
    
    public void appendNumber(final String value) {
        this.addComma();
        this.buffer.append((CharSequence)value);
        this.needComma = true;
    }
    
    public void appendNumber(final String fieldName, final BigDecimal value) {
        this.addComma();
        JSONString.encodeString(fieldName, this.buffer);
        this.buffer.append(':');
        this.buffer.append((CharSequence)value.toPlainString());
        this.needComma = true;
    }
    
    public void appendNumber(final String fieldName, final int value) {
        this.addComma();
        JSONString.encodeString(fieldName, this.buffer);
        this.buffer.append(':');
        this.buffer.append(value);
        this.needComma = true;
    }
    
    public void appendNumber(final String fieldName, final long value) {
        this.addComma();
        JSONString.encodeString(fieldName, this.buffer);
        this.buffer.append(':');
        this.buffer.append(value);
        this.needComma = true;
    }
    
    public void appendNumber(final String fieldName, final String value) {
        this.addComma();
        JSONString.encodeString(fieldName, this.buffer);
        this.buffer.append(':');
        this.buffer.append((CharSequence)value);
        this.needComma = true;
    }
    
    public void appendString(final String value) {
        this.addComma();
        JSONString.encodeString(value, this.buffer);
        this.needComma = true;
    }
    
    public void appendString(final String fieldName, final String value) {
        this.addComma();
        JSONString.encodeString(fieldName, this.buffer);
        this.buffer.append(':');
        JSONString.encodeString(value, this.buffer);
        this.needComma = true;
    }
    
    public void appendValue(final JSONValue value) {
        value.appendToJSONBuffer(this);
    }
    
    public void appendValue(final String fieldName, final JSONValue value) {
        value.appendToJSONBuffer(fieldName, this);
    }
    
    public ByteStringBuffer getBuffer() {
        return this.buffer;
    }
    
    public void writeTo(final OutputStream outputStream) throws IOException {
        this.buffer.write(outputStream);
    }
    
    @Override
    public String toString() {
        return this.buffer.toString();
    }
    
    public JSONObject toJSONObject() throws JSONException {
        return new JSONObject(this.buffer.toString());
    }
    
    private void addComma() {
        if (this.needComma) {
            this.buffer.append(',');
            if (this.multiLine) {
                this.buffer.append(StaticUtils.EOL_BYTES);
                this.buffer.append((CharSequence)this.indents.getLast());
            }
            else {
                this.buffer.append(' ');
            }
        }
    }
    
    private void addIndent(final int size) {
        if (this.multiLine) {
            final char[] spaces = new char[size];
            Arrays.fill(spaces, ' ');
            final String indentStr = new String(spaces);
            if (this.indents.isEmpty()) {
                this.indents.add(indentStr);
            }
            else {
                this.indents.add(this.indents.getLast() + indentStr);
            }
        }
    }
    
    private void removeIndent() {
        if (this.multiLine && !this.indents.isEmpty()) {
            this.indents.removeLast();
        }
    }
}
