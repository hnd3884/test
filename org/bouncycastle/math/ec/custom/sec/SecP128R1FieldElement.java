package org.bouncycastle.math.ec.custom.sec;

import org.bouncycastle.util.Arrays;
import org.bouncycastle.math.raw.Mod;
import org.bouncycastle.math.raw.Nat128;
import java.math.BigInteger;
import org.bouncycastle.math.ec.ECFieldElement;

public class SecP128R1FieldElement extends ECFieldElement
{
    public static final BigInteger Q;
    protected int[] x;
    
    public SecP128R1FieldElement(final BigInteger bigInteger) {
        if (bigInteger == null || bigInteger.signum() < 0 || bigInteger.compareTo(SecP128R1FieldElement.Q) >= 0) {
            throw new IllegalArgumentException("x value invalid for SecP128R1FieldElement");
        }
        this.x = SecP128R1Field.fromBigInteger(bigInteger);
    }
    
    public SecP128R1FieldElement() {
        this.x = Nat128.create();
    }
    
    protected SecP128R1FieldElement(final int[] x) {
        this.x = x;
    }
    
    @Override
    public boolean isZero() {
        return Nat128.isZero(this.x);
    }
    
    @Override
    public boolean isOne() {
        return Nat128.isOne(this.x);
    }
    
    @Override
    public boolean testBitZero() {
        return Nat128.getBit(this.x, 0) == 1;
    }
    
    @Override
    public BigInteger toBigInteger() {
        return Nat128.toBigInteger(this.x);
    }
    
    @Override
    public String getFieldName() {
        return "SecP128R1Field";
    }
    
    @Override
    public int getFieldSize() {
        return SecP128R1FieldElement.Q.bitLength();
    }
    
    @Override
    public ECFieldElement add(final ECFieldElement ecFieldElement) {
        final int[] create = Nat128.create();
        SecP128R1Field.add(this.x, ((SecP128R1FieldElement)ecFieldElement).x, create);
        return new SecP128R1FieldElement(create);
    }
    
    @Override
    public ECFieldElement addOne() {
        final int[] create = Nat128.create();
        SecP128R1Field.addOne(this.x, create);
        return new SecP128R1FieldElement(create);
    }
    
    @Override
    public ECFieldElement subtract(final ECFieldElement ecFieldElement) {
        final int[] create = Nat128.create();
        SecP128R1Field.subtract(this.x, ((SecP128R1FieldElement)ecFieldElement).x, create);
        return new SecP128R1FieldElement(create);
    }
    
    @Override
    public ECFieldElement multiply(final ECFieldElement ecFieldElement) {
        final int[] create = Nat128.create();
        SecP128R1Field.multiply(this.x, ((SecP128R1FieldElement)ecFieldElement).x, create);
        return new SecP128R1FieldElement(create);
    }
    
    @Override
    public ECFieldElement divide(final ECFieldElement ecFieldElement) {
        final int[] create = Nat128.create();
        Mod.invert(SecP128R1Field.P, ((SecP128R1FieldElement)ecFieldElement).x, create);
        SecP128R1Field.multiply(create, this.x, create);
        return new SecP128R1FieldElement(create);
    }
    
    @Override
    public ECFieldElement negate() {
        final int[] create = Nat128.create();
        SecP128R1Field.negate(this.x, create);
        return new SecP128R1FieldElement(create);
    }
    
    @Override
    public ECFieldElement square() {
        final int[] create = Nat128.create();
        SecP128R1Field.square(this.x, create);
        return new SecP128R1FieldElement(create);
    }
    
    @Override
    public ECFieldElement invert() {
        final int[] create = Nat128.create();
        Mod.invert(SecP128R1Field.P, this.x, create);
        return new SecP128R1FieldElement(create);
    }
    
    @Override
    public ECFieldElement sqrt() {
        final int[] x = this.x;
        if (Nat128.isZero(x) || Nat128.isOne(x)) {
            return this;
        }
        final int[] create = Nat128.create();
        SecP128R1Field.square(x, create);
        SecP128R1Field.multiply(create, x, create);
        final int[] create2 = Nat128.create();
        SecP128R1Field.squareN(create, 2, create2);
        SecP128R1Field.multiply(create2, create, create2);
        final int[] create3 = Nat128.create();
        SecP128R1Field.squareN(create2, 4, create3);
        SecP128R1Field.multiply(create3, create2, create3);
        final int[] array = create2;
        SecP128R1Field.squareN(create3, 2, array);
        SecP128R1Field.multiply(array, create, array);
        final int[] array2 = create;
        SecP128R1Field.squareN(array, 10, array2);
        SecP128R1Field.multiply(array2, array, array2);
        final int[] array3 = create3;
        SecP128R1Field.squareN(array2, 10, array3);
        SecP128R1Field.multiply(array3, array, array3);
        final int[] array4 = array;
        SecP128R1Field.square(array3, array4);
        SecP128R1Field.multiply(array4, x, array4);
        final int[] array5 = array4;
        SecP128R1Field.squareN(array5, 95, array5);
        final int[] array6 = array3;
        SecP128R1Field.square(array5, array6);
        return Nat128.eq(x, array6) ? new SecP128R1FieldElement(array5) : null;
    }
    
    @Override
    public boolean equals(final Object o) {
        return o == this || (o instanceof SecP128R1FieldElement && Nat128.eq(this.x, ((SecP128R1FieldElement)o).x));
    }
    
    @Override
    public int hashCode() {
        return SecP128R1FieldElement.Q.hashCode() ^ Arrays.hashCode(this.x, 0, 4);
    }
    
    static {
        Q = SecP128R1Curve.q;
    }
}
