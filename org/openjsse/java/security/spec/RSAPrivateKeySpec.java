package org.openjsse.java.security.spec;

import java.security.spec.AlgorithmParameterSpec;
import java.math.BigInteger;

public class RSAPrivateKeySpec extends java.security.spec.RSAPrivateKeySpec
{
    public RSAPrivateKeySpec(final BigInteger modulus, final BigInteger privateExponent) {
        super(modulus, privateExponent);
    }
    
    public RSAPrivateKeySpec(final BigInteger modulus, final BigInteger privateExponent, final AlgorithmParameterSpec params) {
        super(modulus, privateExponent, params);
    }
}
