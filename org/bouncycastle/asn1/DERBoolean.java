package org.bouncycastle.asn1;

public class DERBoolean extends ASN1Boolean
{
    @Deprecated
    public DERBoolean(final boolean b) {
        super(b);
    }
    
    DERBoolean(final byte[] array) {
        super(array);
    }
}
