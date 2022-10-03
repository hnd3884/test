package sun.security.pkcs11;

import sun.nio.ch.DirectBuffer;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import java.nio.ByteBuffer;
import javax.crypto.ShortBufferException;
import sun.security.pkcs11.wrapper.CK_MECHANISM;
import sun.security.pkcs11.wrapper.CK_GCM_PARAMS;
import java.util.Arrays;
import java.security.spec.InvalidParameterSpecException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.GeneralSecurityException;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.spec.GCMParameterSpec;
import java.security.AlgorithmParameters;
import javax.crypto.NoSuchPaddingException;
import java.util.Locale;
import java.security.NoSuchAlgorithmException;
import sun.security.pkcs11.wrapper.PKCS11Exception;
import java.security.ProviderException;
import sun.security.jca.JCAUtil;
import java.io.ByteArrayOutputStream;
import java.security.SecureRandom;
import javax.crypto.CipherSpi;

final class P11AEADCipher extends CipherSpi
{
    private static final int MODE_GCM = 10;
    private static final int GCM_DEFAULT_TAG_LEN = 16;
    private static final int GCM_DEFAULT_IV_LEN = 16;
    private static final String ALGO = "AES";
    private final Token token;
    private final long mechanism;
    private final int blockMode;
    private final int fixedKeySize;
    private Session session;
    private P11Key p11Key;
    private boolean initialized;
    private boolean encrypt;
    private byte[] iv;
    private int tagLen;
    private SecureRandom random;
    private ByteArrayOutputStream dataBuffer;
    private ByteArrayOutputStream aadBuffer;
    private boolean updateCalled;
    private boolean requireReinit;
    private P11Key lastEncKey;
    private byte[] lastEncIv;
    
    P11AEADCipher(final Token token, final String s, final long mechanism) throws PKCS11Exception, NoSuchAlgorithmException {
        this.session = null;
        this.p11Key = null;
        this.initialized = false;
        this.encrypt = true;
        this.iv = null;
        this.tagLen = -1;
        this.random = JCAUtil.getSecureRandom();
        this.dataBuffer = new ByteArrayOutputStream();
        this.aadBuffer = new ByteArrayOutputStream();
        this.updateCalled = false;
        this.requireReinit = false;
        this.lastEncKey = null;
        this.lastEncIv = null;
        this.token = token;
        this.mechanism = mechanism;
        final String[] split = s.split("/");
        if (split.length != 3) {
            throw new ProviderException("Unsupported Transformation format: " + s);
        }
        if (!split[0].startsWith("AES")) {
            throw new ProviderException("Only support AES for AEAD cipher mode");
        }
        final int index = split[0].indexOf(95);
        if (index != -1) {
            this.fixedKeySize = Integer.parseInt(split[0].substring(index + 1)) >> 3;
        }
        else {
            this.fixedKeySize = -1;
        }
        this.blockMode = this.parseMode(split[1]);
        if (!split[2].equals("NoPadding")) {
            throw new ProviderException("Only NoPadding is supported for AEAD cipher mode");
        }
    }
    
    @Override
    protected void engineSetMode(final String s) throws NoSuchAlgorithmException {
        throw new NoSuchAlgorithmException("Unsupported mode " + s);
    }
    
    private int parseMode(String upperCase) throws NoSuchAlgorithmException {
        upperCase = upperCase.toUpperCase(Locale.ENGLISH);
        if (upperCase.equals("GCM")) {
            return 10;
        }
        throw new NoSuchAlgorithmException("Unsupported mode " + upperCase);
    }
    
    @Override
    protected void engineSetPadding(final String s) throws NoSuchPaddingException {
        throw new NoSuchPaddingException("Unsupported padding " + s);
    }
    
    @Override
    protected int engineGetBlockSize() {
        return 16;
    }
    
    @Override
    protected int engineGetOutputSize(final int n) {
        return this.doFinalLength(n);
    }
    
    @Override
    protected byte[] engineGetIV() {
        return (byte[])((this.iv == null) ? null : ((byte[])this.iv.clone()));
    }
    
