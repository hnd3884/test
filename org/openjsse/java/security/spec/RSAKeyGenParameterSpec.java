package org.openjsse.java.security.spec;

import java.security.spec.AlgorithmParameterSpec;
import java.math.BigInteger;

public class RSAKeyGenParameterSpec extends java.security.spec.RSAKeyGenParameterSpec
{
    public RSAKeyGenParameterSpec(final int keysize, final BigInteger publicExponent) {
        super(keysize, publicExponent);
    }
    
    public RSAKeyGenParameterSpec(final int keysize, final BigInteger publicExponent, final AlgorithmParameterSpec keyParams) {
        super(keysize, publicExponent, keyParams);
    }
}
