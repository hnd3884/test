package org.bouncycastle.crypto.tls;

import org.bouncycastle.crypto.encodings.PKCS1Encoding;
import org.bouncycastle.crypto.engines.RSABlindedEngine;
import org.bouncycastle.crypto.AsymmetricBlockCipher;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.signers.GenericSigner;
import org.bouncycastle.crypto.signers.RSADigestSigner;
import org.bouncycastle.crypto.digests.NullDigest;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.Signer;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;

public class TlsRSASigner extends AbstractTlsSigner
{
    public byte[] generateRawSignature(final SignatureAndHashAlgorithm signatureAndHashAlgorithm, final AsymmetricKeyParameter asymmetricKeyParameter, final byte[] array) throws CryptoException {
        final Signer signer = this.makeSigner(signatureAndHashAlgorithm, true, true, new ParametersWithRandom(asymmetricKeyParameter, this.context.getSecureRandom()));
        signer.update(array, 0, array.length);
        return signer.generateSignature();
    }
    
    public boolean verifyRawSignature(final SignatureAndHashAlgorithm signatureAndHashAlgorithm, final byte[] array, final AsymmetricKeyParameter asymmetricKeyParameter, final byte[] array2) throws CryptoException {
        final Signer signer = this.makeSigner(signatureAndHashAlgorithm, true, false, asymmetricKeyParameter);
        signer.update(array2, 0, array2.length);
        return signer.verifySignature(array);
    }
    
    public Signer createSigner(final SignatureAndHashAlgorithm signatureAndHashAlgorithm, final AsymmetricKeyParameter asymmetricKeyParameter) {
        return this.makeSigner(signatureAndHashAlgorithm, false, true, new ParametersWithRandom(asymmetricKeyParameter, this.context.getSecureRandom()));
    }
    
    public Signer createVerifyer(final SignatureAndHashAlgorithm signatureAndHashAlgorithm, final AsymmetricKeyParameter asymmetricKeyParameter) {
        return this.makeSigner(signatureAndHashAlgorithm, false, false, asymmetricKeyParameter);
    }
    
    public boolean isValidPublicKey(final AsymmetricKeyParameter asymmetricKeyParameter) {
        return asymmetricKeyParameter instanceof RSAKeyParameters && !asymmetricKeyParameter.isPrivate();
    }
    
    protected Signer makeSigner(final SignatureAndHashAlgorithm signatureAndHashAlgorithm, final boolean b, final boolean b2, final CipherParameters cipherParameters) {
        if (signatureAndHashAlgorithm != null != TlsUtils.isTLSv12(this.context)) {
            throw new IllegalStateException();
        }
        if (signatureAndHashAlgorithm != null && signatureAndHashAlgorithm.getSignature() != 1) {
            throw new IllegalStateException();
        }
        Digest hash;
        if (b) {
            hash = new NullDigest();
        }
        else if (signatureAndHashAlgorithm == null) {
            hash = new CombinedHash();
        }
        else {
            hash = TlsUtils.createHash(signatureAndHashAlgorithm.getHash());
        }
        Signer signer;
        if (signatureAndHashAlgorithm != null) {
            signer = new RSADigestSigner(hash, TlsUtils.getOIDForHashAlgorithm(signatureAndHashAlgorithm.getHash()));
        }
        else {
            signer = new GenericSigner(this.createRSAImpl(), hash);
        }
        signer.init(b2, cipherParameters);
        return signer;
    }
    
    protected AsymmetricBlockCipher createRSAImpl() {
        return new PKCS1Encoding(new RSABlindedEngine());
    }
}
