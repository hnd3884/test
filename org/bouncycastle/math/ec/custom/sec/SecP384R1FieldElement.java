package org.bouncycastle.math.ec.custom.sec;

import org.bouncycastle.util.Arrays;
import org.bouncycastle.math.raw.Mod;
import org.bouncycastle.math.raw.Nat;
import java.math.BigInteger;
import org.bouncycastle.math.ec.ECFieldElement;

public class SecP384R1FieldElement extends ECFieldElement
{
    public static final BigInteger Q;
    protected int[] x;
    
    public SecP384R1FieldElement(final BigInteger bigInteger) {
        if (bigInteger == null || bigInteger.signum() < 0 || bigInteger.compareTo(SecP384R1FieldElement.Q) >= 0) {
            throw new IllegalArgumentException("x value invalid for SecP384R1FieldElement");
        }
        this.x = SecP384R1Field.fromBigInteger(bigInteger);
    }
    
    public SecP384R1FieldElement() {
        this.x = Nat.create(12);
    }
    
    protected SecP384R1FieldElement(final int[] x) {
        this.x = x;
    }
    
    @Override
    public boolean isZero() {
        return Nat.isZero(12, this.x);
    }
    
    @Override
    public boolean isOne() {
        return Nat.isOne(12, this.x);
    }
    
    @Override
    public boolean testBitZero() {
        return Nat.getBit(this.x, 0) == 1;
    }
    
    @Override
    public BigInteger toBigInteger() {
        return Nat.toBigInteger(12, this.x);
    }
    
    @Override
    public String getFieldName() {
        return "SecP384R1Field";
    }
    
    @Override
    public int getFieldSize() {
        return SecP384R1FieldElement.Q.bitLength();
    }
    
    @Override
    public ECFieldElement add(final ECFieldElement ecFieldElement) {
        final int[] create = Nat.create(12);
        SecP384R1Field.add(this.x, ((SecP384R1FieldElement)ecFieldElement).x, create);
        return new SecP384R1FieldElement(create);
    }
    
    @Override
    public ECFieldElement addOne() {
        final int[] create = Nat.create(12);
        SecP384R1Field.addOne(this.x, create);
        return new SecP384R1FieldElement(create);
    }
    
    @Override
    public ECFieldElement subtract(final ECFieldElement ecFieldElement) {
        final int[] create = Nat.create(12);
        SecP384R1Field.subtract(this.x, ((SecP384R1FieldElement)ecFieldElement).x, create);
        return new SecP384R1FieldElement(create);
    }
    
    @Override
    public ECFieldElement multiply(final ECFieldElement ecFieldElement) {
        final int[] create = Nat.create(12);
        SecP384R1Field.multiply(this.x, ((SecP384R1FieldElement)ecFieldElement).x, create);
        return new SecP384R1FieldElement(create);
    }
    
    @Override
    public ECFieldElement divide(final ECFieldElement ecFieldElement) {
        final int[] create = Nat.create(12);
        Mod.invert(SecP384R1Field.P, ((SecP384R1FieldElement)ecFieldElement).x, create);
        SecP384R1Field.multiply(create, this.x, create);
        return new SecP384R1FieldElement(create);
    }
    
    @Override
    public ECFieldElement negate() {
        final int[] create = Nat.create(12);
        SecP384R1Field.negate(this.x, create);
        return new SecP384R1FieldElement(create);
    }
    
    @Override
    public ECFieldElement square() {
        final int[] create = Nat.create(12);
        SecP384R1Field.square(this.x, create);
        return new SecP384R1FieldElement(create);
    }
    
    @Override
    public ECFieldElement invert() {
        final int[] create = Nat.create(12);
        Mod.invert(SecP384R1Field.P, this.x, create);
        return new SecP384R1FieldElement(create);
    }
    
    @Override
    public ECFieldElement sqrt() {
        final int[] x = this.x;
        if (Nat.isZero(12, x) || Nat.isOne(12, x)) {
            return this;
        }
        final int[] create = Nat.create(12);
        final int[] create2 = Nat.create(12);
        final int[] create3 = Nat.create(12);
        final int[] create4 = Nat.create(12);
        SecP384R1Field.square(x, create);
        SecP384R1Field.multiply(create, x, create);
        SecP384R1Field.squareN(create, 2, create2);
        SecP384R1Field.multiply(create2, create, create2);
        SecP384R1Field.square(create2, create2);
        SecP384R1Field.multiply(create2, x, create2);
        SecP384R1Field.squareN(create2, 5, create3);
        SecP384R1Field.multiply(create3, create2, create3);
        SecP384R1Field.squareN(create3, 5, create4);
        SecP384R1Field.multiply(create4, create2, create4);
        SecP384R1Field.squareN(create4, 15, create2);
        SecP384R1Field.multiply(create2, create4, create2);
        SecP384R1Field.squareN(create2, 2, create3);
        SecP384R1Field.multiply(create, create3, create);
        SecP384R1Field.squareN(create3, 28, create3);
        SecP384R1Field.multiply(create2, create3, create2);
        SecP384R1Field.squareN(create2, 60, create3);
        SecP384R1Field.multiply(create3, create2, create3);
        final int[] array = create2;
        SecP384R1Field.squareN(create3, 120, array);
        SecP384R1Field.multiply(array, create3, array);
        SecP384R1Field.squareN(array, 15, array);
        SecP384R1Field.multiply(array, create4, array);
        SecP384R1Field.squareN(array, 33, array);
        SecP384R1Field.multiply(array, create, array);
        SecP384R1Field.squareN(array, 64, array);
        SecP384R1Field.multiply(array, x, array);
        SecP384R1Field.squareN(array, 30, create);
        SecP384R1Field.square(create, create2);
        return Nat.eq(12, x, create2) ? new SecP384R1FieldElement(create) : null;
    }
    
    @Override
    public boolean equals(final Object o) {
        return o == this || (o instanceof SecP384R1FieldElement && Nat.eq(12, this.x, ((SecP384R1FieldElement)o).x));
    }
    
    @Override
    public int hashCode() {
        return SecP384R1FieldElement.Q.hashCode() ^ Arrays.hashCode(this.x, 0, 12);
    }
    
    static {
        Q = SecP384R1Curve.q;
    }
}
