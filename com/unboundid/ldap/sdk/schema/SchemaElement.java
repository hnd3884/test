package com.unboundid.ldap.sdk.schema;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.ArrayList;
import com.unboundid.util.StaticUtils;
import java.nio.ByteBuffer;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotExtensible;
import java.io.Serializable;

@NotExtensible
@ThreadSafety(level = ThreadSafetyLevel.INTERFACE_THREADSAFE)
public abstract class SchemaElement implements Serializable
{
    private static final long serialVersionUID = -8249972237068748580L;
    
    static int skipSpaces(final String s, final int startPos, final int length) throws LDAPException {
        int pos;
        for (pos = startPos; pos < length && s.charAt(pos) == ' '; ++pos) {}
        if (pos >= length) {
            throw new LDAPException(ResultCode.DECODING_ERROR, SchemaMessages.ERR_SCHEMA_ELEM_SKIP_SPACES_NO_CLOSE_PAREN.get(s));
        }
        return pos;
    }
    
    private static int readEscapedHexString(final String s, final int startPos, final int length, final StringBuilder buffer) throws LDAPException {
        int pos = startPos;
        final ByteBuffer byteBuffer = ByteBuffer.allocate(length - pos);
        while (pos < length) {
            byte b = 0;
            switch (s.charAt(pos++)) {
                case '0': {
                    b = 0;
                    break;
                }
                case '1': {
                    b = 16;
                    break;
                }
                case '2': {
                    b = 32;
                    break;
                }
                case '3': {
                    b = 48;
                    break;
                }
                case '4': {
                    b = 64;
                    break;
                }
                case '5': {
                    b = 80;
                    break;
                }
                case '6': {
                    b = 96;
                    break;
                }
                case '7': {
                    b = 112;
                    break;
                }
                case '8': {
                    b = -128;
                    break;
                }
                case '9': {
                    b = -112;
                    break;
                }
                case 'A':
                case 'a': {
                    b = -96;
                    break;
                }
                case 'B':
                case 'b': {
                    b = -80;
                    break;
                }
                case 'C':
                case 'c': {
                    b = -64;
                    break;
                }
                case 'D':
                case 'd': {
                    b = -48;
                    break;
                }
                case 'E':
                case 'e': {
                    b = -32;
                    break;
                }
                case 'F':
                case 'f': {
                    b = -16;
                    break;
                }
                default: {
                    throw new LDAPException(ResultCode.INVALID_ATTRIBUTE_SYNTAX, SchemaMessages.ERR_SCHEMA_ELEM_INVALID_HEX_CHAR.get(s, s.charAt(pos - 1), pos - 1));
                }
            }
            if (pos >= length) {
                throw new LDAPException(ResultCode.INVALID_ATTRIBUTE_SYNTAX, SchemaMessages.ERR_SCHEMA_ELEM_MISSING_HEX_CHAR.get(s));
            }
            switch (s.charAt(pos++)) {
                case '0': {
                    byteBuffer.put(b);
                    break;
                }
                case '1': {
                    byteBuffer.put((byte)(b | 0x1));
                    break;
                }
                case '2': {
                    byteBuffer.put((byte)(b | 0x2));
                    break;
                }
                case '3': {
                    byteBuffer.put((byte)(b | 0x3));
                    break;
                }
                case '4': {
                    byteBuffer.put((byte)(b | 0x4));
                    break;
                }
                case '5': {
                    byteBuffer.put((byte)(b | 0x5));
                    break;
                }
                case '6': {
                    byteBuffer.put((byte)(b | 0x6));
                    break;
                }
                case '7': {
                    byteBuffer.put((byte)(b | 0x7));
                    break;
                }
                case '8': {
                    byteBuffer.put((byte)(b | 0x8));
                    break;
                }
                case '9': {
                    byteBuffer.put((byte)(b | 0x9));
                    break;
                }
                case 'A':
                case 'a': {
                    byteBuffer.put((byte)(b | 0xA));
                    break;
                }
                case 'B':
                case 'b': {
                    byteBuffer.put((byte)(b | 0xB));
                    break;
                }
                case 'C':
                case 'c': {
                    byteBuffer.put((byte)(b | 0xC));
                    break;
                }
                case 'D':
                case 'd': {
                    byteBuffer.put((byte)(b | 0xD));
                    break;
                }
                case 'E':
                case 'e': {
                    byteBuffer.put((byte)(b | 0xE));
                    break;
                }
                case 'F':
                case 'f': {
                    byteBuffer.put((byte)(b | 0xF));
                    break;
                }
                default: {
                    throw new LDAPException(ResultCode.INVALID_ATTRIBUTE_SYNTAX, SchemaMessages.ERR_SCHEMA_ELEM_INVALID_HEX_CHAR.get(s, s.charAt(pos - 1), pos - 1));
                }
            }
            if (pos + 1 >= length || s.charAt(pos) != '\\' || !StaticUtils.isHex(s.charAt(pos + 1))) {
                break;
            }
            ++pos;
        }
        byteBuffer.flip();
        final byte[] byteArray = new byte[byteBuffer.limit()];
        byteBuffer.get(byteArray);
        buffer.append(StaticUtils.toUTF8String(byteArray));
        return pos;
    }
    
