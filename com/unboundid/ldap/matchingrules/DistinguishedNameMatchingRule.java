package com.unboundid.ldap.matchingrules;

import com.unboundid.util.StaticUtils;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.util.Debug;
import com.unboundid.ldap.sdk.DN;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;

@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class DistinguishedNameMatchingRule extends MatchingRule
{
    private static final DistinguishedNameMatchingRule INSTANCE;
    public static final String EQUALITY_RULE_NAME = "distinguishedNameMatch";
    static final String LOWER_EQUALITY_RULE_NAME;
    public static final String EQUALITY_RULE_OID = "2.5.13.1";
    private static final long serialVersionUID = -2617356571703597868L;
    
    public static DistinguishedNameMatchingRule getInstance() {
        return DistinguishedNameMatchingRule.INSTANCE;
    }
    
    @Override
    public String getEqualityMatchingRuleName() {
        return "distinguishedNameMatch";
    }
    
    @Override
    public String getEqualityMatchingRuleOID() {
        return "2.5.13.1";
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
        DN dn1;
        try {
            dn1 = new DN(value1.stringValue());
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            throw new LDAPException(ResultCode.INVALID_ATTRIBUTE_SYNTAX, le.getMessage(), le);
        }
        DN dn2;
        try {
            dn2 = new DN(value2.stringValue());
        }
        catch (final LDAPException le2) {
            Debug.debugException(le2);
            throw new LDAPException(ResultCode.INVALID_ATTRIBUTE_SYNTAX, le2.getMessage(), le2);
        }
        return dn1.equals(dn2);
    }
    
    @Override
    public boolean matchesAnyValue(final ASN1OctetString assertionValue, final ASN1OctetString[] attributeValues) throws LDAPException {
        if (assertionValue == null || attributeValues == null || attributeValues.length == 0) {
            return false;
        }
        DN assertionValueDN;
        try {
            assertionValueDN = new DN(assertionValue.stringValue());
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            throw new LDAPException(ResultCode.INVALID_ATTRIBUTE_SYNTAX, le.getMessage(), le);
        }
        for (final ASN1OctetString attributeValue : attributeValues) {
            try {
                if (assertionValueDN.equals(new DN(attributeValue.stringValue()))) {
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
        throw new LDAPException(ResultCode.INAPPROPRIATE_MATCHING, MatchingRuleMessages.ERR_DN_SUBSTRING_MATCHING_NOT_SUPPORTED.get());
    }
    
    @Override
    public int compareValues(final ASN1OctetString value1, final ASN1OctetString value2) throws LDAPException {
        throw new LDAPException(ResultCode.INAPPROPRIATE_MATCHING, MatchingRuleMessages.ERR_DN_ORDERING_MATCHING_NOT_SUPPORTED.get());
    }
    
    @Override
    public ASN1OctetString normalize(final ASN1OctetString value) throws LDAPException {
        try {
            final DN dn = new DN(value.stringValue());
            return new ASN1OctetString(dn.toNormalizedString());
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            throw new LDAPException(ResultCode.INVALID_ATTRIBUTE_SYNTAX, le.getMessage(), le);
        }
    }
    
    @Override
    public ASN1OctetString normalizeSubstring(final ASN1OctetString value, final byte substringType) throws LDAPException {
        throw new LDAPException(ResultCode.INAPPROPRIATE_MATCHING, MatchingRuleMessages.ERR_DN_SUBSTRING_MATCHING_NOT_SUPPORTED.get());
    }
    
    static {
        INSTANCE = new DistinguishedNameMatchingRule();
        LOWER_EQUALITY_RULE_NAME = StaticUtils.toLowerCase("distinguishedNameMatch");
    }
}
