package org.bouncycastle.math.ec.custom.sec;

import org.bouncycastle.math.raw.Nat;
import org.bouncycastle.math.raw.Interleave;
import org.bouncycastle.math.raw.Nat448;
import java.math.BigInteger;

public class SecT409Field
{
    private static final long M25 = 33554431L;
    private static final long M59 = 576460752303423487L;
    
    public static void add(final long[] array, final long[] array2, final long[] array3) {
        array3[0] = (array[0] ^ array2[0]);
        array3[1] = (array[1] ^ array2[1]);
        array3[2] = (array[2] ^ array2[2]);
        array3[3] = (array[3] ^ array2[3]);
        array3[4] = (array[4] ^ array2[4]);
        array3[5] = (array[5] ^ array2[5]);
        array3[6] = (array[6] ^ array2[6]);
    }
    
    public static void addExt(final long[] array, final long[] array2, final long[] array3) {
        for (int i = 0; i < 13; ++i) {
            array3[i] = (array[i] ^ array2[i]);
        }
    }
    
    public static void addOne(final long[] array, final long[] array2) {
        array2[0] = (array[0] ^ 0x1L);
        array2[1] = array[1];
        array2[2] = array[2];
        array2[3] = array[3];
        array2[4] = array[4];
        array2[5] = array[5];
        array2[6] = array[6];
    }
    
    public static long[] fromBigInteger(final BigInteger bigInteger) {
        final long[] fromBigInteger64 = Nat448.fromBigInteger64(bigInteger);
        reduce39(fromBigInteger64, 0);
        return fromBigInteger64;
    }
    
    public static void invert(final long[] array, final long[] array2) {
        if (Nat448.isZero64(array)) {
            throw new IllegalStateException();
        }
        final long[] create64 = Nat448.create64();
        final long[] create65 = Nat448.create64();
        final long[] create66 = Nat448.create64();
        square(array, create64);
        squareN(create64, 1, create65);
        multiply(create64, create65, create64);
        squareN(create65, 1, create65);
        multiply(create64, create65, create64);
        squareN(create64, 3, create65);
        multiply(create64, create65, create64);
        squareN(create64, 6, create65);
        multiply(create64, create65, create64);
        squareN(create64, 12, create65);
        multiply(create64, create65, create66);
        squareN(create66, 24, create64);
        squareN(create64, 24, create65);
        multiply(create64, create65, create64);
        squareN(create64, 48, create65);
        multiply(create64, create65, create64);
        squareN(create64, 96, create65);
        multiply(create64, create65, create64);
        squareN(create64, 192, create65);
        multiply(create64, create65, create64);
        multiply(create64, create66, array2);
    }
    
    public static void multiply(final long[] array, final long[] array2, final long[] array3) {
        final long[] ext64 = Nat448.createExt64();
        implMultiply(array, array2, ext64);
        reduce(ext64, array3);
    }
    
    public static void multiplyAddToExt(final long[] array, final long[] array2, final long[] array3) {
        final long[] ext64 = Nat448.createExt64();
        implMultiply(array, array2, ext64);
        addExt(array3, ext64, array3);
    }
    
    public static void reduce(final long[] array, final long[] array2) {
        final long n = array[0];
        final long n2 = array[1];
        final long n3 = array[2];
        final long n4 = array[3];
        final long n5 = array[4];
        final long n6 = array[5];
        final long n7 = array[6];
        final long n8 = array[7];
        final long n9 = array[12];
        final long n10 = n6 ^ n9 << 39;
        final long n11 = n7 ^ (n9 >>> 25 ^ n9 << 62);
        final long n12 = n8 ^ n9 >>> 2;
        final long n13 = array[11];
        final long n14 = n5 ^ n13 << 39;
        final long n15 = n10 ^ (n13 >>> 25 ^ n13 << 62);
        final long n16 = n11 ^ n13 >>> 2;
        final long n17 = array[10];
        final long n18 = n4 ^ n17 << 39;
        final long n19 = n14 ^ (n17 >>> 25 ^ n17 << 62);
        final long n20 = n15 ^ n17 >>> 2;
        final long n21 = array[9];
        final long n22 = n3 ^ n21 << 39;
        final long n23 = n18 ^ (n21 >>> 25 ^ n21 << 62);
        final long n24 = n19 ^ n21 >>> 2;
        final long n25 = array[8];
        final long n26 = n2 ^ n25 << 39;
        final long n27 = n22 ^ (n25 >>> 25 ^ n25 << 62);
        final long n28 = n23 ^ n25 >>> 2;
        final long n29 = n12;
        final long n30 = n ^ n29 << 39;
        final long n31 = n26 ^ (n29 >>> 25 ^ n29 << 62);
        final long n32 = n27 ^ n29 >>> 2;
        final long n33 = n16 >>> 25;
        array2[0] = (n30 ^ n33);
        array2[1] = (n31 ^ n33 << 23);
        array2[2] = n32;
        array2[3] = n28;
        array2[4] = n24;
        array2[5] = n20;
        array2[6] = (n16 & 0x1FFFFFFL);
    }
    
