package org.bouncycastle.crypto.params;

import org.bouncycastle.crypto.DerivationParameters;

public class KDFParameters implements DerivationParameters
{
    byte[] iv;
    byte[] shared;
    
    public KDFParameters(final byte[] shared, final byte[] iv) {
        this.shared = shared;
        this.iv = iv;
    }
    
    public byte[] getSharedSecret() {
        return this.shared;
    }
    
    public byte[] getIV() {
        return this.iv;
    }
}
