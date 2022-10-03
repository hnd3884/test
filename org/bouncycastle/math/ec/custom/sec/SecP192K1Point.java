package org.bouncycastle.math.ec.custom.sec;

import org.bouncycastle.math.raw.Nat;
import org.bouncycastle.math.raw.Nat192;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECPoint;

public class SecP192K1Point extends AbstractFp
{
    @Deprecated
    public SecP192K1Point(final ECCurve ecCurve, final ECFieldElement ecFieldElement, final ECFieldElement ecFieldElement2) {
        this(ecCurve, ecFieldElement, ecFieldElement2, false);
    }
    
    @Deprecated
    public SecP192K1Point(final ECCurve ecCurve, final ECFieldElement ecFieldElement, final ECFieldElement ecFieldElement2, final boolean withCompression) {
        super(ecCurve, ecFieldElement, ecFieldElement2);
        if (ecFieldElement == null != (ecFieldElement2 == null)) {
            throw new IllegalArgumentException("Exactly one of the field elements is null");
        }
        this.withCompression = withCompression;
    }
    
    SecP192K1Point(final ECCurve ecCurve, final ECFieldElement ecFieldElement, final ECFieldElement ecFieldElement2, final ECFieldElement[] array, final boolean withCompression) {
        super(ecCurve, ecFieldElement, ecFieldElement2, array);
        this.withCompression = withCompression;
    }
    