    public static void reduce39(final long[] array, final int n) {
        final long n2 = array[n + 6];
        final long n3 = n2 >>> 25;
        array[n] ^= n3;
        final int n4 = n + 1;
        array[n4] ^= n3 << 23;
        array[n + 6] = (n2 & 0x1FFFFFFL);
    }
    
    public static void sqrt(final long[] array, final long[] array2) {
        final long unshuffle = Interleave.unshuffle(array[0]);
        final long unshuffle2 = Interleave.unshuffle(array[1]);
        final long n = (unshuffle & 0xFFFFFFFFL) | unshuffle2 << 32;
        final long n2 = unshuffle >>> 32 | (unshuffle2 & 0xFFFFFFFF00000000L);
        final long unshuffle3 = Interleave.unshuffle(array[2]);
        final long unshuffle4 = Interleave.unshuffle(array[3]);
        final long n3 = (unshuffle3 & 0xFFFFFFFFL) | unshuffle4 << 32;
        final long n4 = unshuffle3 >>> 32 | (unshuffle4 & 0xFFFFFFFF00000000L);
        final long unshuffle5 = Interleave.unshuffle(array[4]);
        final long unshuffle6 = Interleave.unshuffle(array[5]);
        final long n5 = (unshuffle5 & 0xFFFFFFFFL) | unshuffle6 << 32;
        final long n6 = unshuffle5 >>> 32 | (unshuffle6 & 0xFFFFFFFF00000000L);
        final long unshuffle7 = Interleave.unshuffle(array[6]);
        final long n7 = unshuffle7 & 0xFFFFFFFFL;
        final long n8 = unshuffle7 >>> 32;
        array2[0] = (n ^ n2 << 44);
        array2[1] = (n3 ^ n4 << 44 ^ n2 >>> 20);
        array2[2] = (n5 ^ n6 << 44 ^ n4 >>> 20);
        array2[3] = (n7 ^ n8 << 44 ^ n6 >>> 20 ^ n2 << 13);
        array2[4] = (n8 >>> 20 ^ n4 << 13 ^ n2 >>> 51);
        array2[5] = (n6 << 13 ^ n4 >>> 51);
        array2[6] = (n8 << 13 ^ n6 >>> 51);
    }
    
    public static void square(final long[] array, final long[] array2) {
        final long[] create64 = Nat.create64(13);
        implSquare(array, create64);
        reduce(create64, array2);
    }
    
    public static void squareAddToExt(final long[] array, final long[] array2) {
        final long[] create64 = Nat.create64(13);
        implSquare(array, create64);
        addExt(array2, create64, array2);
    }
    
    public static void squareN(final long[] array, int n, final long[] array2) {
        final long[] create64 = Nat.create64(13);
        implSquare(array, create64);
        reduce(create64, array2);
        while (--n > 0) {
            implSquare(array2, create64);
            reduce(create64, array2);
        }
    }
    
    public static int trace(final long[] array) {
        return (int)array[0] & 0x1;
    }
    
