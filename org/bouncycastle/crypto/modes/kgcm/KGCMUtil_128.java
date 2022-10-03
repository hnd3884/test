package org.bouncycastle.crypto.modes.kgcm;

import org.bouncycastle.math.raw.Interleave;

public class KGCMUtil_128
{
    public static final int SIZE = 2;
    
    public static void add(final long[] array, final long[] array2, final long[] array3) {
        array3[0] = (array[0] ^ array2[0]);
        array3[1] = (array[1] ^ array2[1]);
    }
    
    public static void copy(final long[] array, final long[] array2) {
        array2[0] = array[0];
        array2[1] = array[1];
    }
    
    public static boolean equal(final long[] array, final long[] array2) {
        return (0x0L | (array[0] ^ array2[0]) | (array[1] ^ array2[1])) == 0x0L;
    }
    
    public static void multiply(final long[] array, final long[] array2, final long[] array3) {
        long n = array[0];
        long n2 = array[1];
        long n3 = array2[0];
        long n4 = array2[1];
        long n5 = 0L;
        long n6 = 0L;
        long n7 = 0L;
        for (int i = 0; i < 64; ++i) {
            final long n8 = -(n & 0x1L);
            n >>>= 1;
            n5 ^= (n3 & n8);
            final long n9 = n6 ^ (n4 & n8);
            final long n10 = -(n2 & 0x1L);
            n2 >>>= 1;
            n6 = (n9 ^ (n3 & n10));
            n7 ^= (n4 & n10);
            final long n11 = n4 >> 63;
            n4 = (n4 << 1 | n3 >>> 63);
            n3 = (n3 << 1 ^ (n11 & 0x87L));
        }
        final long n12 = n5 ^ (n7 ^ n7 << 1 ^ n7 << 2 ^ n7 << 7);
        final long n13 = n6 ^ (n7 >>> 63 ^ n7 >>> 62 ^ n7 >>> 57);
        array3[0] = n12;
        array3[1] = n13;
    }
    
    public static void multiplyX(final long[] array, final long[] array2) {
        final long n = array[0];
        final long n2 = array[1];
        array2[0] = (n << 1 ^ (n2 >> 63 & 0x87L));
        array2[1] = (n2 << 1 | n >>> 63);
    }
    
    public static void multiplyX8(final long[] array, final long[] array2) {
        final long n = array[0];
        final long n2 = array[1];
        final long n3 = n2 >>> 56;
        array2[0] = (n << 8 ^ n3 ^ n3 << 1 ^ n3 << 2 ^ n3 << 7);
        array2[1] = (n2 << 8 | n >>> 56);
    }
    
    public static void one(final long[] array) {
        array[0] = 1L;
        array[1] = 0L;
    }
    
    public static void square(final long[] array, final long[] array2) {
        final long[] array3 = new long[4];
        Interleave.expand64To128(array[0], array3, 0);
        Interleave.expand64To128(array[1], array3, 2);
        final long n = array3[0];
        final long n2 = array3[1];
        final long n3 = array3[2];
        final long n4 = array3[3];
        final long n5 = n2 ^ (n4 ^ n4 << 1 ^ n4 << 2 ^ n4 << 7);
        final long n6 = n3 ^ (n4 >>> 63 ^ n4 >>> 62 ^ n4 >>> 57);
        final long n7 = n ^ (n6 ^ n6 << 1 ^ n6 << 2 ^ n6 << 7);
        final long n8 = n5 ^ (n6 >>> 63 ^ n6 >>> 62 ^ n6 >>> 57);
        array2[0] = n7;
        array2[1] = n8;
    }
    
    public static void x(final long[] array) {
        array[0] = 2L;
        array[1] = 0L;
    }
    
    public static void zero(final long[] array) {
        array[1] = (array[0] = 0L);
    }
}
