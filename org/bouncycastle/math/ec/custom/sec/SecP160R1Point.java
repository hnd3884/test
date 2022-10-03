package org.bouncycastle.math.ec.custom.sec;

import org.bouncycastle.math.raw.Nat;
import org.bouncycastle.math.raw.Nat160;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECPoint;

public class SecP160R1Point extends AbstractFp
{
    @Deprecated
    public SecP160R1Point(final ECCurve ecCurve, final ECFieldElement ecFieldElement, final ECFieldElement ecFieldElement2) {
        this(ecCurve, ecFieldElement, ecFieldElement2, false);
    }
    
    @Deprecated
    public SecP160R1Point(final ECCurve ecCurve, final ECFieldElement ecFieldElement, final ECFieldElement ecFieldElement2, final boolean withCompression) {
        super(ecCurve, ecFieldElement, ecFieldElement2);
        if (ecFieldElement == null != (ecFieldElement2 == null)) {
            throw new IllegalArgumentException("Exactly one of the field elements is null");
        }
        this.withCompression = withCompression;
    }
    
    SecP160R1Point(final ECCurve ecCurve, final ECFieldElement ecFieldElement, final ECFieldElement ecFieldElement2, final ECFieldElement[] array, final boolean withCompression) {
        super(ecCurve, ecFieldElement, ecFieldElement2, array);
        this.withCompression = withCompression;
    }
    
