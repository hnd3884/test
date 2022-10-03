package org.bouncycastle.crypto.params;

import java.math.BigInteger;

public class ECPrivateKeyParameters extends ECKeyParameters
{
    BigInteger d;
    
    public ECPrivateKeyParameters(final BigInteger d, final ECDomainParameters ecDomainParameters) {
        super(true, ecDomainParameters);
        this.d = d;
    }
    
    public BigInteger getD() {
        return this.d;
    }
}
