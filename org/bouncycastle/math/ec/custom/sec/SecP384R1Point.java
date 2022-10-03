package org.bouncycastle.math.ec.custom.sec;

import org.bouncycastle.math.raw.Nat384;
import org.bouncycastle.math.raw.Nat;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECPoint;

public class SecP384R1Point extends AbstractFp
{
    @Deprecated
    public SecP384R1Point(final ECCurve ecCurve, final ECFieldElement ecFieldElement, final ECFieldElement ecFieldElement2) {
        this(ecCurve, ecFieldElement, ecFieldElement2, false);
    }
    
    @Deprecated
    public SecP384R1Point(final ECCurve ecCurve, final ECFieldElement ecFieldElement, final ECFieldElement ecFieldElement2, final boolean withCompression) {
        super(ecCurve, ecFieldElement, ecFieldElement2);
        if (ecFieldElement == null != (ecFieldElement2 == null)) {
            throw new IllegalArgumentException("Exactly one of the field elements is null");
        }
        this.withCompression = withCompression;
    }
    
    SecP384R1Point(final ECCurve ecCurve, final ECFieldElement ecFieldElement, final ECFieldElement ecFieldElement2, final ECFieldElement[] array, final boolean withCompression) {
        super(ecCurve, ecFieldElement, ecFieldElement2, array);
        this.withCompression = withCompression;
    }
    
