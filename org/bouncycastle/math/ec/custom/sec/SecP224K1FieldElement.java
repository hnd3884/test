package org.bouncycastle.math.ec.custom.sec;

import org.bouncycastle.util.Arrays;
import org.bouncycastle.math.raw.Mod;
import org.bouncycastle.math.raw.Nat224;
import java.math.BigInteger;
import org.bouncycastle.math.ec.ECFieldElement;

public class SecP224K1FieldElement extends ECFieldElement
{
    public static final BigInteger Q;
    private static final int[] PRECOMP_POW2;
    protected int[] x;
    
    public SecP224K1FieldElement(final BigInteger bigInteger) {
        if (bigInteger == null || bigInteger.signum() < 0 || bigInteger.compareTo(SecP224K1FieldElement.Q) >= 0) {
            throw new IllegalArgumentException("x value invalid for SecP224K1FieldElement");
        }
        this.x = SecP224K1Field.fromBigInteger(bigInteger);
    }
    
    public SecP224K1FieldElement() {
        this.x = Nat224.create();
    }
    
    protected SecP224K1FieldElement(final int[] x) {
        this.x = x;
    }
    
    @Override
    public boolean isZero() {
        return Nat224.isZero(this.x);
    }
    
    @Override
    public boolean isOne() {
        return Nat224.isOne(this.x);
    }
    
    @Override
    public boolean testBitZero() {
        return Nat224.getBit(this.x, 0) == 1;
    }
    
    @Override
    public BigInteger toBigInteger() {
        return Nat224.toBigInteger(this.x);
    }
    
    @Override
    public String getFieldName() {
        return "SecP224K1Field";
    }
    
    @Override
    public int getFieldSize() {
        return SecP224K1FieldElement.Q.bitLength();
    }
    
    @Override
    public ECFieldElement add(final ECFieldElement ecFieldElement) {
        final int[] create = Nat224.create();
        SecP224K1Field.add(this.x, ((SecP224K1FieldElement)ecFieldElement).x, create);
        return new SecP224K1FieldElement(create);
    }
    
    @Override
    public ECFieldElement addOne() {
        final int[] create = Nat224.create();
        SecP224K1Field.addOne(this.x, create);
        return new SecP224K1FieldElement(create);
    }
    
    @Override
    public ECFieldElement subtract(final ECFieldElement ecFieldElement) {
        final int[] create = Nat224.create();
        SecP224K1Field.subtract(this.x, ((SecP224K1FieldElement)ecFieldElement).x, create);
        return new SecP224K1FieldElement(create);
    }
    
    @Override
    public ECFieldElement multiply(final ECFieldElement ecFieldElement) {
        final int[] create = Nat224.create();
        SecP224K1Field.multiply(this.x, ((SecP224K1FieldElement)ecFieldElement).x, create);
        return new SecP224K1FieldElement(create);
    }
    
    @Override
    public ECFieldElement divide(final ECFieldElement ecFieldElement) {
        final int[] create = Nat224.create();
        Mod.invert(SecP224K1Field.P, ((SecP224K1FieldElement)ecFieldElement).x, create);
        SecP224K1Field.multiply(create, this.x, create);
        return new SecP224K1FieldElement(create);
    }
    
    @Override
    public ECFieldElement negate() {
        final int[] create = Nat224.create();
        SecP224K1Field.negate(this.x, create);
        return new SecP224K1FieldElement(create);
    }
    
    @Override
    public ECFieldElement square() {
        final int[] create = Nat224.create();
        SecP224K1Field.square(this.x, create);
        return new SecP224K1FieldElement(create);
    }
    
    @Override
    public ECFieldElement invert() {
        final int[] create = Nat224.create();
        Mod.invert(SecP224K1Field.P, this.x, create);
        return new SecP224K1FieldElement(create);
    }
    
    @Override
    public ECFieldElement sqrt() {
        final int[] x = this.x;
        if (Nat224.isZero(x) || Nat224.isOne(x)) {
            return this;
        }
        final int[] create = Nat224.create();
        SecP224K1Field.square(x, create);
        SecP224K1Field.multiply(create, x, create);
        final int[] array = create;
        SecP224K1Field.square(create, array);
        SecP224K1Field.multiply(array, x, array);
        final int[] create2 = Nat224.create();
        SecP224K1Field.square(array, create2);
        SecP224K1Field.multiply(create2, x, create2);
        final int[] create3 = Nat224.create();
        SecP224K1Field.squareN(create2, 4, create3);
        SecP224K1Field.multiply(create3, create2, create3);
        final int[] create4 = Nat224.create();
        SecP224K1Field.squareN(create3, 3, create4);
        SecP224K1Field.multiply(create4, array, create4);
        final int[] array2 = create4;
        SecP224K1Field.squareN(create4, 8, array2);
        SecP224K1Field.multiply(array2, create3, array2);
        final int[] array3 = create3;
        SecP224K1Field.squareN(array2, 4, array3);
        SecP224K1Field.multiply(array3, create2, array3);
        final int[] array4 = create2;
        SecP224K1Field.squareN(array3, 19, array4);
        SecP224K1Field.multiply(array4, array2, array4);
        final int[] create5 = Nat224.create();
        SecP224K1Field.squareN(array4, 42, create5);
        SecP224K1Field.multiply(create5, array4, create5);
        final int[] array5 = array4;
        SecP224K1Field.squareN(create5, 23, array5);
        SecP224K1Field.multiply(array5, array3, array5);
        final int[] array6 = array3;
        SecP224K1Field.squareN(array5, 84, array6);
        SecP224K1Field.multiply(array6, create5, array6);
        final int[] array7 = array6;
        SecP224K1Field.squareN(array7, 20, array7);
        SecP224K1Field.multiply(array7, array2, array7);
        SecP224K1Field.squareN(array7, 3, array7);
        SecP224K1Field.multiply(array7, x, array7);
        SecP224K1Field.squareN(array7, 2, array7);
        SecP224K1Field.multiply(array7, x, array7);
        SecP224K1Field.squareN(array7, 4, array7);
        SecP224K1Field.multiply(array7, array, array7);
        SecP224K1Field.square(array7, array7);
        final int[] array8 = create5;
        SecP224K1Field.square(array7, array8);
        if (Nat224.eq(x, array8)) {
            return new SecP224K1FieldElement(array7);
        }
        SecP224K1Field.multiply(array7, SecP224K1FieldElement.PRECOMP_POW2, array7);
        SecP224K1Field.square(array7, array8);
        if (Nat224.eq(x, array8)) {
            return new SecP224K1FieldElement(array7);
        }
        return null;
    }
    
    @Override
    public boolean equals(final Object o) {
        return o == this || (o instanceof SecP224K1FieldElement && Nat224.eq(this.x, ((SecP224K1FieldElement)o).x));
    }
    
    @Override
    public int hashCode() {
        return SecP224K1FieldElement.Q.hashCode() ^ Arrays.hashCode(this.x, 0, 7);
    }
    
    static {
        Q = SecP224K1Curve.q;
        PRECOMP_POW2 = new int[] { 868209154, -587542221, 579297866, -1014948952, -1470801668, 514782679, -1897982644 };
    }
}
