package org.bouncycastle.asn1;

public class DERObjectIdentifier extends ASN1ObjectIdentifier
{
    public DERObjectIdentifier(final String s) {
        super(s);
    }
    
    DERObjectIdentifier(final byte[] array) {
        super(array);
    }
    
    DERObjectIdentifier(final ASN1ObjectIdentifier asn1ObjectIdentifier, final String s) {
        super(asn1ObjectIdentifier, s);
    }
}
