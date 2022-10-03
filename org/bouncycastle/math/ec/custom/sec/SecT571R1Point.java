package org.bouncycastle.math.ec.custom.sec;

import org.bouncycastle.math.raw.Nat;
import org.bouncycastle.math.ec.ECConstants;
import org.bouncycastle.math.raw.Nat576;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECPoint;

public class SecT571R1Point extends AbstractF2m
{
    @Deprecated
    public SecT571R1Point(final ECCurve ecCurve, final ECFieldElement ecFieldElement, final ECFieldElement ecFieldElement2) {
        this(ecCurve, ecFieldElement, ecFieldElement2, false);
    }
    
    @Deprecated
    public SecT571R1Point(final ECCurve ecCurve, final ECFieldElement ecFieldElement, final ECFieldElement ecFieldElement2, final boolean withCompression) {
        super(ecCurve, ecFieldElement, ecFieldElement2);
        if (ecFieldElement == null != (ecFieldElement2 == null)) {
            throw new IllegalArgumentException("Exactly one of the field elements is null");
        }
        this.withCompression = withCompression;
    }
    
    SecT571R1Point(final ECCurve ecCurve, final ECFieldElement ecFieldElement, final ECFieldElement ecFieldElement2, final ECFieldElement[] array, final boolean withCompression) {
        super(ecCurve, ecFieldElement, ecFieldElement2, array);
        this.withCompression = withCompression;
    }
    
