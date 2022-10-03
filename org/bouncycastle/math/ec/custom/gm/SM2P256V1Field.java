package org.bouncycastle.math.ec.custom.gm;

import java.math.BigInteger;
import org.bouncycastle.math.raw.Nat;
import org.bouncycastle.math.raw.Nat256;

public class SM2P256V1Field
{
    private static final long M = 4294967295L;
    static final int[] P;
    static final int[] PExt;
    private static final int P7s1 = Integer.MAX_VALUE;
    private static final int PExt15s1 = Integer.MAX_VALUE;
    
    public static void add(final int[] array, final int[] array2, final int[] array3) {
        if (Nat256.add(array, array2, array3) != 0 || (array3[7] >>> 1 >= Integer.MAX_VALUE && Nat256.gte(array3, SM2P256V1Field.P))) {
            addPInvTo(array3);
        }
    }
    
    public static void addExt(final int[] array, final int[] array2, final int[] array3) {
        if (Nat.add(16, array, array2, array3) != 0 || (array3[15] >>> 1 >= Integer.MAX_VALUE && Nat.gte(16, array3, SM2P256V1Field.PExt))) {
            Nat.subFrom(16, SM2P256V1Field.PExt, array3);
        }
    }
    
    public static void addOne(final int[] array, final int[] array2) {
        if (Nat.inc(8, array, array2) != 0 || (array2[7] >>> 1 >= Integer.MAX_VALUE && Nat256.gte(array2, SM2P256V1Field.P))) {
            addPInvTo(array2);
        }
    }
    
    public static int[] fromBigInteger(final BigInteger bigInteger) {
        final int[] fromBigInteger = Nat256.fromBigInteger(bigInteger);
        if (fromBigInteger[7] >>> 1 >= Integer.MAX_VALUE && Nat256.gte(fromBigInteger, SM2P256V1Field.P)) {
            Nat256.subFrom(SM2P256V1Field.P, fromBigInteger);
        }
        return fromBigInteger;
    }
    
    public static void half(final int[] array, final int[] array2) {
        if ((array[0] & 0x1) == 0x0) {
            Nat.shiftDownBit(8, array, 0, array2);
        }
        else {
            Nat.shiftDownBit(8, array2, Nat256.add(array, SM2P256V1Field.P, array2));
        }
    }
    
    public static void multiply(final int[] array, final int[] array2, final int[] array3) {
        final int[] ext = Nat256.createExt();
        Nat256.mul(array, array2, ext);
        reduce(ext, array3);
    }
    
    public static void multiplyAddToExt(final int[] array, final int[] array2, final int[] array3) {
        if (Nat256.mulAddTo(array, array2, array3) != 0 || (array3[15] >>> 1 >= Integer.MAX_VALUE && Nat.gte(16, array3, SM2P256V1Field.PExt))) {
            Nat.subFrom(16, SM2P256V1Field.PExt, array3);
        }
    }
    
    public static void negate(final int[] array, final int[] array2) {
        if (Nat256.isZero(array)) {
            Nat256.zero(array2);
        }
        else {
            Nat256.sub(SM2P256V1Field.P, array, array2);
        }
    }
    
