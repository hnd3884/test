package org.bouncycastle.math.ec;

import java.math.BigInteger;
import org.bouncycastle.math.ec.endo.GLVEndomorphism;

public class GLVMultiplier extends AbstractECMultiplier
{
    protected final ECCurve curve;
    protected final GLVEndomorphism glvEndomorphism;
    
    public GLVMultiplier(final ECCurve curve, final GLVEndomorphism glvEndomorphism) {
        if (curve == null || curve.getOrder() == null) {
            throw new IllegalArgumentException("Need curve with known group order");
        }
        this.curve = curve;
        this.glvEndomorphism = glvEndomorphism;
    }
    
    @Override
    protected ECPoint multiplyPositive(final ECPoint ecPoint, final BigInteger bigInteger) {
        if (!this.curve.equals(ecPoint.getCurve())) {
            throw new IllegalStateException();
        }
        final BigInteger[] decomposeScalar = this.glvEndomorphism.decomposeScalar(bigInteger.mod(ecPoint.getCurve().getOrder()));
        final BigInteger bigInteger2 = decomposeScalar[0];
        final BigInteger bigInteger3 = decomposeScalar[1];
        final ECPointMap pointMap = this.glvEndomorphism.getPointMap();
        if (this.glvEndomorphism.hasEfficientPointMap()) {
            return ECAlgorithms.implShamirsTrickWNaf(ecPoint, bigInteger2, pointMap, bigInteger3);
        }
        return ECAlgorithms.implShamirsTrickWNaf(ecPoint, bigInteger2, pointMap.map(ecPoint), bigInteger3);
    }
}
