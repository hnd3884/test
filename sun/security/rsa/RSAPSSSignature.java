package sun.security.rsa;

import java.security.AlgorithmParameters;
import java.security.InvalidParameterException;
import java.security.DigestException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.MGF1ParameterSpec;
import javax.crypto.BadPaddingException;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.nio.ByteBuffer;
import java.security.InvalidAlgorithmParameterException;
import java.security.SignatureException;
import java.util.Arrays;
import java.security.spec.AlgorithmParameterSpec;
import sun.security.jca.JCAUtil;
import java.security.PrivateKey;
import java.security.interfaces.RSAKey;
import java.security.InvalidKeyException;
import java.security.PublicKey;
import java.security.ProviderException;
import java.security.SecureRandom;
import java.security.spec.PSSParameterSpec;
import java.security.interfaces.RSAPublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.MessageDigest;
import java.util.Hashtable;
import java.security.SignatureSpi;

public class RSAPSSSignature extends SignatureSpi
{
    private static final boolean DEBUG = false;
    private static final byte[] EIGHT_BYTES_OF_ZEROS;
    private static final Hashtable<String, Integer> DIGEST_LENGTHS;
    private MessageDigest md;
    private boolean digestReset;
    private RSAPrivateKey privKey;
    private RSAPublicKey pubKey;
    private PSSParameterSpec sigParams;
    private SecureRandom random;
    
    private boolean isDigestEqual(final String s, String string) {
        if (s == null || string == null) {
            return false;
        }
        if (string.indexOf("-") != -1) {
            return s.equalsIgnoreCase(string);
        }
        if (s.equals("SHA-1")) {
            return string.equalsIgnoreCase("SHA") || string.equalsIgnoreCase("SHA1");
        }
        final StringBuilder sb = new StringBuilder(string);
        if (string.regionMatches(true, 0, "SHA", 0, 3)) {
            string = sb.insert(3, "-").toString();
            return s.equalsIgnoreCase(string);
        }
        throw new ProviderException("Unsupported digest algorithm " + string);
    }
    
    public RSAPSSSignature() {
        this.digestReset = true;
        this.privKey = null;
        this.pubKey = null;
        this.sigParams = null;
        this.md = null;
    }
    
    @Override
    protected void engineInitVerify(final PublicKey publicKey) throws InvalidKeyException {
        if (!(publicKey instanceof RSAPublicKey)) {
            throw new InvalidKeyException("key must be RSAPublicKey");
        }
        this.pubKey = (RSAPublicKey)this.isValid((RSAKey)publicKey);
        this.privKey = null;
        this.resetDigest();
    }
    
    @Override
    protected void engineInitSign(final PrivateKey privateKey) throws InvalidKeyException {
        this.engineInitSign(privateKey, null);
    }
    
    @Override
    protected void engineInitSign(final PrivateKey privateKey, final SecureRandom secureRandom) throws InvalidKeyException {
        if (!(privateKey instanceof RSAPrivateKey)) {
            throw new InvalidKeyException("key must be RSAPrivateKey");
        }
        this.privKey = (RSAPrivateKey)this.isValid((RSAKey)privateKey);
        this.pubKey = null;
        this.random = ((secureRandom == null) ? JCAUtil.getSecureRandom() : secureRandom);
        this.resetDigest();
    }
    
