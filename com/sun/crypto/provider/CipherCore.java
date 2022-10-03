package com.sun.crypto.provider;

import javax.crypto.AEADBadTagException;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.ShortBufferException;
import java.security.ProviderException;
import java.security.MessageDigest;
import java.util.Arrays;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.SecureRandom;
import java.security.Key;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidParameterSpecException;
import java.security.Provider;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.RC2ParameterSpec;
import javax.crypto.spec.GCMParameterSpec;
import java.security.AlgorithmParameters;
import javax.crypto.NoSuchPaddingException;
import java.util.Locale;
import java.security.NoSuchAlgorithmException;

final class CipherCore
{
    private byte[] buffer;
    private int blockSize;
    private int unitBytes;
    private int buffered;
    private int minBytes;
    private int diffBlocksize;
    private Padding padding;
    private FeedbackCipher cipher;
    private int cipherMode;
    private boolean decrypting;
    private static final int ECB_MODE = 0;
    private static final int CBC_MODE = 1;
    private static final int CFB_MODE = 2;
    private static final int OFB_MODE = 3;
    private static final int PCBC_MODE = 4;
    private static final int CTR_MODE = 5;
    private static final int CTS_MODE = 6;
    static final int GCM_MODE = 7;
    private boolean requireReinit;
    private byte[] lastEncKey;
    private byte[] lastEncIv;
    
    CipherCore(final SymmetricCipher symmetricCipher, final int diffBlocksize) {
        this.buffer = null;
        this.blockSize = 0;
        this.unitBytes = 0;
        this.buffered = 0;
        this.minBytes = 0;
        this.diffBlocksize = 0;
        this.padding = null;
        this.cipher = null;
        this.cipherMode = 0;
        this.decrypting = false;
        this.requireReinit = false;
        this.lastEncKey = null;
        this.lastEncIv = null;
        this.blockSize = diffBlocksize;
        this.unitBytes = diffBlocksize;
        this.diffBlocksize = diffBlocksize;
        this.buffer = new byte[this.blockSize * 2];
        this.cipher = new ElectronicCodeBook(symmetricCipher);
        this.padding = new PKCS5Padding(this.blockSize);
    }
    
    void setMode(final String s) throws NoSuchAlgorithmException {
        if (s == null) {
            throw new NoSuchAlgorithmException("null mode");
        }
        final String upperCase = s.toUpperCase(Locale.ENGLISH);
        if (upperCase.equals("ECB")) {
            return;
        }
        final SymmetricCipher embeddedCipher = this.cipher.getEmbeddedCipher();
        if (upperCase.equals("CBC")) {
            this.cipherMode = 1;
            this.cipher = new CipherBlockChaining(embeddedCipher);
        }
        else if (upperCase.equals("CTS")) {
            this.cipherMode = 6;
            this.cipher = new CipherTextStealing(embeddedCipher);
            this.minBytes = this.blockSize + 1;
            this.padding = null;
        }
        else if (upperCase.equals("CTR")) {
            this.cipherMode = 5;
            this.cipher = new CounterMode(embeddedCipher);
            this.unitBytes = 1;
            this.padding = null;
        }
        else if (upperCase.equals("GCM")) {
            if (this.blockSize != 16) {
                throw new NoSuchAlgorithmException("GCM mode can only be used for AES cipher");
            }
            this.cipherMode = 7;
            this.cipher = new GaloisCounterMode(embeddedCipher);
            this.padding = null;
        }
        else if (upperCase.startsWith("CFB")) {
            this.cipherMode = 2;
            this.unitBytes = getNumOfUnit(s, "CFB".length(), this.blockSize);
            this.cipher = new CipherFeedback(embeddedCipher, this.unitBytes);
        }
        else if (upperCase.startsWith("OFB")) {
            this.cipherMode = 3;
            this.unitBytes = getNumOfUnit(s, "OFB".length(), this.blockSize);
            this.cipher = new OutputFeedback(embeddedCipher, this.unitBytes);
        }
        else {
            if (!upperCase.equals("PCBC")) {
                throw new NoSuchAlgorithmException("Cipher mode: " + s + " not found");
            }
            this.cipherMode = 4;
            this.cipher = new PCBC(embeddedCipher);
        }
    }
    
    int getMode() {
        return this.cipherMode;
    }
    
