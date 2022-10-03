package sun.security.ec;

import java.security.interfaces.ECKey;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.InvalidParameterException;
import sun.security.jca.JCAUtil;
import java.security.GeneralSecurityException;
import java.security.Provider;
import java.util.Optional;
import sun.security.util.ECUtil;
import java.nio.ByteBuffer;
import java.security.SignatureException;
import java.security.PrivateKey;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.PublicKey;
import java.security.NoSuchAlgorithmException;
import java.security.ProviderException;
import java.security.spec.ECParameterSpec;
import java.security.interfaces.ECPublicKey;
import java.security.interfaces.ECPrivateKey;
import java.security.SecureRandom;
import java.security.MessageDigest;
import java.security.SignatureSpi;

abstract class ECDSASignature extends SignatureSpi
{
    private final MessageDigest messageDigest;
    private SecureRandom random;
    private boolean needsReset;
    private ECPrivateKey privateKey;
    private ECPublicKey publicKey;
    private ECParameterSpec sigParams;
    
    ECDSASignature() {
        this.sigParams = null;
        this.messageDigest = null;
    }
    
    ECDSASignature(final String s) {
        this.sigParams = null;
        try {
            this.messageDigest = MessageDigest.getInstance(s);
        }
        catch (final NoSuchAlgorithmException ex) {
            throw new ProviderException(ex);
        }
        this.needsReset = false;
    }
    
    @Override
    protected void engineInitVerify(final PublicKey publicKey) throws InvalidKeyException {
        final ECPublicKey publicKey2 = (ECPublicKey)ECKeyFactory.toECKey(publicKey);
        if (!isCompatible(this.sigParams, publicKey2.getParams())) {
            throw new InvalidKeyException("Key params does not match signature params");
        }
        this.publicKey = publicKey2;
        this.privateKey = null;
        this.resetDigest();
    }
    
    @Override
    protected void engineInitSign(final PrivateKey privateKey) throws InvalidKeyException {
        this.engineInitSign(privateKey, null);
    }
    
    @Override
    protected void engineInitSign(final PrivateKey privateKey, final SecureRandom random) throws InvalidKeyException {
        final ECPrivateKey privateKey2 = (ECPrivateKey)ECKeyFactory.toECKey(privateKey);
        if (!isCompatible(this.sigParams, privateKey2.getParams())) {
            throw new InvalidKeyException("Key params does not match signature params");
        }
        this.privateKey = privateKey2;
        this.publicKey = null;
        this.random = random;
        this.resetDigest();
    }
    
    protected void resetDigest() {
        if (this.needsReset) {
            if (this.messageDigest != null) {
                this.messageDigest.reset();
            }
            this.needsReset = false;
        }
    }
    
    protected byte[] getDigestValue() throws SignatureException {
        this.needsReset = false;
        return this.messageDigest.digest();
    }
    
    @Override
    protected void engineUpdate(final byte b) throws SignatureException {
        this.messageDigest.update(b);
        this.needsReset = true;
    }
    
    @Override
    protected void engineUpdate(final byte[] array, final int n, final int n2) throws SignatureException {
        this.messageDigest.update(array, n, n2);
        this.needsReset = true;
    }
    
    @Override
    protected void engineUpdate(final ByteBuffer byteBuffer) {
        if (byteBuffer.remaining() <= 0) {
            return;
        }
        this.messageDigest.update(byteBuffer);
        this.needsReset = true;
    }
    
    private static boolean isCompatible(final ECParameterSpec ecParameterSpec, final ECParameterSpec ecParameterSpec2) {
        return ecParameterSpec == null || ECUtil.equals(ecParameterSpec, ecParameterSpec2);
    }
    
