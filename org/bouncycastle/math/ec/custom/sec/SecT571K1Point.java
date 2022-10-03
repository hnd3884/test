package org.bouncycastle.math.ec.custom.sec;

import org.bouncycastle.math.ec.ECConstants;
import org.bouncycastle.math.raw.Nat576;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECPoint;

public class SecT571K1Point extends AbstractF2m
{
    @Deprecated
    public SecT571K1Point(final ECCurve ecCurve, final ECFieldElement ecFieldElement, final ECFieldElement ecFieldElement2) {
        this(ecCurve, ecFieldElement, ecFieldElement2, false);
    }
    
    @Deprecated
    public SecT571K1Point(final ECCurve ecCurve, final ECFieldElement ecFieldElement, final ECFieldElement ecFieldElement2, final boolean withCompression) {
        super(ecCurve, ecFieldElement, ecFieldElement2);
        if (ecFieldElement == null != (ecFieldElement2 == null)) {
            throw new IllegalArgumentException("Exactly one of the field elements is null");
        }
        this.withCompression = withCompression;
    }
    
    SecT571K1Point(final ECCurve ecCurve, final ECFieldElement ecFieldElement, final ECFieldElement ecFieldElement2, final ECFieldElement[] array, final boolean withCompression) {
        super(ecCurve, ecFieldElement, ecFieldElement2, array);
        this.withCompression = withCompression;
    }
    
