package org.bouncycastle.jce.interfaces;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;

public interface PKCS12BagAttributeCarrier
{
    void setBagAttribute(final ASN1ObjectIdentifier p0, final ASN1Encodable p1);
    
    ASN1Encodable getBagAttribute(final ASN1ObjectIdentifier p0);
    
    Enumeration getBagAttributeKeys();
}
