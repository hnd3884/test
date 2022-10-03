package org.xbill.DNS.security;

import java.math.BigInteger;
import java.security.interfaces.RSAPublicKey;

class RSAPubKey implements RSAPublicKey
{
    private BigInteger Modulus;
    private BigInteger Exponent;
    
    public RSAPubKey(final BigInteger modulus, final BigInteger exponent) {
        this.Modulus = modulus;
        this.Exponent = exponent;
    }
    
    public BigInteger getModulus() {
        return this.Modulus;
    }
    
    public BigInteger getPublicExponent() {
        return this.Exponent;
    }
    
    public String getAlgorithm() {
        return "RSA";
    }
    
    public String getFormat() {
        return null;
    }
    
    public byte[] getEncoded() {
        return null;
    }
}
