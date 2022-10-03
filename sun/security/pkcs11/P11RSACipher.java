package sun.security.pkcs11;

import sun.security.pkcs11.wrapper.CK_VERSION;
import javax.crypto.SecretKey;
import sun.security.pkcs11.wrapper.CK_ATTRIBUTE;
import sun.security.util.KeyUtil;
import javax.crypto.ShortBufferException;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import sun.security.pkcs11.wrapper.CK_MECHANISM;
import sun.security.pkcs11.wrapper.PKCS11;
import java.security.ProviderException;
import java.security.InvalidAlgorithmParameterException;
import sun.security.internal.spec.TlsRsaPremasterSecretParameterSpec;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.AlgorithmParameters;
import javax.crypto.NoSuchPaddingException;
import java.util.Locale;
import java.security.NoSuchAlgorithmException;
import sun.security.pkcs11.wrapper.PKCS11Exception;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.CipherSpi;

final class P11RSACipher extends CipherSpi
{
    private static final int PKCS1_MIN_PADDING_LENGTH = 11;
    private static final byte[] B0;
    private static final int MODE_ENCRYPT = 1;
    private static final int MODE_DECRYPT = 2;
    private static final int MODE_SIGN = 3;
    private static final int MODE_VERIFY = 4;
    private static final int PAD_NONE = 1;
    private static final int PAD_PKCS1 = 2;
    private final Token token;
    private final String algorithm;
    private final long mechanism;
    private Session session;
    private int mode;
    private int padType;
    private byte[] buffer;
    private int bufOfs;
    private P11Key p11Key;
    private boolean initialized;
    private int maxInputSize;
    private int outputSize;
    private AlgorithmParameterSpec spec;
    private SecureRandom random;
    
    P11RSACipher(final Token token, final String s, final long mechanism) throws PKCS11Exception {
        this.spec = null;
        this.token = token;
        this.algorithm = "RSA";
        this.mechanism = mechanism;
    }
    
    @Override
    protected void engineSetMode(final String s) throws NoSuchAlgorithmException {
        if (!s.equalsIgnoreCase("ECB")) {
            throw new NoSuchAlgorithmException("Unsupported mode " + s);
        }
    }
    
    @Override
    protected void engineSetPadding(final String s) throws NoSuchPaddingException {
        final String lowerCase = s.toLowerCase(Locale.ENGLISH);
        if (lowerCase.equals("pkcs1padding")) {
            this.padType = 2;
        }
        else {
            if (!lowerCase.equals("nopadding")) {
                throw new NoSuchPaddingException("Unsupported padding " + s);
            }
            this.padType = 1;
        }
    }
    
    @Override
    protected int engineGetBlockSize() {
        return 0;
    }
    
    @Override
    protected int engineGetOutputSize(final int n) {
        return this.outputSize;
    }
    
    @Override
    protected byte[] engineGetIV() {
        return null;
    }
    
    @Override
    protected AlgorithmParameters engineGetParameters() {
        return null;
    }
    
    @Override
    protected void engineInit(final int n, final Key key, final SecureRandom secureRandom) throws InvalidKeyException {
        this.implInit(n, key);
    }
    
    @Override
    protected void engineInit(final int n, final Key key, final AlgorithmParameterSpec spec, final SecureRandom random) throws InvalidKeyException, InvalidAlgorithmParameterException {
        if (spec != null) {
            if (!(spec instanceof TlsRsaPremasterSecretParameterSpec)) {
                throw new InvalidAlgorithmParameterException("Parameters not supported");
            }
            this.spec = spec;
            this.random = random;
        }
        this.implInit(n, key);
    }
    
    @Override
    protected void engineInit(final int n, final Key key, final AlgorithmParameters algorithmParameters, final SecureRandom secureRandom) throws InvalidKeyException, InvalidAlgorithmParameterException {
        if (algorithmParameters != null) {
            throw new InvalidAlgorithmParameterException("Parameters not supported");
        }
        this.implInit(n, key);
    }
    
