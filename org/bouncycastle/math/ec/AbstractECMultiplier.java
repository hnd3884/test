package org.bouncycastle.math.ec;

import java.math.BigInteger;

public abstract class AbstractECMultiplier implements ECMultiplier
{
    public ECPoint multiply(final ECPoint ecPoint, final BigInteger bigInteger) {
        final int signum = bigInteger.signum();
        if (signum == 0 || ecPoint.isInfinity()) {
            return ecPoint.getCurve().getInfinity();
        }
        final ECPoint multiplyPositive = this.multiplyPositive(ecPoint, bigInteger.abs());
        return ECAlgorithms.validatePoint((signum > 0) ? multiplyPositive : multiplyPositive.negate());
    }
    
    protected abstract ECPoint multiplyPositive(final ECPoint p0, final BigInteger p1);
}
