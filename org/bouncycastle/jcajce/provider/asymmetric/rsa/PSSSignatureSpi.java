package org.bouncycastle.jcajce.provider.asymmetric.rsa;

import org.bouncycastle.crypto.engines.RSABlindedEngine;
import java.io.ByteArrayOutputStream;
import java.security.spec.MGF1ParameterSpec;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import java.security.InvalidAlgorithmParameterException;
import java.security.spec.AlgorithmParameterSpec;
import org.bouncycastle.crypto.CryptoException;
import java.security.SignatureException;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import java.security.interfaces.RSAPrivateKey;
import java.security.SecureRandom;
import java.security.PrivateKey;
import org.bouncycastle.crypto.CipherParameters;
import java.security.InvalidKeyException;
import java.security.interfaces.RSAPublicKey;
import java.security.PublicKey;
import org.bouncycastle.jcajce.provider.util.DigestFactory;
import org.bouncycastle.jcajce.util.BCJcaJceHelper;
import org.bouncycastle.crypto.signers.PSSSigner;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.AsymmetricBlockCipher;
import java.security.spec.PSSParameterSpec;
import java.security.AlgorithmParameters;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import java.security.SignatureSpi;

public class PSSSignatureSpi extends SignatureSpi
{
    private final JcaJceHelper helper;
    private AlgorithmParameters engineParams;
    private PSSParameterSpec paramSpec;
    private PSSParameterSpec originalSpec;
    private AsymmetricBlockCipher signer;
    private Digest contentDigest;
    private Digest mgfDigest;
    private int saltLength;
    private byte trailer;
    private boolean isRaw;
    private PSSSigner pss;
    
    private byte getTrailer(final int n) {
        if (n == 1) {
            return -68;
        }
        throw new IllegalArgumentException("unknown trailer field");
    }
    
    private void setupContentDigest() {
        if (this.isRaw) {
            this.contentDigest = new NullPssDigest(this.mgfDigest);
        }
        else {
            this.contentDigest = this.mgfDigest;
        }
    }
    
    protected PSSSignatureSpi(final AsymmetricBlockCipher asymmetricBlockCipher, final PSSParameterSpec pssParameterSpec) {
        this(asymmetricBlockCipher, pssParameterSpec, false);
    }
    
    protected PSSSignatureSpi(final AsymmetricBlockCipher signer, final PSSParameterSpec pssParameterSpec, final boolean isRaw) {
        this.helper = new BCJcaJceHelper();
        this.signer = signer;
        this.originalSpec = pssParameterSpec;
        if (pssParameterSpec == null) {
            this.paramSpec = PSSParameterSpec.DEFAULT;
        }
        else {
            this.paramSpec = pssParameterSpec;
        }
        this.mgfDigest = DigestFactory.getDigest(this.paramSpec.getDigestAlgorithm());
        this.saltLength = this.paramSpec.getSaltLength();
        this.trailer = this.getTrailer(this.paramSpec.getTrailerField());
        this.isRaw = isRaw;
        this.setupContentDigest();
    }
    
    @Override
    protected void engineInitVerify(final PublicKey publicKey) throws InvalidKeyException {
        if (!(publicKey instanceof RSAPublicKey)) {
            throw new InvalidKeyException("Supplied key is not a RSAPublicKey instance");
        }
        (this.pss = new PSSSigner(this.signer, this.contentDigest, this.mgfDigest, this.saltLength, this.trailer)).init(false, RSAUtil.generatePublicKeyParameter((RSAPublicKey)publicKey));
    }
    
    @Override
    protected void engineInitSign(final PrivateKey privateKey, final SecureRandom secureRandom) throws InvalidKeyException {
        if (!(privateKey instanceof RSAPrivateKey)) {
            throw new InvalidKeyException("Supplied key is not a RSAPrivateKey instance");
        }
        (this.pss = new PSSSigner(this.signer, this.contentDigest, this.mgfDigest, this.saltLength, this.trailer)).init(true, new ParametersWithRandom(RSAUtil.generatePrivateKeyParameter((RSAPrivateKey)privateKey), secureRandom));
    }
    
