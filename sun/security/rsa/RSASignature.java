package sun.security.rsa;

import java.security.AlgorithmParameters;
import java.security.spec.AlgorithmParameterSpec;
import java.security.InvalidParameterException;
import sun.security.util.DerInputStream;
import sun.security.util.DerValue;
import sun.security.x509.AlgorithmId;
import sun.security.util.DerOutputStream;
import javax.crypto.BadPaddingException;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.nio.ByteBuffer;
import java.security.SignatureException;
import java.security.InvalidAlgorithmParameterException;
import java.security.PrivateKey;
import java.security.InvalidKeyException;
import java.security.SecureRandom;
import java.security.interfaces.RSAKey;
import java.security.Key;
import java.security.PublicKey;
import java.security.NoSuchAlgorithmException;
import java.security.ProviderException;
import java.security.interfaces.RSAPublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.MessageDigest;
import sun.security.util.ObjectIdentifier;
import java.security.SignatureSpi;

public abstract class RSASignature extends SignatureSpi
{
    private static final int baseLength = 8;
    private final ObjectIdentifier digestOID;
    private final int encodedLength;
    private final MessageDigest md;
    private boolean digestReset;
    private RSAPrivateKey privateKey;
    private RSAPublicKey publicKey;
    private RSAPadding padding;
    
    RSASignature(final String s, final ObjectIdentifier digestOID, final int n) {
        this.digestOID = digestOID;
        try {
            this.md = MessageDigest.getInstance(s);
        }
        catch (final NoSuchAlgorithmException ex) {
            throw new ProviderException(ex);
        }
        this.digestReset = true;
        this.encodedLength = 8 + n + this.md.getDigestLength();
    }
    
    @Override
    protected void engineInitVerify(final PublicKey publicKey) throws InvalidKeyException {
        final RSAPublicKey publicKey2 = (RSAPublicKey)RSAKeyFactory.toRSAKey(publicKey);
        this.privateKey = null;
        this.initCommon(this.publicKey = publicKey2, null);
    }
    
    @Override
    protected void engineInitSign(final PrivateKey privateKey) throws InvalidKeyException {
        this.engineInitSign(privateKey, null);
    }
    
    @Override
    protected void engineInitSign(final PrivateKey privateKey, final SecureRandom secureRandom) throws InvalidKeyException {
        final RSAPrivateKey privateKey2 = (RSAPrivateKey)RSAKeyFactory.toRSAKey(privateKey);
        this.privateKey = privateKey2;
        this.publicKey = null;
        this.initCommon(privateKey2, secureRandom);
    }
    
    private void initCommon(final RSAKey rsaKey, final SecureRandom secureRandom) throws InvalidKeyException {
        try {
            RSAUtil.checkParamsAgainstType(RSAUtil.KeyType.RSA, rsaKey.getParams());
        }
        catch (final ProviderException ex) {
            throw new InvalidKeyException("Invalid key for RSA signatures", ex);
        }
        this.resetDigest();
        final int byteLength = RSACore.getByteLength(rsaKey);
        try {
            this.padding = RSAPadding.getInstance(1, byteLength, secureRandom);
        }
        catch (final InvalidAlgorithmParameterException ex2) {
            throw new InvalidKeyException(ex2.getMessage());
        }
        if (this.encodedLength > this.padding.getMaxDataSize()) {
            throw new InvalidKeyException("Key is too short for this signature algorithm");
        }
    }
    
    private void resetDigest() {
        if (!this.digestReset) {
            this.md.reset();
            this.digestReset = true;
        }
    }
    
    private byte[] getDigestValue() {
        this.digestReset = true;
        return this.md.digest();
    }
    
    @Override
    protected void engineUpdate(final byte b) throws SignatureException {
        this.md.update(b);
        this.digestReset = false;
    }
    
    @Override
    protected void engineUpdate(final byte[] array, final int n, final int n2) throws SignatureException {
        this.md.update(array, n, n2);
        this.digestReset = false;
    }
    
    @Override
    protected void engineUpdate(final ByteBuffer byteBuffer) {
        this.md.update(byteBuffer);
        this.digestReset = false;
    }
    
    @Override
    protected byte[] engineSign() throws SignatureException {
        if (this.privateKey == null) {
            throw new SignatureException("Missing private key");
        }
        final byte[] digestValue = this.getDigestValue();
        try {
            return RSACore.rsa(this.padding.pad(encodeSignature(this.digestOID, digestValue)), this.privateKey, true);
        }
        catch (final GeneralSecurityException ex) {
            throw new SignatureException("Could not sign data", ex);
        }
        catch (final IOException ex2) {
            throw new SignatureException("Could not encode data", ex2);
        }
    }
    