    @Override
    protected ECPoint detach() {
        return new SecT571R1Point(null, this.getAffineXCoord(), this.getAffineYCoord());
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
                    ecFieldElement = divide.square().add(divide).add(secT571FieldElement7).addOne();
                    if (((SecT571FieldElement)ecFieldElement).isZero()) {
                        return new SecT571R1Point(curve, ecFieldElement, SecT571R1Curve.SecT571R1_B_SQRT, this.withCompression);
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
                        return new SecT571R1Point(curve, ecFieldElement, SecT571R1Curve.SecT571R1_B_SQRT, this.withCompression);
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
                return new SecT571R1Point(curve, ecFieldElement, ecFieldElement2, new ECFieldElement[] { ecFieldElement3 }, this.withCompression);
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
        final SecT571FieldElement secT571FieldElement = (SecT571FieldElement)this.x;
        if (secT571FieldElement.isZero()) {
            return curve.getInfinity();
        }
        final SecT571FieldElement secT571FieldElement2 = (SecT571FieldElement)this.y;
        final SecT571FieldElement secT571FieldElement3 = (SecT571FieldElement)this.zs[0];
        final long[] create64 = Nat576.create64();
        final long[] create65 = Nat576.create64();
        final long[] array = (long[])(secT571FieldElement3.isOne() ? null : SecT571Field.precompMultiplicand(secT571FieldElement3.x));
        long[] x;
        long[] x2;
        if (array == null) {
            x = secT571FieldElement2.x;
            x2 = secT571FieldElement3.x;
        }
        else {
            SecT571Field.multiplyPrecomp(secT571FieldElement2.x, array, x = create64);
            SecT571Field.square(secT571FieldElement3.x, x2 = create65);
        }
        final long[] create66 = Nat576.create64();
        SecT571Field.square(secT571FieldElement2.x, create66);
        SecT571Field.addBothTo(x, x2, create66);
        if (Nat576.isZero64(create66)) {
            return new SecT571R1Point(curve, new SecT571FieldElement(create66), SecT571R1Curve.SecT571R1_B_SQRT, this.withCompression);
        }
        final long[] ext64 = Nat576.createExt64();
        SecT571Field.multiplyAddToExt(create66, x, ext64);
        final SecT571FieldElement secT571FieldElement4 = new SecT571FieldElement(create64);
        SecT571Field.square(create66, secT571FieldElement4.x);
        final SecT571FieldElement secT571FieldElement5 = new SecT571FieldElement(create66);
        if (array != null) {
            SecT571Field.multiply(secT571FieldElement5.x, x2, secT571FieldElement5.x);
        }
        long[] x3;
        if (array == null) {
            x3 = secT571FieldElement.x;
        }
        else {
            SecT571Field.multiplyPrecomp(secT571FieldElement.x, array, x3 = create65);
        }
        SecT571Field.squareAddToExt(x3, ext64);
        SecT571Field.reduce(ext64, create65);
        SecT571Field.addBothTo(secT571FieldElement4.x, secT571FieldElement5.x, create65);
        return new SecT571R1Point(curve, secT571FieldElement4, new SecT571FieldElement(create65), new ECFieldElement[] { secT571FieldElement5 }, this.withCompression);
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
        final SecT571FieldElement secT571FieldElement = (SecT571FieldElement)this.x;
        if (secT571FieldElement.isZero()) {
            return ecPoint;
        }
        final SecT571FieldElement secT571FieldElement2 = (SecT571FieldElement)ecPoint.getRawXCoord();
        final SecT571FieldElement secT571FieldElement3 = (SecT571FieldElement)ecPoint.getZCoord(0);
        if (secT571FieldElement2.isZero() || !secT571FieldElement3.isOne()) {
            return this.twice().add(ecPoint);
        }
        final SecT571FieldElement secT571FieldElement4 = (SecT571FieldElement)this.y;
        final SecT571FieldElement secT571FieldElement5 = (SecT571FieldElement)this.zs[0];
        final SecT571FieldElement secT571FieldElement6 = (SecT571FieldElement)ecPoint.getRawYCoord();
        final long[] create64 = Nat576.create64();
        final long[] create65 = Nat576.create64();
        final long[] create66 = Nat576.create64();
        final long[] create67 = Nat576.create64();
        final long[] array = create64;
        SecT571Field.square(secT571FieldElement.x, array);
        final long[] array2 = create65;
        SecT571Field.square(secT571FieldElement4.x, array2);
        final long[] array3 = create66;
        SecT571Field.square(secT571FieldElement5.x, array3);
        final long[] array4 = create67;
        SecT571Field.multiply(secT571FieldElement4.x, secT571FieldElement5.x, array4);
        final long[] array5 = array4;
        SecT571Field.addBothTo(array3, array2, array5);
        final long[] precompMultiplicand = SecT571Field.precompMultiplicand(array3);
        final long[] array6 = create66;
        SecT571Field.multiplyPrecomp(secT571FieldElement6.x, precompMultiplicand, array6);
        SecT571Field.add(array6, array2, array6);
        final long[] ext64 = Nat576.createExt64();
        SecT571Field.multiplyAddToExt(array6, array5, ext64);
        SecT571Field.multiplyPrecompAddToExt(array, precompMultiplicand, ext64);
        SecT571Field.reduce(ext64, array6);
        final long[] array7 = create64;
        SecT571Field.multiplyPrecomp(secT571FieldElement2.x, precompMultiplicand, array7);
        final long[] array8 = create65;
        SecT571Field.add(array7, array5, array8);
        SecT571Field.square(array8, array8);
        if (Nat576.isZero64(array8)) {
            if (Nat576.isZero64(array6)) {
                return ecPoint.twice();
            }
            return curve.getInfinity();
        }
        else {
            if (Nat576.isZero64(array6)) {
                return new SecT571R1Point(curve, new SecT571FieldElement(array6), SecT571R1Curve.SecT571R1_B_SQRT, this.withCompression);
            }
            final SecT571FieldElement secT571FieldElement7 = new SecT571FieldElement();
            SecT571Field.square(array6, secT571FieldElement7.x);
            SecT571Field.multiply(secT571FieldElement7.x, array7, secT571FieldElement7.x);
            final SecT571FieldElement secT571FieldElement8 = new SecT571FieldElement(create64);
            SecT571Field.multiply(array6, array8, secT571FieldElement8.x);
            SecT571Field.multiplyPrecomp(secT571FieldElement8.x, precompMultiplicand, secT571FieldElement8.x);
            final SecT571FieldElement secT571FieldElement9 = new SecT571FieldElement(create65);
            SecT571Field.add(array6, array8, secT571FieldElement9.x);
            SecT571Field.square(secT571FieldElement9.x, secT571FieldElement9.x);
            Nat.zero64(18, ext64);
            SecT571Field.multiplyAddToExt(secT571FieldElement9.x, array5, ext64);
            SecT571Field.addOne(secT571FieldElement6.x, create67);
            SecT571Field.multiplyAddToExt(create67, secT571FieldElement8.x, ext64);
            SecT571Field.reduce(ext64, secT571FieldElement9.x);
            return new SecT571R1Point(curve, secT571FieldElement7, secT571FieldElement9, new ECFieldElement[] { secT571FieldElement8 }, this.withCompression);
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
        return new SecT571R1Point(this.curve, x, y.add(ecFieldElement), new ECFieldElement[] { ecFieldElement }, this.withCompression);
    }
}
