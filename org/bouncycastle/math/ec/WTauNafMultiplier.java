package org.bouncycastle.math.ec;

import java.math.BigInteger;

public class WTauNafMultiplier extends AbstractECMultiplier
{
    static final String PRECOMP_NAME = "bc_wtnaf";
    
    @Override
    protected ECPoint multiplyPositive(final ECPoint ecPoint, final BigInteger bigInteger) {
        if (!(ecPoint instanceof ECPoint.AbstractF2m)) {
            throw new IllegalArgumentException("Only ECPoint.AbstractF2m can be used in WTauNafMultiplier");
        }
        final ECPoint.AbstractF2m abstractF2m = (ECPoint.AbstractF2m)ecPoint;
        final ECCurve.AbstractF2m abstractF2m2 = (ECCurve.AbstractF2m)abstractF2m.getCurve();
        final int fieldSize = abstractF2m2.getFieldSize();
        final byte byteValue = abstractF2m2.getA().toBigInteger().byteValue();
        final byte mu = Tnaf.getMu(byteValue);
        return this.multiplyWTnaf(abstractF2m, Tnaf.partModReduction(bigInteger, fieldSize, byteValue, abstractF2m2.getSi(), mu, (byte)10), abstractF2m2.getPreCompInfo(abstractF2m, "bc_wtnaf"), byteValue, mu);
    }
    
    private ECPoint.AbstractF2m multiplyWTnaf(final ECPoint.AbstractF2m abstractF2m, final ZTauElement zTauElement, final PreCompInfo preCompInfo, final byte b, final byte b2) {
        return multiplyFromWTnaf(abstractF2m, Tnaf.tauAdicWNaf(b2, zTauElement, (byte)4, BigInteger.valueOf(16L), Tnaf.getTw(b2, 4), (b == 0) ? Tnaf.alpha0 : Tnaf.alpha1), preCompInfo);
    }
    
    private static ECPoint.AbstractF2m multiplyFromWTnaf(final ECPoint.AbstractF2m abstractF2m, final byte[] array, final PreCompInfo preCompInfo) {
        final ECCurve.AbstractF2m abstractF2m2 = (ECCurve.AbstractF2m)abstractF2m.getCurve();
        final byte byteValue = abstractF2m2.getA().toBigInteger().byteValue();
        ECPoint.AbstractF2m[] preComp;
        if (preCompInfo == null || !(preCompInfo instanceof WTauNafPreCompInfo)) {
            preComp = Tnaf.getPreComp(abstractF2m, byteValue);
            final WTauNafPreCompInfo wTauNafPreCompInfo = new WTauNafPreCompInfo();
            wTauNafPreCompInfo.setPreComp(preComp);
            abstractF2m2.setPreCompInfo(abstractF2m, "bc_wtnaf", wTauNafPreCompInfo);
        }
        else {
            preComp = ((WTauNafPreCompInfo)preCompInfo).getPreComp();
        }
        final ECPoint.AbstractF2m[] array2 = new ECPoint.AbstractF2m[preComp.length];
        for (int i = 0; i < preComp.length; ++i) {
            array2[i] = (ECPoint.AbstractF2m)preComp[i].negate();
        }
        ECPoint.AbstractF2m tauPow = (ECPoint.AbstractF2m)abstractF2m.getCurve().getInfinity();
        int n = 0;
        for (int j = array.length - 1; j >= 0; --j) {
            ++n;
            final byte b = array[j];
            if (b != 0) {
                final ECPoint.AbstractF2m tauPow2 = tauPow.tauPow(n);
                n = 0;
                tauPow = (ECPoint.AbstractF2m)tauPow2.add((b > 0) ? preComp[b >>> 1] : array2[-b >>> 1]);
            }
        }
        if (n > 0) {
            tauPow = tauPow.tauPow(n);
        }
        return tauPow;
    }
}
