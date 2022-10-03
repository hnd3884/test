package org.bouncycastle.asn1;

import java.math.BigInteger;

public class DEREnumerated extends ASN1Enumerated
{
    @Deprecated
    DEREnumerated(final byte[] array) {
        super(array);
    }
    
    @Deprecated
    public DEREnumerated(final BigInteger bigInteger) {
        super(bigInteger);
    }
    
    @Deprecated
    public DEREnumerated(final int n) {
        super(n);
    }
}