    private byte[] signDigestImpl(final ECDSAOperations ecdsaOperations, final int n, final byte[] array, final ECPrivateKeyImpl ecPrivateKeyImpl, final SecureRandom secureRandom) throws SignatureException {
        final byte[] array2 = new byte[(n + 7) / 8];
        final byte[] arrayS = ecPrivateKeyImpl.getArrayS();
        final int n2 = 128;
        int i = 0;
        while (i < n2) {
            secureRandom.nextBytes(array2);
            final ECDSAOperations.Seed seed = new ECDSAOperations.Seed(array2);
            try {
                return ecdsaOperations.signDigest(arrayS, array, seed);
            }
            catch (final ECOperations.IntermediateValueException ex) {
                ++i;
                continue;
            }
            break;
        }
        throw new SignatureException("Unable to produce signature after " + n2 + " attempts");
    }
    
    private Optional<byte[]> signDigestImpl(final ECPrivateKey ecPrivateKey, final byte[] array, final SecureRandom secureRandom) throws SignatureException {
        if (!(ecPrivateKey instanceof ECPrivateKeyImpl)) {
            return Optional.empty();
        }
        final ECPrivateKeyImpl ecPrivateKeyImpl = (ECPrivateKeyImpl)ecPrivateKey;
        final ECParameterSpec params = ecPrivateKey.getParams();
        final int n = params.getOrder().bitLength() + 64;
        final Optional<ECDSAOperations> forParameters = ECDSAOperations.forParameters(params);
        if (!forParameters.isPresent()) {
            return Optional.empty();
        }
        return Optional.of(this.signDigestImpl(forParameters.get(), n, array, ecPrivateKeyImpl, secureRandom));
    }
    
    private byte[] signDigestNative(final ECPrivateKey ecPrivateKey, final byte[] array, final SecureRandom secureRandom) throws SignatureException {
        final byte[] byteArray = ecPrivateKey.getS().toByteArray();
        final ECParameterSpec params = ecPrivateKey.getParams();
        final byte[] encodeECParameterSpec = ECUtil.encodeECParameterSpec(null, params);
        final byte[] array2 = new byte[((params.getOrder().bitLength() + 7 >> 3) + 1) * 2];
        secureRandom.nextBytes(array2);
        final int n = secureRandom.nextInt() | 0x1;
        try {
            return signDigest(array, byteArray, encodeECParameterSpec, array2, n);
        }
        catch (final GeneralSecurityException ex) {
            throw new SignatureException("Could not sign data", ex);
        }
    }
    
    @Override
    protected byte[] engineSign() throws SignatureException {
        if (this.random == null) {
            this.random = JCAUtil.getSecureRandom();
        }
        final byte[] digestValue = this.getDigestValue();
        final Optional<byte[]> signDigestImpl = this.signDigestImpl(this.privateKey, digestValue, this.random);
        byte[] signDigestNative;
        if (signDigestImpl.isPresent()) {
            signDigestNative = signDigestImpl.get();
        }
        else {
            signDigestNative = this.signDigestNative(this.privateKey, digestValue, this.random);
        }
        return ECUtil.encodeSignature(signDigestNative);
    }
    
    @Override
    protected boolean engineVerify(final byte[] array) throws SignatureException {
        final ECParameterSpec params = this.publicKey.getParams();
        final byte[] encodeECParameterSpec = ECUtil.encodeECParameterSpec(null, params);
        byte[] array2;
        if (this.publicKey instanceof ECPublicKeyImpl) {
            array2 = ((ECPublicKeyImpl)this.publicKey).getEncodedPublicValue();
        }
        else {
            array2 = ECUtil.encodePoint(this.publicKey.getW(), params.getCurve());
        }
        try {
            return verifySignedDigest(ECUtil.decodeSignature(array), this.getDigestValue(), array2, encodeECParameterSpec);
        }
        catch (final GeneralSecurityException ex) {
            throw new SignatureException("Could not verify signature", ex);
        }
    }
    
    @Deprecated
    @Override
    protected void engineSetParameter(final String s, final Object o) throws InvalidParameterException {
        throw new UnsupportedOperationException("setParameter() not supported");
    }
    
