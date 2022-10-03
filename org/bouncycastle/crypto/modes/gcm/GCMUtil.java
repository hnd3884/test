package org.bouncycastle.crypto.modes.gcm;

import org.bouncycastle.math.raw.Interleave;
import org.bouncycastle.util.Pack;

public abstract class GCMUtil
{
    private static final int E1 = -520093696;
    private static final long E1L = -2233785415175766016L;
    
    public static byte[] oneAsBytes() {
        final byte[] array = new byte[16];
        array[0] = -128;
        return array;
    }
    
    public static int[] oneAsInts() {
        final int[] array = new int[4];
        array[0] = Integer.MIN_VALUE;
        return array;
    }
    
    public static long[] oneAsLongs() {
        return new long[] { Long.MIN_VALUE, 0L };
    }
    
    public static byte[] asBytes(final int[] array) {
        final byte[] array2 = new byte[16];
        Pack.intToBigEndian(array, array2, 0);
        return array2;
    }
    
    public static void asBytes(final int[] array, final byte[] array2) {
        Pack.intToBigEndian(array, array2, 0);
    }
    
    public static byte[] asBytes(final long[] array) {
        final byte[] array2 = new byte[16];
        Pack.longToBigEndian(array, array2, 0);
        return array2;
    }
    
    public static void asBytes(final long[] array, final byte[] array2) {
        Pack.longToBigEndian(array, array2, 0);
    }
    
    public static int[] asInts(final byte[] array) {
        final int[] array2 = new int[4];
        Pack.bigEndianToInt(array, 0, array2);
        return array2;
    }
    
    public static void asInts(final byte[] array, final int[] array2) {
        Pack.bigEndianToInt(array, 0, array2);
    }
    
    public static long[] asLongs(final byte[] array) {
        final long[] array2 = new long[2];
        Pack.bigEndianToLong(array, 0, array2);
        return array2;
    }
    
    public static void asLongs(final byte[] array, final long[] array2) {
        Pack.bigEndianToLong(array, 0, array2);
    }
    
    public static void copy(final int[] array, final int[] array2) {
        array2[0] = array[0];
        array2[1] = array[1];
        array2[2] = array[2];
        array2[3] = array[3];
    }
    
    public static void copy(final long[] array, final long[] array2) {
        array2[0] = array[0];
        array2[1] = array[1];
    }
    
    public static void divideP(final long[] array, final long[] array2) {
        final long n = array[0];
        final long n2 = array[1];
        final long n3 = n >> 63;
        array2[0] = ((n ^ (n3 & 0xE100000000000000L)) << 1 | n2 >>> 63);
        array2[1] = (n2 << 1 | -n3);
    }
    
    public static void multiply(final byte[] array, final byte[] array2) {
        final long[] longs = asLongs(array);
        multiply(longs, asLongs(array2));
        asBytes(longs, array);
    }
    
    public static void multiply(final int[] array, final int[] array2) {
        int n = array2[0];
        int n2 = array2[1];
        int n3 = array2[2];
        int n4 = array2[3];
        int n5 = 0;
        int n6 = 0;
        int n7 = 0;
        int n8 = 0;
        for (int i = 0; i < 4; ++i) {
            int n9 = array[i];
            for (int j = 0; j < 32; ++j) {
                final int n10 = n9 >> 31;
                n9 <<= 1;
                n5 ^= (n & n10);
                n6 ^= (n2 & n10);
                n7 ^= (n3 & n10);
                n8 ^= (n4 & n10);
                final int n11 = n4 << 31 >> 8;
                n4 = (n4 >>> 1 | n3 << 31);
                n3 = (n3 >>> 1 | n2 << 31);
                n2 = (n2 >>> 1 | n << 31);
                n = (n >>> 1 ^ (n11 & 0xE1000000));
            }
        }
        array[0] = n5;
        array[1] = n6;
        array[2] = n7;
        array[3] = n8;
    }
    
    public static void multiply(final long[] array, final long[] array2) {
        long n = array[0];
        long n2 = array[1];
        long n3 = array2[0];
        long n4 = array2[1];
        long n5 = 0L;
        long n6 = 0L;
        long n7 = 0L;
        for (int i = 0; i < 64; ++i) {
            final long n8 = n >> 63;
            n <<= 1;
            n5 ^= (n3 & n8);
            final long n9 = n6 ^ (n4 & n8);
            final long n10 = n2 >> 63;
            n2 <<= 1;
            n6 = (n9 ^ (n3 & n10));
            n7 ^= (n4 & n10);
            final long n11 = n4 << 63 >> 8;
            n4 = (n4 >>> 1 | n3 << 63);
            n3 = (n3 >>> 1 ^ (n11 & 0xE100000000000000L));
        }
        final long n12 = n5 ^ (n7 ^ n7 >>> 1 ^ n7 >>> 2 ^ n7 >>> 7);
        final long n13 = n6 ^ (n7 << 63 ^ n7 << 62 ^ n7 << 57);
        array[0] = n12;
        array[1] = n13;
    }
    
