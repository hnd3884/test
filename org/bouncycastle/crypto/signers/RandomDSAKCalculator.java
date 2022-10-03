package org.bouncycastle.crypto.signers;

import java.util.Random;
import java.security.SecureRandom;
import java.math.BigInteger;

public class RandomDSAKCalculator implements DSAKCalculator
{
    private static final BigInteger ZERO;
    private BigInteger q;
    private SecureRandom random;
    
    public boolean isDeterministic() {
        return false;
    }
    
    public void init(final BigInteger q, final SecureRandom random) {
        this.q = q;
        this.random = random;
    }
    
    public void init(final BigInteger bigInteger, final BigInteger bigInteger2, final byte[] array) {
        throw new IllegalStateException("Operation not supported");
    }
    
    public BigInteger nextK() {
        final int bitLength = this.q.bitLength();
        BigInteger bigInteger;
        do {
            bigInteger = new BigInteger(bitLength, this.random);
        } while (bigInteger.equals(RandomDSAKCalculator.ZERO) || bigInteger.compareTo(this.q) >= 0);
        return bigInteger;
    }
    
    static {
        ZERO = BigInteger.valueOf(0L);
    }
}
