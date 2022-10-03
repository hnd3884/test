package com.maverick.crypto.publickey;

import java.math.BigInteger;

public class DsaKey
{
    protected BigInteger p;
    protected BigInteger q;
    protected BigInteger g;
    
    public DsaKey() {
    }
    
    public DsaKey(final BigInteger p3, final BigInteger q, final BigInteger g) {
        this.p = p3;
        this.q = q;
        this.g = g;
    }
    
    public BigInteger getP() {
        return this.p;
    }
    
    public BigInteger getQ() {
        return this.q;
    }
    
    public BigInteger getG() {
        return this.g;
    }
}
