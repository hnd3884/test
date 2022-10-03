package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.AsymmetricBlockCipher;

public class RSAEngine implements AsymmetricBlockCipher
{
    private RSACoreEngine core;
    
    public void init(final boolean b, final CipherParameters cipherParameters) {
        if (this.core == null) {
            this.core = new RSACoreEngine();
        }
        this.core.init(b, cipherParameters);
    }
    
    public int getInputBlockSize() {
        return this.core.getInputBlockSize();
    }
    
    public int getOutputBlockSize() {
        return this.core.getOutputBlockSize();
    }
    
    public byte[] processBlock(final byte[] array, final int n, final int n2) {
        if (this.core == null) {
            throw new IllegalStateException("RSA engine not initialised");
        }
        return this.core.convertOutput(this.core.processBlock(this.core.convertInput(array, n, n2)));
    }
}
