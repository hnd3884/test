package com.sun.crypto.provider;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.SecretKey;
import javax.crypto.interfaces.PBEKey;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import javax.crypto.CipherSpi;
import java.security.SecureRandom;
import java.security.Key;
import java.security.spec.InvalidParameterSpecException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.Provider;
import javax.crypto.spec.PBEParameterSpec;
import java.security.AlgorithmParameters;
import javax.crypto.NoSuchPaddingException;
import java.security.NoSuchAlgorithmException;
import java.math.BigInteger;
import java.util.Arrays;
import java.security.MessageDigest;

final class PKCS12PBECipherCore
{
    private CipherCore cipher;
    private int blockSize;
    private int keySize;
    private String algo;
    private String pbeAlgo;
    private byte[] salt;
    private int iCount;
    private static final int DEFAULT_SALT_LENGTH = 20;
    private static final int DEFAULT_COUNT = 1024;
    static final int CIPHER_KEY = 1;
    static final int CIPHER_IV = 2;
    static final int MAC_KEY = 3;
    
    static byte[] derive(final char[] array, final byte[] array2, final int n, final int n2, final int n3) {
        return derive(array, array2, n, n2, n3, "SHA-1", 64);
    }
    
    static byte[] derive(char[] array, final byte[] array2, final int n, int n2, final int n3, final String s, final int n4) {
        int n5 = array.length * 2;
        if (n5 == 2 && array[0] == '\0') {
            array = new char[0];
            n5 = 0;
        }
        else {
            n5 += 2;
        }
        final byte[] array3 = new byte[n5];
        for (int i = 0, n6 = 0; i < array.length; ++i, n6 += 2) {
            array3[n6] = (byte)(array[i] >>> 8 & 0xFF);
            array3[n6 + 1] = (byte)(array[i] & '\u00ff');
        }
        final byte[] array4 = new byte[n2];
        try {
            final MessageDigest instance = MessageDigest.getInstance(s);
            final int digestLength = instance.getDigestLength();
            final int n7 = roundup(n2, digestLength) / digestLength;
            final byte[] array5 = new byte[n4];
            final int roundup = roundup(array2.length, n4);
            final int roundup2 = roundup(array3.length, n4);
            final byte[] array6 = new byte[roundup + roundup2];
            Arrays.fill(array5, (byte)n3);
            concat(array2, array6, 0, roundup);
            concat(array3, array6, roundup, roundup2);
            Arrays.fill(array3, (byte)0);
            final byte[] array7 = new byte[n4];
            byte[] byteArray = new byte[n4];
            int n8 = 0;
            while (true) {
                instance.update(array5);
                instance.update(array6);
                byte[] array8 = instance.digest();
                for (int j = 1; j < n; ++j) {
                    array8 = instance.digest(array8);
                }
                System.arraycopy(array8, 0, array4, digestLength * n8, Math.min(n2, digestLength));
                if (n8 + 1 == n7) {
                    break;
                }
                concat(array8, array7, 0, array7.length);
                final BigInteger add = new BigInteger(1, array7).add(BigInteger.ONE);
                for (int k = 0; k < array6.length; k += n4) {
                    if (byteArray.length != n4) {
                        byteArray = new byte[n4];
                    }
                    System.arraycopy(array6, k, byteArray, 0, n4);
                    byteArray = new BigInteger(1, byteArray).add(add).toByteArray();
                    final int n9 = byteArray.length - n4;
                    if (n9 >= 0) {
                        System.arraycopy(byteArray, n9, array6, k, n4);
                    }
                    else if (n9 < 0) {
                        Arrays.fill(array6, k, k + -n9, (byte)0);
                        System.arraycopy(byteArray, 0, array6, k + -n9, byteArray.length);
                    }
                }
                ++n8;
                n2 -= digestLength;
            }
        }
        catch (final Exception ex) {
            throw new RuntimeException("internal error: " + ex);
        }
        return array4;
    }
    
    private static int roundup(final int n, final int n2) {
        return (n + (n2 - 1)) / n2 * n2;
    }
    
    private static void concat(final byte[] array, final byte[] array2, final int n, final int n2) {
        if (array.length == 0) {
            return;
        }
        int n3;
        int i;
        int n4;
        for (n3 = n2 / array.length, i = 0, n4 = 0; i < n3; ++i, n4 += array.length) {
            System.arraycopy(array, 0, array2, n4 + n, array.length);
        }
        System.arraycopy(array, 0, array2, n4 + n, n2 - n4);
    }
    
