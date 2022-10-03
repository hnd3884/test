package org.bouncycastle.math.ec.custom.sec;

import org.bouncycastle.math.raw.Interleave;
import org.bouncycastle.math.raw.Nat256;
import java.math.BigInteger;

public class SecT193Field
{
    private static final long M01 = 1L;
    private static final long M49 = 562949953421311L;
    
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
    }
    
    public static void addOne(final long[] array, final long[] array2) {
        array2[0] = (array[0] ^ 0x1L);
        array2[1] = array[1];
        array2[2] = array[2];
        array2[3] = array[3];
    }
    
    public static long[] fromBigInteger(final BigInteger bigInteger) {
        final long[] fromBigInteger64 = Nat256.fromBigInteger64(bigInteger);
        reduce63(fromBigInteger64, 0);
        return fromBigInteger64;
    }
    
    public static void invert(final long[] array, final long[] array2) {
        if (Nat256.isZero64(array)) {
            throw new IllegalStateException();
        }
        final long[] create64 = Nat256.create64();
        final long[] create65 = Nat256.create64();
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
        multiply(create64, create65, create64);
        squareN(create64, 24, create65);
        multiply(create64, create65, create64);
        squareN(create64, 48, create65);
        multiply(create64, create65, create64);
        squareN(create64, 96, create65);
        multiply(create64, create65, array2);
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
        final long n8 = n3 ^ n7 << 63;
        final long n9 = n4 ^ (n7 >>> 1 ^ n7 << 14);
        final long n10 = n5 ^ n7 >>> 50;
        final long n11 = n2 ^ n6 << 63;
        final long n12 = n8 ^ (n6 >>> 1 ^ n6 << 14);
        final long n13 = n9 ^ n6 >>> 50;
        final long n14 = n ^ n10 << 63;
        final long n15 = n11 ^ (n10 >>> 1 ^ n10 << 14);
        final long n16 = n12 ^ n10 >>> 50;
        final long n17 = n13 >>> 1;
        array2[0] = (n14 ^ n17 ^ n17 << 15);
        array2[1] = (n15 ^ n17 >>> 49);
        array2[2] = n16;
        array2[3] = (n13 & 0x1L);
    }
    
    public static void reduce63(final long[] array, final int n) {
        final long n2 = array[n + 3];
        final long n3 = n2 >>> 1;
        array[n] ^= (n3 ^ n3 << 15);
        final int n4 = n + 1;
        array[n4] ^= n3 >>> 49;
        array[n + 3] = (n2 & 0x1L);
    }
    
    public static void sqrt(final long[] array, final long[] array2) {
        final long unshuffle = Interleave.unshuffle(array[0]);
        final long unshuffle2 = Interleave.unshuffle(array[1]);
        final long n = (unshuffle & 0xFFFFFFFFL) | unshuffle2 << 32;
        final long n2 = unshuffle >>> 32 | (unshuffle2 & 0xFFFFFFFF00000000L);
        final long unshuffle3 = Interleave.unshuffle(array[2]);
        final long n3 = (unshuffle3 & 0xFFFFFFFFL) ^ array[3] << 32;
        final long n4 = unshuffle3 >>> 32;
        array2[0] = (n ^ n2 << 8);
        array2[1] = (n3 ^ n4 << 8 ^ n2 >>> 56 ^ n2 << 33);
        array2[2] = (n4 >>> 56 ^ n4 << 33 ^ n2 >>> 31);
        array2[3] = n4 >>> 31;
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
        array[0] = (n ^ n2 << 49);
        array[1] = (n2 >>> 15 ^ n3 << 34);
        array[2] = (n3 >>> 30 ^ n4 << 19);
        array[3] = (n4 >>> 45 ^ n5 << 4 ^ n6 << 53);
        array[4] = (n5 >>> 60 ^ n7 << 38 ^ n6 >>> 11);
        array[5] = (n7 >>> 26 ^ n8 << 23);
        array[6] = n8 >>> 41;
        array[7] = 0L;
    }
    
    protected static void implExpand(final long[] array, final long[] array2) {
        final long n = array[0];
        final long n2 = array[1];
        final long n3 = array[2];
        final long n4 = array[3];
        array2[0] = (n & 0x1FFFFFFFFFFFFL);
        array2[1] = ((n >>> 49 ^ n2 << 15) & 0x1FFFFFFFFFFFFL);
        array2[2] = ((n2 >>> 34 ^ n3 << 30) & 0x1FFFFFFFFFFFFL);
        array2[3] = (n3 >>> 19 ^ n4 << 45);
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
        int i = 36;
        do {
            final int n7 = (int)(n >>> i);
            final long n8 = array2[n7 & 0x7] ^ array2[n7 >>> 3 & 0x7] << 3 ^ array2[n7 >>> 6 & 0x7] << 6 ^ array2[n7 >>> 9 & 0x7] << 9 ^ array2[n7 >>> 12 & 0x7] << 12;
            n6 ^= n8 << i;
            n5 ^= n8 >>> -i;
            i -= 15;
        } while (i > 0);
        array[n3] ^= (n6 & 0x1FFFFFFFFFFFFL);
        final int n9 = n3 + 1;
        array[n9] ^= (n6 >>> 49 ^ n5 << 15);
    }
    
    protected static void implSquare(final long[] array, final long[] array2) {
        Interleave.expand64To128(array[0], array2, 0);
        Interleave.expand64To128(array[1], array2, 2);
        Interleave.expand64To128(array[2], array2, 4);
        array2[6] = (array[3] & 0x1L);
    }
}
