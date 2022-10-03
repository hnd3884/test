package sun.security.pkcs11;

import java.util.Arrays;
import sun.nio.ch.DirectBuffer;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import java.nio.ByteBuffer;
import javax.crypto.ShortBufferException;
import sun.security.pkcs11.wrapper.CK_AES_CTR_PARAMS;
import sun.security.pkcs11.wrapper.CK_MECHANISM;
import sun.security.jca.JCAUtil;
import java.security.spec.InvalidParameterSpecException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.SecureRandom;
import java.security.Key;
import java.security.GeneralSecurityException;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import java.security.AlgorithmParameters;
import java.util.Locale;
import java.security.NoSuchAlgorithmException;
import sun.security.pkcs11.wrapper.PKCS11Exception;
import javax.crypto.NoSuchPaddingException;
import java.security.ProviderException;
import javax.crypto.CipherSpi;

final class P11Cipher extends CipherSpi
{
    private static final int MODE_ECB = 3;
    private static final int MODE_CBC = 4;
    private static final int MODE_CTR = 5;
    private static final int PAD_NONE = 5;
    private static final int PAD_PKCS5 = 6;
    private final Token token;
    private final String algorithm;
    private final String keyAlgorithm;
    private final long mechanism;
    private Session session;
    private P11Key p11Key;
    private boolean initialized;
    private boolean encrypt;
    private int blockMode;
    private final int blockSize;
    private int paddingType;
    private Padding paddingObj;
    private byte[] padBuffer;
    private int padBufferLen;
    private byte[] iv;
    private int bytesBuffered;
    private int fixedKeySize;
    
    P11Cipher(final Token token, final String algorithm, final long mechanism) throws PKCS11Exception, NoSuchAlgorithmException {
        this.fixedKeySize = -1;
        this.token = token;
        this.algorithm = algorithm;
        this.mechanism = mechanism;
        final String[] split = algorithm.split("/");
        if (split[0].startsWith("AES")) {
            this.blockSize = 16;
            final int index = split[0].indexOf(95);
            if (index != -1) {
                this.fixedKeySize = Integer.parseInt(split[0].substring(index + 1)) / 8;
            }
            this.keyAlgorithm = "AES";
        }
        else {
            this.keyAlgorithm = split[0];
            if (this.keyAlgorithm.equals("RC4") || this.keyAlgorithm.equals("ARCFOUR")) {
                this.blockSize = 0;
            }
            else {
                this.blockSize = 8;
            }
        }
        this.blockMode = ((split.length > 1) ? this.parseMode(split[1]) : 3);
        final String s = (this.blockSize == 0) ? "NoPadding" : "PKCS5Padding";
        final String s2 = (split.length > 2) ? split[2] : s;
        try {
            this.engineSetPadding(s2);
        }
        catch (final NoSuchPaddingException ex) {
            throw new ProviderException(ex);
        }
    }
    
    @Override
    protected void engineSetMode(final String s) throws NoSuchAlgorithmException {
        throw new NoSuchAlgorithmException("Unsupported mode " + s);
    }
    
    private int parseMode(String upperCase) throws NoSuchAlgorithmException {
        upperCase = upperCase.toUpperCase(Locale.ENGLISH);
        int n;
        if (upperCase.equals("ECB")) {
            n = 3;
        }
        else if (upperCase.equals("CBC")) {
            if (this.blockSize == 0) {
                throw new NoSuchAlgorithmException("CBC mode not supported with stream ciphers");
            }
            n = 4;
        }
        else {
            if (!upperCase.equals("CTR")) {
                throw new NoSuchAlgorithmException("Unsupported mode " + upperCase);
            }
            n = 5;
        }
        return n;
    }
    
    @Override
    protected void engineSetPadding(String upperCase) throws NoSuchPaddingException {
        this.paddingObj = null;
        this.padBuffer = null;
        upperCase = upperCase.toUpperCase(Locale.ENGLISH);
        if (upperCase.equals("NOPADDING")) {
            this.paddingType = 5;
        }
        else {
            if (!upperCase.equals("PKCS5PADDING")) {
                throw new NoSuchPaddingException("Unsupported padding " + upperCase);
            }
            if (this.blockMode == 5) {
                throw new NoSuchPaddingException("PKCS#5 padding not supported with CTR mode");
            }
            this.paddingType = 6;
            if (this.mechanism != 293L && this.mechanism != 310L && this.mechanism != 4229L) {
                this.paddingObj = new PKCS5Padding(this.blockSize);
                this.padBuffer = new byte[this.blockSize];
            }
        }
    }
    
