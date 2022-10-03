package com.unboundid.util.json;

import java.util.List;
import java.util.ArrayList;
import com.unboundid.util.Debug;
import java.io.IOException;
import java.util.Map;
import java.util.LinkedHashMap;
import com.unboundid.util.StaticUtils;
import java.io.BufferedInputStream;
import java.io.InputStream;
import com.unboundid.util.ByteStringBuffer;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import java.io.Closeable;

@ThreadSafety(level = ThreadSafetyLevel.NOT_THREADSAFE)
public final class JSONObjectReader implements Closeable
{
    private final ByteStringBuffer currentObjectBytes;
    private final ByteStringBuffer stringBuffer;
    private final InputStream inputStream;
    
    public JSONObjectReader(final InputStream inputStream) {
        this(inputStream, true);
    }
    
    public JSONObjectReader(final InputStream inputStream, final boolean bufferInputStream) {
        if (bufferInputStream && !(inputStream instanceof BufferedInputStream)) {
            this.inputStream = new BufferedInputStream(inputStream);
        }
        else {
            this.inputStream = inputStream;
        }
        this.currentObjectBytes = new ByteStringBuffer();
        this.stringBuffer = new ByteStringBuffer();
    }
    
    public JSONObject readObject() throws IOException, JSONException {
        this.skipWhitespace();
        this.currentObjectBytes.clear();
        final Object firstToken = this.readToken(true);
        if (firstToken == null) {
            return null;
        }
        if (!firstToken.equals('{')) {
            throw new JSONException(JSONMessages.ERR_OBJECT_READER_ILLEGAL_START_OF_OBJECT.get(String.valueOf(firstToken)));
        }
        final LinkedHashMap<String, JSONValue> m = new LinkedHashMap<String, JSONValue>(StaticUtils.computeMapCapacity(10));
        this.readObject(m);
        return new JSONObject(m, this.currentObjectBytes.toString());
    }
    
    @Override
    public void close() throws IOException {
        this.inputStream.close();
    }
    
    private Object readToken(final boolean allowEndOfStream) throws IOException, JSONException {
        this.skipWhitespace();
        final Byte byteRead = this.readByte(allowEndOfStream);
        if (byteRead == null) {
            return null;
        }
        switch (byteRead) {
            case 123: {
                return '{';
            }
            case 125: {
                return '}';
            }
            case 91: {
                return '[';
            }
            case 93: {
                return ']';
            }
            case 58: {
                return ':';
            }
            case 44: {
                return ',';
            }
            case 34: {
                return this.readString();
            }
            case 102:
            case 116: {
                return this.readBoolean();
            }
            case 110: {
                return this.readNull();
            }
            case 45:
            case 48:
            case 49:
            case 50:
            case 51:
            case 52:
            case 53:
            case 54:
            case 55:
            case 56:
            case 57: {
                return this.readNumber();
            }
            default: {
                throw new JSONException(JSONMessages.ERR_OBJECT_READER_ILLEGAL_FIRST_CHAR_FOR_JSON_TOKEN.get(this.currentObjectBytes.length(), byteToCharString(byteRead)));
            }
        }
    }
    
    private void skipWhitespace() throws IOException, JSONException {
    Label_0267:
        while (true) {
            this.inputStream.mark(1);
            final Byte byteRead = this.readByte(true);
            if (byteRead == null) {
                return;
            }
            switch (byteRead) {
                case 9:
                case 10:
                case 13:
                case 32: {
                    continue;
                }
                case 47: {
                    final byte nextByte = this.readByte(false);
                    if (nextByte == 47) {
                        while (true) {
                            final Byte commentByte = this.readByte(true);
                            if (commentByte == null) {
                                return;
                            }
                            if (commentByte == 10) {
                                continue Label_0267;
                            }
                            if (commentByte == 13) {
                                continue Label_0267;
                            }
                        }
                    }
                    else {
                        if (nextByte == 42) {
                            while (true) {
                                final Byte commentByte = this.readByte(false);
                                if (commentByte == 42) {
                                    final Byte possibleSlashByte = this.readByte(false);
                                    if (possibleSlashByte == 47) {
                                        break;
                                    }
                                    continue;
                                }
                            }
                            continue;
                        }
                        throw new JSONException(JSONMessages.ERR_OBJECT_READER_ILLEGAL_SLASH_SKIPPING_WHITESPACE.get(this.currentObjectBytes.length()));
                    }
                    break;
                }
                case 35: {
                    while (true) {
                        final Byte commentByte = this.readByte(true);
                        if (commentByte == null) {
                            return;
                        }
                        if (commentByte == 10) {
                            continue Label_0267;
                        }
                        if (commentByte == 13) {
                            continue Label_0267;
                        }
                    }
                    break;
                }
                default: {
                    this.inputStream.reset();
                    this.currentObjectBytes.setLength(this.currentObjectBytes.length() - 1);
                    return;
                }
            }
        }
    }
    
