package org.bouncycastle.math.ec.custom.sec;

import org.bouncycastle.math.raw.Interleave;
import org.bouncycastle.math.raw.Nat256;
import java.math.BigInteger;

public class SecT239Field
{
    private static final long M47 = 140737488355327L;
    private static final long M60 = 1152921504606846975L;
    
    public static void add(final long[] array, final long[] array2, final long[] array3) {
        array3[0] = (array[0] ^ array2[0]);
        array3[1] = (array[1] ^ array2[1]);
        array3[2] = (array[2] ^ array2[2]);
        array3[3] = (array[3] ^ array2[3]);
    }
    
    public static void addExt(final long[] array, final long[] array2, final long[] array3) {
        array3[0] = (array[0] ^ array2[0]);
        array3[1] = (array[1] ^ array2[1]);
        array3[2] = (array[2] ^ array2[2]);
        array3[3] = (array[3] ^ array2[3]);
        array3[4] = (array[4] ^ array2[4]);
        array3[5] = (array[5] ^ array2[5]);
        array3[6] = (array[6] ^ array2[6]);
        array3[7] = (array[7] ^ array2[7]);
    }
    
    public static void addOne(final long[] array, final long[] array2) {
        array2[0] = (array[0] ^ 0x1L);
        array2[1] = array[1];
        array2[2] = array[2];
        array2[3] = array[3];
    }
    
    public static long[] fromBigInteger(final BigInteger bigInteger) {
        final long[] fromBigInteger64 = Nat256.fromBigInteger64(bigInteger);
        reduce17(fromBigInteger64, 0);
        return fromBigInteger64;
    }
    
    public static void invert(final long[] array, final long[] array2) {
        if (Nat256.isZero64(array)) {
            throw new IllegalStateException();
        }
        final long[] create64 = Nat256.create64();
        final long[] create65 = Nat256.create64();
        square(array, create64);
        multiply(create64, array, create64);
        square(create64, create64);
        multiply(create64, array, create64);
        squareN(create64, 3, create65);
        multiply(create65, create64, create65);
        square(create65, create65);
        multiply(create65, array, create65);
        squareN(create65, 7, create64);
        multiply(create64, create65, create64);
        squareN(create64, 14, create65);
        multiply(create65, create64, create65);
        square(create65, create65);
        multiply(create65, array, create65);
        squareN(create65, 29, create64);
        multiply(create64, create65, create64);
        square(create64, create64);
        multiply(create64, array, create64);
        squareN(create64, 59, create65);
        multiply(create65, create64, create65);
        square(create65, create65);
        multiply(create65, array, create65);
        squareN(create65, 119, create64);
        multiply(create64, create65, create64);
        square(create64, array2);
    }
    
    public static void multiply(final long[] array, final long[] array2, final long[] array3) {
        final long[] ext64 = Nat256.createExt64();
        implMultiply(array, array2, ext64);
        reduce(ext64, array3);
    }
    
    public static void multiplyAddToExt(final long[] array, final long[] array2, final long[] array3) {
        final long[] ext64 = Nat256.createExt64();
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
        final long n9 = n4 ^ n8 << 17;
        final long n10 = n5 ^ n8 >>> 47;
        final long n11 = n6 ^ n8 << 47;
        final long n12 = n7 ^ n8 >>> 17;
        final long n13 = n3 ^ n12 << 17;
        final long n14 = n9 ^ n12 >>> 47;
        final long n15 = n10 ^ n12 << 47;
        final long n16 = n11 ^ n12 >>> 17;
        final long n17 = n2 ^ n16 << 17;
        final long n18 = n13 ^ n16 >>> 47;
        final long n19 = n14 ^ n16 << 47;
        final long n20 = n15 ^ n16 >>> 17;
        final long n21 = n ^ n20 << 17;
        final long n22 = n17 ^ n20 >>> 47;
        final long n23 = n18 ^ n20 << 47;
        final long n24 = n19 ^ n20 >>> 17;
        final long n25 = n24 >>> 47;
        array2[0] = (n21 ^ n25);
        array2[1] = n22;
        array2[2] = (n23 ^ n25 << 30);
        array2[3] = (n24 & 0x7FFFFFFFFFFFL);
    }
    
    public static void reduce17(final long[] array, final int n) {
        final long n2 = array[n + 3];
        final long n3 = n2 >>> 47;
        array[n] ^= n3;
        final int n4 = n + 2;
        array[n4] ^= n3 << 30;
        array[n + 3] = (n2 & 0x7FFFFFFFFFFFL);
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
        final long n5 = n4 >>> 49;
        final long n6 = n2 >>> 49 | n4 << 15;
        final long n7 = n4 ^ n2 << 15;
        final long[] ext64 = Nat256.createExt64();
        final int[] array3 = { 39, 120 };
        for (int i = 0; i < array3.length; ++i) {
            final int n8 = array3[i] >>> 6;
            final int n9 = array3[i] & 0x3F;
            final long[] array4 = ext64;
            final int n10 = n8;
            array4[n10] ^= n2 << n9;
            final long[] array5 = ext64;
            final int n11 = n8 + 1;
            array5[n11] ^= (n7 << n9 | n2 >>> -n9);
            final long[] array6 = ext64;
            final int n12 = n8 + 2;
            array6[n12] ^= (n6 << n9 | n7 >>> -n9);
            final long[] array7 = ext64;
            final int n13 = n8 + 3;
            array7[n13] ^= (n5 << n9 | n6 >>> -n9);
            final long[] array8 = ext64;
            final int n14 = n8 + 4;
            array8[n14] ^= n5 >>> -n9;
        }
        reduce(ext64, array2);
        final int n15 = 0;
        array2[n15] ^= n;
        final int n16 = 1;
        array2[n16] ^= n3;
    }
    