    @Override
    protected AlgorithmParameters engineGetParameters() {
        if (this.encrypt && this.iv == null && this.tagLen == -1) {
            switch (this.blockMode) {
                case 10: {
                    this.iv = new byte[16];
                    this.tagLen = 16;
                    this.random.nextBytes(this.iv);
                    break;
                }
                default: {
                    throw new ProviderException("Unsupported mode");
                }
            }
        }
        try {
            switch (this.blockMode) {
                case 10: {
                    final String s = "GCM";
                    final GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(this.tagLen << 3, this.iv);
                    final AlgorithmParameters instance = AlgorithmParameters.getInstance(s);
                    instance.init(gcmParameterSpec);
                    return instance;
                }
                default: {
                    throw new ProviderException("Unsupported mode");
                }
            }
        }
        catch (final GeneralSecurityException ex) {
            throw new ProviderException("Could not encode parameters", ex);
        }
    }
    
    @Override
    protected void engineInit(final int n, final Key key, final SecureRandom secureRandom) throws InvalidKeyException {
        if (n == 2) {
            throw new InvalidKeyException("Parameters required for decryption");
        }
        this.updateCalled = false;
        try {
            this.implInit(n, key, null, -1, secureRandom);
        }
        catch (final InvalidAlgorithmParameterException ex) {
            throw new InvalidKeyException("init() failed", ex);
        }
    }
    
    @Override
    protected void engineInit(final int n, final Key key, final AlgorithmParameterSpec algorithmParameterSpec, final SecureRandom secureRandom) throws InvalidKeyException, InvalidAlgorithmParameterException {
        if (n == 2 && algorithmParameterSpec == null) {
            throw new InvalidAlgorithmParameterException("Parameters required for decryption");
        }
        this.updateCalled = false;
        byte[] iv = null;
        int n2 = -1;
        if (algorithmParameterSpec != null) {
            switch (this.blockMode) {
                case 10: {
                    if (!(algorithmParameterSpec instanceof GCMParameterSpec)) {
                        throw new InvalidAlgorithmParameterException("Only GCMParameterSpec is supported");
                    }
                    iv = ((GCMParameterSpec)algorithmParameterSpec).getIV();
                    n2 = ((GCMParameterSpec)algorithmParameterSpec).getTLen() >> 3;
                    break;
                }
                default: {
                    throw new ProviderException("Unsupported mode");
                }
            }
        }
        this.implInit(n, key, iv, n2, secureRandom);
    }
    
    @Override
    protected void engineInit(final int n, final Key key, final AlgorithmParameters algorithmParameters, final SecureRandom secureRandom) throws InvalidKeyException, InvalidAlgorithmParameterException {
        if (n == 2 && algorithmParameters == null) {
            throw new InvalidAlgorithmParameterException("Parameters required for decryption");
        }
        this.updateCalled = false;
        try {
            AlgorithmParameterSpec parameterSpec = null;
            if (algorithmParameters != null) {
                switch (this.blockMode) {
                    case 10: {
                        parameterSpec = algorithmParameters.getParameterSpec(GCMParameterSpec.class);
                        break;
                    }
                    default: {
                        throw new ProviderException("Unsupported mode");
                    }
                }
            }
            this.engineInit(n, key, parameterSpec, secureRandom);
        }
        catch (final InvalidParameterSpecException ex) {
            throw new InvalidAlgorithmParameterException(ex);
        }
    }
    
    private void implInit(final int n, final Key key, byte[] iv, int tagLen, final SecureRandom random) throws InvalidKeyException, InvalidAlgorithmParameterException {
        this.reset(true);
        if (this.fixedKeySize != -1 && ((key instanceof P11Key) ? (((P11Key)key).length() >> 3) : key.getEncoded().length) != this.fixedKeySize) {
            throw new InvalidKeyException("Key size is invalid");
        }
        final P11Key convertKey = P11SecretKeyFactory.convertKey(this.token, key, "AES");
        switch (n) {
            case 1: {
                this.encrypt = true;
                this.requireReinit = (Arrays.equals(iv, this.lastEncIv) && convertKey == this.lastEncKey);
                if (this.requireReinit) {
                    throw new InvalidAlgorithmParameterException("Cannot reuse iv for GCM encryption");
                }
                break;
            }
            case 2: {
                this.encrypt = false;
                this.requireReinit = false;
                break;
            }
            default: {
                throw new InvalidAlgorithmParameterException("Unsupported mode: " + n);
            }
        }
        if (random != null) {
            this.random = random;
        }
        if (iv == null && tagLen == -1) {
            switch (this.blockMode) {
                case 10: {
                    iv = new byte[16];
                    this.random.nextBytes(iv);
                    tagLen = 16;
                    break;
                }
                default: {
                    throw new ProviderException("Unsupported mode");
                }
            }
        }
        this.iv = iv;
        this.tagLen = tagLen;
        this.p11Key = convertKey;
        try {
            this.initialize();
        }
        catch (final PKCS11Exception ex) {
            throw new InvalidKeyException("Could not initialize cipher", ex);
        }
    }
    