    public static void reduce(final int[] array, final int[] array2) {
        final long n = (long)array[8] & 0xFFFFFFFFL;
        final long n2 = (long)array[9] & 0xFFFFFFFFL;
        final long n3 = (long)array[10] & 0xFFFFFFFFL;
        final long n4 = (long)array[11] & 0xFFFFFFFFL;
        final long n5 = (long)array[12] & 0xFFFFFFFFL;
        final long n6 = (long)array[13] & 0xFFFFFFFFL;
        final long n7 = (long)array[14] & 0xFFFFFFFFL;
        final long n8 = (long)array[15] & 0xFFFFFFFFL;
        final long n9 = n + n2;
        final long n10 = n3 + n4;
        final long n11 = n5 + n8;
        final long n12 = n6 + n7;
        final long n13 = n12 + (n8 << 1);
        final long n14 = n9 + n12;
        final long n15 = n10 + n11 + n14;
        final long n16 = 0L + (((long)array[0] & 0xFFFFFFFFL) + n15 + n6 + n7 + n8);
        array2[0] = (int)n16;
        final long n17 = (n16 >> 32) + (((long)array[1] & 0xFFFFFFFFL) + n15 - n + n7 + n8);
        array2[1] = (int)n17;
        final long n18 = (n17 >> 32) + (((long)array[2] & 0xFFFFFFFFL) - n14);
        array2[2] = (int)n18;
        final long n19 = (n18 >> 32) + (((long)array[3] & 0xFFFFFFFFL) + n15 - n2 - n3 + n6);
        array2[3] = (int)n19;
        final long n20 = (n19 >> 32) + (((long)array[4] & 0xFFFFFFFFL) + n15 - n10 - n + n7);
        array2[4] = (int)n20;
        final long n21 = (n20 >> 32) + (((long)array[5] & 0xFFFFFFFFL) + n13 + n3);
        array2[5] = (int)n21;
        final long n22 = (n21 >> 32) + (((long)array[6] & 0xFFFFFFFFL) + n4 + n7 + n8);
        array2[6] = (int)n22;
        final long n23 = (n22 >> 32) + (((long)array[7] & 0xFFFFFFFFL) + n15 + n13 + n5);
        array2[7] = (int)n23;
        reduce32((int)(n23 >> 32), array2);
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
            final long n7 = n5 + (((long)array[2] & 0xFFFFFFFFL) - n3);
            array[2] = (int)n7;
            final long n8 = (n7 >> 32) + (((long)array[3] & 0xFFFFFFFFL) + n3);
            array[3] = (int)n8;
            long n9 = n8 >> 32;
            if (n9 != 0L) {
                final long n10 = n9 + ((long)array[4] & 0xFFFFFFFFL);
                array[4] = (int)n10;
                final long n11 = (n10 >> 32) + ((long)array[5] & 0xFFFFFFFFL);
                array[5] = (int)n11;
                final long n12 = (n11 >> 32) + ((long)array[6] & 0xFFFFFFFFL);
                array[6] = (int)n12;
                n9 = n12 >> 32;
            }
            final long n13 = n9 + (((long)array[7] & 0xFFFFFFFFL) + n3);
            array[7] = (int)n13;
            n2 = n13 >> 32;
        }
        if (n2 != 0L || (array[7] >>> 1 >= Integer.MAX_VALUE && Nat256.gte(array, SM2P256V1Field.P))) {
            addPInvTo(array);
        }
    }
    
    public static void square(final int[] array, final int[] array2) {
        final int[] ext = Nat256.createExt();
        Nat256.square(array, ext);
        reduce(ext, array2);
    }
    
    public static void squareN(final int[] array, int n, final int[] array2) {
        final int[] ext = Nat256.createExt();
        Nat256.square(array, ext);
        reduce(ext, array2);
        while (--n > 0) {
            Nat256.square(array2, ext);
            reduce(ext, array2);
        }
    }
    
    public static void subtract(final int[] array, final int[] array2, final int[] array3) {
        if (Nat256.sub(array, array2, array3) != 0) {
            subPInvFrom(array3);
        }
    }
    
    public static void subtractExt(final int[] array, final int[] array2, final int[] array3) {
        if (Nat.sub(16, array, array2, array3) != 0) {
            Nat.addTo(16, SM2P256V1Field.PExt, array3);
        }
    }
    
    public static void twice(final int[] array, final int[] array2) {
        if (Nat.shiftUpBit(8, array, 0, array2) != 0 || (array2[7] >>> 1 >= Integer.MAX_VALUE && Nat256.gte(array2, SM2P256V1Field.P))) {
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
        final long n4 = n2 + (((long)array[2] & 0xFFFFFFFFL) - 1L);
        array[2] = (int)n4;
        final long n5 = (n4 >> 32) + (((long)array[3] & 0xFFFFFFFFL) + 1L);
        array[3] = (int)n5;
        long n6 = n5 >> 32;
        if (n6 != 0L) {
            final long n7 = n6 + ((long)array[4] & 0xFFFFFFFFL);
            array[4] = (int)n7;
            final long n8 = (n7 >> 32) + ((long)array[5] & 0xFFFFFFFFL);
            array[5] = (int)n8;
            final long n9 = (n8 >> 32) + ((long)array[6] & 0xFFFFFFFFL);
            array[6] = (int)n9;
            n6 = n9 >> 32;
        }
        array[7] = (int)(n6 + (((long)array[7] & 0xFFFFFFFFL) + 1L));
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
        final long n4 = n2 + (((long)array[2] & 0xFFFFFFFFL) + 1L);
        array[2] = (int)n4;
        final long n5 = (n4 >> 32) + (((long)array[3] & 0xFFFFFFFFL) - 1L);
        array[3] = (int)n5;
        long n6 = n5 >> 32;
        if (n6 != 0L) {
            final long n7 = n6 + ((long)array[4] & 0xFFFFFFFFL);
            array[4] = (int)n7;
            final long n8 = (n7 >> 32) + ((long)array[5] & 0xFFFFFFFFL);
            array[5] = (int)n8;
            final long n9 = (n8 >> 32) + ((long)array[6] & 0xFFFFFFFFL);
            array[6] = (int)n9;
            n6 = n9 >> 32;
        }
        array[7] = (int)(n6 + (((long)array[7] & 0xFFFFFFFFL) - 1L));
    }
    
    static {
        P = new int[] { -1, -1, 0, -1, -1, -1, -1, -2 };
        PExt = new int[] { 1, 0, -2, 1, 1, -2, 0, 2, -2, -3, 3, -2, -1, -1, 0, -2 };
    }
}