    private static int getNumOfUnit(final String s, final int n, final int n2) throws NoSuchAlgorithmException {
        int n3 = n2;
        if (s.length() > n) {
            int intValue;
            try {
                intValue = Integer.valueOf(s.substring(n));
                n3 = intValue >> 3;
            }
            catch (final NumberFormatException ex) {
                throw new NoSuchAlgorithmException("Algorithm mode: " + s + " not implemented");
            }
            if (intValue % 8 != 0 || n3 > n2) {
                throw new NoSuchAlgorithmException("Invalid algorithm mode: " + s);
            }
        }
        return n3;
    }
    
    void setPadding(final String s) throws NoSuchPaddingException {
        if (s == null) {
            throw new NoSuchPaddingException("null padding");
        }
        if (s.equalsIgnoreCase("NoPadding")) {
            this.padding = null;
        }
        else if (s.equalsIgnoreCase("ISO10126Padding")) {
            this.padding = new ISO10126Padding(this.blockSize);
        }
        else if (!s.equalsIgnoreCase("PKCS5Padding")) {
            throw new NoSuchPaddingException("Padding: " + s + " not implemented");
        }
        if (this.padding != null && (this.cipherMode == 5 || this.cipherMode == 6 || this.cipherMode == 7)) {
            this.padding = null;
            String s2 = null;
            switch (this.cipherMode) {
                case 5: {
                    s2 = "CTR";
                    break;
                }
                case 7: {
                    s2 = "GCM";
                    break;
                }
                case 6: {
                    s2 = "CTS";
                    break;
                }
            }
            if (s2 != null) {
                throw new NoSuchPaddingException(s2 + " mode must be used with NoPadding");
            }
        }
    }
    
    int getOutputSize(final int n) {
        return this.getOutputSizeByOperation(n, true);
    }
    
    private int getOutputSizeByOperation(final int n, final boolean b) {
        int n2 = Math.addExact(Math.addExact(this.buffered, this.cipher.getBufferedLength()), n);
        switch (this.cipherMode) {
            case 7: {
                if (b) {
                    final int tagLen = ((GaloisCounterMode)this.cipher).getTagLen();
                    if (!this.decrypting) {
                        n2 = Math.addExact(n2, tagLen);
                    }
                    else {
                        n2 -= tagLen;
                    }
                }
                if (n2 < 0) {
                    n2 = 0;
                    break;
                }
                break;
            }
            default: {
                if (this.padding == null || this.decrypting) {
                    break;
                }
                if (this.unitBytes == this.blockSize) {
                    n2 = Math.addExact(n2, this.padding.padLength(n2));
                    break;
                }
                if (n2 < this.diffBlocksize) {
                    n2 = this.diffBlocksize;
                    break;
                }
                n2 = Math.addExact(n2, this.blockSize - (n2 - this.diffBlocksize) % this.blockSize);
                break;
            }
        }
        return n2;
    }
    
    byte[] getIV() {
        final byte[] iv = this.cipher.getIV();
        return (byte[])((iv == null) ? null : ((byte[])iv.clone()));
    }
    
    AlgorithmParameters getParameters(String s) {
        if (this.cipherMode == 0) {
            return null;
        }
        byte[] iv = this.getIV();
        if (iv == null) {
            if (this.cipherMode == 7) {
                iv = new byte[GaloisCounterMode.DEFAULT_IV_LEN];
            }
            else {
                iv = new byte[this.blockSize];
            }
            SunJCE.getRandom().nextBytes(iv);
        }
        AlgorithmParameterSpec algorithmParameterSpec;
        if (this.cipherMode == 7) {
            s = "GCM";
            algorithmParameterSpec = new GCMParameterSpec(((GaloisCounterMode)this.cipher).getTagLen() * 8, iv);
        }
        else if (s.equals("RC2")) {
            algorithmParameterSpec = new RC2ParameterSpec(((RC2Crypt)this.cipher.getEmbeddedCipher()).getEffectiveKeyBits(), iv);
        }
        else {
            algorithmParameterSpec = new IvParameterSpec(iv);
        }
        AlgorithmParameters instance;
        try {
            instance = AlgorithmParameters.getInstance(s, SunJCE.getInstance());
            instance.init(algorithmParameterSpec);
        }
        catch (final NoSuchAlgorithmException ex) {
            throw new RuntimeException("Cannot find " + s + " AlgorithmParameters implementation in SunJCE provider");
        }
        catch (final InvalidParameterSpecException ex2) {
            throw new RuntimeException(algorithmParameterSpec.getClass() + " not supported");
        }
        return instance;
    }
    
