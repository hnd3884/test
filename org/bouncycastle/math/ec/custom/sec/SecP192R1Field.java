package org.bouncycastle.math.ec.custom.sec;

import java.math.BigInteger;
import org.bouncycastle.math.raw.Nat;
import org.bouncycastle.math.raw.Nat192;

public class SecP192R1Field
{
    private static final long M = 4294967295L;
    static final int[] P;
    static final int[] PExt;
    private static final int[] PExtInv;
    private static final int P5 = -1;
    private static final int PExt11 = -1;
    
    public static void add(final int[] array, final int[] array2, final int[] array3) {
        if (Nat192.add(array, array2, array3) != 0 || (array3[5] == -1 && Nat192.gte(array3, SecP192R1Field.P))) {
            addPInvTo(array3);
        }
    }
    
    public static void addExt(final int[] array, final int[] array2, final int[] array3) {
        if ((Nat.add(12, array, array2, array3) != 0 || (array3[11] == -1 && Nat.gte(12, array3, SecP192R1Field.PExt))) && Nat.addTo(SecP192R1Field.PExtInv.length, SecP192R1Field.PExtInv, array3) != 0) {
            Nat.incAt(12, array3, SecP192R1Field.PExtInv.length);
        }
    }
    
    public static void addOne(final int[] array, final int[] array2) {
        if (Nat.inc(6, array, array2) != 0 || (array2[5] == -1 && Nat192.gte(array2, SecP192R1Field.P))) {
            addPInvTo(array2);
        }
    }
    
    public static int[] fromBigInteger(final BigInteger bigInteger) {
        final int[] fromBigInteger = Nat192.fromBigInteger(bigInteger);
        if (fromBigInteger[5] == -1 && Nat192.gte(fromBigInteger, SecP192R1Field.P)) {
            Nat192.subFrom(SecP192R1Field.P, fromBigInteger);
        }
        return fromBigInteger;
    }
    
    public static void half(final int[] array, final int[] array2) {
        if ((array[0] & 0x1) == 0x0) {
            Nat.shiftDownBit(6, array, 0, array2);
        }
        else {
            Nat.shiftDownBit(6, array2, Nat192.add(array, SecP192R1Field.P, array2));
        }
    }
    
    public static void multiply(final int[] array, final int[] array2, final int[] array3) {
        final int[] ext = Nat192.createExt();
        Nat192.mul(array, array2, ext);
        reduce(ext, array3);
    }
    
    public static void multiplyAddToExt(final int[] array, final int[] array2, final int[] array3) {
        if ((Nat192.mulAddTo(array, array2, array3) != 0 || (array3[11] == -1 && Nat.gte(12, array3, SecP192R1Field.PExt))) && Nat.addTo(SecP192R1Field.PExtInv.length, SecP192R1Field.PExtInv, array3) != 0) {
            Nat.incAt(12, array3, SecP192R1Field.PExtInv.length);
        }
    }
    
    public static void negate(final int[] array, final int[] array2) {
        if (Nat192.isZero(array)) {
            Nat192.zero(array2);
        }
        else {
            Nat192.sub(SecP192R1Field.P, array, array2);
        }
    }
    