    @Override
    protected void engineInitSign(final PrivateKey privateKey) throws InvalidKeyException {
        if (!(privateKey instanceof RSAPrivateKey)) {
            throw new InvalidKeyException("Supplied key is not a RSAPrivateKey instance");
        }
        (this.pss = new PSSSigner(this.signer, this.contentDigest, this.mgfDigest, this.saltLength, this.trailer)).init(true, RSAUtil.generatePrivateKeyParameter((RSAPrivateKey)privateKey));
    }
    
    @Override
    protected void engineUpdate(final byte b) throws SignatureException {
        this.pss.update(b);
    }
    
    @Override
    protected void engineUpdate(final byte[] array, final int n, final int n2) throws SignatureException {
        this.pss.update(array, n, n2);
    }
    
    @Override
    protected byte[] engineSign() throws SignatureException {
        try {
            return this.pss.generateSignature();
        }
        catch (final CryptoException ex) {
            throw new SignatureException(ex.getMessage());
        }
    }
    
    @Override
    protected boolean engineVerify(final byte[] array) throws SignatureException {
        return this.pss.verifySignature(array);
    }
    
    @Override
    protected void engineSetParameter(final AlgorithmParameterSpec algorithmParameterSpec) throws InvalidAlgorithmParameterException {
        if (!(algorithmParameterSpec instanceof PSSParameterSpec)) {
            throw new InvalidAlgorithmParameterException("Only PSSParameterSpec supported");
        }
        final PSSParameterSpec paramSpec = (PSSParameterSpec)algorithmParameterSpec;
        if (this.originalSpec != null && !DigestFactory.isSameDigest(this.originalSpec.getDigestAlgorithm(), paramSpec.getDigestAlgorithm())) {
            throw new InvalidAlgorithmParameterException("parameter must be using " + this.originalSpec.getDigestAlgorithm());
        }
        if (!paramSpec.getMGFAlgorithm().equalsIgnoreCase("MGF1") && !paramSpec.getMGFAlgorithm().equals(PKCSObjectIdentifiers.id_mgf1.getId())) {
            throw new InvalidAlgorithmParameterException("unknown mask generation function specified");
        }
        if (!(paramSpec.getMGFParameters() instanceof MGF1ParameterSpec)) {
            throw new InvalidAlgorithmParameterException("unknown MGF parameters");
        }
        final MGF1ParameterSpec mgf1ParameterSpec = (MGF1ParameterSpec)paramSpec.getMGFParameters();
        if (!DigestFactory.isSameDigest(mgf1ParameterSpec.getDigestAlgorithm(), paramSpec.getDigestAlgorithm())) {
            throw new InvalidAlgorithmParameterException("digest algorithm for MGF should be the same as for PSS parameters.");
        }
        final Digest digest = DigestFactory.getDigest(mgf1ParameterSpec.getDigestAlgorithm());
        if (digest == null) {
            throw new InvalidAlgorithmParameterException("no match on MGF digest algorithm: " + mgf1ParameterSpec.getDigestAlgorithm());
        }
        this.engineParams = null;
        this.paramSpec = paramSpec;
        this.mgfDigest = digest;
        this.saltLength = this.paramSpec.getSaltLength();
        this.trailer = this.getTrailer(this.paramSpec.getTrailerField());
        this.setupContentDigest();
    }
    
    @Override
    protected AlgorithmParameters engineGetParameters() {
        if (this.engineParams == null && this.paramSpec != null) {
            try {
                (this.engineParams = this.helper.createAlgorithmParameters("PSS")).init(this.paramSpec);
            }
            catch (final Exception ex) {
                throw new RuntimeException(ex.toString());
            }
        }
        return this.engineParams;
    }
    
    @Override
    @Deprecated
    protected void engineSetParameter(final String s, final Object o) {
        throw new UnsupportedOperationException("engineSetParameter unsupported");
    }
    
    @Override
    protected Object engineGetParameter(final String s) {
        throw new UnsupportedOperationException("engineGetParameter unsupported");
    }
    
    private class NullPssDigest implements Digest
    {
        private ByteArrayOutputStream bOut;
        private Digest baseDigest;
        private boolean oddTime;
        
        public NullPssDigest(final Digest baseDigest) {
            this.bOut = new ByteArrayOutputStream();
            this.oddTime = true;
            this.baseDigest = baseDigest;
        }
        
        public String getAlgorithmName() {
            return "NULL";
        }
        
        public int getDigestSize() {
            return this.baseDigest.getDigestSize();
        }
        
