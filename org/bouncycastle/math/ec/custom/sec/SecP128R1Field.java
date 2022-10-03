package org.bouncycastle.math.ec.custom.sec;

import java.math.BigInteger;
import org.bouncycastle.math.raw.Nat;
import org.bouncycastle.math.raw.Nat256;
import org.bouncycastle.math.raw.Nat128;

public class SecP128R1Field
{
    private static final long M = 4294967295L;
    static final int[] P;
    static final int[] PExt;
    private static final int[] PExtInv;
    private static final int P3s1 = 2147483646;
    private static final int PExt7s1 = 2147483646;
    
    public static void add(final int[] array, final int[] array2, final int[] array3) {
        if (Nat128.add(array, array2, array3) != 0 || (array3[3] >>> 1 >= 2147483646 && Nat128.gte(array3, SecP128R1Field.P))) {
            addPInvTo(array3);
        }
    }
    
    public static void addExt(final int[] array, final int[] array2, final int[] array3) {
        if (Nat256.add(array, array2, array3) != 0 || (array3[7] >>> 1 >= 2147483646 && Nat256.gte(array3, SecP128R1Field.PExt))) {
            Nat.addTo(SecP128R1Field.PExtInv.length, SecP128R1Field.PExtInv, array3);
        }
    }
    
    public static void addOne(final int[] array, final int[] array2) {
        if (Nat.inc(4, array, array2) != 0 || (array2[3] >>> 1 >= 2147483646 && Nat128.gte(array2, SecP128R1Field.P))) {
            addPInvTo(array2);
        }
    }
    
    public static int[] fromBigInteger(final BigInteger bigInteger) {
        final int[] fromBigInteger = Nat128.fromBigInteger(bigInteger);
        if (fromBigInteger[3] >>> 1 >= 2147483646 && Nat128.gte(fromBigInteger, SecP128R1Field.P)) {
            Nat128.subFrom(SecP128R1Field.P, fromBigInteger);
        }
        return fromBigInteger;
    }
    
    public static void half(final int[] array, final int[] array2) {
        if ((array[0] & 0x1) == 0x0) {
            Nat.shiftDownBit(4, array, 0, array2);
        }
        else {
            Nat.shiftDownBit(4, array2, Nat128.add(array, SecP128R1Field.P, array2));
        }
    }
    
    public static void multiply(final int[] array, final int[] array2, final int[] array3) {
        final int[] ext = Nat128.createExt();
        Nat128.mul(array, array2, ext);
        reduce(ext, array3);
    }
    
    public static void multiplyAddToExt(final int[] array, final int[] array2, final int[] array3) {
        if (Nat128.mulAddTo(array, array2, array3) != 0 || (array3[7] >>> 1 >= 2147483646 && Nat256.gte(array3, SecP128R1Field.PExt))) {
            Nat.addTo(SecP128R1Field.PExtInv.length, SecP128R1Field.PExtInv, array3);
        }
    }
    
    public static void negate(final int[] array, final int[] array2) {
        if (Nat128.isZero(array)) {
            Nat128.zero(array2);
        }
        else {
            Nat128.sub(SecP128R1Field.P, array, array2);
        }
    }
    
    public static void reduce(final int[] array, final int[] array2) {
        final long n = (long)array[0] & 0xFFFFFFFFL;
        final long n2 = (long)array[1] & 0xFFFFFFFFL;
        final long n3 = (long)array[2] & 0xFFFFFFFFL;
        final long n4 = (long)array[3] & 0xFFFFFFFFL;
        final long n5 = (long)array[4] & 0xFFFFFFFFL;
        final long n6 = (long)array[5] & 0xFFFFFFFFL;
        final long n7 = (long)array[6] & 0xFFFFFFFFL;
        final long n8 = (long)array[7] & 0xFFFFFFFFL;
        final long n9 = n4 + n8;
        final long n10 = n7 + (n8 << 1);
        final long n11 = n3 + n10;
        final long n12 = n6 + (n10 << 1);
        final long n13 = n2 + n12;
        final long n14 = n5 + (n12 << 1);
        final long n15 = n + n14;
        final long n16 = n9 + (n14 << 1);
        array2[0] = (int)n15;
        final long n17 = n13 + (n15 >>> 32);
        array2[1] = (int)n17;
        final long n18 = n11 + (n17 >>> 32);
        array2[2] = (int)n18;
        final long n19 = n16 + (n18 >>> 32);
        array2[3] = (int)n19;
        reduce32((int)(n19 >>> 32), array2);
    }
    
