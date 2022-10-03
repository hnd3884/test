package org.bouncycastle.pqc.jcajce.provider.sphincs;

import org.bouncycastle.crypto.digests.SHA512tDigest;
import org.bouncycastle.crypto.digests.SHA512Digest;
import org.bouncycastle.crypto.digests.SHA3Digest;
import java.security.spec.AlgorithmParameterSpec;
import java.security.SignatureException;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import java.security.PrivateKey;
import org.bouncycastle.crypto.CipherParameters;
import java.security.InvalidKeyException;
import java.security.PublicKey;
import java.security.SecureRandom;
import org.bouncycastle.pqc.crypto.sphincs.SPHINCS256Signer;
import org.bouncycastle.crypto.Digest;

public class SignatureSpi extends java.security.SignatureSpi
{
    private Digest digest;
    private SPHINCS256Signer signer;
    private SecureRandom random;
    
    protected SignatureSpi(final Digest digest, final SPHINCS256Signer signer) {
        this.digest = digest;
        this.signer = signer;
    }
    
    @Override
    protected void engineInitVerify(final PublicKey publicKey) throws InvalidKeyException {
        if (publicKey instanceof BCSphincs256PublicKey) {
            final CipherParameters keyParams = ((BCSphincs256PublicKey)publicKey).getKeyParams();
            this.digest.reset();
            this.signer.init(false, keyParams);
            return;
        }
        throw new InvalidKeyException("unknown public key passed to SPHINCS-256");
    }
    
    @Override
    protected void engineInitSign(final PrivateKey privateKey, final SecureRandom random) throws InvalidKeyException {
        this.random = random;
        this.engineInitSign(privateKey);
    }
    
    @Override
    protected void engineInitSign(final PrivateKey privateKey) throws InvalidKeyException {
        if (privateKey instanceof BCSphincs256PrivateKey) {
            CipherParameters keyParams = ((BCSphincs256PrivateKey)privateKey).getKeyParams();
            if (this.random != null) {
                keyParams = new ParametersWithRandom(keyParams, this.random);
            }
            this.digest.reset();
            this.signer.init(true, keyParams);
            return;
        }
        throw new InvalidKeyException("unknown private key passed to SPHINCS-256");
    }
    
    @Override
    protected void engineUpdate(final byte b) throws SignatureException {
        this.digest.update(b);
    }
    
    @Override
    protected void engineUpdate(final byte[] array, final int n, final int n2) throws SignatureException {
        this.digest.update(array, n, n2);
    }
    
    @Override
    protected byte[] engineSign() throws SignatureException {
        final byte[] array = new byte[this.digest.getDigestSize()];
        this.digest.doFinal(array, 0);
        try {
            return this.signer.generateSignature(array);
        }
        catch (final Exception ex) {
            throw new SignatureException(ex.toString());
        }
    }
    
    @Override
    protected boolean engineVerify(final byte[] array) throws SignatureException {
        final byte[] array2 = new byte[this.digest.getDigestSize()];
        this.digest.doFinal(array2, 0);
        return this.signer.verifySignature(array2, array);
    }
    
    @Override
    protected void engineSetParameter(final AlgorithmParameterSpec algorithmParameterSpec) {
        throw new UnsupportedOperationException("engineSetParameter unsupported");
    }
    
    @Override
    @Deprecated
    protected void engineSetParameter(final String s, final Object o) {
        throw new UnsupportedOperationException("engineSetParameter unsupported");
    }
    
    @Override
    @Deprecated
    protected Object engineGetParameter(final String s) {
        throw new UnsupportedOperationException("engineSetParameter unsupported");
    }
    
    public static class withSha3_512 extends SignatureSpi
    {
        public withSha3_512() {
            super(new SHA3Digest(512), new SPHINCS256Signer(new SHA3Digest(256), new SHA3Digest(512)));
        }
    }
    
    public static class withSha512 extends SignatureSpi
    {
        public withSha512() {
            super(new SHA512Digest(), new SPHINCS256Signer(new SHA512tDigest(256), new SHA512Digest()));
        }
    }
}
