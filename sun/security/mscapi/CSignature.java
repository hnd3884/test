package sun.security.mscapi;

import java.util.Locale;
import java.security.GeneralSecurityException;
import java.security.spec.MGF1ParameterSpec;
import java.security.NoSuchProviderException;
import java.security.Signature;
import java.security.spec.PSSParameterSpec;
import sun.security.util.ECUtil;
import sun.security.util.KeyUtil;
import java.security.Key;
import java.security.interfaces.ECPublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.PublicKey;
import java.math.BigInteger;
import sun.security.rsa.RSAKeyFactory;
import java.security.InvalidKeyException;
import java.security.PrivateKey;
import java.security.KeyStoreException;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.InvalidParameterException;
import java.nio.ByteBuffer;
import java.security.SignatureException;
import java.security.NoSuchAlgorithmException;
import java.security.ProviderException;
import java.security.MessageDigest;
import java.security.SignatureSpi;

abstract class CSignature extends SignatureSpi
{
    protected String keyAlgorithm;
    protected MessageDigest messageDigest;
    protected String messageDigestAlgorithm;
    protected boolean needsReset;
    protected CPrivateKey privateKey;
    protected CPublicKey publicKey;
    
    CSignature(final String keyAlgorithm, final String s) {
        this.privateKey = null;
        this.publicKey = null;
        this.keyAlgorithm = keyAlgorithm;
        Label_0065: {
            if (s != null) {
                try {
                    this.messageDigest = MessageDigest.getInstance(s);
                    this.messageDigestAlgorithm = this.messageDigest.getAlgorithm();
                    break Label_0065;
                }
                catch (final NoSuchAlgorithmException ex) {
                    throw new ProviderException(ex);
                }
            }
            this.messageDigest = null;
            this.messageDigestAlgorithm = null;
        }
        this.needsReset = false;
    }
    
    static native byte[] signCngHash(final int p0, final byte[] p1, final int p2, final int p3, final String p4, final long p5, final long p6) throws SignatureException;
    
    private static native boolean verifyCngSignedHash(final int p0, final byte[] p1, final int p2, final byte[] p3, final int p4, final int p5, final String p6, final long p7, final long p8) throws SignatureException;
    
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
    
    protected void setDigestName(final String messageDigestAlgorithm) {
        this.messageDigestAlgorithm = messageDigestAlgorithm;
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
        this.messageDigest.update(byteBuffer);
        this.needsReset = true;
    }
    
    private static byte[] convertEndianArray(final byte[] array) {
        if (array == null || array.length == 0) {
            return array;
        }
        final byte[] array2 = new byte[array.length];
        for (int i = 0; i < array.length; ++i) {
            array2[i] = array[array.length - i - 1];
        }
        return array2;
    }
    
    private static native byte[] signHash(final boolean p0, final byte[] p1, final int p2, final String p3, final long p4, final long p5) throws SignatureException;
    
    private static native boolean verifySignedHash(final byte[] p0, final int p1, final String p2, final byte[] p3, final int p4, final long p5, final long p6) throws SignatureException;
    
    @Deprecated
    @Override
    protected void engineSetParameter(final String s, final Object o) throws InvalidParameterException {
        throw new InvalidParameterException("Parameter not supported");
    }
    
    @Override
    protected void engineSetParameter(final AlgorithmParameterSpec algorithmParameterSpec) throws InvalidAlgorithmParameterException {
        if (algorithmParameterSpec != null) {
            throw new InvalidAlgorithmParameterException("No parameter accepted");
        }
    }
    
    @Deprecated
    @Override
    protected Object engineGetParameter(final String s) throws InvalidParameterException {
        throw new InvalidParameterException("Parameter not supported");
    }
    
    @Override
    protected AlgorithmParameters engineGetParameters() {
        return null;
    }
    
    static native CPublicKey importPublicKey(final String p0, final byte[] p1, final int p2) throws KeyStoreException;
    
    static native CPublicKey importECPublicKey(final String p0, final byte[] p1, final int p2) throws KeyStoreException;
    
