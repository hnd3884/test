package org.bouncycastle.crypto.modes.kgcm;

import org.bouncycastle.math.raw.Interleave;

public class KGCMUtil_256
{
    public static final int SIZE = 4;
    
    public static void add(final long[] array, final long[] array2, final long[] array3) {
        array3[0] = (array[0] ^ array2[0]);
        array3[1] = (array[1] ^ array2[1]);
        array3[2] = (array[2] ^ array2[2]);
        array3[3] = (array[3] ^ array2[3]);
    }
    
    public static void copy(final long[] array, final long[] array2) {
        array2[0] = array[0];
        array2[1] = array[1];
        array2[2] = array[2];
        array2[3] = array[3];
    }
    
    public static boolean equal(final long[] array, final long[] array2) {
        return (0x0L | (array[0] ^ array2[0]) | (array[1] ^ array2[1]) | (array[2] ^ array2[2]) | (array[3] ^ array2[3])) == 0x0L;
    }
    
    public static void multiply(final long[] array, final long[] array2, final long[] array3) {
        long n = array[0];
        long n2 = array[1];
        long n3 = array[2];
        long n4 = array[3];
        long n5 = array2[0];
        long n6 = array2[1];
        long n7 = array2[2];
        long n8 = array2[3];
        long n9 = 0L;
        long n10 = 0L;
        long n11 = 0L;
        long n12 = 0L;
        long n13 = 0L;
        for (int i = 0; i < 64; ++i) {
            final long n14 = -(n & 0x1L);
            n >>>= 1;
            n9 ^= (n5 & n14);
            final long n15 = n10 ^ (n6 & n14);
            final long n16 = n11 ^ (n7 & n14);
            final long n17 = n12 ^ (n8 & n14);
            final long n18 = -(n2 & 0x1L);
            n2 >>>= 1;
            n10 = (n15 ^ (n5 & n18));
            n11 = (n16 ^ (n6 & n18));
            n12 = (n17 ^ (n7 & n18));
            n13 ^= (n8 & n18);
            final long n19 = n8 >> 63;
            n8 = (n8 << 1 | n7 >>> 63);
            n7 = (n7 << 1 | n6 >>> 63);
            n6 = (n6 << 1 | n5 >>> 63);
            n5 = (n5 << 1 ^ (n19 & 0x425L));
        }
        final long n20 = n8;
        long n21 = n7;
        long n22 = n6;
        long n23 = n5 ^ n20 >>> 62 ^ n20 >>> 59 ^ n20 >>> 54;
        long n24 = n20 ^ n20 << 2 ^ n20 << 5 ^ n20 << 10;
        for (int j = 0; j < 64; ++j) {
            final long n25 = -(n3 & 0x1L);
            n3 >>>= 1;
            n9 ^= (n24 & n25);
            final long n26 = n10 ^ (n23 & n25);
            final long n27 = n11 ^ (n22 & n25);
            final long n28 = n12 ^ (n21 & n25);
            final long n29 = -(n4 & 0x1L);
            n4 >>>= 1;
            n10 = (n26 ^ (n24 & n29));
            n11 = (n27 ^ (n23 & n29));
            n12 = (n28 ^ (n22 & n29));
            n13 ^= (n21 & n29);
            final long n30 = n21 >> 63;
            n21 = (n21 << 1 | n22 >>> 63);
            n22 = (n22 << 1 | n23 >>> 63);
            n23 = (n23 << 1 | n24 >>> 63);
            n24 = (n24 << 1 ^ (n30 & 0x425L));
        }
        final long n31 = n9 ^ (n13 ^ n13 << 2 ^ n13 << 5 ^ n13 << 10);
        final long n32 = n10 ^ (n13 >>> 62 ^ n13 >>> 59 ^ n13 >>> 54);
        array3[0] = n31;
        array3[1] = n32;
        array3[2] = n11;
        array3[3] = n12;
    }
    
    public static void multiplyX(final long[] array, final long[] array2) {
        final long n = array[0];
        final long n2 = array[1];
        final long n3 = array[2];
        final long n4 = array[3];
        array2[0] = (n << 1 ^ (n4 >> 63 & 0x425L));
        array2[1] = (n2 << 1 | n >>> 63);
        array2[2] = (n3 << 1 | n2 >>> 63);
        array2[3] = (n4 << 1 | n3 >>> 63);
    }
    
    public static void multiplyX8(final long[] array, final long[] array2) {
        final long n = array[0];
        final long n2 = array[1];
        final long n3 = array[2];
        final long n4 = array[3];
        final long n5 = n4 >>> 56;
        array2[0] = (n << 8 ^ n5 ^ n5 << 2 ^ n5 << 5 ^ n5 << 10);
        array2[1] = (n2 << 8 | n >>> 56);
        array2[2] = (n3 << 8 | n2 >>> 56);
        array2[3] = (n4 << 8 | n3 >>> 56);
    }
    
    public static void one(final long[] array) {
        array[0] = 1L;
        array[1] = 0L;
        array[3] = (array[2] = 0L);
    }
    
    public static void square(final long[] array, final long[] array2) {
        final long[] array3 = new long[8];
        for (int i = 0; i < 4; ++i) {
            Interleave.expand64To128(array[i], array3, i << 1);
        }
        int n = 8;
        while (--n >= 4) {
            final long n2 = array3[n];
            final long[] array4 = array3;
            final int n3 = n - 4;
            array4[n3] ^= (n2 ^ n2 << 2 ^ n2 << 5 ^ n2 << 10);
            final long[] array5 = array3;
            final int n4 = n - 4 + 1;
            array5[n4] ^= (n2 >>> 62 ^ n2 >>> 59 ^ n2 >>> 54);
        }
        copy(array3, array2);
    }
    
    public static void x(final long[] array) {
        array[0] = 2L;
        array[1] = 0L;
        array[3] = (array[2] = 0L);
    }
    
    public static void zero(final long[] array) {
        array[1] = (array[0] = 0L);
        array[3] = (array[2] = 0L);
    }
}
