package org.bouncycastle.crypto.signers;

import java.security.SecureRandom;
import java.math.BigInteger;

public interface DSAKCalculator
{
    boolean isDeterministic();
    
    void init(final BigInteger p0, final SecureRandom p1);
    
    void init(final BigInteger p0, final BigInteger p1, final byte[] p2);
    
    BigInteger nextK();
}
