package org.bouncycastle.pqc.jcajce.provider.xmss;

import org.bouncycastle.crypto.digests.SHAKEDigest;
import org.bouncycastle.crypto.digests.SHA512Digest;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.pqc.crypto.xmss.XMSSMTPrivateKeyParameters;
import java.security.spec.AlgorithmParameterSpec;
import java.security.SignatureException;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import java.security.PrivateKey;
import org.bouncycastle.crypto.CipherParameters;
import java.security.InvalidKeyException;
import java.security.PublicKey;
import java.security.SecureRandom;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.pqc.crypto.xmss.XMSSMTSigner;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.pqc.jcajce.interfaces.StateAwareSignature;
import java.security.Signature;

public class XMSSMTSignatureSpi extends Signature implements StateAwareSignature
{
    private Digest digest;
    private XMSSMTSigner signer;
    private ASN1ObjectIdentifier treeDigest;
    private SecureRandom random;
    
    protected XMSSMTSignatureSpi(final String s) {
        super(s);
    }
    
    protected XMSSMTSignatureSpi(final String s, final Digest digest, final XMSSMTSigner signer) {
        super(s);
        this.digest = digest;
        this.signer = signer;
    }
    
    @Override
    protected void engineInitVerify(final PublicKey publicKey) throws InvalidKeyException {
        if (publicKey instanceof BCXMSSMTPublicKey) {
            final CipherParameters keyParams = ((BCXMSSMTPublicKey)publicKey).getKeyParams();
            this.treeDigest = null;
            this.digest.reset();
            this.signer.init(false, keyParams);
            return;
        }
        throw new InvalidKeyException("unknown public key passed to XMSSMT");
    }
    
    @Override
    protected void engineInitSign(final PrivateKey privateKey, final SecureRandom random) throws InvalidKeyException {
        this.random = random;
        this.engineInitSign(privateKey);
    }
    
    @Override
    protected void engineInitSign(final PrivateKey privateKey) throws InvalidKeyException {
        if (privateKey instanceof BCXMSSMTPrivateKey) {
            CipherParameters keyParams = ((BCXMSSMTPrivateKey)privateKey).getKeyParams();
            this.treeDigest = ((BCXMSSMTPrivateKey)privateKey).getTreeDigestOID();
            if (this.random != null) {
                keyParams = new ParametersWithRandom(keyParams, this.random);
            }
            this.digest.reset();
            this.signer.init(true, keyParams);
            return;
        }
        throw new InvalidKeyException("unknown private key passed to XMSSMT");
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
        final byte[] digestResult = DigestUtil.getDigestResult(this.digest);
        try {
            return this.signer.generateSignature(digestResult);
        }
        catch (final Exception ex) {
            if (ex instanceof IllegalStateException) {
                throw new SignatureException(ex.getMessage());
            }
            throw new SignatureException(ex.toString());
        }
    }
    
    @Override
    protected boolean engineVerify(final byte[] array) throws SignatureException {
        return this.signer.verifySignature(DigestUtil.getDigestResult(this.digest), array);
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
    
    public PrivateKey getUpdatedPrivateKey() {
        if (this.treeDigest == null) {
            throw new IllegalStateException("signature object not in a signing state");
        }
        final BCXMSSMTPrivateKey bcxmssmtPrivateKey = new BCXMSSMTPrivateKey(this.treeDigest, (XMSSMTPrivateKeyParameters)this.signer.getUpdatedPrivateKey());
        this.treeDigest = null;
        return bcxmssmtPrivateKey;
    }
    
    public static class withSha256 extends XMSSMTSignatureSpi
    {
        public withSha256() {
            super("SHA256withXMSSMT", new SHA256Digest(), new XMSSMTSigner());
        }
    }
    
    public static class withSha512 extends XMSSMTSignatureSpi
    {
        public withSha512() {
            super("SHA512withXMSSMT", new SHA512Digest(), new XMSSMTSigner());
        }
    }
    
    public static class withShake128 extends XMSSMTSignatureSpi
    {
        public withShake128() {
            super("SHAKE128withXMSSMT", new SHAKEDigest(128), new XMSSMTSigner());
        }
    }
    
    public static class withShake256 extends XMSSMTSignatureSpi
    {
        public withShake256() {
            super("SHAKE256withXMSSMT", new SHAKEDigest(256), new XMSSMTSigner());
        }
    }
}
