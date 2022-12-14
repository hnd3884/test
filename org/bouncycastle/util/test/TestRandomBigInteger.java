package org.bouncycastle.util.test;

import org.bouncycastle.util.BigIntegers;
import java.math.BigInteger;

public class TestRandomBigInteger extends FixedSecureRandom
{
    public TestRandomBigInteger(final String s) {
        this(s, 10);
    }
    
    public TestRandomBigInteger(final String s, final int n) {
        super(new Source[] { new BigInteger(BigIntegers.asUnsignedByteArray(new java.math.BigInteger(s, n))) });
    }
    
    public TestRandomBigInteger(final byte[] array) {
        super(new Source[] { new BigInteger(array) });
    }
    
    public TestRandomBigInteger(final int n, final byte[] array) {
        super(new Source[] { new BigInteger(n, array) });
    }
}
