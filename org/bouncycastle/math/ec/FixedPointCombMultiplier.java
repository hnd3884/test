package org.bouncycastle.math.ec;

import org.bouncycastle.math.raw.Nat;
import java.math.BigInteger;

public class FixedPointCombMultiplier extends AbstractECMultiplier
{
    @Override
    protected ECPoint multiplyPositive(final ECPoint ecPoint, final BigInteger bigInteger) {
        final ECCurve curve = ecPoint.getCurve();
        final int combSize = FixedPointUtil.getCombSize(curve);
        if (bigInteger.bitLength() > combSize) {
            throw new IllegalStateException("fixed-point comb doesn't support scalars larger than the curve order");
        }
        final FixedPointPreCompInfo precompute = FixedPointUtil.precompute(ecPoint);
        final ECLookupTable lookupTable = precompute.getLookupTable();
        final int width = precompute.getWidth();
        final int n = (combSize + width - 1) / width;
        ECPoint ecPoint2 = curve.getInfinity();
        final int n2 = n * width;
        final int[] fromBigInteger = Nat.fromBigInteger(n2, bigInteger);
        final int n3 = n2 - 1;
        for (int i = 0; i < n; ++i) {
            int n4 = 0;
            for (int j = n3 - i; j >= 0; j -= n) {
                n4 = (n4 << 1 | Nat.getBit(fromBigInteger, j));
            }
            ecPoint2 = ecPoint2.twicePlus(lookupTable.lookup(n4));
        }
        return ecPoint2.add(precompute.getOffset());
    }
    
    @Deprecated
    protected int getWidthForCombSize(final int n) {
        return (n > 257) ? 6 : 5;
    }
}
