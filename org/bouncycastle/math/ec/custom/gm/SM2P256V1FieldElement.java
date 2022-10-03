package org.bouncycastle.math.ec.custom.gm;

import org.bouncycastle.util.Arrays;
import org.bouncycastle.math.raw.Mod;
import org.bouncycastle.math.raw.Nat256;
import java.math.BigInteger;
import org.bouncycastle.math.ec.ECFieldElement;

public class SM2P256V1FieldElement extends ECFieldElement
{
    public static final BigInteger Q;
    protected int[] x;
    
    public SM2P256V1FieldElement(final BigInteger bigInteger) {
        if (bigInteger == null || bigInteger.signum() < 0 || bigInteger.compareTo(SM2P256V1FieldElement.Q) >= 0) {
            throw new IllegalArgumentException("x value invalid for SM2P256V1FieldElement");
        }
        this.x = SM2P256V1Field.fromBigInteger(bigInteger);
    }
    
    public SM2P256V1FieldElement() {
        this.x = Nat256.create();
    }
    
    protected SM2P256V1FieldElement(final int[] x) {
        this.x = x;
    }
    
    @Override
    public boolean isZero() {
        return Nat256.isZero(this.x);
    }
    
    @Override
    public boolean isOne() {
        return Nat256.isOne(this.x);
    }
    
    @Override
    public boolean testBitZero() {
        return Nat256.getBit(this.x, 0) == 1;
    }
    
    @Override
    public BigInteger toBigInteger() {
        return Nat256.toBigInteger(this.x);
    }
    
    @Override
    public String getFieldName() {
        return "SM2P256V1Field";
    }
    
    @Override
    public int getFieldSize() {
        return SM2P256V1FieldElement.Q.bitLength();
    }
    
    @Override
    public ECFieldElement add(final ECFieldElement ecFieldElement) {
        final int[] create = Nat256.create();
        SM2P256V1Field.add(this.x, ((SM2P256V1FieldElement)ecFieldElement).x, create);
        return new SM2P256V1FieldElement(create);
    }
    
    @Override
    public ECFieldElement addOne() {
        final int[] create = Nat256.create();
        SM2P256V1Field.addOne(this.x, create);
        return new SM2P256V1FieldElement(create);
    }
    
    @Override
    public ECFieldElement subtract(final ECFieldElement ecFieldElement) {
        final int[] create = Nat256.create();
        SM2P256V1Field.subtract(this.x, ((SM2P256V1FieldElement)ecFieldElement).x, create);
        return new SM2P256V1FieldElement(create);
    }
    
    @Override
    public ECFieldElement multiply(final ECFieldElement ecFieldElement) {
        final int[] create = Nat256.create();
        SM2P256V1Field.multiply(this.x, ((SM2P256V1FieldElement)ecFieldElement).x, create);
        return new SM2P256V1FieldElement(create);
    }
    
    @Override
    public ECFieldElement divide(final ECFieldElement ecFieldElement) {
        final int[] create = Nat256.create();
        Mod.invert(SM2P256V1Field.P, ((SM2P256V1FieldElement)ecFieldElement).x, create);
        SM2P256V1Field.multiply(create, this.x, create);
        return new SM2P256V1FieldElement(create);
    }
    
    @Override
    public ECFieldElement negate() {
        final int[] create = Nat256.create();
        SM2P256V1Field.negate(this.x, create);
        return new SM2P256V1FieldElement(create);
    }
    
    @Override
    public ECFieldElement square() {
        final int[] create = Nat256.create();
        SM2P256V1Field.square(this.x, create);
        return new SM2P256V1FieldElement(create);
    }
    
    @Override
    public ECFieldElement invert() {
        final int[] create = Nat256.create();
        Mod.invert(SM2P256V1Field.P, this.x, create);
        return new SM2P256V1FieldElement(create);
    }
    
    @Override
    public ECFieldElement sqrt() {
        final int[] x = this.x;
        if (Nat256.isZero(x) || Nat256.isOne(x)) {
            return this;
        }
        final int[] create = Nat256.create();
        SM2P256V1Field.square(x, create);
        SM2P256V1Field.multiply(create, x, create);
        final int[] create2 = Nat256.create();
        SM2P256V1Field.squareN(create, 2, create2);
        SM2P256V1Field.multiply(create2, create, create2);
        final int[] create3 = Nat256.create();
        SM2P256V1Field.squareN(create2, 2, create3);
        SM2P256V1Field.multiply(create3, create, create3);
        final int[] array = create;
        SM2P256V1Field.squareN(create3, 6, array);
        SM2P256V1Field.multiply(array, create3, array);
        final int[] create4 = Nat256.create();
        SM2P256V1Field.squareN(array, 12, create4);
        SM2P256V1Field.multiply(create4, array, create4);
        final int[] array2 = array;
        SM2P256V1Field.squareN(create4, 6, array2);
        SM2P256V1Field.multiply(array2, create3, array2);
        final int[] array3 = create3;
        SM2P256V1Field.square(array2, array3);
        SM2P256V1Field.multiply(array3, x, array3);
        final int[] array4 = create4;
        SM2P256V1Field.squareN(array3, 31, array4);
        final int[] array5 = array2;
        SM2P256V1Field.multiply(array4, array3, array5);
        SM2P256V1Field.squareN(array4, 32, array4);
        SM2P256V1Field.multiply(array4, array5, array4);
        SM2P256V1Field.squareN(array4, 62, array4);
        SM2P256V1Field.multiply(array4, array5, array4);
        SM2P256V1Field.squareN(array4, 4, array4);
        SM2P256V1Field.multiply(array4, create2, array4);
        SM2P256V1Field.squareN(array4, 32, array4);
        SM2P256V1Field.multiply(array4, x, array4);
        SM2P256V1Field.squareN(array4, 62, array4);
        final int[] array6 = create2;
        SM2P256V1Field.square(array4, array6);
        return Nat256.eq(x, array6) ? new SM2P256V1FieldElement(array4) : null;
    }
    
    @Override
    public boolean equals(final Object o) {
        return o == this || (o instanceof SM2P256V1FieldElement && Nat256.eq(this.x, ((SM2P256V1FieldElement)o).x));
    }
    
    @Override
    public int hashCode() {
        return SM2P256V1FieldElement.Q.hashCode() ^ Arrays.hashCode(this.x, 0, 8);
    }
    
    static {
        Q = SM2P256V1Curve.q;
    }
}
