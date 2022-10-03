package org.bouncycastle.jce.spec;

import java.math.BigInteger;
import java.security.spec.KeySpec;

public class GOST3410PublicKeySpec implements KeySpec
{
    private BigInteger y;
    private BigInteger p;
    private BigInteger q;
    private BigInteger a;
    
    public GOST3410PublicKeySpec(final BigInteger y, final BigInteger p4, final BigInteger q, final BigInteger a) {
        this.y = y;
        this.p = p4;
        this.q = q;
        this.a = a;
    }
    
    public BigInteger getY() {
        return this.y;
    }
    
    public BigInteger getP() {
        return this.p;
    }
    
    public BigInteger getQ() {
        return this.q;
    }
    
    public BigInteger getA() {
        return this.a;
    }
}
