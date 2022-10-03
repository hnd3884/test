package org.bouncycastle.math.ec.custom.sec;

import org.bouncycastle.math.raw.Nat;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.math.raw.Mod;
import org.bouncycastle.math.raw.Nat224;
import java.math.BigInteger;
import org.bouncycastle.math.ec.ECFieldElement;

public class SecP224R1FieldElement extends ECFieldElement
{
    public static final BigInteger Q;
    protected int[] x;
    
    public SecP224R1FieldElement(final BigInteger bigInteger) {
        if (bigInteger == null || bigInteger.signum() < 0 || bigInteger.compareTo(SecP224R1FieldElement.Q) >= 0) {
            throw new IllegalArgumentException("x value invalid for SecP224R1FieldElement");
        }
        this.x = SecP224R1Field.fromBigInteger(bigInteger);
    }
    
    public SecP224R1FieldElement() {
        this.x = Nat224.create();
    }
    
    protected SecP224R1FieldElement(final int[] x) {
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
        return "SecP224R1Field";
    }
    
    @Override
    public int getFieldSize() {
        return SecP224R1FieldElement.Q.bitLength();
    }
    
    @Override
    public ECFieldElement add(final ECFieldElement ecFieldElement) {
        final int[] create = Nat224.create();
        SecP224R1Field.add(this.x, ((SecP224R1FieldElement)ecFieldElement).x, create);
        return new SecP224R1FieldElement(create);
    }
    
    @Override
    public ECFieldElement addOne() {
        final int[] create = Nat224.create();
        SecP224R1Field.addOne(this.x, create);
        return new SecP224R1FieldElement(create);
    }
    
    @Override
    public ECFieldElement subtract(final ECFieldElement ecFieldElement) {
        final int[] create = Nat224.create();
        SecP224R1Field.subtract(this.x, ((SecP224R1FieldElement)ecFieldElement).x, create);
        return new SecP224R1FieldElement(create);
    }
    
    @Override
    public ECFieldElement multiply(final ECFieldElement ecFieldElement) {
        final int[] create = Nat224.create();
        SecP224R1Field.multiply(this.x, ((SecP224R1FieldElement)ecFieldElement).x, create);
        return new SecP224R1FieldElement(create);
    }
    
    @Override
    public ECFieldElement divide(final ECFieldElement ecFieldElement) {
        final int[] create = Nat224.create();
        Mod.invert(SecP224R1Field.P, ((SecP224R1FieldElement)ecFieldElement).x, create);
        SecP224R1Field.multiply(create, this.x, create);
        return new SecP224R1FieldElement(create);
    }
    
    @Override
    public ECFieldElement negate() {
        final int[] create = Nat224.create();
        SecP224R1Field.negate(this.x, create);
        return new SecP224R1FieldElement(create);
    }
    
    @Override
    public ECFieldElement square() {
        final int[] create = Nat224.create();
        SecP224R1Field.square(this.x, create);
        return new SecP224R1FieldElement(create);
    }
    
    @Override
    public ECFieldElement invert() {
        final int[] create = Nat224.create();
        Mod.invert(SecP224R1Field.P, this.x, create);
        return new SecP224R1FieldElement(create);
    }
    
    @Override
    public ECFieldElement sqrt() {
        final int[] x = this.x;
        if (Nat224.isZero(x) || Nat224.isOne(x)) {
            return this;
        }
        final int[] create = Nat224.create();
        SecP224R1Field.negate(x, create);
        final int[] random = Mod.random(SecP224R1Field.P);
        final int[] create2 = Nat224.create();
        if (!isSquare(x)) {
            return null;
        }
        while (!trySqrt(create, random, create2)) {
            SecP224R1Field.addOne(random, random);
        }
        SecP224R1Field.square(create2, random);
        return Nat224.eq(x, random) ? new SecP224R1FieldElement(create2) : null;
    }
    
    @Override
    public boolean equals(final Object o) {
        return o == this || (o instanceof SecP224R1FieldElement && Nat224.eq(this.x, ((SecP224R1FieldElement)o).x));
    }
    
    @Override
    public int hashCode() {
        return SecP224R1FieldElement.Q.hashCode() ^ Arrays.hashCode(this.x, 0, 7);
    }
    
    private static boolean isSquare(final int[] array) {
        final int[] create = Nat224.create();
        final int[] create2 = Nat224.create();
        Nat224.copy(array, create);
        for (int i = 0; i < 7; ++i) {
            Nat224.copy(create, create2);
            SecP224R1Field.squareN(create, 1 << i, create);
            SecP224R1Field.multiply(create, create2, create);
        }
        SecP224R1Field.squareN(create, 95, create);
        return Nat224.isOne(create);
    }
    
    private static void RM(final int[] array, final int[] array2, final int[] array3, final int[] array4, final int[] array5, final int[] array6, final int[] array7) {
        SecP224R1Field.multiply(array5, array3, array7);
        SecP224R1Field.multiply(array7, array, array7);
        SecP224R1Field.multiply(array4, array2, array6);
        SecP224R1Field.add(array6, array7, array6);
        SecP224R1Field.multiply(array4, array3, array7);
        Nat224.copy(array6, array4);
        SecP224R1Field.multiply(array5, array2, array5);
        SecP224R1Field.add(array5, array7, array5);
        SecP224R1Field.square(array5, array6);
        SecP224R1Field.multiply(array6, array, array6);
    }
    
    private static void RP(final int[] array, final int[] array2, final int[] array3, final int[] array4, final int[] array5) {
        Nat224.copy(array, array4);
        final int[] create = Nat224.create();
        final int[] create2 = Nat224.create();
        for (int i = 0; i < 7; ++i) {
            Nat224.copy(array2, create);
            Nat224.copy(array3, create2);
            int n = 1 << i;
            while (--n >= 0) {
                RS(array2, array3, array4, array5);
            }
            RM(array, create, create2, array2, array3, array4, array5);
        }
    }
    
    private static void RS(final int[] array, final int[] array2, final int[] array3, final int[] array4) {
        SecP224R1Field.multiply(array2, array, array2);
        SecP224R1Field.twice(array2, array2);
        SecP224R1Field.square(array, array4);
        SecP224R1Field.add(array3, array4, array);
        SecP224R1Field.multiply(array3, array4, array3);
        SecP224R1Field.reduce32(Nat.shiftUpBits(7, array3, 2, 0), array3);
    }
    
    private static boolean trySqrt(final int[] array, final int[] array2, final int[] array3) {
        final int[] create = Nat224.create();
        Nat224.copy(array2, create);
        final int[] create2 = Nat224.create();
        create2[0] = 1;
        final int[] create3 = Nat224.create();
        RP(array, create, create2, create3, array3);
        final int[] create4 = Nat224.create();
        final int[] create5 = Nat224.create();
        for (int i = 1; i < 96; ++i) {
            Nat224.copy(create, create4);
            Nat224.copy(create2, create5);
            RS(create, create2, create3, array3);
            if (Nat224.isZero(create)) {
                Mod.invert(SecP224R1Field.P, create5, array3);
                SecP224R1Field.multiply(array3, create4, array3);
                return true;
            }
        }
        return false;
    }
    
    static {
        Q = SecP224R1Curve.q;
    }
}
