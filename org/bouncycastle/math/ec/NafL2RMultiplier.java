package org.bouncycastle.math.ec;

import java.math.BigInteger;

public class NafL2RMultiplier extends AbstractECMultiplier
{
    @Override
    protected ECPoint multiplyPositive(final ECPoint ecPoint, final BigInteger bigInteger) {
        final int[] generateCompactNaf = WNafUtil.generateCompactNaf(bigInteger);
        final ECPoint normalize = ecPoint.normalize();
        final ECPoint negate = normalize.negate();
        ECPoint ecPoint2 = ecPoint.getCurve().getInfinity();
        int length = generateCompactNaf.length;
        while (--length >= 0) {
            final int n = generateCompactNaf[length];
            ecPoint2 = ecPoint2.twicePlus((n >> 16 < 0) ? negate : normalize).timesPow2(n & 0xFFFF);
        }
        return ecPoint2;
    }
}