        public void update(final byte b) {
            this.bOut.write(b);
        }
        
        public void update(final byte[] array, final int n, final int n2) {
            this.bOut.write(array, n, n2);
        }
        
        public int doFinal(final byte[] array, final int n) {
            final byte[] byteArray = this.bOut.toByteArray();
            if (this.oddTime) {
                System.arraycopy(byteArray, 0, array, n, byteArray.length);
            }
            else {
                this.baseDigest.update(byteArray, 0, byteArray.length);
                this.baseDigest.doFinal(array, n);
            }
            this.reset();
            this.oddTime = !this.oddTime;
            return byteArray.length;
        }
        
        public void reset() {
            this.bOut.reset();
            this.baseDigest.reset();
        }
        
        public int getByteLength() {
            return 0;
        }
    }
    
    public static class PSSwithRSA extends PSSSignatureSpi
    {
        public PSSwithRSA() {
            super(new RSABlindedEngine(), null);
        }
    }
    
    public static class SHA1withRSA extends PSSSignatureSpi
    {
        public SHA1withRSA() {
            super(new RSABlindedEngine(), PSSParameterSpec.DEFAULT);
        }
    }
    
    public static class SHA224withRSA extends PSSSignatureSpi
    {
        public SHA224withRSA() {
            super(new RSABlindedEngine(), new PSSParameterSpec("SHA-224", "MGF1", new MGF1ParameterSpec("SHA-224"), 28, 1));
        }
    }
    
    public static class SHA256withRSA extends PSSSignatureSpi
    {
        public SHA256withRSA() {
            super(new RSABlindedEngine(), new PSSParameterSpec("SHA-256", "MGF1", new MGF1ParameterSpec("SHA-256"), 32, 1));
        }
    }
    
    public static class SHA384withRSA extends PSSSignatureSpi
    {
        public SHA384withRSA() {
            super(new RSABlindedEngine(), new PSSParameterSpec("SHA-384", "MGF1", new MGF1ParameterSpec("SHA-384"), 48, 1));
        }
    }
    
    public static class SHA3_224withRSA extends PSSSignatureSpi
    {
        public SHA3_224withRSA() {
            super(new RSABlindedEngine(), new PSSParameterSpec("SHA3-224", "MGF1", new MGF1ParameterSpec("SHA3-224"), 28, 1));
        }
    }
    
    public static class SHA3_256withRSA extends PSSSignatureSpi
    {
        public SHA3_256withRSA() {
            super(new RSABlindedEngine(), new PSSParameterSpec("SHA3-256", "MGF1", new MGF1ParameterSpec("SHA3-256"), 32, 1));
        }
    }
    
    public static class SHA3_384withRSA extends PSSSignatureSpi
    {
        public SHA3_384withRSA() {
            super(new RSABlindedEngine(), new PSSParameterSpec("SHA3-384", "MGF1", new MGF1ParameterSpec("SHA3-384"), 48, 1));
        }
    }
    
    public static class SHA3_512withRSA extends PSSSignatureSpi
    {
        public SHA3_512withRSA() {
            super(new RSABlindedEngine(), new PSSParameterSpec("SHA3-512", "MGF1", new MGF1ParameterSpec("SHA3-512"), 64, 1));
        }
    }
    
    public static class SHA512_224withRSA extends PSSSignatureSpi
    {
        public SHA512_224withRSA() {
            super(new RSABlindedEngine(), new PSSParameterSpec("SHA-512(224)", "MGF1", new MGF1ParameterSpec("SHA-512(224)"), 28, 1));
        }
    }
    
    public static class SHA512_256withRSA extends PSSSignatureSpi
    {
        public SHA512_256withRSA() {
            super(new RSABlindedEngine(), new PSSParameterSpec("SHA-512(256)", "MGF1", new MGF1ParameterSpec("SHA-512(256)"), 32, 1));
        }
    }
    
    public static class SHA512withRSA extends PSSSignatureSpi
    {
        public SHA512withRSA() {
            super(new RSABlindedEngine(), new PSSParameterSpec("SHA-512", "MGF1", new MGF1ParameterSpec("SHA-512"), 64, 1));
        }
    }
    
    public static class nonePSS extends PSSSignatureSpi
    {
        public nonePSS() {
            super(new RSABlindedEngine(), null, true);
        }
    }
}
