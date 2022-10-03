package com.unboundid.ldap.matchingrules;

import com.unboundid.util.StaticUtils;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.util.Debug;
import com.unboundid.asn1.ASN1Element;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;

@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class IntegerMatchingRule extends MatchingRule
{
    private static final IntegerMatchingRule INSTANCE;
    public static final String EQUALITY_RULE_NAME = "integerMatch";
    static final String LOWER_EQUALITY_RULE_NAME;
    public static final String EQUALITY_RULE_OID = "2.5.13.14";
    public static final String ORDERING_RULE_NAME = "integerOrderingMatch";
    static final String LOWER_ORDERING_RULE_NAME;
    public static final String ORDERING_RULE_OID = "2.5.13.15";
    private static final long serialVersionUID = -9056942146971528818L;
    
    public static IntegerMatchingRule getInstance() {
        return IntegerMatchingRule.INSTANCE;
    }
    
    @Override
    public String getEqualityMatchingRuleName() {
        return "integerMatch";
    }
    
    @Override
    public String getEqualityMatchingRuleOID() {
        return "2.5.13.14";
    }
    
    @Override
    public String getOrderingMatchingRuleName() {
        return "integerOrderingMatch";
    }
    
    @Override
    public String getOrderingMatchingRuleOID() {
        return "2.5.13.15";
    }
    
    @Override
    public String getSubstringMatchingRuleName() {
        return null;
    }
    
    @Override
    public String getSubstringMatchingRuleOID() {
        return null;
    }
    
    @Override
    public boolean valuesMatch(final ASN1OctetString value1, final ASN1OctetString value2) throws LDAPException {
        return this.normalize(value1).equals(this.normalize(value2));
    }
    
    @Override
    public boolean matchesAnyValue(final ASN1OctetString assertionValue, final ASN1OctetString[] attributeValues) throws LDAPException {
        if (assertionValue == null || attributeValues == null || attributeValues.length == 0) {
            return false;
        }
        final ASN1OctetString normalizedAssertionValue = this.normalize(assertionValue);
        for (final ASN1OctetString attributeValue : attributeValues) {
            try {
                if (normalizedAssertionValue.equalsIgnoreType(this.normalize(attributeValue))) {
                    return true;
                }
            }
            catch (final Exception e) {
                Debug.debugException(e);
            }
        }
        return false;
    }
    
    @Override
    public boolean matchesSubstring(final ASN1OctetString value, final ASN1OctetString subInitial, final ASN1OctetString[] subAny, final ASN1OctetString subFinal) throws LDAPException {
        throw new LDAPException(ResultCode.INAPPROPRIATE_MATCHING, MatchingRuleMessages.ERR_INTEGER_SUBSTRING_MATCHING_NOT_SUPPORTED.get());
    }
    
    @Override
    public int compareValues(final ASN1OctetString value1, final ASN1OctetString value2) throws LDAPException {
        final byte[] norm1Bytes = this.normalize(value1).getValue();
        final byte[] norm2Bytes = this.normalize(value2).getValue();
        if (norm1Bytes[0] == 45) {
            if (norm2Bytes[0] != 45) {
                return -1;
            }
            if (norm1Bytes.length < norm2Bytes.length) {
                return 1;
            }
            if (norm1Bytes.length > norm2Bytes.length) {
                return -1;
            }
            for (int i = 1; i < norm1Bytes.length; ++i) {
                final int difference = norm2Bytes[i] - norm1Bytes[i];
                if (difference != 0) {
                    return difference;
                }
            }
            return 0;
        }
        else {
            if (norm2Bytes[0] == 45) {
                return 1;
            }
            if (norm1Bytes.length < norm2Bytes.length) {
                return -1;
            }
            if (norm1Bytes.length > norm2Bytes.length) {
                return 1;
            }
            for (int i = 0; i < norm1Bytes.length; ++i) {
                final int difference = norm1Bytes[i] - norm2Bytes[i];
                if (difference != 0) {
                    return difference;
                }
            }
            return 0;
        }
    }
    
    @Override
    public ASN1OctetString normalize(final ASN1OctetString value) throws LDAPException {
        final byte[] valueBytes = value.getValue();
        if (valueBytes.length == 0) {
            throw new LDAPException(ResultCode.INVALID_ATTRIBUTE_SYNTAX, MatchingRuleMessages.ERR_INTEGER_ZERO_LENGTH_NOT_ALLOWED.get());
        }
        if (valueBytes[0] != 32 && valueBytes[valueBytes.length - 1] != 32) {
            for (int i = 0; i < valueBytes.length; ++i) {
                switch (valueBytes[i]) {
                    case 45: {
                        if (i != 0 || valueBytes.length == 1) {
                            throw new LDAPException(ResultCode.INVALID_ATTRIBUTE_SYNTAX, MatchingRuleMessages.ERR_INTEGER_INVALID_CHARACTER.get(i));
                        }
                        break;
                    }
                    case 48: {
                        if ((i == 0 && valueBytes.length > 1) || (i == 1 && valueBytes[0] == 45)) {
                            throw new LDAPException(ResultCode.INVALID_ATTRIBUTE_SYNTAX, MatchingRuleMessages.ERR_INTEGER_INVALID_LEADING_ZERO.get());
                        }
                        break;
                    }
                    case 49:
                    case 50:
                    case 51:
                    case 52:
                    case 53:
                    case 54:
                    case 55:
                    case 56:
                    case 57: {
                        break;
                    }
                    default: {
                        throw new LDAPException(ResultCode.INVALID_ATTRIBUTE_SYNTAX, MatchingRuleMessages.ERR_INTEGER_INVALID_CHARACTER.get(i));
                    }
                }
            }
            return value;
        }
        final String valueStr = value.stringValue().trim();
        if (valueStr.isEmpty()) {
            throw new LDAPException(ResultCode.INVALID_ATTRIBUTE_SYNTAX, MatchingRuleMessages.ERR_INTEGER_ZERO_LENGTH_NOT_ALLOWED.get());
        }
        for (int j = 0; j < valueStr.length(); ++j) {
            switch (valueStr.charAt(j)) {
                case '-': {
                    if (j != 0 || valueStr.length() == 1) {
                        throw new LDAPException(ResultCode.INVALID_ATTRIBUTE_SYNTAX, MatchingRuleMessages.ERR_INTEGER_INVALID_CHARACTER.get(j));
                    }
                    break;
                }
                case '0': {
                    if ((j == 0 && valueStr.length() > 1) || (j == 1 && valueStr.charAt(0) == '-')) {
                        throw new LDAPException(ResultCode.INVALID_ATTRIBUTE_SYNTAX, MatchingRuleMessages.ERR_INTEGER_INVALID_LEADING_ZERO.get());
                    }
                    break;
                }
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9': {
                    break;
                }
                default: {
                    throw new LDAPException(ResultCode.INVALID_ATTRIBUTE_SYNTAX, MatchingRuleMessages.ERR_INTEGER_INVALID_CHARACTER.get(j));
                }
            }
        }
        return new ASN1OctetString(valueStr);
    }
    
    @Override
    public ASN1OctetString normalizeSubstring(final ASN1OctetString value, final byte substringType) throws LDAPException {
        throw new LDAPException(ResultCode.INAPPROPRIATE_MATCHING, MatchingRuleMessages.ERR_INTEGER_SUBSTRING_MATCHING_NOT_SUPPORTED.get());
    }
    
    static {
        INSTANCE = new IntegerMatchingRule();
        LOWER_EQUALITY_RULE_NAME = StaticUtils.toLowerCase("integerMatch");
        LOWER_ORDERING_RULE_NAME = StaticUtils.toLowerCase("integerOrderingMatch");
    }
}
