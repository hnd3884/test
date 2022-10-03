package org.bouncycastle.math.ec.custom.sec;

import java.math.BigInteger;
import org.bouncycastle.math.raw.Nat;
import org.bouncycastle.math.raw.Nat224;

public class SecP224R1Field
{
    private static final long M = 4294967295L;
    static final int[] P;
    static final int[] PExt;
    private static final int[] PExtInv;
    private static final int P6 = -1;
    private static final int PExt13 = -1;
    
    public static void add(final int[] array, final int[] array2, final int[] array3) {
        if (Nat224.add(array, array2, array3) != 0 || (array3[6] == -1 && Nat224.gte(array3, SecP224R1Field.P))) {
            addPInvTo(array3);
        }
    }
    
    public static void addExt(final int[] array, final int[] array2, final int[] array3) {
        if ((Nat.add(14, array, array2, array3) != 0 || (array3[13] == -1 && Nat.gte(14, array3, SecP224R1Field.PExt))) && Nat.addTo(SecP224R1Field.PExtInv.length, SecP224R1Field.PExtInv, array3) != 0) {
            Nat.incAt(14, array3, SecP224R1Field.PExtInv.length);
        }
    }
    
    public static void addOne(final int[] array, final int[] array2) {
        if (Nat.inc(7, array, array2) != 0 || (array2[6] == -1 && Nat224.gte(array2, SecP224R1Field.P))) {
            addPInvTo(array2);
        }
    }
    
    public static int[] fromBigInteger(final BigInteger bigInteger) {
        final int[] fromBigInteger = Nat224.fromBigInteger(bigInteger);
        if (fromBigInteger[6] == -1 && Nat224.gte(fromBigInteger, SecP224R1Field.P)) {
            Nat224.subFrom(SecP224R1Field.P, fromBigInteger);
        }
        return fromBigInteger;
    }
    
    public static void half(final int[] array, final int[] array2) {
        if ((array[0] & 0x1) == 0x0) {
            Nat.shiftDownBit(7, array, 0, array2);
        }
        else {
            Nat.shiftDownBit(7, array2, Nat224.add(array, SecP224R1Field.P, array2));
        }
    }
    
    public static void multiply(final int[] array, final int[] array2, final int[] array3) {
        final int[] ext = Nat224.createExt();
        Nat224.mul(array, array2, ext);
        reduce(ext, array3);
    }
    
    public static void multiplyAddToExt(final int[] array, final int[] array2, final int[] array3) {
        if ((Nat224.mulAddTo(array, array2, array3) != 0 || (array3[13] == -1 && Nat.gte(14, array3, SecP224R1Field.PExt))) && Nat.addTo(SecP224R1Field.PExtInv.length, SecP224R1Field.PExtInv, array3) != 0) {
            Nat.incAt(14, array3, SecP224R1Field.PExtInv.length);
        }
    }
    
    public static void negate(final int[] array, final int[] array2) {
        if (Nat224.isZero(array)) {
            Nat224.zero(array2);
        }
        else {
            Nat224.sub(SecP224R1Field.P, array, array2);
        }
    }
    
