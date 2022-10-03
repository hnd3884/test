package org.bouncycastle.pqc.math.linearalgebra;

public final class CharUtils
{
    private CharUtils() {
    }
    
    public static char[] clone(final char[] array) {
        final char[] array2 = new char[array.length];
        System.arraycopy(array, 0, array2, 0, array.length);
        return array2;
    }
    
    public static byte[] toByteArray(final char[] array) {
        final byte[] array2 = new byte[array.length];
        for (int i = array.length - 1; i >= 0; --i) {
            array2[i] = (byte)array[i];
        }
        return array2;
    }
    
    public static byte[] toByteArrayForPBE(final char[] array) {
        final byte[] array2 = new byte[array.length];
        for (int i = 0; i < array.length; ++i) {
            array2[i] = (byte)array[i];
        }
        final int n = array2.length * 2;
        final byte[] array3 = new byte[n + 2];
        for (int j = 0; j < array2.length; ++j) {
            final int n2 = j * 2;
            array3[n2] = 0;
            array3[n2 + 1] = array2[j];
        }
        array3[n + 1] = (array3[n] = 0);
        return array3;
    }
    
    public static boolean equals(final char[] array, final char[] array2) {
        if (array.length != array2.length) {
            return false;
        }
        boolean b = true;
        for (int i = array.length - 1; i >= 0; --i) {
            b &= (array[i] == array2[i]);
        }
        return b;
    }
}