    static class RSA extends CSignature
    {
        public RSA(final String s) {
            super("RSA", s);
        }
        
        @Override
        protected void engineInitSign(final PrivateKey privateKey) throws InvalidKeyException {
            if (privateKey == null) {
                throw new InvalidKeyException("Key cannot be null");
            }
            if (!(privateKey instanceof CPrivateKey) || !privateKey.getAlgorithm().equalsIgnoreCase("RSA")) {
                throw new InvalidKeyException("Key type not supported: " + privateKey.getClass() + " " + privateKey.getAlgorithm());
            }
            this.privateKey = (CPrivateKey)privateKey;
            RSAKeyFactory.checkKeyLengths(this.privateKey.length() + 7 & 0xFFFFFFF8, null, 512, 16384);
            this.publicKey = null;
            this.resetDigest();
        }
        
        @Override
        protected void engineInitVerify(final PublicKey publicKey) throws InvalidKeyException {
            if (publicKey == null) {
                throw new InvalidKeyException("Key cannot be null");
            }
            if (!(publicKey instanceof RSAPublicKey)) {
                throw new InvalidKeyException("Key type not supported: " + publicKey.getClass());
            }
            if (!(publicKey instanceof CPublicKey)) {
                final RSAPublicKey rsaPublicKey = (RSAPublicKey)publicKey;
                final BigInteger modulus = rsaPublicKey.getModulus();
                final BigInteger publicExponent = rsaPublicKey.getPublicExponent();
                RSAKeyFactory.checkKeyLengths(modulus.bitLength() + 7 & 0xFFFFFFF8, publicExponent, -1, 16384);
                final byte[] byteArray = modulus.toByteArray();
                final byte[] byteArray2 = publicExponent.toByteArray();
                final int n = (byteArray[0] == 0) ? ((byteArray.length - 1) * 8) : (byteArray.length * 8);
                final byte[] generatePublicKeyBlob = generatePublicKeyBlob(n, byteArray, byteArray2);
                try {
                    this.publicKey = CSignature.importPublicKey("RSA", generatePublicKeyBlob, n);
                }
                catch (final KeyStoreException ex) {
                    throw new InvalidKeyException(ex);
                }
            }
            else {
                this.publicKey = (CPublicKey)publicKey;
            }
            this.privateKey = null;
            this.resetDigest();
        }
        
        @Override
        protected byte[] engineSign() throws SignatureException {
            final byte[] digestValue = this.getDigestValue();
            if (this.privateKey.getHCryptKey() == 0L) {
                return CSignature.signCngHash(1, digestValue, digestValue.length, 0, (this instanceof NONEwithRSA) ? null : this.messageDigestAlgorithm, this.privateKey.getHCryptProvider(), 0L);
            }
            return convertEndianArray(signHash(this instanceof NONEwithRSA, digestValue, digestValue.length, this.messageDigestAlgorithm, this.privateKey.getHCryptProvider(), this.privateKey.getHCryptKey()));
        }
        
        @Override
        protected boolean engineVerify(final byte[] array) throws SignatureException {
            final byte[] digestValue = this.getDigestValue();
            if (this.publicKey.getHCryptKey() == 0L) {
                return verifyCngSignedHash(1, digestValue, digestValue.length, array, array.length, 0, this.messageDigestAlgorithm, this.publicKey.getHCryptProvider(), 0L);
            }
            return verifySignedHash(digestValue, digestValue.length, this.messageDigestAlgorithm, convertEndianArray(array), array.length, this.publicKey.getHCryptProvider(), this.publicKey.getHCryptKey());
        }
        
        static native byte[] generatePublicKeyBlob(final int p0, final byte[] p1, final byte[] p2) throws InvalidKeyException;
    }
    
    public static final class NONEwithRSA extends RSA
    {
        private static final int RAW_RSA_MAX = 64;
        private final byte[] precomputedDigest;
        private int offset;
        
