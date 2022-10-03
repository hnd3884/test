package com.sun.crypto.provider;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.ShortBufferException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.interfaces.PBEKey;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.SecureRandom;
import java.security.Key;
import java.security.spec.InvalidParameterSpecException;
import java.security.Provider;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.spec.PBEParameterSpec;
import java.security.AlgorithmParameters;
import javax.crypto.NoSuchPaddingException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.CipherSpi;

abstract class PBES2Core extends CipherSpi
{
    private static final int DEFAULT_SALT_LENGTH = 20;
    private static final int DEFAULT_COUNT = 4096;
    private final CipherCore cipher;
    private final int keyLength;
    private final int blkSize;
    private final PBKDF2Core kdf;
    private final String pbeAlgo;
    private final String cipherAlgo;
    private int iCount;
    private byte[] salt;
    private IvParameterSpec ivSpec;
    
    PBES2Core(final String s, final String cipherAlgo, final int n) throws NoSuchAlgorithmException, NoSuchPaddingException {
        this.iCount = 4096;
        this.salt = null;
        this.ivSpec = null;
        this.cipherAlgo = cipherAlgo;
        this.keyLength = n * 8;
        this.pbeAlgo = "PBEWith" + s + "And" + cipherAlgo + "_" + this.keyLength;
        if (cipherAlgo.equals("AES")) {
            this.blkSize = 16;
            this.cipher = new CipherCore(new AESCrypt(), this.blkSize);
            switch (s) {
                case "HmacSHA1": {
                    this.kdf = new PBKDF2Core.HmacSHA1();
                    break;
                }
                case "HmacSHA224": {
                    this.kdf = new PBKDF2Core.HmacSHA224();
                    break;
                }
                case "HmacSHA256": {
                    this.kdf = new PBKDF2Core.HmacSHA256();
                    break;
                }
                case "HmacSHA384": {
                    this.kdf = new PBKDF2Core.HmacSHA384();
                    break;
                }
                case "HmacSHA512": {
                    this.kdf = new PBKDF2Core.HmacSHA512();
                    break;
                }
                default: {
                    throw new NoSuchAlgorithmException("No Cipher implementation for " + s);
                }
            }
            this.cipher.setMode("CBC");
            this.cipher.setPadding("PKCS5Padding");
            return;
        }
        throw new NoSuchAlgorithmException("No Cipher implementation for " + this.pbeAlgo);
    }
    
    @Override
    protected void engineSetMode(final String s) throws NoSuchAlgorithmException {
        if (s != null && !s.equalsIgnoreCase("CBC")) {
            throw new NoSuchAlgorithmException("Invalid cipher mode: " + s);
        }
    }
    
    @Override
    protected void engineSetPadding(final String s) throws NoSuchPaddingException {
        if (s != null && !s.equalsIgnoreCase("PKCS5Padding")) {
            throw new NoSuchPaddingException("Invalid padding scheme: " + s);
        }
    }
    
    @Override
    protected int engineGetBlockSize() {
        return this.blkSize;
    }
    
    @Override
    protected int engineGetOutputSize(final int n) {
        return this.cipher.getOutputSize(n);
    }
    
    @Override
    protected byte[] engineGetIV() {
        return this.cipher.getIV();
    }
    
    @Override
    protected AlgorithmParameters engineGetParameters() {
        if (this.salt == null) {
            this.salt = new byte[20];
            SunJCE.getRandom().nextBytes(this.salt);
            this.iCount = 4096;
        }
        if (this.ivSpec == null) {
            final byte[] array = new byte[this.blkSize];
            SunJCE.getRandom().nextBytes(array);
            this.ivSpec = new IvParameterSpec(array);
        }
        final PBEParameterSpec pbeParameterSpec = new PBEParameterSpec(this.salt, this.iCount, this.ivSpec);
        AlgorithmParameters instance;
        try {
            instance = AlgorithmParameters.getInstance(this.pbeAlgo, SunJCE.getInstance());
            instance.init(pbeParameterSpec);
        }
        catch (final NoSuchAlgorithmException ex) {
            throw new RuntimeException("SunJCE called, but not configured");
        }
        catch (final InvalidParameterSpecException ex2) {
            throw new RuntimeException("PBEParameterSpec not supported");
        }
        return instance;
    }
    