    PKCS12PBECipherCore(final String algo, final int keySize) throws NoSuchAlgorithmException {
        this.algo = null;
        this.pbeAlgo = null;
        this.salt = null;
        this.iCount = 0;
        this.algo = algo;
        if (this.algo.equals("RC4")) {
            this.pbeAlgo = "PBEWithSHA1AndRC4_" + keySize * 8;
        }
        else {
            SymmetricCipher symmetricCipher;
            if (this.algo.equals("DESede")) {
                symmetricCipher = new DESedeCrypt();
                this.pbeAlgo = "PBEWithSHA1AndDESede";
            }
            else {
                if (!this.algo.equals("RC2")) {
                    throw new NoSuchAlgorithmException("No Cipher implementation for PBEWithSHA1And" + this.algo);
                }
                symmetricCipher = new RC2Crypt();
                this.pbeAlgo = "PBEWithSHA1AndRC2_" + keySize * 8;
            }
            this.blockSize = symmetricCipher.getBlockSize();
            (this.cipher = new CipherCore(symmetricCipher, this.blockSize)).setMode("CBC");
            try {
                this.cipher.setPadding("PKCS5Padding");
            }
            catch (final NoSuchPaddingException ex) {}
        }
        this.keySize = keySize;
    }
    
    void implSetMode(final String s) throws NoSuchAlgorithmException {
        if (s != null && !s.equalsIgnoreCase("CBC")) {
            throw new NoSuchAlgorithmException("Invalid cipher mode: " + s);
        }
    }
    
    void implSetPadding(final String s) throws NoSuchPaddingException {
        if (s != null && !s.equalsIgnoreCase("PKCS5Padding")) {
            throw new NoSuchPaddingException("Invalid padding scheme: " + s);
        }
    }
    
    int implGetBlockSize() {
        return this.blockSize;
    }
    
    int implGetOutputSize(final int n) {
        return this.cipher.getOutputSize(n);
    }
    
    byte[] implGetIV() {
        return this.cipher.getIV();
    }
    
    AlgorithmParameters implGetParameters() {
        if (this.salt == null) {
            this.salt = new byte[20];
            SunJCE.getRandom().nextBytes(this.salt);
            this.iCount = 1024;
        }
        final PBEParameterSpec pbeParameterSpec = new PBEParameterSpec(this.salt, this.iCount);
        AlgorithmParameters instance;
        try {
            instance = AlgorithmParameters.getInstance(this.pbeAlgo, SunJCE.getInstance());
            instance.init(pbeParameterSpec);
        }
        catch (final NoSuchAlgorithmException ex) {
            throw new RuntimeException("SunJCE provider is not configured properly");
        }
        catch (final InvalidParameterSpecException ex2) {
            throw new RuntimeException("PBEParameterSpec not supported");
        }
        return instance;
    }
    
    void implInit(final int n, final Key key, final AlgorithmParameterSpec algorithmParameterSpec, final SecureRandom secureRandom) throws InvalidKeyException, InvalidAlgorithmParameterException {
        this.implInit(n, key, algorithmParameterSpec, secureRandom, null);
    }
    