    public static void reduce32(int i, final int[] array) {
        while (i != 0) {
            final long n = (long)i & 0xFFFFFFFFL;
            final long n2 = ((long)array[0] & 0xFFFFFFFFL) + n;
            array[0] = (int)n2;
            long n3 = n2 >> 32;
            if (n3 != 0L) {
                final long n4 = n3 + ((long)array[1] & 0xFFFFFFFFL);
                array[1] = (int)n4;
                final long n5 = (n4 >> 32) + ((long)array[2] & 0xFFFFFFFFL);
                array[2] = (int)n5;
                n3 = n5 >> 32;
            }
            final long n6 = n3 + (((long)array[3] & 0xFFFFFFFFL) + (n << 1));
            array[3] = (int)n6;
            i = (int)(n6 >> 32);
        }
    }
    
    public static void square(final int[] array, final int[] array2) {
        final int[] ext = Nat128.createExt();
        Nat128.square(array, ext);
        reduce(ext, array2);
    }
    
    public static void squareN(final int[] array, int n, final int[] array2) {
        final int[] ext = Nat128.createExt();
        Nat128.square(array, ext);
        reduce(ext, array2);
        while (--n > 0) {
            Nat128.square(array2, ext);
            reduce(ext, array2);
        }
    }
    
    public static void subtract(final int[] array, final int[] array2, final int[] array3) {
        if (Nat128.sub(array, array2, array3) != 0) {
            subPInvFrom(array3);
        }
    }
    
    public static void subtractExt(final int[] array, final int[] array2, final int[] array3) {
        if (Nat.sub(10, array, array2, array3) != 0) {
            Nat.subFrom(SecP128R1Field.PExtInv.length, SecP128R1Field.PExtInv, array3);
        }
    }
    
    public static void twice(final int[] array, final int[] array2) {
        if (Nat.shiftUpBit(4, array, 0, array2) != 0 || (array2[3] >>> 1 >= 2147483646 && Nat128.gte(array2, SecP128R1Field.P))) {
            addPInvTo(array2);
        }
    }
    
    private static void addPInvTo(final int[] array) {
        final long n = ((long)array[0] & 0xFFFFFFFFL) + 1L;
        array[0] = (int)n;
        long n2 = n >> 32;
        if (n2 != 0L) {
            final long n3 = n2 + ((long)array[1] & 0xFFFFFFFFL);
            array[1] = (int)n3;
            final long n4 = (n3 >> 32) + ((long)array[2] & 0xFFFFFFFFL);
            array[2] = (int)n4;
            n2 = n4 >> 32;
        }
        array[3] = (int)(n2 + (((long)array[3] & 0xFFFFFFFFL) + 2L));
    }
    
    private static void subPInvFrom(final int[] array) {
        final long n = ((long)array[0] & 0xFFFFFFFFL) - 1L;
        array[0] = (int)n;
        long n2 = n >> 32;
        if (n2 != 0L) {
            final long n3 = n2 + ((long)array[1] & 0xFFFFFFFFL);
            array[1] = (int)n3;
            final long n4 = (n3 >> 32) + ((long)array[2] & 0xFFFFFFFFL);
            array[2] = (int)n4;
            n2 = n4 >> 32;
        }
        array[3] = (int)(n2 + (((long)array[3] & 0xFFFFFFFFL) - 2L));
    }
    
    static {
        P = new int[] { -1, -1, -1, -3 };
        PExt = new int[] { 1, 0, 0, 4, -2, -1, 3, -4 };
        PExtInv = new int[] { -1, -1, -1, -5, 1, 0, -4, 3 };
    }
}
