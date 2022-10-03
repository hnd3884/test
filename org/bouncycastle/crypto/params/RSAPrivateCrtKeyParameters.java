package org.bouncycastle.crypto.params;

import java.math.BigInteger;

public class RSAPrivateCrtKeyParameters extends RSAKeyParameters
{
    private BigInteger e;
    private BigInteger p;
    private BigInteger q;
    private BigInteger dP;
    private BigInteger dQ;
    private BigInteger qInv;
    
    public RSAPrivateCrtKeyParameters(final BigInteger bigInteger, final BigInteger e, final BigInteger bigInteger2, final BigInteger p8, final BigInteger q, final BigInteger dp, final BigInteger dq, final BigInteger qInv) {
        super(true, bigInteger, bigInteger2);
        this.e = e;
        this.p = p8;
        this.q = q;
        this.dP = dp;
        this.dQ = dq;
        this.qInv = qInv;
    }
    
    public BigInteger getPublicExponent() {
        return this.e;
    }
    
    public BigInteger getP() {
        return this.p;
    }
    
    public BigInteger getQ() {
        return this.q;
    }
    
    public BigInteger getDP() {
        return this.dP;
    }
    
    public BigInteger getDQ() {
        return this.dQ;
    }
    
    public BigInteger getQInv() {
        return this.qInv;
    }
}
