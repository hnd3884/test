package org.bouncycastle.crypto.ec;

import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;

public class ECElGamalDecryptor implements ECDecryptor
{
    private ECPrivateKeyParameters key;
    
    public void init(final CipherParameters cipherParameters) {
        if (!(cipherParameters instanceof ECPrivateKeyParameters)) {
            throw new IllegalArgumentException("ECPrivateKeyParameters are required for decryption.");
        }
        this.key = (ECPrivateKeyParameters)cipherParameters;
    }
    
    public ECPoint decrypt(final ECPair ecPair) {
        if (this.key == null) {
            throw new IllegalStateException("ECElGamalDecryptor not initialised");
        }
        return ecPair.getY().subtract(ecPair.getX().multiply(this.key.getD())).normalize();
    }
}