    @Override
    protected ECPoint detach() {
        return new SecP160R1Point(null, this.getAffineXCoord(), this.getAffineYCoord());
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
        final SecP160R1FieldElement secP160R1FieldElement = (SecP160R1FieldElement)this.x;
        final SecP160R1FieldElement secP160R1FieldElement2 = (SecP160R1FieldElement)this.y;
        final SecP160R1FieldElement secP160R1FieldElement3 = (SecP160R1FieldElement)ecPoint.getXCoord();
        final SecP160R1FieldElement secP160R1FieldElement4 = (SecP160R1FieldElement)ecPoint.getYCoord();
        final SecP160R1FieldElement secP160R1FieldElement5 = (SecP160R1FieldElement)this.zs[0];
        final SecP160R1FieldElement secP160R1FieldElement6 = (SecP160R1FieldElement)ecPoint.getZCoord(0);
        final int[] ext = Nat160.createExt();
        final int[] create = Nat160.create();
        final int[] create2 = Nat160.create();
        final int[] create3 = Nat160.create();
        final boolean one = secP160R1FieldElement5.isOne();
        int[] x;
        int[] x2;
        if (one) {
            x = secP160R1FieldElement3.x;
            x2 = secP160R1FieldElement4.x;
        }
        else {
            x2 = create2;
            SecP160R1Field.square(secP160R1FieldElement5.x, x2);
            x = create;
            SecP160R1Field.multiply(x2, secP160R1FieldElement3.x, x);
            SecP160R1Field.multiply(x2, secP160R1FieldElement5.x, x2);
            SecP160R1Field.multiply(x2, secP160R1FieldElement4.x, x2);
        }
        final boolean one2 = secP160R1FieldElement6.isOne();
        int[] x3;
        int[] x4;
        if (one2) {
            x3 = secP160R1FieldElement.x;
            x4 = secP160R1FieldElement2.x;
        }
        else {
            x4 = create3;
            SecP160R1Field.square(secP160R1FieldElement6.x, x4);
            x3 = ext;
            SecP160R1Field.multiply(x4, secP160R1FieldElement.x, x3);
            SecP160R1Field.multiply(x4, secP160R1FieldElement6.x, x4);
            SecP160R1Field.multiply(x4, secP160R1FieldElement2.x, x4);
        }
        final int[] create4 = Nat160.create();
        SecP160R1Field.subtract(x3, x, create4);
        final int[] array = create;
        SecP160R1Field.subtract(x4, x2, array);
        if (!Nat160.isZero(create4)) {
            final int[] array2 = create2;
            SecP160R1Field.square(create4, array2);
            final int[] create5 = Nat160.create();
            SecP160R1Field.multiply(array2, create4, create5);
            final int[] array3 = create2;
            SecP160R1Field.multiply(array2, x3, array3);
            SecP160R1Field.negate(create5, create5);
            Nat160.mul(x4, create5, ext);
            SecP160R1Field.reduce32(Nat160.addBothTo(array3, array3, create5), create5);
            final SecP160R1FieldElement secP160R1FieldElement7 = new SecP160R1FieldElement(create3);
            SecP160R1Field.square(array, secP160R1FieldElement7.x);
            SecP160R1Field.subtract(secP160R1FieldElement7.x, create5, secP160R1FieldElement7.x);
            final SecP160R1FieldElement secP160R1FieldElement8 = new SecP160R1FieldElement(create5);
            SecP160R1Field.subtract(array3, secP160R1FieldElement7.x, secP160R1FieldElement8.x);
            SecP160R1Field.multiplyAddToExt(secP160R1FieldElement8.x, array, ext);
            SecP160R1Field.reduce(ext, secP160R1FieldElement8.x);
            final SecP160R1FieldElement secP160R1FieldElement9 = new SecP160R1FieldElement(create4);
            if (!one) {
                SecP160R1Field.multiply(secP160R1FieldElement9.x, secP160R1FieldElement5.x, secP160R1FieldElement9.x);
            }
            if (!one2) {
                SecP160R1Field.multiply(secP160R1FieldElement9.x, secP160R1FieldElement6.x, secP160R1FieldElement9.x);
            }
            return new SecP160R1Point(curve, secP160R1FieldElement7, secP160R1FieldElement8, new ECFieldElement[] { secP160R1FieldElement9 }, this.withCompression);
        }
        if (Nat160.isZero(array)) {
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
        final SecP160R1FieldElement secP160R1FieldElement = (SecP160R1FieldElement)this.y;
        if (secP160R1FieldElement.isZero()) {
            return curve.getInfinity();
        }
        final SecP160R1FieldElement secP160R1FieldElement2 = (SecP160R1FieldElement)this.x;
        final SecP160R1FieldElement secP160R1FieldElement3 = (SecP160R1FieldElement)this.zs[0];
        final int[] create = Nat160.create();
        final int[] create2 = Nat160.create();
        final int[] create3 = Nat160.create();
        SecP160R1Field.square(secP160R1FieldElement.x, create3);
        final int[] create4 = Nat160.create();
        SecP160R1Field.square(create3, create4);
        final boolean one = secP160R1FieldElement3.isOne();
        int[] x = secP160R1FieldElement3.x;
        if (!one) {
            x = create2;
            SecP160R1Field.square(secP160R1FieldElement3.x, x);
        }
        SecP160R1Field.subtract(secP160R1FieldElement2.x, x, create);
        final int[] array = create2;
        SecP160R1Field.add(secP160R1FieldElement2.x, x, array);
        SecP160R1Field.multiply(array, create, array);
        SecP160R1Field.reduce32(Nat160.addBothTo(array, array, array), array);
        final int[] array2 = create3;
        SecP160R1Field.multiply(create3, secP160R1FieldElement2.x, array2);
        SecP160R1Field.reduce32(Nat.shiftUpBits(5, array2, 2, 0), array2);
        SecP160R1Field.reduce32(Nat.shiftUpBits(5, create4, 3, 0, create), create);
        final SecP160R1FieldElement secP160R1FieldElement4 = new SecP160R1FieldElement(create4);
        SecP160R1Field.square(array, secP160R1FieldElement4.x);
        SecP160R1Field.subtract(secP160R1FieldElement4.x, array2, secP160R1FieldElement4.x);
        SecP160R1Field.subtract(secP160R1FieldElement4.x, array2, secP160R1FieldElement4.x);
        final SecP160R1FieldElement secP160R1FieldElement5 = new SecP160R1FieldElement(array2);
        SecP160R1Field.subtract(array2, secP160R1FieldElement4.x, secP160R1FieldElement5.x);
        SecP160R1Field.multiply(secP160R1FieldElement5.x, array, secP160R1FieldElement5.x);
        SecP160R1Field.subtract(secP160R1FieldElement5.x, create, secP160R1FieldElement5.x);
        final SecP160R1FieldElement secP160R1FieldElement6 = new SecP160R1FieldElement(array);
        SecP160R1Field.twice(secP160R1FieldElement.x, secP160R1FieldElement6.x);
        if (!one) {
            SecP160R1Field.multiply(secP160R1FieldElement6.x, secP160R1FieldElement3.x, secP160R1FieldElement6.x);
        }
        return new SecP160R1Point(curve, secP160R1FieldElement4, secP160R1FieldElement5, new ECFieldElement[] { secP160R1FieldElement6 }, this.withCompression);
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
        return new SecP160R1Point(this.curve, this.x, this.y.negate(), this.zs, this.withCompression);
    }
}
