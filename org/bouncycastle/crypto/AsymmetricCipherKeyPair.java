package org.bouncycastle.crypto;

import org.bouncycastle.crypto.params.AsymmetricKeyParameter;

public class AsymmetricCipherKeyPair
{
    private AsymmetricKeyParameter publicParam;
    private AsymmetricKeyParameter privateParam;
    
    public AsymmetricCipherKeyPair(final AsymmetricKeyParameter publicParam, final AsymmetricKeyParameter privateParam) {
        this.publicParam = publicParam;
        this.privateParam = privateParam;
    }
    
    @Deprecated
    public AsymmetricCipherKeyPair(final CipherParameters cipherParameters, final CipherParameters cipherParameters2) {
        this.publicParam = (AsymmetricKeyParameter)cipherParameters;
        this.privateParam = (AsymmetricKeyParameter)cipherParameters2;
    }
    
    public AsymmetricKeyParameter getPublic() {
        return this.publicParam;
    }
    
    public AsymmetricKeyParameter getPrivate() {
        return this.privateParam;
    }
}