    void implInit(final int n, final Key key, final AlgorithmParameterSpec algorithmParameterSpec, final SecureRandom secureRandom, final CipherSpi cipherSpi) throws InvalidKeyException, InvalidAlgorithmParameterException {
        this.salt = null;
        this.iCount = 0;
        char[] password;
        if (key instanceof PBEKey) {
            final PBEKey pbeKey = (PBEKey)key;
            password = pbeKey.getPassword();
            this.salt = pbeKey.getSalt();
            this.iCount = pbeKey.getIterationCount();
        }
        else {
            if (!(key instanceof SecretKey)) {
                throw new InvalidKeyException("SecretKey of PBE type required");
            }
            final byte[] encoded;
            if (!key.getAlgorithm().regionMatches(true, 0, "PBE", 0, 3) || (encoded = key.getEncoded()) == null) {
                throw new InvalidKeyException("Missing password");
            }
            password = new char[encoded.length];
            for (int i = 0; i < password.length; ++i) {
                password[i] = (char)(encoded[i] & 0x7F);
            }
            Arrays.fill(encoded, (byte)0);
        }
        try {
            if ((n == 2 || n == 4) && algorithmParameterSpec == null && (this.salt == null || this.iCount == 0)) {
                throw new InvalidAlgorithmParameterException("Parameters missing");
            }
            if (algorithmParameterSpec == null) {
                if (this.salt == null) {
                    this.salt = new byte[20];
                    if (secureRandom != null) {
                        secureRandom.nextBytes(this.salt);
                    }
                    else {
                        SunJCE.getRandom().nextBytes(this.salt);
                    }
                }
                if (this.iCount == 0) {
                    this.iCount = 1024;
                }
            }
            else {
                if (!(algorithmParameterSpec instanceof PBEParameterSpec)) {
                    throw new InvalidAlgorithmParameterException("PBEParameterSpec type required");
                }
                final PBEParameterSpec pbeParameterSpec = (PBEParameterSpec)algorithmParameterSpec;
                if (this.salt != null) {
                    if (!Arrays.equals(this.salt, pbeParameterSpec.getSalt())) {
                        throw new InvalidAlgorithmParameterException("Inconsistent value of salt between key and params");
                    }
                }
                else {
                    this.salt = pbeParameterSpec.getSalt();
                }
                if (this.iCount != 0) {
                    if (this.iCount != pbeParameterSpec.getIterationCount()) {
                        throw new InvalidAlgorithmParameterException("Different iteration count between key and params");
                    }
                }
                else {
                    this.iCount = pbeParameterSpec.getIterationCount();
                }
            }
            if (this.salt.length < 8) {
                throw new InvalidAlgorithmParameterException("Salt must be at least 8 bytes long");
            }
            if (this.iCount <= 0) {
                throw new InvalidAlgorithmParameterException("IterationCount must be a positive number");
            }
            final SecretKeySpec secretKeySpec = new SecretKeySpec(derive(password, this.salt, this.iCount, this.keySize, 1), this.algo);
            if (cipherSpi != null && cipherSpi instanceof ARCFOURCipher) {
                ((ARCFOURCipher)cipherSpi).engineInit(n, secretKeySpec, secureRandom);
            }
            else {
                this.cipher.init(n, secretKeySpec, new IvParameterSpec(derive(password, this.salt, this.iCount, 8, 2), 0, 8), secureRandom);
            }
        }
        finally {
            Arrays.fill(password, '\0');
        }
    }
    
    void implInit(final int n, final Key key, final AlgorithmParameters algorithmParameters, final SecureRandom secureRandom) throws InvalidKeyException, InvalidAlgorithmParameterException {
        this.implInit(n, key, algorithmParameters, secureRandom, null);
    }
    
    void implInit(final int n, final Key key, final AlgorithmParameters algorithmParameters, final SecureRandom secureRandom, final CipherSpi cipherSpi) throws InvalidKeyException, InvalidAlgorithmParameterException {
        AlgorithmParameterSpec parameterSpec = null;
        if (algorithmParameters != null) {
            try {
                parameterSpec = algorithmParameters.getParameterSpec(PBEParameterSpec.class);
            }
            catch (final InvalidParameterSpecException ex) {
                throw new InvalidAlgorithmParameterException("requires PBE parameters");
            }
        }
        this.implInit(n, key, parameterSpec, secureRandom, cipherSpi);
    }
    
    void implInit(final int n, final Key key, final SecureRandom secureRandom) throws InvalidKeyException {
        this.implInit(n, key, secureRandom, null);
    }
    
    void implInit(final int n, final Key key, final SecureRandom secureRandom, final CipherSpi cipherSpi) throws InvalidKeyException {
        try {
            this.implInit(n, key, (AlgorithmParameterSpec)null, secureRandom, cipherSpi);
        }
        catch (final InvalidAlgorithmParameterException ex) {
            throw new InvalidKeyException("requires PBE parameters");
        }
    }
    
    byte[] implUpdate(final byte[] array, final int n, final int n2) {
        return this.cipher.update(array, n, n2);
    }
    
    int implUpdate(final byte[] array, final int n, final int n2, final byte[] array2, final int n3) throws ShortBufferException {
        return this.cipher.update(array, n, n2, array2, n3);
    }
    
    byte[] implDoFinal(final byte[] array, final int n, final int n2) throws IllegalBlockSizeException, BadPaddingException {
        return this.cipher.doFinal(array, n, n2);
    }
    
