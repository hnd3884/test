package org.bouncycastle.pqc.crypto;

import org.bouncycastle.util.Arrays;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;

public class ExchangePair
{
    private final AsymmetricKeyParameter publicKey;
    private final byte[] shared;
    
    public ExchangePair(final AsymmetricKeyParameter publicKey, final byte[] array) {
        this.publicKey = publicKey;
        this.shared = Arrays.clone(array);
    }
    
    public AsymmetricKeyParameter getPublicKey() {
        return this.publicKey;
    }
    
    public byte[] getSharedValue() {
        return Arrays.clone(this.shared);
    }
}