    @Override
    protected boolean engineVerify(final byte[] array) throws SignatureException {
        if (this.publicKey == null) {
            throw new SignatureException("Missing public key");
        }
        if (array.length != RSACore.getByteLength(this.publicKey)) {
            throw new SignatureException("Signature length not correct: got " + array.length + " but was expecting " + RSACore.getByteLength(this.publicKey));
        }
        final byte[] digestValue = this.getDigestValue();
        try {
            return MessageDigest.isEqual(digestValue, decodeSignature(this.digestOID, this.padding.unpad(RSACore.rsa(array, this.publicKey))));
        }
        catch (final BadPaddingException ex) {
            return false;
        }
        catch (final IOException ex2) {
            throw new SignatureException("Signature encoding error", ex2);
        }
    }
    
    public static byte[] encodeSignature(final ObjectIdentifier objectIdentifier, final byte[] array) throws IOException {
        final DerOutputStream derOutputStream = new DerOutputStream();
        new AlgorithmId(objectIdentifier).encode(derOutputStream);
        derOutputStream.putOctetString(array);
        return new DerValue((byte)48, derOutputStream.toByteArray()).toByteArray();
    }
    
    public static byte[] decodeSignature(final ObjectIdentifier objectIdentifier, final byte[] array) throws IOException {
        final DerInputStream derInputStream = new DerInputStream(array, 0, array.length, false);
        final DerValue[] sequence = derInputStream.getSequence(2);
        if (sequence.length != 2 || derInputStream.available() != 0) {
            throw new IOException("SEQUENCE length error");
        }
        final AlgorithmId parse = AlgorithmId.parse(sequence[0]);
        if (!parse.getOID().equals((Object)objectIdentifier)) {
            throw new IOException("ObjectIdentifier mismatch: " + parse.getOID());
        }
        if (parse.getEncodedParams() != null) {
            throw new IOException("Unexpected AlgorithmId parameters");
        }
        return sequence[1].getOctetString();
    }
    
    @Deprecated
    @Override
    protected void engineSetParameter(final String s, final Object o) throws InvalidParameterException {
        throw new UnsupportedOperationException("setParameter() not supported");
    }
    
    @Override
    protected void engineSetParameter(final AlgorithmParameterSpec algorithmParameterSpec) throws InvalidAlgorithmParameterException {
        if (algorithmParameterSpec != null) {
            throw new InvalidAlgorithmParameterException("No parameters accepted");
        }
    }
    
    @Deprecated
    @Override
    protected Object engineGetParameter(final String s) throws InvalidParameterException {
        throw new UnsupportedOperationException("getParameter() not supported");
    }
    
    @Override
    protected AlgorithmParameters engineGetParameters() {
        return null;
    }
    
    public static final class MD2withRSA extends RSASignature
    {
        public MD2withRSA() {
            super("MD2", AlgorithmId.MD2_oid, 10);
        }
    }
    
    public static final class MD5withRSA extends RSASignature
    {
        public MD5withRSA() {
            super("MD5", AlgorithmId.MD5_oid, 10);
        }
    }
    
    public static final class SHA1withRSA extends RSASignature
    {
        public SHA1withRSA() {
            super("SHA-1", AlgorithmId.SHA_oid, 7);
        }
    }
    
    public static final class SHA224withRSA extends RSASignature
    {
        public SHA224withRSA() {
            super("SHA-224", AlgorithmId.SHA224_oid, 11);
        }
    }
    
    public static final class SHA256withRSA extends RSASignature
    {
        public SHA256withRSA() {
            super("SHA-256", AlgorithmId.SHA256_oid, 11);
        }
    }
    
    public static final class SHA384withRSA extends RSASignature
    {
        public SHA384withRSA() {
            super("SHA-384", AlgorithmId.SHA384_oid, 11);
        }
    }
    
    public static final class SHA512withRSA extends RSASignature
    {
        public SHA512withRSA() {
            super("SHA-512", AlgorithmId.SHA512_oid, 11);
        }
    }
    
    public static final class SHA512_224withRSA extends RSASignature
    {
        public SHA512_224withRSA() {
            super("SHA-512/224", AlgorithmId.SHA512_224_oid, 11);
        }
    }
    
    public static final class SHA512_256withRSA extends RSASignature
    {
        public SHA512_256withRSA() {
            super("SHA-512/256", AlgorithmId.SHA512_256_oid, 11);
        }
    }
}