    private void implInit(final int n, final Key key) throws InvalidKeyException {
        this.reset(true);
        this.p11Key = P11KeyFactory.convertKey(this.token, key, this.algorithm);
        boolean b;
        if (n == 1) {
            b = true;
        }
        else if (n == 2) {
            b = false;
        }
        else if (n == 3) {
            if (!this.p11Key.isPublic()) {
                throw new InvalidKeyException("Wrap has to be used with public keys");
            }
            return;
        }
        else {
            if (n != 4) {
                throw new InvalidKeyException("Unsupported mode: " + n);
            }
            if (!this.p11Key.isPrivate()) {
                throw new InvalidKeyException("Unwrap has to be used with private keys");
            }
            return;
        }
        if (this.p11Key.isPublic()) {
            this.mode = (b ? 1 : 4);
        }
        else {
            if (!this.p11Key.isPrivate()) {
                throw new InvalidKeyException("Unknown key type: " + this.p11Key);
            }
            this.mode = (b ? 3 : 2);
        }
        final int outputSize = this.p11Key.length() + 7 >> 3;
        this.outputSize = outputSize;
        this.buffer = new byte[outputSize];
        this.maxInputSize = ((this.padType == 2 && b) ? (outputSize - 11) : outputSize);
        try {
            this.initialize();
        }
        catch (final PKCS11Exception ex) {
            throw new InvalidKeyException("init() failed", ex);
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
            this.session = this.token.releaseSession(this.session);
        }
    }
    
    private void cancelOperation() {
        this.token.ensureValid();
        try {
            final PKCS11 p11 = this.token.p11;
            final int maxInputSize = this.maxInputSize;
            final int length = this.buffer.length;
            final long id = this.session.id();
            switch (this.mode) {
                case 1: {
                    p11.C_Encrypt(id, 0L, this.buffer, 0, maxInputSize, 0L, this.buffer, 0, length);
                    break;
                }
                case 2: {
                    p11.C_Decrypt(id, 0L, this.buffer, 0, maxInputSize, 0L, this.buffer, 0, length);
                    break;
                }
                case 3: {
                    p11.C_Sign(id, new byte[this.maxInputSize]);
                    break;
                }
                case 4: {
                    p11.C_VerifyRecover(id, this.buffer, 0, maxInputSize, this.buffer, 0, length);
                    break;
                }
                default: {
                    throw new ProviderException("internal error");
                }
            }
        }
        catch (final PKCS11Exception ex) {}
    }
    
    private void ensureInitialized() throws PKCS11Exception {
        this.token.ensureValid();
        if (!this.initialized) {
            this.initialize();
        }
    }
    
    private void initialize() throws PKCS11Exception {
        if (this.p11Key == null) {
            throw new ProviderException("Operation cannot be performed without calling engineInit first");
        }
        final long keyID = this.p11Key.getKeyID();
        try {
            if (this.session == null) {
                this.session = this.token.getOpSession();
            }
            final PKCS11 p11 = this.token.p11;
            final CK_MECHANISM ck_MECHANISM = new CK_MECHANISM(this.mechanism);
            switch (this.mode) {
                case 1: {
                    p11.C_EncryptInit(this.session.id(), ck_MECHANISM, keyID);
                    break;
                }
                case 2: {
                    p11.C_DecryptInit(this.session.id(), ck_MECHANISM, keyID);
                    break;
                }
                case 3: {
                    p11.C_SignInit(this.session.id(), ck_MECHANISM, keyID);
                    break;
                }
                case 4: {
                    p11.C_VerifyRecoverInit(this.session.id(), ck_MECHANISM, keyID);
                    break;
                }
                default: {
                    throw new AssertionError((Object)"internal error");
                }
            }
            this.bufOfs = 0;
            this.initialized = true;
        }
        catch (final PKCS11Exception ex) {
            this.p11Key.releaseKeyID();
            this.session = this.token.releaseSession(this.session);
            throw ex;
        }
    }
    
    private void implUpdate(final byte[] array, final int n, final int n2) {
        try {
            this.ensureInitialized();
        }
        catch (final PKCS11Exception ex) {
            throw new ProviderException("update() failed", ex);
        }
        if (n2 == 0 || array == null) {
            return;
        }
        if (this.bufOfs + n2 > this.maxInputSize) {
            this.bufOfs = this.maxInputSize + 1;
            return;
        }
        System.arraycopy(array, n, this.buffer, this.bufOfs, n2);
        this.bufOfs += n2;
    }
    