    private void cancelOperation() {
        final int doFinalLength = this.doFinalLength(0);
        final byte[] array = new byte[doFinalLength];
        final byte[] byteArray = this.dataBuffer.toByteArray();
        final int length = byteArray.length;
        try {
            if (this.encrypt) {
                this.token.p11.C_Encrypt(this.session.id(), 0L, byteArray, 0, length, 0L, array, 0, doFinalLength);
            }
            else {
                this.token.p11.C_Decrypt(this.session.id(), 0L, byteArray, 0, length, 0L, array, 0, doFinalLength);
            }
        }
        catch (final PKCS11Exception ex) {
            if (this.encrypt) {
                throw new ProviderException("Cancel failed", ex);
            }
        }
    }
    
    private void ensureInitialized() throws PKCS11Exception {
        if (this.initialized && this.aadBuffer.size() > 0) {
            this.reset(true);
        }
        if (!this.initialized) {
            this.initialize();
        }
    }
    
    private void initialize() throws PKCS11Exception {
        if (this.p11Key == null) {
            throw new ProviderException("Operation cannot be performed without calling engineInit first");
        }
        if (this.requireReinit) {
            throw new IllegalStateException("Must use either different key or iv for GCM encryption");
        }
        this.token.ensureValid();
        final byte[] array = (byte[])((this.aadBuffer.size() > 0) ? this.aadBuffer.toByteArray() : null);
        final long keyID = this.p11Key.getKeyID();
        try {
            switch (this.blockMode) {
                case 10: {
                    final CK_MECHANISM ck_MECHANISM = new CK_MECHANISM(this.mechanism, new CK_GCM_PARAMS(this.tagLen << 3, this.iv, array));
                    if (this.session == null) {
                        this.session = this.token.getOpSession();
                    }
                    if (this.encrypt) {
                        this.token.p11.C_EncryptInit(this.session.id(), ck_MECHANISM, keyID);
                    }
                    else {
                        this.token.p11.C_DecryptInit(this.session.id(), ck_MECHANISM, keyID);
                    }
                    break;
                }
                default: {
                    throw new ProviderException("Unsupported mode: " + this.blockMode);
                }
            }
        }
        catch (final PKCS11Exception ex) {
            this.p11Key.releaseKeyID();
            this.session = this.token.releaseSession(this.session);
            throw ex;
        }
        finally {
            this.dataBuffer.reset();
            this.aadBuffer.reset();
        }
        this.initialized = true;
    }
    
