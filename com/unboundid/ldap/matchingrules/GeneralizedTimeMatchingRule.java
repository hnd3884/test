package com.unboundid.ldap.matchingrules;

import java.util.Date;
import java.text.ParseException;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.util.Debug;
import com.unboundid.util.StaticUtils;
import com.unboundid.asn1.ASN1OctetString;
import java.text.SimpleDateFormat;
import java.util.TimeZone;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;

@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class GeneralizedTimeMatchingRule extends MatchingRule
{
    private static final GeneralizedTimeMatchingRule INSTANCE;
    private static final String GENERALIZED_TIME_DATE_FORMAT = "yyyyMMddHHmmss.SSS'Z'";
    private static final TimeZone UTC_TIME_ZONE;
    public static final String EQUALITY_RULE_NAME = "generalizedTimeMatch";
    static final String LOWER_EQUALITY_RULE_NAME;
    public static final String EQUALITY_RULE_OID = "2.5.13.27";
    public static final String ORDERING_RULE_NAME = "generalizedTimeOrderingMatch";
    static final String LOWER_ORDERING_RULE_NAME;
    public static final String ORDERING_RULE_OID = "2.5.13.28";
    private static final long serialVersionUID = -6317451154598148593L;
    private static final ThreadLocal<SimpleDateFormat> dateFormat;
    
    public static GeneralizedTimeMatchingRule getInstance() {
        return GeneralizedTimeMatchingRule.INSTANCE;
    }
    
    @Override
    public String getEqualityMatchingRuleName() {
        return "generalizedTimeMatch";
    }
    
    @Override
    public String getEqualityMatchingRuleOID() {
        return "2.5.13.27";
    }
    
    @Override
    public String getOrderingMatchingRuleName() {
        return "generalizedTimeOrderingMatch";
    }
    
    @Override
    public String getOrderingMatchingRuleOID() {
        return "2.5.13.28";
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
        Date d1;
        try {
            d1 = StaticUtils.decodeGeneralizedTime(value1.stringValue());
        }
        catch (final ParseException pe) {
            Debug.debugException(pe);
            throw new LDAPException(ResultCode.INVALID_ATTRIBUTE_SYNTAX, MatchingRuleMessages.ERR_GENERALIZED_TIME_INVALID_VALUE.get(pe.getMessage()), pe);
        }
        Date d2;
        try {
            d2 = StaticUtils.decodeGeneralizedTime(value2.stringValue());
        }
        catch (final ParseException pe2) {
            Debug.debugException(pe2);
            throw new LDAPException(ResultCode.INVALID_ATTRIBUTE_SYNTAX, MatchingRuleMessages.ERR_GENERALIZED_TIME_INVALID_VALUE.get(pe2.getMessage()), pe2);
        }
        return d1.equals(d2);
    }
    
    @Override
    public boolean matchesAnyValue(final ASN1OctetString assertionValue, final ASN1OctetString[] attributeValues) throws LDAPException {
        if (assertionValue == null || attributeValues == null || attributeValues.length == 0) {
            return false;
        }
        Date assertionValueDate;
        try {
            assertionValueDate = StaticUtils.decodeGeneralizedTime(assertionValue.stringValue());
        }
        catch (final ParseException pe) {
            Debug.debugException(pe);
            throw new LDAPException(ResultCode.INVALID_ATTRIBUTE_SYNTAX, MatchingRuleMessages.ERR_GENERALIZED_TIME_INVALID_VALUE.get(pe.getMessage()), pe);
        }
        for (final ASN1OctetString attributeValue : attributeValues) {
            try {
                if (assertionValueDate.equals(StaticUtils.decodeGeneralizedTime(attributeValue.stringValue()))) {
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
        throw new LDAPException(ResultCode.INAPPROPRIATE_MATCHING, MatchingRuleMessages.ERR_GENERALIZED_TIME_SUBSTRING_MATCHING_NOT_SUPPORTED.get());
    }
    
    @Override
    public int compareValues(final ASN1OctetString value1, final ASN1OctetString value2) throws LDAPException {
        Date d1;
        try {
            d1 = StaticUtils.decodeGeneralizedTime(value1.stringValue());
        }
        catch (final ParseException pe) {
            Debug.debugException(pe);
            throw new LDAPException(ResultCode.INVALID_ATTRIBUTE_SYNTAX, MatchingRuleMessages.ERR_GENERALIZED_TIME_INVALID_VALUE.get(pe.getMessage()), pe);
        }
        Date d2;
        try {
            d2 = StaticUtils.decodeGeneralizedTime(value2.stringValue());
        }
        catch (final ParseException pe2) {
            Debug.debugException(pe2);
            throw new LDAPException(ResultCode.INVALID_ATTRIBUTE_SYNTAX, MatchingRuleMessages.ERR_GENERALIZED_TIME_INVALID_VALUE.get(pe2.getMessage()), pe2);
        }
        return d1.compareTo(d2);
    }
    
    @Override
    public ASN1OctetString normalize(final ASN1OctetString value) throws LDAPException {
        Date d;
        try {
            d = StaticUtils.decodeGeneralizedTime(value.stringValue());
        }
        catch (final ParseException pe) {
            Debug.debugException(pe);
            throw new LDAPException(ResultCode.INVALID_ATTRIBUTE_SYNTAX, MatchingRuleMessages.ERR_GENERALIZED_TIME_INVALID_VALUE.get(pe.getMessage()), pe);
        }
        SimpleDateFormat f = GeneralizedTimeMatchingRule.dateFormat.get();
        if (f == null) {
            f = new SimpleDateFormat("yyyyMMddHHmmss.SSS'Z'");
            f.setTimeZone(GeneralizedTimeMatchingRule.UTC_TIME_ZONE);
            GeneralizedTimeMatchingRule.dateFormat.set(f);
        }
        return new ASN1OctetString(f.format(d));
    }
    
    @Override
    public ASN1OctetString normalizeSubstring(final ASN1OctetString value, final byte substringType) throws LDAPException {
        throw new LDAPException(ResultCode.INAPPROPRIATE_MATCHING, MatchingRuleMessages.ERR_GENERALIZED_TIME_SUBSTRING_MATCHING_NOT_SUPPORTED.get());
    }
    
    static {
        INSTANCE = new GeneralizedTimeMatchingRule();
        UTC_TIME_ZONE = TimeZone.getTimeZone("UTC");
        LOWER_EQUALITY_RULE_NAME = StaticUtils.toLowerCase("generalizedTimeMatch");
        LOWER_ORDERING_RULE_NAME = StaticUtils.toLowerCase("generalizedTimeOrderingMatch");
        dateFormat = new ThreadLocal<SimpleDateFormat>();
    }
}