    void init(final int n, final Key key, final SecureRandom secureRandom) throws InvalidKeyException {
        try {
            this.init(n, key, (AlgorithmParameterSpec)null, secureRandom);
        }
        catch (final InvalidAlgorithmParameterException ex) {
            throw new InvalidKeyException(ex.getMessage());
        }
    }
    
    void init(final int n, final Key key, final AlgorithmParameterSpec algorithmParameterSpec, SecureRandom random) throws InvalidKeyException, InvalidAlgorithmParameterException {
        this.decrypting = (n == 2 || n == 4);
        final byte[] keyBytes = getKeyBytes(key);
        int default_TAG_LEN = -1;
        byte[] lastEncIv = null;
        if (algorithmParameterSpec != null) {
            if (this.cipherMode == 7) {
                if (!(algorithmParameterSpec instanceof GCMParameterSpec)) {
                    throw new InvalidAlgorithmParameterException("Unsupported parameter: " + algorithmParameterSpec);
                }
                final int tLen = ((GCMParameterSpec)algorithmParameterSpec).getTLen();
                if (tLen < 96 || tLen > 128 || (tLen & 0x7) != 0x0) {
                    throw new InvalidAlgorithmParameterException("Unsupported TLen value; must be one of {128, 120, 112, 104, 96}");
                }
                default_TAG_LEN = tLen >> 3;
                lastEncIv = ((GCMParameterSpec)algorithmParameterSpec).getIV();
            }
            else if (algorithmParameterSpec instanceof IvParameterSpec) {
                lastEncIv = ((IvParameterSpec)algorithmParameterSpec).getIV();
                if (lastEncIv == null || lastEncIv.length != this.blockSize) {
                    throw new InvalidAlgorithmParameterException("Wrong IV length: must be " + this.blockSize + " bytes long");
                }
            }
            else {
                if (!(algorithmParameterSpec instanceof RC2ParameterSpec)) {
                    throw new InvalidAlgorithmParameterException("Unsupported parameter: " + algorithmParameterSpec);
                }
                lastEncIv = ((RC2ParameterSpec)algorithmParameterSpec).getIV();
                if (lastEncIv != null && lastEncIv.length != this.blockSize) {
                    throw new InvalidAlgorithmParameterException("Wrong IV length: must be " + this.blockSize + " bytes long");
                }
            }
        }
        if (this.cipherMode == 0) {
            if (lastEncIv != null) {
                throw new InvalidAlgorithmParameterException("ECB mode cannot use IV");
            }
        }
        else if (lastEncIv == null) {
            if (this.decrypting) {
                throw new InvalidAlgorithmParameterException("Parameters missing");
            }
            if (random == null) {
                random = SunJCE.getRandom();
            }
            if (this.cipherMode == 7) {
                lastEncIv = new byte[GaloisCounterMode.DEFAULT_IV_LEN];
            }
            else {
                lastEncIv = new byte[this.blockSize];
            }
            random.nextBytes(lastEncIv);
        }
        this.buffered = 0;
        this.diffBlocksize = this.blockSize;
        final String algorithm = key.getAlgorithm();
        if (this.cipherMode == 7) {
            if (default_TAG_LEN == -1) {
                default_TAG_LEN = GaloisCounterMode.DEFAULT_TAG_LEN;
            }
            if (this.decrypting) {
                this.minBytes = default_TAG_LEN;
            }
            else {
                this.requireReinit = (Arrays.equals(lastEncIv, this.lastEncIv) && MessageDigest.isEqual(keyBytes, this.lastEncKey));
                if (this.requireReinit) {
                    throw new InvalidAlgorithmParameterException("Cannot reuse iv for GCM encryption");
                }
                this.lastEncIv = lastEncIv;
                this.lastEncKey = keyBytes;
            }
            ((GaloisCounterMode)this.cipher).init(this.decrypting, algorithm, keyBytes, lastEncIv, default_TAG_LEN);
        }
        else {
            this.cipher.init(this.decrypting, algorithm, keyBytes, lastEncIv);
        }
        this.requireReinit = false;
    }
    