    public static void reduce(final int[] array, final int[] array2) {
        final long n = (long)array[6] & 0xFFFFFFFFL;
        final long n2 = (long)array[7] & 0xFFFFFFFFL;
        final long n3 = (long)array[8] & 0xFFFFFFFFL;
        final long n4 = (long)array[9] & 0xFFFFFFFFL;
        final long n5 = (long)array[10] & 0xFFFFFFFFL;
        final long n6 = (long)array[11] & 0xFFFFFFFFL;
        final long n7 = n + n5;
        final long n8 = n2 + n6;
        final long n9 = 0L + (((long)array[0] & 0xFFFFFFFFL) + n7);
        final int n10 = (int)n9;
        final long n11 = (n9 >> 32) + (((long)array[1] & 0xFFFFFFFFL) + n8);
        array2[1] = (int)n11;
        final long n12 = n11 >> 32;
        final long n13 = n7 + n3;
        final long n14 = n8 + n4;
        final long n15 = n12 + (((long)array[2] & 0xFFFFFFFFL) + n13);
        final long n16 = n15 & 0xFFFFFFFFL;
        final long n17 = (n15 >> 32) + (((long)array[3] & 0xFFFFFFFFL) + n14);
        array2[3] = (int)n17;
        final long n18 = n17 >> 32;
        final long n19 = n13 - n;
        final long n20 = n14 - n2;
        final long n21 = n18 + (((long)array[4] & 0xFFFFFFFFL) + n19);
        array2[4] = (int)n21;
        final long n22 = (n21 >> 32) + (((long)array[5] & 0xFFFFFFFFL) + n20);
        array2[5] = (int)n22;
        final long n23 = n22 >> 32;
        long n24 = n16 + n23;
        final long n25 = n23 + ((long)n10 & 0xFFFFFFFFL);
        array2[0] = (int)n25;
        final long n26 = n25 >> 32;
        if (n26 != 0L) {
            final long n27 = n26 + ((long)array2[1] & 0xFFFFFFFFL);
            array2[1] = (int)n27;
            n24 += n27 >> 32;
        }
        array2[2] = (int)n24;
        if ((n24 >> 32 != 0L && Nat.incAt(6, array2, 3) != 0) || (array2[5] == -1 && Nat192.gte(array2, SecP192R1Field.P))) {
            addPInvTo(array2);
        }
    }
    
    public static void reduce32(final int n, final int[] array) {
        long n2 = 0L;
        if (n != 0) {
            final long n3 = (long)n & 0xFFFFFFFFL;
            final long n4 = n2 + (((long)array[0] & 0xFFFFFFFFL) + n3);
            array[0] = (int)n4;
            long n5 = n4 >> 32;
            if (n5 != 0L) {
                final long n6 = n5 + ((long)array[1] & 0xFFFFFFFFL);
                array[1] = (int)n6;
                n5 = n6 >> 32;
            }
            final long n7 = n5 + (((long)array[2] & 0xFFFFFFFFL) + n3);
            array[2] = (int)n7;
            n2 = n7 >> 32;
        }
        if ((n2 != 0L && Nat.incAt(6, array, 3) != 0) || (array[5] == -1 && Nat192.gte(array, SecP192R1Field.P))) {
            addPInvTo(array);
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
            subPInvFrom(array3);
        }
    }
    
    public static void subtractExt(final int[] array, final int[] array2, final int[] array3) {
        if (Nat.sub(12, array, array2, array3) != 0 && Nat.subFrom(SecP192R1Field.PExtInv.length, SecP192R1Field.PExtInv, array3) != 0) {
            Nat.decAt(12, array3, SecP192R1Field.PExtInv.length);
        }
    }
    
    public static void twice(final int[] array, final int[] array2) {
        if (Nat.shiftUpBit(6, array, 0, array2) != 0 || (array2[5] == -1 && Nat192.gte(array2, SecP192R1Field.P))) {
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
            n2 = n3 >> 32;
        }
        final long n4 = n2 + (((long)array[2] & 0xFFFFFFFFL) + 1L);
        array[2] = (int)n4;
        if (n4 >> 32 != 0L) {
            Nat.incAt(6, array, 3);
        }
    }
    
    private static void subPInvFrom(final int[] array) {
        final long n = ((long)array[0] & 0xFFFFFFFFL) - 1L;
        array[0] = (int)n;
        long n2 = n >> 32;
        if (n2 != 0L) {
            final long n3 = n2 + ((long)array[1] & 0xFFFFFFFFL);
            array[1] = (int)n3;
            n2 = n3 >> 32;
        }
        final long n4 = n2 + (((long)array[2] & 0xFFFFFFFFL) - 1L);
        array[2] = (int)n4;
        if (n4 >> 32 != 0L) {
            Nat.decAt(6, array, 3);
        }
    }
    
    static {
        P = new int[] { -1, -1, -2, -1, -1, -1 };
        PExt = new int[] { 1, 0, 2, 0, 1, 0, -2, -1, -3, -1, -1, -1 };
        PExtInv = new int[] { -1, -1, -3, -1, -2, -1, 1, 0, 2 };
    }
}
