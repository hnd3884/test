package org.bouncycastle.pqc.jcajce.provider.rainbow;

import org.bouncycastle.crypto.digests.SHA512Digest;
import org.bouncycastle.crypto.digests.SHA384Digest;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.digests.SHA224Digest;
import java.security.spec.AlgorithmParameterSpec;
import java.security.SignatureException;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import java.security.PrivateKey;
import java.security.InvalidKeyException;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.CipherParameters;
import java.security.PublicKey;
import java.security.SecureRandom;
import org.bouncycastle.pqc.crypto.rainbow.RainbowSigner;
import org.bouncycastle.crypto.Digest;

public class SignatureSpi extends java.security.SignatureSpi
{
    private Digest digest;
    private RainbowSigner signer;
    private SecureRandom random;
    
    protected SignatureSpi(final Digest digest, final RainbowSigner signer) {
        this.digest = digest;
        this.signer = signer;
    }
    
    @Override
    protected void engineInitVerify(final PublicKey publicKey) throws InvalidKeyException {
        final AsymmetricKeyParameter generatePublicKeyParameter = RainbowKeysToParams.generatePublicKeyParameter(publicKey);
        this.digest.reset();
        this.signer.init(false, generatePublicKeyParameter);
    }
    
    @Override
    protected void engineInitSign(final PrivateKey privateKey, final SecureRandom random) throws InvalidKeyException {
        this.random = random;
        this.engineInitSign(privateKey);
    }
    
    @Override
    protected void engineInitSign(final PrivateKey privateKey) throws InvalidKeyException {
        CipherParameters generatePrivateKeyParameter = RainbowKeysToParams.generatePrivateKeyParameter(privateKey);
        if (this.random != null) {
            generatePrivateKeyParameter = new ParametersWithRandom(generatePrivateKeyParameter, this.random);
        }
        this.digest.reset();
        this.signer.init(true, generatePrivateKeyParameter);
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
    
    public static class withSha224 extends SignatureSpi
    {
        public withSha224() {
            super(new SHA224Digest(), new RainbowSigner());
        }
    }
    
    public static class withSha256 extends SignatureSpi
    {
        public withSha256() {
            super(new SHA256Digest(), new RainbowSigner());
        }
    }
    
    public static class withSha384 extends SignatureSpi
    {
        public withSha384() {
            super(new SHA384Digest(), new RainbowSigner());
        }
    }
    
    public static class withSha512 extends SignatureSpi
    {
        public withSha512() {
            super(new SHA512Digest(), new RainbowSigner());
        }
    }
}