    @Override
    protected void engineSetParameter(final AlgorithmParameterSpec algorithmParameterSpec) throws InvalidAlgorithmParameterException {
        if (algorithmParameterSpec != null && !(algorithmParameterSpec instanceof ECParameterSpec)) {
            throw new InvalidAlgorithmParameterException("No parameter accepted");
        }
        final Object o = (this.privateKey == null) ? this.publicKey : this.privateKey;
        if (o != null && !isCompatible((ECParameterSpec)algorithmParameterSpec, ((ECKey)o).getParams())) {
            throw new InvalidAlgorithmParameterException("Signature params does not match key params");
        }
        this.sigParams = (ECParameterSpec)algorithmParameterSpec;
    }
    
    @Deprecated
    @Override
    protected Object engineGetParameter(final String s) throws InvalidParameterException {
        throw new UnsupportedOperationException("getParameter() not supported");
    }
    
    @Override
    protected AlgorithmParameters engineGetParameters() {
        if (this.sigParams == null) {
            return null;
        }
        try {
            final AlgorithmParameters instance = AlgorithmParameters.getInstance("EC");
            instance.init(this.sigParams);
            return instance;
        }
        catch (final Exception ex) {
            throw new ProviderException("Error retrieving EC parameters", ex);
        }
    }
    
    private static native byte[] signDigest(final byte[] p0, final byte[] p1, final byte[] p2, final byte[] p3, final int p4) throws GeneralSecurityException;
    
    private static native boolean verifySignedDigest(final byte[] p0, final byte[] p1, final byte[] p2, final byte[] p3) throws GeneralSecurityException;
    
    public static final class Raw extends ECDSASignature
    {
        private static final int RAW_ECDSA_MAX = 64;
        private final byte[] precomputedDigest;
        private int offset;
        
        public Raw() {
            this.offset = 0;
            this.precomputedDigest = new byte[64];
        }
        
        @Override
        protected void engineUpdate(final byte b) throws SignatureException {
            if (this.offset >= this.precomputedDigest.length) {
                this.offset = 65;
                return;
            }
            this.precomputedDigest[this.offset++] = b;
        }
        
        @Override
        protected void engineUpdate(final byte[] array, final int n, final int n2) throws SignatureException {
            if (this.offset >= this.precomputedDigest.length) {
                this.offset = 65;
                return;
            }
            System.arraycopy(array, n, this.precomputedDigest, this.offset, n2);
            this.offset += n2;
        }
        
        @Override
        protected void engineUpdate(final ByteBuffer byteBuffer) {
            final int remaining = byteBuffer.remaining();
            if (remaining <= 0) {
                return;
            }
            if (this.offset + remaining >= this.precomputedDigest.length) {
                this.offset = 65;
                return;
            }
            byteBuffer.get(this.precomputedDigest, this.offset, remaining);
            this.offset += remaining;
        }
        
        @Override
        protected void resetDigest() {
            this.offset = 0;
        }
        
        @Override
        protected byte[] getDigestValue() throws SignatureException {
            if (this.offset > 64) {
                throw new SignatureException("Message digest is too long");
            }
            final byte[] array = new byte[this.offset];
            System.arraycopy(this.precomputedDigest, 0, array, 0, this.offset);
            this.offset = 0;
            return array;
        }
    }
    
    public static final class SHA1 extends ECDSASignature
    {
        public SHA1() {
            super("SHA1");
        }
    }
    
    public static final class SHA224 extends ECDSASignature
    {
        public SHA224() {
            super("SHA-224");
        }
    }
    
    public static final class SHA256 extends ECDSASignature
    {
        public SHA256() {
            super("SHA-256");
        }
    }
    
    public static final class SHA384 extends ECDSASignature
    {
        public SHA384() {
            super("SHA-384");
        }
    }
    
    public static final class SHA512 extends ECDSASignature
    {
        public SHA512() {
            super("SHA-512");
        }
    }
}
