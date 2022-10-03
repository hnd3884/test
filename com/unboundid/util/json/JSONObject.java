package com.unboundid.util.json;

import java.util.TreeMap;
import com.unboundid.util.ByteStringBuffer;
import java.util.HashMap;
import java.util.Iterator;
import java.math.BigDecimal;
import java.util.List;
import java.util.ArrayList;
import com.unboundid.util.Debug;
import java.util.LinkedHashMap;
import com.unboundid.util.StaticUtils;
import java.util.Collections;
import java.util.Map;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class JSONObject extends JSONValue
{
    public static final JSONObject EMPTY_OBJECT;
    private static final long serialVersionUID = -4209509956709292141L;
    private int decodePos;
    private Integer hashCode;
    private final Map<String, JSONValue> fields;
    private String stringRepresentation;
    private final StringBuilder decodeBuffer;
    
    public JSONObject(final JSONField... fields) {
        if (fields == null || fields.length == 0) {
            this.fields = Collections.emptyMap();
        }
        else {
            final LinkedHashMap<String, JSONValue> m = new LinkedHashMap<String, JSONValue>(StaticUtils.computeMapCapacity(fields.length));
            for (final JSONField f : fields) {
                m.put(f.getName(), f.getValue());
            }
            this.fields = Collections.unmodifiableMap((Map<? extends String, ? extends JSONValue>)m);
        }
        this.hashCode = null;
        this.stringRepresentation = null;
        this.decodePos = -1;
        this.decodeBuffer = null;
    }
    
    public JSONObject(final Map<String, JSONValue> fields) {
        if (fields == null) {
            this.fields = Collections.emptyMap();
        }
        else {
            this.fields = Collections.unmodifiableMap((Map<? extends String, ? extends JSONValue>)new LinkedHashMap<String, JSONValue>(fields));
        }
        this.hashCode = null;
        this.stringRepresentation = null;
        this.decodePos = -1;
        this.decodeBuffer = null;
    }
    
    public JSONObject(final String stringRepresentation) throws JSONException {
        this.stringRepresentation = stringRepresentation;
        final char[] chars = stringRepresentation.toCharArray();
        this.decodePos = 0;
        this.decodeBuffer = new StringBuilder(chars.length);
        final Object firstToken = this.readToken(chars);
        if (!firstToken.equals('{')) {
            throw new JSONException(JSONMessages.ERR_OBJECT_DOESNT_START_WITH_BRACE.get(stringRepresentation));
        }
        final LinkedHashMap<String, JSONValue> m = new LinkedHashMap<String, JSONValue>(StaticUtils.computeMapCapacity(10));
        this.readObject(chars, m);
        this.fields = Collections.unmodifiableMap((Map<? extends String, ? extends JSONValue>)m);
        this.skipWhitespace(chars);
        if (this.decodePos < chars.length) {
            throw new JSONException(JSONMessages.ERR_OBJECT_DATA_BEYOND_END.get(stringRepresentation, this.decodePos));
        }
    }
    
    JSONObject(final LinkedHashMap<String, JSONValue> fields, final String stringRepresentation) {
        this.fields = Collections.unmodifiableMap((Map<? extends String, ? extends JSONValue>)fields);
        this.stringRepresentation = stringRepresentation;
        this.hashCode = null;
        this.decodePos = -1;
        this.decodeBuffer = null;
    }
    
    private Object readToken(final char[] chars) throws JSONException {
        this.skipWhitespace(chars);
        final char c = this.readCharacter(chars, false);
        switch (c) {
            case ',':
            case ':':
            case '[':
            case ']':
            case '{':
            case '}': {
                ++this.decodePos;
                return c;
            }
            case '\"': {
                return this.readString(chars);
            }
            case 'f':
            case 't': {
                return this.readBoolean(chars);
            }
            case 'n': {
                return this.readNull(chars);
            }
            case '-':
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9': {
                return this.readNumber(chars);
            }
            default: {
                throw new JSONException(JSONMessages.ERR_OBJECT_INVALID_FIRST_TOKEN_CHAR.get(new String(chars), String.valueOf(c), this.decodePos));
            }
        }
    }
    
    private void skipWhitespace(final char[] chars) throws JSONException {
        while (this.decodePos < chars.length) {
            switch (chars[this.decodePos]) {
                case '\t':
                case '\n':
                case '\r':
                case ' ': {
                    ++this.decodePos;
                    continue;
                }
                case '/': {
                    final int commentStartPos = this.decodePos;
                    if (this.decodePos + 1 >= chars.length) {
                        return;
                    }
                    if (chars[this.decodePos + 1] == '/') {
                        this.decodePos += 2;
                        while (this.decodePos < chars.length && chars[this.decodePos] != '\n') {
                            if (chars[this.decodePos] == '\r') {
                                break;
                            }
                            ++this.decodePos;
                        }
                        continue;
                    }
                    if (chars[this.decodePos + 1] != '*') {
                        return;
                    }
                    this.decodePos += 2;
                    boolean closeFound = false;
                    while (this.decodePos < chars.length) {
                        if (chars[this.decodePos] == '*' && this.decodePos + 1 < chars.length && chars[this.decodePos + 1] == '/') {
                            closeFound = true;
                            this.decodePos += 2;
                            break;
                        }
                        ++this.decodePos;
                    }
                    if (!closeFound) {
                        throw new JSONException(JSONMessages.ERR_OBJECT_UNCLOSED_COMMENT.get(new String(chars), commentStartPos));
                    }
                    continue;
                }
                case '#': {
                    while (this.decodePos < chars.length && chars[this.decodePos] != '\n') {
                        if (chars[this.decodePos] == '\r') {
                            break;
                        }
                        ++this.decodePos;
                    }
                    continue;
                }
                default: {}
            }
        }
    }
    
    private char readCharacter(final char[] chars, final boolean advancePosition) throws JSONException {
        if (this.decodePos >= chars.length) {
            throw new JSONException(JSONMessages.ERR_OBJECT_UNEXPECTED_END_OF_STRING.get(new String(chars)));
        }
        final char c = chars[this.decodePos];
        if (advancePosition) {
            ++this.decodePos;
        }
        return c;
    }
    
    private JSONString readString(final char[] chars) throws JSONException {
        final int startPos = this.decodePos++;
        this.decodeBuffer.setLength(0);
        int escapedCharPos = 0;
        char escapedChar = '\0';
    Label_0323:
        while (true) {
            final char c = this.readCharacter(chars, true);
            if (c == '\\') {
                escapedCharPos = this.decodePos;
                escapedChar = this.readCharacter(chars, true);
                switch (escapedChar) {
                    case '\"':
                    case '/':
                    case '\\': {
                        this.decodeBuffer.append(escapedChar);
                        continue;
                    }
                    case 'b': {
                        this.decodeBuffer.append('\b');
                        continue;
                    }
                    case 'f': {
                        this.decodeBuffer.append('\f');
                        continue;
                    }
                    case 'n': {
                        this.decodeBuffer.append('\n');
                        continue;
                    }
                    case 'r': {
                        this.decodeBuffer.append('\r');
                        continue;
                    }
                    case 't': {
                        this.decodeBuffer.append('\t');
                        continue;
                    }
                    case 'u': {
                        final char[] hexChars = { this.readCharacter(chars, true), this.readCharacter(chars, true), this.readCharacter(chars, true), this.readCharacter(chars, true) };
                        try {
                            this.decodeBuffer.append((char)Integer.parseInt(new String(hexChars), 16));
                            continue;
                        }
                        catch (final Exception e) {
                            Debug.debugException(e);
                            throw new JSONException(JSONMessages.ERR_OBJECT_INVALID_UNICODE_ESCAPE.get(new String(chars), escapedCharPos), e);
                        }
                    }
                    default: {
                        break Label_0323;
                    }
                }
            }
            else {
                if (c == '\"') {
                    return new JSONString(this.decodeBuffer.toString(), new String(chars, startPos, this.decodePos - startPos));
                }
                if (c <= '\u001f') {
                    throw new JSONException(JSONMessages.ERR_OBJECT_UNESCAPED_CONTROL_CHAR.get(new String(chars), String.format("%04X", (int)c), this.decodePos - 1));
                }
                this.decodeBuffer.append(c);
            }
        }
        throw new JSONException(JSONMessages.ERR_OBJECT_INVALID_ESCAPED_CHAR.get(new String(chars), escapedChar, escapedCharPos));
    }
    
    private JSONBoolean readBoolean(final char[] chars) throws JSONException {
        final int startPos = this.decodePos;
        final char firstCharacter = this.readCharacter(chars, true);
        if (firstCharacter == 't') {
            if (this.readCharacter(chars, true) == 'r' && this.readCharacter(chars, true) == 'u' && this.readCharacter(chars, true) == 'e') {
                return JSONBoolean.TRUE;
            }
        }
        else if (firstCharacter == 'f' && this.readCharacter(chars, true) == 'a' && this.readCharacter(chars, true) == 'l' && this.readCharacter(chars, true) == 's' && this.readCharacter(chars, true) == 'e') {
            return JSONBoolean.FALSE;
        }
        throw new JSONException(JSONMessages.ERR_OBJECT_UNABLE_TO_PARSE_BOOLEAN.get(new String(chars), startPos));
    }
    
    private JSONNull readNull(final char[] chars) throws JSONException {
        final int startPos = this.decodePos;
        if (this.readCharacter(chars, true) == 'n' && this.readCharacter(chars, true) == 'u' && this.readCharacter(chars, true) == 'l' && this.readCharacter(chars, true) == 'l') {
            return JSONNull.NULL;
        }
        throw new JSONException(JSONMessages.ERR_OBJECT_UNABLE_TO_PARSE_NULL.get(new String(chars), startPos));
    }
    
    private JSONNumber readNumber(final char[] chars) throws JSONException {
        final int startPos = this.decodePos;
        this.decodeBuffer.setLength(0);
    Label_0088:
        while (true) {
            final char c = this.readCharacter(chars, true);
            switch (c) {
                case '\t':
                case '\n':
                case '\r':
                case ' ':
                case ',':
                case ']':
                case '}': {
                    break Label_0088;
                }
                default: {
                    this.decodeBuffer.append(c);
                    continue;
                }
            }
        }
        --this.decodePos;
        return new JSONNumber(this.decodeBuffer.toString());
    }
    
    private JSONArray readArray(final char[] chars) throws JSONException {
        final ArrayList<JSONValue> values = new ArrayList<JSONValue>(10);
        boolean firstToken = true;
        while (true) {
            int p = this.decodePos;
            Object token = this.readToken(chars);
            if (token instanceof JSONValue) {
                values.add((JSONValue)token);
            }
            else if (token.equals('[')) {
                values.add(this.readArray(chars));
            }
            else if (token.equals('{')) {
                final LinkedHashMap<String, JSONValue> fieldMap = new LinkedHashMap<String, JSONValue>(StaticUtils.computeMapCapacity(10));
                values.add(this.readObject(chars, fieldMap));
            }
            else {
                if (token.equals(']') && firstToken) {
                    return JSONArray.EMPTY_ARRAY;
                }
                throw new JSONException(JSONMessages.ERR_OBJECT_INVALID_TOKEN_WHEN_ARRAY_VALUE_EXPECTED.get(new String(chars), String.valueOf(token), p));
            }
            firstToken = false;
            p = this.decodePos;
            token = this.readToken(chars);
            if (token.equals(']')) {
                return new JSONArray(values);
            }
            if (!token.equals(',')) {
                throw new JSONException(JSONMessages.ERR_OBJECT_INVALID_TOKEN_WHEN_ARRAY_COMMA_OR_BRACKET_EXPECTED.get(new String(chars), String.valueOf(token), p));
            }
        }
    }
    
    private JSONObject readObject(final char[] chars, final Map<String, JSONValue> fields) throws JSONException {
        boolean firstField = true;
        while (true) {
            int p = this.decodePos;
            Object token = this.readToken(chars);
            if (token instanceof JSONString) {
                final String fieldName = ((JSONString)token).stringValue();
                if (fields.containsKey(fieldName)) {
                    throw new JSONException(JSONMessages.ERR_OBJECT_DUPLICATE_FIELD.get(new String(chars), fieldName));
                }
                firstField = false;
                p = this.decodePos;
                token = this.readToken(chars);
                if (!token.equals(':')) {
                    throw new JSONException(JSONMessages.ERR_OBJECT_EXPECTED_COLON.get(new String(chars), String.valueOf(token), p));
                }
                p = this.decodePos;
                token = this.readToken(chars);
                if (token instanceof JSONValue) {
                    fields.put(fieldName, (JSONValue)token);
                }
                else if (token.equals('[')) {
                    final JSONArray a = this.readArray(chars);
                    fields.put(fieldName, a);
                }
                else {
                    if (!token.equals('{')) {
                        throw new JSONException(JSONMessages.ERR_OBJECT_EXPECTED_VALUE.get(new String(chars), String.valueOf(token), p, fieldName));
                    }
                    final LinkedHashMap<String, JSONValue> m = new LinkedHashMap<String, JSONValue>(StaticUtils.computeMapCapacity(10));
                    final JSONObject o = this.readObject(chars, m);
                    fields.put(fieldName, o);
                }
                p = this.decodePos;
                token = this.readToken(chars);
                if (token.equals('}')) {
                    return new JSONObject(fields);
                }
                if (!token.equals(',')) {
                    throw new JSONException(JSONMessages.ERR_OBJECT_EXPECTED_COMMA_OR_CLOSE_BRACE.get(new String(chars), String.valueOf(token), p));
                }
                continue;
            }
            else {
                if (firstField && token.equals('}')) {
                    return new JSONObject(fields);
                }
                throw new JSONException(JSONMessages.ERR_OBJECT_EXPECTED_STRING.get(new String(chars), String.valueOf(token), p));
            }
        }
    }
    
    public Map<String, JSONValue> getFields() {
        return this.fields;
    }
    
    public JSONValue getField(final String name) {
        return this.fields.get(name);
    }
    
    public String getFieldAsString(final String name) {
        final JSONValue value = this.fields.get(name);
        if (value == null || !(value instanceof JSONString)) {
            return null;
        }
        return ((JSONString)value).stringValue();
    }
    
    public Boolean getFieldAsBoolean(final String name) {
        final JSONValue value = this.fields.get(name);
        if (value == null || !(value instanceof JSONBoolean)) {
            return null;
        }
        return ((JSONBoolean)value).booleanValue();
    }
    
    public Integer getFieldAsInteger(final String name) {
        final JSONValue value = this.fields.get(name);
        if (value == null || !(value instanceof JSONNumber)) {
            return null;
        }
        try {
            final JSONNumber number = (JSONNumber)value;
            return number.getValue().intValueExact();
        }
        catch (final Exception e) {
            Debug.debugException(e);
            return null;
        }
    }
    
    public Long getFieldAsLong(final String name) {
        final JSONValue value = this.fields.get(name);
        if (value == null || !(value instanceof JSONNumber)) {
            return null;
        }
        try {
            final JSONNumber number = (JSONNumber)value;
            return number.getValue().longValueExact();
        }
        catch (final Exception e) {
            Debug.debugException(e);
            return null;
        }
    }
    
    public BigDecimal getFieldAsBigDecimal(final String name) {
        final JSONValue value = this.fields.get(name);
        if (value == null || !(value instanceof JSONNumber)) {
            return null;
        }
        return ((JSONNumber)value).getValue();
    }
    
    public JSONObject getFieldAsObject(final String name) {
        final JSONValue value = this.fields.get(name);
        if (value == null || !(value instanceof JSONObject)) {
            return null;
        }
        return (JSONObject)value;
    }
    
    public List<JSONValue> getFieldAsArray(final String name) {
        final JSONValue value = this.fields.get(name);
        if (value == null || !(value instanceof JSONArray)) {
            return null;
        }
        return ((JSONArray)value).getValues();
    }
    
    public boolean hasNullField(final String name) {
        final JSONValue value = this.fields.get(name);
        return value != null && value instanceof JSONNull;
    }
    
    @Override
    public int hashCode() {
        if (this.hashCode == null) {
            int hc = 0;
            for (final Map.Entry<String, JSONValue> e : this.fields.entrySet()) {
                hc += e.getKey().hashCode() + e.getValue().hashCode();
            }
            this.hashCode = hc;
        }
        return this.hashCode;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof JSONObject) {
            final JSONObject obj = (JSONObject)o;
            return this.fields.equals(obj.fields);
        }
        return false;
    }
    
    public boolean equals(final JSONObject o, final boolean ignoreFieldNameCase, final boolean ignoreValueCase, final boolean ignoreArrayOrder) {
        if (!ignoreFieldNameCase && !ignoreValueCase && !ignoreArrayOrder) {
            return this.fields.equals(o.fields);
        }
        if (this.fields.size() != o.fields.size()) {
            return false;
        }
        if (!ignoreFieldNameCase) {
            for (final Map.Entry<String, JSONValue> e : this.fields.entrySet()) {
                final JSONValue thisValue = e.getValue();
                final JSONValue thatValue = o.fields.get(e.getKey());
                if (thatValue == null) {
                    return false;
                }
                if (!thisValue.equals(thatValue, ignoreFieldNameCase, ignoreValueCase, ignoreArrayOrder)) {
                    return false;
                }
            }
            return true;
        }
        final HashMap<String, JSONValue> thatMap = new HashMap<String, JSONValue>(o.fields);
        for (final Map.Entry<String, JSONValue> thisEntry : this.fields.entrySet()) {
            final String thisFieldName = thisEntry.getKey();
            final JSONValue thisValue2 = thisEntry.getValue();
            final Iterator<Map.Entry<String, JSONValue>> thatIterator = thatMap.entrySet().iterator();
            boolean found = false;
            while (thatIterator.hasNext()) {
                final Map.Entry<String, JSONValue> thatEntry = thatIterator.next();
                final String thatFieldName = thatEntry.getKey();
                if (!thisFieldName.equalsIgnoreCase(thatFieldName)) {
                    continue;
                }
                final JSONValue thatValue2 = thatEntry.getValue();
                if (thisValue2.equals(thatValue2, ignoreFieldNameCase, ignoreValueCase, ignoreArrayOrder)) {
                    found = true;
                    thatIterator.remove();
                    break;
                }
            }
            if (!found) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public boolean equals(final JSONValue v, final boolean ignoreFieldNameCase, final boolean ignoreValueCase, final boolean ignoreArrayOrder) {
        return v instanceof JSONObject && this.equals((JSONObject)v, ignoreFieldNameCase, ignoreValueCase, ignoreArrayOrder);
    }
    
    @Override
    public String toString() {
        if (this.stringRepresentation == null) {
            final StringBuilder buffer = new StringBuilder();
            this.toString(buffer);
            this.stringRepresentation = buffer.toString();
        }
        return this.stringRepresentation;
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        if (this.stringRepresentation != null) {
            buffer.append(this.stringRepresentation);
            return;
        }
        buffer.append("{ ");
        final Iterator<Map.Entry<String, JSONValue>> iterator = this.fields.entrySet().iterator();
        while (iterator.hasNext()) {
            final Map.Entry<String, JSONValue> e = iterator.next();
            JSONString.encodeString(e.getKey(), buffer);
            buffer.append(':');
            e.getValue().toString(buffer);
            if (iterator.hasNext()) {
                buffer.append(',');
            }
            buffer.append(' ');
        }
        buffer.append('}');
    }
    
    public String toMultiLineString() {
        final JSONBuffer jsonBuffer = new JSONBuffer(null, 0, true);
        this.appendToJSONBuffer(jsonBuffer);
        return jsonBuffer.toString();
    }
    
    @Override
    public String toSingleLineString() {
        final StringBuilder buffer = new StringBuilder();
        this.toSingleLineString(buffer);
        return buffer.toString();
    }
    
    @Override
    public void toSingleLineString(final StringBuilder buffer) {
        buffer.append("{ ");
        final Iterator<Map.Entry<String, JSONValue>> iterator = this.fields.entrySet().iterator();
        while (iterator.hasNext()) {
            final Map.Entry<String, JSONValue> e = iterator.next();
            JSONString.encodeString(e.getKey(), buffer);
            buffer.append(':');
            e.getValue().toSingleLineString(buffer);
            if (iterator.hasNext()) {
                buffer.append(',');
            }
            buffer.append(' ');
        }
        buffer.append('}');
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
        final TreeMap<String, String> m = new TreeMap<String, String>();
        for (final Map.Entry<String, JSONValue> e : this.fields.entrySet()) {
            m.put(new JSONString(e.getKey()).toNormalizedString(false, ignoreFieldNameCase, false), e.getValue().toNormalizedString(ignoreFieldNameCase, ignoreValueCase, ignoreArrayOrder));
        }
        buffer.append('{');
        final Iterator<Map.Entry<String, String>> iterator = m.entrySet().iterator();
        while (iterator.hasNext()) {
            final Map.Entry<String, String> e2 = iterator.next();
            buffer.append(e2.getKey());
            buffer.append(':');
            buffer.append(e2.getValue());
            if (iterator.hasNext()) {
                buffer.append(',');
            }
        }
        buffer.append('}');
    }
    
    @Override
    public void appendToJSONBuffer(final JSONBuffer buffer) {
        buffer.beginObject();
        for (final Map.Entry<String, JSONValue> field : this.fields.entrySet()) {
            final String name = field.getKey();
            final JSONValue value = field.getValue();
            value.appendToJSONBuffer(name, buffer);
        }
        buffer.endObject();
    }
    
    @Override
    public void appendToJSONBuffer(final String fieldName, final JSONBuffer buffer) {
        buffer.beginObject(fieldName);
        for (final Map.Entry<String, JSONValue> field : this.fields.entrySet()) {
            final String name = field.getKey();
            final JSONValue value = field.getValue();
            value.appendToJSONBuffer(name, buffer);
        }
        buffer.endObject();
    }
    
    static {
        EMPTY_OBJECT = new JSONObject(Collections.emptyMap());
    }
}