    private Byte readByte(final boolean allowEndOfStream) throws IOException, JSONException {
        final int byteRead = this.inputStream.read();
        if (byteRead >= 0) {
            final byte b = (byte)(byteRead & 0xFF);
            this.currentObjectBytes.append(b);
            return b;
        }
        if (allowEndOfStream) {
            return null;
        }
        throw new JSONException(JSONMessages.ERR_OBJECT_READER_UNEXPECTED_END_OF_STREAM.get(this.currentObjectBytes.length()));
    }
    
    private JSONString readString() throws IOException, JSONException {
        this.stringBuffer.clear();
        final int jsonStringStartPos = this.currentObjectBytes.length() - 1;
        byte nextByte = 0;
    Label_0576:
        while (true) {
            final Byte byteRead = this.readByte(false);
            if ((byteRead & 0x80) == 0x80) {
                byte[] charBytes;
                if ((byteRead & 0xE0) == 0xC0) {
                    charBytes = new byte[] { byteRead, this.readByte(false) };
                }
                else if ((byteRead & 0xF0) == 0xE0) {
                    charBytes = new byte[] { byteRead, this.readByte(false), this.readByte(false) };
                }
                else {
                    if ((byteRead & 0xF8) != 0xF0) {
                        throw new JSONException(JSONMessages.ERR_OBJECT_READER_INVALID_UTF_8_BYTE_IN_STREAM.get(this.currentObjectBytes.length(), "0x" + StaticUtils.toHex(byteRead)));
                    }
                    charBytes = new byte[] { byteRead, this.readByte(false), this.readByte(false), this.readByte(false) };
                }
                this.stringBuffer.append((CharSequence)StaticUtils.toUTF8String(charBytes));
            }
            else if (byteRead == 92) {
                nextByte = this.readByte(false);
                switch (nextByte) {
                    case 34:
                    case 47:
                    case 92: {
                        this.stringBuffer.append(nextByte);
                        continue;
                    }
                    case 98: {
                        this.stringBuffer.append('\b');
                        continue;
                    }
                    case 102: {
                        this.stringBuffer.append('\f');
                        continue;
                    }
                    case 110: {
                        this.stringBuffer.append('\n');
                        continue;
                    }
                    case 114: {
                        this.stringBuffer.append('\r');
                        continue;
                    }
                    case 116: {
                        this.stringBuffer.append('\t');
                        continue;
                    }
                    case 117: {
                        final char[] hexChars = { (char)(this.readByte(false) & 0xFF), (char)(this.readByte(false) & 0xFF), (char)(this.readByte(false) & 0xFF), (char)(this.readByte(false) & 0xFF) };
                        try {
                            this.stringBuffer.append((char)Integer.parseInt(new String(hexChars), 16));
                            continue;
                        }
                        catch (final Exception e) {
                            Debug.debugException(e);
                            throw new JSONException(JSONMessages.ERR_OBJECT_READER_INVALID_UNICODE_ESCAPE.get(this.currentObjectBytes.length()), e);
                        }
                    }
                    default: {
                        break Label_0576;
                    }
                }
            }
            else {
                if (byteRead == 34) {
                    return new JSONString(this.stringBuffer.toString(), StaticUtils.toUTF8String(this.currentObjectBytes.getBackingArray(), jsonStringStartPos, this.currentObjectBytes.length() - jsonStringStartPos));
                }
                final int byteReadInt = byteRead & 0xFF;
                if ((byteRead & 0xFF) <= 31) {
                    throw new JSONException(JSONMessages.ERR_OBJECT_READER_UNESCAPED_CONTROL_CHAR.get(this.currentObjectBytes.length(), byteToCharString(byteRead)));
                }
                this.stringBuffer.append((char)byteReadInt);
            }
        }
        throw new JSONException(JSONMessages.ERR_OBJECT_READER_INVALID_ESCAPED_CHAR.get(this.currentObjectBytes.length(), byteToCharString(nextByte)));
    }
    
    private JSONBoolean readBoolean() throws IOException, JSONException {
        final byte firstByte = this.currentObjectBytes.getBackingArray()[this.currentObjectBytes.length() - 1];
        if (firstByte == 116) {
            if (this.readByte(false) == 114 && this.readByte(false) == 117 && this.readByte(false) == 101) {
                return JSONBoolean.TRUE;
            }
            throw new JSONException(JSONMessages.ERR_OBJECT_READER_INVALID_BOOLEAN_TRUE.get(this.currentObjectBytes.length()));
        }
        else {
            if (this.readByte(false) == 97 && this.readByte(false) == 108 && this.readByte(false) == 115 && this.readByte(false) == 101) {
                return JSONBoolean.FALSE;
            }
            throw new JSONException(JSONMessages.ERR_OBJECT_READER_INVALID_BOOLEAN_FALSE.get(this.currentObjectBytes.length()));
        }
    }
    
    private JSONNull readNull() throws IOException, JSONException {
        if (this.readByte(false) == 117 && this.readByte(false) == 108 && this.readByte(false) == 108) {
            return JSONNull.NULL;
        }
        throw new JSONException(JSONMessages.ERR_OBJECT_READER_INVALID_NULL.get(this.currentObjectBytes.length()));
    }
    
