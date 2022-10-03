package org.bouncycastle.jce.spec;

import java.math.BigInteger;

public class ECPrivateKeySpec extends ECKeySpec
{
    private BigInteger d;
    
    public ECPrivateKeySpec(final BigInteger d, final ECParameterSpec ecParameterSpec) {
        super(ecParameterSpec);
        this.d = d;
    }
    
    public BigInteger getD() {
        return this.d;
    }
}
