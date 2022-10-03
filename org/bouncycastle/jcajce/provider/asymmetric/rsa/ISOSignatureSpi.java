package org.bouncycastle.jcajce.provider.asymmetric.rsa;

import org.bouncycastle.crypto.digests.WhirlpoolDigest;
import org.bouncycastle.crypto.digests.RIPEMD160Digest;
import org.bouncycastle.crypto.engines.RSABlindedEngine;
import org.bouncycastle.crypto.util.DigestFactory;
import java.security.spec.AlgorithmParameterSpec;
import java.security.SignatureException;
import java.security.interfaces.RSAPrivateKey;
import java.security.PrivateKey;
import java.security.InvalidKeyException;
import org.bouncycastle.crypto.CipherParameters;
import java.security.interfaces.RSAPublicKey;
import java.security.PublicKey;
import org.bouncycastle.crypto.AsymmetricBlockCipher;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.signers.ISO9796d2Signer;
import java.security.SignatureSpi;

public class ISOSignatureSpi extends SignatureSpi
{
    private ISO9796d2Signer signer;
    
    protected ISOSignatureSpi(final Digest digest, final AsymmetricBlockCipher asymmetricBlockCipher) {
        this.signer = new ISO9796d2Signer(asymmetricBlockCipher, digest, true);
    }
    
    @Override
    protected void engineInitVerify(final PublicKey publicKey) throws InvalidKeyException {
        this.signer.init(false, RSAUtil.generatePublicKeyParameter((RSAPublicKey)publicKey));
    }
    
    @Override
    protected void engineInitSign(final PrivateKey privateKey) throws InvalidKeyException {
        this.signer.init(true, RSAUtil.generatePrivateKeyParameter((RSAPrivateKey)privateKey));
    }
    
    @Override
    protected void engineUpdate(final byte b) throws SignatureException {
        this.signer.update(b);
    }
    
    @Override
    protected void engineUpdate(final byte[] array, final int n, final int n2) throws SignatureException {
        this.signer.update(array, n, n2);
    }
    
    @Override
    protected byte[] engineSign() throws SignatureException {
        try {
            return this.signer.generateSignature();
        }
        catch (final Exception ex) {
            throw new SignatureException(ex.toString());
        }
    }
    
    @Override
    protected boolean engineVerify(final byte[] array) throws SignatureException {
        return this.signer.verifySignature(array);
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
    
    public static class MD5WithRSAEncryption extends ISOSignatureSpi
    {
        public MD5WithRSAEncryption() {
            super(DigestFactory.createMD5(), new RSABlindedEngine());
        }
    }
    
    public static class RIPEMD160WithRSAEncryption extends ISOSignatureSpi
    {
        public RIPEMD160WithRSAEncryption() {
            super(new RIPEMD160Digest(), new RSABlindedEngine());
        }
    }
    
    public static class SHA1WithRSAEncryption extends ISOSignatureSpi
    {
        public SHA1WithRSAEncryption() {
            super(DigestFactory.createSHA1(), new RSABlindedEngine());
        }
    }
    
    public static class SHA224WithRSAEncryption extends ISOSignatureSpi
    {
        public SHA224WithRSAEncryption() {
            super(DigestFactory.createSHA224(), new RSABlindedEngine());
        }
    }
    
    public static class SHA256WithRSAEncryption extends ISOSignatureSpi
    {
        public SHA256WithRSAEncryption() {
            super(DigestFactory.createSHA256(), new RSABlindedEngine());
        }
    }
    
    public static class SHA384WithRSAEncryption extends ISOSignatureSpi
    {
        public SHA384WithRSAEncryption() {
            super(DigestFactory.createSHA384(), new RSABlindedEngine());
        }
    }
    
    public static class SHA512WithRSAEncryption extends ISOSignatureSpi
    {
        public SHA512WithRSAEncryption() {
            super(DigestFactory.createSHA512(), new RSABlindedEngine());
        }
    }
    
    public static class SHA512_224WithRSAEncryption extends ISOSignatureSpi
    {
        public SHA512_224WithRSAEncryption() {
            super(DigestFactory.createSHA512_224(), new RSABlindedEngine());
        }
    }
    
    public static class SHA512_256WithRSAEncryption extends ISOSignatureSpi
    {
        public SHA512_256WithRSAEncryption() {
            super(DigestFactory.createSHA512_256(), new RSABlindedEngine());
        }
    }
    
    public static class WhirlpoolWithRSAEncryption extends ISOSignatureSpi
    {
        public WhirlpoolWithRSAEncryption() {
            super(new WhirlpoolDigest(), new RSABlindedEngine());
        }
    }
}