    private static boolean isCompatible(final AlgorithmParameterSpec algorithmParameterSpec, final PSSParameterSpec pssParameterSpec) {
        if (algorithmParameterSpec == null) {
            return true;
        }
        if (!(algorithmParameterSpec instanceof PSSParameterSpec)) {
            return false;
        }
        if (pssParameterSpec == null) {
            return true;
        }
        final PSSParameterSpec pssParameterSpec2 = (PSSParameterSpec)algorithmParameterSpec;
        if (pssParameterSpec2.getSaltLength() > pssParameterSpec.getSaltLength()) {
            return false;
        }
        final PSSParameterSpec pssParameterSpec3 = new PSSParameterSpec(pssParameterSpec2.getDigestAlgorithm(), pssParameterSpec2.getMGFAlgorithm(), pssParameterSpec2.getMGFParameters(), pssParameterSpec.getSaltLength(), pssParameterSpec2.getTrailerField());
        final PSSParameters pssParameters = new PSSParameters();
        try {
            pssParameters.engineInit(pssParameterSpec3);
            final byte[] engineGetEncoded = pssParameters.engineGetEncoded();
            pssParameters.engineInit(pssParameterSpec);
            return Arrays.equals(engineGetEncoded, pssParameters.engineGetEncoded());
        }
        catch (final Exception ex) {
            return false;
        }
    }
    
