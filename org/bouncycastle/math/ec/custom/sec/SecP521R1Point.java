package org.bouncycastle.math.ec.custom.sec;

import org.bouncycastle.math.raw.Nat;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECPoint;

public class SecP521R1Point extends AbstractFp
{
    @Deprecated
    public SecP521R1Point(final ECCurve ecCurve, final ECFieldElement ecFieldElement, final ECFieldElement ecFieldElement2) {
        this(ecCurve, ecFieldElement, ecFieldElement2, false);
    }
    
    @Deprecated
    public SecP521R1Point(final ECCurve ecCurve, final ECFieldElement ecFieldElement, final ECFieldElement ecFieldElement2, final boolean withCompression) {
        super(ecCurve, ecFieldElement, ecFieldElement2);
        if (ecFieldElement == null != (ecFieldElement2 == null)) {
            throw new IllegalArgumentException("Exactly one of the field elements is null");
        }
        this.withCompression = withCompression;
    }
    
    SecP521R1Point(final ECCurve ecCurve, final ECFieldElement ecFieldElement, final ECFieldElement ecFieldElement2, final ECFieldElement[] array, final boolean withCompression) {
        super(ecCurve, ecFieldElement, ecFieldElement2, array);
        this.withCompression = withCompression;
    }
    
    @Override
    protected ECPoint detach() {
        return new SecP521R1Point(null, this.getAffineXCoord(), this.getAffineYCoord());
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
        final SecP521R1FieldElement secP521R1FieldElement = (SecP521R1FieldElement)this.x;
        final SecP521R1FieldElement secP521R1FieldElement2 = (SecP521R1FieldElement)this.y;
        final SecP521R1FieldElement secP521R1FieldElement3 = (SecP521R1FieldElement)ecPoint.getXCoord();
        final SecP521R1FieldElement secP521R1FieldElement4 = (SecP521R1FieldElement)ecPoint.getYCoord();
        final SecP521R1FieldElement secP521R1FieldElement5 = (SecP521R1FieldElement)this.zs[0];
        final SecP521R1FieldElement secP521R1FieldElement6 = (SecP521R1FieldElement)ecPoint.getZCoord(0);
        final int[] create = Nat.create(17);
        final int[] create2 = Nat.create(17);
        final int[] create3 = Nat.create(17);
        final int[] create4 = Nat.create(17);
        final boolean one = secP521R1FieldElement5.isOne();
        int[] x;
        int[] x2;
        if (one) {
            x = secP521R1FieldElement3.x;
            x2 = secP521R1FieldElement4.x;
        }
        else {
            x2 = create3;
            SecP521R1Field.square(secP521R1FieldElement5.x, x2);
            x = create2;
            SecP521R1Field.multiply(x2, secP521R1FieldElement3.x, x);
            SecP521R1Field.multiply(x2, secP521R1FieldElement5.x, x2);
            SecP521R1Field.multiply(x2, secP521R1FieldElement4.x, x2);
        }
        final boolean one2 = secP521R1FieldElement6.isOne();
        int[] x3;
        int[] x4;
        if (one2) {
            x3 = secP521R1FieldElement.x;
            x4 = secP521R1FieldElement2.x;
        }
        else {
            x4 = create4;
            SecP521R1Field.square(secP521R1FieldElement6.x, x4);
            x3 = create;
            SecP521R1Field.multiply(x4, secP521R1FieldElement.x, x3);
            SecP521R1Field.multiply(x4, secP521R1FieldElement6.x, x4);
            SecP521R1Field.multiply(x4, secP521R1FieldElement2.x, x4);
        }
        final int[] create5 = Nat.create(17);
        SecP521R1Field.subtract(x3, x, create5);
        final int[] array = create2;
        SecP521R1Field.subtract(x4, x2, array);
        if (!Nat.isZero(17, create5)) {
            final int[] array2 = create3;
            SecP521R1Field.square(create5, array2);
            final int[] create6 = Nat.create(17);
            SecP521R1Field.multiply(array2, create5, create6);
            final int[] array3 = create3;
            SecP521R1Field.multiply(array2, x3, array3);
            SecP521R1Field.multiply(x4, create6, create);
            final SecP521R1FieldElement secP521R1FieldElement7 = new SecP521R1FieldElement(create4);
            SecP521R1Field.square(array, secP521R1FieldElement7.x);
            SecP521R1Field.add(secP521R1FieldElement7.x, create6, secP521R1FieldElement7.x);
            SecP521R1Field.subtract(secP521R1FieldElement7.x, array3, secP521R1FieldElement7.x);
            SecP521R1Field.subtract(secP521R1FieldElement7.x, array3, secP521R1FieldElement7.x);
            final SecP521R1FieldElement secP521R1FieldElement8 = new SecP521R1FieldElement(create6);
            SecP521R1Field.subtract(array3, secP521R1FieldElement7.x, secP521R1FieldElement8.x);
            SecP521R1Field.multiply(secP521R1FieldElement8.x, array, create2);
            SecP521R1Field.subtract(create2, create, secP521R1FieldElement8.x);
            final SecP521R1FieldElement secP521R1FieldElement9 = new SecP521R1FieldElement(create5);
            if (!one) {
                SecP521R1Field.multiply(secP521R1FieldElement9.x, secP521R1FieldElement5.x, secP521R1FieldElement9.x);
            }
            if (!one2) {
                SecP521R1Field.multiply(secP521R1FieldElement9.x, secP521R1FieldElement6.x, secP521R1FieldElement9.x);
            }
            return new SecP521R1Point(curve, secP521R1FieldElement7, secP521R1FieldElement8, new ECFieldElement[] { secP521R1FieldElement9 }, this.withCompression);
        }
        if (Nat.isZero(17, array)) {
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
        final SecP521R1FieldElement secP521R1FieldElement = (SecP521R1FieldElement)this.y;
        if (secP521R1FieldElement.isZero()) {
            return curve.getInfinity();
        }
        final SecP521R1FieldElement secP521R1FieldElement2 = (SecP521R1FieldElement)this.x;
        final SecP521R1FieldElement secP521R1FieldElement3 = (SecP521R1FieldElement)this.zs[0];
        final int[] create = Nat.create(17);
        final int[] create2 = Nat.create(17);
        final int[] create3 = Nat.create(17);
        SecP521R1Field.square(secP521R1FieldElement.x, create3);
        final int[] create4 = Nat.create(17);
        SecP521R1Field.square(create3, create4);
        final boolean one = secP521R1FieldElement3.isOne();
        int[] x = secP521R1FieldElement3.x;
        if (!one) {
            x = create2;
            SecP521R1Field.square(secP521R1FieldElement3.x, x);
        }
        SecP521R1Field.subtract(secP521R1FieldElement2.x, x, create);
        final int[] array = create2;
        SecP521R1Field.add(secP521R1FieldElement2.x, x, array);
        SecP521R1Field.multiply(array, create, array);
        Nat.addBothTo(17, array, array, array);
        SecP521R1Field.reduce23(array);
        final int[] array2 = create3;
        SecP521R1Field.multiply(create3, secP521R1FieldElement2.x, array2);
        Nat.shiftUpBits(17, array2, 2, 0);
        SecP521R1Field.reduce23(array2);
        Nat.shiftUpBits(17, create4, 3, 0, create);
        SecP521R1Field.reduce23(create);
        final SecP521R1FieldElement secP521R1FieldElement4 = new SecP521R1FieldElement(create4);
        SecP521R1Field.square(array, secP521R1FieldElement4.x);
        SecP521R1Field.subtract(secP521R1FieldElement4.x, array2, secP521R1FieldElement4.x);
        SecP521R1Field.subtract(secP521R1FieldElement4.x, array2, secP521R1FieldElement4.x);
        final SecP521R1FieldElement secP521R1FieldElement5 = new SecP521R1FieldElement(array2);
        SecP521R1Field.subtract(array2, secP521R1FieldElement4.x, secP521R1FieldElement5.x);
        SecP521R1Field.multiply(secP521R1FieldElement5.x, array, secP521R1FieldElement5.x);
        SecP521R1Field.subtract(secP521R1FieldElement5.x, create, secP521R1FieldElement5.x);
        final SecP521R1FieldElement secP521R1FieldElement6 = new SecP521R1FieldElement(array);
        SecP521R1Field.twice(secP521R1FieldElement.x, secP521R1FieldElement6.x);
        if (!one) {
            SecP521R1Field.multiply(secP521R1FieldElement6.x, secP521R1FieldElement3.x, secP521R1FieldElement6.x);
        }
        return new SecP521R1Point(curve, secP521R1FieldElement4, secP521R1FieldElement5, new ECFieldElement[] { secP521R1FieldElement6 }, this.withCompression);
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
    
    protected ECFieldElement two(final ECFieldElement ecFieldElement) {
        return ecFieldElement.add(ecFieldElement);
    }
    
    protected ECFieldElement three(final ECFieldElement ecFieldElement) {
        return this.two(ecFieldElement).add(ecFieldElement);
    }
    
    protected ECFieldElement four(final ECFieldElement ecFieldElement) {
        return this.two(this.two(ecFieldElement));
    }
    
    protected ECFieldElement eight(final ECFieldElement ecFieldElement) {
        return this.four(this.two(ecFieldElement));
    }
    
    protected ECFieldElement doubleProductFromSquares(final ECFieldElement ecFieldElement, final ECFieldElement ecFieldElement2, final ECFieldElement ecFieldElement3, final ECFieldElement ecFieldElement4) {
        return ecFieldElement.add(ecFieldElement2).square().subtract(ecFieldElement3).subtract(ecFieldElement4);
    }
    
    @Override
    public ECPoint negate() {
        if (this.isInfinity()) {
            return this;
        }
        return new SecP521R1Point(this.curve, this.x, this.y.negate(), this.zs, this.withCompression);
    }
}