    @Override
    protected ECPoint detach() {
        return new SecP384R1Point(null, this.getAffineXCoord(), this.getAffineYCoord());
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
        final SecP384R1FieldElement secP384R1FieldElement = (SecP384R1FieldElement)this.x;
        final SecP384R1FieldElement secP384R1FieldElement2 = (SecP384R1FieldElement)this.y;
        final SecP384R1FieldElement secP384R1FieldElement3 = (SecP384R1FieldElement)ecPoint.getXCoord();
        final SecP384R1FieldElement secP384R1FieldElement4 = (SecP384R1FieldElement)ecPoint.getYCoord();
        final SecP384R1FieldElement secP384R1FieldElement5 = (SecP384R1FieldElement)this.zs[0];
        final SecP384R1FieldElement secP384R1FieldElement6 = (SecP384R1FieldElement)ecPoint.getZCoord(0);
        final int[] create = Nat.create(24);
        final int[] create2 = Nat.create(24);
        final int[] create3 = Nat.create(12);
        final int[] create4 = Nat.create(12);
        final boolean one = secP384R1FieldElement5.isOne();
        int[] x;
        int[] x2;
        if (one) {
            x = secP384R1FieldElement3.x;
            x2 = secP384R1FieldElement4.x;
        }
        else {
            x2 = create3;
            SecP384R1Field.square(secP384R1FieldElement5.x, x2);
            x = create2;
            SecP384R1Field.multiply(x2, secP384R1FieldElement3.x, x);
            SecP384R1Field.multiply(x2, secP384R1FieldElement5.x, x2);
            SecP384R1Field.multiply(x2, secP384R1FieldElement4.x, x2);
        }
        final boolean one2 = secP384R1FieldElement6.isOne();
        int[] x3;
        int[] x4;
        if (one2) {
            x3 = secP384R1FieldElement.x;
            x4 = secP384R1FieldElement2.x;
        }
        else {
            x4 = create4;
            SecP384R1Field.square(secP384R1FieldElement6.x, x4);
            x3 = create;
            SecP384R1Field.multiply(x4, secP384R1FieldElement.x, x3);
            SecP384R1Field.multiply(x4, secP384R1FieldElement6.x, x4);
            SecP384R1Field.multiply(x4, secP384R1FieldElement2.x, x4);
        }
        final int[] create5 = Nat.create(12);
        SecP384R1Field.subtract(x3, x, create5);
        final int[] create6 = Nat.create(12);
        SecP384R1Field.subtract(x4, x2, create6);
        if (!Nat.isZero(12, create5)) {
            final int[] array = create3;
            SecP384R1Field.square(create5, array);
            final int[] create7 = Nat.create(12);
            SecP384R1Field.multiply(array, create5, create7);
            final int[] array2 = create3;
            SecP384R1Field.multiply(array, x3, array2);
            SecP384R1Field.negate(create7, create7);
            Nat384.mul(x4, create7, create);
            SecP384R1Field.reduce32(Nat.addBothTo(12, array2, array2, create7), create7);
            final SecP384R1FieldElement secP384R1FieldElement7 = new SecP384R1FieldElement(create4);
            SecP384R1Field.square(create6, secP384R1FieldElement7.x);
            SecP384R1Field.subtract(secP384R1FieldElement7.x, create7, secP384R1FieldElement7.x);
            final SecP384R1FieldElement secP384R1FieldElement8 = new SecP384R1FieldElement(create7);
            SecP384R1Field.subtract(array2, secP384R1FieldElement7.x, secP384R1FieldElement8.x);
            Nat384.mul(secP384R1FieldElement8.x, create6, create2);
            SecP384R1Field.addExt(create, create2, create);
            SecP384R1Field.reduce(create, secP384R1FieldElement8.x);
            final SecP384R1FieldElement secP384R1FieldElement9 = new SecP384R1FieldElement(create5);
            if (!one) {
                SecP384R1Field.multiply(secP384R1FieldElement9.x, secP384R1FieldElement5.x, secP384R1FieldElement9.x);
            }
            if (!one2) {
                SecP384R1Field.multiply(secP384R1FieldElement9.x, secP384R1FieldElement6.x, secP384R1FieldElement9.x);
            }
            return new SecP384R1Point(curve, secP384R1FieldElement7, secP384R1FieldElement8, new ECFieldElement[] { secP384R1FieldElement9 }, this.withCompression);
        }
        if (Nat.isZero(12, create6)) {
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
        final SecP384R1FieldElement secP384R1FieldElement = (SecP384R1FieldElement)this.y;
        if (secP384R1FieldElement.isZero()) {
            return curve.getInfinity();
        }
        final SecP384R1FieldElement secP384R1FieldElement2 = (SecP384R1FieldElement)this.x;
        final SecP384R1FieldElement secP384R1FieldElement3 = (SecP384R1FieldElement)this.zs[0];
        final int[] create = Nat.create(12);
        final int[] create2 = Nat.create(12);
        final int[] create3 = Nat.create(12);
        SecP384R1Field.square(secP384R1FieldElement.x, create3);
        final int[] create4 = Nat.create(12);
        SecP384R1Field.square(create3, create4);
        final boolean one = secP384R1FieldElement3.isOne();
        int[] x = secP384R1FieldElement3.x;
        if (!one) {
            x = create2;
            SecP384R1Field.square(secP384R1FieldElement3.x, x);
        }
        SecP384R1Field.subtract(secP384R1FieldElement2.x, x, create);
        final int[] array = create2;
        SecP384R1Field.add(secP384R1FieldElement2.x, x, array);
        SecP384R1Field.multiply(array, create, array);
        SecP384R1Field.reduce32(Nat.addBothTo(12, array, array, array), array);
        final int[] array2 = create3;
        SecP384R1Field.multiply(create3, secP384R1FieldElement2.x, array2);
        SecP384R1Field.reduce32(Nat.shiftUpBits(12, array2, 2, 0), array2);
        SecP384R1Field.reduce32(Nat.shiftUpBits(12, create4, 3, 0, create), create);
        final SecP384R1FieldElement secP384R1FieldElement4 = new SecP384R1FieldElement(create4);
        SecP384R1Field.square(array, secP384R1FieldElement4.x);
        SecP384R1Field.subtract(secP384R1FieldElement4.x, array2, secP384R1FieldElement4.x);
        SecP384R1Field.subtract(secP384R1FieldElement4.x, array2, secP384R1FieldElement4.x);
        final SecP384R1FieldElement secP384R1FieldElement5 = new SecP384R1FieldElement(array2);
        SecP384R1Field.subtract(array2, secP384R1FieldElement4.x, secP384R1FieldElement5.x);
        SecP384R1Field.multiply(secP384R1FieldElement5.x, array, secP384R1FieldElement5.x);
        SecP384R1Field.subtract(secP384R1FieldElement5.x, create, secP384R1FieldElement5.x);
        final SecP384R1FieldElement secP384R1FieldElement6 = new SecP384R1FieldElement(array);
        SecP384R1Field.twice(secP384R1FieldElement.x, secP384R1FieldElement6.x);
        if (!one) {
            SecP384R1Field.multiply(secP384R1FieldElement6.x, secP384R1FieldElement3.x, secP384R1FieldElement6.x);
        }
        return new SecP384R1Point(curve, secP384R1FieldElement4, secP384R1FieldElement5, new ECFieldElement[] { secP384R1FieldElement6 }, this.withCompression);
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
        return new SecP384R1Point(this.curve, this.x, this.y.negate(), this.zs, this.withCompression);
    }
}