    @Override
    protected ECPoint detach() {
        return new SecT571K1Point(null, this.getAffineXCoord(), this.getAffineYCoord());
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
        final SecT571FieldElement secT571FieldElement = (SecT571FieldElement)this.x;
        final SecT571FieldElement secT571FieldElement2 = (SecT571FieldElement)ecPoint.getRawXCoord();
        if (secT571FieldElement.isZero()) {
            if (secT571FieldElement2.isZero()) {
                return curve.getInfinity();
            }
            return ecPoint.add(this);
        }
        else {
            final SecT571FieldElement secT571FieldElement3 = (SecT571FieldElement)this.y;
            final SecT571FieldElement secT571FieldElement4 = (SecT571FieldElement)this.zs[0];
            final SecT571FieldElement secT571FieldElement5 = (SecT571FieldElement)ecPoint.getRawYCoord();
            final SecT571FieldElement secT571FieldElement6 = (SecT571FieldElement)ecPoint.getZCoord(0);
            final long[] create64 = Nat576.create64();
            final long[] create65 = Nat576.create64();
            final long[] create66 = Nat576.create64();
            final long[] create67 = Nat576.create64();
            final long[] array = (long[])(secT571FieldElement4.isOne() ? null : SecT571Field.precompMultiplicand(secT571FieldElement4.x));
            long[] x;
            long[] x2;
            if (array == null) {
                x = secT571FieldElement2.x;
                x2 = secT571FieldElement5.x;
            }
            else {
                SecT571Field.multiplyPrecomp(secT571FieldElement2.x, array, x = create65);
                SecT571Field.multiplyPrecomp(secT571FieldElement5.x, array, x2 = create67);
            }
            final long[] array2 = (long[])(secT571FieldElement6.isOne() ? null : SecT571Field.precompMultiplicand(secT571FieldElement6.x));
            long[] x3;
            long[] x4;
            if (array2 == null) {
                x3 = secT571FieldElement.x;
                x4 = secT571FieldElement3.x;
            }
            else {
                SecT571Field.multiplyPrecomp(secT571FieldElement.x, array2, x3 = create64);
                SecT571Field.multiplyPrecomp(secT571FieldElement3.x, array2, x4 = create66);
            }
            final long[] array3 = create66;
            SecT571Field.add(x4, x2, array3);
            final long[] array4 = create67;
            SecT571Field.add(x3, x, array4);
            if (!Nat576.isZero64(array4)) {
                ECFieldElement ecFieldElement;
                ECFieldElement ecFieldElement2;
                ECFieldElement ecFieldElement3;
                if (secT571FieldElement2.isZero()) {
                    final ECPoint normalize = this.normalize();
                    final SecT571FieldElement secT571FieldElement7 = (SecT571FieldElement)normalize.getXCoord();
                    final ECFieldElement yCoord = normalize.getYCoord();
                    final ECFieldElement divide = yCoord.add(secT571FieldElement5).divide(secT571FieldElement7);
                    ecFieldElement = divide.square().add(divide).add(secT571FieldElement7);
                    if (((SecT571FieldElement)ecFieldElement).isZero()) {
                        return new SecT571K1Point(curve, ecFieldElement, curve.getB(), this.withCompression);
                    }
                    ecFieldElement2 = divide.multiply(secT571FieldElement7.add(ecFieldElement)).add(ecFieldElement).add(yCoord).divide(ecFieldElement).add(ecFieldElement);
                    ecFieldElement3 = curve.fromBigInteger(ECConstants.ONE);
                }
                else {
                    SecT571Field.square(array4, array4);
                    final long[] precompMultiplicand = SecT571Field.precompMultiplicand(array3);
                    final long[] array5 = create64;
                    final long[] array6 = create65;
                    SecT571Field.multiplyPrecomp(x3, precompMultiplicand, array5);
                    SecT571Field.multiplyPrecomp(x, precompMultiplicand, array6);
                    ecFieldElement = new SecT571FieldElement(create64);
                    SecT571Field.multiply(array5, array6, ((SecT571FieldElement)ecFieldElement).x);
                    if (((SecT571FieldElement)ecFieldElement).isZero()) {
                        return new SecT571K1Point(curve, ecFieldElement, curve.getB(), this.withCompression);
                    }
                    ecFieldElement3 = new SecT571FieldElement(create66);
                    SecT571Field.multiplyPrecomp(array4, precompMultiplicand, ((SecT571FieldElement)ecFieldElement3).x);
                    if (array2 != null) {
                        SecT571Field.multiplyPrecomp(((SecT571FieldElement)ecFieldElement3).x, array2, ((SecT571FieldElement)ecFieldElement3).x);
                    }
                    final long[] ext64 = Nat576.createExt64();
                    SecT571Field.add(array6, array4, create67);
                    SecT571Field.squareAddToExt(create67, ext64);
                    SecT571Field.add(secT571FieldElement3.x, secT571FieldElement4.x, create67);
                    SecT571Field.multiplyAddToExt(create67, ((SecT571FieldElement)ecFieldElement3).x, ext64);
                    ecFieldElement2 = new SecT571FieldElement(create67);
                    SecT571Field.reduce(ext64, ((SecT571FieldElement)ecFieldElement2).x);
                    if (array != null) {
                        SecT571Field.multiplyPrecomp(((SecT571FieldElement)ecFieldElement3).x, array, ((SecT571FieldElement)ecFieldElement3).x);
                    }
                }
                return new SecT571K1Point(curve, ecFieldElement, ecFieldElement2, new ECFieldElement[] { ecFieldElement3 }, this.withCompression);
            }
            if (Nat576.isZero64(array3)) {
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
        final ECFieldElement ecFieldElement2 = one ? ecFieldElement : ecFieldElement.square();
        ECFieldElement ecFieldElement3;
        if (one) {
            ecFieldElement3 = y.square().add(y);
        }
        else {
            ecFieldElement3 = y.add(ecFieldElement).multiply(y);
        }
        if (ecFieldElement3.isZero()) {
            return new SecT571K1Point(curve, ecFieldElement3, curve.getB(), this.withCompression);
        }
        final ECFieldElement square = ecFieldElement3.square();
        final ECFieldElement ecFieldElement4 = one ? ecFieldElement3 : ecFieldElement3.multiply(ecFieldElement2);
        final ECFieldElement square2 = y.add(x).square();
        return new SecT571K1Point(curve, square, square2.add(ecFieldElement3).add(ecFieldElement2).multiply(square2).add(one ? ecFieldElement : ecFieldElement2.square()).add(square).add(ecFieldElement4), new ECFieldElement[] { ecFieldElement4 }, this.withCompression);
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
        final ECFieldElement add = square2.add(y.multiply(ecFieldElement));
        final ECFieldElement addOne = rawYCoord.addOne();
        final ECFieldElement multiplyPlusProduct = addOne.multiply(square3).add(square2).multiplyPlusProduct(add, square, square3);
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
                return new SecT571K1Point(curve, multiplyPlusProduct, curve.getB(), this.withCompression);
            }
            final ECFieldElement multiply2 = multiplyPlusProduct.square().multiply(multiply);
            final ECFieldElement multiply3 = multiplyPlusProduct.multiply(square4).multiply(square3);
            return new SecT571K1Point(curve, multiply2, multiplyPlusProduct.add(square4).square().multiplyPlusProduct(add, addOne, multiply3), new ECFieldElement[] { multiply3 }, this.withCompression);
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
        return new SecT571K1Point(this.curve, x, y.add(ecFieldElement), new ECFieldElement[] { ecFieldElement }, this.withCompression);
    }
}