    public static void multiplyP(final int[] array) {
        final int n = array[0];
        final int n2 = array[1];
        final int n3 = array[2];
        final int n4 = array[3];
        array[0] = (n >>> 1 ^ (n4 << 31 >> 31 & 0xE1000000));
        array[1] = (n2 >>> 1 | n << 31);
        array[2] = (n3 >>> 1 | n2 << 31);
        array[3] = (n4 >>> 1 | n3 << 31);
    }
    
    public static void multiplyP(final int[] array, final int[] array2) {
        final int n = array[0];
        final int n2 = array[1];
        final int n3 = array[2];
        final int n4 = array[3];
        array2[0] = (n >>> 1 ^ (n4 << 31 >> 31 & 0xE1000000));
        array2[1] = (n2 >>> 1 | n << 31);
        array2[2] = (n3 >>> 1 | n2 << 31);
        array2[3] = (n4 >>> 1 | n3 << 31);
    }
    
    public static void multiplyP(final long[] array) {
        final long n = array[0];
        final long n2 = array[1];
        array[0] = (n >>> 1 ^ (n2 << 63 >> 63 & 0xE100000000000000L));
        array[1] = (n2 >>> 1 | n << 63);
    }
    
    public static void multiplyP(final long[] array, final long[] array2) {
        final long n = array[0];
        final long n2 = array[1];
        array2[0] = (n >>> 1 ^ (n2 << 63 >> 63 & 0xE100000000000000L));
        array2[1] = (n2 >>> 1 | n << 63);
    }
    
    public static void multiplyP3(final long[] array, final long[] array2) {
        final long n = array[0];
        final long n2 = array[1];
        final long n3 = n2 << 61;
        array2[0] = (n >>> 3 ^ n3 ^ n3 >>> 1 ^ n3 >>> 2 ^ n3 >>> 7);
        array2[1] = (n2 >>> 3 | n << 61);
    }
    
    public static void multiplyP4(final long[] array, final long[] array2) {
        final long n = array[0];
        final long n2 = array[1];
        final long n3 = n2 << 60;
        array2[0] = (n >>> 4 ^ n3 ^ n3 >>> 1 ^ n3 >>> 2 ^ n3 >>> 7);
        array2[1] = (n2 >>> 4 | n << 60);
    }
    
    public static void multiplyP7(final long[] array, final long[] array2) {
        final long n = array[0];
        final long n2 = array[1];
        final long n3 = n2 << 57;
        array2[0] = (n >>> 7 ^ n3 ^ n3 >>> 1 ^ n3 >>> 2 ^ n3 >>> 7);
        array2[1] = (n2 >>> 7 | n << 57);
    }
    
    public static void multiplyP8(final int[] array) {
        final int n = array[0];
        final int n2 = array[1];
        final int n3 = array[2];
        final int n4 = array[3];
        final int n5 = n4 << 24;
        array[0] = (n >>> 8 ^ n5 ^ n5 >>> 1 ^ n5 >>> 2 ^ n5 >>> 7);
        array[1] = (n2 >>> 8 | n << 24);
        array[2] = (n3 >>> 8 | n2 << 24);
        array[3] = (n4 >>> 8 | n3 << 24);
    }
    
    public static void multiplyP8(final int[] array, final int[] array2) {
        final int n = array[0];
        final int n2 = array[1];
        final int n3 = array[2];
        final int n4 = array[3];
        final int n5 = n4 << 24;
        array2[0] = (n >>> 8 ^ n5 ^ n5 >>> 1 ^ n5 >>> 2 ^ n5 >>> 7);
        array2[1] = (n2 >>> 8 | n << 24);
        array2[2] = (n3 >>> 8 | n2 << 24);
        array2[3] = (n4 >>> 8 | n3 << 24);
    }
    
    public static void multiplyP8(final long[] array) {
        final long n = array[0];
        final long n2 = array[1];
        final long n3 = n2 << 56;
        array[0] = (n >>> 8 ^ n3 ^ n3 >>> 1 ^ n3 >>> 2 ^ n3 >>> 7);
        array[1] = (n2 >>> 8 | n << 56);
    }
    