    @Override
    protected void engineInit(final int n, final Key key, final SecureRandom secureRandom) throws InvalidKeyException {
        try {
            this.engineInit(n, key, (AlgorithmParameterSpec)null, secureRandom);
        }
        catch (final InvalidAlgorithmParameterException ex) {
            final InvalidKeyException ex2 = new InvalidKeyException("requires PBE parameters");
            ex2.initCause(ex);
            throw ex2;
        }
    }
    
    @Override
    protected void engineInit(final int n, final Key key, final AlgorithmParameterSpec algorithmParameterSpec, final SecureRandom secureRandom) throws InvalidKeyException, InvalidAlgorithmParameterException {
        if (key == null) {
            throw new InvalidKeyException("Null key");
        }
        final byte[] encoded = key.getEncoded();
        char[] array = null;
        PBEKeySpec pbeKeySpec;
        try {
            if (encoded == null || !key.getAlgorithm().regionMatches(true, 0, "PBE", 0, 3)) {
                throw new InvalidKeyException("Missing password");
            }
            if (key instanceof PBEKey) {
                this.salt = ((PBEKey)key).getSalt();
                if (this.salt != null && this.salt.length < 8) {
                    throw new InvalidAlgorithmParameterException("Salt must be at least 8 bytes long");
                }
                this.iCount = ((PBEKey)key).getIterationCount();
                if (this.iCount == 0) {
                    this.iCount = 4096;
                }
                else if (this.iCount < 0) {
                    throw new InvalidAlgorithmParameterException("Iteration count must be a positive number");
                }
            }
            if (algorithmParameterSpec == null) {
                if (this.salt == null) {
                    secureRandom.nextBytes(this.salt = new byte[20]);
                    this.iCount = 4096;
                }
                if (n == 1 || n == 3) {
                    final byte[] array2 = new byte[this.blkSize];
                    secureRandom.nextBytes(array2);
                    this.ivSpec = new IvParameterSpec(array2);
                }
            }
            else {
                if (!(algorithmParameterSpec instanceof PBEParameterSpec)) {
                    throw new InvalidAlgorithmParameterException("Wrong parameter type: PBE expected");
                }
                final byte[] salt = ((PBEParameterSpec)algorithmParameterSpec).getSalt();
                if (salt != null && salt.length < 8) {
                    throw new InvalidAlgorithmParameterException("Salt must be at least 8 bytes long");
                }
                this.salt = salt;
                int iterationCount = ((PBEParameterSpec)algorithmParameterSpec).getIterationCount();
                if (iterationCount == 0) {
                    iterationCount = 4096;
                }
                else if (iterationCount < 0) {
                    throw new InvalidAlgorithmParameterException("Iteration count must be a positive number");
                }
                this.iCount = iterationCount;
                final AlgorithmParameterSpec parameterSpec = ((PBEParameterSpec)algorithmParameterSpec).getParameterSpec();
                if (parameterSpec != null) {
                    if (!(parameterSpec instanceof IvParameterSpec)) {
                        throw new InvalidAlgorithmParameterException("Wrong parameter type: IV expected");
                    }
                    this.ivSpec = (IvParameterSpec)parameterSpec;
                }
                else {
                    if (n != 1 && n != 3) {
                        throw new InvalidAlgorithmParameterException("Missing parameter type: IV expected");
                    }
                    final byte[] array3 = new byte[this.blkSize];
                    secureRandom.nextBytes(array3);
                    this.ivSpec = new IvParameterSpec(array3);
                }
            }
            array = new char[encoded.length];
            for (int i = 0; i < array.length; ++i) {
                array[i] = (char)(encoded[i] & 0x7F);
            }
            pbeKeySpec = new PBEKeySpec(array, this.salt, this.iCount, this.keyLength);
        }
        finally {
            if (array != null) {
                Arrays.fill(array, '\0');
            }
            if (encoded != null) {
                Arrays.fill(encoded, (byte)0);
            }
        }
        SecretKey engineGenerateSecret;
        try {
            engineGenerateSecret = this.kdf.engineGenerateSecret(pbeKeySpec);
        }
        catch (final InvalidKeySpecException ex) {
            final InvalidKeyException ex2 = new InvalidKeyException("Cannot construct PBE key");
            ex2.initCause(ex);
            throw ex2;
        }
        this.cipher.init(n, new SecretKeySpec(engineGenerateSecret.getEncoded(), this.cipherAlgo), this.ivSpec, secureRandom);
    }
    
