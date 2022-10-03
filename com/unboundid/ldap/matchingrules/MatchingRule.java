package com.unboundid.ldap.matchingrules;

import com.unboundid.ldap.sdk.unboundidds.jsonfilter.JSONObjectExactMatchingRule;
import com.unboundid.util.StaticUtils;
import com.unboundid.ldap.sdk.schema.AttributeTypeDefinition;
import com.unboundid.ldap.sdk.schema.Schema;
import com.unboundid.util.Debug;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.Extensible;
import java.io.Serializable;

@Extensible
@ThreadSafety(level = ThreadSafetyLevel.INTERFACE_THREADSAFE)
public abstract class MatchingRule implements Serializable
{
    public static final byte SUBSTRING_TYPE_SUBINITIAL = Byte.MIN_VALUE;
    public static final byte SUBSTRING_TYPE_SUBANY = -127;
    public static final byte SUBSTRING_TYPE_SUBFINAL = -126;
    private static final long serialVersionUID = 6050276733546358513L;
    
    protected MatchingRule() {
    }
    
    public abstract String getEqualityMatchingRuleName();
    
    public abstract String getEqualityMatchingRuleOID();
    
    public String getEqualityMatchingRuleNameOrOID() {
        final String name = this.getEqualityMatchingRuleName();
        if (name == null) {
            return this.getEqualityMatchingRuleOID();
        }
        return name;
    }
    
    public abstract String getOrderingMatchingRuleName();
    
    public abstract String getOrderingMatchingRuleOID();
    
    public String getOrderingMatchingRuleNameOrOID() {
        final String name = this.getOrderingMatchingRuleName();
        if (name == null) {
            return this.getOrderingMatchingRuleOID();
        }
        return name;
    }
    
    public abstract String getSubstringMatchingRuleName();
    
    public abstract String getSubstringMatchingRuleOID();
    
    public String getSubstringMatchingRuleNameOrOID() {
        final String name = this.getSubstringMatchingRuleName();
        if (name == null) {
            return this.getSubstringMatchingRuleOID();
        }
        return name;
    }
    
    public abstract boolean valuesMatch(final ASN1OctetString p0, final ASN1OctetString p1) throws LDAPException;
    
    public boolean matchesAnyValue(final ASN1OctetString assertionValue, final ASN1OctetString[] attributeValues) throws LDAPException {
        if (assertionValue == null || attributeValues == null || attributeValues.length == 0) {
            return false;
        }
        boolean exceptionOnEveryAttempt = true;
        LDAPException firstException = null;
        for (final ASN1OctetString attributeValue : attributeValues) {
            try {
                if (this.valuesMatch(assertionValue, attributeValue)) {
                    return true;
                }
                exceptionOnEveryAttempt = false;
            }
            catch (final LDAPException le) {
                Debug.debugException(le);
                if (firstException == null) {
                    firstException = le;
                }
            }
        }
        if (exceptionOnEveryAttempt) {
            throw firstException;
        }
        return false;
    }
    
    public abstract boolean matchesSubstring(final ASN1OctetString p0, final ASN1OctetString p1, final ASN1OctetString[] p2, final ASN1OctetString p3) throws LDAPException;
    
    public abstract int compareValues(final ASN1OctetString p0, final ASN1OctetString p1) throws LDAPException;
    
    public abstract ASN1OctetString normalize(final ASN1OctetString p0) throws LDAPException;
    
    public abstract ASN1OctetString normalizeSubstring(final ASN1OctetString p0, final byte p1) throws LDAPException;
    
    public static MatchingRule selectEqualityMatchingRule(final String attrName, final Schema schema) {
        return selectEqualityMatchingRule(attrName, null, schema);
    }
    