    public static void square(final long[] array, final long[] array2) {
        final long[] ext64 = Nat256.createExt64();
        implSquare(array, ext64);
        reduce(ext64, array2);
    }
    
    public static void squareAddToExt(final long[] array, final long[] array2) {
        final long[] ext64 = Nat256.createExt64();
        implSquare(array, ext64);
        addExt(array2, ext64, array2);
    }
    
    public static void squareN(final long[] array, int n, final long[] array2) {
        final long[] ext64 = Nat256.createExt64();
        implSquare(array, ext64);
        reduce(ext64, array2);
        while (--n > 0) {
            implSquare(array2, ext64);
            reduce(ext64, array2);
        }
    }
    
    public static int trace(final long[] array) {
        return (int)(array[0] ^ array[1] >>> 17 ^ array[2] >>> 34) & 0x1;
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
        array[0] = (n ^ n2 << 60);
        array[1] = (n2 >>> 4 ^ n3 << 56);
        array[2] = (n3 >>> 8 ^ n4 << 52);
        array[3] = (n4 >>> 12 ^ n5 << 48);
        array[4] = (n5 >>> 16 ^ n6 << 44);
        array[5] = (n6 >>> 20 ^ n7 << 40);
        array[6] = (n7 >>> 24 ^ n8 << 36);
        array[7] = n8 >>> 28;
    }
    
    protected static void implExpand(final long[] array, final long[] array2) {
        final long n = array[0];
        final long n2 = array[1];
        final long n3 = array[2];
        final long n4 = array[3];
        array2[0] = (n & 0xFFFFFFFFFFFFFFFL);
        array2[1] = ((n >>> 60 ^ n2 << 4) & 0xFFFFFFFFFFFFFFFL);
        array2[2] = ((n2 >>> 56 ^ n3 << 8) & 0xFFFFFFFFFFFFFFFL);
        array2[3] = (n3 >>> 52 ^ n4 << 12);
    }
    
    protected static void implMultiply(final long[] array, final long[] array2, final long[] array3) {
        final long[] array4 = new long[4];
        final long[] array5 = new long[4];
        implExpand(array, array4);
        implExpand(array2, array5);
        implMulwAcc(array4[0], array5[0], array3, 0);
        implMulwAcc(array4[1], array5[1], array3, 1);
        implMulwAcc(array4[2], array5[2], array3, 2);
        implMulwAcc(array4[3], array5[3], array3, 3);
        for (int i = 5; i > 0; --i) {
            final int n = i;
            array3[n] ^= array3[i - 1];
        }
        implMulwAcc(array4[0] ^ array4[1], array5[0] ^ array5[1], array3, 1);
        implMulwAcc(array4[2] ^ array4[3], array5[2] ^ array5[3], array3, 3);
        for (int j = 7; j > 1; --j) {
            final int n2 = j;
            array3[n2] ^= array3[j - 2];
        }
        final long n3 = array4[0] ^ array4[2];
        final long n4 = array4[1] ^ array4[3];
        final long n5 = array5[0] ^ array5[2];
        final long n6 = array5[1] ^ array5[3];
        implMulwAcc(n3 ^ n4, n5 ^ n6, array3, 3);
        final long[] array6 = new long[3];
        implMulwAcc(n3, n5, array6, 0);
        implMulwAcc(n4, n6, array6, 1);
        final long n7 = array6[0];
        final long n8 = array6[1];
        final long n9 = array6[2];
        final int n10 = 2;
        array3[n10] ^= n7;
        final int n11 = 3;
        array3[n11] ^= (n7 ^ n8);
        final int n12 = 4;
        array3[n12] ^= (n9 ^ n8);
        final int n13 = 5;
        array3[n13] ^= n9;
        implCompactExt(array3);
    }
    
    protected static void implMulwAcc(final long n, final long n2, final long[] array, final int n3) {
        final long[] array2 = new long[8];
        array2[1] = n2;
        array2[2] = array2[1] << 1;
        array2[3] = (array2[2] ^ n2);
        array2[4] = array2[2] << 1;
        array2[5] = (array2[4] ^ n2);
        array2[6] = array2[3] << 1;
        array2[7] = (array2[6] ^ n2);
        final int n4 = (int)n;
        long n5 = 0L;
        long n6 = array2[n4 & 0x7] ^ array2[n4 >>> 3 & 0x7] << 3;
        int i = 54;
        do {
            final int n7 = (int)(n >>> i);
            final long n8 = array2[n7 & 0x7] ^ array2[n7 >>> 3 & 0x7] << 3;
            n6 ^= n8 << i;
            n5 ^= n8 >>> -i;
            i -= 6;
        } while (i > 0);
        final long n9 = n5 ^ (n & 0x820820820820820L & n2 << 4 >> 63) >>> 5;
        array[n3] ^= (n6 & 0xFFFFFFFFFFFFFFFL);
        final int n10 = n3 + 1;
        array[n10] ^= (n6 >>> 60 ^ n9 << 4);
    }
    
    protected static void implSquare(final long[] array, final long[] array2) {
        Interleave.expand64To128(array[0], array2, 0);
        Interleave.expand64To128(array[1], array2, 2);
        Interleave.expand64To128(array[2], array2, 4);
        final long n = array[3];
        array2[6] = Interleave.expand32to64((int)n);
        array2[7] = ((long)Interleave.expand16to32((int)(n >>> 32)) & 0xFFFFFFFFL);
    }
}