    @Override
    protected void engineInit(final int n, final Key key, final AlgorithmParameters algorithmParameters, final SecureRandom secureRandom) throws InvalidKeyException, InvalidAlgorithmParameterException {
        AlgorithmParameterSpec parameterSpec = null;
        if (algorithmParameters != null) {
            try {
                parameterSpec = algorithmParameters.getParameterSpec(PBEParameterSpec.class);
            }
            catch (final InvalidParameterSpecException ex) {
                throw new InvalidAlgorithmParameterException("Wrong parameter type: PBE expected");
            }
        }
        this.engineInit(n, key, parameterSpec, secureRandom);
    }
    
    @Override
    protected byte[] engineUpdate(final byte[] array, final int n, final int n2) {
        return this.cipher.update(array, n, n2);
    }
    
    @Override
    protected int engineUpdate(final byte[] array, final int n, final int n2, final byte[] array2, final int n3) throws ShortBufferException {
        return this.cipher.update(array, n, n2, array2, n3);
    }
    
    @Override
    protected byte[] engineDoFinal(final byte[] array, final int n, final int n2) throws IllegalBlockSizeException, BadPaddingException {
        return this.cipher.doFinal(array, n, n2);
    }
    
    @Override
    protected int engineDoFinal(final byte[] array, final int n, final int n2, final byte[] array2, final int n3) throws ShortBufferException, IllegalBlockSizeException, BadPaddingException {
        return this.cipher.doFinal(array, n, n2, array2, n3);
    }
    
    @Override
    protected int engineGetKeySize(final Key key) throws InvalidKeyException {
        return this.keyLength;
    }
    
    @Override
    protected byte[] engineWrap(final Key key) throws IllegalBlockSizeException, InvalidKeyException {
        return this.cipher.wrap(key);
    }
    
    @Override
    protected Key engineUnwrap(final byte[] array, final String s, final int n) throws InvalidKeyException, NoSuchAlgorithmException {
        return this.cipher.unwrap(array, s, n);
    }
    
    public static final class HmacSHA1AndAES_128 extends PBES2Core
    {
        public HmacSHA1AndAES_128() throws NoSuchAlgorithmException, NoSuchPaddingException {
            super("HmacSHA1", "AES", 16);
        }
    }
    
    public static final class HmacSHA224AndAES_128 extends PBES2Core
    {
        public HmacSHA224AndAES_128() throws NoSuchAlgorithmException, NoSuchPaddingException {
            super("HmacSHA224", "AES", 16);
        }
    }
    
    public static final class HmacSHA256AndAES_128 extends PBES2Core
    {
        public HmacSHA256AndAES_128() throws NoSuchAlgorithmException, NoSuchPaddingException {
            super("HmacSHA256", "AES", 16);
        }
    }
    
    public static final class HmacSHA384AndAES_128 extends PBES2Core
    {
        public HmacSHA384AndAES_128() throws NoSuchAlgorithmException, NoSuchPaddingException {
            super("HmacSHA384", "AES", 16);
        }
    }
    
    public static final class HmacSHA512AndAES_128 extends PBES2Core
    {
        public HmacSHA512AndAES_128() throws NoSuchAlgorithmException, NoSuchPaddingException {
            super("HmacSHA512", "AES", 16);
        }
    }
    
    public static final class HmacSHA1AndAES_256 extends PBES2Core
    {
        public HmacSHA1AndAES_256() throws NoSuchAlgorithmException, NoSuchPaddingException {
            super("HmacSHA1", "AES", 32);
        }
    }
    
    public static final class HmacSHA224AndAES_256 extends PBES2Core
    {
        public HmacSHA224AndAES_256() throws NoSuchAlgorithmException, NoSuchPaddingException {
            super("HmacSHA224", "AES", 32);
        }
    }
    
    public static final class HmacSHA256AndAES_256 extends PBES2Core
    {
        public HmacSHA256AndAES_256() throws NoSuchAlgorithmException, NoSuchPaddingException {
            super("HmacSHA256", "AES", 32);
        }
    }
    
    public static final class HmacSHA384AndAES_256 extends PBES2Core
    {
        public HmacSHA384AndAES_256() throws NoSuchAlgorithmException, NoSuchPaddingException {
            super("HmacSHA384", "AES", 32);
        }
    }
    
    public static final class HmacSHA512AndAES_256 extends PBES2Core
    {
        public HmacSHA512AndAES_256() throws NoSuchAlgorithmException, NoSuchPaddingException {
            super("HmacSHA512", "AES", 32);
        }
    }
}
