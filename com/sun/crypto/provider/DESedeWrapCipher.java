package com.sun.crypto.provider;

import java.security.MessageDigest;
import java.security.spec.InvalidParameterSpecException;
import java.security.Provider;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.ShortBufferException;
import java.security.AlgorithmParameters;
import javax.crypto.spec.IvParameterSpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.SecureRandom;
import javax.crypto.NoSuchPaddingException;
import java.security.NoSuchAlgorithmException;
import java.security.Key;
import javax.crypto.CipherSpi;

public final class DESedeWrapCipher extends CipherSpi
{
    private static final byte[] IV2;
    private static final int CHECKSUM_LEN = 8;
    private static final int IV_LEN = 8;
    private FeedbackCipher cipher;
    private byte[] iv;
    private Key cipherKey;
    private boolean decrypting;
    
    public DESedeWrapCipher() {
        this.iv = null;
        this.cipherKey = null;
        this.decrypting = false;
        this.cipher = new CipherBlockChaining(new DESedeCrypt());
    }
    
    @Override
    protected void engineSetMode(final String s) throws NoSuchAlgorithmException {
        if (!s.equalsIgnoreCase("CBC")) {
            throw new NoSuchAlgorithmException(s + " cannot be used");
        }
    }
    
    @Override
    protected void engineSetPadding(final String s) throws NoSuchPaddingException {
        if (!s.equalsIgnoreCase("NoPadding")) {
            throw new NoSuchPaddingException(s + " cannot be used");
        }
    }
    
    @Override
    protected int engineGetBlockSize() {
        return 8;
    }
    
    @Override
    protected int engineGetOutputSize(final int n) {
        int addExact;
        if (this.decrypting) {
            addExact = n - 16;
        }
        else {
            addExact = Math.addExact(n, 16);
        }
        return (addExact < 0) ? 0 : addExact;
    }
    
    @Override
    protected byte[] engineGetIV() {
        return (byte[])((this.iv == null) ? null : ((byte[])this.iv.clone()));
    }
    
    @Override
    protected void engineInit(final int n, final Key key, final SecureRandom secureRandom) throws InvalidKeyException {
        try {
            this.engineInit(n, key, (AlgorithmParameterSpec)null, secureRandom);
        }
        catch (final InvalidAlgorithmParameterException ex) {
            final InvalidKeyException ex2 = new InvalidKeyException("Parameters required");
            ex2.initCause(ex);
            throw ex2;
        }
    }
    
    @Override
    protected void engineInit(final int n, final Key cipherKey, final AlgorithmParameterSpec algorithmParameterSpec, SecureRandom random) throws InvalidKeyException, InvalidAlgorithmParameterException {
        byte[] array;
        if (n == 3) {
            this.decrypting = false;
            if (algorithmParameterSpec == null) {
                this.iv = new byte[8];
                if (random == null) {
                    random = SunJCE.getRandom();
                }
                random.nextBytes(this.iv);
            }
            else {
                if (!(algorithmParameterSpec instanceof IvParameterSpec)) {
                    throw new InvalidAlgorithmParameterException("Wrong parameter type: IV expected");
                }
                this.iv = ((IvParameterSpec)algorithmParameterSpec).getIV();
            }
            array = this.iv;
        }
        else {
            if (n != 4) {
                throw new UnsupportedOperationException("This cipher can only be used for key wrapping and unwrapping");
            }
            if (algorithmParameterSpec != null) {
                throw new InvalidAlgorithmParameterException("No parameter accepted for unwrapping keys");
            }
            this.iv = null;
            this.decrypting = true;
            array = DESedeWrapCipher.IV2;
        }
        this.cipher.init(this.decrypting, cipherKey.getAlgorithm(), cipherKey.getEncoded(), array);
        this.cipherKey = cipherKey;
    }
    
    @Override
    protected void engineInit(final int n, final Key key, final AlgorithmParameters algorithmParameters, final SecureRandom secureRandom) throws InvalidKeyException, InvalidAlgorithmParameterException {
        AlgorithmParameterSpec algorithmParameterSpec = null;
        if (algorithmParameters != null) {
            try {
                final DESedeParameters deSedeParameters = new DESedeParameters();
                deSedeParameters.engineInit(algorithmParameters.getEncoded());
                algorithmParameterSpec = deSedeParameters.engineGetParameterSpec(IvParameterSpec.class);
            }
            catch (final Exception ex) {
                final InvalidAlgorithmParameterException ex2 = new InvalidAlgorithmParameterException("Wrong parameter type: IV expected");
                ex2.initCause(ex);
                throw ex2;
            }
        }
        this.engineInit(n, key, algorithmParameterSpec, secureRandom);
    }
    
    @Override
    protected byte[] engineUpdate(final byte[] array, final int n, final int n2) {
        throw new IllegalStateException("Cipher has not been initialized");
    }
    
    @Override
    protected int engineUpdate(final byte[] array, final int n, final int n2, final byte[] array2, final int n3) throws ShortBufferException {
        throw new IllegalStateException("Cipher has not been initialized");
    }
    
    @Override
    protected byte[] engineDoFinal(final byte[] array, final int n, final int n2) throws IllegalBlockSizeException, BadPaddingException {
        throw new IllegalStateException("Cipher has not been initialized");
    }
    
    @Override
    protected int engineDoFinal(final byte[] array, final int n, final int n2, final byte[] array2, final int n3) throws IllegalBlockSizeException, ShortBufferException, BadPaddingException {
        throw new IllegalStateException("Cipher has not been initialized");
    }
    