    public static MatchingRule selectEqualityMatchingRule(final String attrName, final String ruleID, final Schema schema) {
        if (ruleID != null) {
            return selectEqualityMatchingRule(ruleID);
        }
        if (attrName == null || schema == null) {
            return getDefaultEqualityMatchingRule();
        }
        final AttributeTypeDefinition attrType = schema.getAttributeType(attrName);
        if (attrType == null) {
            return getDefaultEqualityMatchingRule();
        }
        final String mrName = attrType.getEqualityMatchingRule(schema);
        if (mrName != null) {
            return selectEqualityMatchingRule(mrName);
        }
        final String syntaxOID = attrType.getBaseSyntaxOID(schema);
        if (syntaxOID != null) {
            return selectMatchingRuleForSyntax(syntaxOID);
        }
        return getDefaultEqualityMatchingRule();
    }
    
    public static MatchingRule selectEqualityMatchingRule(final String ruleID) {
        if (ruleID == null || ruleID.isEmpty()) {
            return getDefaultEqualityMatchingRule();
        }
        final String lowerName = StaticUtils.toLowerCase(ruleID);
        if (lowerName.equals(BooleanMatchingRule.LOWER_EQUALITY_RULE_NAME) || lowerName.equals("2.5.13.13")) {
            return BooleanMatchingRule.getInstance();
        }
        if (lowerName.equals(CaseExactStringMatchingRule.LOWER_EQUALITY_RULE_NAME) || lowerName.equals("2.5.13.5") || lowerName.equals("caseexactia5match") || lowerName.equals("1.3.6.1.4.1.1466.109.114.1")) {
            return CaseExactStringMatchingRule.getInstance();
        }
        if (lowerName.equals(CaseIgnoreListMatchingRule.LOWER_EQUALITY_RULE_NAME) || lowerName.equals("2.5.13.11")) {
            return CaseIgnoreListMatchingRule.getInstance();
        }
        if (lowerName.equals(CaseIgnoreStringMatchingRule.LOWER_EQUALITY_RULE_NAME) || lowerName.equals("2.5.13.2") || lowerName.equals("caseignoreia5match") || lowerName.equals("1.3.6.1.4.1.1466.109.114.2")) {
            return CaseIgnoreStringMatchingRule.getInstance();
        }
        if (lowerName.equals(DistinguishedNameMatchingRule.LOWER_EQUALITY_RULE_NAME) || lowerName.equals("2.5.13.1") || lowerName.equals("uniquemembermatch") || lowerName.equals("2.5.13.23")) {
            return DistinguishedNameMatchingRule.getInstance();
        }
        if (lowerName.equals(GeneralizedTimeMatchingRule.LOWER_EQUALITY_RULE_NAME) || lowerName.equals("2.5.13.27")) {
            return GeneralizedTimeMatchingRule.getInstance();
        }
        if (lowerName.equals(IntegerMatchingRule.LOWER_EQUALITY_RULE_NAME) || lowerName.equals("2.5.13.14")) {
            return IntegerMatchingRule.getInstance();
        }
        if (lowerName.equals(NumericStringMatchingRule.LOWER_EQUALITY_RULE_NAME) || lowerName.equals("2.5.13.8")) {
            return NumericStringMatchingRule.getInstance();
        }
        if (lowerName.equals(OctetStringMatchingRule.LOWER_EQUALITY_RULE_NAME) || lowerName.equals("2.5.13.17")) {
            return OctetStringMatchingRule.getInstance();
        }
        if (lowerName.equals(TelephoneNumberMatchingRule.LOWER_EQUALITY_RULE_NAME) || lowerName.equals("2.5.13.20")) {
            return TelephoneNumberMatchingRule.getInstance();
        }
        if (lowerName.equals("jsonobjectexactmatch") || lowerName.equals("1.3.6.1.4.1.30221.2.4.12")) {
            return JSONObjectExactMatchingRule.getInstance();
        }
        return getDefaultEqualityMatchingRule();
    }
    
    public static MatchingRule getDefaultEqualityMatchingRule() {
        return CaseIgnoreStringMatchingRule.getInstance();
    }
    
    public static MatchingRule selectOrderingMatchingRule(final String attrName, final Schema schema) {
        return selectOrderingMatchingRule(attrName, null, schema);
    }
    
