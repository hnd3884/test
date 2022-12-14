package org.bouncycastle.math.ec.custom.sec;

import org.bouncycastle.util.Arrays;
import org.bouncycastle.math.raw.Mod;
import org.bouncycastle.math.raw.Nat256;
import java.math.BigInteger;
import org.bouncycastle.math.ec.ECFieldElement;

public class SecP256R1FieldElement extends ECFieldElement
{
    public static final BigInteger Q;
    protected int[] x;
    
    public SecP256R1FieldElement(final BigInteger bigInteger) {
        if (bigInteger == null || bigInteger.signum() < 0 || bigInteger.compareTo(SecP256R1FieldElement.Q) >= 0) {
            throw new IllegalArgumentException("x value invalid for SecP256R1FieldElement");
        }
        this.x = SecP256R1Field.fromBigInteger(bigInteger);
    }
    
    public SecP256R1FieldElement() {
        this.x = Nat256.create();
    }
    
    protected SecP256R1FieldElement(final int[] x) {
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
        return "SecP256R1Field";
    }
    
    @Override
    public int getFieldSize() {
        return SecP256R1FieldElement.Q.bitLength();
    }
    
    @Override
    public ECFieldElement add(final ECFieldElement ecFieldElement) {
        final int[] create = Nat256.create();
        SecP256R1Field.add(this.x, ((SecP256R1FieldElement)ecFieldElement).x, create);
        return new SecP256R1FieldElement(create);
    }
    
    @Override
    public ECFieldElement addOne() {
        final int[] create = Nat256.create();
        SecP256R1Field.addOne(this.x, create);
        return new SecP256R1FieldElement(create);
    }
    
    @Override
    public ECFieldElement subtract(final ECFieldElement ecFieldElement) {
        final int[] create = Nat256.create();
        SecP256R1Field.subtract(this.x, ((SecP256R1FieldElement)ecFieldElement).x, create);
        return new SecP256R1FieldElement(create);
    }
    
    @Override
    public ECFieldElement multiply(final ECFieldElement ecFieldElement) {
        final int[] create = Nat256.create();
        SecP256R1Field.multiply(this.x, ((SecP256R1FieldElement)ecFieldElement).x, create);
        return new SecP256R1FieldElement(create);
    }
    
    @Override
    public ECFieldElement divide(final ECFieldElement ecFieldElement) {
        final int[] create = Nat256.create();
        Mod.invert(SecP256R1Field.P, ((SecP256R1FieldElement)ecFieldElement).x, create);
        SecP256R1Field.multiply(create, this.x, create);
        return new SecP256R1FieldElement(create);
    }
    
    @Override
    public ECFieldElement negate() {
        final int[] create = Nat256.create();
        SecP256R1Field.negate(this.x, create);
        return new SecP256R1FieldElement(create);
    }
    
    @Override
    public ECFieldElement square() {
        final int[] create = Nat256.create();
        SecP256R1Field.square(this.x, create);
        return new SecP256R1FieldElement(create);
    }
    
    @Override
    public ECFieldElement invert() {
        final int[] create = Nat256.create();
        Mod.invert(SecP256R1Field.P, this.x, create);
        return new SecP256R1FieldElement(create);
    }
    
    @Override
    public ECFieldElement sqrt() {
        final int[] x = this.x;
        if (Nat256.isZero(x) || Nat256.isOne(x)) {
            return this;
        }
        final int[] create = Nat256.create();
        final int[] create2 = Nat256.create();
        SecP256R1Field.square(x, create);
        SecP256R1Field.multiply(create, x, create);
        SecP256R1Field.squareN(create, 2, create2);
        SecP256R1Field.multiply(create2, create, create2);
        SecP256R1Field.squareN(create2, 4, create);
        SecP256R1Field.multiply(create, create2, create);
        SecP256R1Field.squareN(create, 8, create2);
        SecP256R1Field.multiply(create2, create, create2);
        SecP256R1Field.squareN(create2, 16, create);
        SecP256R1Field.multiply(create, create2, create);
        SecP256R1Field.squareN(create, 32, create);
        SecP256R1Field.multiply(create, x, create);
        SecP256R1Field.squareN(create, 96, create);
        SecP256R1Field.multiply(create, x, create);
        SecP256R1Field.squareN(create, 94, create);
        SecP256R1Field.square(create, create2);
        return Nat256.eq(x, create2) ? new SecP256R1FieldElement(create) : null;
    }
    
    @Override
    public boolean equals(final Object o) {
        return o == this || (o instanceof SecP256R1FieldElement && Nat256.eq(this.x, ((SecP256R1FieldElement)o).x));
    }
    
    @Override
    public int hashCode() {
        return SecP256R1FieldElement.Q.hashCode() ^ Arrays.hashCode(this.x, 0, 8);
    }
    
    static {
        Q = SecP256R1Curve.q;
    }
}
