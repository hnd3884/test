package org.bouncycastle.pqc.crypto.sphincs;

class Wots
{
    static final int WOTS_LOGW = 4;
    static final int WOTS_W = 16;
    static final int WOTS_L1 = 64;
    static final int WOTS_L = 67;
    static final int WOTS_LOG_L = 7;
    static final int WOTS_SIGBYTES = 2144;
    
    static void expand_seed(final byte[] array, final int n, final byte[] array2, final int n2) {
        clear(array, n, 2144);
        Seed.prg(array, n, 2144L, array2, n2);
    }
    
    private static void clear(final byte[] array, final int n, final int n2) {
        for (int i = 0; i != n2; ++i) {
            array[i + n] = 0;
        }
    }
    
    static void gen_chain(final HashFunctions hashFunctions, final byte[] array, final int n, final byte[] array2, final int n2, final byte[] array3, final int n3, final int n4) {
        for (int i = 0; i < 32; ++i) {
            array[i + n] = array2[i + n2];
        }
        for (int n5 = 0; n5 < n4 && n5 < 16; ++n5) {
            hashFunctions.hash_n_n_mask(array, n, array, n, array3, n3 + n5 * 32);
        }
    }
    
    void wots_pkgen(final HashFunctions hashFunctions, final byte[] array, final int n, final byte[] array2, final int n2, final byte[] array3, final int n3) {
        expand_seed(array, n, array2, n2);
        for (int i = 0; i < 67; ++i) {
            gen_chain(hashFunctions, array, n + i * 32, array, n + i * 32, array3, n3, 15);
        }
    }
    
    void wots_sign(final HashFunctions hashFunctions, final byte[] array, final int n, final byte[] array2, final byte[] array3, final byte[] array4) {
        final int[] array5 = new int[67];
        int n2 = 0;
        int i;
        for (i = 0; i < 64; i += 2) {
            array5[i] = (array2[i / 2] & 0xF);
            array5[i + 1] = (array2[i / 2] & 0xFF) >>> 4;
            n2 = n2 + (15 - array5[i]) + (15 - array5[i + 1]);
        }
        while (i < 67) {
            array5[i] = (n2 & 0xF);
            n2 >>>= 4;
            ++i;
        }
        expand_seed(array, n, array3, 0);
        for (int j = 0; j < 67; ++j) {
            gen_chain(hashFunctions, array, n + j * 32, array, n + j * 32, array4, 0, array5[j]);
        }
    }
    
    void wots_verify(final HashFunctions hashFunctions, final byte[] array, final byte[] array2, final int n, final byte[] array3, final byte[] array4) {
        final int[] array5 = new int[67];
        int n2 = 0;
        int i;
        for (i = 0; i < 64; i += 2) {
            array5[i] = (array3[i / 2] & 0xF);
            array5[i + 1] = (array3[i / 2] & 0xFF) >>> 4;
            n2 = n2 + (15 - array5[i]) + (15 - array5[i + 1]);
        }
        while (i < 67) {
            array5[i] = (n2 & 0xF);
            n2 >>>= 4;
            ++i;
        }
        for (int j = 0; j < 67; ++j) {
            gen_chain(hashFunctions, array, j * 32, array2, n + j * 32, array4, array5[j] * 32, 15 - array5[j]);
        }
    }
}
