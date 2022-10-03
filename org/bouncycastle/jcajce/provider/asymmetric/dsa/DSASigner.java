package org.bouncycastle.jcajce.provider.asymmetric.dsa;

import org.bouncycastle.crypto.digests.NullDigest;
import org.bouncycastle.crypto.signers.DSAKCalculator;
import org.bouncycastle.crypto.signers.HMacDSAKCalculator;
import org.bouncycastle.crypto.util.DigestFactory;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import java.io.IOException;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1Integer;
import java.security.spec.AlgorithmParameterSpec;
import java.math.BigInteger;
import java.security.SignatureException;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import java.security.PrivateKey;
import java.security.InvalidKeyException;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.CipherParameters;
import java.security.PublicKey;
import java.security.SecureRandom;
import org.bouncycastle.crypto.DSA;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.asn1.x509.X509ObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import java.security.SignatureSpi;

public class DSASigner extends SignatureSpi implements PKCSObjectIdentifiers, X509ObjectIdentifiers
{
    private Digest digest;
    private DSA signer;
    private SecureRandom random;
    
    protected DSASigner(final Digest digest, final DSA signer) {
        this.digest = digest;
        this.signer = signer;
    }
    
    @Override
    protected void engineInitVerify(final PublicKey publicKey) throws InvalidKeyException {
        final AsymmetricKeyParameter generatePublicKeyParameter = DSAUtil.generatePublicKeyParameter(publicKey);
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
        CipherParameters generatePrivateKeyParameter = DSAUtil.generatePrivateKeyParameter(privateKey);
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
            final BigInteger[] generateSignature = this.signer.generateSignature(array);
            return this.derEncode(generateSignature[0], generateSignature[1]);
        }
        catch (final Exception ex) {
            throw new SignatureException(ex.toString());
        }
    }
    
    @Override
    protected boolean engineVerify(final byte[] array) throws SignatureException {
        final byte[] array2 = new byte[this.digest.getDigestSize()];
        this.digest.doFinal(array2, 0);
        BigInteger[] derDecode;
        try {
            derDecode = this.derDecode(array);
        }
        catch (final Exception ex) {
            throw new SignatureException("error decoding signature bytes.");
        }
        return this.signer.verifySignature(array2, derDecode[0], derDecode[1]);
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
    
    private byte[] derEncode(final BigInteger bigInteger, final BigInteger bigInteger2) throws IOException {
        return new DERSequence(new ASN1Integer[] { new ASN1Integer(bigInteger), new ASN1Integer(bigInteger2) }).getEncoded("DER");
    }
    
    private BigInteger[] derDecode(final byte[] array) throws IOException {
        final ASN1Sequence asn1Sequence = (ASN1Sequence)ASN1Primitive.fromByteArray(array);
        if (asn1Sequence.size() != 2) {
            throw new IOException("malformed signature");
        }
        if (!Arrays.areEqual(array, asn1Sequence.getEncoded("DER"))) {
            throw new IOException("malformed signature");
        }
        return new BigInteger[] { ((ASN1Integer)asn1Sequence.getObjectAt(0)).getValue(), ((ASN1Integer)asn1Sequence.getObjectAt(1)).getValue() };
    }
    
    public static class detDSA extends DSASigner
    {
        public detDSA() {
            super(DigestFactory.createSHA1(), new org.bouncycastle.crypto.signers.DSASigner(new HMacDSAKCalculator(DigestFactory.createSHA1())));
        }
    }
    
    public static class detDSA224 extends DSASigner
    {
        public detDSA224() {
            super(DigestFactory.createSHA224(), new org.bouncycastle.crypto.signers.DSASigner(new HMacDSAKCalculator(DigestFactory.createSHA224())));
        }
    }
    
    public static class detDSA256 extends DSASigner
    {
        public detDSA256() {
            super(DigestFactory.createSHA256(), new org.bouncycastle.crypto.signers.DSASigner(new HMacDSAKCalculator(DigestFactory.createSHA256())));
        }
    }
    
    public static class detDSA384 extends DSASigner
    {
        public detDSA384() {
            super(DigestFactory.createSHA384(), new org.bouncycastle.crypto.signers.DSASigner(new HMacDSAKCalculator(DigestFactory.createSHA384())));
        }
    }
    
    public static class detDSA512 extends DSASigner
    {
        public detDSA512() {
            super(DigestFactory.createSHA512(), new org.bouncycastle.crypto.signers.DSASigner(new HMacDSAKCalculator(DigestFactory.createSHA512())));
        }
    }
    
    public static class detDSASha3_224 extends DSASigner
    {
        public detDSASha3_224() {
            super(DigestFactory.createSHA3_224(), new org.bouncycastle.crypto.signers.DSASigner(new HMacDSAKCalculator(DigestFactory.createSHA3_224())));
        }
    }
    
    public static class detDSASha3_256 extends DSASigner
    {
        public detDSASha3_256() {
            super(DigestFactory.createSHA3_256(), new org.bouncycastle.crypto.signers.DSASigner(new HMacDSAKCalculator(DigestFactory.createSHA3_256())));
        }
    }
    
    public static class detDSASha3_384 extends DSASigner
    {
        public detDSASha3_384() {
            super(DigestFactory.createSHA3_384(), new org.bouncycastle.crypto.signers.DSASigner(new HMacDSAKCalculator(DigestFactory.createSHA3_384())));
        }
    }
    
    public static class detDSASha3_512 extends DSASigner
    {
        public detDSASha3_512() {
            super(DigestFactory.createSHA3_512(), new org.bouncycastle.crypto.signers.DSASigner(new HMacDSAKCalculator(DigestFactory.createSHA3_512())));
        }
    }
    
    public static class dsa224 extends DSASigner
    {
        public dsa224() {
            super(DigestFactory.createSHA224(), new org.bouncycastle.crypto.signers.DSASigner());
        }
    }
    
    public static class dsa256 extends DSASigner
    {
        public dsa256() {
            super(DigestFactory.createSHA256(), new org.bouncycastle.crypto.signers.DSASigner());
        }
    }
    
    public static class dsa384 extends DSASigner
    {
        public dsa384() {
            super(DigestFactory.createSHA384(), new org.bouncycastle.crypto.signers.DSASigner());
        }
    }
    
    public static class dsa512 extends DSASigner
    {
        public dsa512() {
            super(DigestFactory.createSHA512(), new org.bouncycastle.crypto.signers.DSASigner());
        }
    }
    
    public static class dsaSha3_224 extends DSASigner
    {
        public dsaSha3_224() {
            super(DigestFactory.createSHA3_224(), new org.bouncycastle.crypto.signers.DSASigner());
        }
    }
    
    public static class dsaSha3_256 extends DSASigner
    {
        public dsaSha3_256() {
            super(DigestFactory.createSHA3_256(), new org.bouncycastle.crypto.signers.DSASigner());
        }
    }
    
    public static class dsaSha3_384 extends DSASigner
    {
        public dsaSha3_384() {
            super(DigestFactory.createSHA3_384(), new org.bouncycastle.crypto.signers.DSASigner());
        }
    }
    
    public static class dsaSha3_512 extends DSASigner
    {
        public dsaSha3_512() {
            super(DigestFactory.createSHA3_512(), new org.bouncycastle.crypto.signers.DSASigner());
        }
    }
    
    public static class noneDSA extends DSASigner
    {
        public noneDSA() {
            super(new NullDigest(), new org.bouncycastle.crypto.signers.DSASigner());
        }
    }
    
    public static class stdDSA extends DSASigner
    {
        public stdDSA() {
            super(DigestFactory.createSHA1(), new org.bouncycastle.crypto.signers.DSASigner());
        }
    }
}
