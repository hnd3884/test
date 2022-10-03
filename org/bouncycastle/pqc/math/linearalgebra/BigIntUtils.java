package org.bouncycastle.pqc.math.linearalgebra;

import java.math.BigInteger;

public final class BigIntUtils
{
    private BigIntUtils() {
    }
    
    public static boolean equals(final BigInteger[] array, final BigInteger[] array2) {
        int n = 0;
        if (array.length != array2.length) {
            return false;
        }
        for (int i = 0; i < array.length; ++i) {
            n |= array[i].compareTo(array2[i]);
        }
        return n == 0;
    }
    
    public static void fill(final BigInteger[] array, final BigInteger bigInteger) {
        for (int i = array.length - 1; i >= 0; --i) {
            array[i] = bigInteger;
        }
    }
    
    public static BigInteger[] subArray(final BigInteger[] array, final int n, final int n2) {
        final BigInteger[] array2 = new BigInteger[n2 - n];
        System.arraycopy(array, n, array2, 0, n2 - n);
        return array2;
    }
    
    public static int[] toIntArray(final BigInteger[] array) {
        final int[] array2 = new int[array.length];
        for (int i = 0; i < array.length; ++i) {
            array2[i] = array[i].intValue();
        }
        return array2;
    }
    
    public static int[] toIntArrayModQ(final int n, final BigInteger[] array) {
        final BigInteger value = BigInteger.valueOf(n);
        final int[] array2 = new int[array.length];
        for (int i = 0; i < array.length; ++i) {
            array2[i] = array[i].mod(value).intValue();
        }
        return array2;
    }
    
    public static byte[] toMinimalByteArray(final BigInteger bigInteger) {
        final byte[] byteArray = bigInteger.toByteArray();
        if (byteArray.length == 1 || (bigInteger.bitLength() & 0x7) != 0x0) {
            return byteArray;
        }
        final byte[] array = new byte[bigInteger.bitLength() >> 3];
        System.arraycopy(byteArray, 1, array, 0, array.length);
        return array;
    }
}
