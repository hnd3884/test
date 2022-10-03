package org.bouncycastle.math.ec;

import java.math.BigInteger;

public class ZSignedDigitR2LMultiplier extends AbstractECMultiplier
{
    @Override
    protected ECPoint multiplyPositive(final ECPoint ecPoint, final BigInteger bigInteger) {
        ECPoint ecPoint2 = ecPoint.getCurve().getInfinity();
        final int bitLength = bigInteger.bitLength();
        final int lowestSetBit = bigInteger.getLowestSetBit();
        ECPoint ecPoint3 = ecPoint.timesPow2(lowestSetBit);
        int n = lowestSetBit;
        while (++n < bitLength) {
            ecPoint2 = ecPoint2.add(bigInteger.testBit(n) ? ecPoint3 : ecPoint3.negate());
            ecPoint3 = ecPoint3.twice();
        }
        return ecPoint2.add(ecPoint3);
    }
}
