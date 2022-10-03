package org.bouncycastle.math.ec;

import java.math.BigInteger;

public class ZSignedDigitL2RMultiplier extends AbstractECMultiplier
{
    @Override
    protected ECPoint multiplyPositive(final ECPoint ecPoint, final BigInteger bigInteger) {
        final ECPoint normalize = ecPoint.normalize();
        final ECPoint negate = normalize.negate();
        ECPoint twicePlus = normalize;
        final int bitLength = bigInteger.bitLength();
        final int lowestSetBit = bigInteger.getLowestSetBit();
        int n = bitLength;
        while (--n > lowestSetBit) {
            twicePlus = twicePlus.twicePlus(bigInteger.testBit(n) ? normalize : negate);
        }
        return twicePlus.timesPow2(lowestSetBit);
    }
}