    private int implDoFinal(final byte[] array, final int n, final int n2) throws BadPaddingException, IllegalBlockSizeException {
        if (this.bufOfs > this.maxInputSize) {
            this.reset(true);
            throw new IllegalBlockSizeException("Data must not be longer than " + this.maxInputSize + " bytes");
        }
        try {
            this.ensureInitialized();
            final PKCS11 p3 = this.token.p11;
            int n3 = 0;
            switch (this.mode) {
                case 1: {
                    n3 = p3.C_Encrypt(this.session.id(), 0L, this.buffer, 0, this.bufOfs, 0L, array, n, n2);
                    break;
                }
                case 2: {
                    n3 = p3.C_Decrypt(this.session.id(), 0L, this.buffer, 0, this.bufOfs, 0L, array, n, n2);
                    break;
                }
                case 3: {
                    final byte[] array2 = new byte[this.bufOfs];
                    System.arraycopy(this.buffer, 0, array2, 0, this.bufOfs);
                    final byte[] c_Sign = p3.C_Sign(this.session.id(), array2);
                    if (c_Sign.length > n2) {
                        throw new BadPaddingException("Output buffer (" + n2 + ") is too small to hold the produced data (" + c_Sign.length + ")");
                    }
                    System.arraycopy(c_Sign, 0, array, n, c_Sign.length);
                    n3 = c_Sign.length;
                    break;
                }
                case 4: {
                    n3 = p3.C_VerifyRecover(this.session.id(), this.buffer, 0, this.bufOfs, array, n, n2);
                    break;
                }
                default: {
                    throw new ProviderException("internal error");
                }
            }
            return n3;
        }
        catch (final PKCS11Exception ex) {
            throw (BadPaddingException)new BadPaddingException("doFinal() failed").initCause(ex);
        }
        finally {
            this.reset(false);
        }
    }
    
    @Override
    protected byte[] engineUpdate(final byte[] array, final int n, final int n2) {
        this.implUpdate(array, n, n2);
        return P11RSACipher.B0;
    }
    
    @Override
    protected int engineUpdate(final byte[] array, final int n, final int n2, final byte[] array2, final int n3) throws ShortBufferException {
        this.implUpdate(array, n, n2);
        return 0;
    }
    
    @Override
    protected byte[] engineDoFinal(final byte[] array, final int n, final int n2) throws IllegalBlockSizeException, BadPaddingException {
        this.implUpdate(array, n, n2);
        final int implDoFinal = this.implDoFinal(this.buffer, 0, this.buffer.length);
        final byte[] array2 = new byte[implDoFinal];
        System.arraycopy(this.buffer, 0, array2, 0, implDoFinal);
        return array2;
    }
    
    @Override
    protected int engineDoFinal(final byte[] array, final int n, final int n2, final byte[] array2, final int n3) throws ShortBufferException, IllegalBlockSizeException, BadPaddingException {
        this.implUpdate(array, n, n2);
        return this.implDoFinal(array2, n3, array2.length - n3);
    }
    
    private byte[] doFinal() throws BadPaddingException, IllegalBlockSizeException {
        final byte[] array = new byte[2048];
        final int implDoFinal = this.implDoFinal(array, 0, array.length);
        final byte[] array2 = new byte[implDoFinal];
        System.arraycopy(array, 0, array2, 0, implDoFinal);
        return array2;
    }
    
    @Override
    protected byte[] engineWrap(final Key key) throws InvalidKeyException, IllegalBlockSizeException {
        final String algorithm = key.getAlgorithm();
        P11Key convertKey;
        try {
            convertKey = P11SecretKeyFactory.convertKey(this.token, key, algorithm);
        }
        catch (final InvalidKeyException ex) {
            final byte[] encoded = key.getEncoded();
            if (encoded == null) {
                throw new InvalidKeyException("wrap() failed, no encoding available", ex);
            }
            this.implInit(1, this.p11Key);
            this.implUpdate(encoded, 0, encoded.length);
            try {
                return this.doFinal();
            }
            catch (final BadPaddingException ex2) {
                throw new InvalidKeyException("wrap() failed", ex2);
            }
            finally {
                this.implInit(3, this.p11Key);
            }
        }
        Session opSession = null;
        final long keyID = this.p11Key.getKeyID();
        final long keyID2 = convertKey.getKeyID();
        try {
            opSession = this.token.getOpSession();
            return this.token.p11.C_WrapKey(opSession.id(), new CK_MECHANISM(this.mechanism), keyID, keyID2);
        }
        catch (final PKCS11Exception ex3) {
            throw new InvalidKeyException("wrap() failed", ex3);
        }
        finally {
            this.p11Key.releaseKeyID();
            convertKey.releaseKeyID();
            this.token.releaseSession(opSession);
        }
    }
    
