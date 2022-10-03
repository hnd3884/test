package org.bouncycastle.pqc.crypto.newhope;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.engines.ChaChaEngine;

class ChaCha20
{
    static void process(final byte[] array, final byte[] array2, final byte[] array3, final int n, final int n2) {
        final ChaChaEngine chaChaEngine = new ChaChaEngine(20);
        chaChaEngine.init(true, new ParametersWithIV(new KeyParameter(array), array2));
        chaChaEngine.processBytes(array3, n, n2, array3, n);
    }
}
