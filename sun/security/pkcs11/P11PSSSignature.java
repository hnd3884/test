package sun.security.pkcs11;

import java.security.GeneralSecurityException;
import java.security.AlgorithmParameters;
import java.security.InvalidParameterException;
import sun.nio.ch.DirectBuffer;
import java.nio.ByteBuffer;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.InvalidAlgorithmParameterException;
import sun.security.pkcs11.wrapper.CK_MECHANISM_INFO;
import java.security.interfaces.RSAKey;
import java.security.InvalidKeyException;
import java.security.Key;
import sun.security.pkcs11.wrapper.CK_RSA_PKCS_PSS_PARAMS;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.MGF1ParameterSpec;
import java.security.SignatureException;
import sun.security.pkcs11.wrapper.PKCS11Exception;
import java.security.NoSuchAlgorithmException;
import java.security.ProviderException;
import java.security.MessageDigest;
import java.security.spec.PSSParameterSpec;
import sun.security.pkcs11.wrapper.CK_MECHANISM;
import java.util.Hashtable;
import java.security.SignatureSpi;

final class P11PSSSignature extends SignatureSpi
{
    private static final boolean DEBUG = false;
    private static final Hashtable<String, Integer> DIGEST_LENGTHS;
    private final Token token;
    private final String algorithm;
    private static final String KEY_ALGO = "RSA";
    private final CK_MECHANISM mechanism;
    private final int type;
    private P11Key p11Key;
    private PSSParameterSpec sigParams;
    private boolean isActive;
    private final String mdAlg;
    private MessageDigest md;
    private Session session;
    private int mode;
    private boolean initialized;
    private final byte[] buffer;
    private int bytesProcessed;
    private static final int M_SIGN = 1;
    private static final int M_VERIFY = 2;
    private static final int T_DIGEST = 1;
    private static final int T_UPDATE = 2;
    