    @Override
    protected int engineGetBlockSize() {
        return this.blockSize;
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
        if (this.iv == null) {
            return null;
        }
        final IvParameterSpec ivParameterSpec = new IvParameterSpec(this.iv);
        try {
            final AlgorithmParameters instance = AlgorithmParameters.getInstance(this.keyAlgorithm, P11Util.getSunJceProvider());
            instance.init(ivParameterSpec);
            return instance;
        }
        catch (final GeneralSecurityException ex) {
            throw new ProviderException("Could not encode parameters", ex);
        }
    }
    
    @Override
    protected void engineInit(final int n, final Key key, final SecureRandom secureRandom) throws InvalidKeyException {
        try {
            this.implInit(n, key, null, secureRandom);
        }
        catch (final InvalidAlgorithmParameterException ex) {
            throw new InvalidKeyException("init() failed", ex);
        }
    }
    
    @Override
    protected void engineInit(final int n, final Key key, final AlgorithmParameterSpec algorithmParameterSpec, final SecureRandom secureRandom) throws InvalidKeyException, InvalidAlgorithmParameterException {
        byte[] iv;
        if (algorithmParameterSpec != null) {
            if (!(algorithmParameterSpec instanceof IvParameterSpec)) {
                throw new InvalidAlgorithmParameterException("Only IvParameterSpec supported");
            }
            iv = ((IvParameterSpec)algorithmParameterSpec).getIV();
        }
        else {
            iv = null;
        }
        this.implInit(n, key, iv, secureRandom);
    }
    
    @Override
    protected void engineInit(final int n, final Key key, final AlgorithmParameters algorithmParameters, final SecureRandom secureRandom) throws InvalidKeyException, InvalidAlgorithmParameterException {
        byte[] iv = null;
        Label_0042: {
            if (algorithmParameters != null) {
                try {
                    iv = algorithmParameters.getParameterSpec(IvParameterSpec.class).getIV();
                    break Label_0042;
                }
                catch (final InvalidParameterSpecException ex) {
                    throw new InvalidAlgorithmParameterException("Could not decode IV", ex);
                }
            }
            iv = null;
        }
        this.implInit(n, key, iv, secureRandom);
    }
    
    private void implInit(final int n, final Key key, byte[] iv, SecureRandom secureRandom) throws InvalidKeyException, InvalidAlgorithmParameterException {
        this.reset(true);
        if (this.fixedKeySize != -1 && ((key instanceof P11Key) ? (((P11Key)key).length() >> 3) : key.getEncoded().length) != this.fixedKeySize) {
            throw new InvalidKeyException("Key size is invalid");
        }
        switch (n) {
            case 1: {
                this.encrypt = true;
                break;
            }
            case 2: {
                this.encrypt = false;
                break;
            }
            default: {
                throw new InvalidAlgorithmParameterException("Unsupported mode: " + n);
            }
        }
        if (this.blockMode == 3) {
            if (iv != null) {
                if (this.blockSize == 0) {
                    throw new InvalidAlgorithmParameterException("IV not used with stream ciphers");
                }
                throw new InvalidAlgorithmParameterException("IV not used in ECB mode");
            }
        }
        else if (iv == null) {
            if (!this.encrypt) {
                throw new InvalidAlgorithmParameterException((this.blockMode == 4) ? "IV must be specified for decryption in CBC mode" : "IV must be specified for decryption in CTR mode");
            }
            if (secureRandom == null) {
                secureRandom = JCAUtil.getSecureRandom();
            }
            iv = new byte[this.blockSize];
            secureRandom.nextBytes(iv);
        }
        else if (iv.length != this.blockSize) {
            throw new InvalidAlgorithmParameterException("IV length must match block size");
        }
        this.iv = iv;
        this.p11Key = P11SecretKeyFactory.convertKey(this.token, key, this.keyAlgorithm);
        try {
            this.initialize();
        }
        catch (final PKCS11Exception ex) {
            throw new InvalidKeyException("Could not initialize cipher", ex);
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
            this.bytesBuffered = 0;
            this.padBufferLen = 0;
        }
    }
    