    int implDoFinal(final byte[] array, final int n, final int n2, final byte[] array2, final int n3) throws ShortBufferException, IllegalBlockSizeException, BadPaddingException {
        return this.cipher.doFinal(array, n, n2, array2, n3);
    }
    
    int implGetKeySize(final Key key) throws InvalidKeyException {
        return this.keySize;
    }
    
    byte[] implWrap(final Key key) throws IllegalBlockSizeException, InvalidKeyException {
        return this.cipher.wrap(key);
    }
    
    Key implUnwrap(final byte[] array, final String s, final int n) throws InvalidKeyException, NoSuchAlgorithmException {
        return this.cipher.unwrap(array, s, n);
    }
    
    public static final class PBEWithSHA1AndDESede extends CipherSpi
    {
        private final PKCS12PBECipherCore core;
        
        public PBEWithSHA1AndDESede() throws NoSuchAlgorithmException {
            this.core = new PKCS12PBECipherCore("DESede", 24);
        }
        
        @Override
        protected byte[] engineDoFinal(final byte[] array, final int n, final int n2) throws IllegalBlockSizeException, BadPaddingException {
            return this.core.implDoFinal(array, n, n2);
        }
        
        @Override
        protected int engineDoFinal(final byte[] array, final int n, final int n2, final byte[] array2, final int n3) throws ShortBufferException, IllegalBlockSizeException, BadPaddingException {
            return this.core.implDoFinal(array, n, n2, array2, n3);
        }
        
        @Override
        protected int engineGetBlockSize() {
            return this.core.implGetBlockSize();
        }
        
        @Override
        protected byte[] engineGetIV() {
            return this.core.implGetIV();
        }
        
        @Override
        protected int engineGetKeySize(final Key key) throws InvalidKeyException {
            return this.core.implGetKeySize(key);
        }
        
        @Override
        protected int engineGetOutputSize(final int n) {
            return this.core.implGetOutputSize(n);
        }
        
        @Override
        protected AlgorithmParameters engineGetParameters() {
            return this.core.implGetParameters();
        }
        
        @Override
        protected void engineInit(final int n, final Key key, final AlgorithmParameterSpec algorithmParameterSpec, final SecureRandom secureRandom) throws InvalidKeyException, InvalidAlgorithmParameterException {
            this.core.implInit(n, key, algorithmParameterSpec, secureRandom);
        }
        
        @Override
        protected void engineInit(final int n, final Key key, final AlgorithmParameters algorithmParameters, final SecureRandom secureRandom) throws InvalidKeyException, InvalidAlgorithmParameterException {
            this.core.implInit(n, key, algorithmParameters, secureRandom);
        }
        
        @Override
        protected void engineInit(final int n, final Key key, final SecureRandom secureRandom) throws InvalidKeyException {
            this.core.implInit(n, key, secureRandom);
        }
        
        @Override
        protected void engineSetMode(final String s) throws NoSuchAlgorithmException {
            this.core.implSetMode(s);
        }
        
        @Override
        protected void engineSetPadding(final String s) throws NoSuchPaddingException {
            this.core.implSetPadding(s);
        }
        
        @Override
        protected Key engineUnwrap(final byte[] array, final String s, final int n) throws InvalidKeyException, NoSuchAlgorithmException {
            return this.core.implUnwrap(array, s, n);
        }
        
        @Override
        protected byte[] engineUpdate(final byte[] array, final int n, final int n2) {
            return this.core.implUpdate(array, n, n2);
        }
        
        @Override
        protected int engineUpdate(final byte[] array, final int n, final int n2, final byte[] array2, final int n3) throws ShortBufferException {
            return this.core.implUpdate(array, n, n2, array2, n3);
        }
        
        @Override
        protected byte[] engineWrap(final Key key) throws IllegalBlockSizeException, InvalidKeyException {
            return this.core.implWrap(key);
        }
    }
    
    public static final class PBEWithSHA1AndRC2_40 extends CipherSpi
    {
        private final PKCS12PBECipherCore core;
        
        public PBEWithSHA1AndRC2_40() throws NoSuchAlgorithmException {
            this.core = new PKCS12PBECipherCore("RC2", 5);
        }
        
        @Override
        protected byte[] engineDoFinal(final byte[] array, final int n, final int n2) throws IllegalBlockSizeException, BadPaddingException {
            return this.core.implDoFinal(array, n, n2);
        }
        
