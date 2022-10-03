package com.unboundid.ldap.matchingrules;

import com.unboundid.util.Debug;
import com.unboundid.asn1.ASN1Element;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.Extensible;

@Extensible
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public abstract class SimpleMatchingRule extends MatchingRule
{
    private static final long serialVersionUID = -7221506185552250694L;
    
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
        final byte[] normValue = this.normalize(value).getValue();
        int pos = 0;
        if (subInitial != null) {
            final byte[] normSubInitial = this.normalizeSubstring(subInitial, (byte)(-128)).getValue();
            if (normValue.length < normSubInitial.length) {
                return false;
            }
            for (int i = 0; i < normSubInitial.length; ++i) {
                if (normValue[i] != normSubInitial[i]) {
                    return false;
                }
            }
            pos = normSubInitial.length;
        }
        if (subAny != null) {
            final byte[][] normSubAny = new byte[subAny.length][];
            for (int i = 0; i < subAny.length; ++i) {
                normSubAny[i] = this.normalizeSubstring(subAny[i], (byte)(-127)).getValue();
            }
            for (final byte[] b : normSubAny) {
                if (b.length != 0) {
                    boolean match = false;
                    for (int subEndLength = normValue.length - b.length; pos <= subEndLength; ++pos) {
                        match = true;
                        for (int j = 0; j < b.length; ++j) {
                            if (normValue[pos + j] != b[j]) {
                                match = false;
                                break;
                            }
                        }
                        if (match) {
                            pos += b.length;
                            break;
                        }
                    }
                    if (!match) {
                        return false;
                    }
                }
            }
        }
        if (subFinal != null) {
            final byte[] normSubFinal = this.normalizeSubstring(subFinal, (byte)(-126)).getValue();
            int finalStartPos = normValue.length - normSubFinal.length;
            if (finalStartPos < pos) {
                return false;
            }
            for (int k = 0; k < normSubFinal.length; ++k, ++finalStartPos) {
                if (normValue[finalStartPos] != normSubFinal[k]) {
                    return false;
                }
            }
        }
        return true;
    }
    
    @Override
    public int compareValues(final ASN1OctetString value1, final ASN1OctetString value2) throws LDAPException {
        final byte[] normValue1 = this.normalize(value1).getValue();
        final byte[] normValue2 = this.normalize(value2).getValue();
        for (int minLength = Math.min(normValue1.length, normValue2.length), i = 0; i < minLength; ++i) {
            final int b1 = normValue1[i] & 0xFF;
            final int b2 = normValue2[i] & 0xFF;
            if (b1 < b2) {
                return -1;
            }
            if (b1 > b2) {
                return 1;
            }
        }
        return normValue1.length - normValue2.length;
    }
}