    static int readQDString(final String s, final int startPos, final int length, final StringBuilder buffer) throws LDAPException {
        if (s.charAt(startPos) != '\'') {
            throw new LDAPException(ResultCode.DECODING_ERROR, SchemaMessages.ERR_SCHEMA_ELEM_EXPECTED_SINGLE_QUOTE.get(s, startPos));
        }
        int pos = startPos + 1;
        while (pos < length) {
            final char c = s.charAt(pos++);
            if (c == '\'') {
                break;
            }
            if (c == '\\') {
                if (pos >= length) {
                    throw new LDAPException(ResultCode.DECODING_ERROR, SchemaMessages.ERR_SCHEMA_ELEM_ENDS_WITH_BACKSLASH.get(s));
                }
                pos = readEscapedHexString(s, pos, length, buffer);
            }
            else {
                buffer.append(c);
            }
        }
        if (pos >= length || (s.charAt(pos) != ' ' && s.charAt(pos) != ')')) {
            throw new LDAPException(ResultCode.DECODING_ERROR, SchemaMessages.ERR_SCHEMA_ELEM_NO_CLOSING_PAREN.get(s));
        }
        if (buffer.length() == 0) {
            throw new LDAPException(ResultCode.DECODING_ERROR, SchemaMessages.ERR_SCHEMA_ELEM_EMPTY_QUOTES.get(s));
        }
        return pos;
    }
    
    static int readQDStrings(final String s, final int startPos, final int length, final ArrayList<String> valueList) throws LDAPException {
        char c = s.charAt(startPos);
        if (c == '\'') {
            final StringBuilder buffer = new StringBuilder();
            final int returnPos = readQDString(s, startPos, length, buffer);
            valueList.add(buffer.toString());
            return returnPos;
        }
        if (c != '(') {
            throw new LDAPException(ResultCode.DECODING_ERROR, SchemaMessages.ERR_SCHEMA_ELEM_EXPECTED_QUOTE_OR_PAREN.get(s, startPos));
        }
        int pos = startPos + 1;
        while (true) {
            pos = skipSpaces(s, pos, length);
            c = s.charAt(pos);
            if (c == ')') {
                ++pos;
                if (valueList.isEmpty()) {
                    throw new LDAPException(ResultCode.DECODING_ERROR, SchemaMessages.ERR_SCHEMA_ELEM_EMPTY_STRING_LIST.get(s));
                }
                if (pos >= length || (s.charAt(pos) != ' ' && s.charAt(pos) != ')')) {
                    throw new LDAPException(ResultCode.DECODING_ERROR, SchemaMessages.ERR_SCHEMA_ELEM_NO_SPACE_AFTER_QUOTE.get(s));
                }
                return pos;
            }
            else {
                if (c != '\'') {
                    throw new LDAPException(ResultCode.DECODING_ERROR, SchemaMessages.ERR_SCHEMA_ELEM_EXPECTED_QUOTE_OR_PAREN.get(s, startPos));
                }
                final StringBuilder buffer2 = new StringBuilder();
                pos = readQDString(s, pos, length, buffer2);
                valueList.add(buffer2.toString());
            }
        }
    }
    