    private void cancelOperation() {
        this.token.ensureValid();
        try {
            final int doFinalLength = this.doFinalLength(0);
            final byte[] array = new byte[doFinalLength];
            if (this.encrypt) {
                this.token.p11.C_EncryptFinal(this.session.id(), 0L, array, 0, doFinalLength);
            }
            else {
                this.token.p11.C_DecryptFinal(this.session.id(), 0L, array, 0, doFinalLength);
            }
        }
        catch (final PKCS11Exception ex) {
            if (this.encrypt) {
                throw new ProviderException("Cancel failed", ex);
            }
        }
    }
    
    private void ensureInitialized() throws PKCS11Exception {
        if (!this.initialized) {
            this.initialize();
        }
    }
    
    private void initialize() throws PKCS11Exception {
        if (this.p11Key == null) {
            throw new ProviderException("Operation cannot be performed without calling engineInit first");
        }
        this.token.ensureValid();
        final long keyID = this.p11Key.getKeyID();
        try {
            if (this.session == null) {
                this.session = this.token.getOpSession();
            }
            CK_MECHANISM ck_MECHANISM;
            if (this.blockMode == 5) {
                final long mechanism;
                final CK_AES_CTR_PARAMS ck_AES_CTR_PARAMS;
                ck_MECHANISM = new CK_MECHANISM(mechanism, ck_AES_CTR_PARAMS);
                mechanism = this.mechanism;
                ck_AES_CTR_PARAMS = new CK_AES_CTR_PARAMS(this.iv);
            }
            else {
                ck_MECHANISM = new CK_MECHANISM(this.mechanism, this.iv);
            }
            final CK_MECHANISM ck_MECHANISM2 = ck_MECHANISM;
            if (this.encrypt) {
                this.token.p11.C_EncryptInit(this.session.id(), ck_MECHANISM2, keyID);
            }
            else {
                this.token.p11.C_DecryptInit(this.session.id(), ck_MECHANISM2, keyID);
            }
        }
        catch (final PKCS11Exception ex) {
            this.p11Key.releaseKeyID();
            this.session = this.token.releaseSession(this.session);
            throw ex;
        }
        this.initialized = true;
        this.bytesBuffered = 0;
        this.padBufferLen = 0;
    }
    
    private int updateLength(final int n) {
        if (n <= 0) {
            return 0;
        }
        int n2 = n + this.bytesBuffered;
        if (this.blockSize != 0 && this.blockMode != 5) {
            n2 -= (n2 & this.blockSize - 1);
        }
        return n2;
    }
    
    private int doFinalLength(final int n) {
        if (n < 0) {
            return 0;
        }
        int n2 = n + this.bytesBuffered;
        if (this.blockSize != 0 && this.encrypt && this.paddingType != 5) {
            n2 += this.blockSize - (n2 & this.blockSize - 1);
        }
        return n2;
    }
    
    @Override
    protected byte[] engineUpdate(final byte[] array, final int n, final int n2) {
        try {
            final byte[] array2 = new byte[this.updateLength(n2)];
            return P11Util.convert(array2, 0, this.engineUpdate(array, n, n2, array2, 0));
        }
        catch (final ShortBufferException ex) {
            throw new ProviderException(ex);
        }
    }
    
    @Override
    protected int engineUpdate(final byte[] array, final int n, final int n2, final byte[] array2, final int n3) throws ShortBufferException {
        return this.implUpdate(array, n, n2, array2, n3, array2.length - n3);
    }
    
    @Override
    protected int engineUpdate(final ByteBuffer byteBuffer, final ByteBuffer byteBuffer2) throws ShortBufferException {
        return this.implUpdate(byteBuffer, byteBuffer2);
    }
    
    @Override
    protected byte[] engineDoFinal(final byte[] array, final int n, final int n2) throws IllegalBlockSizeException, BadPaddingException {
        try {
            final byte[] array2 = new byte[this.doFinalLength(n2)];
            return P11Util.convert(array2, 0, this.engineDoFinal(array, n, n2, array2, 0));
        }
        catch (final ShortBufferException ex) {
            throw new ProviderException(ex);
        }
    }
    
    @Override
    protected int engineDoFinal(final byte[] array, final int n, final int n2, final byte[] array2, int n3) throws ShortBufferException, IllegalBlockSizeException, BadPaddingException {
        int engineUpdate = 0;
        if (n2 != 0 && array != null) {
            engineUpdate = this.engineUpdate(array, n, n2, array2, n3);
            n3 += engineUpdate;
        }
        return engineUpdate + this.implDoFinal(array2, n3, array2.length - n3);
    }
    
