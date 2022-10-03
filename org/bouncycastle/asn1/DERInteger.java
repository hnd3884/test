package org.bouncycastle.asn1;

import java.math.BigInteger;

public class DERInteger extends ASN1Integer
{
    public DERInteger(final byte[] array) {
        super(array, true);
    }
    
    public DERInteger(final BigInteger bigInteger) {
        super(bigInteger);
    }
    
    public DERInteger(final long n) {
        super(n);
    }
}
