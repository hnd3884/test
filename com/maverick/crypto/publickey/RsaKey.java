package com.maverick.crypto.publickey;

import java.math.BigInteger;

public abstract class RsaKey
{
    protected BigInteger modulus;
    
    public RsaKey() {
    }
    
    public RsaKey(final BigInteger modulus) {
        this.modulus = modulus;
    }
    
    public BigInteger getModulus() {
        return this.modulus;
    }
    
    protected void setModulus(final BigInteger modulus) {
        this.modulus = modulus;
    }
    
    public int getBitLength() {
        return this.modulus.bitLength();
    }
}
