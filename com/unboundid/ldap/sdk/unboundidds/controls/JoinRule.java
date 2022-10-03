package com.unboundid.ldap.sdk.unboundidds.controls;

import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.util.StaticUtils;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.util.Debug;
import com.unboundid.asn1.ASN1Sequence;
import com.unboundid.asn1.ASN1Boolean;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.asn1.ASN1Set;
import com.unboundid.asn1.ASN1Element;
import java.util.List;
import com.unboundid.util.Validator;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import java.io.Serializable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class JoinRule implements Serializable
{
    public static final byte JOIN_TYPE_AND = -96;
    public static final byte JOIN_TYPE_OR = -95;
    public static final byte JOIN_TYPE_DN = -126;
    public static final byte JOIN_TYPE_EQUALITY = -93;
    public static final byte JOIN_TYPE_CONTAINS = -92;
    public static final byte JOIN_TYPE_REVERSE_DN = -123;
    private static final JoinRule[] NO_RULES;
    private static final long serialVersionUID = 9041070342511946580L;
    private final boolean matchAll;
    private final byte type;
    private final JoinRule[] components;
    private final String sourceAttribute;
    private final String targetAttribute;
    
    private JoinRule(final byte type, final JoinRule[] components, final String sourceAttribute, final String targetAttribute, final boolean matchAll) {
        this.type = type;
        this.components = components;
        this.sourceAttribute = sourceAttribute;
        this.targetAttribute = targetAttribute;
        this.matchAll = matchAll;
    }
    
    public static JoinRule createANDRule(final JoinRule... components) {
        Validator.ensureNotNull(components);
        Validator.ensureFalse(components.length == 0);
        return new JoinRule((byte)(-96), components, null, null, false);
    }
    
    public static JoinRule createANDRule(final List<JoinRule> components) {
        Validator.ensureNotNull(components);
        Validator.ensureFalse(components.isEmpty());
        final JoinRule[] compArray = new JoinRule[components.size()];
        return new JoinRule((byte)(-96), components.toArray(compArray), null, null, false);
    }
    
    public static JoinRule createORRule(final JoinRule... components) {
        Validator.ensureNotNull(components);
        Validator.ensureFalse(components.length == 0);
        return new JoinRule((byte)(-95), components, null, null, false);
    }
    
    public static JoinRule createORRule(final List<JoinRule> components) {
        Validator.ensureNotNull(components);
        Validator.ensureFalse(components.isEmpty());
        final JoinRule[] compArray = new JoinRule[components.size()];
        return new JoinRule((byte)(-95), components.toArray(compArray), null, null, false);
    }
    
    public static JoinRule createDNJoin(final String sourceAttribute) {
        Validator.ensureNotNull(sourceAttribute);
        return new JoinRule((byte)(-126), JoinRule.NO_RULES, sourceAttribute, null, false);
    }
    
    public static JoinRule createEqualityJoin(final String sourceAttribute, final String targetAttribute, final boolean matchAll) {
        Validator.ensureNotNull(sourceAttribute, targetAttribute);
        return new JoinRule((byte)(-93), JoinRule.NO_RULES, sourceAttribute, targetAttribute, matchAll);
    }
    
    public static JoinRule createContainsJoin(final String sourceAttribute, final String targetAttribute, final boolean matchAll) {
        Validator.ensureNotNull(sourceAttribute, targetAttribute);
        return new JoinRule((byte)(-92), JoinRule.NO_RULES, sourceAttribute, targetAttribute, matchAll);
    }
    
    public static JoinRule createReverseDNJoin(final String targetAttribute) {
        Validator.ensureNotNull(targetAttribute);
        return new JoinRule((byte)(-123), JoinRule.NO_RULES, null, targetAttribute, false);
    }
    
    public byte getType() {
        return this.type;
    }
    
    public JoinRule[] getComponents() {
        return this.components;
    }
    
    public String getSourceAttribute() {
        return this.sourceAttribute;
    }
    
    public String getTargetAttribute() {
        return this.targetAttribute;
    }
    
    public boolean matchAll() {
        return this.matchAll;
    }
    
    ASN1Element encode() {
        switch (this.type) {
            case -96:
            case -95: {
                final ASN1Element[] compElements = new ASN1Element[this.components.length];
                for (int i = 0; i < this.components.length; ++i) {
                    compElements[i] = this.components[i].encode();
                }
                return new ASN1Set(this.type, compElements);
            }
            case -126: {
                return new ASN1OctetString(this.type, this.sourceAttribute);
            }
            case -93:
            case -92: {
                if (this.matchAll) {
                    return new ASN1Sequence(this.type, new ASN1Element[] { new ASN1OctetString(this.sourceAttribute), new ASN1OctetString(this.targetAttribute), new ASN1Boolean(this.matchAll) });
                }
                return new ASN1Sequence(this.type, new ASN1Element[] { new ASN1OctetString(this.sourceAttribute), new ASN1OctetString(this.targetAttribute) });
            }
            case -123: {
                return new ASN1OctetString(this.type, this.targetAttribute);
            }
            default: {
                return null;
            }
        }
    }
    
    static JoinRule decode(final ASN1Element element) throws LDAPException {
        final byte elementType = element.getType();
        switch (elementType) {
            case -96:
            case -95: {
                try {
                    final ASN1Element[] elements = ASN1Set.decodeAsSet(element).elements();
                    final JoinRule[] rules = new JoinRule[elements.length];
                    for (int i = 0; i < rules.length; ++i) {
                        rules[i] = decode(elements[i]);
                    }
                    return new JoinRule(elementType, rules, null, null, false);
                }
                catch (final Exception e) {
                    Debug.debugException(e);
                    throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_JOIN_RULE_CANNOT_DECODE.get(StaticUtils.getExceptionMessage(e)), e);
                }
            }
            case -126: {
                return new JoinRule(elementType, JoinRule.NO_RULES, ASN1OctetString.decodeAsOctetString(element).stringValue(), null, false);
            }
            case -93:
            case -92: {
                try {
                    final ASN1Element[] elements = ASN1Sequence.decodeAsSequence(element).elements();
                    final String sourceAttribute = elements[0].decodeAsOctetString().stringValue();
                    final String targetAttribute = elements[1].decodeAsOctetString().stringValue();
                    boolean matchAll = false;
                    if (elements.length == 3) {
                        matchAll = elements[2].decodeAsBoolean().booleanValue();
                    }
                    return new JoinRule(elementType, JoinRule.NO_RULES, sourceAttribute, targetAttribute, matchAll);
                }
                catch (final Exception e) {
                    Debug.debugException(e);
                    throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_JOIN_RULE_CANNOT_DECODE.get(StaticUtils.getExceptionMessage(e)), e);
                }
            }
            case -123: {
                return new JoinRule(elementType, JoinRule.NO_RULES, null, ASN1OctetString.decodeAsOctetString(element).stringValue(), false);
            }
            default: {
                throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_JOIN_RULE_DECODE_INVALID_TYPE.get(StaticUtils.toHex(elementType)));
            }
        }
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        this.toString(buffer);
        return buffer.toString();
    }
    
    public void toString(final StringBuilder buffer) {
        switch (this.type) {
            case -96: {
                buffer.append("ANDJoinRule(components={");
                for (int i = 0; i < this.components.length; ++i) {
                    if (i > 0) {
                        buffer.append(", ");
                    }
                    this.components[i].toString(buffer);
                }
                buffer.append("})");
                break;
            }
            case -95: {
                buffer.append("ORJoinRule(components={");
                for (int i = 0; i < this.components.length; ++i) {
                    if (i > 0) {
                        buffer.append(", ");
                    }
                    this.components[i].toString(buffer);
                }
                buffer.append("})");
                break;
            }
            case -126: {
                buffer.append("DNJoinRule(sourceAttr=");
                buffer.append(this.sourceAttribute);
                buffer.append(')');
                break;
            }
            case -93: {
                buffer.append("EqualityJoinRule(sourceAttr=");
                buffer.append(this.sourceAttribute);
                buffer.append(", targetAttr=");
                buffer.append(this.targetAttribute);
                buffer.append(", matchAll=");
                buffer.append(this.matchAll);
                buffer.append(')');
                break;
            }
            case -92: {
                buffer.append("ContainsJoinRule(sourceAttr=");
                buffer.append(this.sourceAttribute);
                buffer.append(", targetAttr=");
                buffer.append(this.targetAttribute);
                buffer.append(", matchAll=");
                buffer.append(this.matchAll);
                buffer.append(')');
                break;
            }
            case -123: {
                buffer.append("ReverseDNJoinRule(targetAttr=");
                buffer.append(this.targetAttribute);
                buffer.append(')');
                break;
            }
        }
    }
    
    static {
        NO_RULES = new JoinRule[0];
    }
}