        public NONEwithRSA() {
            super(null);
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
            if (n2 > this.precomputedDigest.length - this.offset) {
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
            if (remaining > this.precomputedDigest.length - this.offset) {
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
            if (this.offset == 20) {
                this.setDigestName("SHA1");
            }
            else if (this.offset == 36) {
                this.setDigestName("SHA1+MD5");
            }
            else if (this.offset == 32) {
                this.setDigestName("SHA-256");
            }
            else if (this.offset == 48) {
                this.setDigestName("SHA-384");
            }
            else if (this.offset == 64) {
                this.setDigestName("SHA-512");
            }
            else {
                if (this.offset != 16) {
                    throw new SignatureException("Message digest length is not supported");
                }
                this.setDigestName("MD5");
            }
            final byte[] array = new byte[this.offset];
            System.arraycopy(this.precomputedDigest, 0, array, 0, this.offset);
            this.offset = 0;
            return array;
        }
    }
    
    public static final class SHA1withRSA extends RSA
    {
        public SHA1withRSA() {
            super("SHA1");
        }
    }
    
    public static final class SHA256withRSA extends RSA
    {
        public SHA256withRSA() {
            super("SHA-256");
        }
    }
    
    public static final class SHA384withRSA extends RSA
    {
        public SHA384withRSA() {
            super("SHA-384");
        }
    }
    
    public static final class SHA512withRSA extends RSA
    {
        public SHA512withRSA() {
            super("SHA-512");
        }
    }
    
    public static final class MD5withRSA extends RSA
    {
        public MD5withRSA() {
            super("MD5");
        }
    }
    
    public static final class MD2withRSA extends RSA
    {
        public MD2withRSA() {
            super("MD2");
        }
    }
    
    public static final class SHA1withECDSA extends ECDSA
    {
        public SHA1withECDSA() {
            super("SHA-1");
        }
    }
    
    public static final class SHA224withECDSA extends ECDSA
    {
        public SHA224withECDSA() {
            super("SHA-224");
        }
    }
    
    public static final class SHA256withECDSA extends ECDSA
    {
        public SHA256withECDSA() {
            super("SHA-256");
        }
    }
    
    public static final class SHA384withECDSA extends ECDSA
    {
        public SHA384withECDSA() {
            super("SHA-384");
        }
    }
    
    public static final class SHA512withECDSA extends ECDSA
    {
        public SHA512withECDSA() {
            super("SHA-512");
        }
    }
    
    static class ECDSA extends CSignature
    {
        public ECDSA(final String s) {
            super("EC", s);
        }
        
        @Override
        protected void engineInitSign(final PrivateKey privateKey) throws InvalidKeyException {
            if (privateKey == null) {
                throw new InvalidKeyException("Key cannot be null");
            }
            if (!(privateKey instanceof CPrivateKey) || !privateKey.getAlgorithm().equalsIgnoreCase("EC")) {
                throw new InvalidKeyException("Key type not supported: " + privateKey.getClass() + " " + privateKey.getAlgorithm());
            }
            this.privateKey = (CPrivateKey)privateKey;
            this.publicKey = null;
            this.resetDigest();
        }
        
        @Override
        protected void engineInitVerify(final PublicKey publicKey) throws InvalidKeyException {
            if (publicKey == null) {
                throw new InvalidKeyException("Key cannot be null");
            }
            if (!(publicKey instanceof ECPublicKey)) {
                throw new InvalidKeyException("Key type not supported: " + publicKey.getClass());
            }
            Label_0096: {
                if (!(publicKey instanceof CPublicKey)) {
                    try {
                        this.publicKey = CSignature.importECPublicKey("EC", CKey.generateECBlob(publicKey), KeyUtil.getKeySize(publicKey));
                        break Label_0096;
                    }
                    catch (final KeyStoreException ex) {
                        throw new InvalidKeyException(ex);
                    }
                }
                this.publicKey = (CPublicKey)publicKey;
            }
            this.privateKey = null;
            this.resetDigest();
        }
        
        @Override
        protected byte[] engineSign() throws SignatureException {
            final byte[] digestValue = this.getDigestValue();
            return ECUtil.encodeSignature(CSignature.signCngHash(0, digestValue, digestValue.length, 0, null, this.privateKey.getHCryptProvider(), 0L));
        }
        
        @Override
        protected boolean engineVerify(byte[] decodeSignature) throws SignatureException {
            final byte[] digestValue = this.getDigestValue();
            decodeSignature = ECUtil.decodeSignature(decodeSignature);
            return verifyCngSignedHash(0, digestValue, digestValue.length, decodeSignature, decodeSignature.length, 0, null, this.publicKey.getHCryptProvider(), 0L);
        }
    }
    
    public static final class PSS extends RSA
    {
        private PSSParameterSpec pssParams;
        private Signature fallbackSignature;
        
        public PSS() {
            super(null);
            this.pssParams = null;
        }
        
        @Override
        protected void engineInitSign(final PrivateKey privateKey) throws InvalidKeyException {
            super.engineInitSign(privateKey);
            this.fallbackSignature = null;
        }
        
        @Override
        protected void engineInitVerify(final PublicKey publicKey) throws InvalidKeyException {
            if (publicKey == null) {
                throw new InvalidKeyException("Key cannot be null");
            }
            if (!(publicKey instanceof RSAPublicKey)) {
                throw new InvalidKeyException("Key type not supported: " + publicKey.getClass());
            }
            this.privateKey = null;
            if (publicKey instanceof CPublicKey) {
                this.fallbackSignature = null;
                this.publicKey = (CPublicKey)publicKey;
            }
            else {
                if (this.fallbackSignature == null) {
                    try {
                        this.fallbackSignature = Signature.getInstance("RSASSA-PSS", "SunRsaSign");
                    }
                    catch (final NoSuchAlgorithmException | NoSuchProviderException ex) {
                        throw new InvalidKeyException("Invalid key", (Throwable)ex);
                    }
                }
                this.fallbackSignature.initVerify(publicKey);
                if (this.pssParams != null) {
                    try {
                        this.fallbackSignature.setParameter(this.pssParams);
                    }
                    catch (final InvalidAlgorithmParameterException ex2) {
                        throw new InvalidKeyException("Invalid params", ex2);
                    }
                }
                this.publicKey = null;
            }
            this.resetDigest();
        }
        
        @Override
        protected void engineUpdate(final byte b) throws SignatureException {
            this.ensureInit();
            if (this.fallbackSignature != null) {
                this.fallbackSignature.update(b);
            }
            else {
                this.messageDigest.update(b);
            }
            this.needsReset = true;
        }
        
        @Override
        protected void engineUpdate(final byte[] array, final int n, final int n2) throws SignatureException {
            this.ensureInit();
            if (this.fallbackSignature != null) {
                this.fallbackSignature.update(array, n, n2);
            }
            else {
                this.messageDigest.update(array, n, n2);
            }
            this.needsReset = true;
        }
        
        @Override
        protected void engineUpdate(final ByteBuffer byteBuffer) {
            try {
                this.ensureInit();
            }
            catch (final SignatureException ex) {
                throw new RuntimeException(ex.getMessage());
            }
            Label_0059: {
                if (this.fallbackSignature != null) {
                    try {
                        this.fallbackSignature.update(byteBuffer);
                        break Label_0059;
                    }
                    catch (final SignatureException ex2) {
                        throw new RuntimeException(ex2.getMessage());
                    }
                }
                this.messageDigest.update(byteBuffer);
            }
            this.needsReset = true;
        }
        
        @Override
        protected byte[] engineSign() throws SignatureException {
            this.ensureInit();
            final byte[] digestValue = this.getDigestValue();
            return CSignature.signCngHash(2, digestValue, digestValue.length, this.pssParams.getSaltLength(), ((MGF1ParameterSpec)this.pssParams.getMGFParameters()).getDigestAlgorithm(), this.privateKey.getHCryptProvider(), this.privateKey.getHCryptKey());
        }
        
        @Override
        protected boolean engineVerify(final byte[] array) throws SignatureException {
            this.ensureInit();
            if (this.fallbackSignature != null) {
                this.needsReset = false;
                return this.fallbackSignature.verify(array);
            }
            final byte[] digestValue = this.getDigestValue();
            return verifyCngSignedHash(2, digestValue, digestValue.length, array, array.length, this.pssParams.getSaltLength(), ((MGF1ParameterSpec)this.pssParams.getMGFParameters()).getDigestAlgorithm(), this.publicKey.getHCryptProvider(), this.publicKey.getHCryptKey());
        }
        
        @Override
        protected void engineSetParameter(final AlgorithmParameterSpec parameter) throws InvalidAlgorithmParameterException {
            if (this.needsReset) {
                throw new ProviderException("Cannot set parameters during operations");
            }
            this.pssParams = this.validateSigParams(parameter);
            if (this.fallbackSignature != null) {
                this.fallbackSignature.setParameter(parameter);
            }
        }
        
        @Override
        protected AlgorithmParameters engineGetParameters() {
            AlgorithmParameters instance = null;
            if (this.pssParams != null) {
                try {
                    instance = AlgorithmParameters.getInstance("RSASSA-PSS");
                    instance.init(this.pssParams);
                }
                catch (final GeneralSecurityException ex) {
                    throw new ProviderException(ex.getMessage());
                }
            }
            return instance;
        }
        
        private void ensureInit() throws SignatureException {
            if (this.privateKey == null && this.publicKey == null && this.fallbackSignature == null) {
                throw new SignatureException("Missing key");
            }
            if (this.pssParams == null) {
                throw new SignatureException("Parameters required for RSASSA-PSS signatures");
            }
            if (this.fallbackSignature == null && this.messageDigest == null) {
                try {
                    this.messageDigest = MessageDigest.getInstance(this.pssParams.getDigestAlgorithm());
                }
                catch (final NoSuchAlgorithmException ex) {
                    throw new SignatureException(ex);
                }
            }
        }
        
        private PSSParameterSpec validateSigParams(final AlgorithmParameterSpec algorithmParameterSpec) throws InvalidAlgorithmParameterException {
            if (algorithmParameterSpec == null) {
                throw new InvalidAlgorithmParameterException("Parameters cannot be null");
            }
            if (!(algorithmParameterSpec instanceof PSSParameterSpec)) {
                throw new InvalidAlgorithmParameterException("parameters must be type PSSParameterSpec");
            }
            final PSSParameterSpec pssParameterSpec = (PSSParameterSpec)algorithmParameterSpec;
            if (pssParameterSpec == this.pssParams) {
                return pssParameterSpec;
            }
            if (!pssParameterSpec.getMGFAlgorithm().equalsIgnoreCase("MGF1")) {
                throw new InvalidAlgorithmParameterException("Only supports MGF1");
            }
            if (pssParameterSpec.getTrailerField() != 1) {
                throw new InvalidAlgorithmParameterException("Only supports TrailerFieldBC(1)");
            }
            final AlgorithmParameterSpec mgfParameters = pssParameterSpec.getMGFParameters();
            if (!(mgfParameters instanceof MGF1ParameterSpec)) {
                throw new InvalidAlgorithmParameterException("Only support MGF1ParameterSpec");
            }
            final MGF1ParameterSpec mgf1ParameterSpec = (MGF1ParameterSpec)mgfParameters;
            String replaceAll = pssParameterSpec.getDigestAlgorithm().toLowerCase(Locale.ROOT).replaceAll("-", "");
            if (replaceAll.equals("sha")) {
                replaceAll = "sha1";
            }
            String replaceAll2 = mgf1ParameterSpec.getDigestAlgorithm().toLowerCase(Locale.ROOT).replaceAll("-", "");
            if (replaceAll2.equals("sha")) {
                replaceAll2 = "sha1";
            }
            if (!replaceAll2.equals(replaceAll)) {
                throw new InvalidAlgorithmParameterException("MGF1 hash must be the same as message hash");
            }
            return pssParameterSpec;
        }
    }
}
