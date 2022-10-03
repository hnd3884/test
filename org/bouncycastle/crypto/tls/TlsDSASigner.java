package org.bouncycastle.crypto.tls;

import org.bouncycastle.crypto.DSA;
import org.bouncycastle.crypto.signers.DSADigestSigner;
import org.bouncycastle.crypto.digests.NullDigest;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.Signer;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;

public abstract class TlsDSASigner extends AbstractTlsSigner
{
    public byte[] generateRawSignature(final SignatureAndHashAlgorithm signatureAndHashAlgorithm, final AsymmetricKeyParameter asymmetricKeyParameter, final byte[] array) throws CryptoException {
        final Signer signer = this.makeSigner(signatureAndHashAlgorithm, true, true, new ParametersWithRandom(asymmetricKeyParameter, this.context.getSecureRandom()));
        if (signatureAndHashAlgorithm == null) {
            signer.update(array, 16, 20);
        }
        else {
            signer.update(array, 0, array.length);
        }
        return signer.generateSignature();
    }
    
    public boolean verifyRawSignature(final SignatureAndHashAlgorithm signatureAndHashAlgorithm, final byte[] array, final AsymmetricKeyParameter asymmetricKeyParameter, final byte[] array2) throws CryptoException {
        final Signer signer = this.makeSigner(signatureAndHashAlgorithm, true, false, asymmetricKeyParameter);
        if (signatureAndHashAlgorithm == null) {
            signer.update(array2, 16, 20);
        }
        else {
            signer.update(array2, 0, array2.length);
        }
        return signer.verifySignature(array);
    }
    
    public Signer createSigner(final SignatureAndHashAlgorithm signatureAndHashAlgorithm, final AsymmetricKeyParameter asymmetricKeyParameter) {
        return this.makeSigner(signatureAndHashAlgorithm, false, true, asymmetricKeyParameter);
    }
    
    public Signer createVerifyer(final SignatureAndHashAlgorithm signatureAndHashAlgorithm, final AsymmetricKeyParameter asymmetricKeyParameter) {
        return this.makeSigner(signatureAndHashAlgorithm, false, false, asymmetricKeyParameter);
    }
    
    protected CipherParameters makeInitParameters(final boolean b, final CipherParameters cipherParameters) {
        return cipherParameters;
    }
    
    protected Signer makeSigner(final SignatureAndHashAlgorithm signatureAndHashAlgorithm, final boolean b, final boolean b2, final CipherParameters cipherParameters) {
        if (signatureAndHashAlgorithm != null != TlsUtils.isTLSv12(this.context)) {
            throw new IllegalStateException();
        }
        if (signatureAndHashAlgorithm != null && signatureAndHashAlgorithm.getSignature() != this.getSignatureAlgorithm()) {
            throw new IllegalStateException();
        }
        final short n = (short)((signatureAndHashAlgorithm == null) ? 2 : signatureAndHashAlgorithm.getHash());
        final DSADigestSigner dsaDigestSigner = new DSADigestSigner(this.createDSAImpl(n), b ? new NullDigest() : TlsUtils.createHash(n));
        dsaDigestSigner.init(b2, this.makeInitParameters(b2, cipherParameters));
        return dsaDigestSigner;
    }
    
    protected abstract short getSignatureAlgorithm();
    
    protected abstract DSA createDSAImpl(final short p0);
}
