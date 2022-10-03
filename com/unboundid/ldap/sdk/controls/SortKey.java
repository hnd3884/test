package com.unboundid.ldap.sdk.controls;

import com.unboundid.util.StaticUtils;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.util.Debug;
import java.util.Collection;
import com.unboundid.asn1.ASN1Boolean;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.asn1.ASN1Element;
import java.util.ArrayList;
import com.unboundid.asn1.ASN1Sequence;
import com.unboundid.util.Validator;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import java.io.Serializable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class SortKey implements Serializable
{
    private static final byte TYPE_MATCHING_RULE_ID = Byte.MIN_VALUE;
    private static final byte TYPE_REVERSE_ORDER = -127;
    private static final long serialVersionUID = -8631224188301402858L;
    private final boolean reverseOrder;
    private final String attributeName;
    private final String matchingRuleID;
    
    public SortKey(final String attributeName) {
        this(attributeName, null, false);
    }
    
    public SortKey(final String attributeName, final boolean reverseOrder) {
        this(attributeName, null, reverseOrder);
    }
    
    public SortKey(final String attributeName, final String matchingRuleID, final boolean reverseOrder) {
        Validator.ensureNotNull(attributeName);
        this.attributeName = attributeName;
        this.matchingRuleID = matchingRuleID;
        this.reverseOrder = reverseOrder;
    }
    
    public String getAttributeName() {
        return this.attributeName;
    }
    
    public String getMatchingRuleID() {
        return this.matchingRuleID;
    }
    
    public boolean reverseOrder() {
        return this.reverseOrder;
    }
    
    ASN1Sequence encode() {
        final ArrayList<ASN1Element> elements = new ArrayList<ASN1Element>(3);
        elements.add(new ASN1OctetString(this.attributeName));
        if (this.matchingRuleID != null) {
            elements.add(new ASN1OctetString((byte)(-128), this.matchingRuleID));
        }
        if (this.reverseOrder) {
            elements.add(new ASN1Boolean((byte)(-127), this.reverseOrder));
        }
        return new ASN1Sequence(elements);
    }
    
    public static SortKey decode(final ASN1Element element) throws LDAPException {
        ASN1Element[] elements;
        try {
            elements = ASN1Sequence.decodeAsSequence(element).elements();
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_SORT_KEY_NOT_SEQUENCE.get(e), e);
        }
        if (elements.length < 1 || elements.length > 3) {
            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_SORT_KEY_INVALID_ELEMENT_COUNT.get(elements.length));
        }
        boolean reverseOrder = false;
        String matchingRuleID = null;
        final String attributeName = ASN1OctetString.decodeAsOctetString(elements[0]).stringValue();
        for (int i = 1; i < elements.length; ++i) {
            switch (elements[i].getType()) {
                case Byte.MIN_VALUE: {
                    matchingRuleID = ASN1OctetString.decodeAsOctetString(elements[i]).stringValue();
                    break;
                }
                case -127: {
                    try {
                        reverseOrder = ASN1Boolean.decodeAsBoolean(elements[i]).booleanValue();
                        break;
                    }
                    catch (final Exception e2) {
                        Debug.debugException(e2);
                        throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_SORT_KEY_REVERSE_NOT_BOOLEAN.get(e2), e2);
                    }
                    throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_SORT_KEY_ELEMENT_INVALID_TYPE.get(StaticUtils.toHex(elements[i].getType())));
                }
            }
        }
        return new SortKey(attributeName, matchingRuleID, reverseOrder);
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        this.toString(buffer);
        return buffer.toString();
    }
    
    public void toString(final StringBuilder buffer) {
        buffer.append("SortKey(attributeName=");
        buffer.append(this.attributeName);
        if (this.matchingRuleID != null) {
            buffer.append(", matchingRuleID=");
            buffer.append(this.matchingRuleID);
        }
        buffer.append(", reverseOrder=");
        buffer.append(this.reverseOrder);
        buffer.append(')');
    }
}