    private RSAKey isValid(final RSAKey rsaKey) throws InvalidKeyException {
        try {
            rsaKey.getParams();
            if (!isCompatible(rsaKey.getParams(), this.sigParams)) {
                throw new InvalidKeyException("Key contains incompatible PSS parameter values");
            }
            if (this.sigParams != null) {
                final Integer n = RSAPSSSignature.DIGEST_LENGTHS.get(this.sigParams.getDigestAlgorithm());
                if (n == null) {
                    throw new ProviderException("Unsupported digest algo: " + this.sigParams.getDigestAlgorithm());
                }
                checkKeyLength(rsaKey, n, this.sigParams.getSaltLength());
            }
            return rsaKey;
        }
        catch (final SignatureException ex) {
            throw new InvalidKeyException(ex);
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
        if (pssParameterSpec == this.sigParams) {
            return pssParameterSpec;
        }
        final Object o = (this.privKey == null) ? this.pubKey : this.privKey;
        if (o != null && !isCompatible(((RSAKey)o).getParams(), pssParameterSpec)) {
            throw new InvalidAlgorithmParameterException("Signature parameters does not match key parameters");
        }
        if (!pssParameterSpec.getMGFAlgorithm().equalsIgnoreCase("MGF1")) {
            throw new InvalidAlgorithmParameterException("Only supports MGF1");
        }
        if (pssParameterSpec.getTrailerField() != 1) {
            throw new InvalidAlgorithmParameterException("Only supports TrailerFieldBC(1)");
        }
        final String digestAlgorithm = pssParameterSpec.getDigestAlgorithm();
        if (o != null) {
            try {
                checkKeyLength((RSAKey)o, RSAPSSSignature.DIGEST_LENGTHS.get(digestAlgorithm), pssParameterSpec.getSaltLength());
            }
            catch (final SignatureException ex) {
                throw new InvalidAlgorithmParameterException(ex);
            }
        }
        return pssParameterSpec;
    }
    
    private void ensureInit() throws SignatureException {
        if (((this.privKey == null) ? this.pubKey : this.privKey) == null) {
            throw new SignatureException("Missing key");
        }
        if (this.sigParams == null) {
            throw new SignatureException("Parameters required for RSASSA-PSS signatures");
        }
    }
    
    private static void checkKeyLength(final RSAKey rsaKey, final int n, final int n2) throws SignatureException {
        if (rsaKey != null) {
            final int n3 = getKeyLengthInBits(rsaKey) + 7 >> 3;
            final int addExact = Math.addExact(Math.addExact(n, n2), 2);
            if (n3 < addExact) {
                throw new SignatureException("Key is too short, need min " + addExact + " bytes");
            }
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
        this.ensureInit();
        this.md.update(b);
        this.digestReset = false;
    }
    
    @Override
    protected void engineUpdate(final byte[] array, final int n, final int n2) throws SignatureException {
        this.ensureInit();
        this.md.update(array, n, n2);
        this.digestReset = false;
    }
    
    @Override
    protected void engineUpdate(final ByteBuffer byteBuffer) {
        try {
            this.ensureInit();
        }
        catch (final SignatureException ex) {
            throw new RuntimeException(ex.getMessage());
        }
        this.md.update(byteBuffer);
        this.digestReset = false;
    }
    
    @Override
    protected byte[] engineSign() throws SignatureException {
        this.ensureInit();
        final byte[] digestValue = this.getDigestValue();
        try {
            return RSACore.rsa(this.encodeSignature(digestValue), this.privKey, true);
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
        this.ensureInit();
        try {
            if (array.length != RSACore.getByteLength(this.pubKey)) {
                throw new SignatureException("Signature length not correct: got " + array.length + " but was expecting " + RSACore.getByteLength(this.pubKey));
            }
            return this.decodeSignature(this.getDigestValue(), RSACore.rsa(array, this.pubKey));
        }
        catch (final BadPaddingException ex) {
            return false;
        }
        catch (final IOException ex2) {
            throw new SignatureException("Signature encoding error", ex2);
        }
        finally {
            this.resetDigest();
        }
    }
    
    private static int getKeyLengthInBits(final RSAKey rsaKey) {
        if (rsaKey != null) {
            return rsaKey.getModulus().bitLength();
        }
        return -1;
    }
    
    private byte[] encodeSignature(final byte[] array) throws IOException, DigestException {
        final AlgorithmParameterSpec mgfParameters = this.sigParams.getMGFParameters();
        String s;
        if (mgfParameters != null) {
            s = ((MGF1ParameterSpec)mgfParameters).getDigestAlgorithm();
        }
        else {
            s = this.md.getAlgorithm();
        }
        try {
            final int n = getKeyLengthInBits(this.privKey) - 1;
            final int n2 = n + 7 >> 3;
            final int digestLength = this.md.getDigestLength();
            final int n3 = n2 - digestLength - 1;
            final int saltLength = this.sigParams.getSaltLength();
            final byte[] array2 = new byte[n2];
            array2[n3 - saltLength - 1] = 1;
            array2[array2.length - 1] = -68;
            if (!this.digestReset) {
                throw new ProviderException("Digest should be reset");
            }
            this.md.update(RSAPSSSignature.EIGHT_BYTES_OF_ZEROS);
            this.digestReset = false;
            this.md.update(array);
            if (saltLength != 0) {
                final byte[] array3 = new byte[saltLength];
                this.random.nextBytes(array3);
                this.md.update(array3);
                System.arraycopy(array3, 0, array2, n3 - saltLength, saltLength);
            }
            this.md.digest(array2, n3, digestLength);
            this.digestReset = true;
            new MGF1(s).generateAndXor(array2, n3, digestLength, n3, array2, 0);
            final int n4 = (n2 << 3) - n;
            if (n4 != 0) {
                array2[0] &= (byte)(255 >>> n4);
            }
            return array2;
        }
        catch (final NoSuchAlgorithmException ex) {
            throw new IOException(ex.toString());
        }
    }
    
    private boolean decodeSignature(final byte[] array, final byte[] array2) throws IOException {
        final int length = array.length;
        final int saltLength = this.sigParams.getSaltLength();
        final int n = getKeyLengthInBits(this.pubKey) - 1;
        final int n2 = n + 7 >> 3;
        final int n3 = array2.length - n2;
        if (n3 == 1 && array2[0] != 0) {
            return false;
        }
        if (n2 < length + saltLength + 2) {
            return false;
        }
        if (array2[n3 + n2 - 1] != -68) {
            return false;
        }
        final int n4 = (n2 << 3) - n;
        if (n4 != 0 && (array2[n3] & (byte)(255 << 8 - n4)) != 0x0) {
            return false;
        }
        final AlgorithmParameterSpec mgfParameters = this.sigParams.getMGFParameters();
        String s;
        if (mgfParameters != null) {
            s = ((MGF1ParameterSpec)mgfParameters).getDigestAlgorithm();
        }
        else {
            s = this.md.getAlgorithm();
        }
        final int n5 = n2 - length - 1;
        try {
            new MGF1(s).generateAndXor(array2, n3 + n5, length, n5, array2, n3);
        }
        catch (final NoSuchAlgorithmException ex) {
            throw new IOException(ex.toString());
        }
        if (n4 != 0) {
            array2[n3] &= (byte)(255 >>> n4);
        }
        int i;
        for (i = n3; i < n3 + (n5 - saltLength - 1); ++i) {
            if (array2[i] != 0) {
                return false;
            }
        }
        if (array2[i] != 1) {
            return false;
        }
        this.md.update(RSAPSSSignature.EIGHT_BYTES_OF_ZEROS);
        this.digestReset = false;
        this.md.update(array);
        if (saltLength > 0) {
            this.md.update(array2, n3 + (n5 - saltLength), saltLength);
        }
        final byte[] digest = this.md.digest();
        this.digestReset = true;
        return MessageDigest.isEqual(digest, Arrays.copyOfRange(array2, n3 + n5, n3 + n2 - 1));
    }
    
    @Deprecated
    @Override
    protected void engineSetParameter(final String s, final Object o) throws InvalidParameterException {
        throw new UnsupportedOperationException("setParameter() not supported");
    }
    
    @Override
    protected void engineSetParameter(final AlgorithmParameterSpec algorithmParameterSpec) throws InvalidAlgorithmParameterException {
        this.sigParams = this.validateSigParams(algorithmParameterSpec);
        if (!this.digestReset) {
            throw new ProviderException("Cannot set parameters during operations");
        }
        final String digestAlgorithm = this.sigParams.getDigestAlgorithm();
        if (this.md != null) {
            if (this.md.getAlgorithm().equalsIgnoreCase(digestAlgorithm)) {
                return;
            }
        }
        try {
            this.md = MessageDigest.getInstance(digestAlgorithm);
        }
        catch (final NoSuchAlgorithmException ex) {
            throw new InvalidAlgorithmParameterException("Unsupported digest algorithm " + digestAlgorithm, ex);
        }
    }
    
    @Deprecated
    @Override
    protected Object engineGetParameter(final String s) throws InvalidParameterException {
        throw new UnsupportedOperationException("getParameter() not supported");
    }
    
    @Override
    protected AlgorithmParameters engineGetParameters() {
        AlgorithmParameters instance = null;
        if (this.sigParams != null) {
            try {
                instance = AlgorithmParameters.getInstance("RSASSA-PSS");
                instance.init(this.sigParams);
            }
            catch (final GeneralSecurityException ex) {
                throw new ProviderException(ex.getMessage());
            }
        }
        return instance;
    }
    
    static {
        EIGHT_BYTES_OF_ZEROS = new byte[8];
        (DIGEST_LENGTHS = new Hashtable<String, Integer>()).put("SHA-1", 20);
        RSAPSSSignature.DIGEST_LENGTHS.put("SHA", 20);
        RSAPSSSignature.DIGEST_LENGTHS.put("SHA1", 20);
        RSAPSSSignature.DIGEST_LENGTHS.put("SHA-224", 28);
        RSAPSSSignature.DIGEST_LENGTHS.put("SHA224", 28);
        RSAPSSSignature.DIGEST_LENGTHS.put("SHA-256", 32);
        RSAPSSSignature.DIGEST_LENGTHS.put("SHA256", 32);
        RSAPSSSignature.DIGEST_LENGTHS.put("SHA-384", 48);
        RSAPSSSignature.DIGEST_LENGTHS.put("SHA384", 48);
        RSAPSSSignature.DIGEST_LENGTHS.put("SHA-512", 64);
        RSAPSSSignature.DIGEST_LENGTHS.put("SHA512", 64);
        RSAPSSSignature.DIGEST_LENGTHS.put("SHA-512/224", 28);
        RSAPSSSignature.DIGEST_LENGTHS.put("SHA512/224", 28);
        RSAPSSSignature.DIGEST_LENGTHS.put("SHA-512/256", 32);
        RSAPSSSignature.DIGEST_LENGTHS.put("SHA512/256", 32);
    }
}
