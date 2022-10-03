package org.bouncycastle.math.ec.endo;

import java.math.BigInteger;

public interface GLVEndomorphism extends ECEndomorphism
{
    BigInteger[] decomposeScalar(final BigInteger p0);
}
