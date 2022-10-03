package org.bouncycastle.math.ec.custom.djb;

import org.bouncycastle.math.raw.Nat256;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECPoint;

public class Curve25519Point extends AbstractFp
{
    @Deprecated
    public Curve25519Point(final ECCurve ecCurve, final ECFieldElement ecFieldElement, final ECFieldElement ecFieldElement2) {
        this(ecCurve, ecFieldElement, ecFieldElement2, false);
    }
    
    @Deprecated
    public Curve25519Point(final ECCurve ecCurve, final ECFieldElement ecFieldElement, final ECFieldElement ecFieldElement2, final boolean withCompression) {
        super(ecCurve, ecFieldElement, ecFieldElement2);
        if (ecFieldElement == null != (ecFieldElement2 == null)) {
            throw new IllegalArgumentException("Exactly one of the field elements is null");
        }
        this.withCompression = withCompression;
    }
    
    Curve25519Point(final ECCurve ecCurve, final ECFieldElement ecFieldElement, final ECFieldElement ecFieldElement2, final ECFieldElement[] array, final boolean withCompression) {
        super(ecCurve, ecFieldElement, ecFieldElement2, array);
        this.withCompression = withCompression;
    }
    
    @Override
    protected ECPoint detach() {
        return new Curve25519Point(null, this.getAffineXCoord(), this.getAffineYCoord());
    }
    
