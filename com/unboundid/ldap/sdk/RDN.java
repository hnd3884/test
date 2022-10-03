package com.unboundid.ldap.sdk;

import com.unboundid.ldap.matchingrules.MatchingRule;
import com.unboundid.ldap.sdk.schema.AttributeTypeDefinition;
import java.util.Iterator;
import java.util.Collections;
import java.util.TreeSet;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import com.unboundid.util.Debug;
import com.unboundid.util.StaticUtils;
import com.unboundid.util.Validator;
import java.util.SortedSet;
import com.unboundid.ldap.sdk.schema.Schema;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import java.io.Serializable;
import java.util.Comparator;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class RDN implements Comparable<RDN>, Comparator<RDN>, Serializable
{
    private static final long serialVersionUID = 2923419812807188487L;
    private final ASN1OctetString[] attributeValues;
    private final Schema schema;
    private volatile SortedSet<RDNNameValuePair> nameValuePairs;
    private volatile String normalizedString;
    private volatile String rdnString;
    private final String[] attributeNames;
    
    public RDN(final String attributeName, final String attributeValue) {
        this(attributeName, attributeValue, null);
    }
    
    public RDN(final String attributeName, final String attributeValue, final Schema schema) {
        Validator.ensureNotNull(attributeName, attributeValue);
        this.schema = schema;
        this.attributeNames = new String[] { attributeName };
        this.attributeValues = new ASN1OctetString[] { new ASN1OctetString(attributeValue) };
        this.nameValuePairs = null;
        this.normalizedString = null;
        this.rdnString = null;
    }
    
    public RDN(final String attributeName, final byte[] attributeValue) {
        this(attributeName, attributeValue, null);
    }
    
    public RDN(final String attributeName, final byte[] attributeValue, final Schema schema) {
        Validator.ensureNotNull(attributeName, attributeValue);
        this.schema = schema;
        this.attributeNames = new String[] { attributeName };
        this.attributeValues = new ASN1OctetString[] { new ASN1OctetString(attributeValue) };
        this.nameValuePairs = null;
        this.normalizedString = null;
        this.rdnString = null;
    }
    
    public RDN(final String[] attributeNames, final String[] attributeValues) {
        this(attributeNames, attributeValues, null);
    }
    
    public RDN(final String[] attributeNames, final String[] attributeValues, final Schema schema) {
        Validator.ensureNotNull(attributeNames, attributeValues);
        Validator.ensureTrue(attributeNames.length == attributeValues.length, "RDN.attributeNames and attributeValues must be the same size.");
        Validator.ensureTrue(attributeNames.length > 0, "RDN.attributeNames must not be empty.");
        this.attributeNames = attributeNames;
        this.schema = schema;
        this.attributeValues = new ASN1OctetString[attributeValues.length];
        for (int i = 0; i < attributeValues.length; ++i) {
            this.attributeValues[i] = new ASN1OctetString(attributeValues[i]);
        }
        this.nameValuePairs = null;
        this.normalizedString = null;
        this.rdnString = null;
    }
    
    public RDN(final String[] attributeNames, final byte[][] attributeValues) {
        this(attributeNames, attributeValues, null);
    }
    
    public RDN(final String[] attributeNames, final byte[][] attributeValues, final Schema schema) {
        Validator.ensureNotNull(attributeNames, attributeValues);
        Validator.ensureTrue(attributeNames.length == attributeValues.length, "RDN.attributeNames and attributeValues must be the same size.");
        Validator.ensureTrue(attributeNames.length > 0, "RDN.attributeNames must not be empty.");
        this.attributeNames = attributeNames;
        this.schema = schema;
        this.attributeValues = new ASN1OctetString[attributeValues.length];
        for (int i = 0; i < attributeValues.length; ++i) {
            this.attributeValues[i] = new ASN1OctetString(attributeValues[i]);
        }
        this.nameValuePairs = null;
        this.normalizedString = null;
        this.rdnString = null;
    }
    
    RDN(final String attributeName, final ASN1OctetString attributeValue, final Schema schema, final String rdnString) {
        this.rdnString = rdnString;
        this.schema = schema;
        this.attributeNames = new String[] { attributeName };
        this.attributeValues = new ASN1OctetString[] { attributeValue };
        this.nameValuePairs = null;
        this.normalizedString = null;
    }
    
    RDN(final String[] attributeNames, final ASN1OctetString[] attributeValues, final Schema schema, final String rdnString) {
        this.rdnString = rdnString;
        this.schema = schema;
        this.attributeNames = attributeNames;
        this.attributeValues = attributeValues;
        this.nameValuePairs = null;
        this.normalizedString = null;
    }
    
    public RDN(final String rdnString) throws LDAPException {
        this(rdnString, null, false);
    }
    
    public RDN(final String rdnString, final Schema schema) throws LDAPException {
        this(rdnString, schema, false);
    }
    
    public RDN(final String rdnString, final Schema schema, final boolean strictNameChecking) throws LDAPException {
        Validator.ensureNotNull(rdnString);
        this.rdnString = rdnString;
        this.schema = schema;
        this.nameValuePairs = null;
        this.normalizedString = null;
        int pos;
        int length;
        for (pos = 0, length = rdnString.length(); pos < length && rdnString.charAt(pos) == ' '; ++pos) {}
        int attrStartPos = pos;
        while (pos < length) {
            final char c = rdnString.charAt(pos);
            if (c == ' ') {
                break;
            }
            if (c == '=') {
                break;
            }
            ++pos;
        }
        String attrName = rdnString.substring(attrStartPos, pos);
        if (attrName.isEmpty()) {
            throw new LDAPException(ResultCode.INVALID_DN_SYNTAX, LDAPMessages.ERR_RDN_NO_ATTR_NAME.get(rdnString));
        }
        if (strictNameChecking && !Attribute.nameIsValid(attrName) && !StaticUtils.isNumericOID(attrName)) {
            throw new LDAPException(ResultCode.INVALID_DN_SYNTAX, LDAPMessages.ERR_RDN_INVALID_ATTR_NAME.get(rdnString, attrName));
        }
        while (pos < length && rdnString.charAt(pos) == ' ') {
            ++pos;
        }
        if (pos >= length || rdnString.charAt(pos) != '=') {
            throw new LDAPException(ResultCode.INVALID_DN_SYNTAX, LDAPMessages.ERR_RDN_NO_EQUAL_SIGN.get(rdnString, attrName));
        }
        ++pos;
        while (pos < length && rdnString.charAt(pos) == ' ') {
            ++pos;
        }
        ASN1OctetString value;
        if (pos >= length) {
            value = new ASN1OctetString();
        }
        else if (rdnString.charAt(pos) == '#') {
            final byte[] valueArray = readHexString(rdnString, ++pos);
            try {
                value = ASN1OctetString.decodeAsOctetString(valueArray);
            }
            catch (final Exception e) {
                Debug.debugException(e);
                throw new LDAPException(ResultCode.INVALID_DN_SYNTAX, LDAPMessages.ERR_RDN_HEX_STRING_NOT_BER_ENCODED.get(rdnString, attrName), e);
            }
            pos += valueArray.length * 2;
        }
        else {
            final StringBuilder buffer = new StringBuilder();
            pos = readValueString(rdnString, pos, buffer);
            value = new ASN1OctetString(buffer.toString());
        }
        while (pos < length && rdnString.charAt(pos) == ' ') {
            ++pos;
        }
        if (pos >= length) {
            this.attributeNames = new String[] { attrName };
            this.attributeValues = new ASN1OctetString[] { value };
            return;
        }
        final ArrayList<String> nameList = new ArrayList<String>(5);
        final ArrayList<ASN1OctetString> valueList = new ArrayList<ASN1OctetString>(5);
        nameList.add(attrName);
        valueList.add(value);
        if (rdnString.charAt(pos) != '+') {
            throw new LDAPException(ResultCode.INVALID_DN_SYNTAX, LDAPMessages.ERR_RDN_VALUE_NOT_FOLLOWED_BY_PLUS.get(rdnString));
        }
        ++pos;
        if (pos >= length) {
            throw new LDAPException(ResultCode.INVALID_DN_SYNTAX, LDAPMessages.ERR_RDN_PLUS_NOT_FOLLOWED_BY_AVP.get(rdnString));
        }
        int numValues = 1;
        while (pos < length) {
            while (pos < length && rdnString.charAt(pos) == ' ') {
                ++pos;
            }
            attrStartPos = pos;
            while (pos < length) {
                final char c2 = rdnString.charAt(pos);
                if (c2 == ' ') {
                    break;
                }
                if (c2 == '=') {
                    break;
                }
                ++pos;
            }
            attrName = rdnString.substring(attrStartPos, pos);
            if (attrName.isEmpty()) {
                throw new LDAPException(ResultCode.INVALID_DN_SYNTAX, LDAPMessages.ERR_RDN_NO_ATTR_NAME.get(rdnString));
            }
            if (strictNameChecking && !Attribute.nameIsValid(attrName) && !StaticUtils.isNumericOID(attrName)) {
                throw new LDAPException(ResultCode.INVALID_DN_SYNTAX, LDAPMessages.ERR_RDN_INVALID_ATTR_NAME.get(rdnString, attrName));
            }
            while (pos < length && rdnString.charAt(pos) == ' ') {
                ++pos;
            }
            if (pos >= length || rdnString.charAt(pos) != '=') {
                throw new LDAPException(ResultCode.INVALID_DN_SYNTAX, LDAPMessages.ERR_RDN_NO_EQUAL_SIGN.get(rdnString, attrName));
            }
            ++pos;
            while (pos < length && rdnString.charAt(pos) == ' ') {
                ++pos;
            }
            if (pos >= length) {
                value = new ASN1OctetString();
            }
            else if (rdnString.charAt(pos) == '#') {
                final byte[] valueArray2 = readHexString(rdnString, ++pos);
                try {
                    value = ASN1OctetString.decodeAsOctetString(valueArray2);
                }
                catch (final Exception e2) {
                    Debug.debugException(e2);
                    throw new LDAPException(ResultCode.INVALID_DN_SYNTAX, LDAPMessages.ERR_RDN_HEX_STRING_NOT_BER_ENCODED.get(rdnString, attrName), e2);
                }
                pos += valueArray2.length * 2;
            }
            else {
                final StringBuilder buffer2 = new StringBuilder();
                pos = readValueString(rdnString, pos, buffer2);
                value = new ASN1OctetString(buffer2.toString());
            }
            while (pos < length && rdnString.charAt(pos) == ' ') {
                ++pos;
            }
            nameList.add(attrName);
            valueList.add(value);
            ++numValues;
            if (pos >= length) {
                break;
            }
            if (rdnString.charAt(pos) != '+') {
                throw new LDAPException(ResultCode.INVALID_DN_SYNTAX, LDAPMessages.ERR_RDN_VALUE_NOT_FOLLOWED_BY_PLUS.get(rdnString));
            }
            ++pos;
            if (pos >= length) {
                throw new LDAPException(ResultCode.INVALID_DN_SYNTAX, LDAPMessages.ERR_RDN_PLUS_NOT_FOLLOWED_BY_AVP.get(rdnString));
            }
        }
        this.attributeNames = new String[numValues];
        this.attributeValues = new ASN1OctetString[numValues];
        for (int i = 0; i < numValues; ++i) {
            this.attributeNames[i] = nameList.get(i);
            this.attributeValues[i] = valueList.get(i);
        }
    }
    
    static byte[] readHexString(final String rdnString, final int startPos) throws LDAPException {
        final int length = rdnString.length();
        int pos = startPos;
        final ByteBuffer buffer = ByteBuffer.allocate(length - pos);
    Label_1041:
        while (pos < length) {
            byte hexByte = 0;
            switch (rdnString.charAt(pos++)) {
                case '0': {
                    hexByte = 0;
                    break;
                }
                case '1': {
                    hexByte = 16;
                    break;
                }
                case '2': {
                    hexByte = 32;
                    break;
                }
                case '3': {
                    hexByte = 48;
                    break;
                }
                case '4': {
                    hexByte = 64;
                    break;
                }
                case '5': {
                    hexByte = 80;
                    break;
                }
                case '6': {
                    hexByte = 96;
                    break;
                }
                case '7': {
                    hexByte = 112;
                    break;
                }
                case '8': {
                    hexByte = -128;
                    break;
                }
                case '9': {
                    hexByte = -112;
                    break;
                }
                case 'A':
                case 'a': {
                    hexByte = -96;
                    break;
                }
                case 'B':
                case 'b': {
                    hexByte = -80;
                    break;
                }
                case 'C':
                case 'c': {
                    hexByte = -64;
                    break;
                }
                case 'D':
                case 'd': {
                    hexByte = -48;
                    break;
                }
                case 'E':
                case 'e': {
                    hexByte = -32;
                    break;
                }
                case 'F':
                case 'f': {
                    hexByte = -16;
                    break;
                }
                case ' ':
                case '+':
                case ',':
                case ';': {
                    break Label_1041;
                }
                default: {
                    throw new LDAPException(ResultCode.INVALID_DN_SYNTAX, LDAPMessages.ERR_RDN_INVALID_HEX_CHAR.get(rdnString, rdnString.charAt(pos - 1), pos - 1));
                }
            }
            if (pos >= length) {
                throw new LDAPException(ResultCode.INVALID_DN_SYNTAX, LDAPMessages.ERR_RDN_MISSING_HEX_CHAR.get(rdnString));
            }
            switch (rdnString.charAt(pos++)) {
                case '0': {
                    buffer.put(hexByte);
                    continue;
                }
                case '1': {
                    buffer.put((byte)(hexByte | 0x1));
                    continue;
                }
                case '2': {
                    buffer.put((byte)(hexByte | 0x2));
                    continue;
                }
                case '3': {
                    buffer.put((byte)(hexByte | 0x3));
                    continue;
                }
                case '4': {
                    buffer.put((byte)(hexByte | 0x4));
                    continue;
                }
                case '5': {
                    buffer.put((byte)(hexByte | 0x5));
                    continue;
                }
                case '6': {
                    buffer.put((byte)(hexByte | 0x6));
                    continue;
                }
                case '7': {
                    buffer.put((byte)(hexByte | 0x7));
                    continue;
                }
                case '8': {
                    buffer.put((byte)(hexByte | 0x8));
                    continue;
                }
                case '9': {
                    buffer.put((byte)(hexByte | 0x9));
                    continue;
                }
                case 'A':
                case 'a': {
                    buffer.put((byte)(hexByte | 0xA));
                    continue;
                }
                case 'B':
                case 'b': {
                    buffer.put((byte)(hexByte | 0xB));
                    continue;
                }
                case 'C':
                case 'c': {
                    buffer.put((byte)(hexByte | 0xC));
                    continue;
                }
                case 'D':
                case 'd': {
                    buffer.put((byte)(hexByte | 0xD));
                    continue;
                }
                case 'E':
                case 'e': {
                    buffer.put((byte)(hexByte | 0xE));
                    continue;
                }
                case 'F':
                case 'f': {
                    buffer.put((byte)(hexByte | 0xF));
                    continue;
                }
                default: {
                    throw new LDAPException(ResultCode.INVALID_DN_SYNTAX, LDAPMessages.ERR_RDN_INVALID_HEX_CHAR.get(rdnString, rdnString.charAt(pos - 1), pos - 1));
                }
            }
        }
        buffer.flip();
        final byte[] valueArray = new byte[buffer.limit()];
        buffer.get(valueArray);
        return valueArray;
    }
    
    static int readValueString(final String rdnString, final int startPos, final StringBuilder buffer) throws LDAPException {
        final int length = rdnString.length();
        int pos = startPos;
        boolean inQuotes = false;
    Label_0333:
        while (pos < length) {
            char c = rdnString.charAt(pos);
            switch (c) {
                case '\\': {
                    if (pos + 1 >= length) {
                        throw new LDAPException(ResultCode.INVALID_DN_SYNTAX, LDAPMessages.ERR_RDN_ENDS_WITH_BACKSLASH.get(rdnString));
                    }
                    ++pos;
                    c = rdnString.charAt(pos);
                    if (StaticUtils.isHex(c)) {
                        pos = readEscapedHexString(rdnString, pos, buffer) - 1;
                        break;
                    }
                    buffer.append(c);
                    break;
                }
                case '\"': {
                    if (inQuotes) {
                        ++pos;
                        while (pos < length) {
                            c = rdnString.charAt(pos);
                            if (c == '+' || c == ',') {
                                break;
                            }
                            if (c == ';') {
                                break;
                            }
                            if (c != ' ') {
                                throw new LDAPException(ResultCode.INVALID_DN_SYNTAX, LDAPMessages.ERR_RDN_CHAR_OUTSIDE_QUOTES.get(rdnString, c, pos - 1));
                            }
                            ++pos;
                        }
                        inQuotes = false;
                        break Label_0333;
                    }
                    if (pos == startPos) {
                        inQuotes = true;
                        break;
                    }
                    throw new LDAPException(ResultCode.INVALID_DN_SYNTAX, LDAPMessages.ERR_RDN_UNEXPECTED_DOUBLE_QUOTE.get(rdnString, pos));
                }
                case '+':
                case ',':
                case ';': {
                    if (inQuotes) {
                        buffer.append(c);
                        break;
                    }
                    break Label_0333;
                }
                default: {
                    buffer.append(c);
                    break;
                }
            }
            ++pos;
        }
        if (inQuotes) {
            throw new LDAPException(ResultCode.INVALID_DN_SYNTAX, LDAPMessages.ERR_RDN_UNCLOSED_DOUBLE_QUOTE.get(rdnString));
        }
        for (int bufferPos = buffer.length() - 1, rdnStrPos = pos - 2; bufferPos > 0 && buffer.charAt(bufferPos) == ' ' && rdnString.charAt(rdnStrPos) != '\\'; --rdnStrPos) {
            buffer.deleteCharAt(bufferPos--);
        }
        return pos;
    }
    
    private static int readEscapedHexString(final String rdnString, final int startPos, final StringBuilder buffer) throws LDAPException {
        final int length = rdnString.length();
        int pos = startPos;
        final ByteBuffer byteBuffer = ByteBuffer.allocate(length - pos);
        while (pos < length) {
            byte b = 0;
            switch (rdnString.charAt(pos++)) {
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
                    throw new LDAPException(ResultCode.INVALID_DN_SYNTAX, LDAPMessages.ERR_RDN_INVALID_HEX_CHAR.get(rdnString, rdnString.charAt(pos - 1), pos - 1));
                }
            }
            if (pos >= length) {
                throw new LDAPException(ResultCode.INVALID_DN_SYNTAX, LDAPMessages.ERR_RDN_MISSING_HEX_CHAR.get(rdnString));
            }
            switch (rdnString.charAt(pos++)) {
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
                    throw new LDAPException(ResultCode.INVALID_DN_SYNTAX, LDAPMessages.ERR_RDN_INVALID_HEX_CHAR.get(rdnString, rdnString.charAt(pos - 1), pos - 1));
                }
            }
            if (pos + 1 >= length || rdnString.charAt(pos) != '\\' || !StaticUtils.isHex(rdnString.charAt(pos + 1))) {
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
    
    public static boolean isValidRDN(final String s) {
        return isValidRDN(s, false);
    }
    
    public static boolean isValidRDN(final String s, final boolean strictNameChecking) {
        try {
            new RDN(s, null, strictNameChecking);
            return true;
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            return false;
        }
    }
    
    public boolean isMultiValued() {
        return this.attributeNames.length != 1;
    }
    
    public int getValueCount() {
        return this.attributeNames.length;
    }
    
    public Attribute[] getAttributes() {
        final Attribute[] attrs = new Attribute[this.attributeNames.length];
        for (int i = 0; i < attrs.length; ++i) {
            attrs[i] = new Attribute(this.attributeNames[i], this.schema, new ASN1OctetString[] { this.attributeValues[i] });
        }
        return attrs;
    }
    
    public String[] getAttributeNames() {
        return this.attributeNames;
    }
    
    public String[] getAttributeValues() {
        final String[] stringValues = new String[this.attributeValues.length];
        for (int i = 0; i < stringValues.length; ++i) {
            stringValues[i] = this.attributeValues[i].stringValue();
        }
        return stringValues;
    }
    
    public byte[][] getByteArrayAttributeValues() {
        final byte[][] byteValues = new byte[this.attributeValues.length][];
        for (int i = 0; i < byteValues.length; ++i) {
            byteValues[i] = this.attributeValues[i].getValue();
        }
        return byteValues;
    }
    
    public SortedSet<RDNNameValuePair> getNameValuePairs() {
        if (this.nameValuePairs == null) {
            final SortedSet<RDNNameValuePair> s = new TreeSet<RDNNameValuePair>();
            for (int i = 0; i < this.attributeNames.length; ++i) {
                s.add(new RDNNameValuePair(this.attributeNames[i], this.attributeValues[i], this.schema));
            }
            this.nameValuePairs = Collections.unmodifiableSortedSet(s);
        }
        return this.nameValuePairs;
    }
    
    Schema getSchema() {
        return this.schema;
    }
    
    public boolean hasAttribute(final String attributeName) {
        for (final RDNNameValuePair nameValuePair : this.getNameValuePairs()) {
            if (nameValuePair.hasAttributeName(attributeName)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean hasAttributeValue(final String attributeName, final String attributeValue) {
        for (final RDNNameValuePair nameValuePair : this.getNameValuePairs()) {
            if (nameValuePair.hasAttributeName(attributeName) && nameValuePair.hasAttributeValue(attributeValue)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean hasAttributeValue(final String attributeName, final byte[] attributeValue) {
        for (final RDNNameValuePair nameValuePair : this.getNameValuePairs()) {
            if (nameValuePair.hasAttributeName(attributeName) && nameValuePair.hasAttributeValue(attributeValue)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public String toString() {
        if (this.rdnString == null) {
            final StringBuilder buffer = new StringBuilder();
            this.toString(buffer, false);
            this.rdnString = buffer.toString();
        }
        return this.rdnString;
    }
    
    public String toMinimallyEncodedString() {
        final StringBuilder buffer = new StringBuilder();
        this.toString(buffer, true);
        return buffer.toString();
    }
    
    public void toString(final StringBuilder buffer) {
        this.toString(buffer, false);
    }
    
    public void toString(final StringBuilder buffer, final boolean minimizeEncoding) {
        if (this.rdnString != null && !minimizeEncoding) {
            buffer.append(this.rdnString);
            return;
        }
        for (int i = 0; i < this.attributeNames.length; ++i) {
            if (i > 0) {
                buffer.append('+');
            }
            buffer.append(this.attributeNames[i]);
            buffer.append('=');
            appendValue(buffer, this.attributeValues[i], minimizeEncoding);
        }
    }
    
    static void appendValue(final StringBuilder buffer, final ASN1OctetString value, final boolean minimizeEncoding) {
        final String valueString = value.stringValue();
        for (int length = valueString.length(), j = 0; j < length; ++j) {
            final char c = valueString.charAt(j);
            switch (c) {
                case '\"':
                case '+':
                case ',':
                case ';':
                case '<':
                case '=':
                case '>':
                case '\\': {
                    buffer.append('\\');
                    buffer.append(c);
                    break;
                }
                case '#': {
                    if (j == 0) {
                        buffer.append("\\#");
                        break;
                    }
                    buffer.append('#');
                    break;
                }
                case ' ': {
                    if (j == 0 || j + 1 == length) {
                        buffer.append("\\ ");
                        break;
                    }
                    buffer.append(' ');
                    break;
                }
                case '\0': {
                    buffer.append("\\00");
                    break;
                }
                default: {
                    if (!minimizeEncoding && (c < ' ' || c > '~')) {
                        StaticUtils.hexEncode(c, buffer);
                        break;
                    }
                    buffer.append(c);
                    break;
                }
            }
        }
    }
    
    public String toNormalizedString() {
        if (this.normalizedString == null) {
            final StringBuilder buffer = new StringBuilder();
            this.toNormalizedString(buffer);
            this.normalizedString = buffer.toString();
        }
        return this.normalizedString;
    }
    
    public void toNormalizedString(final StringBuilder buffer) {
        if (this.attributeNames.length == 1) {
            final String name = this.normalizeAttrName(this.attributeNames[0]);
            buffer.append(name);
            buffer.append('=');
            appendNormalizedValue(buffer, name, this.attributeValues[0], this.schema);
        }
        else {
            final Iterator<RDNNameValuePair> iterator = this.getNameValuePairs().iterator();
            while (iterator.hasNext()) {
                buffer.append(iterator.next().toNormalizedString());
                if (iterator.hasNext()) {
                    buffer.append('+');
                }
            }
        }
    }
    
    private String normalizeAttrName(final String name) {
        String n = name;
        if (this.schema != null) {
            final AttributeTypeDefinition at = this.schema.getAttributeType(name);
            if (at != null) {
                n = at.getNameOrOID();
            }
        }
        return StaticUtils.toLowerCase(n);
    }
    
    public static String normalize(final String s) throws LDAPException {
        return normalize(s, null);
    }
    
    public static String normalize(final String s, final Schema schema) throws LDAPException {
        return new RDN(s, schema).toNormalizedString();
    }
    
    static void appendNormalizedValue(final StringBuilder buffer, final String attributeName, final ASN1OctetString value, final Schema schema) {
        final MatchingRule matchingRule = MatchingRule.selectEqualityMatchingRule(attributeName, schema);
        ASN1OctetString rawNormValue;
        try {
            rawNormValue = matchingRule.normalize(value);
        }
        catch (final Exception e) {
            Debug.debugException(e);
            rawNormValue = new ASN1OctetString(StaticUtils.toLowerCase(value.stringValue()));
        }
        final String valueString = rawNormValue.stringValue();
        for (int length = valueString.length(), i = 0; i < length; ++i) {
            final char c = valueString.charAt(i);
            switch (c) {
                case '\"':
                case '+':
                case ',':
                case ';':
                case '<':
                case '=':
                case '>':
                case '\\': {
                    buffer.append('\\');
                    buffer.append(c);
                    break;
                }
                case '#': {
                    if (i == 0) {
                        buffer.append("\\#");
                        break;
                    }
                    buffer.append('#');
                    break;
                }
                case ' ': {
                    if (i == 0 || i + 1 == length) {
                        buffer.append("\\ ");
                        break;
                    }
                    buffer.append(' ');
                    break;
                }
                default: {
                    if (c >= ' ' && c <= '~') {
                        buffer.append(c);
                        break;
                    }
                    if (!Character.isHighSurrogate(c)) {
                        StaticUtils.hexEncode(c, buffer);
                        break;
                    }
                    if (i + 1 < length && Character.isLowSurrogate(valueString.charAt(i + 1))) {
                        final char c2 = valueString.charAt(++i);
                        final int codePoint = Character.toCodePoint(c, c2);
                        StaticUtils.hexEncode(codePoint, buffer);
                        break;
                    }
                    StaticUtils.hexEncode(c, buffer);
                    break;
                }
            }
        }
    }
    
    @Override
    public int hashCode() {
        return this.toNormalizedString().hashCode();
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == null) {
            return false;
        }
        if (o == this) {
            return true;
        }
        if (!(o instanceof RDN)) {
            return false;
        }
        final RDN rdn = (RDN)o;
        return this.toNormalizedString().equals(rdn.toNormalizedString());
    }
    
    public boolean equals(final String s) throws LDAPException {
        return s != null && this.equals(new RDN(s, this.schema));
    }
    
    public static boolean equals(final String s1, final String s2) throws LDAPException {
        return new RDN(s1).equals(new RDN(s2));
    }
    
    @Override
    public int compareTo(final RDN rdn) {
        return this.compare(this, rdn);
    }
    
    @Override
    public int compare(final RDN rdn1, final RDN rdn2) {
        Validator.ensureNotNull(rdn1, rdn2);
        final Iterator<RDNNameValuePair> iterator1 = rdn1.getNameValuePairs().iterator();
        final Iterator<RDNNameValuePair> iterator2 = rdn2.getNameValuePairs().iterator();
        while (iterator1.hasNext()) {
            if (!iterator2.hasNext()) {
                return 1;
            }
            final RDNNameValuePair p1 = iterator1.next();
            final RDNNameValuePair p2 = iterator2.next();
            final int compareValue = p1.compareTo(p2);
            if (compareValue != 0) {
                return compareValue;
            }
        }
        if (iterator2.hasNext()) {
            return -1;
        }
        return 0;
    }
    
    public static int compare(final String s1, final String s2) throws LDAPException {
        return compare(s1, s2, null);
    }
    
    public static int compare(final String s1, final String s2, final Schema schema) throws LDAPException {
        return new RDN(s1, schema).compareTo(new RDN(s2, schema));
    }
}
