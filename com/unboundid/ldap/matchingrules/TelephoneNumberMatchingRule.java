package com.unboundid.ldap.matchingrules;

import com.unboundid.util.StaticUtils;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;

@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class TelephoneNumberMatchingRule extends SimpleMatchingRule
{
    private static final TelephoneNumberMatchingRule INSTANCE;
    public static final String EQUALITY_RULE_NAME = "telephoneNumberMatch";
    static final String LOWER_EQUALITY_RULE_NAME;
    public static final String EQUALITY_RULE_OID = "2.5.13.20";
    public static final String SUBSTRING_RULE_NAME = "telephoneNumberSubstringsMatch";
    static final String LOWER_SUBSTRING_RULE_NAME;
    public static final String SUBSTRING_RULE_OID = "2.5.13.21";
    private static final long serialVersionUID = -5463096544849211252L;
    
    public static TelephoneNumberMatchingRule getInstance() {
        return TelephoneNumberMatchingRule.INSTANCE;
    }
    
    @Override
    public String getEqualityMatchingRuleName() {
        return "telephoneNumberMatch";
    }
    
    @Override
    public String getEqualityMatchingRuleOID() {
        return "2.5.13.20";
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
        return "telephoneNumberSubstringsMatch";
    }
    
    @Override
    public String getSubstringMatchingRuleOID() {
        return "2.5.13.21";
    }
    
    @Override
    public int compareValues(final ASN1OctetString value1, final ASN1OctetString value2) throws LDAPException {
        throw new LDAPException(ResultCode.INAPPROPRIATE_MATCHING, MatchingRuleMessages.ERR_TELEPHONE_NUMBER_ORDERING_MATCHING_NOT_SUPPORTED.get());
    }
    
    @Override
    public ASN1OctetString normalize(final ASN1OctetString value) throws LDAPException {
        final byte[] valueBytes = value.getValue();
        final StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < valueBytes.length; ++i) {
            switch (valueBytes[i]) {
                case 32:
                case 45: {
                    break;
                }
                case 39:
                case 40:
                case 41:
                case 43:
                case 44:
                case 46:
                case 47:
                case 58:
                case 61:
                case 63: {
                    buffer.append((char)valueBytes[i]);
                    break;
                }
                default: {
                    final byte b = valueBytes[i];
                    if ((b >= 48 && b <= 57) || (b >= 97 && b <= 122) || (b >= 65 && b <= 90)) {
                        buffer.append((char)valueBytes[i]);
                        break;
                    }
                    throw new LDAPException(ResultCode.INVALID_ATTRIBUTE_SYNTAX, MatchingRuleMessages.ERR_TELEPHONE_NUMBER_INVALID_CHARACTER.get(i));
                }
            }
        }
        return new ASN1OctetString(buffer.toString());
    }
    
    @Override
    public ASN1OctetString normalizeSubstring(final ASN1OctetString value, final byte substringType) throws LDAPException {
        return this.normalize(value);
    }
    
    static {
        INSTANCE = new TelephoneNumberMatchingRule();
        LOWER_EQUALITY_RULE_NAME = StaticUtils.toLowerCase("telephoneNumberMatch");
        LOWER_SUBSTRING_RULE_NAME = StaticUtils.toLowerCase("telephoneNumberSubstringsMatch");
    }
}