    static int readOID(final String s, final int startPos, final int length, final StringBuilder buffer) throws LDAPException {
        int pos = startPos;
        boolean lastWasQuote = false;
        while (pos < length) {
            final char c = s.charAt(pos);
            if (c == ' ' || c == '$' || c == ')') {
                if (buffer.length() == 0) {
                    throw new LDAPException(ResultCode.DECODING_ERROR, SchemaMessages.ERR_SCHEMA_ELEM_EMPTY_OID.get(s));
                }
                return pos;
            }
            else {
                if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9') || c == '-' || c == '.' || c == '_' || c == '{' || c == '}') {
                    if (lastWasQuote) {
                        throw new LDAPException(ResultCode.DECODING_ERROR, SchemaMessages.ERR_SCHEMA_ELEM_UNEXPECTED_CHAR_IN_OID.get(s, pos - 1));
                    }
                    buffer.append(c);
                }
                else {
                    if (c != '\'') {
                        throw new LDAPException(ResultCode.DECODING_ERROR, SchemaMessages.ERR_SCHEMA_ELEM_UNEXPECTED_CHAR_IN_OID.get(s, pos));
                    }
                    if (buffer.length() != 0) {
                        lastWasQuote = true;
                    }
                }
                ++pos;
            }
        }
        throw new LDAPException(ResultCode.DECODING_ERROR, SchemaMessages.ERR_SCHEMA_ELEM_NO_SPACE_AFTER_OID.get(s));
    }
    
    static int readOIDs(final String s, final int startPos, final int length, final ArrayList<String> valueList) throws LDAPException {
        char c = s.charAt(startPos);
        if (c != '(') {
            final StringBuilder buffer = new StringBuilder();
            final int returnPos = readOID(s, startPos, length, buffer);
            valueList.add(buffer.toString());
            return returnPos;
        }
        int pos = startPos + 1;
        while (true) {
            pos = skipSpaces(s, pos, length);
            c = s.charAt(pos);
            if (c == ')') {
                ++pos;
                if (valueList.isEmpty()) {
                    throw new LDAPException(ResultCode.DECODING_ERROR, SchemaMessages.ERR_SCHEMA_ELEM_EMPTY_OID_LIST.get(s));
                }
                if (pos >= length) {
                    throw new LDAPException(ResultCode.DECODING_ERROR, SchemaMessages.ERR_SCHEMA_ELEM_NO_SPACE_AFTER_OID_LIST.get(s));
                }
                return pos;
            }
            else if (c == '$') {
                ++pos;
                pos = skipSpaces(s, pos, length);
                final StringBuilder buffer2 = new StringBuilder();
                pos = readOID(s, pos, length, buffer2);
                valueList.add(buffer2.toString());
            }
            else {
                if (!valueList.isEmpty()) {
                    throw new LDAPException(ResultCode.DECODING_ERROR, SchemaMessages.ERR_SCHEMA_ELEM_UNEXPECTED_CHAR_IN_OID_LIST.get(s, pos));
                }
                final StringBuilder buffer2 = new StringBuilder();
                pos = readOID(s, pos, length, buffer2);
                valueList.add(buffer2.toString());
            }
        }
    }
    
    static void encodeValue(final String value, final StringBuilder buffer) {
        for (int length = value.length(), i = 0; i < length; ++i) {
            final char c = value.charAt(i);
            if (c < ' ' || c > '~' || c == '\\' || c == '\'') {
                StaticUtils.hexEncode(c, buffer);
            }
            else {
                buffer.append(c);
            }
        }
    }
    
    @Override
    public abstract int hashCode();
    
    @Override
    public abstract boolean equals(final Object p0);
    
    protected static boolean extensionsEqual(final Map<String, String[]> m1, final Map<String, String[]> m2) {
        if (m1.isEmpty()) {
            return m2.isEmpty();
        }
        if (m1.size() != m2.size()) {
            return false;
        }
        for (final Map.Entry<String, String[]> e : m1.entrySet()) {
            final String[] v1 = e.getValue();
            final String[] v2 = m2.get(e.getKey());
            if (!StaticUtils.arraysEqualOrderIndependent(v1, v2)) {
                return false;
            }
        }
        return true;
    }
    
    static String[] toArray(final Collection<String> c) {
        if (c == null) {
            return null;
        }
        return c.toArray(StaticUtils.NO_STRINGS);
    }
    
    @Override
    public abstract String toString();
}