    @Override
    protected ECPoint detach() {
        return new SecP192K1Point(null, this.getAffineXCoord(), this.getAffineYCoord());
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
        final SecP192K1FieldElement secP192K1FieldElement = (SecP192K1FieldElement)this.x;
        final SecP192K1FieldElement secP192K1FieldElement2 = (SecP192K1FieldElement)this.y;
        final SecP192K1FieldElement secP192K1FieldElement3 = (SecP192K1FieldElement)ecPoint.getXCoord();
        final SecP192K1FieldElement secP192K1FieldElement4 = (SecP192K1FieldElement)ecPoint.getYCoord();
        final SecP192K1FieldElement secP192K1FieldElement5 = (SecP192K1FieldElement)this.zs[0];
        final SecP192K1FieldElement secP192K1FieldElement6 = (SecP192K1FieldElement)ecPoint.getZCoord(0);
        final int[] ext = Nat192.createExt();
        final int[] create = Nat192.create();
        final int[] create2 = Nat192.create();
        final int[] create3 = Nat192.create();
        final boolean one = secP192K1FieldElement5.isOne();
        int[] x;
        int[] x2;
        if (one) {
            x = secP192K1FieldElement3.x;
            x2 = secP192K1FieldElement4.x;
        }
        else {
            x2 = create2;
            SecP192K1Field.square(secP192K1FieldElement5.x, x2);
            x = create;
            SecP192K1Field.multiply(x2, secP192K1FieldElement3.x, x);
            SecP192K1Field.multiply(x2, secP192K1FieldElement5.x, x2);
            SecP192K1Field.multiply(x2, secP192K1FieldElement4.x, x2);
        }
        final boolean one2 = secP192K1FieldElement6.isOne();
        int[] x3;
        int[] x4;
        if (one2) {
            x3 = secP192K1FieldElement.x;
            x4 = secP192K1FieldElement2.x;
        }
        else {
            x4 = create3;
            SecP192K1Field.square(secP192K1FieldElement6.x, x4);
            x3 = ext;
            SecP192K1Field.multiply(x4, secP192K1FieldElement.x, x3);
            SecP192K1Field.multiply(x4, secP192K1FieldElement6.x, x4);
            SecP192K1Field.multiply(x4, secP192K1FieldElement2.x, x4);
        }
        final int[] create4 = Nat192.create();
        SecP192K1Field.subtract(x3, x, create4);
        final int[] array = create;
        SecP192K1Field.subtract(x4, x2, array);
        if (!Nat192.isZero(create4)) {
            final int[] array2 = create2;
            SecP192K1Field.square(create4, array2);
            final int[] create5 = Nat192.create();
            SecP192K1Field.multiply(array2, create4, create5);
            final int[] array3 = create2;
            SecP192K1Field.multiply(array2, x3, array3);
            SecP192K1Field.negate(create5, create5);
            Nat192.mul(x4, create5, ext);
            SecP192K1Field.reduce32(Nat192.addBothTo(array3, array3, create5), create5);
            final SecP192K1FieldElement secP192K1FieldElement7 = new SecP192K1FieldElement(create3);
            SecP192K1Field.square(array, secP192K1FieldElement7.x);
            SecP192K1Field.subtract(secP192K1FieldElement7.x, create5, secP192K1FieldElement7.x);
            final SecP192K1FieldElement secP192K1FieldElement8 = new SecP192K1FieldElement(create5);
            SecP192K1Field.subtract(array3, secP192K1FieldElement7.x, secP192K1FieldElement8.x);
            SecP192K1Field.multiplyAddToExt(secP192K1FieldElement8.x, array, ext);
            SecP192K1Field.reduce(ext, secP192K1FieldElement8.x);
            final SecP192K1FieldElement secP192K1FieldElement9 = new SecP192K1FieldElement(create4);
            if (!one) {
                SecP192K1Field.multiply(secP192K1FieldElement9.x, secP192K1FieldElement5.x, secP192K1FieldElement9.x);
            }
            if (!one2) {
                SecP192K1Field.multiply(secP192K1FieldElement9.x, secP192K1FieldElement6.x, secP192K1FieldElement9.x);
            }
            return new SecP192K1Point(curve, secP192K1FieldElement7, secP192K1FieldElement8, new ECFieldElement[] { secP192K1FieldElement9 }, this.withCompression);
        }
        if (Nat192.isZero(array)) {
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
        final SecP192K1FieldElement secP192K1FieldElement = (SecP192K1FieldElement)this.y;
        if (secP192K1FieldElement.isZero()) {
            return curve.getInfinity();
        }
        final SecP192K1FieldElement secP192K1FieldElement2 = (SecP192K1FieldElement)this.x;
        final SecP192K1FieldElement secP192K1FieldElement3 = (SecP192K1FieldElement)this.zs[0];
        final int[] create = Nat192.create();
        SecP192K1Field.square(secP192K1FieldElement.x, create);
        final int[] create2 = Nat192.create();
        SecP192K1Field.square(create, create2);
        final int[] create3 = Nat192.create();
        SecP192K1Field.square(secP192K1FieldElement2.x, create3);
        SecP192K1Field.reduce32(Nat192.addBothTo(create3, create3, create3), create3);
        final int[] array = create;
        SecP192K1Field.multiply(create, secP192K1FieldElement2.x, array);
        SecP192K1Field.reduce32(Nat.shiftUpBits(6, array, 2, 0), array);
        final int[] create4 = Nat192.create();
        SecP192K1Field.reduce32(Nat.shiftUpBits(6, create2, 3, 0, create4), create4);
        final SecP192K1FieldElement secP192K1FieldElement4 = new SecP192K1FieldElement(create2);
        SecP192K1Field.square(create3, secP192K1FieldElement4.x);
        SecP192K1Field.subtract(secP192K1FieldElement4.x, array, secP192K1FieldElement4.x);
        SecP192K1Field.subtract(secP192K1FieldElement4.x, array, secP192K1FieldElement4.x);
        final SecP192K1FieldElement secP192K1FieldElement5 = new SecP192K1FieldElement(array);
        SecP192K1Field.subtract(array, secP192K1FieldElement4.x, secP192K1FieldElement5.x);
        SecP192K1Field.multiply(secP192K1FieldElement5.x, create3, secP192K1FieldElement5.x);
        SecP192K1Field.subtract(secP192K1FieldElement5.x, create4, secP192K1FieldElement5.x);
        final SecP192K1FieldElement secP192K1FieldElement6 = new SecP192K1FieldElement(create3);
        SecP192K1Field.twice(secP192K1FieldElement.x, secP192K1FieldElement6.x);
        if (!secP192K1FieldElement3.isOne()) {
            SecP192K1Field.multiply(secP192K1FieldElement6.x, secP192K1FieldElement3.x, secP192K1FieldElement6.x);
        }
        return new SecP192K1Point(curve, secP192K1FieldElement4, secP192K1FieldElement5, new ECFieldElement[] { secP192K1FieldElement6 }, this.withCompression);
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
        return new SecP192K1Point(this.curve, this.x, this.y.negate(), this.zs, this.withCompression);
    }
}