    @Override
    protected int engineDoFinal(final ByteBuffer byteBuffer, final ByteBuffer byteBuffer2) throws ShortBufferException, IllegalBlockSizeException, BadPaddingException {
        return this.engineUpdate(byteBuffer, byteBuffer2) + this.implDoFinal(byteBuffer2);
    }
    
    private int implUpdate(final byte[] array, int n, int n2, final byte[] array2, final int n3, final int n4) throws ShortBufferException {
        if (n4 < this.updateLength(n2)) {
            throw new ShortBufferException();
        }
        try {
            this.ensureInitialized();
            int n5 = 0;
            if (this.encrypt) {
                n5 = this.token.p11.C_EncryptUpdate(this.session.id(), 0L, array, n, n2, 0L, array2, n3, n4);
            }
            else {
                int length = 0;
                if (this.paddingObj != null) {
                    if (this.padBufferLen != 0) {
                        if (this.padBufferLen != this.padBuffer.length) {
                            final int n6 = this.padBuffer.length - this.padBufferLen;
                            if (n2 <= n6) {
                                this.bufferInputBytes(array, n, n2);
                                return 0;
                            }
                            this.bufferInputBytes(array, n, n6);
                            n += n6;
                            n2 -= n6;
                        }
                        n5 = this.token.p11.C_DecryptUpdate(this.session.id(), 0L, this.padBuffer, 0, this.padBufferLen, 0L, array2, n3, n4);
                        this.padBufferLen = 0;
                    }
                    length = (n2 & this.blockSize - 1);
                    if (length == 0) {
                        length = this.padBuffer.length;
                    }
                    n2 -= length;
                }
                if (n2 > 0) {
                    n5 += this.token.p11.C_DecryptUpdate(this.session.id(), 0L, array, n, n2, 0L, array2, n3 + n5, n4 - n5);
                }
                if (this.paddingObj != null) {
                    this.bufferInputBytes(array, n + n2, length);
                }
            }
            this.bytesBuffered += n2 - n5;
            return n5;
        }
        catch (final PKCS11Exception ex) {
            if (ex.getErrorCode() == 336L) {
                throw (ShortBufferException)new ShortBufferException().initCause(ex);
            }
            this.reset(false);
            throw new ProviderException("update() failed", ex);
        }
    }
    
    private int implUpdate(final ByteBuffer byteBuffer, final ByteBuffer byteBuffer2) throws ShortBufferException {
        int remaining = byteBuffer.remaining();
        if (remaining <= 0) {
            return 0;
        }
        final int remaining2 = byteBuffer2.remaining();
        if (remaining2 < this.updateLength(remaining)) {
            throw new ShortBufferException();
        }
        final int position = byteBuffer.position();
        try {
            this.ensureInitialized();
            long address = 0L;
            int n = 0;
            byte[] array = null;
            if (byteBuffer instanceof DirectBuffer) {
                address = ((DirectBuffer)byteBuffer).address();
                n = position;
            }
            else if (byteBuffer.hasArray()) {
                array = byteBuffer.array();
                n = position + byteBuffer.arrayOffset();
            }
            long address2 = 0L;
            int position2 = 0;
            byte[] array2 = null;
            if (byteBuffer2 instanceof DirectBuffer) {
                address2 = ((DirectBuffer)byteBuffer2).address();
                position2 = byteBuffer2.position();
            }
            else if (byteBuffer2.hasArray()) {
                array2 = byteBuffer2.array();
                position2 = byteBuffer2.position() + byteBuffer2.arrayOffset();
            }
            else {
                array2 = new byte[remaining2];
            }
            int n2 = 0;
            if (this.encrypt) {
                if (address == 0L && array == null) {
                    array = new byte[remaining];
                    byteBuffer.get(array);
                }
                else {
                    byteBuffer.position(position + remaining);
                }
                n2 = this.token.p11.C_EncryptUpdate(this.session.id(), address, array, n, remaining, address2, array2, position2, remaining2);
            }
            else {
                int length = 0;
                if (this.paddingObj != null) {
                    if (this.padBufferLen != 0) {
                        if (this.padBufferLen != this.padBuffer.length) {
                            final int n3 = this.padBuffer.length - this.padBufferLen;
                            if (remaining <= n3) {
                                this.bufferInputBytes(byteBuffer, remaining);
                                return 0;
                            }
                            this.bufferInputBytes(byteBuffer, n3);
                            n += n3;
                            remaining -= n3;
                        }
                        n2 = this.token.p11.C_DecryptUpdate(this.session.id(), 0L, this.padBuffer, 0, this.padBufferLen, address2, array2, position2, remaining2);
                        this.padBufferLen = 0;
                    }
                    length = (remaining & this.blockSize - 1);
                    if (length == 0) {
                        length = this.padBuffer.length;
                    }
                    remaining -= length;
                }
                if (remaining > 0) {
                    if (address == 0L && array == null) {
                        array = new byte[remaining];
                        byteBuffer.get(array);
                    }
                    else {
                        byteBuffer.position(byteBuffer.position() + remaining);
                    }
                    n2 += this.token.p11.C_DecryptUpdate(this.session.id(), address, array, n, remaining, address2, array2, position2 + n2, remaining2 - n2);
                }
                if (this.paddingObj != null && length != 0) {
                    this.bufferInputBytes(byteBuffer, length);
                }
            }
            this.bytesBuffered += remaining - n2;
            if (!(byteBuffer2 instanceof DirectBuffer) && !byteBuffer2.hasArray()) {
                byteBuffer2.put(array2, position2, n2);
            }
            else {
                byteBuffer2.position(byteBuffer2.position() + n2);
            }
            return n2;
        }
        catch (final PKCS11Exception ex) {
            byteBuffer.position(position);
            if (ex.getErrorCode() == 336L) {
                throw (ShortBufferException)new ShortBufferException().initCause(ex);
            }
            this.reset(false);
            throw new ProviderException("update() failed", ex);
        }
    }
    
