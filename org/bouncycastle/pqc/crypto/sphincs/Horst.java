package org.bouncycastle.pqc.crypto.sphincs;

class Horst
{
    static final int HORST_LOGT = 16;
    static final int HORST_T = 65536;
    static final int HORST_K = 32;
    static final int HORST_SKBYTES = 32;
    static final int HORST_SIGBYTES = 13312;
    static final int N_MASKS = 32;
    
    static void expand_seed(final byte[] array, final byte[] array2) {
        Seed.prg(array, 0, 2097152L, array2, 0);
    }
    
    static int horst_sign(final HashFunctions hashFunctions, final byte[] array, final int n, final byte[] array2, final byte[] array3, final byte[] array4, final byte[] array5) {
        final byte[] array6 = new byte[2097152];
        int n2 = n;
        final byte[] array7 = new byte[4194272];
        expand_seed(array6, array3);
        for (int i = 0; i < 65536; ++i) {
            hashFunctions.hash_n_n(array7, (65535 + i) * 32, array6, i * 32);
        }
        for (int j = 0; j < 16; ++j) {
            final long n3 = (1 << 16 - j) - 1;
            final long n4 = (1 << 16 - j - 1) - 1;
            for (int k = 0; k < 1 << 16 - j - 1; ++k) {
                hashFunctions.hash_2n_n_mask(array7, (int)((n4 + k) * 32L), array7, (int)((n3 + 2 * k) * 32L), array4, 2 * j * 32);
            }
        }
        for (int l = 2016; l < 4064; ++l) {
            array[n2++] = array7[l];
        }
        for (int n5 = 0; n5 < 32; ++n5) {
            final int n6 = (array5[2 * n5] & 0xFF) + ((array5[2 * n5 + 1] & 0xFF) << 8);
            for (int n7 = 0; n7 < 32; ++n7) {
                array[n2++] = array6[n6 * 32 + n7];
            }
            int n8 = n6 + 65535;
            for (int n9 = 0; n9 < 10; ++n9) {
                final int n10 = ((n8 & 0x1) != 0x0) ? (n8 + 1) : (n8 - 1);
                for (int n11 = 0; n11 < 32; ++n11) {
                    array[n2++] = array7[n10 * 32 + n11];
                }
                n8 = (n10 - 1) / 2;
            }
        }
        for (int n12 = 0; n12 < 32; ++n12) {
            array2[n12] = array7[n12];
        }
        return 13312;
    }
    
    static int horst_verify(final HashFunctions hashFunctions, final byte[] array, final byte[] array2, final int n, final byte[] array3, final byte[] array4) {
        final byte[] array5 = new byte[1024];
        int n2 = n + 2048;
        for (int i = 0; i < 32; ++i) {
            int n3 = (array4[2 * i] & 0xFF) + ((array4[2 * i + 1] & 0xFF) << 8);
            if ((n3 & 0x1) == 0x0) {
                hashFunctions.hash_n_n(array5, 0, array2, n2);
                for (int j = 0; j < 32; ++j) {
                    array5[32 + j] = array2[n2 + 32 + j];
                }
            }
            else {
                hashFunctions.hash_n_n(array5, 32, array2, n2);
                for (int k = 0; k < 32; ++k) {
                    array5[k] = array2[n2 + 32 + k];
                }
            }
            n2 += 64;
            for (int l = 1; l < 10; ++l) {
                n3 >>>= 1;
                if ((n3 & 0x1) == 0x0) {
                    hashFunctions.hash_2n_n_mask(array5, 0, array5, 0, array3, 2 * (l - 1) * 32);
                    for (int n4 = 0; n4 < 32; ++n4) {
                        array5[32 + n4] = array2[n2 + n4];
                    }
                }
                else {
                    hashFunctions.hash_2n_n_mask(array5, 32, array5, 0, array3, 2 * (l - 1) * 32);
                    for (int n5 = 0; n5 < 32; ++n5) {
                        array5[n5] = array2[n2 + n5];
                    }
                }
                n2 += 32;
            }
            final int n6 = n3 >>> 1;
            hashFunctions.hash_2n_n_mask(array5, 0, array5, 0, array3, 576);
            for (int n7 = 0; n7 < 32; ++n7) {
                if (array2[n + n6 * 32 + n7] != array5[n7]) {
                    for (int n8 = 0; n8 < 32; ++n8) {
                        array[n8] = 0;
                    }
                    return -1;
                }
            }
        }
        for (int n9 = 0; n9 < 32; ++n9) {
            hashFunctions.hash_2n_n_mask(array5, n9 * 32, array2, n + 2 * n9 * 32, array3, 640);
        }
        for (int n10 = 0; n10 < 16; ++n10) {
            hashFunctions.hash_2n_n_mask(array5, n10 * 32, array5, 2 * n10 * 32, array3, 704);
        }
        for (int n11 = 0; n11 < 8; ++n11) {
            hashFunctions.hash_2n_n_mask(array5, n11 * 32, array5, 2 * n11 * 32, array3, 768);
        }
        for (int n12 = 0; n12 < 4; ++n12) {
            hashFunctions.hash_2n_n_mask(array5, n12 * 32, array5, 2 * n12 * 32, array3, 832);
        }
        for (int n13 = 0; n13 < 2; ++n13) {
            hashFunctions.hash_2n_n_mask(array5, n13 * 32, array5, 2 * n13 * 32, array3, 896);
        }
        hashFunctions.hash_2n_n_mask(array, 0, array5, 0, array3, 960);
        return 0;
    }
}