        @Override
        protected int engineDoFinal(final byte[] array, final int n, final int n2, final byte[] array2, final int n3) throws ShortBufferException, IllegalBlockSizeException, BadPaddingException {
            return this.core.implDoFinal(array, n, n2, array2, n3);
        }
        
        @Override
        protected int engineGetBlockSize() {
            return this.core.implGetBlockSize();
        }
        
        @Override
        protected byte[] engineGetIV() {
            return this.core.implGetIV();
        }
        
        @Override
        protected int engineGetKeySize(final Key key) throws InvalidKeyException {
            return this.core.implGetKeySize(key);
        }
        
        @Override
        protected int engineGetOutputSize(final int n) {
            return this.core.implGetOutputSize(n);
        }
        
        @Override
        protected AlgorithmParameters engineGetParameters() {
            return this.core.implGetParameters();
        }
        
        @Override
        protected void engineInit(final int n, final Key key, final AlgorithmParameterSpec algorithmParameterSpec, final SecureRandom secureRandom) throws InvalidKeyException, InvalidAlgorithmParameterException {
            this.core.implInit(n, key, algorithmParameterSpec, secureRandom);
        }
        
        @Override
        protected void engineInit(final int n, final Key key, final AlgorithmParameters algorithmParameters, final SecureRandom secureRandom) throws InvalidKeyException, InvalidAlgorithmParameterException {
            this.core.implInit(n, key, algorithmParameters, secureRandom);
        }
        
        @Override
        protected void engineInit(final int n, final Key key, final SecureRandom secureRandom) throws InvalidKeyException {
            this.core.implInit(n, key, secureRandom);
        }
        
        @Override
        protected void engineSetMode(final String s) throws NoSuchAlgorithmException {
            this.core.implSetMode(s);
        }
        
        @Override
        protected void engineSetPadding(final String s) throws NoSuchPaddingException {
            this.core.implSetPadding(s);
        }
        
        @Override
        protected Key engineUnwrap(final byte[] array, final String s, final int n) throws InvalidKeyException, NoSuchAlgorithmException {
            return this.core.implUnwrap(array, s, n);
        }
        
        @Override
        protected byte[] engineUpdate(final byte[] array, final int n, final int n2) {
            return this.core.implUpdate(array, n, n2);
        }
        
        @Override
        protected int engineUpdate(final byte[] array, final int n, final int n2, final byte[] array2, final int n3) throws ShortBufferException {
            return this.core.implUpdate(array, n, n2, array2, n3);
        }
        
        @Override
        protected byte[] engineWrap(final Key key) throws IllegalBlockSizeException, InvalidKeyException {
            return this.core.implWrap(key);
        }
    }
    
    public static final class PBEWithSHA1AndRC2_128 extends CipherSpi
    {
        private final PKCS12PBECipherCore core;
        
        public PBEWithSHA1AndRC2_128() throws NoSuchAlgorithmException {
            this.core = new PKCS12PBECipherCore("RC2", 16);
        }
        
        @Override
        protected byte[] engineDoFinal(final byte[] array, final int n, final int n2) throws IllegalBlockSizeException, BadPaddingException {
            return this.core.implDoFinal(array, n, n2);
        }
        
        @Override
        protected int engineDoFinal(final byte[] array, final int n, final int n2, final byte[] array2, final int n3) throws ShortBufferException, IllegalBlockSizeException, BadPaddingException {
            return this.core.implDoFinal(array, n, n2, array2, n3);
        }
        
        @Override
        protected int engineGetBlockSize() {
            return this.core.implGetBlockSize();
        }
        
        @Override
        protected byte[] engineGetIV() {
            return this.core.implGetIV();
        }
        
        @Override
        protected int engineGetKeySize(final Key key) throws InvalidKeyException {
            return this.core.implGetKeySize(key);
        }
        
        @Override
        protected int engineGetOutputSize(final int n) {
            return this.core.implGetOutputSize(n);
        }
        
        @Override
        protected AlgorithmParameters engineGetParameters() {
            return this.core.implGetParameters();
        }
        
        @Override
        protected void engineInit(final int n, final Key key, final AlgorithmParameterSpec algorithmParameterSpec, final SecureRandom secureRandom) throws InvalidKeyException, InvalidAlgorithmParameterException {
            this.core.implInit(n, key, algorithmParameterSpec, secureRandom);
        }
        