    private static boolean isDigestEqual(final String s, String string) {
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
    
    P11PSSSignature(final Token token, final String algorithm, final long n) throws NoSuchAlgorithmException, PKCS11Exception {
        this.p11Key = null;
        this.sigParams = null;
        this.isActive = false;
        this.md = null;
        this.initialized = false;
        this.buffer = new byte[1];
        this.bytesProcessed = 0;
        this.token = token;
        this.algorithm = algorithm;
        this.mechanism = new CK_MECHANISM(n);
        final int index = algorithm.indexOf("with");
        this.mdAlg = ((index == -1) ? null : algorithm.substring(0, index));
        switch ((int)n) {
            case 14:
            case 67:
            case 68:
            case 69:
            case 71: {
                this.type = 2;
                break;
            }
            case 13: {
                this.type = 1;
                break;
            }
            default: {
                throw new ProviderException("Unsupported mechanism: " + n);
            }
        }
        this.md = null;
    }
    
    private void ensureInitialized() throws SignatureException {
        this.token.ensureValid();
        if (this.p11Key == null) {
            throw new SignatureException("Missing key");
        }
        if (this.sigParams == null) {
            if (this.mdAlg == null) {
                throw new SignatureException("Parameters required for RSASSA-PSS signature");
            }
            this.sigParams = new PSSParameterSpec(this.mdAlg, "MGF1", new MGF1ParameterSpec(this.mdAlg), P11PSSSignature.DIGEST_LENGTHS.get(this.mdAlg), 1);
            this.mechanism.setParameter(new CK_RSA_PKCS_PSS_PARAMS(this.mdAlg, "MGF1", this.mdAlg, P11PSSSignature.DIGEST_LENGTHS.get(this.mdAlg)));
        }
        if (!this.initialized) {
            this.initialize();
        }
    }
    
    private void reset(final boolean b) {
        if (!this.initialized) {
            return;
        }
        this.initialized = false;
        try {
            if (this.session == null) {
                return;
            }
            if (b && this.token.explicitCancel) {
                this.cancelOperation();
            }
        }
        finally {
            this.p11Key.releaseKeyID();
            this.mechanism.freeHandle();
            this.session = this.token.releaseSession(this.session);
            this.isActive = false;
        }
    }
    
    private void cancelOperation() {
        this.token.ensureValid();
        try {
            if (this.mode == 1) {
                if (this.type == 2) {
                    this.token.p11.C_SignFinal(this.session.id(), 0);
                }
                else {
                    this.token.p11.C_Sign(this.session.id(), (this.md == null) ? new byte[0] : this.md.digest());
                }
            }
            else {
                final byte[] array = new byte[this.p11Key.length() + 7 >> 3];
                if (this.type == 2) {
                    this.token.p11.C_VerifyFinal(this.session.id(), array);
                }
                else {
                    this.token.p11.C_Verify(this.session.id(), (this.md == null) ? new byte[0] : this.md.digest(), array);
                }
            }
        }
        catch (final PKCS11Exception ex) {
            if (this.mode == 1) {
                throw new ProviderException("cancel failed", ex);
            }
        }
    }
    
    private void initialize() {
        if (this.p11Key == null) {
            throw new ProviderException("No Key found, call initSign/initVerify first");
        }
        final long keyID = this.p11Key.getKeyID();
        try {
            if (this.session == null) {
                this.session = this.token.getOpSession();
            }
            if (this.mode == 1) {
                this.token.p11.C_SignInit(this.session.id(), this.mechanism, keyID);
            }
            else {
                this.token.p11.C_VerifyInit(this.session.id(), this.mechanism, keyID);
            }
        }
        catch (final PKCS11Exception ex) {
            this.p11Key.releaseKeyID();
            this.session = this.token.releaseSession(this.session);
            throw new ProviderException("Initialization failed", ex);
        }
        if (this.bytesProcessed != 0) {
            this.bytesProcessed = 0;
            if (this.md != null) {
                this.md.reset();
            }
        }
        this.initialized = true;
        this.isActive = false;
    }
    
    private void checkKeySize(final Key key) throws InvalidKeyException {
        if (!key.getAlgorithm().equals("RSA")) {
            throw new InvalidKeyException("Only RSA keys are supported");
        }
        CK_MECHANISM_INFO mechanismInfo = null;
        try {
            mechanismInfo = this.token.getMechanismInfo(this.mechanism.mechanism);
        }
        catch (final PKCS11Exception ex) {}
        int n = 0;
        if (mechanismInfo != null) {
            if (key instanceof P11Key) {
                n = ((P11Key)key).length() + 7 >> 3;
            }
            else {
                if (!(key instanceof RSAKey)) {
                    throw new InvalidKeyException("Unrecognized key type " + key);
                }
                n = ((RSAKey)key).getModulus().bitLength() >> 3;
            }
            if (mechanismInfo.iMinKeySize != 0 && n < mechanismInfo.iMinKeySize >> 3) {
                throw new InvalidKeyException("RSA key must be at least " + mechanismInfo.iMinKeySize + " bits");
            }
            if (mechanismInfo.iMaxKeySize != Integer.MAX_VALUE && n > mechanismInfo.iMaxKeySize >> 3) {
                throw new InvalidKeyException("RSA key must be at most " + mechanismInfo.iMaxKeySize + " bits");
            }
        }
        if (this.sigParams != null) {
            final int addExact = Math.addExact(Math.addExact(this.sigParams.getSaltLength(), P11PSSSignature.DIGEST_LENGTHS.get(this.sigParams.getDigestAlgorithm())), 2);
            if (n < addExact) {
                throw new InvalidKeyException("Key is too short for current params, need min " + addExact);
            }
        }
    }
    
    private void setSigParams(final AlgorithmParameterSpec algorithmParameterSpec) throws InvalidAlgorithmParameterException {
        if (algorithmParameterSpec == null) {
            throw new InvalidAlgorithmParameterException("PSS Parameter required");
        }
        if (!(algorithmParameterSpec instanceof PSSParameterSpec)) {
            throw new InvalidAlgorithmParameterException("Only PSSParameterSpec is supported");
        }
        final PSSParameterSpec sigParams = (PSSParameterSpec)algorithmParameterSpec;
        if (sigParams == this.sigParams) {
            return;
        }
        final String digestAlgorithm = sigParams.getDigestAlgorithm();
        if (this.mdAlg != null && !isDigestEqual(digestAlgorithm, this.mdAlg)) {
            throw new InvalidAlgorithmParameterException("Digest algorithm in Signature parameters must be " + this.mdAlg);
        }
        final Integer n = P11PSSSignature.DIGEST_LENGTHS.get(digestAlgorithm);
        if (n == null) {
            throw new InvalidAlgorithmParameterException("Unsupported digest algorithm in Signature parameters: " + digestAlgorithm);
        }
        if (!sigParams.getMGFAlgorithm().equalsIgnoreCase("MGF1")) {
            throw new InvalidAlgorithmParameterException("Only supports MGF1");
        }
        String digestAlgorithm2 = digestAlgorithm;
        final AlgorithmParameterSpec mgfParameters = sigParams.getMGFParameters();
        if (mgfParameters != null) {
            if (!(mgfParameters instanceof MGF1ParameterSpec)) {
                throw new InvalidAlgorithmParameterException("Only MGF1ParameterSpec is supported");
            }
            digestAlgorithm2 = ((MGF1ParameterSpec)mgfParameters).getDigestAlgorithm();
        }
        if (sigParams.getTrailerField() != 1) {
            throw new InvalidAlgorithmParameterException("Only supports TrailerFieldBC(1)");
        }
        final int saltLength = sigParams.getSaltLength();
        if (this.p11Key != null) {
            final int n2 = (this.p11Key.length() + 7 >> 3) - n - 2;
            if (n2 < 0 || saltLength > n2) {
                throw new InvalidAlgorithmParameterException("Invalid with current key size");
            }
        }
        try {
            this.mechanism.setParameter(new CK_RSA_PKCS_PSS_PARAMS(digestAlgorithm, "MGF1", digestAlgorithm2, saltLength));
            this.sigParams = sigParams;
        }
        catch (final IllegalArgumentException ex) {
            throw new InvalidAlgorithmParameterException(ex);
        }
    }
    
    @Override
    protected void engineInitVerify(final PublicKey publicKey) throws InvalidKeyException {
        if (publicKey == null) {
            throw new InvalidKeyException("Key must not be null");
        }
        if (publicKey != this.p11Key) {
            this.checkKeySize(publicKey);
        }
        this.reset(true);
        this.mode = 2;
        this.p11Key = P11KeyFactory.convertKey(this.token, publicKey, "RSA");
    }
    
    @Override
    protected void engineInitSign(final PrivateKey privateKey) throws InvalidKeyException {
        if (privateKey == null) {
            throw new InvalidKeyException("Key must not be null");
        }
        if (privateKey != this.p11Key) {
            this.checkKeySize(privateKey);
        }
        this.reset(true);
        this.mode = 1;
        this.p11Key = P11KeyFactory.convertKey(this.token, privateKey, "RSA");
    }
    
    @Override
    protected void engineUpdate(final byte b) throws SignatureException {
        this.ensureInitialized();
        this.isActive = true;
        this.buffer[0] = b;
        this.engineUpdate(this.buffer, 0, 1);
    }
    
    @Override
    protected void engineUpdate(final byte[] array, final int n, final int n2) throws SignatureException {
        this.ensureInitialized();
        if (n2 == 0) {
            return;
        }
        if (n2 + this.bytesProcessed < 0) {
            throw new ProviderException("Processed bytes limits exceeded.");
        }
        this.isActive = true;
        switch (this.type) {
            case 2: {
                try {
                    if (this.mode == 1) {
                        System.out.println(this + ": Calling C_SignUpdate");
                        this.token.p11.C_SignUpdate(this.session.id(), 0L, array, n, n2);
                    }
                    else {
                        System.out.println(this + ": Calling C_VerfifyUpdate");
                        this.token.p11.C_VerifyUpdate(this.session.id(), 0L, array, n, n2);
                    }
                    this.bytesProcessed += n2;
                    break;
                }
                catch (final PKCS11Exception ex) {
                    this.reset(false);
                    throw new ProviderException(ex);
                }
            }
            case 1: {
                if (this.md == null) {
                    throw new ProviderException("PSS Parameters required");
                }
                this.md.update(array, n, n2);
                this.bytesProcessed += n2;
                break;
            }
            default: {
                throw new ProviderException("Internal error");
            }
        }
    }
    
    @Override
    protected void engineUpdate(final ByteBuffer byteBuffer) {
        try {
            this.ensureInitialized();
        }
        catch (final SignatureException ex) {
            throw new ProviderException(ex);
        }
        final int remaining = byteBuffer.remaining();
        if (remaining <= 0) {
            return;
        }
        this.isActive = true;
        switch (this.type) {
            case 2: {
                if (!(byteBuffer instanceof DirectBuffer)) {
                    super.engineUpdate(byteBuffer);
                    return;
                }
                final long address = ((DirectBuffer)byteBuffer).address();
                final int position = byteBuffer.position();
                try {
                    if (this.mode == 1) {
                        System.out.println(this + ": Calling C_SignUpdate");
                        this.token.p11.C_SignUpdate(this.session.id(), address + position, null, 0, remaining);
                    }
                    else {
                        System.out.println(this + ": Calling C_VerifyUpdate");
                        this.token.p11.C_VerifyUpdate(this.session.id(), address + position, null, 0, remaining);
                    }
                    this.bytesProcessed += remaining;
                    byteBuffer.position(position + remaining);
                    break;
                }
                catch (final PKCS11Exception ex2) {
                    this.reset(false);
                    throw new ProviderException("Update failed", ex2);
                }
            }
            case 1: {
                if (this.md == null) {
                    throw new ProviderException("PSS Parameters required");
                }
                this.md.update(byteBuffer);
                this.bytesProcessed += remaining;
                break;
            }
            default: {
                this.reset(false);
                throw new ProviderException("Internal error");
            }
        }
    }
    
    @Override
    protected byte[] engineSign() throws SignatureException {
        this.ensureInitialized();
        boolean b = true;
        try {
            byte[] array;
            if (this.type == 2) {
                array = this.token.p11.C_SignFinal(this.session.id(), 0);
            }
            else {
                if (this.md == null) {
                    throw new ProviderException("PSS Parameters required");
                }
                array = this.token.p11.C_Sign(this.session.id(), this.md.digest());
            }
            b = false;
            return array;
        }
        catch (final PKCS11Exception ex) {
            b = false;
            throw new ProviderException(ex);
        }
        catch (final ProviderException ex2) {
            throw ex2;
        }
        finally {
            this.reset(b);
        }
    }
    
    @Override
    protected boolean engineVerify(final byte[] array) throws SignatureException {
        this.ensureInitialized();
        boolean b = true;
        try {
            if (this.type == 2) {
                this.token.p11.C_VerifyFinal(this.session.id(), array);
            }
            else {
                if (this.md == null) {
                    throw new ProviderException("PSS Parameters required");
                }
                this.token.p11.C_Verify(this.session.id(), this.md.digest(), array);
            }
            b = false;
            return true;
        }
        catch (final PKCS11Exception ex) {
            b = false;
            final long errorCode = ex.getErrorCode();
            if (errorCode == 192L) {
                return false;
            }
            if (errorCode == 193L) {
                return false;
            }
            if (errorCode == 33L) {
                return false;
            }
            throw new ProviderException(ex);
        }
        catch (final ProviderException ex2) {
            throw ex2;
        }
        finally {
            this.reset(b);
        }
    }
    
    @Override
    protected void engineSetParameter(final String s, final Object o) throws InvalidParameterException {
        throw new UnsupportedOperationException("setParameter() not supported");
    }
    
    @Override
    protected void engineSetParameter(final AlgorithmParameterSpec sigParams) throws InvalidAlgorithmParameterException {
        if (this.isActive) {
            throw new ProviderException("Cannot set parameters during operations");
        }
        this.setSigParams(sigParams);
        if (this.type == 1) {
            try {
                this.md = MessageDigest.getInstance(this.sigParams.getDigestAlgorithm());
            }
            catch (final NoSuchAlgorithmException ex) {
                throw new InvalidAlgorithmParameterException(ex);
            }
        }
    }
    
    @Override
    protected Object engineGetParameter(final String s) throws InvalidParameterException {
        throw new UnsupportedOperationException("getParameter() not supported");
    }
    
    @Override
    protected AlgorithmParameters engineGetParameters() {
        if (this.sigParams != null) {
            try {
                final AlgorithmParameters instance = AlgorithmParameters.getInstance("RSASSA-PSS");
                instance.init(this.sigParams);
                return instance;
            }
            catch (final GeneralSecurityException ex) {
                throw new RuntimeException(ex);
            }
        }
        return null;
    }
    
    static {
        (DIGEST_LENGTHS = new Hashtable<String, Integer>()).put("SHA-1", 20);
        P11PSSSignature.DIGEST_LENGTHS.put("SHA", 20);
        P11PSSSignature.DIGEST_LENGTHS.put("SHA1", 20);
        P11PSSSignature.DIGEST_LENGTHS.put("SHA-224", 28);
        P11PSSSignature.DIGEST_LENGTHS.put("SHA224", 28);
        P11PSSSignature.DIGEST_LENGTHS.put("SHA-256", 32);
        P11PSSSignature.DIGEST_LENGTHS.put("SHA256", 32);
        P11PSSSignature.DIGEST_LENGTHS.put("SHA-384", 48);
        P11PSSSignature.DIGEST_LENGTHS.put("SHA384", 48);
        P11PSSSignature.DIGEST_LENGTHS.put("SHA-512", 64);
        P11PSSSignature.DIGEST_LENGTHS.put("SHA512", 64);
        P11PSSSignature.DIGEST_LENGTHS.put("SHA-512/224", 28);
        P11PSSSignature.DIGEST_LENGTHS.put("SHA512/224", 28);
        P11PSSSignature.DIGEST_LENGTHS.put("SHA-512/256", 32);
        P11PSSSignature.DIGEST_LENGTHS.put("SHA512/256", 32);
    }
}
