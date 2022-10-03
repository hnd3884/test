package org.bouncycastle.math.ec;

import java.math.BigInteger;

public class DoubleAddMultiplier extends AbstractECMultiplier
{
    @Override
    protected ECPoint multiplyPositive(final ECPoint ecPoint, final BigInteger bigInteger) {
        final ECPoint[] array = { ecPoint.getCurve().getInfinity(), ecPoint };
        for (int bitLength = bigInteger.bitLength(), i = 0; i < bitLength; ++i) {
            final int testBit = bigInteger.testBit(i) ? 1 : 0;
            final int n = 1 - testBit;
            array[n] = array[n].twicePlus(array[testBit]);
        }
        return array[0];
    }
}
