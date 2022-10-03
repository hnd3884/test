package org.openjsse.java.security.spec;

import java.security.spec.AlgorithmParameterSpec;
import java.math.BigInteger;

public class RSAPrivateCrtKeySpec extends java.security.spec.RSAPrivateCrtKeySpec
{
    public RSAPrivateCrtKeySpec(final BigInteger modulus, final BigInteger publicExponent, final BigInteger privateExponent, final BigInteger primeP, final BigInteger primeQ, final BigInteger primeExponentP, final BigInteger primeExponentQ, final BigInteger crtCoefficient) {
        super(modulus, publicExponent, privateExponent, primeP, primeQ, primeExponentP, primeExponentQ, crtCoefficient);
    }
    
    public RSAPrivateCrtKeySpec(final BigInteger modulus, final BigInteger publicExponent, final BigInteger privateExponent, final BigInteger primeP, final BigInteger primeQ, final BigInteger primeExponentP, final BigInteger primeExponentQ, final BigInteger crtCoefficient, final AlgorithmParameterSpec keyParams) {
        super(modulus, publicExponent, privateExponent, primeP, primeQ, primeExponentP, primeExponentQ, crtCoefficient, keyParams);
    }
}
