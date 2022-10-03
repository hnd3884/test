package org.bouncycastle.crypto.modes.kgcm;

import org.bouncycastle.math.raw.Interleave;

public class KGCMUtil_512
{
    public static final int SIZE = 8;
    
    public static void add(final long[] array, final long[] array2, final long[] array3) {
        array3[0] = (array[0] ^ array2[0]);
        array3[1] = (array[1] ^ array2[1]);
        array3[2] = (array[2] ^ array2[2]);
        array3[3] = (array[3] ^ array2[3]);
        array3[4] = (array[4] ^ array2[4]);
        array3[5] = (array[5] ^ array2[5]);
        array3[6] = (array[6] ^ array2[6]);
        array3[7] = (array[7] ^ array2[7]);
    }
    
    public static void copy(final long[] array, final long[] array2) {
        array2[0] = array[0];
        array2[1] = array[1];
        array2[2] = array[2];
        array2[3] = array[3];
        array2[4] = array[4];
        array2[5] = array[5];
        array2[6] = array[6];
        array2[7] = array[7];
    }
    
    public static boolean equal(final long[] array, final long[] array2) {
        return (0x0L | (array[0] ^ array2[0]) | (array[1] ^ array2[1]) | (array[2] ^ array2[2]) | (array[3] ^ array2[3]) | (array[4] ^ array2[4]) | (array[5] ^ array2[5]) | (array[6] ^ array2[6]) | (array[7] ^ array2[7])) == 0x0L;
    }
    
    public static void multiply(final long[] array, final long[] array2, final long[] array3) {
        long n = array2[0];
        long n2 = array2[1];
        long n3 = array2[2];
        long n4 = array2[3];
        long n5 = array2[4];
        long n6 = array2[5];
        long n7 = array2[6];
        long n8 = array2[7];
        long n9 = 0L;
        long n10 = 0L;
        long n11 = 0L;
        long n12 = 0L;
        long n13 = 0L;
        long n14 = 0L;
        long n15 = 0L;
        long n16 = 0L;
        long n17 = 0L;
        for (int i = 0; i < 8; i += 2) {
            long n18 = array[i];
            long n19 = array[i + 1];
            for (int j = 0; j < 64; ++j) {
                final long n20 = -(n18 & 0x1L);
                n18 >>>= 1;
                n9 ^= (n & n20);
                final long n21 = n10 ^ (n2 & n20);
                final long n22 = n11 ^ (n3 & n20);
                final long n23 = n12 ^ (n4 & n20);
                final long n24 = n13 ^ (n5 & n20);
                final long n25 = n14 ^ (n6 & n20);
                final long n26 = n15 ^ (n7 & n20);
                final long n27 = n16 ^ (n8 & n20);
                final long n28 = -(n19 & 0x1L);
                n19 >>>= 1;
                n10 = (n21 ^ (n & n28));
                n11 = (n22 ^ (n2 & n28));
                n12 = (n23 ^ (n3 & n28));
                n13 = (n24 ^ (n4 & n28));
                n14 = (n25 ^ (n5 & n28));
                n15 = (n26 ^ (n6 & n28));
                n16 = (n27 ^ (n7 & n28));
                n17 ^= (n8 & n28);
                final long n29 = n8 >> 63;
                n8 = (n8 << 1 | n7 >>> 63);
                n7 = (n7 << 1 | n6 >>> 63);
                n6 = (n6 << 1 | n5 >>> 63);
                n5 = (n5 << 1 | n4 >>> 63);
                n4 = (n4 << 1 | n3 >>> 63);
                n3 = (n3 << 1 | n2 >>> 63);
                n2 = (n2 << 1 | n >>> 63);
                n = (n << 1 ^ (n29 & 0x125L));
            }
            final long n30 = n8;
            n8 = n7;
            n7 = n6;
            n6 = n5;
            n5 = n4;
            n4 = n3;
            n3 = n2;
            n2 = (n ^ n30 >>> 62 ^ n30 >>> 59 ^ n30 >>> 56);
            n = (n30 ^ n30 << 2 ^ n30 << 5 ^ n30 << 8);
        }
        final long n31 = n9 ^ (n17 ^ n17 << 2 ^ n17 << 5 ^ n17 << 8);
        final long n32 = n10 ^ (n17 >>> 62 ^ n17 >>> 59 ^ n17 >>> 56);
        array3[0] = n31;
        array3[1] = n32;
        array3[2] = n11;
        array3[3] = n12;
        array3[4] = n13;
        array3[5] = n14;
        array3[6] = n15;
        array3[7] = n16;
    }
    