        @Override
        protected void engineInit(final int n, final Key key, final AlgorithmParameters algorithmParameters, final SecureRandom secureRandom) throws InvalidKeyException, InvalidAlgorithmParameterException {
            this.core.implInit(n, key, algorithmParameters, secureRandom);
        }
        
        @Override
        protected void engineInit(final int n, final Key key, final SecureRandom secureRandom) throws InvalidKeyException {
            this.core.implInit(n, key, secureRandom);
        }
        
        @Override
        protected void engineSetMode(final String s) throws NoSuchAlgorithmException {
            this.core.implSetMode(s);
        }
        
        @Override
        protected void engineSetPadding(final String s) throws NoSuchPaddingException {
            this.core.implSetPadding(s);
        }
        
        @Override
        protected Key engineUnwrap(final byte[] array, final String s, final int n) throws InvalidKeyException, NoSuchAlgorithmException {
            return this.core.implUnwrap(array, s, n);
        }
        
        @Override
        protected byte[] engineUpdate(final byte[] array, final int n, final int n2) {
            return this.core.implUpdate(array, n, n2);
        }
        
        @Override
        protected int engineUpdate(final byte[] array, final int n, final int n2, final byte[] array2, final int n3) throws ShortBufferException {
            return this.core.implUpdate(array, n, n2, array2, n3);
        }
        
        @Override
        protected byte[] engineWrap(final Key key) throws IllegalBlockSizeException, InvalidKeyException {
            return this.core.implWrap(key);
        }
    }
    
    public static final class PBEWithSHA1AndRC4_40 extends CipherSpi
    {
        private static final int RC4_KEYSIZE = 5;
        private final PKCS12PBECipherCore core;
        private final ARCFOURCipher cipher;
        
        public PBEWithSHA1AndRC4_40() throws NoSuchAlgorithmException {
            this.core = new PKCS12PBECipherCore("RC4", 5);
            this.cipher = new ARCFOURCipher();
        }
        
        @Override
        protected byte[] engineDoFinal(final byte[] array, final int n, final int n2) throws IllegalBlockSizeException, BadPaddingException {
            return this.cipher.engineDoFinal(array, n, n2);
        }
        
        @Override
        protected int engineDoFinal(final byte[] array, final int n, final int n2, final byte[] array2, final int n3) throws ShortBufferException, IllegalBlockSizeException, BadPaddingException {
            return this.cipher.engineDoFinal(array, n, n2, array2, n3);
        }
        
        @Override
        protected int engineGetBlockSize() {
            return this.cipher.engineGetBlockSize();
        }
        
        @Override
        protected byte[] engineGetIV() {
            return this.cipher.engineGetIV();
        }
        
        @Override
        protected int engineGetKeySize(final Key key) throws InvalidKeyException {
            return 5;
        }
        
        @Override
        protected int engineGetOutputSize(final int n) {
            return this.cipher.engineGetOutputSize(n);
        }
        
        @Override
        protected AlgorithmParameters engineGetParameters() {
            return this.core.implGetParameters();
        }
        
        @Override
        protected void engineInit(final int n, final Key key, final AlgorithmParameterSpec algorithmParameterSpec, final SecureRandom secureRandom) throws InvalidKeyException, InvalidAlgorithmParameterException {
            this.core.implInit(n, key, algorithmParameterSpec, secureRandom, this.cipher);
        }
        
        @Override
        protected void engineInit(final int n, final Key key, final AlgorithmParameters algorithmParameters, final SecureRandom secureRandom) throws InvalidKeyException, InvalidAlgorithmParameterException {
            this.core.implInit(n, key, algorithmParameters, secureRandom, this.cipher);
        }
        
        @Override
        protected void engineInit(final int n, final Key key, final SecureRandom secureRandom) throws InvalidKeyException {
            this.core.implInit(n, key, secureRandom, this.cipher);
        }
        
        @Override
        protected void engineSetMode(final String s) throws NoSuchAlgorithmException {
            if (!s.equalsIgnoreCase("ECB")) {
                throw new NoSuchAlgorithmException("Unsupported mode " + s);
            }
        }
        
        @Override
        protected void engineSetPadding(final String s) throws NoSuchPaddingException {
            if (!s.equalsIgnoreCase("NoPadding")) {
                throw new NoSuchPaddingException("Padding must be NoPadding");
            }
        }
        
