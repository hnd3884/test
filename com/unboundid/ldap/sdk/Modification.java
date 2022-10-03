package com.unboundid.ldap.sdk;

import java.util.List;
import com.unboundid.util.Base64;
import com.unboundid.asn1.ASN1Exception;
import com.unboundid.asn1.ASN1StreamReaderSet;
import com.unboundid.util.Debug;
import java.util.ArrayList;
import com.unboundid.asn1.ASN1StreamReader;
import com.unboundid.asn1.ASN1Enumerated;
import com.unboundid.asn1.ASN1Set;
import com.unboundid.asn1.ASN1Sequence;
import com.unboundid.asn1.ASN1BufferSet;
import com.unboundid.asn1.ASN1BufferSequence;
import com.unboundid.asn1.ASN1Element;
import com.unboundid.asn1.ASN1Buffer;
import com.unboundid.util.StaticUtils;
import com.unboundid.ldap.matchingrules.MatchingRule;
import com.unboundid.ldap.matchingrules.CaseIgnoreStringMatchingRule;
import com.unboundid.util.Validator;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import java.io.Serializable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class Modification implements Serializable
{
    private static final ASN1OctetString[] NO_VALUES;
    private static final byte[][] NO_BYTE_VALUES;
    private static final long serialVersionUID = 5170107037390858876L;
    private final ASN1OctetString[] values;
    private final ModificationType modificationType;
    private final String attributeName;
    
    public Modification(final ModificationType modificationType, final String attributeName) {
        Validator.ensureNotNull(attributeName);
        this.modificationType = modificationType;
        this.attributeName = attributeName;
        this.values = Modification.NO_VALUES;
    }
    
    public Modification(final ModificationType modificationType, final String attributeName, final String attributeValue) {
        Validator.ensureNotNull(attributeName, attributeValue);
        this.modificationType = modificationType;
        this.attributeName = attributeName;
        this.values = new ASN1OctetString[] { new ASN1OctetString(attributeValue) };
    }
    
    public Modification(final ModificationType modificationType, final String attributeName, final byte[] attributeValue) {
        Validator.ensureNotNull(attributeName, attributeValue);
        this.modificationType = modificationType;
        this.attributeName = attributeName;
        this.values = new ASN1OctetString[] { new ASN1OctetString(attributeValue) };
    }
    
    public Modification(final ModificationType modificationType, final String attributeName, final String... attributeValues) {
        Validator.ensureNotNull(attributeName, attributeValues);
        this.modificationType = modificationType;
        this.attributeName = attributeName;
        this.values = new ASN1OctetString[attributeValues.length];
        for (int i = 0; i < this.values.length; ++i) {
            this.values[i] = new ASN1OctetString(attributeValues[i]);
        }
    }
    
    public Modification(final ModificationType modificationType, final String attributeName, final byte[]... attributeValues) {
        Validator.ensureNotNull(attributeName, attributeValues);
        this.modificationType = modificationType;
        this.attributeName = attributeName;
        this.values = new ASN1OctetString[attributeValues.length];
        for (int i = 0; i < this.values.length; ++i) {
            this.values[i] = new ASN1OctetString(attributeValues[i]);
        }
    }
    
    public Modification(final ModificationType modificationType, final String attributeName, final ASN1OctetString[] attributeValues) {
        this.modificationType = modificationType;
        this.attributeName = attributeName;
        this.values = attributeValues;
    }
    
    public ModificationType getModificationType() {
        return this.modificationType;
    }
    
    public Attribute getAttribute() {
        return new Attribute(this.attributeName, CaseIgnoreStringMatchingRule.getInstance(), this.values);
    }
    
    public String getAttributeName() {
        return this.attributeName;
    }
    
    public boolean hasValue() {
        return this.values.length > 0;
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
            return Modification.NO_BYTE_VALUES;
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
    
    public void writeTo(final ASN1Buffer buffer) {
        final ASN1BufferSequence modSequence = buffer.beginSequence();
        buffer.addEnumerated(this.modificationType.intValue());
        final ASN1BufferSequence attrSequence = buffer.beginSequence();
        buffer.addOctetString(this.attributeName);
        final ASN1BufferSet valueSet = buffer.beginSet();
        for (final ASN1OctetString v : this.values) {
            buffer.addElement(v);
        }
        valueSet.end();
        attrSequence.end();
        modSequence.end();
    }
    
    public ASN1Sequence encode() {
        final ASN1Element[] attrElements = { new ASN1OctetString(this.attributeName), new ASN1Set((ASN1Element[])this.values) };
        final ASN1Element[] modificationElements = { new ASN1Enumerated(this.modificationType.intValue()), new ASN1Sequence(attrElements) };
        return new ASN1Sequence(modificationElements);
    }
    
    public static Modification readFrom(final ASN1StreamReader reader) throws LDAPException {
        try {
            Validator.ensureNotNull(reader.beginSequence());
            final ModificationType modType = ModificationType.valueOf(reader.readEnumerated());
            Validator.ensureNotNull(reader.beginSequence());
            final String attrName = reader.readString();
            final ArrayList<ASN1OctetString> valueList = new ArrayList<ASN1OctetString>(5);
            final ASN1StreamReaderSet valueSet = reader.beginSet();
            while (valueSet.hasMoreElements()) {
                valueList.add(new ASN1OctetString(reader.readBytes()));
            }
            final ASN1OctetString[] values = new ASN1OctetString[valueList.size()];
            valueList.toArray(values);
            return new Modification(modType, attrName, values);
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new LDAPException(ResultCode.DECODING_ERROR, LDAPMessages.ERR_MOD_CANNOT_DECODE.get(StaticUtils.getExceptionMessage(e)), e);
        }
    }
    
    public static Modification decode(final ASN1Sequence modificationSequence) throws LDAPException {
        Validator.ensureNotNull(modificationSequence);
        final ASN1Element[] modificationElements = modificationSequence.elements();
        if (modificationElements.length != 2) {
            throw new LDAPException(ResultCode.DECODING_ERROR, LDAPMessages.ERR_MOD_DECODE_INVALID_ELEMENT_COUNT.get(modificationElements.length));
        }
        int modType;
        try {
            final ASN1Enumerated typeEnumerated = ASN1Enumerated.decodeAsEnumerated(modificationElements[0]);
            modType = typeEnumerated.intValue();
        }
        catch (final ASN1Exception ae) {
            Debug.debugException(ae);
            throw new LDAPException(ResultCode.DECODING_ERROR, LDAPMessages.ERR_MOD_DECODE_CANNOT_PARSE_MOD_TYPE.get(StaticUtils.getExceptionMessage(ae)), ae);
        }
        ASN1Sequence attrSequence;
        try {
            attrSequence = ASN1Sequence.decodeAsSequence(modificationElements[1]);
        }
        catch (final ASN1Exception ae2) {
            Debug.debugException(ae2);
            throw new LDAPException(ResultCode.DECODING_ERROR, LDAPMessages.ERR_MOD_DECODE_CANNOT_PARSE_ATTR.get(StaticUtils.getExceptionMessage(ae2)), ae2);
        }
        final ASN1Element[] attrElements = attrSequence.elements();
        if (attrElements.length != 2) {
            throw new LDAPException(ResultCode.DECODING_ERROR, LDAPMessages.ERR_MOD_DECODE_INVALID_ATTR_ELEMENT_COUNT.get(attrElements.length));
        }
        final String attrName = ASN1OctetString.decodeAsOctetString(attrElements[0]).stringValue();
        ASN1Set valueSet;
        try {
            valueSet = ASN1Set.decodeAsSet(attrElements[1]);
        }
        catch (final ASN1Exception ae3) {
            Debug.debugException(ae3);
            throw new LDAPException(ResultCode.DECODING_ERROR, LDAPMessages.ERR_MOD_DECODE_CANNOT_PARSE_ATTR_VALUE_SET.get(StaticUtils.getExceptionMessage(ae3)), ae3);
        }
        final ASN1Element[] valueElements = valueSet.elements();
        final ASN1OctetString[] values = new ASN1OctetString[valueElements.length];
        for (int i = 0; i < values.length; ++i) {
            values[i] = ASN1OctetString.decodeAsOctetString(valueElements[i]);
        }
        return new Modification(ModificationType.valueOf(modType), attrName, values);
    }
    
    @Override
    public int hashCode() {
        int hashCode = this.modificationType.intValue() + StaticUtils.toLowerCase(this.attributeName).hashCode();
        for (final ASN1OctetString value : this.values) {
            hashCode += value.hashCode();
        }
        return hashCode;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == null) {
            return false;
        }
        if (o == this) {
            return true;
        }
        if (!(o instanceof Modification)) {
            return false;
        }
        final Modification mod = (Modification)o;
        if (this.modificationType != mod.modificationType) {
            return false;
        }
        if (!this.attributeName.equalsIgnoreCase(mod.attributeName)) {
            return false;
        }
        if (this.values.length != mod.values.length) {
            return false;
        }
        for (final ASN1OctetString value : this.values) {
            boolean found = false;
            for (int j = 0; j < mod.values.length; ++j) {
                if (value.equalsIgnoreType(mod.values[j])) {
                    found = true;
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
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        this.toString(buffer);
        return buffer.toString();
    }
    
    public void toString(final StringBuilder buffer) {
        buffer.append("LDAPModification(type=");
        switch (this.modificationType.intValue()) {
            case 0: {
                buffer.append("add");
                break;
            }
            case 1: {
                buffer.append("delete");
                break;
            }
            case 2: {
                buffer.append("replace");
                break;
            }
            case 3: {
                buffer.append("increment");
                break;
            }
            default: {
                buffer.append(this.modificationType);
                break;
            }
        }
        buffer.append(", attr=");
        buffer.append(this.attributeName);
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
    
    private boolean needsBase64Encoding() {
        for (final ASN1OctetString s : this.values) {
            if (Attribute.needsBase64Encoding(s.getValue())) {
                return true;
            }
        }
        return false;
    }
    
    public void toCode(final List<String> lineList, final int indentSpaces, final String firstLinePrefix, final String lastLineSuffix) {
        final StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < indentSpaces; ++i) {
            buffer.append(' ');
        }
        final String indent = buffer.toString();
        buffer.setLength(0);
        buffer.append(indent);
        if (firstLinePrefix != null) {
            buffer.append(firstLinePrefix);
        }
        buffer.append("new Modification(");
        lineList.add(buffer.toString());
        buffer.setLength(0);
        buffer.append(indent);
        buffer.append("     \"ModificationType.");
        buffer.append(this.modificationType.getName());
        buffer.append(',');
        lineList.add(buffer.toString());
        buffer.setLength(0);
        buffer.append(indent);
        buffer.append("     \"");
        buffer.append(this.attributeName);
        buffer.append('\"');
        if (this.values.length > 0) {
            boolean allPrintable = true;
            ASN1OctetString[] attrValues;
            if (StaticUtils.isSensitiveToCodeAttribute(this.attributeName)) {
                attrValues = new ASN1OctetString[this.values.length];
                for (int j = 0; j < this.values.length; ++j) {
                    attrValues[j] = new ASN1OctetString("---redacted-value-" + (j + 1) + "---");
                }
            }
            else {
                attrValues = this.values;
                for (final ASN1OctetString v : this.values) {
                    if (!StaticUtils.isPrintableString(v.getValue())) {
                        allPrintable = false;
                        break;
                    }
                }
            }
            for (final ASN1OctetString v : attrValues) {
                buffer.append(',');
                lineList.add(buffer.toString());
                buffer.setLength(0);
                buffer.append(indent);
                buffer.append("     ");
                if (allPrintable) {
                    buffer.append('\"');
                    buffer.append(v.stringValue());
                    buffer.append('\"');
                }
                else {
                    StaticUtils.byteArrayToCode(v.getValue(), buffer);
                }
            }
        }
        buffer.append(')');
        if (lastLineSuffix != null) {
            buffer.append(lastLineSuffix);
        }
        lineList.add(buffer.toString());
    }
    
    static {
        NO_VALUES = new ASN1OctetString[0];
        NO_BYTE_VALUES = new byte[0][];
    }
}