    public static void multiplyP8(final long[] array, final long[] array2) {
        final long n = array[0];
        final long n2 = array[1];
        final long n3 = n2 << 56;
        array2[0] = (n >>> 8 ^ n3 ^ n3 >>> 1 ^ n3 >>> 2 ^ n3 >>> 7);
        array2[1] = (n2 >>> 8 | n << 56);
    }
    
    public static long[] pAsLongs() {
        return new long[] { 4611686018427387904L, 0L };
    }
    
    public static void square(final long[] array, final long[] array2) {
        final long[] array3 = new long[4];
        Interleave.expand64To128Rev(array[0], array3, 0);
        Interleave.expand64To128Rev(array[1], array3, 2);
        final long n = array3[0];
        final long n2 = array3[1];
        final long n3 = array3[2];
        final long n4 = array3[3];
        final long n5 = n2 ^ (n4 ^ n4 >>> 1 ^ n4 >>> 2 ^ n4 >>> 7);
        final long n6 = n3 ^ (n4 << 63 ^ n4 << 62 ^ n4 << 57);
        final long n7 = n ^ (n6 ^ n6 >>> 1 ^ n6 >>> 2 ^ n6 >>> 7);
        final long n8 = n5 ^ (n6 << 63 ^ n6 << 62 ^ n6 << 57);
        array2[0] = n7;
        array2[1] = n8;
    }
    
    public static void xor(final byte[] array, final byte[] array2) {
        int n = 0;
        do {
            final int n2 = n;
            array[n2] ^= array2[n];
            ++n;
            final int n3 = n;
            array[n3] ^= array2[n];
            ++n;
            final int n4 = n;
            array[n4] ^= array2[n];
            ++n;
            final int n5 = n;
            array[n5] ^= array2[n];
        } while (++n < 16);
    }
    
    public static void xor(final byte[] array, final byte[] array2, final int n) {
        int n2 = 0;
        do {
            final int n3 = n2;
            array[n3] ^= array2[n + n2];
            ++n2;
            final int n4 = n2;
            array[n4] ^= array2[n + n2];
            ++n2;
            final int n5 = n2;
            array[n5] ^= array2[n + n2];
            ++n2;
            final int n6 = n2;
            array[n6] ^= array2[n + n2];
        } while (++n2 < 16);
    }
    
    public static void xor(final byte[] array, final int n, final byte[] array2, final int n2, final byte[] array3, final int n3) {
        int n4 = 0;
        do {
            array3[n3 + n4] = (byte)(array[n + n4] ^ array2[n2 + n4]);
            ++n4;
            array3[n3 + n4] = (byte)(array[n + n4] ^ array2[n2 + n4]);
            ++n4;
            array3[n3 + n4] = (byte)(array[n + n4] ^ array2[n2 + n4]);
            ++n4;
            array3[n3 + n4] = (byte)(array[n + n4] ^ array2[n2 + n4]);
        } while (++n4 < 16);
    }
    
    public static void xor(final byte[] array, final byte[] array2, final int n, int n2) {
        while (--n2 >= 0) {
            final int n3 = n2;
            array[n3] ^= array2[n + n2];
        }
    }
    
    public static void xor(final byte[] array, final int n, final byte[] array2, final int n2, int n3) {
        while (--n3 >= 0) {
            final int n4 = n + n3;
            array[n4] ^= array2[n2 + n3];
        }
    }
    
    public static void xor(final byte[] array, final byte[] array2, final byte[] array3) {
        int n = 0;
        do {
            array3[n] = (byte)(array[n] ^ array2[n]);
            ++n;
            array3[n] = (byte)(array[n] ^ array2[n]);
            ++n;
            array3[n] = (byte)(array[n] ^ array2[n]);
            ++n;
            array3[n] = (byte)(array[n] ^ array2[n]);
        } while (++n < 16);
    }
    
    public static void xor(final int[] array, final int[] array2) {
        final int n = 0;
        array[n] ^= array2[0];
        final int n2 = 1;
        array[n2] ^= array2[1];
        final int n3 = 2;
        array[n3] ^= array2[2];
        final int n4 = 3;
        array[n4] ^= array2[3];
    }
    
    public static void xor(final int[] array, final int[] array2, final int[] array3) {
        array3[0] = (array[0] ^ array2[0]);
        array3[1] = (array[1] ^ array2[1]);
        array3[2] = (array[2] ^ array2[2]);
        array3[3] = (array[3] ^ array2[3]);
    }
    
    public static void xor(final long[] array, final long[] array2) {
        final int n = 0;
        array[n] ^= array2[0];
        final int n2 = 1;
        array[n2] ^= array2[1];
    }
    
    public static void xor(final long[] array, final long[] array2, final long[] array3) {
        array3[0] = (array[0] ^ array2[0]);
        array3[1] = (array[1] ^ array2[1]);
    }
}
