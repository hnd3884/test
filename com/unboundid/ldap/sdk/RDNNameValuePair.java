package com.unboundid.ldap.sdk;

import com.unboundid.util.Debug;
import com.unboundid.ldap.matchingrules.MatchingRule;
import com.unboundid.ldap.sdk.schema.AttributeTypeDefinition;
import com.unboundid.util.StaticUtils;
import com.unboundid.ldap.sdk.schema.Schema;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import java.io.Serializable;
import java.util.Comparator;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class RDNNameValuePair implements Comparable<RDNNameValuePair>, Comparator<RDNNameValuePair>, Serializable
{
    private static final long serialVersionUID = -8780852504883527870L;
    private final ASN1OctetString attributeValue;
    private final Schema schema;
    private final String attributeName;
    private volatile String normalizedAttributeName;
    private volatile String normalizedString;
    private volatile String stringRepresentation;
    
    RDNNameValuePair(final String attributeName, final ASN1OctetString attributeValue, final Schema schema) {
        this.attributeName = attributeName;
        this.attributeValue = attributeValue;
        this.schema = schema;
        this.normalizedAttributeName = null;
        this.normalizedString = null;
        this.stringRepresentation = null;
    }
    
    public String getAttributeName() {
        return this.attributeName;
    }
    
    public String getNormalizedAttributeName() {
        if (this.normalizedAttributeName == null) {
            if (this.schema != null) {
                final AttributeTypeDefinition attributeType = this.schema.getAttributeType(this.attributeName);
                if (attributeType != null) {
                    this.normalizedAttributeName = StaticUtils.toLowerCase(attributeType.getNameOrOID());
                }
            }
            if (this.normalizedAttributeName == null) {
                this.normalizedAttributeName = StaticUtils.toLowerCase(this.attributeName);
            }
        }
        return this.normalizedAttributeName;
    }
    
    public boolean hasAttributeName(final String name) {
        if (this.attributeName.equalsIgnoreCase(name)) {
            return true;
        }
        if (this.schema != null) {
            final AttributeTypeDefinition attributeType = this.schema.getAttributeType(this.attributeName);
            return attributeType != null && attributeType.hasNameOrOID(name);
        }
        return false;
    }
    
    public String getAttributeValue() {
        return this.attributeValue.stringValue();
    }
    
    public byte[] getAttributeValueBytes() {
        return this.attributeValue.getValue();
    }
    
    public ASN1OctetString getRawAttributeValue() {
        return this.attributeValue;
    }
    
    public boolean hasAttributeValue(final String value) {
        try {
            final MatchingRule matchingRule = MatchingRule.selectEqualityMatchingRule(this.attributeName, this.schema);
            return matchingRule.valuesMatch(new ASN1OctetString(value), this.attributeValue);
        }
        catch (final Exception e) {
            Debug.debugException(e);
            return false;
        }
    }
    
    public boolean hasAttributeValue(final byte[] value) {
        try {
            final MatchingRule matchingRule = MatchingRule.selectEqualityMatchingRule(this.attributeName, this.schema);
            return matchingRule.valuesMatch(new ASN1OctetString(value), this.attributeValue);
        }
        catch (final Exception e) {
            Debug.debugException(e);
            return false;
        }
    }
    
    @Override
    public int compareTo(final RDNNameValuePair p) {
        final String thisNormalizedName = this.getNormalizedAttributeName();
        final String thatNormalizedName = p.getNormalizedAttributeName();
        final int nameComparison = thisNormalizedName.compareTo(thatNormalizedName);
        if (nameComparison != 0) {
            return nameComparison;
        }
        try {
            final MatchingRule matchingRule = MatchingRule.selectOrderingMatchingRule(this.attributeName, this.schema);
            return matchingRule.compareValues(this.attributeValue, p.attributeValue);
        }
        catch (final Exception e) {
            Debug.debugException(e);
            final String thisNormalizedString = this.toNormalizedString();
            final String thatNormalizedString = p.toNormalizedString();
            return thisNormalizedString.compareTo(thatNormalizedString);
        }
    }
    
    @Override
    public int compare(final RDNNameValuePair p1, final RDNNameValuePair p2) {
        return p1.compareTo(p2);
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
        if (!(o instanceof RDNNameValuePair)) {
            return false;
        }
        final RDNNameValuePair p = (RDNNameValuePair)o;
        return this.toNormalizedString().equals(p.toNormalizedString());
    }
    
    @Override
    public String toString() {
        if (this.stringRepresentation == null) {
            final StringBuilder buffer = new StringBuilder();
            this.toString(buffer, false);
            this.stringRepresentation = buffer.toString();
        }
        return this.stringRepresentation;
    }
    
    public String toMinimallyEncodedString() {
        final StringBuilder buffer = new StringBuilder();
        this.toString(buffer, true);
        return buffer.toString();
    }
    
    public void toString(final StringBuilder buffer, final boolean minimizeEncoding) {
        if (this.stringRepresentation != null && !minimizeEncoding) {
            buffer.append(this.stringRepresentation);
            return;
        }
        final boolean bufferWasEmpty = buffer.length() == 0;
        buffer.append(this.attributeName);
        buffer.append('=');
        RDN.appendValue(buffer, this.attributeValue, minimizeEncoding);
        if (bufferWasEmpty && !minimizeEncoding) {
            this.stringRepresentation = buffer.toString();
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
        buffer.append(this.getNormalizedAttributeName());
        buffer.append('=');
        RDN.appendNormalizedValue(buffer, this.attributeName, this.attributeValue, this.schema);
    }
}