    void init(final int n, final Key key, final AlgorithmParameters algorithmParameters, final SecureRandom secureRandom) throws InvalidKeyException, InvalidAlgorithmParameterException {
        AlgorithmParameterSpec algorithmParameterSpec = null;
        String s = null;
        if (algorithmParameters != null) {
            try {
                if (this.cipherMode == 7) {
                    s = "GCM";
                    algorithmParameterSpec = algorithmParameters.getParameterSpec(GCMParameterSpec.class);
                }
                else {
                    s = "IV";
                    algorithmParameterSpec = algorithmParameters.getParameterSpec(IvParameterSpec.class);
                }
            }
            catch (final InvalidParameterSpecException ex) {
                throw new InvalidAlgorithmParameterException("Wrong parameter type: " + s + " expected");
            }
        }
        this.init(n, key, algorithmParameterSpec, secureRandom);
    }
    
    static byte[] getKeyBytes(final Key key) throws InvalidKeyException {
        if (key == null) {
            throw new InvalidKeyException("No key given");
        }
        if (!"RAW".equalsIgnoreCase(key.getFormat())) {
            throw new InvalidKeyException("Wrong format: RAW bytes needed");
        }
        final byte[] encoded = key.getEncoded();
        if (encoded == null) {
            throw new InvalidKeyException("RAW key bytes missing");
        }
        return encoded;
    }
    
    byte[] update(final byte[] array, final int n, final int n2) {
        this.checkReinit();
        try {
            final byte[] array2 = new byte[this.getOutputSizeByOperation(n2, false)];
            final int update = this.update(array, n, n2, array2, 0);
            if (update == array2.length) {
                return array2;
            }
            final byte[] copy = Arrays.copyOf(array2, update);
            if (this.decrypting) {
                Arrays.fill(array2, (byte)0);
            }
            return copy;
        }
        catch (final ShortBufferException ex) {
            throw new ProviderException("Unexpected exception", ex);
        }
    }
    
    int update(byte[] copyOfRange, int addExact, int n, final byte[] array, int addExact2) throws ShortBufferException {
        this.checkReinit();
        int n2 = Math.addExact(this.buffered, n) - this.minBytes;
        if (this.padding != null && this.decrypting) {
            n2 -= this.blockSize;
        }
        final int n3 = (n2 > 0) ? (n2 - n2 % this.unitBytes) : 0;
        if (array == null || array.length - addExact2 < n3) {
            throw new ShortBufferException("Output buffer must be (at least) " + n3 + " bytes long");
        }
        int n4 = 0;
        if (n3 != 0) {
            if (copyOfRange == array && addExact2 - addExact < n && addExact - addExact2 < this.buffer.length) {
                copyOfRange = Arrays.copyOfRange(copyOfRange, addExact, Math.addExact(addExact, n));
                addExact = 0;
            }
            if (n3 <= this.buffered) {
                if (this.decrypting) {
                    n4 = this.cipher.decrypt(this.buffer, 0, n3, array, addExact2);
                }
                else {
                    n4 = this.cipher.encrypt(this.buffer, 0, n3, array, addExact2);
                }
                this.buffered -= n3;
                if (this.buffered != 0) {
                    System.arraycopy(this.buffer, n3, this.buffer, 0, this.buffered);
                }
            }
            else {
                int n5 = n3 - this.buffered;
                if (this.buffered > 0) {
                    final int n6 = this.buffer.length - this.buffered;
                    if (n6 != 0) {
                        int min = Math.min(n6, n5);
                        if (this.unitBytes != this.blockSize) {
                            min -= Math.addExact(this.buffered, min) % this.unitBytes;
                        }
                        System.arraycopy(copyOfRange, addExact, this.buffer, this.buffered, min);
                        addExact = Math.addExact(addExact, min);
                        n5 -= min;
                        n -= min;
                        this.buffered = Math.addExact(this.buffered, min);
                    }
                    if (this.decrypting) {
                        n4 = this.cipher.decrypt(this.buffer, 0, this.buffered, array, addExact2);
                    }
                    else {
                        n4 = this.cipher.encrypt(this.buffer, 0, this.buffered, array, addExact2);
                        Arrays.fill(this.buffer, (byte)0);
                    }
                    addExact2 = Math.addExact(addExact2, n4);
                    this.buffered = 0;
                }
                if (n5 > 0) {
                    if (this.decrypting) {
                        n4 += this.cipher.decrypt(copyOfRange, addExact, n5, array, addExact2);
                    }
                    else {
                        n4 += this.cipher.encrypt(copyOfRange, addExact, n5, array, addExact2);
                    }
                    addExact += n5;
                    n -= n5;
                }
            }
            if (this.unitBytes != this.blockSize) {
                if (n3 < this.diffBlocksize) {
                    this.diffBlocksize -= n3;
                }
                else {
                    this.diffBlocksize = this.blockSize - (n3 - this.diffBlocksize) % this.blockSize;
                }
            }
        }
        if (n > 0) {
            System.arraycopy(copyOfRange, addExact, this.buffer, this.buffered, n);
            this.buffered = Math.addExact(this.buffered, n);
        }
        return n4;
    }
    
