package org.bouncycastle.asn1.x500;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;

public interface X500NameStyle
{
    ASN1Encodable stringToValue(final ASN1ObjectIdentifier p0, final String p1);
    
    ASN1ObjectIdentifier attrNameToOID(final String p0);
    
    RDN[] fromString(final String p0);
    
    boolean areEqual(final X500Name p0, final X500Name p1);
    
    int calculateHashCode(final X500Name p0);
    
    String toString(final X500Name p0);
    
    String oidToDisplayName(final ASN1ObjectIdentifier p0);
    
    String[] oidToAttrNames(final ASN1ObjectIdentifier p0);
}
