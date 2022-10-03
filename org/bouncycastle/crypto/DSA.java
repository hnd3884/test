package org.bouncycastle.crypto;

import java.math.BigInteger;

public interface DSA
{
    void init(final boolean p0, final CipherParameters p1);
    
    BigInteger[] generateSignature(final byte[] p0);
    
    boolean verifySignature(final byte[] p0, final BigInteger p1, final BigInteger p2);
}
