package com.unboundid.ldap.sdk.unboundidds.jsonfilter;

import com.unboundid.util.json.JSONException;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.util.Debug;
import com.unboundid.util.json.JSONObject;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.ldap.matchingrules.MatchingRule;

@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class JSONObjectExactMatchingRule extends MatchingRule
{
    private static final JSONObjectExactMatchingRule INSTANCE;
    private static final long serialVersionUID = -4476702301631553228L;
    
    public static JSONObjectExactMatchingRule getInstance() {
        return JSONObjectExactMatchingRule.INSTANCE;
    }
    
    @Override
    public String getEqualityMatchingRuleName() {
        return "jsonObjectExactMatch";
    }
    
    @Override
    public String getEqualityMatchingRuleOID() {
        return "1.3.6.1.4.1.30221.2.4.12";
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
        JSONObject o1;
        try {
            o1 = new JSONObject(value1.stringValue());
        }
        catch (final JSONException e) {
            Debug.debugException(e);
            throw new LDAPException(ResultCode.INVALID_ATTRIBUTE_SYNTAX, e.getMessage(), e);
        }
        JSONObject o2;
        try {
            o2 = new JSONObject(value2.stringValue());
        }
        catch (final JSONException e2) {
            Debug.debugException(e2);
            throw new LDAPException(ResultCode.INVALID_ATTRIBUTE_SYNTAX, e2.getMessage(), e2);
        }
        return o1.equals(o2, false, true, false);
    }
    
    @Override
    public boolean matchesSubstring(final ASN1OctetString value, final ASN1OctetString subInitial, final ASN1OctetString[] subAny, final ASN1OctetString subFinal) throws LDAPException {
        throw new LDAPException(ResultCode.INAPPROPRIATE_MATCHING, JFMessages.ERR_JSON_MATCHING_RULE_SUBSTRING_NOT_SUPPORTED.get());
    }
    
    @Override
    public int compareValues(final ASN1OctetString value1, final ASN1OctetString value2) throws LDAPException {
        throw new LDAPException(ResultCode.INAPPROPRIATE_MATCHING, JFMessages.ERR_JSON_MATCHING_RULE_ORDERING_NOT_SUPPORTED.get());
    }
    
    @Override
    public ASN1OctetString normalize(final ASN1OctetString value) throws LDAPException {
        JSONObject o;
        try {
            o = new JSONObject(value.stringValue());
        }
        catch (final JSONException e) {
            Debug.debugException(e);
            throw new LDAPException(ResultCode.INVALID_ATTRIBUTE_SYNTAX, e.getMessage(), e);
        }
        return new ASN1OctetString(o.toNormalizedString());
    }
    
    @Override
    public ASN1OctetString normalizeSubstring(final ASN1OctetString value, final byte substringType) throws LDAPException {
        throw new LDAPException(ResultCode.INAPPROPRIATE_MATCHING, JFMessages.ERR_JSON_MATCHING_RULE_SUBSTRING_NOT_SUPPORTED.get());
    }
    
    static {
        INSTANCE = new JSONObjectExactMatchingRule();
    }
}
