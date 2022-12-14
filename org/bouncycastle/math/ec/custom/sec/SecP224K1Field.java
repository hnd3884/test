package org.bouncycastle.math.ec.custom.sec;

import java.math.BigInteger;
import org.bouncycastle.math.raw.Nat;
import org.bouncycastle.math.raw.Nat224;

public class SecP224K1Field
{
    static final int[] P;
    static final int[] PExt;
    private static final int[] PExtInv;
    private static final int P6 = -1;
    private static final int PExt13 = -1;
    private static final int PInv33 = 6803;
    
    public static void add(final int[] array, final int[] array2, final int[] array3) {
        if (Nat224.add(array, array2, array3) != 0 || (array3[6] == -1 && Nat224.gte(array3, SecP224K1Field.P))) {
            Nat.add33To(7, 6803, array3);
        }
    }
    
    public static void addExt(final int[] array, final int[] array2, final int[] array3) {
        if ((Nat.add(14, array, array2, array3) != 0 || (array3[13] == -1 && Nat.gte(14, array3, SecP224K1Field.PExt))) && Nat.addTo(SecP224K1Field.PExtInv.length, SecP224K1Field.PExtInv, array3) != 0) {
            Nat.incAt(14, array3, SecP224K1Field.PExtInv.length);
        }
    }
    
    public static void addOne(final int[] array, final int[] array2) {
        if (Nat.inc(7, array, array2) != 0 || (array2[6] == -1 && Nat224.gte(array2, SecP224K1Field.P))) {
            Nat.add33To(7, 6803, array2);
        }
    }
    
    public static int[] fromBigInteger(final BigInteger bigInteger) {
        final int[] fromBigInteger = Nat224.fromBigInteger(bigInteger);
        if (fromBigInteger[6] == -1 && Nat224.gte(fromBigInteger, SecP224K1Field.P)) {
            Nat.add33To(7, 6803, fromBigInteger);
        }
        return fromBigInteger;
    }
    
    public static void half(final int[] array, final int[] array2) {
        if ((array[0] & 0x1) == 0x0) {
            Nat.shiftDownBit(7, array, 0, array2);
        }
        else {
            Nat.shiftDownBit(7, array2, Nat224.add(array, SecP224K1Field.P, array2));
        }
    }
    
    public static void multiply(final int[] array, final int[] array2, final int[] array3) {
        final int[] ext = Nat224.createExt();
        Nat224.mul(array, array2, ext);
        reduce(ext, array3);
    }
    
    public static void multiplyAddToExt(final int[] array, final int[] array2, final int[] array3) {
        if ((Nat224.mulAddTo(array, array2, array3) != 0 || (array3[13] == -1 && Nat.gte(14, array3, SecP224K1Field.PExt))) && Nat.addTo(SecP224K1Field.PExtInv.length, SecP224K1Field.PExtInv, array3) != 0) {
            Nat.incAt(14, array3, SecP224K1Field.PExtInv.length);
        }
    }
    
    public static void negate(final int[] array, final int[] array2) {
        if (Nat224.isZero(array)) {
            Nat224.zero(array2);
        }
        else {
            Nat224.sub(SecP224K1Field.P, array, array2);
        }
    }
    
    public static void reduce(final int[] array, final int[] array2) {
        if (Nat224.mul33DWordAdd(6803, Nat224.mul33Add(6803, array, 7, array, 0, array2, 0), array2, 0) != 0 || (array2[6] == -1 && Nat224.gte(array2, SecP224K1Field.P))) {
            Nat.add33To(7, 6803, array2);
        }
    }
    
    public static void reduce32(final int n, final int[] array) {
        if ((n != 0 && Nat224.mul33WordAdd(6803, n, array, 0) != 0) || (array[6] == -1 && Nat224.gte(array, SecP224K1Field.P))) {
            Nat.add33To(7, 6803, array);
        }
    }
    
    public static void square(final int[] array, final int[] array2) {
        final int[] ext = Nat224.createExt();
        Nat224.square(array, ext);
        reduce(ext, array2);
    }
    
    public static void squareN(final int[] array, int n, final int[] array2) {
        final int[] ext = Nat224.createExt();
        Nat224.square(array, ext);
        reduce(ext, array2);
        while (--n > 0) {
            Nat224.square(array2, ext);
            reduce(ext, array2);
        }
    }
    
    public static void subtract(final int[] array, final int[] array2, final int[] array3) {
        if (Nat224.sub(array, array2, array3) != 0) {
            Nat.sub33From(7, 6803, array3);
        }
    }
    
    public static void subtractExt(final int[] array, final int[] array2, final int[] array3) {
        if (Nat.sub(14, array, array2, array3) != 0 && Nat.subFrom(SecP224K1Field.PExtInv.length, SecP224K1Field.PExtInv, array3) != 0) {
            Nat.decAt(14, array3, SecP224K1Field.PExtInv.length);
        }
    }
    
    public static void twice(final int[] array, final int[] array2) {
        if (Nat.shiftUpBit(7, array, 0, array2) != 0 || (array2[6] == -1 && Nat224.gte(array2, SecP224K1Field.P))) {
            Nat.add33To(7, 6803, array2);
        }
    }
    
    static {
        P = new int[] { -6803, -2, -1, -1, -1, -1, -1 };
        PExt = new int[] { 46280809, 13606, 1, 0, 0, 0, 0, -13606, -3, -1, -1, -1, -1, -1 };
        PExtInv = new int[] { -46280809, -13607, -2, -1, -1, -1, -1, 13605, 2 };
    }
}
