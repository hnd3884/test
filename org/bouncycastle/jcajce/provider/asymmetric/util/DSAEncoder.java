package org.bouncycastle.jcajce.provider.asymmetric.util;

import java.io.IOException;
import java.math.BigInteger;

public interface DSAEncoder
{
    byte[] encode(final BigInteger p0, final BigInteger p1) throws IOException;
    
    BigInteger[] decode(final byte[] p0) throws IOException;
}
