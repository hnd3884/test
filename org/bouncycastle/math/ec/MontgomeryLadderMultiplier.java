package org.bouncycastle.math.ec;

import java.math.BigInteger;

public class MontgomeryLadderMultiplier extends AbstractECMultiplier
{
    @Override
    protected ECPoint multiplyPositive(final ECPoint ecPoint, final BigInteger bigInteger) {
        final ECPoint[] array = { ecPoint.getCurve().getInfinity(), ecPoint };
        int bitLength = bigInteger.bitLength();
        while (--bitLength >= 0) {
            final int testBit = bigInteger.testBit(bitLength) ? 1 : 0;
            final int n = 1 - testBit;
            array[n] = array[n].add(array[testBit]);
            array[testBit] = array[testBit].twice();
        }
        return array[0];
    }
}