    private int implDoFinal(final byte[] array, final int n, final int n2) throws ShortBufferException, IllegalBlockSizeException, BadPaddingException {
        final int doFinalLength = this.doFinalLength(0);
        if (n2 < doFinalLength) {
            throw new ShortBufferException();
        }
        boolean b = true;
        try {
            this.ensureInitialized();
            int n3 = 0;
            int c_DecryptFinal;
            if (this.encrypt) {
                if (this.paddingObj != null) {
                    n3 = this.token.p11.C_EncryptUpdate(this.session.id(), 0L, this.padBuffer, 0, this.paddingObj.setPaddingBytes(this.padBuffer, doFinalLength - this.bytesBuffered), 0L, array, n, n2);
                }
                c_DecryptFinal = n3 + this.token.p11.C_EncryptFinal(this.session.id(), 0L, array, n + n3, n2 - n3);
                b = false;
            }
            else {
                if (this.bytesBuffered == 0 && this.padBufferLen == 0) {
                    return 0;
                }
                if (this.paddingObj != null) {
                    if (this.padBufferLen != 0) {
                        n3 = this.token.p11.C_DecryptUpdate(this.session.id(), 0L, this.padBuffer, 0, this.padBufferLen, 0L, this.padBuffer, 0, this.padBuffer.length);
                    }
                    final int n4 = n3 + this.token.p11.C_DecryptFinal(this.session.id(), 0L, this.padBuffer, n3, this.padBuffer.length - n3);
                    b = false;
                    c_DecryptFinal = n4 - this.paddingObj.unpad(this.padBuffer, n4);
                    System.arraycopy(this.padBuffer, 0, array, n, c_DecryptFinal);
                }
                else {
                    c_DecryptFinal = this.token.p11.C_DecryptFinal(this.session.id(), 0L, array, n, n2);
                    b = false;
                }
            }
            return c_DecryptFinal;
        }
        catch (final PKCS11Exception ex) {
            b = false;
            this.handleException(ex);
            throw new ProviderException("doFinal() failed", ex);
        }
        finally {
            this.reset(b);
        }
    }
    
