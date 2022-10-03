package org.bouncycastle.math.ec.custom.sec;

import org.bouncycastle.math.ec.ECConstants;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECPoint;

public class SecT409R1Point extends AbstractF2m
{
    @Deprecated
    public SecT409R1Point(final ECCurve ecCurve, final ECFieldElement ecFieldElement, final ECFieldElement ecFieldElement2) {
        this(ecCurve, ecFieldElement, ecFieldElement2, false);
    }
    
    @Deprecated
    public SecT409R1Point(final ECCurve ecCurve, final ECFieldElement ecFieldElement, final ECFieldElement ecFieldElement2, final boolean withCompression) {
        super(ecCurve, ecFieldElement, ecFieldElement2);
        if (ecFieldElement == null != (ecFieldElement2 == null)) {
            throw new IllegalArgumentException("Exactly one of the field elements is null");
        }
        this.withCompression = withCompression;
    }
    
    SecT409R1Point(final ECCurve ecCurve, final ECFieldElement ecFieldElement, final ECFieldElement ecFieldElement2, final ECFieldElement[] array, final boolean withCompression) {
        super(ecCurve, ecFieldElement, ecFieldElement2, array);
        this.withCompression = withCompression;
    }
    
    @Override
    protected ECPoint detach() {
        return new SecT409R1Point(null, this.getAffineXCoord(), this.getAffineYCoord());
    }
    
    @Override
    public ECFieldElement getYCoord() {
        final ECFieldElement x = this.x;
        final ECFieldElement y = this.y;
        if (this.isInfinity() || x.isZero()) {
            return y;
        }
        ECFieldElement ecFieldElement = y.add(x).multiply(x);
        final ECFieldElement ecFieldElement2 = this.zs[0];
        if (!ecFieldElement2.isOne()) {
            ecFieldElement = ecFieldElement.divide(ecFieldElement2);
        }
        return ecFieldElement;
    }
    
    @Override
    protected boolean getCompressionYTilde() {
        final ECFieldElement rawXCoord = this.getRawXCoord();
        return !rawXCoord.isZero() && this.getRawYCoord().testBitZero() != rawXCoord.testBitZero();
    }
    
    @Override
    public ECPoint add(final ECPoint ecPoint) {
        if (this.isInfinity()) {
            return ecPoint;
        }
        if (ecPoint.isInfinity()) {
            return this;
        }
        final ECCurve curve = this.getCurve();
        final ECFieldElement x = this.x;
        final ECFieldElement rawXCoord = ecPoint.getRawXCoord();
        if (x.isZero()) {
            if (rawXCoord.isZero()) {
                return curve.getInfinity();
            }
            return ecPoint.add(this);
        }
        else {
            final ECFieldElement y = this.y;
            final ECFieldElement ecFieldElement = this.zs[0];
            final ECFieldElement rawYCoord = ecPoint.getRawYCoord();
            final ECFieldElement zCoord = ecPoint.getZCoord(0);
            final boolean one = ecFieldElement.isOne();
            ECFieldElement multiply = rawXCoord;
            ECFieldElement multiply2 = rawYCoord;
            if (!one) {
                multiply = multiply.multiply(ecFieldElement);
                multiply2 = multiply2.multiply(ecFieldElement);
            }
            final boolean one2 = zCoord.isOne();
            ECFieldElement multiply3 = x;
            ECFieldElement multiply4 = y;
            if (!one2) {
                multiply3 = multiply3.multiply(zCoord);
                multiply4 = multiply4.multiply(zCoord);
            }
            final ECFieldElement add = multiply4.add(multiply2);
            final ECFieldElement add2 = multiply3.add(multiply);
            if (!add2.isZero()) {
                ECFieldElement ecFieldElement2;
                ECFieldElement ecFieldElement3;
                ECFieldElement ecFieldElement4;
                if (rawXCoord.isZero()) {
                    final ECPoint normalize = this.normalize();
                    final ECFieldElement xCoord = normalize.getXCoord();
                    final ECFieldElement yCoord = normalize.getYCoord();
                    final ECFieldElement divide = yCoord.add(rawYCoord).divide(xCoord);
                    ecFieldElement2 = divide.square().add(divide).add(xCoord).addOne();
                    if (ecFieldElement2.isZero()) {
                        return new SecT409R1Point(curve, ecFieldElement2, curve.getB().sqrt(), this.withCompression);
                    }
                    ecFieldElement3 = divide.multiply(xCoord.add(ecFieldElement2)).add(ecFieldElement2).add(yCoord).divide(ecFieldElement2).add(ecFieldElement2);
                    ecFieldElement4 = curve.fromBigInteger(ECConstants.ONE);
                }
                else {
                    final ECFieldElement square = add2.square();
                    final ECFieldElement multiply5 = add.multiply(multiply3);
                    final ECFieldElement multiply6 = add.multiply(multiply);
                    ecFieldElement2 = multiply5.multiply(multiply6);
                    if (ecFieldElement2.isZero()) {
                        return new SecT409R1Point(curve, ecFieldElement2, curve.getB().sqrt(), this.withCompression);
                    }
                    ECFieldElement ecFieldElement5 = add.multiply(square);
                    if (!one2) {
                        ecFieldElement5 = ecFieldElement5.multiply(zCoord);
                    }
                    ecFieldElement3 = multiply6.add(square).squarePlusProduct(ecFieldElement5, y.add(ecFieldElement));
                    ecFieldElement4 = ecFieldElement5;
                    if (!one) {
                        ecFieldElement4 = ecFieldElement4.multiply(ecFieldElement);
                    }
                }
                return new SecT409R1Point(curve, ecFieldElement2, ecFieldElement3, new ECFieldElement[] { ecFieldElement4 }, this.withCompression);
            }
            if (add.isZero()) {
                return this.twice();
            }
            return curve.getInfinity();
        }
    }
    
