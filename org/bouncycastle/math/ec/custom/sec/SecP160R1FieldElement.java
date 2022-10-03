package org.bouncycastle.math.ec.custom.sec;

import org.bouncycastle.util.Arrays;
import org.bouncycastle.math.raw.Mod;
import org.bouncycastle.math.raw.Nat160;
import java.math.BigInteger;
import org.bouncycastle.math.ec.ECFieldElement;

public class SecP160R1FieldElement extends ECFieldElement
{
    public static final BigInteger Q;
    protected int[] x;
    
    public SecP160R1FieldElement(final BigInteger bigInteger) {
        if (bigInteger == null || bigInteger.signum() < 0 || bigInteger.compareTo(SecP160R1FieldElement.Q) >= 0) {
            throw new IllegalArgumentException("x value invalid for SecP160R1FieldElement");
        }
        this.x = SecP160R1Field.fromBigInteger(bigInteger);
    }
    
    public SecP160R1FieldElement() {
        this.x = Nat160.create();
    }
    
    protected SecP160R1FieldElement(final int[] x) {
        this.x = x;
    }
    
    @Override
    public boolean isZero() {
        return Nat160.isZero(this.x);
    }
    
    @Override
    public boolean isOne() {
        return Nat160.isOne(this.x);
    }
    
    @Override
    public boolean testBitZero() {
        return Nat160.getBit(this.x, 0) == 1;
    }
    
    @Override
    public BigInteger toBigInteger() {
        return Nat160.toBigInteger(this.x);
    }
    
    @Override
    public String getFieldName() {
        return "SecP160R1Field";
    }
    
    @Override
    public int getFieldSize() {
        return SecP160R1FieldElement.Q.bitLength();
    }
    
    @Override
    public ECFieldElement add(final ECFieldElement ecFieldElement) {
        final int[] create = Nat160.create();
        SecP160R1Field.add(this.x, ((SecP160R1FieldElement)ecFieldElement).x, create);
        return new SecP160R1FieldElement(create);
    }
    
    @Override
    public ECFieldElement addOne() {
        final int[] create = Nat160.create();
        SecP160R1Field.addOne(this.x, create);
        return new SecP160R1FieldElement(create);
    }
    
    @Override
    public ECFieldElement subtract(final ECFieldElement ecFieldElement) {
        final int[] create = Nat160.create();
        SecP160R1Field.subtract(this.x, ((SecP160R1FieldElement)ecFieldElement).x, create);
        return new SecP160R1FieldElement(create);
    }
    
    @Override
    public ECFieldElement multiply(final ECFieldElement ecFieldElement) {
        final int[] create = Nat160.create();
        SecP160R1Field.multiply(this.x, ((SecP160R1FieldElement)ecFieldElement).x, create);
        return new SecP160R1FieldElement(create);
    }
    
    @Override
    public ECFieldElement divide(final ECFieldElement ecFieldElement) {
        final int[] create = Nat160.create();
        Mod.invert(SecP160R1Field.P, ((SecP160R1FieldElement)ecFieldElement).x, create);
        SecP160R1Field.multiply(create, this.x, create);
        return new SecP160R1FieldElement(create);
    }
    
    @Override
    public ECFieldElement negate() {
        final int[] create = Nat160.create();
        SecP160R1Field.negate(this.x, create);
        return new SecP160R1FieldElement(create);
    }
    
    @Override
    public ECFieldElement square() {
        final int[] create = Nat160.create();
        SecP160R1Field.square(this.x, create);
        return new SecP160R1FieldElement(create);
    }
    
    @Override
    public ECFieldElement invert() {
        final int[] create = Nat160.create();
        Mod.invert(SecP160R1Field.P, this.x, create);
        return new SecP160R1FieldElement(create);
    }
    
    @Override
    public ECFieldElement sqrt() {
        final int[] x = this.x;
        if (Nat160.isZero(x) || Nat160.isOne(x)) {
            return this;
        }
        final int[] create = Nat160.create();
        SecP160R1Field.square(x, create);
        SecP160R1Field.multiply(create, x, create);
        final int[] create2 = Nat160.create();
        SecP160R1Field.squareN(create, 2, create2);
        SecP160R1Field.multiply(create2, create, create2);
        final int[] array = create;
        SecP160R1Field.squareN(create2, 4, array);
        SecP160R1Field.multiply(array, create2, array);
        final int[] array2 = create2;
        SecP160R1Field.squareN(array, 8, array2);
        SecP160R1Field.multiply(array2, array, array2);
        final int[] array3 = array;
        SecP160R1Field.squareN(array2, 16, array3);
        SecP160R1Field.multiply(array3, array2, array3);
        final int[] array4 = array2;
        SecP160R1Field.squareN(array3, 32, array4);
        SecP160R1Field.multiply(array4, array3, array4);
        final int[] array5 = array3;
        SecP160R1Field.squareN(array4, 64, array5);
        SecP160R1Field.multiply(array5, array4, array5);
        final int[] array6 = array4;
        SecP160R1Field.square(array5, array6);
        SecP160R1Field.multiply(array6, x, array6);
        final int[] array7 = array6;
        SecP160R1Field.squareN(array7, 29, array7);
        final int[] array8 = array5;
        SecP160R1Field.square(array7, array8);
        return Nat160.eq(x, array8) ? new SecP160R1FieldElement(array7) : null;
    }
    
    @Override
    public boolean equals(final Object o) {
        return o == this || (o instanceof SecP160R1FieldElement && Nat160.eq(this.x, ((SecP160R1FieldElement)o).x));
    }
    
    @Override
    public int hashCode() {
        return SecP160R1FieldElement.Q.hashCode() ^ Arrays.hashCode(this.x, 0, 5);
    }
    
    static {
        Q = SecP160R1Curve.q;
    }
}
