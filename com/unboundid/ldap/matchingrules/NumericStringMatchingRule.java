package com.unboundid.ldap.matchingrules;

import com.unboundid.util.StaticUtils;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;

@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class NumericStringMatchingRule extends SimpleMatchingRule
{
    private static final NumericStringMatchingRule INSTANCE;
    public static final String EQUALITY_RULE_NAME = "numericStringMatch";
    static final String LOWER_EQUALITY_RULE_NAME;
    public static final String EQUALITY_RULE_OID = "2.5.13.8";
    public static final String ORDERING_RULE_NAME = "numericStringOrderingMatch";
    static final String LOWER_ORDERING_RULE_NAME;
    public static final String ORDERING_RULE_OID = "2.5.13.9";
    public static final String SUBSTRING_RULE_NAME = "numericStringSubstringsMatch";
    static final String LOWER_SUBSTRING_RULE_NAME;
    public static final String SUBSTRING_RULE_OID = "2.5.13.10";
    private static final long serialVersionUID = -898484312052746321L;
    
    public static NumericStringMatchingRule getInstance() {
        return NumericStringMatchingRule.INSTANCE;
    }
    
    @Override
    public String getEqualityMatchingRuleName() {
        return "numericStringMatch";
    }
    
    @Override
    public String getEqualityMatchingRuleOID() {
        return "2.5.13.8";
    }
    
    @Override
    public String getOrderingMatchingRuleName() {
        return "numericStringOrderingMatch";
    }
    
    @Override
    public String getOrderingMatchingRuleOID() {
        return "2.5.13.9";
    }
    
    @Override
    public String getSubstringMatchingRuleName() {
        return "numericStringSubstringsMatch";
    }
    
    @Override
    public String getSubstringMatchingRuleOID() {
        return "2.5.13.10";
    }
    
    @Override
    public ASN1OctetString normalize(final ASN1OctetString value) throws LDAPException {
        int numSpaces = 0;
        final byte[] valueBytes = value.getValue();
        for (int i = 0; i < valueBytes.length; ++i) {
            if (valueBytes[i] == 32) {
                ++numSpaces;
            }
            else if (valueBytes[i] < 48 || valueBytes[i] > 57) {
                throw new LDAPException(ResultCode.INVALID_ATTRIBUTE_SYNTAX, MatchingRuleMessages.ERR_NUMERIC_STRING_INVALID_CHARACTER.get(i));
            }
        }
        if (numSpaces == 0) {
            return value;
        }
        int pos = 0;
        final byte[] returnBytes = new byte[valueBytes.length - numSpaces];
        for (final byte b : valueBytes) {
            if (b != 32) {
                returnBytes[pos++] = b;
            }
        }
        return new ASN1OctetString(returnBytes);
    }
    
    @Override
    public ASN1OctetString normalizeSubstring(final ASN1OctetString value, final byte substringType) throws LDAPException {
        return this.normalize(value);
    }
    
    static {
        INSTANCE = new NumericStringMatchingRule();
        LOWER_EQUALITY_RULE_NAME = StaticUtils.toLowerCase("numericStringMatch");
        LOWER_ORDERING_RULE_NAME = StaticUtils.toLowerCase("numericStringOrderingMatch");
        LOWER_SUBSTRING_RULE_NAME = StaticUtils.toLowerCase("numericStringSubstringsMatch");
    }
}
