package org.bouncycastle.math.ec.custom.sec;

import java.math.BigInteger;
import org.bouncycastle.math.raw.Nat;
import org.bouncycastle.math.raw.Nat192;

public class SecP192K1Field
{
    static final int[] P;
    static final int[] PExt;
    private static final int[] PExtInv;
    private static final int P5 = -1;
    private static final int PExt11 = -1;
    private static final int PInv33 = 4553;
    
    public static void add(final int[] array, final int[] array2, final int[] array3) {
        if (Nat192.add(array, array2, array3) != 0 || (array3[5] == -1 && Nat192.gte(array3, SecP192K1Field.P))) {
            Nat.add33To(6, 4553, array3);
        }
    }
    
    public static void addExt(final int[] array, final int[] array2, final int[] array3) {
        if ((Nat.add(12, array, array2, array3) != 0 || (array3[11] == -1 && Nat.gte(12, array3, SecP192K1Field.PExt))) && Nat.addTo(SecP192K1Field.PExtInv.length, SecP192K1Field.PExtInv, array3) != 0) {
            Nat.incAt(12, array3, SecP192K1Field.PExtInv.length);
        }
    }
    
    public static void addOne(final int[] array, final int[] array2) {
        if (Nat.inc(6, array, array2) != 0 || (array2[5] == -1 && Nat192.gte(array2, SecP192K1Field.P))) {
            Nat.add33To(6, 4553, array2);
        }
    }
    
    public static int[] fromBigInteger(final BigInteger bigInteger) {
        final int[] fromBigInteger = Nat192.fromBigInteger(bigInteger);
        if (fromBigInteger[5] == -1 && Nat192.gte(fromBigInteger, SecP192K1Field.P)) {
            Nat192.subFrom(SecP192K1Field.P, fromBigInteger);
        }
        return fromBigInteger;
    }
    
    public static void half(final int[] array, final int[] array2) {
        if ((array[0] & 0x1) == 0x0) {
            Nat.shiftDownBit(6, array, 0, array2);
        }
        else {
            Nat.shiftDownBit(6, array2, Nat192.add(array, SecP192K1Field.P, array2));
        }
    }
    
    public static void multiply(final int[] array, final int[] array2, final int[] array3) {
        final int[] ext = Nat192.createExt();
        Nat192.mul(array, array2, ext);
        reduce(ext, array3);
    }
    
    public static void multiplyAddToExt(final int[] array, final int[] array2, final int[] array3) {
        if ((Nat192.mulAddTo(array, array2, array3) != 0 || (array3[11] == -1 && Nat.gte(12, array3, SecP192K1Field.PExt))) && Nat.addTo(SecP192K1Field.PExtInv.length, SecP192K1Field.PExtInv, array3) != 0) {
            Nat.incAt(12, array3, SecP192K1Field.PExtInv.length);
        }
    }
    
    public static void negate(final int[] array, final int[] array2) {
        if (Nat192.isZero(array)) {
            Nat192.zero(array2);
        }
        else {
            Nat192.sub(SecP192K1Field.P, array, array2);
        }
    }
    
    public static void reduce(final int[] array, final int[] array2) {
        if (Nat192.mul33DWordAdd(4553, Nat192.mul33Add(4553, array, 6, array, 0, array2, 0), array2, 0) != 0 || (array2[5] == -1 && Nat192.gte(array2, SecP192K1Field.P))) {
            Nat.add33To(6, 4553, array2);
        }
    }
    
    public static void reduce32(final int n, final int[] array) {
        if ((n != 0 && Nat192.mul33WordAdd(4553, n, array, 0) != 0) || (array[5] == -1 && Nat192.gte(array, SecP192K1Field.P))) {
            Nat.add33To(6, 4553, array);
        }
    }
    
    public static void square(final int[] array, final int[] array2) {
        final int[] ext = Nat192.createExt();
        Nat192.square(array, ext);
        reduce(ext, array2);
    }
    
    public static void squareN(final int[] array, int n, final int[] array2) {
        final int[] ext = Nat192.createExt();
        Nat192.square(array, ext);
        reduce(ext, array2);
        while (--n > 0) {
            Nat192.square(array2, ext);
            reduce(ext, array2);
        }
    }
    
    public static void subtract(final int[] array, final int[] array2, final int[] array3) {
        if (Nat192.sub(array, array2, array3) != 0) {
            Nat.sub33From(6, 4553, array3);
        }
    }
    
    public static void subtractExt(final int[] array, final int[] array2, final int[] array3) {
        if (Nat.sub(12, array, array2, array3) != 0 && Nat.subFrom(SecP192K1Field.PExtInv.length, SecP192K1Field.PExtInv, array3) != 0) {
            Nat.decAt(12, array3, SecP192K1Field.PExtInv.length);
        }
    }
    
    public static void twice(final int[] array, final int[] array2) {
        if (Nat.shiftUpBit(6, array, 0, array2) != 0 || (array2[5] == -1 && Nat192.gte(array2, SecP192K1Field.P))) {
            Nat.add33To(6, 4553, array2);
        }
    }
    
    static {
        P = new int[] { -4553, -2, -1, -1, -1, -1 };
        PExt = new int[] { 20729809, 9106, 1, 0, 0, 0, -9106, -3, -1, -1, -1, -1 };
        PExtInv = new int[] { -20729809, -9107, -2, -1, -1, -1, 9105, 2 };
    }
}