    @Override
    protected AlgorithmParameters engineGetParameters() {
        AlgorithmParameters instance = null;
        if (this.iv != null) {
            final String algorithm = this.cipherKey.getAlgorithm();
            try {
                instance = AlgorithmParameters.getInstance(algorithm, SunJCE.getInstance());
                instance.init(new IvParameterSpec(this.iv));
            }
            catch (final NoSuchAlgorithmException ex) {
                throw new RuntimeException("Cannot find " + algorithm + " AlgorithmParameters implementation in SunJCE provider");
            }
            catch (final InvalidParameterSpecException ex2) {
                throw new RuntimeException("IvParameterSpec not supported");
            }
        }
        return instance;
    }
    
    @Override
    protected int engineGetKeySize(final Key key) throws InvalidKeyException {
        final byte[] encoded = key.getEncoded();
        if (encoded.length != 24) {
            throw new InvalidKeyException("Invalid key length: " + encoded.length + " bytes");
        }
        return 112;
    }
    
    @Override
    protected byte[] engineWrap(final Key key) throws IllegalBlockSizeException, InvalidKeyException {
        final byte[] encoded = key.getEncoded();
        if (encoded == null || encoded.length == 0) {
            throw new InvalidKeyException("Cannot get an encoding of the key to be wrapped");
        }
        final byte[] checksum = getChecksum(encoded);
        final byte[] array = new byte[Math.addExact(encoded.length, 8)];
        System.arraycopy(encoded, 0, array, 0, encoded.length);
        System.arraycopy(checksum, 0, array, encoded.length, 8);
        final byte[] array2 = new byte[Math.addExact(this.iv.length, array.length)];
        System.arraycopy(this.iv, 0, array2, 0, this.iv.length);
        this.cipher.encrypt(array, 0, array.length, array2, this.iv.length);
        for (int i = 0; i < array2.length / 2; ++i) {
            final byte b = array2[i];
            array2[i] = array2[array2.length - 1 - i];
            array2[array2.length - 1 - i] = b;
        }
        try {
            this.cipher.init(false, this.cipherKey.getAlgorithm(), this.cipherKey.getEncoded(), DESedeWrapCipher.IV2);
        }
        catch (final InvalidKeyException ex) {
            throw new RuntimeException("Internal cipher key is corrupted");
        }
        catch (final InvalidAlgorithmParameterException ex2) {
            throw new RuntimeException("Internal cipher IV is invalid");
        }
        final byte[] array3 = new byte[array2.length];
        this.cipher.encrypt(array2, 0, array2.length, array3, 0);
        try {
            this.cipher.init(this.decrypting, this.cipherKey.getAlgorithm(), this.cipherKey.getEncoded(), this.iv);
        }
        catch (final InvalidKeyException ex3) {
            throw new RuntimeException("Internal cipher key is corrupted");
        }
        catch (final InvalidAlgorithmParameterException ex4) {
            throw new RuntimeException("Internal cipher IV is invalid");
        }
        return array3;
    }
    
    @Override
    protected Key engineUnwrap(final byte[] array, final String s, final int n) throws InvalidKeyException, NoSuchAlgorithmException {
        if (array.length == 0) {
            throw new InvalidKeyException("The wrapped key is empty");
        }
        final byte[] array2 = new byte[array.length];
        this.cipher.decrypt(array, 0, array.length, array2, 0);
        for (int i = 0; i < array2.length / 2; ++i) {
            final byte b = array2[i];
            array2[i] = array2[array2.length - 1 - i];
            array2[array2.length - 1 - i] = b;
        }
        System.arraycopy(array2, 0, this.iv = new byte[8], 0, this.iv.length);
        try {
            this.cipher.init(true, this.cipherKey.getAlgorithm(), this.cipherKey.getEncoded(), this.iv);
        }
        catch (final InvalidAlgorithmParameterException ex) {
            throw new InvalidKeyException("IV in wrapped key is invalid");
        }
        final byte[] array3 = new byte[array2.length - this.iv.length];
        this.cipher.decrypt(array2, this.iv.length, array3.length, array3, 0);
        final int n2 = array3.length - 8;
        final byte[] checksum = getChecksum(array3, 0, n2);
        final int n3 = n2;
        for (int j = 0; j < 8; ++j) {
            if (array3[n3 + j] != checksum[j]) {
                throw new InvalidKeyException("Checksum comparison failed");
            }
        }
        try {
            this.cipher.init(this.decrypting, this.cipherKey.getAlgorithm(), this.cipherKey.getEncoded(), DESedeWrapCipher.IV2);
        }
        catch (final InvalidAlgorithmParameterException ex2) {
            throw new InvalidKeyException("IV in wrapped key is invalid");
        }
        final byte[] array4 = new byte[n2];
        System.arraycopy(array3, 0, array4, 0, n2);
        return ConstructKeys.constructKey(array4, s, n);
    }
    
    private static final byte[] getChecksum(final byte[] array) {
        return getChecksum(array, 0, array.length);
    }
    
    private static final byte[] getChecksum(final byte[] array, final int n, final int n2) {
        MessageDigest instance;
        try {
            instance = MessageDigest.getInstance("SHA1");
        }
        catch (final NoSuchAlgorithmException ex) {
            throw new RuntimeException("SHA1 message digest not available");
        }
        instance.update(array, n, n2);
        final byte[] array2 = new byte[8];
        System.arraycopy(instance.digest(), 0, array2, 0, array2.length);
        return array2;
    }
    
    static {
        IV2 = new byte[] { 74, -35, -94, 44, 121, -24, 33, 5 };
    }
}
