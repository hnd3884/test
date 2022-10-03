package org.openjsse.java.security.spec;

import java.security.spec.AlgorithmParameterSpec;
import java.math.BigInteger;

public class RSAPublicKeySpec extends java.security.spec.RSAPublicKeySpec
{
    public RSAPublicKeySpec(final BigInteger modulus, final BigInteger publicExponent) {
        super(modulus, publicExponent);
    }
    
    public RSAPublicKeySpec(final BigInteger modulus, final BigInteger publicExponent, final AlgorithmParameterSpec params) {
        super(modulus, publicExponent, params);
    }
}
