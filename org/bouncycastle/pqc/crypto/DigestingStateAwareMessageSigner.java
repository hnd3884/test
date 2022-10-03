package org.bouncycastle.pqc.crypto;

import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.Digest;

public class DigestingStateAwareMessageSigner extends DigestingMessageSigner
{
    private final StateAwareMessageSigner signer;
    
    public DigestingStateAwareMessageSigner(final StateAwareMessageSigner signer, final Digest digest) {
        super(signer, digest);
        this.signer = signer;
    }
    
    public AsymmetricKeyParameter getUpdatedPrivateKey() {
        return this.signer.getUpdatedPrivateKey();
    }
}
