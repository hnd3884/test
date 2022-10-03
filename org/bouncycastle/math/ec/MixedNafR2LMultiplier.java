package org.bouncycastle.math.ec;

import java.math.BigInteger;

public class MixedNafR2LMultiplier extends AbstractECMultiplier
{
    protected int additionCoord;
    protected int doublingCoord;
    
    public MixedNafR2LMultiplier() {
        this(2, 4);
    }
    
    public MixedNafR2LMultiplier(final int additionCoord, final int doublingCoord) {
        this.additionCoord = additionCoord;
        this.doublingCoord = doublingCoord;
    }
    
    @Override
    protected ECPoint multiplyPositive(final ECPoint ecPoint, final BigInteger bigInteger) {
        final ECCurve curve = ecPoint.getCurve();
        final ECCurve configureCurve = this.configureCurve(curve, this.additionCoord);
        final ECCurve configureCurve2 = this.configureCurve(curve, this.doublingCoord);
        final int[] generateCompactNaf = WNafUtil.generateCompactNaf(bigInteger);
        ECPoint ecPoint2 = configureCurve.getInfinity();
        ECPoint ecPoint3 = configureCurve2.importPoint(ecPoint);
        int n = 0;
        for (int i = 0; i < generateCompactNaf.length; ++i) {
            final int n2 = generateCompactNaf[i];
            final int n3 = n2 >> 16;
            ecPoint3 = ecPoint3.timesPow2(n + (n2 & 0xFFFF));
            ECPoint ecPoint4 = configureCurve.importPoint(ecPoint3);
            if (n3 < 0) {
                ecPoint4 = ecPoint4.negate();
            }
            ecPoint2 = ecPoint2.add(ecPoint4);
            n = 1;
        }
        return curve.importPoint(ecPoint2);
    }
    
    protected ECCurve configureCurve(final ECCurve ecCurve, final int coordinateSystem) {
        if (ecCurve.getCoordinateSystem() == coordinateSystem) {
            return ecCurve;
        }
        if (!ecCurve.supportsCoordinateSystem(coordinateSystem)) {
            throw new IllegalArgumentException("Coordinate system " + coordinateSystem + " not supported by this curve");
        }
        return ecCurve.configure().setCoordinateSystem(coordinateSystem).create();
    }
}
