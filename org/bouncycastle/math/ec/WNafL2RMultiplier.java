package org.bouncycastle.math.ec;

import java.math.BigInteger;

public class WNafL2RMultiplier extends AbstractECMultiplier
{
    @Override
    protected ECPoint multiplyPositive(final ECPoint ecPoint, final BigInteger bigInteger) {
        final int max = Math.max(2, Math.min(16, this.getWindowSize(bigInteger.bitLength())));
        final WNafPreCompInfo precompute = WNafUtil.precompute(ecPoint, max, true);
        final ECPoint[] preComp = precompute.getPreComp();
        final ECPoint[] preCompNeg = precompute.getPreCompNeg();
        final int[] generateCompactWindowNaf = WNafUtil.generateCompactWindowNaf(max, bigInteger);
        ECPoint ecPoint2 = ecPoint.getCurve().getInfinity();
        int i = generateCompactWindowNaf.length;
        if (i > 1) {
            final int n = generateCompactWindowNaf[--i];
            final int n2 = n >> 16;
            int n3 = n & 0xFFFF;
            final int abs = Math.abs(n2);
            final ECPoint[] array = (n2 < 0) ? preCompNeg : preComp;
            ECPoint add;
            if (abs << 2 < 1 << max) {
                final byte b = LongArray.bitLengths[abs];
                final int n4 = max - b;
                add = array[(1 << max - 1) - 1 >>> 1].add(array[((abs ^ 1 << b - 1) << n4) + 1 >>> 1]);
                n3 -= n4;
            }
            else {
                add = array[abs >>> 1];
            }
            ecPoint2 = add.timesPow2(n3);
        }
        while (i > 0) {
            final int n5 = generateCompactWindowNaf[--i];
            final int n6 = n5 >> 16;
            ecPoint2 = ecPoint2.twicePlus(((n6 < 0) ? preCompNeg : preComp)[Math.abs(n6) >>> 1]).timesPow2(n5 & 0xFFFF);
        }
        return ecPoint2;
    }
    
    protected int getWindowSize(final int n) {
        return WNafUtil.getWindowSize(n);
    }
}
