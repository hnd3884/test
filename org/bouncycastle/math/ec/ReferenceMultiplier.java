package org.bouncycastle.math.ec;

import java.math.BigInteger;

public class ReferenceMultiplier extends AbstractECMultiplier
{
    @Override
    protected ECPoint multiplyPositive(final ECPoint ecPoint, final BigInteger bigInteger) {
        return ECAlgorithms.referenceMultiply(ecPoint, bigInteger);
    }
}