    byte[] doFinal(final byte[] array, final int n, final int n2) throws IllegalBlockSizeException, BadPaddingException {
        try {
            this.checkReinit();
            final byte[] array2 = new byte[this.getOutputSizeByOperation(n2, true)];
            final byte[] prepareInputBuffer = this.prepareInputBuffer(array, n, n2, array2, 0);
            final int fillOutputBuffer = this.fillOutputBuffer(prepareInputBuffer, (prepareInputBuffer == array) ? n : 0, array2, 0, (prepareInputBuffer == array) ? n2 : prepareInputBuffer.length, array);
            this.endDoFinal();
            if (fillOutputBuffer < array2.length) {
                final byte[] copy = Arrays.copyOf(array2, fillOutputBuffer);
                if (this.decrypting) {
                    Arrays.fill(array2, (byte)0);
                }
                return copy;
            }
            return array2;
        }
        catch (final ShortBufferException ex) {
            throw new ProviderException("Unexpected exception", ex);
        }
    }
    
    int doFinal(final byte[] array, final int n, final int n2, final byte[] array2, final int n3) throws IllegalBlockSizeException, ShortBufferException, BadPaddingException {
        this.checkReinit();
        final int outputSizeByOperation = this.getOutputSizeByOperation(n2, true);
        final int checkOutputCapacity = this.checkOutputCapacity(array2, n3, outputSizeByOperation);
        final int n4 = this.decrypting ? 0 : n3;
        final byte[] prepareInputBuffer = this.prepareInputBuffer(array, n, n2, array2, n3);
        byte[] array3 = null;
        final int n5 = (prepareInputBuffer == array) ? n : 0;
        final int n6 = (prepareInputBuffer == array) ? n2 : prepareInputBuffer.length;
        if (this.decrypting) {
            if (checkOutputCapacity < outputSizeByOperation) {
                this.cipher.save();
            }
            array3 = new byte[outputSizeByOperation];
        }
        final int fillOutputBuffer = this.fillOutputBuffer(prepareInputBuffer, n5, this.decrypting ? array3 : array2, n4, n6, array);
        if (this.decrypting) {
            if (checkOutputCapacity < fillOutputBuffer) {
                this.cipher.restore();
                throw new ShortBufferException("Output buffer too short: " + checkOutputCapacity + " bytes given, " + fillOutputBuffer + " bytes needed");
            }
            System.arraycopy(array3, 0, array2, n3, fillOutputBuffer);
            Arrays.fill(array3, (byte)0);
        }
        this.endDoFinal();
        return fillOutputBuffer;
    }
    
    private void endDoFinal() {
        this.buffered = 0;
        this.diffBlocksize = this.blockSize;
        if (this.cipherMode != 0) {
            this.cipher.reset();
        }
    }
    
    private int unpad(int n, final byte[] array) throws BadPaddingException {
        final int unpad = this.padding.unpad(array, 0, n);
        if (unpad < 0) {
            throw new BadPaddingException("Given final block not properly padded. Such issues can arise if a bad key is used during decryption.");
        }
        n = unpad;
        return n;
    }
    
