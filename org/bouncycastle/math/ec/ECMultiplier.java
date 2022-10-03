package org.bouncycastle.math.ec;

import java.math.BigInteger;

public interface ECMultiplier
{
    ECPoint multiply(final ECPoint p0, final BigInteger p1);
}