    private JSONNumber readNumber() throws IOException, JSONException {
        this.stringBuffer.clear();
        this.stringBuffer.append(this.currentObjectBytes.getBackingArray()[this.currentObjectBytes.length() - 1]);
    Label_0116:
        while (true) {
            this.inputStream.mark(1);
            final Byte b = this.readByte(false);
            switch (b) {
                case 9:
                case 10:
                case 13:
                case 32:
                case 44:
                case 93:
                case 125: {
                    break Label_0116;
                }
                default: {
                    this.stringBuffer.append(b);
                    continue;
                }
            }
        }
        this.inputStream.reset();
        this.currentObjectBytes.setLength(this.currentObjectBytes.length() - 1);
        return new JSONNumber(this.stringBuffer.toString());
    }
    
    private JSONArray readArray() throws IOException, JSONException {
        final ArrayList<JSONValue> values = new ArrayList<JSONValue>(10);
        boolean firstToken = true;
        while (true) {
            final Object token = this.readToken(false);
            if (token instanceof JSONValue) {
                values.add((JSONValue)token);
            }
            else if (token.equals('[')) {
                values.add(this.readArray());
            }
            else if (token.equals('{')) {
                final LinkedHashMap<String, JSONValue> fieldMap = new LinkedHashMap<String, JSONValue>(StaticUtils.computeMapCapacity(10));
                values.add(this.readObject(fieldMap));
            }
            else {
                if (token.equals(']') && firstToken) {
                    return JSONArray.EMPTY_ARRAY;
                }
                throw new JSONException(JSONMessages.ERR_OBJECT_READER_INVALID_TOKEN_IN_ARRAY.get(this.currentObjectBytes.length(), String.valueOf(token)));
            }
            firstToken = false;
            final Object nextToken = this.readToken(false);
            if (nextToken.equals(']')) {
                return new JSONArray(values);
            }
            if (!nextToken.equals(',')) {
                throw new JSONException(JSONMessages.ERR_OBJECT_READER_INVALID_TOKEN_AFTER_ARRAY_VALUE.get(this.currentObjectBytes.length(), String.valueOf(nextToken)));
            }
        }
    }
    
    private JSONObject readObject(final Map<String, JSONValue> fields) throws IOException, JSONException {
        boolean firstField = true;
        while (true) {
            final Object fieldNameToken = this.readToken(false);
            if (fieldNameToken instanceof JSONString) {
                final String fieldName = ((JSONString)fieldNameToken).stringValue();
                if (fields.containsKey(fieldName)) {
                    throw new JSONException(JSONMessages.ERR_OBJECT_READER_DUPLICATE_FIELD.get(this.currentObjectBytes.length(), fieldName));
                }
                firstField = false;
                final Object colonToken = this.readToken(false);
                if (!colonToken.equals(':')) {
                    throw new JSONException(JSONMessages.ERR_OBJECT_READER_TOKEN_NOT_COLON.get(this.currentObjectBytes.length(), String.valueOf(colonToken), String.valueOf(fieldNameToken)));
                }
                final Object valueToken = this.readToken(false);
                if (valueToken instanceof JSONValue) {
                    fields.put(fieldName, (JSONValue)valueToken);
                }
                else if (valueToken.equals('[')) {
                    final JSONArray a = this.readArray();
                    fields.put(fieldName, a);
                }
                else {
                    if (!valueToken.equals('{')) {
                        throw new JSONException(JSONMessages.ERR_OBJECT_READER_TOKEN_NOT_VALUE.get(this.currentObjectBytes.length(), String.valueOf(valueToken), String.valueOf(fieldNameToken)));
                    }
                    final LinkedHashMap<String, JSONValue> m = new LinkedHashMap<String, JSONValue>(StaticUtils.computeMapCapacity(10));
                    final JSONObject o = this.readObject(m);
                    fields.put(fieldName, o);
                }
                final Object separatorToken = this.readToken(false);
                if (separatorToken.equals('}')) {
                    return new JSONObject(fields);
                }
                if (!separatorToken.equals(',')) {
                    throw new JSONException(JSONMessages.ERR_OBJECT_READER_INVALID_TOKEN_AFTER_OBJECT_VALUE.get(this.currentObjectBytes.length(), String.valueOf(separatorToken), String.valueOf(fieldNameToken)));
                }
                continue;
            }
            else {
                if (firstField && fieldNameToken.equals('}')) {
                    return new JSONObject(fields);
                }
                throw new JSONException(JSONMessages.ERR_OBJECT_READER_INVALID_TOKEN_IN_OBJECT.get(this.currentObjectBytes.length(), String.valueOf(fieldNameToken)));
            }
        }
    }
    
    private static String byteToCharString(final byte b) {
        if (b >= 32 && b <= 126) {
            return String.valueOf((char)(b & 0xFF));
        }
        return "0x" + StaticUtils.toHex(b);
    }
}