    @Override
    protected Key engineUnwrap(final byte[] array, final String s, final int n) throws InvalidKeyException, NoSuchAlgorithmException {
        final boolean equals = s.equals("TlsRsaPremasterSecret");
        PKCS11Exception ex = null;
        if (this.token.supportsRawSecretKeyImport()) {
            this.implInit(2, this.p11Key);
            try {
                if (array.length > this.maxInputSize) {
                    throw new InvalidKeyException("Key is too long for unwrapping");
                }
                byte[] array2 = null;
                this.implUpdate(array, 0, array.length);
                try {
                    array2 = this.doFinal();
                }
                catch (final BadPaddingException ex2) {
                    if (!equals) {
                        throw new InvalidKeyException("Unwrapping failed", ex2);
                    }
                    ex = (PKCS11Exception)ex2;
                }
                catch (final IllegalBlockSizeException ex3) {
                    throw new InvalidKeyException("Unwrapping failed", ex3);
                }
                if (equals) {
                    if (!(this.spec instanceof TlsRsaPremasterSecretParameterSpec)) {
                        throw new IllegalStateException("No TlsRsaPremasterSecretParameterSpec specified");
                    }
                    final TlsRsaPremasterSecretParameterSpec tlsRsaPremasterSecretParameterSpec = (TlsRsaPremasterSecretParameterSpec)this.spec;
                    array2 = KeyUtil.checkTlsPreMasterSecretKey(tlsRsaPremasterSecretParameterSpec.getClientVersion(), tlsRsaPremasterSecretParameterSpec.getServerVersion(), this.random, array2, ex != null);
                }
                return ConstructKeys.constructKey(array2, s, n);
            }
            finally {
                this.implInit(4, this.p11Key);
            }
        }
        Session objSession = null;
        SecretKey secretKey = null;
        final long keyID = this.p11Key.getKeyID();
        try {
            try {
                objSession = this.token.getObjSession();
                final long n2 = 16L;
                final CK_ATTRIBUTE[] attributes = this.token.getAttributes("import", 4L, n2, new CK_ATTRIBUTE[] { new CK_ATTRIBUTE(0L, 4L), new CK_ATTRIBUTE(256L, n2) });
                secretKey = P11Key.secretKey(objSession, this.token.p11.C_UnwrapKey(objSession.id(), new CK_MECHANISM(this.mechanism), keyID, array, attributes), s, 384, attributes);
            }
            catch (final PKCS11Exception ex4) {
                if (!equals) {
                    throw new InvalidKeyException("unwrap() failed", ex4);
                }
                ex = ex4;
            }
            if (equals) {
                final TlsRsaPremasterSecretParameterSpec tlsRsaPremasterSecretParameterSpec2 = (TlsRsaPremasterSecretParameterSpec)this.spec;
                secretKey = polishPreMasterSecretKey(this.token, objSession, ex, secretKey, tlsRsaPremasterSecretParameterSpec2.getClientVersion(), tlsRsaPremasterSecretParameterSpec2.getServerVersion());
            }
            return secretKey;
        }
        finally {
            this.p11Key.releaseKeyID();
            this.token.releaseSession(objSession);
        }
    }
    
    @Override
    protected int engineGetKeySize(final Key key) throws InvalidKeyException {
        return P11KeyFactory.convertKey(this.token, key, this.algorithm).length();
    }
    
    private static SecretKey polishPreMasterSecretKey(final Token token, final Session session, final Exception ex, final SecretKey secretKey, final int n, final int n2) {
        final CK_VERSION ck_VERSION = new CK_VERSION(n >>> 8 & 0xFF, n & 0xFF);
        try {
            final CK_ATTRIBUTE[] attributes = token.getAttributes("generate", 4L, 16L, new CK_ATTRIBUTE[0]);
            final SecretKey secretKey2 = P11Key.secretKey(session, token.p11.C_GenerateKey(session.id(), new CK_MECHANISM(880L, ck_VERSION), attributes), "TlsRsaPremasterSecret", 384, attributes);
        }
        catch (final PKCS11Exception ex2) {
            throw new ProviderException("Could not generate premaster secret", ex2);
        }
        SecretKey secretKey2;
        return (ex == null) ? secretKey : secretKey2;
    }
    
    static {
        B0 = new byte[0];
    }
}
