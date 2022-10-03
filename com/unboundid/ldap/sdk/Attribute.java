package com.unboundid.ldap.sdk;

import com.unboundid.util.Base64;
import java.util.HashSet;
import com.unboundid.asn1.ASN1Exception;
import com.unboundid.asn1.ASN1StreamReaderSet;
import com.unboundid.asn1.ASN1StreamReader;
import com.unboundid.asn1.ASN1Set;
import com.unboundid.asn1.ASN1Sequence;
import com.unboundid.asn1.ASN1BufferSet;
import com.unboundid.asn1.ASN1BufferSequence;
import com.unboundid.asn1.ASN1Buffer;
import com.unboundid.asn1.ASN1Element;
import com.unboundid.util.Debug;
import java.util.Date;
import java.util.Collections;
import java.util.LinkedHashSet;
import com.unboundid.util.StaticUtils;
import java.util.Set;
import java.util.ArrayList;
import java.util.Arrays;
import com.unboundid.ldap.sdk.schema.Schema;
import java.util.Iterator;
import java.util.Collection;
import com.unboundid.ldap.matchingrules.CaseIgnoreStringMatchingRule;
import com.unboundid.util.Validator;
import com.unboundid.ldap.matchingrules.MatchingRule;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import java.io.Serializable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class Attribute implements Serializable
{
    private static final ASN1OctetString[] NO_VALUES;
    private static final byte[][] NO_BYTE_VALUES;
    private static final long serialVersionUID = 5867076498293567612L;
    private final ASN1OctetString[] values;
    private int hashCode;
    private final MatchingRule matchingRule;
    private final String name;
    
    public Attribute(final String name) {
        this.hashCode = -1;
        Validator.ensureNotNull(name);
        this.name = name;
        this.values = Attribute.NO_VALUES;
        this.matchingRule = CaseIgnoreStringMatchingRule.getInstance();
    }
    
    public Attribute(final String name, final String value) {
        this.hashCode = -1;
        Validator.ensureNotNull(name, value);
        this.name = name;
        this.values = new ASN1OctetString[] { new ASN1OctetString(value) };
        this.matchingRule = CaseIgnoreStringMatchingRule.getInstance();
    }
    
    public Attribute(final String name, final byte[] value) {
        this.hashCode = -1;
        Validator.ensureNotNull(name, value);
        this.name = name;
        this.values = new ASN1OctetString[] { new ASN1OctetString(value) };
        this.matchingRule = CaseIgnoreStringMatchingRule.getInstance();
    }
    
    public Attribute(final String name, final String... values) {
        this.hashCode = -1;
        Validator.ensureNotNull(name, values);
        this.name = name;
        this.values = new ASN1OctetString[values.length];
        for (int i = 0; i < values.length; ++i) {
            this.values[i] = new ASN1OctetString(values[i]);
        }
        this.matchingRule = CaseIgnoreStringMatchingRule.getInstance();
    }
    
    public Attribute(final String name, final byte[]... values) {
        this.hashCode = -1;
        Validator.ensureNotNull(name, values);
        this.name = name;
        this.values = new ASN1OctetString[values.length];
        for (int i = 0; i < values.length; ++i) {
            this.values[i] = new ASN1OctetString(values[i]);
        }
        this.matchingRule = CaseIgnoreStringMatchingRule.getInstance();
    }
    
    public Attribute(final String name, final ASN1OctetString... values) {
        this.hashCode = -1;
        Validator.ensureNotNull(name, values);
        this.name = name;
        this.values = values;
        this.matchingRule = CaseIgnoreStringMatchingRule.getInstance();
    }
    
    public Attribute(final String name, final Collection<String> values) {
        this.hashCode = -1;
        Validator.ensureNotNull(name, values);
        this.name = name;
        this.values = new ASN1OctetString[values.size()];
        int i = 0;
        for (final String s : values) {
            this.values[i++] = new ASN1OctetString(s);
        }
        this.matchingRule = CaseIgnoreStringMatchingRule.getInstance();
    }
    
    public Attribute(final String name, final MatchingRule matchingRule) {
        this.hashCode = -1;
        Validator.ensureNotNull(name, matchingRule);
        this.name = name;
        this.matchingRule = matchingRule;
        this.values = Attribute.NO_VALUES;
    }
    
    public Attribute(final String name, final MatchingRule matchingRule, final String value) {
        this.hashCode = -1;
        Validator.ensureNotNull(name, matchingRule, value);
        this.name = name;
        this.matchingRule = matchingRule;
        this.values = new ASN1OctetString[] { new ASN1OctetString(value) };
    }
    
    public Attribute(final String name, final MatchingRule matchingRule, final byte[] value) {
        this.hashCode = -1;
        Validator.ensureNotNull(name, matchingRule, value);
        this.name = name;
        this.matchingRule = matchingRule;
        this.values = new ASN1OctetString[] { new ASN1OctetString(value) };
    }
    
    public Attribute(final String name, final MatchingRule matchingRule, final String... values) {
        this.hashCode = -1;
        Validator.ensureNotNull(name, matchingRule, values);
        this.name = name;
        this.matchingRule = matchingRule;
        this.values = new ASN1OctetString[values.length];
        for (int i = 0; i < values.length; ++i) {
            this.values[i] = new ASN1OctetString(values[i]);
        }
    }
    
    public Attribute(final String name, final MatchingRule matchingRule, final byte[]... values) {
        this.hashCode = -1;
        Validator.ensureNotNull(name, matchingRule, values);
        this.name = name;
        this.matchingRule = matchingRule;
        this.values = new ASN1OctetString[values.length];
        for (int i = 0; i < values.length; ++i) {
            this.values[i] = new ASN1OctetString(values[i]);
        }
    }
    
    public Attribute(final String name, final MatchingRule matchingRule, final Collection<String> values) {
        this.hashCode = -1;
        Validator.ensureNotNull(name, matchingRule, values);
        this.name = name;
        this.matchingRule = matchingRule;
        this.values = new ASN1OctetString[values.size()];
        int i = 0;
        for (final String s : values) {
            this.values[i++] = new ASN1OctetString(s);
        }
    }
    
    public Attribute(final String name, final MatchingRule matchingRule, final ASN1OctetString[] values) {
        this.hashCode = -1;
        this.name = name;
        this.matchingRule = matchingRule;
        this.values = values;
    }
    
    public Attribute(final String name, final Schema schema, final String... values) {
        this(name, MatchingRule.selectEqualityMatchingRule(name, schema), values);
    }
    
    public Attribute(final String name, final Schema schema, final byte[]... values) {
        this(name, MatchingRule.selectEqualityMatchingRule(name, schema), values);
    }
    
    public Attribute(final String name, final Schema schema, final Collection<String> values) {
        this(name, MatchingRule.selectEqualityMatchingRule(name, schema), values);
    }
    
    public Attribute(final String name, final Schema schema, final ASN1OctetString[] values) {
        this(name, MatchingRule.selectEqualityMatchingRule(name, schema), values);
    }
    
    public static Attribute mergeAttributes(final Attribute attr1, final Attribute attr2) {
        return mergeAttributes(attr1, attr2, attr1.matchingRule);
    }
    
    public static Attribute mergeAttributes(final Attribute attr1, final Attribute attr2, final MatchingRule matchingRule) {
        Validator.ensureNotNull(attr1, attr2);
        final String name = attr1.name;
        Validator.ensureTrue(name.equalsIgnoreCase(attr2.name));
        MatchingRule mr;
        if (matchingRule == null) {
            mr = attr1.matchingRule;
        }
        else {
            mr = matchingRule;
        }
        ASN1OctetString[] mergedValues = new ASN1OctetString[attr1.values.length + attr2.values.length];
        System.arraycopy(attr1.values, 0, mergedValues, 0, attr1.values.length);
        int pos = attr1.values.length;
        for (final ASN1OctetString attr2Value : attr2.values) {
            if (!attr1.hasValue(attr2Value, mr)) {
                mergedValues[pos++] = attr2Value;
            }
        }
        if (pos != mergedValues.length) {
            final ASN1OctetString[] newMergedValues = new ASN1OctetString[pos];
            System.arraycopy(mergedValues, 0, newMergedValues, 0, pos);
            mergedValues = newMergedValues;
        }
        return new Attribute(name, mr, mergedValues);
    }
    
    public static Attribute removeValues(final Attribute attr1, final Attribute attr2) {
        return removeValues(attr1, attr2, attr1.matchingRule);
    }
    
    public static Attribute removeValues(final Attribute attr1, final Attribute attr2, final MatchingRule matchingRule) {
        Validator.ensureNotNull(attr1, attr2);
        final String name = attr1.name;
        Validator.ensureTrue(name.equalsIgnoreCase(attr2.name));
        MatchingRule mr;
        if (matchingRule == null) {
            mr = attr1.matchingRule;
        }
        else {
            mr = matchingRule;
        }
        final ArrayList<ASN1OctetString> newValues = new ArrayList<ASN1OctetString>(Arrays.asList(attr1.values));
        final Iterator<ASN1OctetString> iterator = newValues.iterator();
        while (iterator.hasNext()) {
            if (attr2.hasValue(iterator.next(), mr)) {
                iterator.remove();
            }
        }
        final ASN1OctetString[] newValueArray = new ASN1OctetString[newValues.size()];
        newValues.toArray(newValueArray);
        return new Attribute(name, mr, newValueArray);
    }
    
    public String getName() {
        return this.name;
    }
    
    public String getBaseName() {
        return getBaseName(this.name);
    }
    
    public static String getBaseName(final String name) {
        final int semicolonPos = name.indexOf(59);
        if (semicolonPos > 0) {
            return name.substring(0, semicolonPos);
        }
        return name;
    }
    
    public boolean nameIsValid() {
        return nameIsValid(this.name, true);
    }
    
    public static boolean nameIsValid(final String s) {
        return nameIsValid(s, true);
    }
    
    public static boolean nameIsValid(final String s, final boolean allowOptions) {
        final int length;
        if (s == null || (length = s.length()) == 0) {
            return false;
        }
        final char firstChar = s.charAt(0);
        if ((firstChar < 'a' || firstChar > 'z') && (firstChar < 'A' || firstChar > 'Z')) {
            return false;
        }
        boolean lastWasSemiColon = false;
        for (int i = 1; i < length; ++i) {
            final char c = s.charAt(i);
            if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')) {
                lastWasSemiColon = false;
            }
            else if ((c >= '0' && c <= '9') || c == '-') {
                if (lastWasSemiColon) {
                    return false;
                }
                lastWasSemiColon = false;
            }
            else {
                if (c != ';') {
                    return false;
                }
                if (lastWasSemiColon || !allowOptions) {
                    return false;
                }
                lastWasSemiColon = true;
            }
        }
        return !lastWasSemiColon;
    }
    
    public boolean hasOptions() {
        return hasOptions(this.name);
    }
    
    public static boolean hasOptions(final String name) {
        return name.indexOf(59) > 0;
    }
    
    public boolean hasOption(final String option) {
        return hasOption(this.name, option);
    }
    
    public static boolean hasOption(final String name, final String option) {
        final Set<String> options = getOptions(name);
        for (final String s : options) {
            if (s.equalsIgnoreCase(option)) {
                return true;
            }
        }
        return false;
    }
    
    public Set<String> getOptions() {
        return getOptions(this.name);
    }
    
    public static Set<String> getOptions(final String name) {
        int semicolonPos = name.indexOf(59);
        if (semicolonPos > 0) {
            final LinkedHashSet<String> options = new LinkedHashSet<String>(StaticUtils.computeMapCapacity(5));
            while (true) {
                final int nextSemicolonPos = name.indexOf(59, semicolonPos + 1);
                if (nextSemicolonPos <= 0) {
                    break;
                }
                options.add(name.substring(semicolonPos + 1, nextSemicolonPos));
                semicolonPos = nextSemicolonPos;
            }
            options.add(name.substring(semicolonPos + 1));
            return Collections.unmodifiableSet((Set<? extends String>)options);
        }
        return Collections.emptySet();
    }
    
    public MatchingRule getMatchingRule() {
        return this.matchingRule;
    }
    
    public String getValue() {
        if (this.values.length == 0) {
            return null;
        }
        return this.values[0].stringValue();
    }
    
    public byte[] getValueByteArray() {
        if (this.values.length == 0) {
            return null;
        }
        return this.values[0].getValue();
    }
    
    public Boolean getValueAsBoolean() {
        if (this.values.length == 0) {
            return null;
        }
        final String lowerValue = StaticUtils.toLowerCase(this.values[0].stringValue());
        if (lowerValue.equals("true") || lowerValue.equals("t") || lowerValue.equals("yes") || lowerValue.equals("y") || lowerValue.equals("on") || lowerValue.equals("1")) {
            return Boolean.TRUE;
        }
        if (lowerValue.equals("false") || lowerValue.equals("f") || lowerValue.equals("no") || lowerValue.equals("n") || lowerValue.equals("off") || lowerValue.equals("0")) {
            return Boolean.FALSE;
        }
        return null;
    }
    
    public Date getValueAsDate() {
        if (this.values.length == 0) {
            return null;
        }
        try {
            return StaticUtils.decodeGeneralizedTime(this.values[0].stringValue());
        }
        catch (final Exception e) {
            Debug.debugException(e);
            return null;
        }
    }
    
    public DN getValueAsDN() {
        if (this.values.length == 0) {
            return null;
        }
        try {
            return new DN(this.values[0].stringValue());
        }
        catch (final Exception e) {
            Debug.debugException(e);
            return null;
        }
    }
    
    public Integer getValueAsInteger() {
        if (this.values.length == 0) {
            return null;
        }
        try {
            return Integer.valueOf(this.values[0].stringValue());
        }
        catch (final NumberFormatException nfe) {
            Debug.debugException(nfe);
            return null;
        }
    }
    
    public Long getValueAsLong() {
        if (this.values.length == 0) {
            return null;
        }
        try {
            return Long.valueOf(this.values[0].stringValue());
        }
        catch (final NumberFormatException nfe) {
            Debug.debugException(nfe);
            return null;
        }
    }
    
    public String[] getValues() {
        if (this.values.length == 0) {
            return StaticUtils.NO_STRINGS;
        }
        final String[] stringValues = new String[this.values.length];
        for (int i = 0; i < this.values.length; ++i) {
            stringValues[i] = this.values[i].stringValue();
        }
        return stringValues;
    }
    
    public byte[][] getValueByteArrays() {
        if (this.values.length == 0) {
            return Attribute.NO_BYTE_VALUES;
        }
        final byte[][] byteValues = new byte[this.values.length][];
        for (int i = 0; i < this.values.length; ++i) {
            byteValues[i] = this.values[i].getValue();
        }
        return byteValues;
    }
    
    public ASN1OctetString[] getRawValues() {
        return this.values;
    }
    
    public boolean hasValue() {
        return this.values.length > 0;
    }
    
    public boolean hasValue(final String value) {
        Validator.ensureNotNull(value);
        return this.hasValue(new ASN1OctetString(value), this.matchingRule);
    }
    
    public boolean hasValue(final String value, final MatchingRule matchingRule) {
        Validator.ensureNotNull(value);
        return this.hasValue(new ASN1OctetString(value), matchingRule);
    }
    
    public boolean hasValue(final byte[] value) {
        Validator.ensureNotNull(value);
        return this.hasValue(new ASN1OctetString(value), this.matchingRule);
    }
    
    public boolean hasValue(final byte[] value, final MatchingRule matchingRule) {
        Validator.ensureNotNull(value);
        return this.hasValue(new ASN1OctetString(value), matchingRule);
    }
    
    boolean hasValue(final ASN1OctetString value) {
        return this.hasValue(value, this.matchingRule);
    }
    
    boolean hasValue(final ASN1OctetString value, final MatchingRule matchingRule) {
        try {
            return matchingRule.matchesAnyValue(value, this.values);
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            for (final ASN1OctetString existingValue : this.values) {
                if (value.equalsIgnoreType(existingValue)) {
                    return true;
                }
            }
            return false;
        }
    }
    
    public int size() {
        return this.values.length;
    }
    
    public void writeTo(final ASN1Buffer buffer) {
        final ASN1BufferSequence attrSequence = buffer.beginSequence();
        buffer.addOctetString(this.name);
        final ASN1BufferSet valueSet = buffer.beginSet();
        for (final ASN1OctetString value : this.values) {
            buffer.addElement(value);
        }
        valueSet.end();
        attrSequence.end();
    }
    
    public ASN1Sequence encode() {
        final ASN1Element[] elements = { new ASN1OctetString(this.name), new ASN1Set((ASN1Element[])this.values) };
        return new ASN1Sequence(elements);
    }
    
    public static Attribute readFrom(final ASN1StreamReader reader) throws LDAPException {
        return readFrom(reader, null);
    }
    
    public static Attribute readFrom(final ASN1StreamReader reader, final Schema schema) throws LDAPException {
        try {
            Validator.ensureNotNull(reader.beginSequence());
            final String attrName = reader.readString();
            Validator.ensureNotNull(attrName);
            final MatchingRule matchingRule = MatchingRule.selectEqualityMatchingRule(attrName, schema);
            final ArrayList<ASN1OctetString> valueList = new ArrayList<ASN1OctetString>(10);
            final ASN1StreamReaderSet valueSet = reader.beginSet();
            while (valueSet.hasMoreElements()) {
                valueList.add(new ASN1OctetString(reader.readBytes()));
            }
            final ASN1OctetString[] values = new ASN1OctetString[valueList.size()];
            valueList.toArray(values);
            return new Attribute(attrName, matchingRule, values);
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new LDAPException(ResultCode.DECODING_ERROR, LDAPMessages.ERR_ATTR_CANNOT_DECODE.get(StaticUtils.getExceptionMessage(e)), e);
        }
    }
    
    public static Attribute decode(final ASN1Sequence encodedAttribute) throws LDAPException {
        Validator.ensureNotNull(encodedAttribute);
        final ASN1Element[] elements = encodedAttribute.elements();
        if (elements.length != 2) {
            throw new LDAPException(ResultCode.DECODING_ERROR, LDAPMessages.ERR_ATTR_DECODE_INVALID_COUNT.get(elements.length));
        }
        final String name = ASN1OctetString.decodeAsOctetString(elements[0]).stringValue();
        ASN1Set valueSet;
        try {
            valueSet = ASN1Set.decodeAsSet(elements[1]);
        }
        catch (final ASN1Exception ae) {
            Debug.debugException(ae);
            throw new LDAPException(ResultCode.DECODING_ERROR, LDAPMessages.ERR_ATTR_DECODE_VALUE_SET.get(StaticUtils.getExceptionMessage(ae)), ae);
        }
        final ASN1OctetString[] values = new ASN1OctetString[valueSet.elements().length];
        for (int i = 0; i < values.length; ++i) {
            values[i] = ASN1OctetString.decodeAsOctetString(valueSet.elements()[i]);
        }
        return new Attribute(name, CaseIgnoreStringMatchingRule.getInstance(), values);
    }
    
    public boolean needsBase64Encoding() {
        for (final ASN1OctetString v : this.values) {
            if (needsBase64Encoding(v.getValue())) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean needsBase64Encoding(final String v) {
        return needsBase64Encoding(StaticUtils.getBytes(v));
    }
    
    public static boolean needsBase64Encoding(final byte[] v) {
        if (v.length == 0) {
            return false;
        }
        switch (v[0] & 0xFF) {
            case 32:
            case 58:
            case 60: {
                return true;
            }
            default: {
                if ((v[v.length - 1] & 0xFF) == 0x20) {
                    return true;
                }
                final byte[] arr$ = v;
                final int len$ = arr$.length;
                int i$ = 0;
                while (i$ < len$) {
                    final byte b = arr$[i$];
                    switch (b & 0xFF) {
                        case 0:
                        case 10:
                        case 13: {
                            return true;
                        }
                        default: {
                            if ((b & 0x80) != 0x0) {
                                return true;
                            }
                            ++i$;
                            continue;
                        }
                    }
                }
                return false;
            }
        }
    }
    
    @Override
    public int hashCode() {
        if (this.hashCode == -1) {
            int c = StaticUtils.toLowerCase(this.name).hashCode();
            for (final ASN1OctetString value : this.values) {
                try {
                    c += this.matchingRule.normalize(value).hashCode();
                }
                catch (final LDAPException le) {
                    Debug.debugException(le);
                    c += value.hashCode();
                }
            }
            this.hashCode = c;
        }
        return this.hashCode;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == null) {
            return false;
        }
        if (o == this) {
            return true;
        }
        if (!(o instanceof Attribute)) {
            return false;
        }
        final Attribute a = (Attribute)o;
        if (!this.name.equalsIgnoreCase(a.name)) {
            return false;
        }
        if (this.values.length != a.values.length) {
            return false;
        }
        if (this.values.length > 10) {
            final HashSet<ASN1OctetString> unNormalizedValues = StaticUtils.hashSetOf(this.values);
            HashSet<ASN1OctetString> normalizedMissingValues = null;
            for (final ASN1OctetString value : a.values) {
                if (!unNormalizedValues.remove(value)) {
                    if (normalizedMissingValues == null) {
                        normalizedMissingValues = new HashSet<ASN1OctetString>(StaticUtils.computeMapCapacity(this.values.length));
                    }
                    try {
                        normalizedMissingValues.add(this.matchingRule.normalize(value));
                    }
                    catch (final Exception e) {
                        Debug.debugException(e);
                        return false;
                    }
                }
            }
            if (normalizedMissingValues != null) {
                for (final ASN1OctetString value2 : unNormalizedValues) {
                    try {
                        if (!normalizedMissingValues.contains(this.matchingRule.normalize(value2))) {
                            return false;
                        }
                        continue;
                    }
                    catch (final Exception e2) {
                        Debug.debugException(e2);
                        return false;
                    }
                }
            }
        }
        else {
            for (final ASN1OctetString value2 : this.values) {
                if (!a.hasValue(value2)) {
                    return false;
                }
            }
        }
        return true;
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        this.toString(buffer);
        return buffer.toString();
    }
    
    public void toString(final StringBuilder buffer) {
        buffer.append("Attribute(name=");
        buffer.append(this.name);
        if (this.values.length == 0) {
            buffer.append(", values={");
        }
        else if (this.needsBase64Encoding()) {
            buffer.append(", base64Values={'");
            for (int i = 0; i < this.values.length; ++i) {
                if (i > 0) {
                    buffer.append("', '");
                }
                buffer.append(Base64.encode(this.values[i].getValue()));
            }
            buffer.append('\'');
        }
        else {
            buffer.append(", values={'");
            for (int i = 0; i < this.values.length; ++i) {
                if (i > 0) {
                    buffer.append("', '");
                }
                buffer.append(this.values[i].stringValue());
            }
            buffer.append('\'');
        }
        buffer.append("})");
    }
    
    static {
        NO_VALUES = new ASN1OctetString[0];
        NO_BYTE_VALUES = new byte[0][];
    }
}
