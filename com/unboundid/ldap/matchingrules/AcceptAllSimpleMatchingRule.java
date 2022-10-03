package com.unboundid.ldap.matchingrules;

import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.util.Debug;
import com.unboundid.asn1.ASN1Element;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.Extensible;

@Extensible
@ThreadSafety(level = ThreadSafetyLevel.INTERFACE_THREADSAFE)
public abstract class AcceptAllSimpleMatchingRule extends SimpleMatchingRule
{
    private static final long serialVersionUID = -7450007924568660003L;
    
    @Override
    public boolean valuesMatch(final ASN1OctetString value1, final ASN1OctetString value2) {
        return this.normalize(value1).equalsIgnoreType(this.normalize(value2));
    }
    
    @Override
    public boolean matchesAnyValue(final ASN1OctetString assertionValue, final ASN1OctetString[] attributeValues) {
        if (assertionValue == null || attributeValues == null || attributeValues.length == 0) {
            return false;
        }
        final ASN1OctetString normalizedAssertionValue = this.normalize(assertionValue);
        for (final ASN1OctetString attributeValue : attributeValues) {
            if (normalizedAssertionValue.equalsIgnoreType(this.normalize(attributeValue))) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public boolean matchesSubstring(final ASN1OctetString value, final ASN1OctetString subInitial, final ASN1OctetString[] subAny, final ASN1OctetString subFinal) {
        try {
            return super.matchesSubstring(value, subInitial, subAny, subFinal);
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            return false;
        }
    }
    
    @Override
    public int compareValues(final ASN1OctetString value1, final ASN1OctetString value2) {
        try {
            return super.compareValues(value1, value2);
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            return 0;
        }
    }
    
    @Override
    public abstract ASN1OctetString normalize(final ASN1OctetString p0);
    
    @Override
    public abstract ASN1OctetString normalizeSubstring(final ASN1OctetString p0, final byte p1);
}
