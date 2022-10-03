package com.unboundid.ldap.matchingrules;

import com.unboundid.util.StaticUtils;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;

@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class OctetStringMatchingRule extends AcceptAllSimpleMatchingRule
{
    private static final OctetStringMatchingRule INSTANCE;
    public static final String EQUALITY_RULE_NAME = "octetStringMatch";
    static final String LOWER_EQUALITY_RULE_NAME;
    public static final String EQUALITY_RULE_OID = "2.5.13.17";
    public static final String ORDERING_RULE_NAME = "octetStringOrderingMatch";
    static final String LOWER_ORDERING_RULE_NAME;
    public static final String ORDERING_RULE_OID = "2.5.13.18";
    public static final String SUBSTRING_RULE_NAME = "octetStringSubstringsMatch";
    static final String LOWER_SUBSTRING_RULE_NAME;
    public static final String SUBSTRING_RULE_OID = "2.5.13.19";
    private static final long serialVersionUID = -5655018388491186342L;
    
    public static OctetStringMatchingRule getInstance() {
        return OctetStringMatchingRule.INSTANCE;
    }
    
    @Override
    public String getEqualityMatchingRuleName() {
        return "octetStringMatch";
    }
    
    @Override
    public String getEqualityMatchingRuleOID() {
        return "2.5.13.17";
    }
    
    @Override
    public String getOrderingMatchingRuleName() {
        return "octetStringOrderingMatch";
    }
    
    @Override
    public String getOrderingMatchingRuleOID() {
        return "2.5.13.18";
    }
    
    @Override
    public String getSubstringMatchingRuleName() {
        return "octetStringSubstringsMatch";
    }
    
    @Override
    public String getSubstringMatchingRuleOID() {
        return "2.5.13.19";
    }
    
    @Override
    public ASN1OctetString normalize(final ASN1OctetString value) {
        return value;
    }
    
    @Override
    public ASN1OctetString normalizeSubstring(final ASN1OctetString value, final byte substringType) {
        return value;
    }
    
    static {
        INSTANCE = new OctetStringMatchingRule();
        LOWER_EQUALITY_RULE_NAME = StaticUtils.toLowerCase("octetStringMatch");
        LOWER_ORDERING_RULE_NAME = StaticUtils.toLowerCase("octetStringOrderingMatch");
        LOWER_SUBSTRING_RULE_NAME = StaticUtils.toLowerCase("octetStringSubstringsMatch");
    }
}