    public static void multiplyX(final long[] array, final long[] array2) {
        final long n = array[0];
        final long n2 = array[1];
        final long n3 = array[2];
        final long n4 = array[3];
        final long n5 = array[4];
        final long n6 = array[5];
        final long n7 = array[6];
        final long n8 = array[7];
        array2[0] = (n << 1 ^ (n8 >> 63 & 0x125L));
        array2[1] = (n2 << 1 | n >>> 63);
        array2[2] = (n3 << 1 | n2 >>> 63);
        array2[3] = (n4 << 1 | n3 >>> 63);
        array2[4] = (n5 << 1 | n4 >>> 63);
        array2[5] = (n6 << 1 | n5 >>> 63);
        array2[6] = (n7 << 1 | n6 >>> 63);
        array2[7] = (n8 << 1 | n7 >>> 63);
    }
    
    public static void multiplyX8(final long[] array, final long[] array2) {
        final long n = array[0];
        final long n2 = array[1];
        final long n3 = array[2];
        final long n4 = array[3];
        final long n5 = array[4];
        final long n6 = array[5];
        final long n7 = array[6];
        final long n8 = array[7];
        final long n9 = n8 >>> 56;
        array2[0] = (n << 8 ^ n9 ^ n9 << 2 ^ n9 << 5 ^ n9 << 8);
        array2[1] = (n2 << 8 | n >>> 56);
        array2[2] = (n3 << 8 | n2 >>> 56);
        array2[3] = (n4 << 8 | n3 >>> 56);
        array2[4] = (n5 << 8 | n4 >>> 56);
        array2[5] = (n6 << 8 | n5 >>> 56);
        array2[6] = (n7 << 8 | n6 >>> 56);
        array2[7] = (n8 << 8 | n7 >>> 56);
    }
    
    public static void one(final long[] array) {
        array[0] = 1L;
        array[1] = 0L;
        array[3] = (array[2] = 0L);
        array[5] = (array[4] = 0L);
        array[7] = (array[6] = 0L);
    }
    
    public static void square(final long[] array, final long[] array2) {
        final long[] array3 = new long[16];
        for (int i = 0; i < 8; ++i) {
            Interleave.expand64To128(array[i], array3, i << 1);
        }
        int n = 16;
        while (--n >= 8) {
            final long n2 = array3[n];
            final long[] array4 = array3;
            final int n3 = n - 8;
            array4[n3] ^= (n2 ^ n2 << 2 ^ n2 << 5 ^ n2 << 8);
            final long[] array5 = array3;
            final int n4 = n - 8 + 1;
            array5[n4] ^= (n2 >>> 62 ^ n2 >>> 59 ^ n2 >>> 56);
        }
        copy(array3, array2);
    }
    
    public static void x(final long[] array) {
        array[0] = 2L;
        array[1] = 0L;
        array[3] = (array[2] = 0L);
        array[5] = (array[4] = 0L);
        array[7] = (array[6] = 0L);
    }
    
    public static void zero(final long[] array) {
        array[1] = (array[0] = 0L);
        array[3] = (array[2] = 0L);
        array[5] = (array[4] = 0L);
        array[7] = (array[6] = 0L);
    }
}
