package org.bouncycastle.math.ec.custom.gm;

import org.bouncycastle.math.raw.Nat;
import org.bouncycastle.math.raw.Nat256;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECPoint;

public class SM2P256V1Point extends AbstractFp
{
    @Deprecated
    public SM2P256V1Point(final ECCurve ecCurve, final ECFieldElement ecFieldElement, final ECFieldElement ecFieldElement2) {
        this(ecCurve, ecFieldElement, ecFieldElement2, false);
    }
    
    @Deprecated
    public SM2P256V1Point(final ECCurve ecCurve, final ECFieldElement ecFieldElement, final ECFieldElement ecFieldElement2, final boolean withCompression) {
        super(ecCurve, ecFieldElement, ecFieldElement2);
        if (ecFieldElement == null != (ecFieldElement2 == null)) {
            throw new IllegalArgumentException("Exactly one of the field elements is null");
        }
        this.withCompression = withCompression;
    }
    
    SM2P256V1Point(final ECCurve ecCurve, final ECFieldElement ecFieldElement, final ECFieldElement ecFieldElement2, final ECFieldElement[] array, final boolean withCompression) {
        super(ecCurve, ecFieldElement, ecFieldElement2, array);
        this.withCompression = withCompression;
    }
    
    @Override
    protected ECPoint detach() {
        return new SM2P256V1Point(null, this.getAffineXCoord(), this.getAffineYCoord());
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
        final SM2P256V1FieldElement sm2P256V1FieldElement = (SM2P256V1FieldElement)this.x;
        final SM2P256V1FieldElement sm2P256V1FieldElement2 = (SM2P256V1FieldElement)this.y;
        final SM2P256V1FieldElement sm2P256V1FieldElement3 = (SM2P256V1FieldElement)ecPoint.getXCoord();
        final SM2P256V1FieldElement sm2P256V1FieldElement4 = (SM2P256V1FieldElement)ecPoint.getYCoord();
        final SM2P256V1FieldElement sm2P256V1FieldElement5 = (SM2P256V1FieldElement)this.zs[0];
        final SM2P256V1FieldElement sm2P256V1FieldElement6 = (SM2P256V1FieldElement)ecPoint.getZCoord(0);
        final int[] ext = Nat256.createExt();
        final int[] create = Nat256.create();
        final int[] create2 = Nat256.create();
        final int[] create3 = Nat256.create();
        final boolean one = sm2P256V1FieldElement5.isOne();
        int[] x;
        int[] x2;
        if (one) {
            x = sm2P256V1FieldElement3.x;
            x2 = sm2P256V1FieldElement4.x;
        }
        else {
            x2 = create2;
            SM2P256V1Field.square(sm2P256V1FieldElement5.x, x2);
            x = create;
            SM2P256V1Field.multiply(x2, sm2P256V1FieldElement3.x, x);
            SM2P256V1Field.multiply(x2, sm2P256V1FieldElement5.x, x2);
            SM2P256V1Field.multiply(x2, sm2P256V1FieldElement4.x, x2);
        }
        final boolean one2 = sm2P256V1FieldElement6.isOne();
        int[] x3;
        int[] x4;
        if (one2) {
            x3 = sm2P256V1FieldElement.x;
            x4 = sm2P256V1FieldElement2.x;
        }
        else {
            x4 = create3;
            SM2P256V1Field.square(sm2P256V1FieldElement6.x, x4);
            x3 = ext;
            SM2P256V1Field.multiply(x4, sm2P256V1FieldElement.x, x3);
            SM2P256V1Field.multiply(x4, sm2P256V1FieldElement6.x, x4);
            SM2P256V1Field.multiply(x4, sm2P256V1FieldElement2.x, x4);
        }
        final int[] create4 = Nat256.create();
        SM2P256V1Field.subtract(x3, x, create4);
        final int[] array = create;
        SM2P256V1Field.subtract(x4, x2, array);
        if (!Nat256.isZero(create4)) {
            final int[] array2 = create2;
            SM2P256V1Field.square(create4, array2);
            final int[] create5 = Nat256.create();
            SM2P256V1Field.multiply(array2, create4, create5);
            final int[] array3 = create2;
            SM2P256V1Field.multiply(array2, x3, array3);
            SM2P256V1Field.negate(create5, create5);
            Nat256.mul(x4, create5, ext);
            SM2P256V1Field.reduce32(Nat256.addBothTo(array3, array3, create5), create5);
            final SM2P256V1FieldElement sm2P256V1FieldElement7 = new SM2P256V1FieldElement(create3);
            SM2P256V1Field.square(array, sm2P256V1FieldElement7.x);
            SM2P256V1Field.subtract(sm2P256V1FieldElement7.x, create5, sm2P256V1FieldElement7.x);
            final SM2P256V1FieldElement sm2P256V1FieldElement8 = new SM2P256V1FieldElement(create5);
            SM2P256V1Field.subtract(array3, sm2P256V1FieldElement7.x, sm2P256V1FieldElement8.x);
            SM2P256V1Field.multiplyAddToExt(sm2P256V1FieldElement8.x, array, ext);
            SM2P256V1Field.reduce(ext, sm2P256V1FieldElement8.x);
            final SM2P256V1FieldElement sm2P256V1FieldElement9 = new SM2P256V1FieldElement(create4);
            if (!one) {
                SM2P256V1Field.multiply(sm2P256V1FieldElement9.x, sm2P256V1FieldElement5.x, sm2P256V1FieldElement9.x);
            }
            if (!one2) {
                SM2P256V1Field.multiply(sm2P256V1FieldElement9.x, sm2P256V1FieldElement6.x, sm2P256V1FieldElement9.x);
            }
            return new SM2P256V1Point(curve, sm2P256V1FieldElement7, sm2P256V1FieldElement8, new ECFieldElement[] { sm2P256V1FieldElement9 }, this.withCompression);
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
        final SM2P256V1FieldElement sm2P256V1FieldElement = (SM2P256V1FieldElement)this.y;
        if (sm2P256V1FieldElement.isZero()) {
            return curve.getInfinity();
        }
        final SM2P256V1FieldElement sm2P256V1FieldElement2 = (SM2P256V1FieldElement)this.x;
        final SM2P256V1FieldElement sm2P256V1FieldElement3 = (SM2P256V1FieldElement)this.zs[0];
        final int[] create = Nat256.create();
        final int[] create2 = Nat256.create();
        final int[] create3 = Nat256.create();
        SM2P256V1Field.square(sm2P256V1FieldElement.x, create3);
        final int[] create4 = Nat256.create();
        SM2P256V1Field.square(create3, create4);
        final boolean one = sm2P256V1FieldElement3.isOne();
        int[] x = sm2P256V1FieldElement3.x;
        if (!one) {
            x = create2;
            SM2P256V1Field.square(sm2P256V1FieldElement3.x, x);
        }
        SM2P256V1Field.subtract(sm2P256V1FieldElement2.x, x, create);
        final int[] array = create2;
        SM2P256V1Field.add(sm2P256V1FieldElement2.x, x, array);
        SM2P256V1Field.multiply(array, create, array);
        SM2P256V1Field.reduce32(Nat256.addBothTo(array, array, array), array);
        final int[] array2 = create3;
        SM2P256V1Field.multiply(create3, sm2P256V1FieldElement2.x, array2);
        SM2P256V1Field.reduce32(Nat.shiftUpBits(8, array2, 2, 0), array2);
        SM2P256V1Field.reduce32(Nat.shiftUpBits(8, create4, 3, 0, create), create);
        final SM2P256V1FieldElement sm2P256V1FieldElement4 = new SM2P256V1FieldElement(create4);
        SM2P256V1Field.square(array, sm2P256V1FieldElement4.x);
        SM2P256V1Field.subtract(sm2P256V1FieldElement4.x, array2, sm2P256V1FieldElement4.x);
        SM2P256V1Field.subtract(sm2P256V1FieldElement4.x, array2, sm2P256V1FieldElement4.x);
        final SM2P256V1FieldElement sm2P256V1FieldElement5 = new SM2P256V1FieldElement(array2);
        SM2P256V1Field.subtract(array2, sm2P256V1FieldElement4.x, sm2P256V1FieldElement5.x);
        SM2P256V1Field.multiply(sm2P256V1FieldElement5.x, array, sm2P256V1FieldElement5.x);
        SM2P256V1Field.subtract(sm2P256V1FieldElement5.x, create, sm2P256V1FieldElement5.x);
        final SM2P256V1FieldElement sm2P256V1FieldElement6 = new SM2P256V1FieldElement(array);
        SM2P256V1Field.twice(sm2P256V1FieldElement.x, sm2P256V1FieldElement6.x);
        if (!one) {
            SM2P256V1Field.multiply(sm2P256V1FieldElement6.x, sm2P256V1FieldElement3.x, sm2P256V1FieldElement6.x);
        }
        return new SM2P256V1Point(curve, sm2P256V1FieldElement4, sm2P256V1FieldElement5, new ECFieldElement[] { sm2P256V1FieldElement6 }, this.withCompression);
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
        return new SM2P256V1Point(this.curve, this.x, this.y.negate(), this.zs, this.withCompression);
    }
}
