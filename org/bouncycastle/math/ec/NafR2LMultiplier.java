package org.bouncycastle.math.ec;

import java.math.BigInteger;

public class NafR2LMultiplier extends AbstractECMultiplier
{
    @Override
    protected ECPoint multiplyPositive(final ECPoint ecPoint, final BigInteger bigInteger) {
        final int[] generateCompactNaf = WNafUtil.generateCompactNaf(bigInteger);
        ECPoint ecPoint2 = ecPoint.getCurve().getInfinity();
        ECPoint timesPow2 = ecPoint;
        int n = 0;
        for (int i = 0; i < generateCompactNaf.length; ++i) {
            final int n2 = generateCompactNaf[i];
            final int n3 = n2 >> 16;
            timesPow2 = timesPow2.timesPow2(n + (n2 & 0xFFFF));
            ecPoint2 = ecPoint2.add((n3 < 0) ? timesPow2.negate() : timesPow2);
            n = 1;
        }
        return ecPoint2;
    }
}