    public static MatchingRule selectOrderingMatchingRule(final String attrName, final String ruleID, final Schema schema) {
        if (ruleID != null) {
            return selectOrderingMatchingRule(ruleID);
        }
        if (attrName == null || schema == null) {
            return getDefaultOrderingMatchingRule();
        }
        final AttributeTypeDefinition attrType = schema.getAttributeType(attrName);
        if (attrType == null) {
            return getDefaultOrderingMatchingRule();
        }
        final String mrName = attrType.getOrderingMatchingRule(schema);
        if (mrName != null) {
            return selectOrderingMatchingRule(mrName);
        }
        final String syntaxOID = attrType.getBaseSyntaxOID(schema);
        if (syntaxOID != null) {
            return selectMatchingRuleForSyntax(syntaxOID);
        }
        return getDefaultOrderingMatchingRule();
    }
    
    public static MatchingRule selectOrderingMatchingRule(final String ruleID) {
        if (ruleID == null || ruleID.isEmpty()) {
            return getDefaultOrderingMatchingRule();
        }
        final String lowerName = StaticUtils.toLowerCase(ruleID);
        if (lowerName.equals(CaseExactStringMatchingRule.LOWER_ORDERING_RULE_NAME) || lowerName.equals("2.5.13.6")) {
            return CaseExactStringMatchingRule.getInstance();
        }
        if (lowerName.equals(CaseIgnoreStringMatchingRule.LOWER_ORDERING_RULE_NAME) || lowerName.equals("2.5.13.3")) {
            return CaseIgnoreStringMatchingRule.getInstance();
        }
        if (lowerName.equals(GeneralizedTimeMatchingRule.LOWER_ORDERING_RULE_NAME) || lowerName.equals("2.5.13.28")) {
            return GeneralizedTimeMatchingRule.getInstance();
        }
        if (lowerName.equals(IntegerMatchingRule.LOWER_ORDERING_RULE_NAME) || lowerName.equals("2.5.13.15")) {
            return IntegerMatchingRule.getInstance();
        }
        if (lowerName.equals(NumericStringMatchingRule.LOWER_ORDERING_RULE_NAME) || lowerName.equals("2.5.13.9")) {
            return NumericStringMatchingRule.getInstance();
        }
        if (lowerName.equals(OctetStringMatchingRule.LOWER_ORDERING_RULE_NAME) || lowerName.equals("2.5.13.18")) {
            return OctetStringMatchingRule.getInstance();
        }
        return getDefaultOrderingMatchingRule();
    }
    
    public static MatchingRule getDefaultOrderingMatchingRule() {
        return CaseIgnoreStringMatchingRule.getInstance();
    }
    
    public static MatchingRule selectSubstringMatchingRule(final String attrName, final Schema schema) {
        return selectSubstringMatchingRule(attrName, null, schema);
    }
    
    public static MatchingRule selectSubstringMatchingRule(final String attrName, final String ruleID, final Schema schema) {
        if (ruleID != null) {
            return selectSubstringMatchingRule(ruleID);
        }
        if (attrName == null || schema == null) {
            return getDefaultSubstringMatchingRule();
        }
        final AttributeTypeDefinition attrType = schema.getAttributeType(attrName);
        if (attrType == null) {
            return getDefaultSubstringMatchingRule();
        }
        final String mrName = attrType.getSubstringMatchingRule(schema);
        if (mrName != null) {
            return selectSubstringMatchingRule(mrName);
        }
        final String syntaxOID = attrType.getBaseSyntaxOID(schema);
        if (syntaxOID != null) {
            return selectMatchingRuleForSyntax(syntaxOID);
        }
        return getDefaultSubstringMatchingRule();
    }
    
