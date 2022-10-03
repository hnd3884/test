package org.bouncycastle.math.ec.custom.sec;

import org.bouncycastle.util.Arrays;
import org.bouncycastle.math.raw.Mod;
import org.bouncycastle.math.raw.Nat256;
import java.math.BigInteger;
import org.bouncycastle.math.ec.ECFieldElement;

public class SecP256K1FieldElement extends ECFieldElement
{
    public static final BigInteger Q;
    protected int[] x;
    
    public SecP256K1FieldElement(final BigInteger bigInteger) {
        if (bigInteger == null || bigInteger.signum() < 0 || bigInteger.compareTo(SecP256K1FieldElement.Q) >= 0) {
            throw new IllegalArgumentException("x value invalid for SecP256K1FieldElement");
        }
        this.x = SecP256K1Field.fromBigInteger(bigInteger);
    }
    
    public SecP256K1FieldElement() {
        this.x = Nat256.create();
    }
    
    protected SecP256K1FieldElement(final int[] x) {
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
        return "SecP256K1Field";
    }
    
    @Override
    public int getFieldSize() {
        return SecP256K1FieldElement.Q.bitLength();
    }
    
    @Override
    public ECFieldElement add(final ECFieldElement ecFieldElement) {
        final int[] create = Nat256.create();
        SecP256K1Field.add(this.x, ((SecP256K1FieldElement)ecFieldElement).x, create);
        return new SecP256K1FieldElement(create);
    }
    
    @Override
    public ECFieldElement addOne() {
        final int[] create = Nat256.create();
        SecP256K1Field.addOne(this.x, create);
        return new SecP256K1FieldElement(create);
    }
    
    @Override
    public ECFieldElement subtract(final ECFieldElement ecFieldElement) {
        final int[] create = Nat256.create();
        SecP256K1Field.subtract(this.x, ((SecP256K1FieldElement)ecFieldElement).x, create);
        return new SecP256K1FieldElement(create);
    }
    
    @Override
    public ECFieldElement multiply(final ECFieldElement ecFieldElement) {
        final int[] create = Nat256.create();
        SecP256K1Field.multiply(this.x, ((SecP256K1FieldElement)ecFieldElement).x, create);
        return new SecP256K1FieldElement(create);
    }
    
    @Override
    public ECFieldElement divide(final ECFieldElement ecFieldElement) {
        final int[] create = Nat256.create();
        Mod.invert(SecP256K1Field.P, ((SecP256K1FieldElement)ecFieldElement).x, create);
        SecP256K1Field.multiply(create, this.x, create);
        return new SecP256K1FieldElement(create);
    }
    
    @Override
    public ECFieldElement negate() {
        final int[] create = Nat256.create();
        SecP256K1Field.negate(this.x, create);
        return new SecP256K1FieldElement(create);
    }
    
    @Override
    public ECFieldElement square() {
        final int[] create = Nat256.create();
        SecP256K1Field.square(this.x, create);
        return new SecP256K1FieldElement(create);
    }
    
    @Override
    public ECFieldElement invert() {
        final int[] create = Nat256.create();
        Mod.invert(SecP256K1Field.P, this.x, create);
        return new SecP256K1FieldElement(create);
    }
    
    @Override
    public ECFieldElement sqrt() {
        final int[] x = this.x;
        if (Nat256.isZero(x) || Nat256.isOne(x)) {
            return this;
        }
        final int[] create = Nat256.create();
        SecP256K1Field.square(x, create);
        SecP256K1Field.multiply(create, x, create);
        final int[] create2 = Nat256.create();
        SecP256K1Field.square(create, create2);
        SecP256K1Field.multiply(create2, x, create2);
        final int[] create3 = Nat256.create();
        SecP256K1Field.squareN(create2, 3, create3);
        SecP256K1Field.multiply(create3, create2, create3);
        final int[] array = create3;
        SecP256K1Field.squareN(create3, 3, array);
        SecP256K1Field.multiply(array, create2, array);
        final int[] array2 = array;
        SecP256K1Field.squareN(array, 2, array2);
        SecP256K1Field.multiply(array2, create, array2);
        final int[] create4 = Nat256.create();
        SecP256K1Field.squareN(array2, 11, create4);
        SecP256K1Field.multiply(create4, array2, create4);
        final int[] array3 = array2;
        SecP256K1Field.squareN(create4, 22, array3);
        SecP256K1Field.multiply(array3, create4, array3);
        final int[] create5 = Nat256.create();
        SecP256K1Field.squareN(array3, 44, create5);
        SecP256K1Field.multiply(create5, array3, create5);
        final int[] create6 = Nat256.create();
        SecP256K1Field.squareN(create5, 88, create6);
        SecP256K1Field.multiply(create6, create5, create6);
        final int[] array4 = create5;
        SecP256K1Field.squareN(create6, 44, array4);
        SecP256K1Field.multiply(array4, array3, array4);
        final int[] array5 = array3;
        SecP256K1Field.squareN(array4, 3, array5);
        SecP256K1Field.multiply(array5, create2, array5);
        final int[] array6 = array5;
        SecP256K1Field.squareN(array6, 23, array6);
        SecP256K1Field.multiply(array6, create4, array6);
        SecP256K1Field.squareN(array6, 6, array6);
        SecP256K1Field.multiply(array6, create, array6);
        SecP256K1Field.squareN(array6, 2, array6);
        final int[] array7 = create;
        SecP256K1Field.square(array6, array7);
        return Nat256.eq(x, array7) ? new SecP256K1FieldElement(array6) : null;
    }
    
    @Override
    public boolean equals(final Object o) {
        return o == this || (o instanceof SecP256K1FieldElement && Nat256.eq(this.x, ((SecP256K1FieldElement)o).x));
    }
    
    @Override
    public int hashCode() {
        return SecP256K1FieldElement.Q.hashCode() ^ Arrays.hashCode(this.x, 0, 8);
    }
    
    static {
        Q = SecP256K1Curve.q;
    }
}