    private byte[] prepareInputBuffer(final byte[] array, final int n, final int n2, final byte[] array2, final int n3) throws IllegalBlockSizeException, ShortBufferException {
        final int addExact = Math.addExact(this.buffered, n2);
        final int addExact2 = Math.addExact(addExact, this.cipher.getBufferedLength());
        int padLength = 0;
        if (this.unitBytes != this.blockSize) {
            if (addExact2 < this.diffBlocksize) {
                padLength = this.diffBlocksize - addExact2;
            }
            else {
                padLength = this.blockSize - (addExact2 - this.diffBlocksize) % this.blockSize;
            }
        }
        else if (this.padding != null) {
            padLength = this.padding.padLength(addExact2);
        }
        if (this.decrypting && this.padding != null && padLength > 0 && padLength != this.blockSize) {
            throw new IllegalBlockSizeException("Input length must be multiple of " + this.blockSize + " when decrypting with padded cipher");
        }
        if (this.buffered != 0 || (!this.decrypting && this.padding != null) || (array == array2 && n3 - n < n2 && n - n3 < this.buffer.length)) {
            if (this.decrypting || this.padding == null) {
                padLength = 0;
            }
            final byte[] array3 = new byte[Math.addExact(addExact, padLength)];
            if (this.buffered != 0) {
                System.arraycopy(this.buffer, 0, array3, 0, this.buffered);
                if (!this.decrypting) {
                    Arrays.fill(this.buffer, (byte)0);
                }
            }
            if (n2 != 0) {
                System.arraycopy(array, n, array3, this.buffered, n2);
            }
            if (padLength != 0) {
                this.padding.padWithLen(array3, Math.addExact(this.buffered, n2), padLength);
            }
            return array3;
        }
        return array;
    }
    
    private int fillOutputBuffer(final byte[] array, final int n, final byte[] array2, final int n2, final int n3, final byte[] array3) throws ShortBufferException, BadPaddingException, IllegalBlockSizeException {
        try {
            int n4 = this.finalNoPadding(array, n, array2, n2, n3);
            if (this.decrypting && this.padding != null) {
                n4 = this.unpad(n4, array2);
            }
            return n4;
        }
        finally {
            if (!this.decrypting) {
                this.requireReinit = (this.cipherMode == 7);
                if (array != array3) {
                    Arrays.fill(array, (byte)0);
                }
            }
        }
    }
    
    private int checkOutputCapacity(final byte[] array, final int n, final int n2) throws ShortBufferException {
        final int n3 = array.length - n;
        final int n4 = this.decrypting ? (n2 - this.blockSize) : n2;
        if (array == null || n3 < n4) {
            throw new ShortBufferException("Output buffer must be (at least) " + n4 + " bytes long");
        }
        return n3;
    }
    
    private void checkReinit() {
        if (this.requireReinit) {
            throw new IllegalStateException("Must use either different key or iv for GCM encryption");
        }
    }
    
    private int finalNoPadding(final byte[] array, final int n, final byte[] array2, final int n2, final int n3) throws IllegalBlockSizeException, AEADBadTagException, ShortBufferException {
        if (this.cipherMode != 7 && (array == null || n3 == 0)) {
            return 0;
        }
        if (this.cipherMode == 2 || this.cipherMode == 3 || this.cipherMode == 7 || n3 % this.unitBytes == 0 || this.cipherMode == 6) {
            int n4;
            if (this.decrypting) {
                n4 = this.cipher.decryptFinal(array, n, n3, array2, n2);
            }
            else {
                n4 = this.cipher.encryptFinal(array, n, n3, array2, n2);
            }
            return n4;
        }
        if (this.padding != null) {
            throw new IllegalBlockSizeException("Input length (with padding) not multiple of " + this.unitBytes + " bytes");
        }
        throw new IllegalBlockSizeException("Input length not multiple of " + this.unitBytes + " bytes");
    }
    
    byte[] wrap(final Key key) throws IllegalBlockSizeException, InvalidKeyException {
        byte[] doFinal = null;
        try {
            final byte[] encoded = key.getEncoded();
            if (encoded == null || encoded.length == 0) {
                throw new InvalidKeyException("Cannot get an encoding of the key to be wrapped");
            }
            doFinal = this.doFinal(encoded, 0, encoded.length);
        }
        catch (final BadPaddingException ex) {}
        return doFinal;
    }
    
    Key unwrap(final byte[] array, final String s, final int n) throws InvalidKeyException, NoSuchAlgorithmException {
        byte[] doFinal;
        try {
            doFinal = this.doFinal(array, 0, array.length);
        }
        catch (final BadPaddingException ex) {
            throw new InvalidKeyException("The wrapped key is not padded correctly");
        }
        catch (final IllegalBlockSizeException ex2) {
            throw new InvalidKeyException("The wrapped key does not have the correct length");
        }
        return ConstructKeys.constructKey(doFinal, s, n);
    }
    
    void updateAAD(final byte[] array, final int n, final int n2) {
        this.checkReinit();
        this.cipher.updateAAD(array, n, n2);
    }
}