    private int doFinalLength(final int n) {
        if (n < 0) {
            throw new ProviderException("Invalid negative input length");
        }
        int n2 = n + this.dataBuffer.size();
        if (this.encrypt) {
            n2 += this.tagLen;
        }
        return n2;
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
            this.dataBuffer.reset();
        }
    }
    
    @Override
    protected byte[] engineUpdate(final byte[] array, final int n, final int n2) {
        this.updateCalled = true;
        this.implUpdate(array, n, n2);
        return new byte[0];
    }
    
    @Override
    protected int engineUpdate(final byte[] array, final int n, final int n2, final byte[] array2, final int n3) throws ShortBufferException {
        this.updateCalled = true;
        this.implUpdate(array, n, n2);
        return 0;
    }
    
    @Override
    protected int engineUpdate(final ByteBuffer byteBuffer, final ByteBuffer byteBuffer2) throws ShortBufferException {
        this.updateCalled = true;
        this.implUpdate(byteBuffer);
        return 0;
    }
    
    @Override
    protected synchronized void engineUpdateAAD(final byte[] array, final int n, final int n2) throws IllegalStateException {
        if (array == null || n < 0 || n + n2 > array.length) {
            throw new IllegalArgumentException("Invalid AAD");
        }
        if (this.requireReinit) {
            throw new IllegalStateException("Must use either different key or iv for GCM encryption");
        }
        if (this.p11Key == null) {
            throw new IllegalStateException("Need to initialize Cipher first");
        }
        if (this.updateCalled) {
            throw new IllegalStateException("Update has been called; no more AAD data");
        }
        this.aadBuffer.write(array, n, n2);
    }
    
    @Override
    protected void engineUpdateAAD(final ByteBuffer byteBuffer) throws IllegalStateException {
        if (byteBuffer == null) {
            throw new IllegalArgumentException("Invalid AAD");
        }
        final byte[] array = new byte[byteBuffer.remaining()];
        byteBuffer.get(array);
        this.engineUpdateAAD(array, 0, array.length);
    }
    
    @Override
    protected byte[] engineDoFinal(final byte[] array, final int n, final int n2) throws IllegalBlockSizeException, BadPaddingException {
        final int doFinalLength = this.doFinalLength(n2);
        try {
            final byte[] array2 = new byte[doFinalLength];
            return P11Util.convert(array2, 0, this.engineDoFinal(array, n, n2, array2, 0));
        }
        catch (final ShortBufferException ex) {
            throw new ProviderException(ex);
        }
        finally {
            this.updateCalled = false;
        }
    }
    
    @Override
    protected int engineDoFinal(final byte[] array, final int n, final int n2, final byte[] array2, final int n3) throws ShortBufferException, IllegalBlockSizeException, BadPaddingException {
        try {
            return this.implDoFinal(array, n, n2, array2, n3, array2.length - n3);
        }
        finally {
            this.updateCalled = false;
        }
    }
    
    @Override
    protected int engineDoFinal(final ByteBuffer byteBuffer, final ByteBuffer byteBuffer2) throws ShortBufferException, IllegalBlockSizeException, BadPaddingException {
        try {
            return this.implDoFinal(byteBuffer, byteBuffer2);
        }
        finally {
            this.updateCalled = false;
        }
    }
    
    private int implUpdate(final byte[] array, final int n, final int n2) {
        if (n2 > 0) {
            this.updateCalled = true;
            try {
                this.ensureInitialized();
            }
            catch (final PKCS11Exception ex) {
                this.reset(false);
                throw new ProviderException("update() failed", ex);
            }
            this.dataBuffer.write(array, n, n2);
        }
        return 0;
    }
    
    private int implUpdate(final ByteBuffer byteBuffer) {
        final int remaining = byteBuffer.remaining();
        if (remaining > 0) {
            try {
                this.ensureInitialized();
            }
            catch (final PKCS11Exception ex) {
                this.reset(false);
                throw new ProviderException("update() failed", ex);
            }
            final byte[] array = new byte[remaining];
            byteBuffer.get(array);
            this.dataBuffer.write(array, 0, array.length);
        }
        return 0;
    }
    
    private int implDoFinal(byte[] byteArray, int n, int length, final byte[] array, final int n2, final int n3) throws ShortBufferException, IllegalBlockSizeException, BadPaddingException {
        if (n3 < this.doFinalLength(length)) {
            throw new ShortBufferException();
        }
        boolean b = true;
        try {
            this.ensureInitialized();
            if (this.dataBuffer.size() > 0) {
                if (byteArray != null && n > 0 && length > 0 && n < byteArray.length - length) {
                    this.dataBuffer.write(byteArray, n, length);
                }
                byteArray = this.dataBuffer.toByteArray();
                n = 0;
                length = byteArray.length;
            }
            int n4;
            if (this.encrypt) {
                n4 = this.token.p11.C_Encrypt(this.session.id(), 0L, byteArray, n, length, 0L, array, n2, n3);
                b = false;
            }
            else {
                if (length == 0) {
                    return 0;
                }
                n4 = this.token.p11.C_Decrypt(this.session.id(), 0L, byteArray, n, length, 0L, array, n2, n3);
                b = false;
            }
            return n4;
        }
        catch (final PKCS11Exception ex) {
            b = false;
            this.handleException(ex);
            throw new ProviderException("doFinal() failed", ex);
        }
        finally {
            if (this.encrypt) {
                this.lastEncKey = this.p11Key;
                this.lastEncIv = this.iv;
                this.requireReinit = true;
            }
            this.reset(b);
        }
    }
    
    private int implDoFinal(final ByteBuffer byteBuffer, final ByteBuffer byteBuffer2) throws ShortBufferException, IllegalBlockSizeException, BadPaddingException {
        final int remaining = byteBuffer2.remaining();
        int n = byteBuffer.remaining();
        if (remaining < this.doFinalLength(n)) {
            throw new ShortBufferException();
        }
        boolean b = true;
        try {
            this.ensureInitialized();
            long address = 0L;
            byte[] array = null;
            int position = 0;
            if (this.dataBuffer.size() > 0) {
                if (n > 0) {
                    final byte[] array2 = new byte[n];
                    byteBuffer.get(array2);
                    this.dataBuffer.write(array2, 0, array2.length);
                }
                array = this.dataBuffer.toByteArray();
                position = 0;
                n = array.length;
            }
            else if (byteBuffer instanceof DirectBuffer) {
                address = ((DirectBuffer)byteBuffer).address();
                position = byteBuffer.position();
            }
            else if (byteBuffer.hasArray()) {
                array = byteBuffer.array();
                position = byteBuffer.position() + byteBuffer.arrayOffset();
            }
            else {
                array = new byte[n];
                byteBuffer.get(array);
            }
            long address2 = 0L;
            byte[] array3 = null;
            int position2 = 0;
            if (byteBuffer2 instanceof DirectBuffer) {
                address2 = ((DirectBuffer)byteBuffer2).address();
                position2 = byteBuffer2.position();
            }
            else if (byteBuffer2.hasArray()) {
                array3 = byteBuffer2.array();
                position2 = byteBuffer2.position() + byteBuffer2.arrayOffset();
            }
            else {
                array3 = new byte[remaining];
            }
            int n2;
            if (this.encrypt) {
                n2 = this.token.p11.C_Encrypt(this.session.id(), address, array, position, n, address2, array3, position2, remaining);
                b = false;
            }
            else {
                if (n == 0) {
                    return 0;
                }
                n2 = this.token.p11.C_Decrypt(this.session.id(), address, array, position, n, address2, array3, position2, remaining);
                b = false;
            }
            byteBuffer2.position(byteBuffer2.position() + n2);
            return n2;
        }
        catch (final PKCS11Exception ex) {
            b = false;
            this.handleException(ex);
            throw new ProviderException("doFinal() failed", ex);
        }
        finally {
            if (this.encrypt) {
                this.lastEncKey = this.p11Key;
                this.lastEncIv = this.iv;
                this.requireReinit = true;
            }
            this.reset(b);
        }
    }
    
    private void handleException(final PKCS11Exception ex) throws ShortBufferException, IllegalBlockSizeException, BadPaddingException {
        final long errorCode = ex.getErrorCode();
        if (errorCode == 336L) {
            throw (ShortBufferException)new ShortBufferException().initCause(ex);
        }
        if (errorCode == 33L || errorCode == 65L) {
            throw (IllegalBlockSizeException)new IllegalBlockSizeException(ex.toString()).initCause(ex);
        }
        if (errorCode == 64L || errorCode == 5L) {
            throw (BadPaddingException)new BadPaddingException(ex.toString()).initCause(ex);
        }
    }
    
    @Override
    protected byte[] engineWrap(final Key key) throws IllegalBlockSizeException, InvalidKeyException {
        throw new UnsupportedOperationException("engineWrap()");
    }
    
    @Override
    protected Key engineUnwrap(final byte[] array, final String s, final int n) throws InvalidKeyException, NoSuchAlgorithmException {
        throw new UnsupportedOperationException("engineUnwrap()");
    }
    
    @Override
    protected int engineGetKeySize(final Key key) throws InvalidKeyException {
        return P11SecretKeyFactory.convertKey(this.token, key, "AES").length();
    }
}