    protected static void implCompactExt(final long[] array) {
        final long n = array[0];
        final long n2 = array[1];
        final long n3 = array[2];
        final long n4 = array[3];
        final long n5 = array[4];
        final long n6 = array[5];
        final long n7 = array[6];
        final long n8 = array[7];
        final long n9 = array[8];
        final long n10 = array[9];
        final long n11 = array[10];
        final long n12 = array[11];
        final long n13 = array[12];
        final long n14 = array[13];
        array[0] = (n ^ n2 << 59);
        array[1] = (n2 >>> 5 ^ n3 << 54);
        array[2] = (n3 >>> 10 ^ n4 << 49);
        array[3] = (n4 >>> 15 ^ n5 << 44);
        array[4] = (n5 >>> 20 ^ n6 << 39);
        array[5] = (n6 >>> 25 ^ n7 << 34);
        array[6] = (n7 >>> 30 ^ n8 << 29);
        array[7] = (n8 >>> 35 ^ n9 << 24);
        array[8] = (n9 >>> 40 ^ n10 << 19);
        array[9] = (n10 >>> 45 ^ n11 << 14);
        array[10] = (n11 >>> 50 ^ n12 << 9);
        array[11] = (n12 >>> 55 ^ n13 << 4 ^ n14 << 63);
        array[12] = (n13 >>> 60 ^ n14 >>> 1);
        array[13] = 0L;
    }
    
    protected static void implExpand(final long[] array, final long[] array2) {
        final long n = array[0];
        final long n2 = array[1];
        final long n3 = array[2];
        final long n4 = array[3];
        final long n5 = array[4];
        final long n6 = array[5];
        final long n7 = array[6];
        array2[0] = (n & 0x7FFFFFFFFFFFFFFL);
        array2[1] = ((n >>> 59 ^ n2 << 5) & 0x7FFFFFFFFFFFFFFL);
        array2[2] = ((n2 >>> 54 ^ n3 << 10) & 0x7FFFFFFFFFFFFFFL);
        array2[3] = ((n3 >>> 49 ^ n4 << 15) & 0x7FFFFFFFFFFFFFFL);
        array2[4] = ((n4 >>> 44 ^ n5 << 20) & 0x7FFFFFFFFFFFFFFL);
        array2[5] = ((n5 >>> 39 ^ n6 << 25) & 0x7FFFFFFFFFFFFFFL);
        array2[6] = (n6 >>> 34 ^ n7 << 30);
    }
    
    protected static void implMultiply(final long[] array, final long[] array2, final long[] array3) {
        final long[] array4 = new long[7];
        final long[] array5 = new long[7];
        implExpand(array, array4);
        implExpand(array2, array5);
        for (int i = 0; i < 7; ++i) {
            implMulwAcc(array4, array5[i], array3, i);
        }
        implCompactExt(array3);
    }
    
    protected static void implMulwAcc(final long[] array, final long n, final long[] array2, final int n2) {
        final long[] array3 = new long[8];
        array3[1] = n;
        array3[2] = array3[1] << 1;
        array3[3] = (array3[2] ^ n);
        array3[4] = array3[2] << 1;
        array3[5] = (array3[4] ^ n);
        array3[6] = array3[3] << 1;
        array3[7] = (array3[6] ^ n);
        for (int i = 0; i < 7; ++i) {
            final long n3 = array[i];
            final int n4 = (int)n3;
            long n5 = 0L;
            long n6 = array3[n4 & 0x7] ^ array3[n4 >>> 3 & 0x7] << 3;
            int j = 54;
            do {
                final int n7 = (int)(n3 >>> j);
                final long n8 = array3[n7 & 0x7] ^ array3[n7 >>> 3 & 0x7] << 3;
                n6 ^= n8 << j;
                n5 ^= n8 >>> -j;
                j -= 6;
            } while (j > 0);
            final int n9 = n2 + i;
            array2[n9] ^= (n6 & 0x7FFFFFFFFFFFFFFL);
            final int n10 = n2 + i + 1;
            array2[n10] ^= (n6 >>> 59 ^ n5 << 5);
        }
    }
    
    protected static void implSquare(final long[] array, final long[] array2) {
        for (int i = 0; i < 6; ++i) {
            Interleave.expand64To128(array[i], array2, i << 1);
        }
        array2[12] = Interleave.expand32to64((int)array[6]);
    }
}
