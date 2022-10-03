package org.bouncycastle.pqc.crypto.sphincs;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.engines.ChaChaEngine;
import org.bouncycastle.util.Pack;

class Seed
{
    static void get_seed(final HashFunctions hashFunctions, final byte[] array, final int n, final byte[] array2, final Tree.leafaddr leafaddr) {
        final byte[] array3 = new byte[40];
        for (int i = 0; i < 32; ++i) {
            array3[i] = array2[i];
        }
        Pack.longToLittleEndian((long)leafaddr.level | leafaddr.subtree << 4 | leafaddr.subleaf << 59, array3, 32);
        hashFunctions.varlen_hash(array, n, array3, array3.length);
    }
    
    static void prg(final byte[] array, final int n, final long n2, final byte[] array2, final int n3) {
        final byte[] array3 = new byte[8];
        final ChaChaEngine chaChaEngine = new ChaChaEngine(12);
        chaChaEngine.init(true, new ParametersWithIV(new KeyParameter(array2, n3, 32), array3));
        chaChaEngine.processBytes(array, n, (int)n2, array, n);
    }
}
