package org.bouncycastle.crypto.macs;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.BlockCipher;

public class CMacWithIV extends CMac
{
    public CMacWithIV(final BlockCipher blockCipher) {
        super(blockCipher);
    }
    
    public CMacWithIV(final BlockCipher blockCipher, final int n) {
        super(blockCipher, n);
    }
    
    @Override
    void validate(final CipherParameters cipherParameters) {
    }
}
