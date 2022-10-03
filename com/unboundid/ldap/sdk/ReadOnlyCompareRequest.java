package com.unboundid.ldap.sdk;

import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotExtensible;

@NotExtensible
@ThreadSafety(level = ThreadSafetyLevel.INTERFACE_NOT_THREADSAFE)
public interface ReadOnlyCompareRequest extends ReadOnlyLDAPRequest
{
    String getDN();
    
    String getAttributeName();
    
    String getAssertionValue();
    
    byte[] getAssertionValueBytes();
    
    ASN1OctetString getRawAssertionValue();
    
    CompareRequest duplicate();
    
    CompareRequest duplicate(final Control[] p0);
}