    public static MatchingRule selectSubstringMatchingRule(final String ruleID) {
        if (ruleID == null || ruleID.isEmpty()) {
            return getDefaultSubstringMatchingRule();
        }
        final String lowerName = StaticUtils.toLowerCase(ruleID);
        if (lowerName.equals(CaseExactStringMatchingRule.LOWER_SUBSTRING_RULE_NAME) || lowerName.equals("2.5.13.7") || lowerName.equals("caseexactia5substringsmatch")) {
            return CaseExactStringMatchingRule.getInstance();
        }
        if (lowerName.equals(CaseIgnoreListMatchingRule.LOWER_SUBSTRING_RULE_NAME) || lowerName.equals("2.5.13.12")) {
            return CaseIgnoreListMatchingRule.getInstance();
        }
        if (lowerName.equals(CaseIgnoreStringMatchingRule.LOWER_SUBSTRING_RULE_NAME) || lowerName.equals("2.5.13.4") || lowerName.equals("caseignoreia5substringsmatch") || lowerName.equals("1.3.6.1.4.1.1466.109.114.3")) {
            return CaseIgnoreStringMatchingRule.getInstance();
        }
        if (lowerName.equals(NumericStringMatchingRule.LOWER_SUBSTRING_RULE_NAME) || lowerName.equals("2.5.13.10")) {
            return NumericStringMatchingRule.getInstance();
        }
        if (lowerName.equals(OctetStringMatchingRule.LOWER_SUBSTRING_RULE_NAME) || lowerName.equals("2.5.13.19")) {
            return OctetStringMatchingRule.getInstance();
        }
        if (lowerName.equals(TelephoneNumberMatchingRule.LOWER_SUBSTRING_RULE_NAME) || lowerName.equals("2.5.13.21")) {
            return TelephoneNumberMatchingRule.getInstance();
        }
        return getDefaultSubstringMatchingRule();
    }
    
    public static MatchingRule getDefaultSubstringMatchingRule() {
        return CaseIgnoreStringMatchingRule.getInstance();
    }
    
    public static MatchingRule selectMatchingRuleForSyntax(final String syntaxOID) {
        if (syntaxOID.equals("1.3.6.1.4.1.1466.115.121.1.7")) {
            return BooleanMatchingRule.getInstance();
        }
        if (syntaxOID.equals("1.3.6.1.4.1.1466.115.121.1.41")) {
            return CaseIgnoreListMatchingRule.getInstance();
        }
        if (syntaxOID.equals("1.3.6.1.4.1.1466.115.121.1.12") || syntaxOID.equals("1.3.6.1.4.1.1466.115.121.1.34")) {
            return DistinguishedNameMatchingRule.getInstance();
        }
        if (syntaxOID.equals("1.3.6.1.4.1.1466.115.121.1.24") || syntaxOID.equals("1.3.6.1.4.1.1466.115.121.1.53")) {
            return GeneralizedTimeMatchingRule.getInstance();
        }
        if (syntaxOID.equals("1.3.6.1.4.1.1466.115.121.1.27")) {
            return IntegerMatchingRule.getInstance();
        }
        if (syntaxOID.equals("1.3.6.1.4.1.1466.115.121.1.36")) {
            return NumericStringMatchingRule.getInstance();
        }
        if (syntaxOID.equals("1.3.6.1.4.1.4203.1.1.2") || syntaxOID.equals("1.3.6.1.4.1.1466.115.121.1.5") || syntaxOID.equals("1.3.6.1.4.1.1466.115.121.1.8") || syntaxOID.equals("1.3.6.1.4.1.1466.115.121.1.9") || syntaxOID.equals("1.3.6.1.4.1.1466.115.121.1.10") || syntaxOID.equals("1.3.6.1.4.1.1466.115.121.1.28") || syntaxOID.equals("1.3.6.1.4.1.1466.115.121.1.40")) {
            return OctetStringMatchingRule.getInstance();
        }
        if (syntaxOID.equals("1.3.6.1.4.1.1466.115.121.1.50")) {
            return TelephoneNumberMatchingRule.getInstance();
        }
        if (syntaxOID.equals("1.3.6.1.4.1.30221.2.3.4")) {
            return JSONObjectExactMatchingRule.getInstance();
        }
        return CaseIgnoreStringMatchingRule.getInstance();
    }
}