    @Override
    public ECFieldElement getZCoord(final int n) {
        if (n == 1) {
            return this.getJacobianModifiedW();
        }
        return super.getZCoord(n);
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
        final Curve25519FieldElement curve25519FieldElement = (Curve25519FieldElement)this.x;
        final Curve25519FieldElement curve25519FieldElement2 = (Curve25519FieldElement)this.y;
        final Curve25519FieldElement curve25519FieldElement3 = (Curve25519FieldElement)this.zs[0];
        final Curve25519FieldElement curve25519FieldElement4 = (Curve25519FieldElement)ecPoint.getXCoord();
        final Curve25519FieldElement curve25519FieldElement5 = (Curve25519FieldElement)ecPoint.getYCoord();
        final Curve25519FieldElement curve25519FieldElement6 = (Curve25519FieldElement)ecPoint.getZCoord(0);
        final int[] ext = Nat256.createExt();
        final int[] create = Nat256.create();
        final int[] create2 = Nat256.create();
        final int[] create3 = Nat256.create();
        final boolean one = curve25519FieldElement3.isOne();
        int[] x;
        int[] x2;
        if (one) {
            x = curve25519FieldElement4.x;
            x2 = curve25519FieldElement5.x;
        }
        else {
            x2 = create2;
            Curve25519Field.square(curve25519FieldElement3.x, x2);
            x = create;
            Curve25519Field.multiply(x2, curve25519FieldElement4.x, x);
            Curve25519Field.multiply(x2, curve25519FieldElement3.x, x2);
            Curve25519Field.multiply(x2, curve25519FieldElement5.x, x2);
        }
        final boolean one2 = curve25519FieldElement6.isOne();
        int[] x3;
        int[] x4;
        if (one2) {
            x3 = curve25519FieldElement.x;
            x4 = curve25519FieldElement2.x;
        }
        else {
            x4 = create3;
            Curve25519Field.square(curve25519FieldElement6.x, x4);
            x3 = ext;
            Curve25519Field.multiply(x4, curve25519FieldElement.x, x3);
            Curve25519Field.multiply(x4, curve25519FieldElement6.x, x4);
            Curve25519Field.multiply(x4, curve25519FieldElement2.x, x4);
        }
        final int[] create4 = Nat256.create();
        Curve25519Field.subtract(x3, x, create4);
        final int[] array = create;
        Curve25519Field.subtract(x4, x2, array);
        if (!Nat256.isZero(create4)) {
            final int[] create5 = Nat256.create();
            Curve25519Field.square(create4, create5);
            final int[] create6 = Nat256.create();
            Curve25519Field.multiply(create5, create4, create6);
            final int[] array2 = create2;
            Curve25519Field.multiply(create5, x3, array2);
            Curve25519Field.negate(create6, create6);
            Nat256.mul(x4, create6, ext);
            Curve25519Field.reduce27(Nat256.addBothTo(array2, array2, create6), create6);
            final Curve25519FieldElement curve25519FieldElement7 = new Curve25519FieldElement(create3);
            Curve25519Field.square(array, curve25519FieldElement7.x);
            Curve25519Field.subtract(curve25519FieldElement7.x, create6, curve25519FieldElement7.x);
            final Curve25519FieldElement curve25519FieldElement8 = new Curve25519FieldElement(create6);
            Curve25519Field.subtract(array2, curve25519FieldElement7.x, curve25519FieldElement8.x);
            Curve25519Field.multiplyAddToExt(curve25519FieldElement8.x, array, ext);
            Curve25519Field.reduce(ext, curve25519FieldElement8.x);
            final Curve25519FieldElement curve25519FieldElement9 = new Curve25519FieldElement(create4);
            if (!one) {
                Curve25519Field.multiply(curve25519FieldElement9.x, curve25519FieldElement3.x, curve25519FieldElement9.x);
            }
            if (!one2) {
                Curve25519Field.multiply(curve25519FieldElement9.x, curve25519FieldElement6.x, curve25519FieldElement9.x);
            }
            return new Curve25519Point(curve, curve25519FieldElement7, curve25519FieldElement8, new ECFieldElement[] { curve25519FieldElement9, this.calculateJacobianModifiedW(curve25519FieldElement9, (int[])((one && one2) ? create5 : null)) }, this.withCompression);
        }
        if (Nat256.isZero(array)) {
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
        if (this.y.isZero()) {
            return curve.getInfinity();
        }
        return this.twiceJacobianModified(true);
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
        return this.twiceJacobianModified(false).add(ecPoint);
    }
    
    @Override
    public ECPoint threeTimes() {
        if (this.isInfinity()) {
            return this;
        }
        if (this.y.isZero()) {
            return this;
        }
        return this.twiceJacobianModified(false).add(this);
    }
    
    @Override
    public ECPoint negate() {
        if (this.isInfinity()) {
            return this;
        }
        return new Curve25519Point(this.getCurve(), this.x, this.y.negate(), this.zs, this.withCompression);
    }
    
    protected Curve25519FieldElement calculateJacobianModifiedW(final Curve25519FieldElement curve25519FieldElement, int[] x) {
        final Curve25519FieldElement curve25519FieldElement2 = (Curve25519FieldElement)this.getCurve().getA();
        if (curve25519FieldElement.isOne()) {
            return curve25519FieldElement2;
        }
        final Curve25519FieldElement curve25519FieldElement3 = new Curve25519FieldElement();
        if (x == null) {
            x = curve25519FieldElement3.x;
            Curve25519Field.square(curve25519FieldElement.x, x);
        }
        Curve25519Field.square(x, curve25519FieldElement3.x);
        Curve25519Field.multiply(curve25519FieldElement3.x, curve25519FieldElement2.x, curve25519FieldElement3.x);
        return curve25519FieldElement3;
    }
    
    protected Curve25519FieldElement getJacobianModifiedW() {
        ECFieldElement ecFieldElement = this.zs[1];
        if (ecFieldElement == null) {
            ecFieldElement = (this.zs[1] = this.calculateJacobianModifiedW((Curve25519FieldElement)this.zs[0], null));
        }
        return (Curve25519FieldElement)ecFieldElement;
    }
    
    protected Curve25519Point twiceJacobianModified(final boolean b) {
        final Curve25519FieldElement curve25519FieldElement = (Curve25519FieldElement)this.x;
        final Curve25519FieldElement curve25519FieldElement2 = (Curve25519FieldElement)this.y;
        final Curve25519FieldElement curve25519FieldElement3 = (Curve25519FieldElement)this.zs[0];
        final Curve25519FieldElement jacobianModifiedW = this.getJacobianModifiedW();
        final int[] create = Nat256.create();
        Curve25519Field.square(curve25519FieldElement.x, create);
        Curve25519Field.reduce27(Nat256.addBothTo(create, create, create) + Nat256.addTo(jacobianModifiedW.x, create), create);
        final int[] create2 = Nat256.create();
        Curve25519Field.twice(curve25519FieldElement2.x, create2);
        final int[] create3 = Nat256.create();
        Curve25519Field.multiply(create2, curve25519FieldElement2.x, create3);
        final int[] create4 = Nat256.create();
        Curve25519Field.multiply(create3, curve25519FieldElement.x, create4);
        Curve25519Field.twice(create4, create4);
        final int[] create5 = Nat256.create();
        Curve25519Field.square(create3, create5);
        Curve25519Field.twice(create5, create5);
        final Curve25519FieldElement curve25519FieldElement4 = new Curve25519FieldElement(create3);
        Curve25519Field.square(create, curve25519FieldElement4.x);
        Curve25519Field.subtract(curve25519FieldElement4.x, create4, curve25519FieldElement4.x);
        Curve25519Field.subtract(curve25519FieldElement4.x, create4, curve25519FieldElement4.x);
        final Curve25519FieldElement curve25519FieldElement5 = new Curve25519FieldElement(create4);
        Curve25519Field.subtract(create4, curve25519FieldElement4.x, curve25519FieldElement5.x);
        Curve25519Field.multiply(curve25519FieldElement5.x, create, curve25519FieldElement5.x);
        Curve25519Field.subtract(curve25519FieldElement5.x, create5, curve25519FieldElement5.x);
        final Curve25519FieldElement curve25519FieldElement6 = new Curve25519FieldElement(create2);
        if (!Nat256.isOne(curve25519FieldElement3.x)) {
            Curve25519Field.multiply(curve25519FieldElement6.x, curve25519FieldElement3.x, curve25519FieldElement6.x);
        }
        Curve25519FieldElement curve25519FieldElement7 = null;
        if (b) {
            curve25519FieldElement7 = new Curve25519FieldElement(create5);
            Curve25519Field.multiply(curve25519FieldElement7.x, jacobianModifiedW.x, curve25519FieldElement7.x);
            Curve25519Field.twice(curve25519FieldElement7.x, curve25519FieldElement7.x);
        }
        return new Curve25519Point(this.getCurve(), curve25519FieldElement4, curve25519FieldElement5, new ECFieldElement[] { curve25519FieldElement6, curve25519FieldElement7 }, this.withCompression);
    }
}
