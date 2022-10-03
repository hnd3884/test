package org.bouncycastle.crypto.modes;

import org.bouncycastle.util.Arrays;

class GOST3413CipherUtil
{
    public static byte[] MSB(final byte[] array, final int n) {
        return Arrays.copyOf(array, n);
    }
    
    public static byte[] LSB(final byte[] array, final int n) {
        final byte[] array2 = new byte[n];
        System.arraycopy(array, array.length - n, array2, 0, n);
        return array2;
    }
    
    public static byte[] sum(final byte[] array, final byte[] array2) {
        final byte[] array3 = new byte[array.length];
        for (int i = 0; i < array.length; ++i) {
            array3[i] = (byte)(array[i] ^ array2[i]);
        }
        return array3;
    }
    
    public static byte[] copyFromInput(final byte[] array, int n, final int n2) {
        if (array.length < n + n2) {
            n = array.length - n2;
        }
        final byte[] array2 = new byte[n];
        System.arraycopy(array, n2, array2, 0, n);
        return array2;
    }
}
