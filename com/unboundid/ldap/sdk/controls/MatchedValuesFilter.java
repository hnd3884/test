package com.unboundid.ldap.sdk.controls;

import com.unboundid.util.Debug;
import java.util.Collection;
import java.util.Arrays;
import java.util.ArrayList;
import com.unboundid.asn1.ASN1Sequence;
import com.unboundid.asn1.ASN1Element;
import com.unboundid.util.StaticUtils;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.ldap.sdk.Filter;
import com.unboundid.util.Validator;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import java.io.Serializable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class MatchedValuesFilter implements Serializable
{
    public static final byte MATCH_TYPE_EQUALITY = -93;
    public static final byte MATCH_TYPE_SUBSTRINGS = -92;
    public static final byte MATCH_TYPE_GREATER_OR_EQUAL = -91;
    public static final byte MATCH_TYPE_LESS_OR_EQUAL = -90;
    public static final byte MATCH_TYPE_PRESENT = -121;
    public static final byte MATCH_TYPE_APPROXIMATE = -88;
    public static final byte MATCH_TYPE_EXTENSIBLE = -87;
    private static final byte SUBSTRING_TYPE_SUBINITIAL = Byte.MIN_VALUE;
    private static final byte SUBSTRING_TYPE_SUBANY = -127;
    private static final byte SUBSTRING_TYPE_SUBFINAL = -126;
    private static final byte EXTENSIBLE_TYPE_MATCHING_RULE_ID = -127;
    private static final byte EXTENSIBLE_TYPE_ATTRIBUTE_NAME = -126;
    private static final byte EXTENSIBLE_TYPE_MATCH_VALUE = -125;
    private static final ASN1OctetString[] NO_SUB_ANY;
    private static final String[] NO_SUB_ANY_STRINGS;
    private static final byte[][] NO_SUB_ANY_BYTES;
    private static final long serialVersionUID = 8144732301100674661L;
    private final ASN1OctetString assertionValue;
    private final ASN1OctetString subFinalValue;
    private final ASN1OctetString subInitialValue;
    private final ASN1OctetString[] subAnyValues;
    private final byte matchType;
    private final String attributeType;
    private final String matchingRuleID;
    
    private MatchedValuesFilter(final byte matchType, final String attributeType, final ASN1OctetString assertionValue, final ASN1OctetString subInitialValue, final ASN1OctetString[] subAnyValues, final ASN1OctetString subFinalValue, final String matchingRuleID) {
        this.matchType = matchType;
        this.attributeType = attributeType;
        this.assertionValue = assertionValue;
        this.subInitialValue = subInitialValue;
        this.subAnyValues = subAnyValues;
        this.subFinalValue = subFinalValue;
        this.matchingRuleID = matchingRuleID;
    }
    
    public static MatchedValuesFilter createEqualityFilter(final String attributeType, final String assertionValue) {
        Validator.ensureNotNull(attributeType, assertionValue);
        return new MatchedValuesFilter((byte)(-93), attributeType, new ASN1OctetString(assertionValue), null, MatchedValuesFilter.NO_SUB_ANY, null, null);
    }
    
    public static MatchedValuesFilter createEqualityFilter(final String attributeType, final byte[] assertionValue) {
        Validator.ensureNotNull(attributeType, assertionValue);
        return new MatchedValuesFilter((byte)(-93), attributeType, new ASN1OctetString(assertionValue), null, MatchedValuesFilter.NO_SUB_ANY, null, null);
    }
    
    public static MatchedValuesFilter createSubstringFilter(final String attributeType, final String subInitialValue, final String[] subAnyValues, final String subFinalValue) {
        Validator.ensureNotNull(attributeType);
        Validator.ensureTrue(subInitialValue != null || (subAnyValues != null && subAnyValues.length > 0) || subFinalValue != null);
        ASN1OctetString subInitialOS;
        if (subInitialValue == null) {
            subInitialOS = null;
        }
        else {
            subInitialOS = new ASN1OctetString((byte)(-128), subInitialValue);
        }
        ASN1OctetString[] subAnyOS;
        if (subAnyValues == null || subAnyValues.length == 0) {
            subAnyOS = MatchedValuesFilter.NO_SUB_ANY;
        }
        else {
            subAnyOS = new ASN1OctetString[subAnyValues.length];
            for (int i = 0; i < subAnyValues.length; ++i) {
                subAnyOS[i] = new ASN1OctetString((byte)(-127), subAnyValues[i]);
            }
        }
        ASN1OctetString subFinalOS;
        if (subFinalValue == null) {
            subFinalOS = null;
        }
        else {
            subFinalOS = new ASN1OctetString((byte)(-126), subFinalValue);
        }
        return new MatchedValuesFilter((byte)(-92), attributeType, null, subInitialOS, subAnyOS, subFinalOS, null);
    }
    
    public static MatchedValuesFilter createSubstringFilter(final String attributeType, final byte[] subInitialValue, final byte[][] subAnyValues, final byte[] subFinalValue) {
        Validator.ensureNotNull(attributeType);
        Validator.ensureTrue(subInitialValue != null || (subAnyValues != null && subAnyValues.length > 0) || subFinalValue != null);
        ASN1OctetString subInitialOS;
        if (subInitialValue == null) {
            subInitialOS = null;
        }
        else {
            subInitialOS = new ASN1OctetString((byte)(-128), subInitialValue);
        }
        ASN1OctetString[] subAnyOS;
        if (subAnyValues == null || subAnyValues.length == 0) {
            subAnyOS = MatchedValuesFilter.NO_SUB_ANY;
        }
        else {
            subAnyOS = new ASN1OctetString[subAnyValues.length];
            for (int i = 0; i < subAnyValues.length; ++i) {
                subAnyOS[i] = new ASN1OctetString((byte)(-127), subAnyValues[i]);
            }
        }
        ASN1OctetString subFinalOS;
        if (subFinalValue == null) {
            subFinalOS = null;
        }
        else {
            subFinalOS = new ASN1OctetString((byte)(-126), subFinalValue);
        }
        return new MatchedValuesFilter((byte)(-92), attributeType, null, subInitialOS, subAnyOS, subFinalOS, null);
    }
    
    public static MatchedValuesFilter createGreaterOrEqualFilter(final String attributeType, final String assertionValue) {
        Validator.ensureNotNull(attributeType, assertionValue);
        return new MatchedValuesFilter((byte)(-91), attributeType, new ASN1OctetString(assertionValue), null, MatchedValuesFilter.NO_SUB_ANY, null, null);
    }
    
    public static MatchedValuesFilter createGreaterOrEqualFilter(final String attributeType, final byte[] assertionValue) {
        Validator.ensureNotNull(attributeType, assertionValue);
        return new MatchedValuesFilter((byte)(-91), attributeType, new ASN1OctetString(assertionValue), null, MatchedValuesFilter.NO_SUB_ANY, null, null);
    }
    
    public static MatchedValuesFilter createLessOrEqualFilter(final String attributeType, final String assertionValue) {
        Validator.ensureNotNull(attributeType, assertionValue);
        return new MatchedValuesFilter((byte)(-90), attributeType, new ASN1OctetString(assertionValue), null, MatchedValuesFilter.NO_SUB_ANY, null, null);
    }
    
    public static MatchedValuesFilter createLessOrEqualFilter(final String attributeType, final byte[] assertionValue) {
        Validator.ensureNotNull(attributeType, assertionValue);
        return new MatchedValuesFilter((byte)(-90), attributeType, new ASN1OctetString(assertionValue), null, MatchedValuesFilter.NO_SUB_ANY, null, null);
    }
    
    public static MatchedValuesFilter createPresentFilter(final String attributeType) {
        Validator.ensureNotNull(attributeType);
        return new MatchedValuesFilter((byte)(-121), attributeType, null, null, MatchedValuesFilter.NO_SUB_ANY, null, null);
    }
    
    public static MatchedValuesFilter createApproximateFilter(final String attributeType, final String assertionValue) {
        Validator.ensureNotNull(attributeType, assertionValue);
        return new MatchedValuesFilter((byte)(-88), attributeType, new ASN1OctetString(assertionValue), null, MatchedValuesFilter.NO_SUB_ANY, null, null);
    }
    
    public static MatchedValuesFilter createApproximateFilter(final String attributeType, final byte[] assertionValue) {
        Validator.ensureNotNull(attributeType, assertionValue);
        return new MatchedValuesFilter((byte)(-88), attributeType, new ASN1OctetString(assertionValue), null, MatchedValuesFilter.NO_SUB_ANY, null, null);
    }
    
    public static MatchedValuesFilter createExtensibleMatchFilter(final String attributeType, final String matchingRuleID, final String assertionValue) {
        Validator.ensureNotNull(assertionValue);
        Validator.ensureTrue(attributeType != null || matchingRuleID != null);
        final ASN1OctetString matchValue = new ASN1OctetString((byte)(-125), assertionValue);
        return new MatchedValuesFilter((byte)(-87), attributeType, matchValue, null, MatchedValuesFilter.NO_SUB_ANY, null, matchingRuleID);
    }
    
    public static MatchedValuesFilter createExtensibleMatchFilter(final String attributeType, final String matchingRuleID, final byte[] assertionValue) {
        Validator.ensureNotNull(assertionValue);
        Validator.ensureTrue(attributeType != null || matchingRuleID != null);
        final ASN1OctetString matchValue = new ASN1OctetString((byte)(-125), assertionValue);
        return new MatchedValuesFilter((byte)(-87), attributeType, matchValue, null, MatchedValuesFilter.NO_SUB_ANY, null, matchingRuleID);
    }
    
    public static MatchedValuesFilter create(final Filter filter) throws LDAPException {
        switch (filter.getFilterType()) {
            case -96: {
                throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_MV_FILTER_AND_NOT_SUPPORTED.get());
            }
            case -95: {
                throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_MV_FILTER_OR_NOT_SUPPORTED.get());
            }
            case -94: {
                throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_MV_FILTER_NOT_NOT_SUPPORTED.get());
            }
            case -93: {
                return createEqualityFilter(filter.getAttributeName(), filter.getAssertionValueBytes());
            }
            case -92: {
                return createSubstringFilter(filter.getAttributeName(), filter.getSubInitialBytes(), filter.getSubAnyBytes(), filter.getSubFinalBytes());
            }
            case -91: {
                return createGreaterOrEqualFilter(filter.getAttributeName(), filter.getAssertionValueBytes());
            }
            case -90: {
                return createLessOrEqualFilter(filter.getAttributeName(), filter.getAssertionValueBytes());
            }
            case -121: {
                return createPresentFilter(filter.getAttributeName());
            }
            case -88: {
                return createApproximateFilter(filter.getAttributeName(), filter.getAssertionValueBytes());
            }
            case -87: {
                if (filter.getDNAttributes()) {
                    throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_MV_FILTER_DNATTRS_NOT_SUPPORTED.get());
                }
                return createExtensibleMatchFilter(filter.getAttributeName(), filter.getMatchingRuleID(), filter.getAssertionValueBytes());
            }
            default: {
                throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_MV_FILTER_INVALID_FILTER_TYPE.get(StaticUtils.toHex(filter.getFilterType())));
            }
        }
    }
    
    public byte getMatchType() {
        return this.matchType;
    }
    
    public String getAttributeType() {
        return this.attributeType;
    }
    
    public String getAssertionValue() {
        if (this.assertionValue == null) {
            return null;
        }
        return this.assertionValue.stringValue();
    }
    
    public byte[] getAssertionValueBytes() {
        if (this.assertionValue == null) {
            return null;
        }
        return this.assertionValue.getValue();
    }
    
    public ASN1OctetString getRawAssertionValue() {
        return this.assertionValue;
    }
    
    public String getSubInitialValue() {
        if (this.subInitialValue == null) {
            return null;
        }
        return this.subInitialValue.stringValue();
    }
    
    public byte[] getSubInitialValueBytes() {
        if (this.subInitialValue == null) {
            return null;
        }
        return this.subInitialValue.getValue();
    }
    
    public ASN1OctetString getRawSubInitialValue() {
        return this.subInitialValue;
    }
    
    public String[] getSubAnyValues() {
        if (this.subAnyValues.length == 0) {
            return MatchedValuesFilter.NO_SUB_ANY_STRINGS;
        }
        final String[] subAnyStrings = new String[this.subAnyValues.length];
        for (int i = 0; i < this.subAnyValues.length; ++i) {
            subAnyStrings[i] = this.subAnyValues[i].stringValue();
        }
        return subAnyStrings;
    }
    
    public byte[][] getSubAnyValueBytes() {
        if (this.subAnyValues.length == 0) {
            return MatchedValuesFilter.NO_SUB_ANY_BYTES;
        }
        final byte[][] subAnyBytes = new byte[this.subAnyValues.length][];
        for (int i = 0; i < this.subAnyValues.length; ++i) {
            subAnyBytes[i] = this.subAnyValues[i].getValue();
        }
        return subAnyBytes;
    }
    
    public ASN1OctetString[] getRawSubAnyValues() {
        return this.subAnyValues;
    }
    
    public String getSubFinalValue() {
        if (this.subFinalValue == null) {
            return null;
        }
        return this.subFinalValue.stringValue();
    }
    
    public byte[] getSubFinalValueBytes() {
        if (this.subFinalValue == null) {
            return null;
        }
        return this.subFinalValue.getValue();
    }
    
    public ASN1OctetString getRawSubFinalValue() {
        return this.subFinalValue;
    }
    
    public String getMatchingRuleID() {
        return this.matchingRuleID;
    }
    
    public ASN1Element encode() {
        switch (this.matchType) {
            case -93:
            case -91:
            case -90:
            case -88: {
                final ASN1Element[] elements = { new ASN1OctetString(this.attributeType), this.assertionValue };
                return new ASN1Sequence(this.matchType, elements);
            }
            case -92: {
                final ArrayList<ASN1Element> subElements = new ArrayList<ASN1Element>(3);
                if (this.subInitialValue != null) {
                    subElements.add(this.subInitialValue);
                }
                if (this.subAnyValues.length > 0) {
                    subElements.addAll(Arrays.asList(this.subAnyValues));
                }
                if (this.subFinalValue != null) {
                    subElements.add(this.subFinalValue);
                }
                final ASN1Element[] elements = { new ASN1OctetString(this.attributeType), new ASN1Sequence(subElements) };
                return new ASN1Sequence(this.matchType, elements);
            }
            case -121: {
                return new ASN1OctetString(this.matchType, this.attributeType);
            }
            case -87: {
                final ArrayList<ASN1Element> extElements = new ArrayList<ASN1Element>(3);
                if (this.attributeType != null) {
                    extElements.add(new ASN1OctetString((byte)(-126), this.attributeType));
                }
                if (this.matchingRuleID != null) {
                    extElements.add(new ASN1OctetString((byte)(-127), this.matchingRuleID));
                }
                extElements.add(this.assertionValue);
                return new ASN1Sequence(this.matchType, extElements);
            }
            default: {
                return null;
            }
        }
    }
    
    public static MatchedValuesFilter decode(final ASN1Element element) throws LDAPException {
        ASN1OctetString assertionValue = null;
        ASN1OctetString subInitialValue = null;
        ASN1OctetString subFinalValue = null;
        ASN1OctetString[] subAnyValues = MatchedValuesFilter.NO_SUB_ANY;
        final byte matchType = element.getType();
        String attributeType = null;
        String matchingRuleID = null;
        switch (matchType) {
            case -93:
            case -91:
            case -90:
            case -88: {
                try {
                    final ASN1Element[] elements = ASN1Sequence.decodeAsSequence(element).elements();
                    attributeType = ASN1OctetString.decodeAsOctetString(elements[0]).stringValue();
                    assertionValue = ASN1OctetString.decodeAsOctetString(elements[1]);
                    break;
                }
                catch (final Exception e) {
                    Debug.debugException(e);
                    throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_MV_FILTER_NOT_AVA.get(e), e);
                }
            }
            case -92: {
                try {
                    final ASN1Element[] elements = ASN1Sequence.decodeAsSequence(element).elements();
                    attributeType = ASN1OctetString.decodeAsOctetString(elements[0]).stringValue();
                    ArrayList<ASN1OctetString> subAnyList = null;
                    final ASN1Element[] arr$;
                    final ASN1Element[] subElements = arr$ = ASN1Sequence.decodeAsSequence(elements[1]).elements();
                    for (final ASN1Element e2 : arr$) {
                        switch (e2.getType()) {
                            case Byte.MIN_VALUE: {
                                if (subInitialValue == null) {
                                    subInitialValue = ASN1OctetString.decodeAsOctetString(e2);
                                    break;
                                }
                                throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_MV_FILTER_MULTIPLE_SUBINITIAL.get());
                            }
                            case -127: {
                                if (subAnyList == null) {
                                    subAnyList = new ArrayList<ASN1OctetString>(subElements.length);
                                }
                                subAnyList.add(ASN1OctetString.decodeAsOctetString(e2));
                                break;
                            }
                            case -126: {
                                if (subFinalValue == null) {
                                    subFinalValue = ASN1OctetString.decodeAsOctetString(e2);
                                    break;
                                }
                                throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_MV_FILTER_MULTIPLE_SUBFINAL.get());
                            }
                            default: {
                                throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_MV_FILTER_INVALID_SUB_TYPE.get(StaticUtils.toHex(e2.getType())));
                            }
                        }
                    }
                    if (subAnyList != null) {
                        subAnyValues = subAnyList.toArray(new ASN1OctetString[subAnyList.size()]);
                    }
                }
                catch (final LDAPException le) {
                    Debug.debugException(le);
                    throw le;
                }
                catch (final Exception e) {
                    Debug.debugException(e);
                    throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_MV_FILTER_CANNOT_DECODE_SUBSTRING.get(e), e);
                }
                if (subInitialValue == null && subAnyValues.length == 0 && subFinalValue == null) {
                    throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_MV_FILTER_NO_SUBSTRING_ELEMENTS.get());
                }
                break;
            }
            case -121: {
                attributeType = ASN1OctetString.decodeAsOctetString(element).stringValue();
                break;
            }
            case -87: {
                try {
                    final ASN1Element[] arr$2;
                    final ASN1Element[] elements = arr$2 = ASN1Sequence.decodeAsSequence(element).elements();
                    for (final ASN1Element e3 : arr$2) {
                        switch (e3.getType()) {
                            case -126: {
                                if (attributeType == null) {
                                    attributeType = ASN1OctetString.decodeAsOctetString(e3).stringValue();
                                    break;
                                }
                                throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_MV_FILTER_EXT_MULTIPLE_AT.get());
                            }
                            case -127: {
                                if (matchingRuleID == null) {
                                    matchingRuleID = ASN1OctetString.decodeAsOctetString(e3).stringValue();
                                    break;
                                }
                                throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_MV_FILTER_MULTIPLE_MRID.get());
                            }
                            case -125: {
                                if (assertionValue == null) {
                                    assertionValue = ASN1OctetString.decodeAsOctetString(e3);
                                    break;
                                }
                                throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_MV_FILTER_EXT_MULTIPLE_VALUE.get());
                            }
                            default: {
                                throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_MV_FILTER_EXT_INVALID_TYPE.get(StaticUtils.toHex(e3.getType())));
                            }
                        }
                    }
                }
                catch (final LDAPException le) {
                    Debug.debugException(le);
                    throw le;
                }
                catch (final Exception e) {
                    Debug.debugException(e);
                    throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_MV_FILTER_EXT_NOT_SEQUENCE.get(e), e);
                }
                if (attributeType == null && matchingRuleID == null) {
                    throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_MV_FILTER_NO_ATTR_OR_MRID.get());
                }
                if (assertionValue == null) {
                    throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_MV_FILTER_EXT_NO_VALUE.get());
                }
                break;
            }
            default: {
                throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_MV_FILTER_INVALID_TYPE.get(StaticUtils.toHex(matchType)));
            }
        }
        return new MatchedValuesFilter(matchType, attributeType, assertionValue, subInitialValue, subAnyValues, subFinalValue, matchingRuleID);
    }
    
    public Filter toFilter() {
        switch (this.matchType) {
            case -93: {
                return Filter.createEqualityFilter(this.attributeType, this.assertionValue.getValue());
            }
            case -92: {
                return Filter.createSubstringFilter(this.attributeType, this.getSubInitialValueBytes(), this.getSubAnyValueBytes(), this.getSubFinalValueBytes());
            }
            case -91: {
                return Filter.createGreaterOrEqualFilter(this.attributeType, this.assertionValue.getValue());
            }
            case -90: {
                return Filter.createLessOrEqualFilter(this.attributeType, this.assertionValue.getValue());
            }
            case -121: {
                return Filter.createPresenceFilter(this.attributeType);
            }
            case -88: {
                return Filter.createApproximateMatchFilter(this.attributeType, this.assertionValue.getValue());
            }
            case -87: {
                return Filter.createExtensibleMatchFilter(this.attributeType, this.matchingRuleID, false, this.assertionValue.getValue());
            }
            default: {
                return null;
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
        buffer.append('(');
        switch (this.matchType) {
            case -93: {
                buffer.append(this.attributeType);
                buffer.append('=');
                buffer.append(this.assertionValue.stringValue());
                break;
            }
            case -92: {
                buffer.append(this.attributeType);
                buffer.append('=');
                if (this.subInitialValue != null) {
                    buffer.append(this.subInitialValue.stringValue());
                }
                for (final ASN1OctetString s : this.subAnyValues) {
                    buffer.append('*');
                    buffer.append(s.stringValue());
                }
                buffer.append('*');
                if (this.subFinalValue != null) {
                    buffer.append(this.subFinalValue.stringValue());
                    break;
                }
                break;
            }
            case -91: {
                buffer.append(this.attributeType);
                buffer.append(">=");
                buffer.append(this.assertionValue.stringValue());
                break;
            }
            case -90: {
                buffer.append(this.attributeType);
                buffer.append("<=");
                buffer.append(this.assertionValue.stringValue());
                break;
            }
            case -121: {
                buffer.append(this.attributeType);
                buffer.append("=*");
                break;
            }
            case -88: {
                buffer.append(this.attributeType);
                buffer.append("~=");
                buffer.append(this.assertionValue.stringValue());
                break;
            }
            case -87: {
                if (this.attributeType != null) {
                    buffer.append(this.attributeType);
                }
                if (this.matchingRuleID != null) {
                    buffer.append(':');
                    buffer.append(this.matchingRuleID);
                }
                buffer.append(":=");
                buffer.append(this.assertionValue.stringValue());
                break;
            }
        }
        buffer.append(')');
    }
    
    static {
        NO_SUB_ANY = new ASN1OctetString[0];
        NO_SUB_ANY_STRINGS = StaticUtils.NO_STRINGS;
        NO_SUB_ANY_BYTES = new byte[0][];
    }
}
