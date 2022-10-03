package org.bouncycastle.math.ec.custom.djb;

import org.bouncycastle.util.Arrays;
import org.bouncycastle.math.raw.Mod;
import org.bouncycastle.math.raw.Nat256;
import java.math.BigInteger;
import org.bouncycastle.math.ec.ECFieldElement;

public class Curve25519FieldElement extends ECFieldElement
{
    public static final BigInteger Q;
    private static final int[] PRECOMP_POW2;
    protected int[] x;
    
    public Curve25519FieldElement(final BigInteger bigInteger) {
        if (bigInteger == null || bigInteger.signum() < 0 || bigInteger.compareTo(Curve25519FieldElement.Q) >= 0) {
            throw new IllegalArgumentException("x value invalid for Curve25519FieldElement");
        }
        this.x = Curve25519Field.fromBigInteger(bigInteger);
    }
    
    public Curve25519FieldElement() {
        this.x = Nat256.create();
    }
    
    protected Curve25519FieldElement(final int[] x) {
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
        return "Curve25519Field";
    }
    
    @Override
    public int getFieldSize() {
        return Curve25519FieldElement.Q.bitLength();
    }
    
    @Override
    public ECFieldElement add(final ECFieldElement ecFieldElement) {
        final int[] create = Nat256.create();
        Curve25519Field.add(this.x, ((Curve25519FieldElement)ecFieldElement).x, create);
        return new Curve25519FieldElement(create);
    }
    
    @Override
    public ECFieldElement addOne() {
        final int[] create = Nat256.create();
        Curve25519Field.addOne(this.x, create);
        return new Curve25519FieldElement(create);
    }
    
    @Override
    public ECFieldElement subtract(final ECFieldElement ecFieldElement) {
        final int[] create = Nat256.create();
        Curve25519Field.subtract(this.x, ((Curve25519FieldElement)ecFieldElement).x, create);
        return new Curve25519FieldElement(create);
    }
    
    @Override
    public ECFieldElement multiply(final ECFieldElement ecFieldElement) {
        final int[] create = Nat256.create();
        Curve25519Field.multiply(this.x, ((Curve25519FieldElement)ecFieldElement).x, create);
        return new Curve25519FieldElement(create);
    }
    
    @Override
    public ECFieldElement divide(final ECFieldElement ecFieldElement) {
        final int[] create = Nat256.create();
        Mod.invert(Curve25519Field.P, ((Curve25519FieldElement)ecFieldElement).x, create);
        Curve25519Field.multiply(create, this.x, create);
        return new Curve25519FieldElement(create);
    }
    
    @Override
    public ECFieldElement negate() {
        final int[] create = Nat256.create();
        Curve25519Field.negate(this.x, create);
        return new Curve25519FieldElement(create);
    }
    
    @Override
    public ECFieldElement square() {
        final int[] create = Nat256.create();
        Curve25519Field.square(this.x, create);
        return new Curve25519FieldElement(create);
    }
    
    @Override
    public ECFieldElement invert() {
        final int[] create = Nat256.create();
        Mod.invert(Curve25519Field.P, this.x, create);
        return new Curve25519FieldElement(create);
    }
    
    @Override
    public ECFieldElement sqrt() {
        final int[] x = this.x;
        if (Nat256.isZero(x) || Nat256.isOne(x)) {
            return this;
        }
        final int[] create = Nat256.create();
        Curve25519Field.square(x, create);
        Curve25519Field.multiply(create, x, create);
        final int[] array = create;
        Curve25519Field.square(create, array);
        Curve25519Field.multiply(array, x, array);
        final int[] create2 = Nat256.create();
        Curve25519Field.square(array, create2);
        Curve25519Field.multiply(create2, x, create2);
        final int[] create3 = Nat256.create();
        Curve25519Field.squareN(create2, 3, create3);
        Curve25519Field.multiply(create3, array, create3);
        final int[] array2 = array;
        Curve25519Field.squareN(create3, 4, array2);
        Curve25519Field.multiply(array2, create2, array2);
        final int[] array3 = create3;
        Curve25519Field.squareN(array2, 4, array3);
        Curve25519Field.multiply(array3, create2, array3);
        final int[] array4 = create2;
        Curve25519Field.squareN(array3, 15, array4);
        Curve25519Field.multiply(array4, array3, array4);
        final int[] array5 = array3;
        Curve25519Field.squareN(array4, 30, array5);
        Curve25519Field.multiply(array5, array4, array5);
        final int[] array6 = array4;
        Curve25519Field.squareN(array5, 60, array6);
        Curve25519Field.multiply(array6, array5, array6);
        final int[] array7 = array5;
        Curve25519Field.squareN(array6, 11, array7);
        Curve25519Field.multiply(array7, array2, array7);
        final int[] array8 = array2;
        Curve25519Field.squareN(array7, 120, array8);
        Curve25519Field.multiply(array8, array6, array8);
        final int[] array9 = array8;
        Curve25519Field.square(array9, array9);
        final int[] array10 = array6;
        Curve25519Field.square(array9, array10);
        if (Nat256.eq(x, array10)) {
            return new Curve25519FieldElement(array9);
        }
        Curve25519Field.multiply(array9, Curve25519FieldElement.PRECOMP_POW2, array9);
        Curve25519Field.square(array9, array10);
        if (Nat256.eq(x, array10)) {
            return new Curve25519FieldElement(array9);
        }
        return null;
    }
    
    @Override
    public boolean equals(final Object o) {
        return o == this || (o instanceof Curve25519FieldElement && Nat256.eq(this.x, ((Curve25519FieldElement)o).x));
    }
    
    @Override
    public int hashCode() {
        return Curve25519FieldElement.Q.hashCode() ^ Arrays.hashCode(this.x, 0, 8);
    }
    
    static {
        Q = Curve25519.q;
        PRECOMP_POW2 = new int[] { 1242472624, -991028441, -1389370248, 792926214, 1039914919, 726466713, 1338105611, 730014848 };
    }
}
