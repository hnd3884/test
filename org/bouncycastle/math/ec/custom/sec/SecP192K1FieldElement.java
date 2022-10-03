package org.bouncycastle.math.ec.custom.sec;

import org.bouncycastle.util.Arrays;
import org.bouncycastle.math.raw.Mod;
import org.bouncycastle.math.raw.Nat192;
import java.math.BigInteger;
import org.bouncycastle.math.ec.ECFieldElement;

public class SecP192K1FieldElement extends ECFieldElement
{
    public static final BigInteger Q;
    protected int[] x;
    
    public SecP192K1FieldElement(final BigInteger bigInteger) {
        if (bigInteger == null || bigInteger.signum() < 0 || bigInteger.compareTo(SecP192K1FieldElement.Q) >= 0) {
            throw new IllegalArgumentException("x value invalid for SecP192K1FieldElement");
        }
        this.x = SecP192K1Field.fromBigInteger(bigInteger);
    }
    
    public SecP192K1FieldElement() {
        this.x = Nat192.create();
    }
    
    protected SecP192K1FieldElement(final int[] x) {
        this.x = x;
    }
    
    @Override
    public boolean isZero() {
        return Nat192.isZero(this.x);
    }
    
    @Override
    public boolean isOne() {
        return Nat192.isOne(this.x);
    }
    
    @Override
    public boolean testBitZero() {
        return Nat192.getBit(this.x, 0) == 1;
    }
    
    @Override
    public BigInteger toBigInteger() {
        return Nat192.toBigInteger(this.x);
    }
    
    @Override
    public String getFieldName() {
        return "SecP192K1Field";
    }
    
    @Override
    public int getFieldSize() {
        return SecP192K1FieldElement.Q.bitLength();
    }
    
    @Override
    public ECFieldElement add(final ECFieldElement ecFieldElement) {
        final int[] create = Nat192.create();
        SecP192K1Field.add(this.x, ((SecP192K1FieldElement)ecFieldElement).x, create);
        return new SecP192K1FieldElement(create);
    }
    
    @Override
    public ECFieldElement addOne() {
        final int[] create = Nat192.create();
        SecP192K1Field.addOne(this.x, create);
        return new SecP192K1FieldElement(create);
    }
    
    @Override
    public ECFieldElement subtract(final ECFieldElement ecFieldElement) {
        final int[] create = Nat192.create();
        SecP192K1Field.subtract(this.x, ((SecP192K1FieldElement)ecFieldElement).x, create);
        return new SecP192K1FieldElement(create);
    }
    
    @Override
    public ECFieldElement multiply(final ECFieldElement ecFieldElement) {
        final int[] create = Nat192.create();
        SecP192K1Field.multiply(this.x, ((SecP192K1FieldElement)ecFieldElement).x, create);
        return new SecP192K1FieldElement(create);
    }
    
    @Override
    public ECFieldElement divide(final ECFieldElement ecFieldElement) {
        final int[] create = Nat192.create();
        Mod.invert(SecP192K1Field.P, ((SecP192K1FieldElement)ecFieldElement).x, create);
        SecP192K1Field.multiply(create, this.x, create);
        return new SecP192K1FieldElement(create);
    }
    
    @Override
    public ECFieldElement negate() {
        final int[] create = Nat192.create();
        SecP192K1Field.negate(this.x, create);
        return new SecP192K1FieldElement(create);
    }
    
    @Override
    public ECFieldElement square() {
        final int[] create = Nat192.create();
        SecP192K1Field.square(this.x, create);
        return new SecP192K1FieldElement(create);
    }
    
    @Override
    public ECFieldElement invert() {
        final int[] create = Nat192.create();
        Mod.invert(SecP192K1Field.P, this.x, create);
        return new SecP192K1FieldElement(create);
    }
    
    @Override
    public ECFieldElement sqrt() {
        final int[] x = this.x;
        if (Nat192.isZero(x) || Nat192.isOne(x)) {
            return this;
        }
        final int[] create = Nat192.create();
        SecP192K1Field.square(x, create);
        SecP192K1Field.multiply(create, x, create);
        final int[] create2 = Nat192.create();
        SecP192K1Field.square(create, create2);
        SecP192K1Field.multiply(create2, x, create2);
        final int[] create3 = Nat192.create();
        SecP192K1Field.squareN(create2, 3, create3);
        SecP192K1Field.multiply(create3, create2, create3);
        final int[] array = create3;
        SecP192K1Field.squareN(create3, 2, array);
        SecP192K1Field.multiply(array, create, array);
        final int[] array2 = create;
        SecP192K1Field.squareN(array, 8, array2);
        SecP192K1Field.multiply(array2, array, array2);
        final int[] array3 = array;
        SecP192K1Field.squareN(array2, 3, array3);
        SecP192K1Field.multiply(array3, create2, array3);
        final int[] create4 = Nat192.create();
        SecP192K1Field.squareN(array3, 16, create4);
        SecP192K1Field.multiply(create4, array2, create4);
        final int[] array4 = array2;
        SecP192K1Field.squareN(create4, 35, array4);
        SecP192K1Field.multiply(array4, create4, array4);
        final int[] array5 = create4;
        SecP192K1Field.squareN(array4, 70, array5);
        SecP192K1Field.multiply(array5, array4, array5);
        final int[] array6 = array4;
        SecP192K1Field.squareN(array5, 19, array6);
        SecP192K1Field.multiply(array6, array3, array6);
        final int[] array7 = array6;
        SecP192K1Field.squareN(array7, 20, array7);
        SecP192K1Field.multiply(array7, array3, array7);
        SecP192K1Field.squareN(array7, 4, array7);
        SecP192K1Field.multiply(array7, create2, array7);
        SecP192K1Field.squareN(array7, 6, array7);
        SecP192K1Field.multiply(array7, create2, array7);
        SecP192K1Field.square(array7, array7);
        final int[] array8 = create2;
        SecP192K1Field.square(array7, array8);
        return Nat192.eq(x, array8) ? new SecP192K1FieldElement(array7) : null;
    }
    
    @Override
    public boolean equals(final Object o) {
        return o == this || (o instanceof SecP192K1FieldElement && Nat192.eq(this.x, ((SecP192K1FieldElement)o).x));
    }
    
    @Override
    public int hashCode() {
        return SecP192K1FieldElement.Q.hashCode() ^ Arrays.hashCode(this.x, 0, 6);
    }
    
    static {
        Q = SecP192K1Curve.q;
    }
}
