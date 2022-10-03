package com.unboundid.ldap.matchingrules;

import com.unboundid.util.StaticUtils;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;

@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class BooleanMatchingRule extends MatchingRule
{
    private static final BooleanMatchingRule INSTANCE;
    private static final ASN1OctetString TRUE_VALUE;
    private static final ASN1OctetString FALSE_VALUE;
    public static final String EQUALITY_RULE_NAME = "booleanMatch";
    static final String LOWER_EQUALITY_RULE_NAME;
    public static final String EQUALITY_RULE_OID = "2.5.13.13";
    private static final long serialVersionUID = 5137725892611277972L;
    
    public static BooleanMatchingRule getInstance() {
        return BooleanMatchingRule.INSTANCE;
    }
    
    @Override
    public String getEqualityMatchingRuleName() {
        return "booleanMatch";
    }
    
    @Override
    public String getEqualityMatchingRuleOID() {
        return "2.5.13.13";
    }
    
    @Override
    public String getOrderingMatchingRuleName() {
        return null;
    }
    
    @Override
    public String getOrderingMatchingRuleOID() {
        return null;
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
    public boolean matchesSubstring(final ASN1OctetString value, final ASN1OctetString subInitial, final ASN1OctetString[] subAny, final ASN1OctetString subFinal) throws LDAPException {
        throw new LDAPException(ResultCode.INAPPROPRIATE_MATCHING, MatchingRuleMessages.ERR_BOOLEAN_SUBSTRING_MATCHING_NOT_SUPPORTED.get());
    }
    
    @Override
    public int compareValues(final ASN1OctetString value1, final ASN1OctetString value2) throws LDAPException {
        throw new LDAPException(ResultCode.INAPPROPRIATE_MATCHING, MatchingRuleMessages.ERR_BOOLEAN_ORDERING_MATCHING_NOT_SUPPORTED.get());
    }
    
    @Override
    public ASN1OctetString normalize(final ASN1OctetString value) throws LDAPException {
        final byte[] valueBytes = value.getValue();
        if (valueBytes.length == 4 && (valueBytes[0] == 84 || valueBytes[0] == 116) && (valueBytes[1] == 82 || valueBytes[1] == 114) && (valueBytes[2] == 85 || valueBytes[2] == 117) && (valueBytes[3] == 69 || valueBytes[3] == 101)) {
            return BooleanMatchingRule.TRUE_VALUE;
        }
        if (valueBytes.length == 5 && (valueBytes[0] == 70 || valueBytes[0] == 102) && (valueBytes[1] == 65 || valueBytes[1] == 97) && (valueBytes[2] == 76 || valueBytes[2] == 108) && (valueBytes[3] == 83 || valueBytes[3] == 115) && (valueBytes[4] == 69 || valueBytes[4] == 101)) {
            return BooleanMatchingRule.FALSE_VALUE;
        }
        throw new LDAPException(ResultCode.INVALID_ATTRIBUTE_SYNTAX, MatchingRuleMessages.ERR_BOOLEAN_INVALID_VALUE.get());
    }
    
    @Override
    public ASN1OctetString normalizeSubstring(final ASN1OctetString value, final byte substringType) throws LDAPException {
        throw new LDAPException(ResultCode.INAPPROPRIATE_MATCHING, MatchingRuleMessages.ERR_BOOLEAN_SUBSTRING_MATCHING_NOT_SUPPORTED.get());
    }
    
    static {
        INSTANCE = new BooleanMatchingRule();
        TRUE_VALUE = new ASN1OctetString("TRUE");
        FALSE_VALUE = new ASN1OctetString("FALSE");
        LOWER_EQUALITY_RULE_NAME = StaticUtils.toLowerCase("booleanMatch");
    }
}
