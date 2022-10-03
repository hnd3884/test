package org.bouncycastle.pqc.crypto.newhope;

import org.bouncycastle.crypto.digests.SHAKEDigest;
import org.bouncycastle.util.Pack;

class Poly
{
    static void add(final short[] array, final short[] array2, final short[] array3) {
        for (int i = 0; i < 1024; ++i) {
            array3[i] = Reduce.barrett((short)(array[i] + array2[i]));
        }
    }
    
    static void fromBytes(final short[] array, final byte[] array2) {
        for (int i = 0; i < 256; ++i) {
            final int n = 7 * i;
            final int n2 = array2[n + 0] & 0xFF;
            final int n3 = array2[n + 1] & 0xFF;
            final int n4 = array2[n + 2] & 0xFF;
            final int n5 = array2[n + 3] & 0xFF;
            final int n6 = array2[n + 4] & 0xFF;
            final int n7 = array2[n + 5] & 0xFF;
            final int n8 = array2[n + 6] & 0xFF;
            final int n9 = 4 * i;
            array[n9 + 0] = (short)(n2 | (n3 & 0x3F) << 8);
            array[n9 + 1] = (short)(n3 >>> 6 | n4 << 2 | (n5 & 0xF) << 10);
            array[n9 + 2] = (short)(n5 >>> 4 | n6 << 4 | (n7 & 0x3) << 12);
            array[n9 + 3] = (short)(n7 >>> 2 | n8 << 6);
        }
    }
    
    static void fromNTT(final short[] array) {
        NTT.bitReverse(array);
        NTT.core(array, Precomp.OMEGAS_INV_MONTGOMERY);
        NTT.mulCoefficients(array, Precomp.PSIS_INV_MONTGOMERY);
    }
    
    static void getNoise(final short[] array, final byte[] array2, final byte b) {
        final byte[] array3 = new byte[8];
        array3[0] = b;
        final byte[] array4 = new byte[4096];
        ChaCha20.process(array2, array3, array4, 0, array4.length);
        for (int i = 0; i < 1024; ++i) {
            final int bigEndianToInt = Pack.bigEndianToInt(array4, i * 4);
            int n = 0;
            for (int j = 0; j < 8; ++j) {
                n += (bigEndianToInt >> j & 0x1010101);
            }
            array[i] = (short)(((n >>> 24) + (n >>> 0) & 0xFF) + 12289 - ((n >>> 16) + (n >>> 8) & 0xFF));
        }
    }
    
    static void pointWise(final short[] array, final short[] array2, final short[] array3) {
        for (int i = 0; i < 1024; ++i) {
            array3[i] = Reduce.montgomery((array[i] & 0xFFFF) * (Reduce.montgomery(3186 * (array2[i] & 0xFFFF)) & 0xFFFF));
        }
    }
    
    static void toBytes(final byte[] array, final short[] array2) {
        for (int i = 0; i < 256; ++i) {
            final int n = 4 * i;
            final short normalize = normalize(array2[n + 0]);
            final short normalize2 = normalize(array2[n + 1]);
            final short normalize3 = normalize(array2[n + 2]);
            final short normalize4 = normalize(array2[n + 3]);
            final int n2 = 7 * i;
            array[n2 + 0] = (byte)normalize;
            array[n2 + 1] = (byte)(normalize >> 8 | normalize2 << 6);
            array[n2 + 2] = (byte)(normalize2 >> 2);
            array[n2 + 3] = (byte)(normalize2 >> 10 | normalize3 << 4);
            array[n2 + 4] = (byte)(normalize3 >> 4);
            array[n2 + 5] = (byte)(normalize3 >> 12 | normalize4 << 2);
            array[n2 + 6] = (byte)(normalize4 >> 6);
        }
    }
    
    static void toNTT(final short[] array) {
        NTT.mulCoefficients(array, Precomp.PSIS_BITREV_MONTGOMERY);
        NTT.core(array, Precomp.OMEGAS_MONTGOMERY);
    }
    
    static void uniform(final short[] array, final byte[] array2) {
        final SHAKEDigest shakeDigest = new SHAKEDigest(128);
        shakeDigest.update(array2, 0, array2.length);
        int n = 0;
    Block_3:
        while (true) {
            final byte[] array3 = new byte[256];
            shakeDigest.doOutput(array3, 0, array3.length);
            for (int i = 0; i < array3.length; i += 2) {
                final int n2 = (array3[i] & 0xFF) | (array3[i + 1] & 0xFF) << 8;
                if (n2 < 61445) {
                    array[n++] = (short)n2;
                    if (n == 1024) {
                        break Block_3;
                    }
                }
            }
        }
    }
    
    private static short normalize(final short n) {
        final short barrett = Reduce.barrett(n);
        final int n2 = barrett - 12289;
        return (short)(n2 ^ ((barrett ^ n2) & n2 >> 31));
    }
}