    @Override
    public ECPoint twice() {
        if (this.isInfinity()) {
            return this;
        }
        final ECCurve curve = this.getCurve();
        final ECFieldElement x = this.x;
        if (x.isZero()) {
            return curve.getInfinity();
        }
        final ECFieldElement y = this.y;
        final ECFieldElement ecFieldElement = this.zs[0];
        final boolean one = ecFieldElement.isOne();
        final ECFieldElement ecFieldElement2 = one ? y : y.multiply(ecFieldElement);
        final ECFieldElement ecFieldElement3 = one ? ecFieldElement : ecFieldElement.square();
        final ECFieldElement add = y.square().add(ecFieldElement2).add(ecFieldElement3);
        if (add.isZero()) {
            return new SecT409R1Point(curve, add, curve.getB().sqrt(), this.withCompression);
        }
        final ECFieldElement square = add.square();
        final ECFieldElement ecFieldElement4 = one ? add : add.multiply(ecFieldElement3);
        return new SecT409R1Point(curve, square, (one ? x : x.multiply(ecFieldElement)).squarePlusProduct(add, ecFieldElement2).add(square).add(ecFieldElement4), new ECFieldElement[] { ecFieldElement4 }, this.withCompression);
    }
    
    @Override
    public ECPoint twicePlus(final ECPoint ecPoint) {
        if (this.isInfinity()) {
            return ecPoint;
        }
        if (ecPoint.isInfinity()) {
            return this.twice();
        }
        final ECCurve curve = this.getCurve();
        final ECFieldElement x = this.x;
        if (x.isZero()) {
            return ecPoint;
        }
        final ECFieldElement rawXCoord = ecPoint.getRawXCoord();
        final ECFieldElement zCoord = ecPoint.getZCoord(0);
        if (rawXCoord.isZero() || !zCoord.isOne()) {
            return this.twice().add(ecPoint);
        }
        final ECFieldElement y = this.y;
        final ECFieldElement ecFieldElement = this.zs[0];
        final ECFieldElement rawYCoord = ecPoint.getRawYCoord();
        final ECFieldElement square = x.square();
        final ECFieldElement square2 = y.square();
        final ECFieldElement square3 = ecFieldElement.square();
        final ECFieldElement add = square3.add(square2).add(y.multiply(ecFieldElement));
        final ECFieldElement multiplyPlusProduct = rawYCoord.multiply(square3).add(square2).multiplyPlusProduct(add, square, square3);
        final ECFieldElement multiply = rawXCoord.multiply(square3);
        final ECFieldElement square4 = multiply.add(add).square();
        if (square4.isZero()) {
            if (multiplyPlusProduct.isZero()) {
                return ecPoint.twice();
            }
            return curve.getInfinity();
        }
        else {
            if (multiplyPlusProduct.isZero()) {
                return new SecT409R1Point(curve, multiplyPlusProduct, curve.getB().sqrt(), this.withCompression);
            }
            final ECFieldElement multiply2 = multiplyPlusProduct.square().multiply(multiply);
            final ECFieldElement multiply3 = multiplyPlusProduct.multiply(square4).multiply(square3);
            return new SecT409R1Point(curve, multiply2, multiplyPlusProduct.add(square4).square().multiplyPlusProduct(add, rawYCoord.addOne(), multiply3), new ECFieldElement[] { multiply3 }, this.withCompression);
        }
    }
    
    @Override
    public ECPoint negate() {
        if (this.isInfinity()) {
            return this;
        }
        final ECFieldElement x = this.x;
        if (x.isZero()) {
            return this;
        }
        final ECFieldElement y = this.y;
        final ECFieldElement ecFieldElement = this.zs[0];
        return new SecT409R1Point(this.curve, x, y.add(ecFieldElement), new ECFieldElement[] { ecFieldElement }, this.withCompression);
    }
}
