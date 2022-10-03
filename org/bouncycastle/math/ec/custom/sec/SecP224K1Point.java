package org.bouncycastle.math.ec.custom.sec;

import org.bouncycastle.math.raw.Nat;
import org.bouncycastle.math.raw.Nat224;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECPoint;

public class SecP224K1Point extends AbstractFp
{
    @Deprecated
    public SecP224K1Point(final ECCurve ecCurve, final ECFieldElement ecFieldElement, final ECFieldElement ecFieldElement2) {
        this(ecCurve, ecFieldElement, ecFieldElement2, false);
    }
    
    @Deprecated
    public SecP224K1Point(final ECCurve ecCurve, final ECFieldElement ecFieldElement, final ECFieldElement ecFieldElement2, final boolean withCompression) {
        super(ecCurve, ecFieldElement, ecFieldElement2);
        if (ecFieldElement == null != (ecFieldElement2 == null)) {
            throw new IllegalArgumentException("Exactly one of the field elements is null");
        }
        this.withCompression = withCompression;
    }
    
    SecP224K1Point(final ECCurve ecCurve, final ECFieldElement ecFieldElement, final ECFieldElement ecFieldElement2, final ECFieldElement[] array, final boolean withCompression) {
        super(ecCurve, ecFieldElement, ecFieldElement2, array);
        this.withCompression = withCompression;
    }
    
    @Override
    protected ECPoint detach() {
        return new SecP224K1Point(null, this.getAffineXCoord(), this.getAffineYCoord());
    }
    
    @Override
    public ECPoint add(final ECPoint ecPoint) {
        if (this.isInfinity()) {
            return ecPoint;
        }
        if (ecPoint.isInfinity()) {
            return this;
        }
        if (this == ecPoint) {
            return this.twice();
        }
        final ECCurve curve = this.getCurve();
        final SecP224K1FieldElement secP224K1FieldElement = (SecP224K1FieldElement)this.x;
        final SecP224K1FieldElement secP224K1FieldElement2 = (SecP224K1FieldElement)this.y;
        final SecP224K1FieldElement secP224K1FieldElement3 = (SecP224K1FieldElement)ecPoint.getXCoord();
        final SecP224K1FieldElement secP224K1FieldElement4 = (SecP224K1FieldElement)ecPoint.getYCoord();
        final SecP224K1FieldElement secP224K1FieldElement5 = (SecP224K1FieldElement)this.zs[0];
        final SecP224K1FieldElement secP224K1FieldElement6 = (SecP224K1FieldElement)ecPoint.getZCoord(0);
        final int[] ext = Nat224.createExt();
        final int[] create = Nat224.create();
        final int[] create2 = Nat224.create();
        final int[] create3 = Nat224.create();
        final boolean one = secP224K1FieldElement5.isOne();
        int[] x;
        int[] x2;
        if (one) {
            x = secP224K1FieldElement3.x;
            x2 = secP224K1FieldElement4.x;
        }
        else {
            x2 = create2;
            SecP224K1Field.square(secP224K1FieldElement5.x, x2);
            x = create;
            SecP224K1Field.multiply(x2, secP224K1FieldElement3.x, x);
            SecP224K1Field.multiply(x2, secP224K1FieldElement5.x, x2);
            SecP224K1Field.multiply(x2, secP224K1FieldElement4.x, x2);
        }
        final boolean one2 = secP224K1FieldElement6.isOne();
        int[] x3;
        int[] x4;
        if (one2) {
            x3 = secP224K1FieldElement.x;
            x4 = secP224K1FieldElement2.x;
        }
        else {
            x4 = create3;
            SecP224K1Field.square(secP224K1FieldElement6.x, x4);
            x3 = ext;
            SecP224K1Field.multiply(x4, secP224K1FieldElement.x, x3);
            SecP224K1Field.multiply(x4, secP224K1FieldElement6.x, x4);
            SecP224K1Field.multiply(x4, secP224K1FieldElement2.x, x4);
        }
        final int[] create4 = Nat224.create();
        SecP224K1Field.subtract(x3, x, create4);
        final int[] array = create;
        SecP224K1Field.subtract(x4, x2, array);
        if (!Nat224.isZero(create4)) {
            final int[] array2 = create2;
            SecP224K1Field.square(create4, array2);
            final int[] create5 = Nat224.create();
            SecP224K1Field.multiply(array2, create4, create5);
            final int[] array3 = create2;
            SecP224K1Field.multiply(array2, x3, array3);
            SecP224K1Field.negate(create5, create5);
            Nat224.mul(x4, create5, ext);
            SecP224K1Field.reduce32(Nat224.addBothTo(array3, array3, create5), create5);
            final SecP224K1FieldElement secP224K1FieldElement7 = new SecP224K1FieldElement(create3);
            SecP224K1Field.square(array, secP224K1FieldElement7.x);
            SecP224K1Field.subtract(secP224K1FieldElement7.x, create5, secP224K1FieldElement7.x);
            final SecP224K1FieldElement secP224K1FieldElement8 = new SecP224K1FieldElement(create5);
            SecP224K1Field.subtract(array3, secP224K1FieldElement7.x, secP224K1FieldElement8.x);
            SecP224K1Field.multiplyAddToExt(secP224K1FieldElement8.x, array, ext);
            SecP224K1Field.reduce(ext, secP224K1FieldElement8.x);
            final SecP224K1FieldElement secP224K1FieldElement9 = new SecP224K1FieldElement(create4);
            if (!one) {
                SecP224K1Field.multiply(secP224K1FieldElement9.x, secP224K1FieldElement5.x, secP224K1FieldElement9.x);
            }
            if (!one2) {
                SecP224K1Field.multiply(secP224K1FieldElement9.x, secP224K1FieldElement6.x, secP224K1FieldElement9.x);
            }
            return new SecP224K1Point(curve, secP224K1FieldElement7, secP224K1FieldElement8, new ECFieldElement[] { secP224K1FieldElement9 }, this.withCompression);
        }
        if (Nat224.isZero(array)) {
            return this.twice();
        }
        return curve.getInfinity();
    }
    