    private int implDoFinal(final ByteBuffer byteBuffer) throws ShortBufferException, IllegalBlockSizeException, BadPaddingException {
        final int remaining = byteBuffer.remaining();
        final int doFinalLength = this.doFinalLength(0);
        if (remaining < doFinalLength) {
            throw new ShortBufferException();
        }
        boolean b = true;
        try {
            this.ensureInitialized();
            long address = 0L;
            byte[] array = null;
            int position = 0;
            if (byteBuffer instanceof DirectBuffer) {
                address = ((DirectBuffer)byteBuffer).address();
                position = byteBuffer.position();
            }
            else if (byteBuffer.hasArray()) {
                array = byteBuffer.array();
                position = byteBuffer.position() + byteBuffer.arrayOffset();
            }
            else {
                array = new byte[remaining];
            }
            int n = 0;
            int c_DecryptFinal;
            if (this.encrypt) {
                if (this.paddingObj != null) {
                    n = this.token.p11.C_EncryptUpdate(this.session.id(), 0L, this.padBuffer, 0, this.paddingObj.setPaddingBytes(this.padBuffer, doFinalLength - this.bytesBuffered), address, array, position, remaining);
                }
                c_DecryptFinal = n + this.token.p11.C_EncryptFinal(this.session.id(), address, array, position + n, remaining - n);
                b = false;
            }
            else {
                if (this.bytesBuffered == 0 && this.padBufferLen == 0) {
                    return 0;
                }
                if (this.paddingObj != null) {
                    if (this.padBufferLen != 0) {
                        n = this.token.p11.C_DecryptUpdate(this.session.id(), 0L, this.padBuffer, 0, this.padBufferLen, 0L, this.padBuffer, 0, this.padBuffer.length);
                        this.padBufferLen = 0;
                    }
                    final int n2 = n + this.token.p11.C_DecryptFinal(this.session.id(), 0L, this.padBuffer, n, this.padBuffer.length - n);
                    b = false;
                    c_DecryptFinal = n2 - this.paddingObj.unpad(this.padBuffer, n2);
                    array = this.padBuffer;
                    position = 0;
                }
                else {
                    c_DecryptFinal = this.token.p11.C_DecryptFinal(this.session.id(), address, array, position, remaining);
                    b = false;
                }
            }
            if ((!this.encrypt && this.paddingObj != null) || (!(byteBuffer instanceof DirectBuffer) && !byteBuffer.hasArray())) {
                byteBuffer.put(array, position, c_DecryptFinal);
            }
            else {
                byteBuffer.position(byteBuffer.position() + c_DecryptFinal);
            }
            return c_DecryptFinal;
        }
        catch (final PKCS11Exception ex) {
            b = false;
            this.handleException(ex);
            throw new ProviderException("doFinal() failed", ex);
        }
        finally {
            this.reset(b);
        }
    }
    
    private void handleException(final PKCS11Exception ex) throws ShortBufferException, IllegalBlockSizeException {
        final long errorCode = ex.getErrorCode();
        if (errorCode == 336L) {
            throw (ShortBufferException)new ShortBufferException().initCause(ex);
        }
        if (errorCode == 33L || errorCode == 65L) {
            throw (IllegalBlockSizeException)new IllegalBlockSizeException(ex.toString()).initCause(ex);
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
        return P11SecretKeyFactory.convertKey(this.token, key, this.keyAlgorithm).length();
    }
    
    private final void bufferInputBytes(final byte[] array, final int n, final int n2) {
        System.arraycopy(array, n, this.padBuffer, this.padBufferLen, n2);
        this.padBufferLen += n2;
        this.bytesBuffered += n2;
    }
    
    private final void bufferInputBytes(final ByteBuffer byteBuffer, final int n) {
        byteBuffer.get(this.padBuffer, this.padBufferLen, n);
        this.padBufferLen += n;
        this.bytesBuffered += n;
    }
    
    private static class PKCS5Padding implements Padding
    {
        private final int blockSize;
        
        PKCS5Padding(final int blockSize) throws NoSuchPaddingException {
            if (blockSize == 0) {
                throw new NoSuchPaddingException("PKCS#5 padding not supported with stream ciphers");
            }
            this.blockSize = blockSize;
        }
        
        @Override
        public int setPaddingBytes(final byte[] array, final int n) {
            Arrays.fill(array, 0, n, (byte)(n & 0x7F));
            return n;
        }
        
        @Override
        public int unpad(final byte[] array, final int n) throws BadPaddingException, IllegalBlockSizeException {
            if (n < 1 || n % this.blockSize != 0) {
                throw new IllegalBlockSizeException("Input length must be multiples of " + this.blockSize);
            }
            final byte b = array[n - 1];
            if (b < 1 || b > this.blockSize) {
                throw new BadPaddingException("Invalid pad value!");
            }
            for (int i = n - b; i < n; ++i) {
                if (array[i] != b) {
                    throw new BadPaddingException("Invalid pad bytes!");
                }
            }
            return b;
        }
    }
    
    private interface Padding
    {
        int setPaddingBytes(final byte[] p0, final int p1);
        
        int unpad(final byte[] p0, final int p1) throws BadPaddingException, IllegalBlockSizeException;
    }
}
