package org.bouncycastle.pqc.crypto.newhope;

import org.bouncycastle.util.Arrays;

class ErrorCorrection
{
    static int abs(final int n) {
        final int n2 = n >> 31;
        return (n ^ n2) - n2;
    }
    
    static int f(final int[] array, final int n, final int n2, final int n3) {
        final int n4 = n3 * 2730 >> 25;
        int n5 = n4 - (12288 - (n3 - n4 * 12289) >> 31);
        array[n] = (n5 >> 1) + (n5 & 0x1);
        array[n2] = (n5 >> 1) + (--n5 & 0x1);
        return abs(n3 - array[n] * 2 * 12289);
    }
    
    static int g(final int n) {
        final int n2 = n * 2730 >> 27;
        final int n3 = n2 - (49155 - (n - n2 * 49156) >> 31);
        return abs(((n3 >> 1) + (n3 & 0x1)) * 98312 - n);
    }
    
    static void helpRec(final short[] array, final short[] array2, final byte[] array3, final byte b) {
        final byte[] array4 = new byte[8];
        array4[0] = b;
        final byte[] array5 = new byte[32];
        ChaCha20.process(array3, array4, array5, 0, array5.length);
        final int[] array6 = new int[8];
        final int[] array7 = new int[4];
        for (int i = 0; i < 256; ++i) {
            final int n = array5[i >>> 3] >>> (i & 0x7) & 0x1;
            final int n2 = 24577 - (f(array6, 0, 4, 8 * array2[0 + i] + 4 * n) + f(array6, 1, 5, 8 * array2[256 + i] + 4 * n) + f(array6, 2, 6, 8 * array2[512 + i] + 4 * n) + f(array6, 3, 7, 8 * array2[768 + i] + 4 * n)) >> 31;
            array7[0] = ((~n2 & array6[0]) ^ (n2 & array6[4]));
            array7[1] = ((~n2 & array6[1]) ^ (n2 & array6[5]));
            array7[2] = ((~n2 & array6[2]) ^ (n2 & array6[6]));
            array7[3] = ((~n2 & array6[3]) ^ (n2 & array6[7]));
            array[0 + i] = (short)(array7[0] - array7[3] & 0x3);
            array[256 + i] = (short)(array7[1] - array7[3] & 0x3);
            array[512 + i] = (short)(array7[2] - array7[3] & 0x3);
            array[768 + i] = (short)(-n2 + 2 * array7[3] & 0x3);
        }
    }
    
    static short LDDecode(final int n, final int n2, final int n3, final int n4) {
        return (short)(g(n) + g(n2) + g(n3) + g(n4) - 98312 >>> 31);
    }
    
    static void rec(final byte[] array, final short[] array2, final short[] array3) {
        Arrays.fill(array, (byte)0);
        final int[] array4 = new int[4];
        for (int i = 0; i < 256; ++i) {
            array4[0] = 196624 + 8 * array2[0 + i] - 12289 * (2 * array3[0 + i] + array3[768 + i]);
            array4[1] = 196624 + 8 * array2[256 + i] - 12289 * (2 * array3[256 + i] + array3[768 + i]);
            array4[2] = 196624 + 8 * array2[512 + i] - 12289 * (2 * array3[512 + i] + array3[768 + i]);
            array4[3] = 196624 + 8 * array2[768 + i] - 12289 * array3[768 + i];
            final int n = i >>> 3;
            array[n] |= (byte)(LDDecode(array4[0], array4[1], array4[2], array4[3]) << (i & 0x7));
        }
    }
}
