package org.bouncycastle.crypto.signers;

import org.bouncycastle.util.Arrays;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.AsymmetricBlockCipher;
import org.bouncycastle.crypto.Signer;

public class GenericSigner implements Signer
{
    private final AsymmetricBlockCipher engine;
    private final Digest digest;
    private boolean forSigning;
    
    public GenericSigner(final AsymmetricBlockCipher engine, final Digest digest) {
        this.engine = engine;
        this.digest = digest;
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
            throw new IllegalArgumentException("signing requires private key");
        }
        if (!forSigning && asymmetricKeyParameter.isPrivate()) {
            throw new IllegalArgumentException("verification requires public key");
        }
        this.reset();
        this.engine.init(forSigning, cipherParameters);
    }
    
    public void update(final byte b) {
        this.digest.update(b);
    }
    
    public void update(final byte[] array, final int n, final int n2) {
        this.digest.update(array, n, n2);
    }
    
    public byte[] generateSignature() throws CryptoException, DataLengthException {
        if (!this.forSigning) {
            throw new IllegalStateException("GenericSigner not initialised for signature generation.");
        }
        final byte[] array = new byte[this.digest.getDigestSize()];
        this.digest.doFinal(array, 0);
        return this.engine.processBlock(array, 0, array.length);
    }
    
    public boolean verifySignature(final byte[] array) {
        if (this.forSigning) {
            throw new IllegalStateException("GenericSigner not initialised for verification");
        }
        final byte[] array2 = new byte[this.digest.getDigestSize()];
        this.digest.doFinal(array2, 0);
        try {
            byte[] processBlock = this.engine.processBlock(array, 0, array.length);
            if (processBlock.length < array2.length) {
                final byte[] array3 = new byte[array2.length];
                System.arraycopy(processBlock, 0, array3, array3.length - processBlock.length, processBlock.length);
                processBlock = array3;
            }
            return Arrays.constantTimeAreEqual(processBlock, array2);
        }
        catch (final Exception ex) {
            return false;
        }
    }
    
    public void reset() {
        this.digest.reset();
    }
}
