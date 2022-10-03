package org.bouncycastle.crypto.util;

public abstract class Pack
{
    public static int bigEndianToInt(final byte[] array, int n) {
        return array[n] << 24 | (array[++n] & 0xFF) << 16 | (array[++n] & 0xFF) << 8 | (array[++n] & 0xFF);
    }
    
    public static void bigEndianToInt(final byte[] array, int n, final int[] array2) {
        for (int i = 0; i < array2.length; ++i) {
            array2[i] = bigEndianToInt(array, n);
            n += 4;
        }
    }
    
    public static byte[] intToBigEndian(final int n) {
        final byte[] array = new byte[4];
        intToBigEndian(n, array, 0);
        return array;
    }
    
    public static void intToBigEndian(final int n, final byte[] array, int n2) {
        array[n2] = (byte)(n >>> 24);
        array[++n2] = (byte)(n >>> 16);
        array[++n2] = (byte)(n >>> 8);
        array[++n2] = (byte)n;
    }
    
    public static byte[] intToBigEndian(final int[] array) {
        final byte[] array2 = new byte[4 * array.length];
        intToBigEndian(array, array2, 0);
        return array2;
    }
    
    public static void intToBigEndian(final int[] array, final byte[] array2, int n) {
        for (int i = 0; i < array.length; ++i) {
            intToBigEndian(array[i], array2, n);
            n += 4;
        }
    }
    
    public static long bigEndianToLong(final byte[] array, final int n) {
        return ((long)bigEndianToInt(array, n) & 0xFFFFFFFFL) << 32 | ((long)bigEndianToInt(array, n + 4) & 0xFFFFFFFFL);
    }
    
    public static void bigEndianToLong(final byte[] array, int n, final long[] array2) {
        for (int i = 0; i < array2.length; ++i) {
            array2[i] = bigEndianToLong(array, n);
            n += 8;
        }
    }
    
    public static byte[] longToBigEndian(final long n) {
        final byte[] array = new byte[8];
        longToBigEndian(n, array, 0);
        return array;
    }
    
    public static void longToBigEndian(final long n, final byte[] array, final int n2) {
        intToBigEndian((int)(n >>> 32), array, n2);
        intToBigEndian((int)(n & 0xFFFFFFFFL), array, n2 + 4);
    }
    
    public static byte[] longToBigEndian(final long[] array) {
        final byte[] array2 = new byte[8 * array.length];
        longToBigEndian(array, array2, 0);
        return array2;
    }
    
    public static void longToBigEndian(final long[] array, final byte[] array2, int n) {
        for (int i = 0; i < array.length; ++i) {
            longToBigEndian(array[i], array2, n);
            n += 8;
        }
    }
    
    public static int littleEndianToInt(final byte[] array, int n) {
        return (array[n] & 0xFF) | (array[++n] & 0xFF) << 8 | (array[++n] & 0xFF) << 16 | array[++n] << 24;
    }
    
    public static void littleEndianToInt(final byte[] array, int n, final int[] array2) {
        for (int i = 0; i < array2.length; ++i) {
            array2[i] = littleEndianToInt(array, n);
            n += 4;
        }
    }
    
    public static void littleEndianToInt(final byte[] array, int n, final int[] array2, final int n2, final int n3) {
        for (int i = 0; i < n3; ++i) {
            array2[n2 + i] = littleEndianToInt(array, n);
            n += 4;
        }
    }
    
    public static byte[] intToLittleEndian(final int n) {
        final byte[] array = new byte[4];
        intToLittleEndian(n, array, 0);
        return array;
    }
    
    public static void intToLittleEndian(final int n, final byte[] array, int n2) {
        array[n2] = (byte)n;
        array[++n2] = (byte)(n >>> 8);
        array[++n2] = (byte)(n >>> 16);
        array[++n2] = (byte)(n >>> 24);
    }
    
    public static byte[] intToLittleEndian(final int[] array) {
        final byte[] array2 = new byte[4 * array.length];
        intToLittleEndian(array, array2, 0);
        return array2;
    }
    
    public static void intToLittleEndian(final int[] array, final byte[] array2, int n) {
        for (int i = 0; i < array.length; ++i) {
            intToLittleEndian(array[i], array2, n);
            n += 4;
        }
    }
    
    public static long littleEndianToLong(final byte[] array, final int n) {
        return ((long)littleEndianToInt(array, n + 4) & 0xFFFFFFFFL) << 32 | ((long)littleEndianToInt(array, n) & 0xFFFFFFFFL);
    }
    
    public static void littleEndianToLong(final byte[] array, int n, final long[] array2) {
        for (int i = 0; i < array2.length; ++i) {
            array2[i] = littleEndianToLong(array, n);
            n += 8;
        }
    }
    
    public static byte[] longToLittleEndian(final long n) {
        final byte[] array = new byte[8];
        longToLittleEndian(n, array, 0);
        return array;
    }
    
    public static void longToLittleEndian(final long n, final byte[] array, final int n2) {
        intToLittleEndian((int)(n & 0xFFFFFFFFL), array, n2);
        intToLittleEndian((int)(n >>> 32), array, n2 + 4);
    }
    
    public static byte[] longToLittleEndian(final long[] array) {
        final byte[] array2 = new byte[8 * array.length];
        longToLittleEndian(array, array2, 0);
        return array2;
    }
    
    public static void longToLittleEndian(final long[] array, final byte[] array2, int n) {
        for (int i = 0; i < array.length; ++i) {
            longToLittleEndian(array[i], array2, n);
            n += 8;
        }
    }
}