    public static void reduce(final int[] array, final int[] array2) {
        final long n = (long)array[10] & 0xFFFFFFFFL;
        final long n2 = (long)array[11] & 0xFFFFFFFFL;
        final long n3 = (long)array[12] & 0xFFFFFFFFL;
        final long n4 = (long)array[13] & 0xFFFFFFFFL;
        final long n5 = ((long)array[7] & 0xFFFFFFFFL) + n2 - 1L;
        final long n6 = ((long)array[8] & 0xFFFFFFFFL) + n3;
        final long n7 = ((long)array[9] & 0xFFFFFFFFL) + n4;
        final long n8 = 0L + (((long)array[0] & 0xFFFFFFFFL) - n5);
        final long n9 = n8 & 0xFFFFFFFFL;
        final long n10 = (n8 >> 32) + (((long)array[1] & 0xFFFFFFFFL) - n6);
        array2[1] = (int)n10;
        final long n11 = (n10 >> 32) + (((long)array[2] & 0xFFFFFFFFL) - n7);
        array2[2] = (int)n11;
        final long n12 = (n11 >> 32) + (((long)array[3] & 0xFFFFFFFFL) + n5 - n);
        final long n13 = n12 & 0xFFFFFFFFL;
        final long n14 = (n12 >> 32) + (((long)array[4] & 0xFFFFFFFFL) + n6 - n2);
        array2[4] = (int)n14;
        final long n15 = (n14 >> 32) + (((long)array[5] & 0xFFFFFFFFL) + n7 - n3);
        array2[5] = (int)n15;
        final long n16 = (n15 >> 32) + (((long)array[6] & 0xFFFFFFFFL) + n - n4);
        array2[6] = (int)n16;
        final long n17 = (n16 >> 32) + 1L;
        long n18 = n13 + n17;
        final long n19 = n9 - n17;
        array2[0] = (int)n19;
        final long n20 = n19 >> 32;
        if (n20 != 0L) {
            final long n21 = n20 + ((long)array2[1] & 0xFFFFFFFFL);
            array2[1] = (int)n21;
            final long n22 = (n21 >> 32) + ((long)array2[2] & 0xFFFFFFFFL);
            array2[2] = (int)n22;
            n18 += n22 >> 32;
        }
        array2[3] = (int)n18;
        if ((n18 >> 32 != 0L && Nat.incAt(7, array2, 4) != 0) || (array2[6] == -1 && Nat224.gte(array2, SecP224R1Field.P))) {
            addPInvTo(array2);
        }
    }
    
    public static void reduce32(final int n, final int[] array) {
        long n2 = 0L;
        if (n != 0) {
            final long n3 = (long)n & 0xFFFFFFFFL;
            final long n4 = n2 + (((long)array[0] & 0xFFFFFFFFL) - n3);
            array[0] = (int)n4;
            long n5 = n4 >> 32;
            if (n5 != 0L) {
                final long n6 = n5 + ((long)array[1] & 0xFFFFFFFFL);
                array[1] = (int)n6;
                final long n7 = (n6 >> 32) + ((long)array[2] & 0xFFFFFFFFL);
                array[2] = (int)n7;
                n5 = n7 >> 32;
            }
            final long n8 = n5 + (((long)array[3] & 0xFFFFFFFFL) + n3);
            array[3] = (int)n8;
            n2 = n8 >> 32;
        }
        if ((n2 != 0L && Nat.incAt(7, array, 4) != 0) || (array[6] == -1 && Nat224.gte(array, SecP224R1Field.P))) {
            addPInvTo(array);
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
            subPInvFrom(array3);
        }
    }
    
    public static void subtractExt(final int[] array, final int[] array2, final int[] array3) {
        if (Nat.sub(14, array, array2, array3) != 0 && Nat.subFrom(SecP224R1Field.PExtInv.length, SecP224R1Field.PExtInv, array3) != 0) {
            Nat.decAt(14, array3, SecP224R1Field.PExtInv.length);
        }
    }
    
    public static void twice(final int[] array, final int[] array2) {
        if (Nat.shiftUpBit(7, array, 0, array2) != 0 || (array2[6] == -1 && Nat224.gte(array2, SecP224R1Field.P))) {
            addPInvTo(array2);
        }
    }
    
    private static void addPInvTo(final int[] array) {
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
        final long n5 = n2 + (((long)array[3] & 0xFFFFFFFFL) + 1L);
        array[3] = (int)n5;
        if (n5 >> 32 != 0L) {
            Nat.incAt(7, array, 4);
        }
    }
    
    private static void subPInvFrom(final int[] array) {
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
        final long n5 = n2 + (((long)array[3] & 0xFFFFFFFFL) - 1L);
        array[3] = (int)n5;
        if (n5 >> 32 != 0L) {
            Nat.decAt(7, array, 4);
        }
    }
    
    static {
        P = new int[] { 1, 0, 0, -1, -1, -1, -1 };
        PExt = new int[] { 1, 0, 0, -2, -1, -1, 0, 2, 0, 0, -2, -1, -1, -1 };
        PExtInv = new int[] { -1, -1, -1, 1, 0, 0, -1, -3, -1, -1, 1 };
    }
}