        @Override
        protected Key engineUnwrap(final byte[] array, final String s, final int n) throws InvalidKeyException, NoSuchAlgorithmException {
            return this.cipher.engineUnwrap(array, s, n);
        }
        
        @Override
        protected byte[] engineUpdate(final byte[] array, final int n, final int n2) {
            return this.cipher.engineUpdate(array, n, n2);
        }
        
        @Override
        protected int engineUpdate(final byte[] array, final int n, final int n2, final byte[] array2, final int n3) throws ShortBufferException {
            return this.cipher.engineUpdate(array, n, n2, array2, n3);
        }
        
        @Override
        protected byte[] engineWrap(final Key key) throws IllegalBlockSizeException, InvalidKeyException {
            return this.cipher.engineWrap(key);
        }
    }
    
    public static final class PBEWithSHA1AndRC4_128 extends CipherSpi
    {
        private static final int RC4_KEYSIZE = 16;
        private final PKCS12PBECipherCore core;
        private final ARCFOURCipher cipher;
        
        public PBEWithSHA1AndRC4_128() throws NoSuchAlgorithmException {
            this.core = new PKCS12PBECipherCore("RC4", 16);
            this.cipher = new ARCFOURCipher();
        }
        
        @Override
        protected byte[] engineDoFinal(final byte[] array, final int n, final int n2) throws IllegalBlockSizeException, BadPaddingException {
            return this.cipher.engineDoFinal(array, n, n2);
        }
        
        @Override
        protected int engineDoFinal(final byte[] array, final int n, final int n2, final byte[] array2, final int n3) throws ShortBufferException, IllegalBlockSizeException, BadPaddingException {
            return this.cipher.engineDoFinal(array, n, n2, array2, n3);
        }
        
        @Override
        protected int engineGetBlockSize() {
            return this.cipher.engineGetBlockSize();
        }
        
        @Override
        protected byte[] engineGetIV() {
            return this.cipher.engineGetIV();
        }
        
        @Override
        protected int engineGetKeySize(final Key key) throws InvalidKeyException {
            return 16;
        }
        
        @Override
        protected int engineGetOutputSize(final int n) {
            return this.cipher.engineGetOutputSize(n);
        }
        
        @Override
        protected AlgorithmParameters engineGetParameters() {
            return this.core.implGetParameters();
        }
        
        @Override
        protected void engineInit(final int n, final Key key, final AlgorithmParameterSpec algorithmParameterSpec, final SecureRandom secureRandom) throws InvalidKeyException, InvalidAlgorithmParameterException {
            this.core.implInit(n, key, algorithmParameterSpec, secureRandom, this.cipher);
        }
        
        @Override
        protected void engineInit(final int n, final Key key, final AlgorithmParameters algorithmParameters, final SecureRandom secureRandom) throws InvalidKeyException, InvalidAlgorithmParameterException {
            this.core.implInit(n, key, algorithmParameters, secureRandom, this.cipher);
        }
        
        @Override
        protected void engineInit(final int n, final Key key, final SecureRandom secureRandom) throws InvalidKeyException {
            this.core.implInit(n, key, secureRandom, this.cipher);
        }
        
        @Override
        protected void engineSetMode(final String s) throws NoSuchAlgorithmException {
            if (!s.equalsIgnoreCase("ECB")) {
                throw new NoSuchAlgorithmException("Unsupported mode " + s);
            }
        }
        
        @Override
        protected void engineSetPadding(final String s) throws NoSuchPaddingException {
            if (!s.equalsIgnoreCase("NoPadding")) {
                throw new NoSuchPaddingException("Padding must be NoPadding");
            }
        }
        
        @Override
        protected Key engineUnwrap(final byte[] array, final String s, final int n) throws InvalidKeyException, NoSuchAlgorithmException {
            return this.cipher.engineUnwrap(array, s, n);
        }
        
        @Override
        protected byte[] engineUpdate(final byte[] array, final int n, final int n2) {
            return this.cipher.engineUpdate(array, n, n2);
        }
        
        @Override
        protected int engineUpdate(final byte[] array, final int n, final int n2, final byte[] array2, final int n3) throws ShortBufferException {
            return this.cipher.engineUpdate(array, n, n2, array2, n3);
        }
        
        @Override
        protected byte[] engineWrap(final Key key) throws IllegalBlockSizeException, InvalidKeyException {
            return this.cipher.engineWrap(key);
        }
    }
}
