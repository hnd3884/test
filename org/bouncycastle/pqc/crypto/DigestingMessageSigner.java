package org.bouncycastle.pqc.crypto;

import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.Signer;

public class DigestingMessageSigner implements Signer
{
    private final Digest messDigest;
    private final MessageSigner messSigner;
    private boolean forSigning;
    
    public DigestingMessageSigner(final MessageSigner messSigner, final Digest messDigest) {
        this.messSigner = messSigner;
        this.messDigest = messDigest;
    }
    
    public void init(final boolean forSigning, final CipherParameters cipherParameters) {
        this.forSigning = forSigning;
        AsymmetricKeyParameter asymmetricKeyParameter;
        if (cipherParameters instanceof ParametersWithRandom) {
            asymmetricKeyParameter = (AsymmetricKeyParameter)((ParametersWithRandom)cipherParameters).getParameters();
        }
        else {
            asymmetricKeyParameter = (AsymmetricKeyParameter)cipherParameters;
        }
        if (forSigning && !asymmetricKeyParameter.isPrivate()) {
            throw new IllegalArgumentException("Signing Requires Private Key.");
        }
        if (!forSigning && asymmetricKeyParameter.isPrivate()) {
            throw new IllegalArgumentException("Verification Requires Public Key.");
        }
        this.reset();
        this.messSigner.init(forSigning, cipherParameters);
    }
    
    public byte[] generateSignature() {
        if (!this.forSigning) {
            throw new IllegalStateException("DigestingMessageSigner not initialised for signature generation.");
        }
        final byte[] array = new byte[this.messDigest.getDigestSize()];
        this.messDigest.doFinal(array, 0);
        return this.messSigner.generateSignature(array);
    }
    
    public void update(final byte b) {
        this.messDigest.update(b);
    }
    
    public void update(final byte[] array, final int n, final int n2) {
        this.messDigest.update(array, n, n2);
    }
    
    public void reset() {
        this.messDigest.reset();
    }
    
    public boolean verifySignature(final byte[] array) {
        if (this.forSigning) {
            throw new IllegalStateException("DigestingMessageSigner not initialised for verification");
        }
        final byte[] array2 = new byte[this.messDigest.getDigestSize()];
        this.messDigest.doFinal(array2, 0);
        return this.messSigner.verifySignature(array2, array);
    }
}
