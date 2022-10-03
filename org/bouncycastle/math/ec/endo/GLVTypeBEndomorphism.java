package org.bouncycastle.math.ec.endo;

import org.bouncycastle.math.ec.ECConstants;
import java.math.BigInteger;
import org.bouncycastle.math.ec.ScaleXPointMap;
import org.bouncycastle.math.ec.ECPointMap;
import org.bouncycastle.math.ec.ECCurve;

public class GLVTypeBEndomorphism implements GLVEndomorphism
{
    protected final ECCurve curve;
    protected final GLVTypeBParameters parameters;
    protected final ECPointMap pointMap;
    
    public GLVTypeBEndomorphism(final ECCurve curve, final GLVTypeBParameters parameters) {
        this.curve = curve;
        this.parameters = parameters;
        this.pointMap = new ScaleXPointMap(curve.fromBigInteger(parameters.getBeta()));
    }
    
    public BigInteger[] decomposeScalar(final BigInteger bigInteger) {
        final int bits = this.parameters.getBits();
        final BigInteger calculateB = this.calculateB(bigInteger, this.parameters.getG1(), bits);
        final BigInteger calculateB2 = this.calculateB(bigInteger, this.parameters.getG2(), bits);
        final GLVTypeBParameters parameters = this.parameters;
        return new BigInteger[] { bigInteger.subtract(calculateB.multiply(parameters.getV1A()).add(calculateB2.multiply(parameters.getV2A()))), calculateB.multiply(parameters.getV1B()).add(calculateB2.multiply(parameters.getV2B())).negate() };
    }
    
    public ECPointMap getPointMap() {
        return this.pointMap;
    }
    
    public boolean hasEfficientPointMap() {
        return true;
    }
    
    protected BigInteger calculateB(final BigInteger bigInteger, final BigInteger bigInteger2, final int n) {
        final boolean b = bigInteger2.signum() < 0;
        final BigInteger multiply = bigInteger.multiply(bigInteger2.abs());
        final boolean testBit = multiply.testBit(n - 1);
        BigInteger bigInteger3 = multiply.shiftRight(n);
        if (testBit) {
            bigInteger3 = bigInteger3.add(ECConstants.ONE);
        }
        return b ? bigInteger3.negate() : bigInteger3;
    }
}
