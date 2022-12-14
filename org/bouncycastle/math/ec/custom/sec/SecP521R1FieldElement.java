package org.bouncycastle.math.ec.custom.sec;

import org.bouncycastle.util.Arrays;
import org.bouncycastle.math.raw.Mod;
import org.bouncycastle.math.raw.Nat;
import java.math.BigInteger;
import org.bouncycastle.math.ec.ECFieldElement;

public class SecP521R1FieldElement extends ECFieldElement
{
    public static final BigInteger Q;
    protected int[] x;
    
    public SecP521R1FieldElement(final BigInteger bigInteger) {
        if (bigInteger == null || bigInteger.signum() < 0 || bigInteger.compareTo(SecP521R1FieldElement.Q) >= 0) {
            throw new IllegalArgumentException("x value invalid for SecP521R1FieldElement");
        }
        this.x = SecP521R1Field.fromBigInteger(bigInteger);
    }
    
    public SecP521R1FieldElement() {
        this.x = Nat.create(17);
    }
    
    protected SecP521R1FieldElement(final int[] x) {
        this.x = x;
    }
    
    @Override
    public boolean isZero() {
        return Nat.isZero(17, this.x);
    }
    
    @Override
    public boolean isOne() {
        return Nat.isOne(17, this.x);
    }
    
    @Override
    public boolean testBitZero() {
        return Nat.getBit(this.x, 0) == 1;
    }
    
    @Override
    public BigInteger toBigInteger() {
        return Nat.toBigInteger(17, this.x);
    }
    
    @Override
    public String getFieldName() {
        return "SecP521R1Field";
    }
    
    @Override
    public int getFieldSize() {
        return SecP521R1FieldElement.Q.bitLength();
    }
    
    @Override
    public ECFieldElement add(final ECFieldElement ecFieldElement) {
        final int[] create = Nat.create(17);
        SecP521R1Field.add(this.x, ((SecP521R1FieldElement)ecFieldElement).x, create);
        return new SecP521R1FieldElement(create);
    }
    
    @Override
    public ECFieldElement addOne() {
        final int[] create = Nat.create(17);
        SecP521R1Field.addOne(this.x, create);
        return new SecP521R1FieldElement(create);
    }
    
    @Override
    public ECFieldElement subtract(final ECFieldElement ecFieldElement) {
        final int[] create = Nat.create(17);
        SecP521R1Field.subtract(this.x, ((SecP521R1FieldElement)ecFieldElement).x, create);
        return new SecP521R1FieldElement(create);
    }
    
    @Override
    public ECFieldElement multiply(final ECFieldElement ecFieldElement) {
        final int[] create = Nat.create(17);
        SecP521R1Field.multiply(this.x, ((SecP521R1FieldElement)ecFieldElement).x, create);
        return new SecP521R1FieldElement(create);
    }
    
    @Override
    public ECFieldElement divide(final ECFieldElement ecFieldElement) {
        final int[] create = Nat.create(17);
        Mod.invert(SecP521R1Field.P, ((SecP521R1FieldElement)ecFieldElement).x, create);
        SecP521R1Field.multiply(create, this.x, create);
        return new SecP521R1FieldElement(create);
    }
    
    @Override
    public ECFieldElement negate() {
        final int[] create = Nat.create(17);
        SecP521R1Field.negate(this.x, create);
        return new SecP521R1FieldElement(create);
    }
    
    @Override
    public ECFieldElement square() {
        final int[] create = Nat.create(17);
        SecP521R1Field.square(this.x, create);
        return new SecP521R1FieldElement(create);
    }
    
    @Override
    public ECFieldElement invert() {
        final int[] create = Nat.create(17);
        Mod.invert(SecP521R1Field.P, this.x, create);
        return new SecP521R1FieldElement(create);
    }
    
    @Override
    public ECFieldElement sqrt() {
        final int[] x = this.x;
        if (Nat.isZero(17, x) || Nat.isOne(17, x)) {
            return this;
        }
        final int[] create = Nat.create(17);
        final int[] create2 = Nat.create(17);
        SecP521R1Field.squareN(x, 519, create);
        SecP521R1Field.square(create, create2);
        return Nat.eq(17, x, create2) ? new SecP521R1FieldElement(create) : null;
    }
    
    @Override
    public boolean equals(final Object o) {
        return o == this || (o instanceof SecP521R1FieldElement && Nat.eq(17, this.x, ((SecP521R1FieldElement)o).x));
    }
    
    @Override
    public int hashCode() {
        return SecP521R1FieldElement.Q.hashCode() ^ Arrays.hashCode(this.x, 0, 17);
    }
    
    static {
        Q = SecP521R1Curve.q;
    }
}
