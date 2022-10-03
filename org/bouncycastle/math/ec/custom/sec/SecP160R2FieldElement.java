package org.bouncycastle.math.ec.custom.sec;

import org.bouncycastle.util.Arrays;
import org.bouncycastle.math.raw.Mod;
import org.bouncycastle.math.raw.Nat160;
import java.math.BigInteger;
import org.bouncycastle.math.ec.ECFieldElement;

public class SecP160R2FieldElement extends ECFieldElement
{
    public static final BigInteger Q;
    protected int[] x;
    
    public SecP160R2FieldElement(final BigInteger bigInteger) {
        if (bigInteger == null || bigInteger.signum() < 0 || bigInteger.compareTo(SecP160R2FieldElement.Q) >= 0) {
            throw new IllegalArgumentException("x value invalid for SecP160R2FieldElement");
        }
        this.x = SecP160R2Field.fromBigInteger(bigInteger);
    }
    
    public SecP160R2FieldElement() {
        this.x = Nat160.create();
    }
    
    protected SecP160R2FieldElement(final int[] x) {
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
        return "SecP160R2Field";
    }
    
    @Override
    public int getFieldSize() {
        return SecP160R2FieldElement.Q.bitLength();
    }
    
    @Override
    public ECFieldElement add(final ECFieldElement ecFieldElement) {
        final int[] create = Nat160.create();
        SecP160R2Field.add(this.x, ((SecP160R2FieldElement)ecFieldElement).x, create);
        return new SecP160R2FieldElement(create);
    }
    
    @Override
    public ECFieldElement addOne() {
        final int[] create = Nat160.create();
        SecP160R2Field.addOne(this.x, create);
        return new SecP160R2FieldElement(create);
    }
    
    @Override
    public ECFieldElement subtract(final ECFieldElement ecFieldElement) {
        final int[] create = Nat160.create();
        SecP160R2Field.subtract(this.x, ((SecP160R2FieldElement)ecFieldElement).x, create);
        return new SecP160R2FieldElement(create);
    }
    
    @Override
    public ECFieldElement multiply(final ECFieldElement ecFieldElement) {
        final int[] create = Nat160.create();
        SecP160R2Field.multiply(this.x, ((SecP160R2FieldElement)ecFieldElement).x, create);
        return new SecP160R2FieldElement(create);
    }
    
    @Override
    public ECFieldElement divide(final ECFieldElement ecFieldElement) {
        final int[] create = Nat160.create();
        Mod.invert(SecP160R2Field.P, ((SecP160R2FieldElement)ecFieldElement).x, create);
        SecP160R2Field.multiply(create, this.x, create);
        return new SecP160R2FieldElement(create);
    }
    
    @Override
    public ECFieldElement negate() {
        final int[] create = Nat160.create();
        SecP160R2Field.negate(this.x, create);
        return new SecP160R2FieldElement(create);
    }
    
    @Override
    public ECFieldElement square() {
        final int[] create = Nat160.create();
        SecP160R2Field.square(this.x, create);
        return new SecP160R2FieldElement(create);
    }
    
    @Override
    public ECFieldElement invert() {
        final int[] create = Nat160.create();
        Mod.invert(SecP160R2Field.P, this.x, create);
        return new SecP160R2FieldElement(create);
    }
    
    @Override
    public ECFieldElement sqrt() {
        final int[] x = this.x;
        if (Nat160.isZero(x) || Nat160.isOne(x)) {
            return this;
        }
        final int[] create = Nat160.create();
        SecP160R2Field.square(x, create);
        SecP160R2Field.multiply(create, x, create);
        final int[] create2 = Nat160.create();
        SecP160R2Field.square(create, create2);
        SecP160R2Field.multiply(create2, x, create2);
        final int[] create3 = Nat160.create();
        SecP160R2Field.square(create2, create3);
        SecP160R2Field.multiply(create3, x, create3);
        final int[] create4 = Nat160.create();
        SecP160R2Field.squareN(create3, 3, create4);
        SecP160R2Field.multiply(create4, create2, create4);
        final int[] array = create3;
        SecP160R2Field.squareN(create4, 7, array);
        SecP160R2Field.multiply(array, create4, array);
        final int[] array2 = create4;
        SecP160R2Field.squareN(array, 3, array2);
        SecP160R2Field.multiply(array2, create2, array2);
        final int[] create5 = Nat160.create();
        SecP160R2Field.squareN(array2, 14, create5);
        SecP160R2Field.multiply(create5, array, create5);
        final int[] array3 = array;
        SecP160R2Field.squareN(create5, 31, array3);
        SecP160R2Field.multiply(array3, create5, array3);
        final int[] array4 = create5;
        SecP160R2Field.squareN(array3, 62, array4);
        SecP160R2Field.multiply(array4, array3, array4);
        final int[] array5 = array3;
        SecP160R2Field.squareN(array4, 3, array5);
        SecP160R2Field.multiply(array5, create2, array5);
        final int[] array6 = array5;
        SecP160R2Field.squareN(array6, 18, array6);
        SecP160R2Field.multiply(array6, array2, array6);
        SecP160R2Field.squareN(array6, 2, array6);
        SecP160R2Field.multiply(array6, x, array6);
        SecP160R2Field.squareN(array6, 3, array6);
        SecP160R2Field.multiply(array6, create, array6);
        SecP160R2Field.squareN(array6, 6, array6);
        SecP160R2Field.multiply(array6, create2, array6);
        SecP160R2Field.squareN(array6, 2, array6);
        SecP160R2Field.multiply(array6, x, array6);
        final int[] array7 = create;
        SecP160R2Field.square(array6, array7);
        return Nat160.eq(x, array7) ? new SecP160R2FieldElement(array6) : null;
    }
    
    @Override
    public boolean equals(final Object o) {
        return o == this || (o instanceof SecP160R2FieldElement && Nat160.eq(this.x, ((SecP160R2FieldElement)o).x));
    }
    
    @Override
    public int hashCode() {
        return SecP160R2FieldElement.Q.hashCode() ^ Arrays.hashCode(this.x, 0, 5);
    }
    
    static {
        Q = SecP160R2Curve.q;
    }
}