    @Override
    public ECPoint twice() {
        if (this.isInfinity()) {
            return this;
        }
        final ECCurve curve = this.getCurve();
        final SecP224K1FieldElement secP224K1FieldElement = (SecP224K1FieldElement)this.y;
        if (secP224K1FieldElement.isZero()) {
            return curve.getInfinity();
        }
        final SecP224K1FieldElement secP224K1FieldElement2 = (SecP224K1FieldElement)this.x;
        final SecP224K1FieldElement secP224K1FieldElement3 = (SecP224K1FieldElement)this.zs[0];
        final int[] create = Nat224.create();
        SecP224K1Field.square(secP224K1FieldElement.x, create);
        final int[] create2 = Nat224.create();
        SecP224K1Field.square(create, create2);
        final int[] create3 = Nat224.create();
        SecP224K1Field.square(secP224K1FieldElement2.x, create3);
        SecP224K1Field.reduce32(Nat224.addBothTo(create3, create3, create3), create3);
        final int[] array = create;
        SecP224K1Field.multiply(create, secP224K1FieldElement2.x, array);
        SecP224K1Field.reduce32(Nat.shiftUpBits(7, array, 2, 0), array);
        final int[] create4 = Nat224.create();
        SecP224K1Field.reduce32(Nat.shiftUpBits(7, create2, 3, 0, create4), create4);
        final SecP224K1FieldElement secP224K1FieldElement4 = new SecP224K1FieldElement(create2);
        SecP224K1Field.square(create3, secP224K1FieldElement4.x);
        SecP224K1Field.subtract(secP224K1FieldElement4.x, array, secP224K1FieldElement4.x);
        SecP224K1Field.subtract(secP224K1FieldElement4.x, array, secP224K1FieldElement4.x);
        final SecP224K1FieldElement secP224K1FieldElement5 = new SecP224K1FieldElement(array);
        SecP224K1Field.subtract(array, secP224K1FieldElement4.x, secP224K1FieldElement5.x);
        SecP224K1Field.multiply(secP224K1FieldElement5.x, create3, secP224K1FieldElement5.x);
        SecP224K1Field.subtract(secP224K1FieldElement5.x, create4, secP224K1FieldElement5.x);
        final SecP224K1FieldElement secP224K1FieldElement6 = new SecP224K1FieldElement(create3);
        SecP224K1Field.twice(secP224K1FieldElement.x, secP224K1FieldElement6.x);
        if (!secP224K1FieldElement3.isOne()) {
            SecP224K1Field.multiply(secP224K1FieldElement6.x, secP224K1FieldElement3.x, secP224K1FieldElement6.x);
        }
        return new SecP224K1Point(curve, secP224K1FieldElement4, secP224K1FieldElement5, new ECFieldElement[] { secP224K1FieldElement6 }, this.withCompression);
    }
    
    @Override
    public ECPoint twicePlus(final ECPoint ecPoint) {
        if (this == ecPoint) {
            return this.threeTimes();
        }
        if (this.isInfinity()) {
            return ecPoint;
        }
        if (ecPoint.isInfinity()) {
            return this.twice();
        }
        if (this.y.isZero()) {
            return ecPoint;
        }
        return this.twice().add(ecPoint);
    }
    
    @Override
    public ECPoint threeTimes() {
        if (this.isInfinity() || this.y.isZero()) {
            return this;
        }
        return this.twice().add(this);
    }
    
    @Override
    public ECPoint negate() {
        if (this.isInfinity()) {
            return this;
        }
        return new SecP224K1Point(this.curve, this.x, this.y.negate(), this.zs, this.withCompression);
    }
}
