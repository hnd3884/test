package org.bouncycastle.pqc.math.linearalgebra;

public final class BigEndianConversions
{
    private BigEndianConversions() {
    }
    
    public static byte[] I2OSP(final int n) {
        return new byte[] { (byte)(n >>> 24), (byte)(n >>> 16), (byte)(n >>> 8), (byte)n };
    }
    
    public static byte[] I2OSP(final int n, final int n2) throws ArithmeticException {
        if (n < 0) {
            return null;
        }
        final int ceilLog256 = IntegerFunctions.ceilLog256(n);
        if (ceilLog256 > n2) {
            throw new ArithmeticException("Cannot encode given integer into specified number of octets.");
        }
        final byte[] array = new byte[n2];
        for (int i = n2 - 1; i >= n2 - ceilLog256; --i) {
            array[i] = (byte)(n >>> 8 * (n2 - 1 - i));
        }
        return array;
    }
    
    public static void I2OSP(final int n, final byte[] array, int n2) {
        array[n2++] = (byte)(n >>> 24);
        array[n2++] = (byte)(n >>> 16);
        array[n2++] = (byte)(n >>> 8);
        array[n2] = (byte)n;
    }
    
    public static byte[] I2OSP(final long n) {
        return new byte[] { (byte)(n >>> 56), (byte)(n >>> 48), (byte)(n >>> 40), (byte)(n >>> 32), (byte)(n >>> 24), (byte)(n >>> 16), (byte)(n >>> 8), (byte)n };
    }
    
    public static void I2OSP(final long n, final byte[] array, int n2) {
        array[n2++] = (byte)(n >>> 56);
        array[n2++] = (byte)(n >>> 48);
        array[n2++] = (byte)(n >>> 40);
        array[n2++] = (byte)(n >>> 32);
        array[n2++] = (byte)(n >>> 24);
        array[n2++] = (byte)(n >>> 16);
        array[n2++] = (byte)(n >>> 8);
        array[n2] = (byte)n;
    }
    
    public static void I2OSP(final int n, final byte[] array, final int n2, final int n3) {
        for (int i = n3 - 1; i >= 0; --i) {
            array[n2 + i] = (byte)(n >>> 8 * (n3 - 1 - i));
        }
    }
    
    public static int OS2IP(final byte[] array) {
        if (array.length > 4) {
            throw new ArithmeticException("invalid input length");
        }
        if (array.length == 0) {
            return 0;
        }
        int n = 0;
        for (int i = 0; i < array.length; ++i) {
            n |= (array[i] & 0xFF) << 8 * (array.length - 1 - i);
        }
        return n;
    }
    
    public static int OS2IP(final byte[] array, int n) {
        return (array[n++] & 0xFF) << 24 | (array[n++] & 0xFF) << 16 | (array[n++] & 0xFF) << 8 | (array[n] & 0xFF);
    }
    
    public static int OS2IP(final byte[] array, final int n, final int n2) {
        if (array.length == 0 || array.length < n + n2 - 1) {
            return 0;
        }
        int n3 = 0;
        for (int i = 0; i < n2; ++i) {
            n3 |= (array[n + i] & 0xFF) << 8 * (n2 - i - 1);
        }
        return n3;
    }
    
    public static long OS2LIP(final byte[] array, int n) {
        return ((long)array[n++] & 0xFFL) << 56 | ((long)array[n++] & 0xFFL) << 48 | ((long)array[n++] & 0xFFL) << 40 | ((long)array[n++] & 0xFFL) << 32 | ((long)array[n++] & 0xFFL) << 24 | (long)((array[n++] & 0xFF) << 16) | (long)((array[n++] & 0xFF) << 8) | (long)(array[n] & 0xFF);
    }
    
    public static byte[] toByteArray(final int[] array) {
        final byte[] array2 = new byte[array.length << 2];
        for (int i = 0; i < array.length; ++i) {
            I2OSP(array[i], array2, i << 2);
        }
        return array2;
    }
    
    public static byte[] toByteArray(final int[] array, final int n) {
        final int length = array.length;
        final byte[] array2 = new byte[n];
        int n2 = 0;
        for (int i = 0; i <= length - 2; ++i, n2 += 4) {
            I2OSP(array[i], array2, n2);
        }
        I2OSP(array[length - 1], array2, n2, n - n2);
        return array2;
    }
    
    public static int[] toIntArray(final byte[] array) {
        final int n = (array.length + 3) / 4;
        final int n2 = array.length & 0x3;
        final int[] array2 = new int[n];
        int n3 = 0;
        for (int i = 0; i <= n - 2; ++i, n3 += 4) {
            array2[i] = OS2IP(array, n3);
        }
        if (n2 != 0) {
            array2[n - 1] = OS2IP(array, n3, n2);
        }
        else {
            array2[n - 1] = OS2IP(array, n3);
        }
        return array2;
    }
}
